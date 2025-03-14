// package com.devteria.payment.service;
//
// import java.time.LocalDateTime;
// import java.util.HashSet;
// import java.util.Optional;
// import java.util.Random;
//
// import com.devteria.payment.dto.request.Recipient;
// import com.devteria.payment.repository.httpclient.NotificationClient;
// import com.devteria.payment.repository.httpclient.ProfileClient;
// import feign.FeignException;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
//
// import com.devteria.payment.constant.PredefinedRole;
// import com.devteria.payment.dto.request.UserCreationRequest;
// import com.devteria.payment.dto.request.EmailVerifyOTPRequest;
// import com.devteria.payment.dto.response.UserResponse;
// import com.devteria.payment.entity.Role;
// import com.devteria.payment.entity.User;
// import com.devteria.payment.exception.AppException;
// import com.devteria.payment.exception.ErrorCode;
// import com.devteria.payment.mapper.UserMapper;
// import com.devteria.payment.repository.RoleRepository;
// import com.devteria.payment.repository.UserRepository;
//
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.transaction.annotation.Transactional;
//
// @Service
// @RequiredArgsConstructor
// @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// @Slf4j
// public class UserService {
//    UserRepository userRepository;
//    RoleRepository roleRepository;
//    UserMapper userMapper;
//    PasswordEncoder passwordEncoder;
//    NotificationClient notificationClient;
//    ProfileClient profileClient;
////    public UserResponse createUser(UserCreationRequest request) {
////        User user = userMapper.toUser(request);
////        user.setPassword(passwordEncoder.encode(request.getPassword()));
////
////        HashSet<Role> roles = new HashSet<>();
////        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
////
////        user.setRoles(roles);
////        user.setDob(request.getDob());
////        user.setEmail(request.getEmail());
////        user.setEmailVerified(false);
////
////        try {
////            user = userRepository.save(user);
////        } catch (DataIntegrityViolationException exception) {
////            throw new AppException(ErrorCode.USER_EXISTED);
////        }
////        //send email verificaiton ... send mail service.
////        return userMapper.toUserResponse(user);
////    }
//
//    public UserResponse createUser(UserCreationRequest request) {
//        User user = userMapper.toUser(request);
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setUsername(request.getEmail());
//        HashSet<Role> roles = new HashSet<>();
//        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
//        user.setRoles(roles);
//        user.setDob(request.getDob());
//        user.setEmail(request.getEmail());
//        user.setEmailVerified(false);
//
//        // Generate OTP and set expiration time (5 minutes from now)
//        String otp = generateOtp();
//        user.setOtp(otp);
//        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(5).toString());
//
//        try {
//            user = userRepository.save(user);
//        } catch (DataIntegrityViolationException exception) {
//            throw new AppException(ErrorCode.USER_EXISTED);
//        }
//
//        try {
//            notificationClient.sendEmailVerificationOTP(EmailVerifyOTPRequest.builder()
//                    .to(Recipient.builder()
//                            .email(user.getEmail())
//                            .name(user.getEmail())
//                            .build()) // Build Recipient inline
//                    .otp(otp)
//                    .build());
//        } catch (
//                FeignException e) { // Handle Feign client errors
//            throw new AppException(ErrorCode.CANNOT_SEND_OTP);
//        }
//
//        return userMapper.toUserResponse(user);
//    }
//
//    /**
//     * Generates a random 6-digit OTP
//     */
//    private String generateOtp() {
//        Random random = new Random();
//        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit OTP
//        return String.valueOf(otp);
//    }
//
//    public void verifyOtp(String email, String providedOtp) {
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//        if (optionalUser.isEmpty()) {
//            throw new AppException(ErrorCode.USER_NOT_EXISTED);
//        }
//
//        User user = optionalUser.get();
//
//        // Check if OTP is expired
//        if (user.getOtpExpiryDate() == null ||
// LocalDateTime.parse(user.getOtpExpiryDate()).isBefore(LocalDateTime.now())) {
//            throw new AppException(ErrorCode.INVALID_OTP);
//        }
//
//        // Validate OTP
//        if (!user.getOtp().equals(providedOtp)) {
//            throw new AppException(ErrorCode.INVALID_OTP);
//        }
//
//        // OTP is valid, mark email as verified
//        user.setEmailVerified(true);
//        user.setOtp(null);  // Clear OTP after verification
//        user.setOtpExpiryDate(null);
//        userRepository.save(user);
//
//    }
//
//
//    public void resendOtp(String email) {
//        Optional<User> optionalUser = userRepository.findByEmail(email.trim());
//        log.info("Processing email: {}", email.trim());
//        if (optionalUser.isEmpty()) {
//            throw new AppException(ErrorCode.USER_NOT_EXISTED);
//        }
//
//        User user = optionalUser.get();
//
//        // Generate new OTP and set new expiry time
//        String newOtp = generateOtp();
//        user.setOtp(newOtp);
//        user.setOtpExpiryDate(LocalDateTime.now().plusMinutes(5).toString());
//        userRepository.save(user);
//
//        // Resend OTP email
//        try {
//            notificationClient.sendEmailVerificationOTP(EmailVerifyOTPRequest.builder()
//                    .to(Recipient.builder()
//                            .email(user.getEmail())
//                            .name(user.getEmail())
//                            .build()) // Build Recipient inline
//                    .otp(newOtp)
//                    .build());
//        } catch (FeignException e) { // Handle Feign client errors
//            throw new AppException(ErrorCode.CANNOT_SEND_OTP);
//        }
//
//    }
//
//
//
////    public UserResponse getMyInfo() {
////        var context = SecurityContextHolder.getContext();
////        String name = context.getAuthentication().getName();
////
////        User user = userRepository.findByUsername(name).orElseThrow(() -> new
// AppException(ErrorCode.USER_NOT_EXISTED));
////
////        return userMapper.toUserResponse(user);
////    }
//
////    @PreAuthorize("hasRole('ADMIN')")
////    public UserResponse updateUser(String userId, UserUpdateRequest request) {
////        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
////
////        userMapper.updateUser(user, request);
////        user.setPassword(passwordEncoder.encode(request.getPassword()));
////
////        var roles = roleRepository.findAllById(request.getRoles());
////        user.setRoles(new HashSet<>(roles));
////
////        return userMapper.toUserResponse(userRepository.save(user));
////    }
//
////    @PreAuthorize("hasRole('ADMIN')")
////    public void deleteUser(String userId) {
////        userRepository.deleteById(userId);
////    }
//
////    @PreAuthorize("hasRole('ADMIN')")
////    public List<UserResponse> getUsers() {
////        log.info("In method get Users");
////        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
////    }
//
////    @PreAuthorize("hasRole('ADMIN')")
//    public UserResponse getUser(String id) {
//        return userMapper.toUserResponse(
//                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
//    }
// }
