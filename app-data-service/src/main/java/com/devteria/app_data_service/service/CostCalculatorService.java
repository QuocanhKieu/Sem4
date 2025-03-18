package com.devteria.app_data_service.service;

import com.devteria.app_data_service.enums.ParkingRate;
import com.devteria.app_data_service.enums.ChargingRate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CostCalculatorService {

    public BigDecimal calculateParkingCost(BigDecimal duration) {
        return duration.multiply(ParkingRate.NORMAL.getPriceVND()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateChargingCost(BigDecimal duration, Integer kwhUsed) {
        return duration.multiply(ChargingRate.NORMAL.getServiceFeeVnd()
                        .multiply(BigDecimal.valueOf(kwhUsed)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Calculate parking cost in USD
    public BigDecimal calculateParkingCostInUSD(BigDecimal duration) {
        return duration.multiply(ParkingRate.NORMAL.getPriceUSD()).setScale(2, RoundingMode.HALF_UP);
    }

    // Calculate charging cost in USD
    public BigDecimal calculateChargingCostInUSD(BigDecimal duration, Integer kwhUsed) {
        return duration.multiply(ChargingRate.NORMAL.getServiceFeeVnd()
                        .multiply(BigDecimal.valueOf(kwhUsed)))
                .divide(BigDecimal.valueOf(24000), 2, RoundingMode.HALF_UP); // Convert to USD
    }
}
