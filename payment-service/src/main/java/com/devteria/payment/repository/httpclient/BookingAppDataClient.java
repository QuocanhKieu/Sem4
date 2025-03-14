package com.devteria.payment.repository.httpclient;// package com.devteria.payment.repository.httpclient;

 import com.devteria.payment.configuration.AuthenticationRequestInterceptor;
 import com.devteria.payment.dto.ApiResponse;

 import com.devteria.payment.dto.request.ConfirmedBookingRequest;
 import org.springframework.cloud.openfeign.FeignClient;
 import org.springframework.http.MediaType;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.PutMapping;
 import org.springframework.web.bind.annotation.RequestBody;

 import java.io.IOException;

@FeignClient(name = "app-data-service", url = "${app.services.app-data-service}",
        configuration = { AuthenticationRequestInterceptor.class })
 public interface BookingAppDataClient {
    @PutMapping(value = "internal/confirm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<String> confirmBooking(@RequestBody ConfirmedBookingRequest confirmedBookingRequest);

 }

//@PutMapping("internal/confirm/{id}")
//public ApiResponse<String> confirmBooking(@PathVariable String id) throws IOException, WriterException {
//    Booking confirmedBooking = bookingService.confirmBooking(id);
//    return ApiResponse.<String>builder()
//            .message("Booking confirmed successfully")
//            .result("Booking confirmed successfully with ID:" + confirmedBooking.getId() )
//            .build();
//}
//@PutMapping("internal/confirm/{id}")
//public ResponseEntity<?> confirmBooking(@PathVariable String id) {
//    try {
//        Booking confirmedBooking = bookingService.confirmBooking(id);
//        return ResponseEntity.ok(confirmedBooking);
//    } catch (WriterException | IOException e) {
//        return ResponseEntity.status(500).body("Error generating QR codes: " + e.getMessage());
//    } catch (RuntimeException e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }
//}