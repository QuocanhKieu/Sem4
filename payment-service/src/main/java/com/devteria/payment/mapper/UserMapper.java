// package com.devteria.payment.mapper;
//
// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.MappingTarget;
//
// import com.devteria.payment.dto.request.UserCreationRequest;
// import com.devteria.payment.dto.request.UserUpdateRequest;
// import com.devteria.payment.dto.response.UserResponse;
// import com.devteria.payment.entity.User;
//
// @Mapper(componentModel = "spring")
// public interface UserMapper {
//    @Mapping(target = "username", ignore = true) // Ignore username field
//    User toUser(UserCreationRequest request);
//
//    UserResponse toUserResponse(User user);
//
//    @Mapping(target = "roles", ignore = true)
//    void updateUser(@MappingTarget User user, UserUpdateRequest request);
// }
