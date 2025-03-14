//package com.devteria.app_data_service.mapper;
//
//import com.devteria.payment.dto.request.RoleRequest;
//import com.devteria.payment.dto.response.RoleResponse;
//import com.devteria.payment.entity.Role;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface RoleMapper {
//    @Mapping(target = "permissions", ignore = true)
//    Role toRole(RoleRequest request);
//
//    RoleResponse toRoleResponse(Role role);
//}
