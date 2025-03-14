// package com.devteria.payment.repository.httpclient;
//
// import com.devteria.payment.configuration.AuthenticationRequestInterceptor;
// import com.devteria.payment.dto.ApiResponse;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
//
// import com.devteria.payment.dto.request.ProfileCreationRequest;
// import com.devteria.payment.dto.response.UserProfileResponse;
//
// @FeignClient(name = "profile-service", url = "${app.services.profile}",
//        configuration = { AuthenticationRequestInterceptor.class })
// public interface ProfileClient {
//    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
//    ApiResponse<UserProfileResponse> createProfile(@RequestBody ProfileCreationRequest request);
//
// }
