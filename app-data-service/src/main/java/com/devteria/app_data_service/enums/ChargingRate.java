package com.devteria.app_data_service.enums;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum ChargingRate {
    NORMAL(BigDecimal.valueOf(3000), BigDecimal.valueOf(1500)),
    PEAK(BigDecimal.valueOf(3500), BigDecimal.valueOf(1800)),
    OFF_PEAK(BigDecimal.valueOf(2800), BigDecimal.valueOf(1200));

    private static final BigDecimal EXCHANGE_RATE = BigDecimal.valueOf(25000); // Example: 1 USD = 24,000 VND

    private final BigDecimal evnPriceVnd;
    private final BigDecimal serviceFeeVnd;

    ChargingRate(BigDecimal evnPriceVnd, BigDecimal serviceFeeVnd) {
        this.evnPriceVnd = evnPriceVnd;
        this.serviceFeeVnd = serviceFeeVnd;
    }

    public BigDecimal getTotalPrice() {
        return evnPriceVnd.add(serviceFeeVnd);
    }

    public BigDecimal getEvnPriceVnd() {
        return evnPriceVnd;
    }

    public BigDecimal getServiceFeeVnd() {
        return serviceFeeVnd;
    }

    public BigDecimal getEvnPriceUsd() {
        return evnPriceVnd.divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP); // Convert to USD with 4 decimal places
    }

    public BigDecimal getServiceFeeUsd() {
        return serviceFeeVnd.divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalPriceUsd() {
        return getTotalPrice().divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
    }
}
