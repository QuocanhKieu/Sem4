package com.devteria.app_data_service.enums;

public enum ParkingRate {
    PEAK(20000),
    NORMAL(15000);

    private final double priceVND;

    ParkingRate(double priceVND) {
        this.priceVND = priceVND;
    }

    public double getPriceVND() {
        return priceVND;
    }

}
