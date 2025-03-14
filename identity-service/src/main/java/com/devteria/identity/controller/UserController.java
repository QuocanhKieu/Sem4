package com.devteria.identity.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.devteria.identity.dto.request.ApiResponse;
import com.devteria.identity.dto.request.UserCreationRequest;
import com.devteria.identity.dto.request.VerifyOtpRequest;
import com.devteria.identity.dto.response.UserResponse;
import com.devteria.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    //    @GetMapping
    //    ApiResponse<List<UserResponse>> getUsers() {
    //        return ApiResponse.<List<UserResponse>>builder()
    //                .result(userService.getUsers())
    //                .build();
    //    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        userService.verifyOtp(request.getEmail(), request.getOtp());
        return ApiResponse.<String>builder()
                .result("Email verified successfully!")
                .build();
    }

    @PostMapping("/resend-otp")
    public ApiResponse<String> resendOtp(@RequestParam String email) {
        userService.resendOtp(email);
        return ApiResponse.<String>builder()
                .result("OTP has been resent successfully!")
                .build();
    }

    //    @GetMapping("/my-info")
    //    ApiResponse<UserResponse> getMyInfo() {
    //        return ApiResponse.<UserResponse>builder()
    //                .result(userService.getMyInfo())
    //                .build();
    //    }
    //
    //    @DeleteMapping("/{userId}")
    //    ApiResponse<String> deleteUser(@PathVariable String userId) {
    //        userService.deleteUser(userId);
    //        return ApiResponse.<String>builder().result("User has been deleted").build();
    //    }
    //
    //    @PutMapping("/{userId}")
    //    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
    //        return ApiResponse.<UserResponse>builder()
    //                .result(userService.updateUser(userId, request))
    //                .build();
    //    }
}
