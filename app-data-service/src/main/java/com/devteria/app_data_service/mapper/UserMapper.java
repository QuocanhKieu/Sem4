//package com.devteria.app_data_service.mapper;
//
//import com.devteria.payment.dto.request.UserCreationRequest;
//import com.devteria.payment.dto.request.UserUpdateRequest;
//import com.devteria.payment.dto.response.UserResponse;
//import com.devteria.payment.entity.User;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//
//@Mapper(componentModel = "spring")
//public interface UserMapper {
//    User toUser(UserCreationRequest request);
//
//    UserResponse toUserResponse(User user);
//
//    @Mapping(target = "roles", ignore = true)
//    void updateUser(@MappingTarget User user, UserUpdateRequest request);
//}
