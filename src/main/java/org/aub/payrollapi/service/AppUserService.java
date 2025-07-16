package org.aub.payrollapi.service;


import org.aub.payrollapi.model.dto.response.AppUserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
    AppUserResponse getCurrentUser();
}
