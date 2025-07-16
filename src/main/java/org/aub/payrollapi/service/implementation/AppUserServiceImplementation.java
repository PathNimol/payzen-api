package org.aub.payrollapi.service.implementation;

import lombok.RequiredArgsConstructor;
import org.aub.payrollapi.model.dto.response.AppUserResponse;
import org.aub.payrollapi.model.entity.AppUser;
import org.aub.payrollapi.model.mapper.AppUserMapper;
import org.aub.payrollapi.respository.AuthRepository;
import org.aub.payrollapi.service.AppUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImplementation implements AppUserService {
    private final AuthRepository authRepository;
    private final AppUserMapper appUserMapper;

    public AppUser getAppCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authRepository.getUserByEmail(email);
    }

    @Override
    public AppUserResponse getCurrentUser() {
        return appUserMapper.toResponse(authRepository.getUserById(getAppCurrentUser().getUserId()));
    }
}

