package com.devteria.notification.enums;

public enum ChargingRate {
    NORMAL(3000, 1500),
    PEAK(3500, 1800),
    OFF_PEAK(2800, 1200);

    private final double evnPriceVnd;
    private final double serviceFeeVnd;

    ChargingRate(double evnPriceVnd, double serviceFeeVnd) {
        this.evnPriceVnd = evnPriceVnd;
        this.serviceFeeVnd = serviceFeeVnd;
    }

    public double getTotalPrice() {
        return evnPriceVnd + serviceFeeVnd;
    }
    public double getEvnPriceVnd() {
        return evnPriceVnd;
    }
    public double getServiceFeeVnd() {
        return serviceFeeVnd;
    }
}
