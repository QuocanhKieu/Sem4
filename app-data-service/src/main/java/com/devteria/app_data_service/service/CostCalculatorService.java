package com.devteria.app_data_service.service;

import com.devteria.app_data_service.configuration.SecurityUtils;
import com.devteria.app_data_service.dto.request.BookingRequest;
import com.devteria.app_data_service.entity.Booking;
import com.devteria.app_data_service.enums.BookingStatusEnums;
import com.devteria.app_data_service.enums.ChargingRate;
import com.devteria.app_data_service.enums.ParkingRate;
import com.devteria.app_data_service.mapper.BookingMapper;
import com.devteria.app_data_service.repository.BookingRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CostCalculatorService {

    public double calculateParkingCost(Double duration) {

        return duration* ParkingRate.NORMAL.getPriceVND();
    }

    public double calculateChargingCost(Double duration, Integer kwhUsed) {

        return duration*(ChargingRate.NORMAL.getServiceFeeVnd()+ChargingRate.NORMAL.getServiceFeeVnd())*kwhUsed;
    }

//    private boolean isTimeInRange(LocalTime time, List<TimeRange> timeRanges) {
//        for (TimeRange range : timeRanges) {
//            if (!time.isBefore(range.getStart()) && !time.isAfter(range.getEnd())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
