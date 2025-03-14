package com.devteria.app_data_service.repository.impl;

import com.devteria.app_data_service.dto.response.NearbyLocationResponse;
import com.devteria.app_data_service.enums.LocationServiceEnums;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomLocationRepository {

    private final MongoTemplate mongoTemplate;

    public CustomLocationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<NearbyLocationResponse> findNearbyWithDistance(double longitude, double latitude, double maxDistance,
                                                               List<LocationServiceEnums> services, String searchText) {
        // ✅ Create a GeoJSON Point
        Point point = new Point(longitude, latitude);

        // ✅ Create a NearQuery with spherical search
        NearQuery nearQuery = NearQuery.near(point)
                .maxDistance(new Distance(maxDistance, Metrics.KILOMETERS))
                .spherical(true);

        // ✅ Use MongoDB’s `geoNear` to calculate distances
        GeoNearOperation geoNearOperation = Aggregation.geoNear(nearQuery, "distance");

        // ✅ Define optional filtering conditions
        List<Criteria> criteriaList = new ArrayList<>();

        if (services != null && !services.isEmpty()) {
            criteriaList.add(Criteria.where("services").in(services));
        }

        if (searchText != null && !searchText.isEmpty()) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("name").regex(searchText, "i"),
                    Criteria.where("address").regex(searchText, "i")
            ));
        }

        // ✅ Build the match criteria dynamically
        MatchOperation matchOperation = null;
        if (!criteriaList.isEmpty()) {
            matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // ✅ Projection to correctly extract latitude & longitude
        ProjectionOperation projectOperation = Aggregation.project("id", "name", "address", "images", "services", "distance")
                .and("location.coordinates").arrayElementAt(1).as("latitude")  // ✅ Extract latitude correctly
                .and("location.coordinates").arrayElementAt(0).as("longitude"); // ✅ Extract longitude correctly

        // ✅ Define aggregation pipeline
        List<AggregationOperation> aggregationPipeline = new ArrayList<>();
        aggregationPipeline.add(geoNearOperation);
        if (matchOperation != null) aggregationPipeline.add(matchOperation);
        aggregationPipeline.add(projectOperation);

        // ✅ Execute the aggregation query
        Aggregation aggregation = Aggregation.newAggregation(aggregationPipeline);
        AggregationResults<NearbyLocationResponse> results = mongoTemplate.aggregate(aggregation, "locations", NearbyLocationResponse.class);

        return results.getMappedResults();
    }
}
