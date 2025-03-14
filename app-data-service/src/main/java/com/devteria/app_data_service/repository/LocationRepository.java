    package com.devteria.app_data_service.repository;

    import com.devteria.app_data_service.entity.Location;
    import com.devteria.app_data_service.enums.LocationServiceEnums;
    import com.mongodb.client.model.geojson.Point;
    import com.mongodb.client.model.geojson.Position;
    import org.springframework.data.mongodb.repository.Query;
    import org.springframework.stereotype.Repository;
    import org.springframework.data.mongodb.repository.MongoRepository;

    import java.util.List;

    @Repository
    public interface LocationRepository extends MongoRepository<Location, String> {

    }

