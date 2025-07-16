package org.aub.payrollapi.model.mapper;

import org.aub.payrollapi.model.dto.response.AppUserResponse;
import org.aub.payrollapi.model.dto.response.UserResponse;
import org.aub.payrollapi.model.entity.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserResponse toResponse(AppUser appUser);
    UserResponse toUserResponse(AppUserResponse appUserResponse);
}
