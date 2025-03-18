package com.devteria.app_data_service.enums;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum ParkingRate {
    PEAK(BigDecimal.valueOf(20000)),
    NORMAL(BigDecimal.valueOf(15000));

    private static final BigDecimal EXCHANGE_RATE = BigDecimal.valueOf(25000); // Example: 1 USD = 24,000 VND

    private final BigDecimal priceVND;

    ParkingRate(BigDecimal priceVND) {
        this.priceVND = priceVND;
    }

    public BigDecimal getPriceVND() {
        return priceVND;
    }

    public BigDecimal getPriceUSD() {
        return priceVND.divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP); // Convert to USD with 4 decimal places
    }
}
