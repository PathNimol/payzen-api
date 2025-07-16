package org.aub.payrollapi.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aub.payrollapi.exception.BadRequestException;
import org.aub.payrollapi.exception.NotFoundException;
import org.aub.payrollapi.jwt.JwtService;
import org.aub.payrollapi.model.dto.request.AppUserRequest;
import org.aub.payrollapi.model.dto.request.AuthRequest;
import org.aub.payrollapi.model.dto.request.PasswordRequest;
import org.aub.payrollapi.model.dto.response.AppUserResponse;
import org.aub.payrollapi.model.dto.response.AuthResponse;
import org.aub.payrollapi.model.entity.AppUser;
import org.aub.payrollapi.model.mapper.AppUserMapper;
import org.aub.payrollapi.respository.AppUserRepository;
import org.aub.payrollapi.respository.AuthRepository;
import org.aub.payrollapi.service.AppUserService;
import org.aub.payrollapi.service.AuthService;
import org.aub.payrollapi.service.EmailSenderService;
import org.aub.payrollapi.utils.RandomOtp;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {
    private final JwtService jwtService;
    private final AuthRepository authRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;
    private final EmailSenderService emailSenderService;
    private final RedisTemplate<String, String> redisTemplate;

    public AppUser getAppCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void authenticate(String email, String password) {
        try {
            AppUser appUser = authRepository.getUserByEmail(email);

            if (appUser == null) {
                throw new NotFoundException("Invalid email");
            }
            if (!passwordEncoder.matches(password, appUser.getPassword())) {
                throw new NotFoundException("Invalid Password");
            }
            if(!appUser.getIsVerified()) {
                throw new BadRequestException("Your account is not verified");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        } catch (DisabledException e) {
            throw new BadRequestException("USER_DISABLED" + e.getMessage());
        } catch (BadCredentialsException e) {
            throw new BadRequestException("INVALID_CREDENTIALS" + e.getMessage());
        }
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        String email = authRequest.getEmail().toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if(appUser == null) throw new BadRequestException("User is not registered");
        if(!appUser.getIsVerified()) throw new BadRequestException("User needs to verify before login");
        authenticate(authRequest.getEmail().toLowerCase(), authRequest.getPassword());
        final UserDetails userDetails = appUserService.loadUserByUsername(authRequest.getEmail().toLowerCase());
        final String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    @SneakyThrows
    @Override
    public AppUserResponse register(AppUserRequest appUserRequest) {
        if (authRepository.getUserByEmail(appUserRequest.getEmail().toLowerCase()) != null)
            throw new BadRequestException("User already exists");

        appUserRequest.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));
        appUserRequest.setEmail(appUserRequest.getEmail().toLowerCase());
        AppUser appUser = authRepository.register(appUserRequest);
        String otp = new RandomOtp().generateOtp();
        while (redisTemplate.opsForValue().get(otp) != null) {
            otp = new RandomOtp().generateOtp();
        }
            redisTemplate.opsForValue().set(appUser.getEmail(), otp, Duration.ofMinutes(5));
            emailSenderService.sendEmail(appUser.getEmail(), otp);
        return appUserMapper.toResponse(appUser);
    }

//    @Override
//    public void verify(String emailRequest, String otpCode) {
//        String email = emailRequest.toLowerCase();
//        AppUser appUser = authRepository.getUserByEmail(email);
//        if (appUser == null) throw new NotFoundException("User doesn't exist");
//        if (appUser.getIsVerified()) throw new BadRequestException("User already verified");
//
//        String storedOTP = redisTemplate.opsForValue().get(email);
//        if(storedOTP == null) throw new BadRequestException("OTP already expired");
//        if (!storedOTP.equals(otpCode)) throw new BadRequestException("OTP code doesn't match");
//
//        redisTemplate.delete(otpCode);
//        appUserRepository.updateVerificationStatus(email);
//    }
@Transactional
@Override
public void verify(String emailRequest, String otpCode) {
    String email = emailRequest.toLowerCase();
    AppUser appUser = authRepository.getUserByEmail(email);
    if (appUser == null) throw new NotFoundException("User doesn't exist");
    if (appUser.getIsVerified()) throw new BadRequestException("User already verified");

    String storedOTP = redisTemplate.opsForValue().get(email);
    if(storedOTP == null) throw new BadRequestException("OTP already expired");
    if (!storedOTP.equals(otpCode)) throw new BadRequestException("OTP code doesn't match");

    redisTemplate.delete(email); // ✅ fix here
    appUserRepository.updateVerificationStatus(email); // ✅ update with @Modifying query
}

    @SneakyThrows
    @Override
    public void resend(String emailRequest) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");
        if(appUser.getIsVerified()) throw new BadRequestException("User already verified");
        String otp = new RandomOtp().generateOtp();

        while (redisTemplate.opsForValue().get(otp) != null) {
            otp = new RandomOtp().generateOtp();
        }

        emailSenderService.sendEmail(appUser.getEmail(), otp);
        redisTemplate.opsForValue().set(appUser.getEmail(), otp, Duration.ofMinutes(5));
    }

    @Override
    public void forgotPassword(String emailRequest) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");
        String otp = new RandomOtp().generateOtp();

        while (redisTemplate.opsForValue().get(otp) != null) {
            otp = new RandomOtp().generateOtp();
        }

        emailSenderService.sendEmail(appUser.getEmail(), otp);
        redisTemplate.opsForValue().set(appUser.getEmail(), otp, Duration.ofMinutes(5));
    }

    @Override
    public void verifyForgot(String emailRequest, String otpCode) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");
        String storedOTP = redisTemplate.opsForValue().get(email);
        if(storedOTP == null) throw new BadRequestException("OTP already expired");
        if (!storedOTP.equals(otpCode)) throw new BadRequestException("OTP code doesn't match");
    }

    @Override
    public AppUserResponse resetPassword(String emailRequest, String otpCode, String newPassword) {
        String email = emailRequest.toLowerCase();
        AppUser appUser = authRepository.getUserByEmail(email);
        if (appUser == null) throw new NotFoundException("User doesn't exist");

        String storedOTP = redisTemplate.opsForValue().get(email);
        if(storedOTP == null) throw new BadRequestException("OTP already expired");
        if (!storedOTP.equals(otpCode)) throw new BadRequestException("OTP code doesn't match");

        redisTemplate.delete(otpCode);

        String password = passwordEncoder.encode(newPassword);
        AppUser newUserPassword = authRepository.updatePassword(email, password);

        return appUserMapper.toResponse(newUserPassword);

    }

    @Override
    public void verifyOldPassword(PasswordRequest passwordRequest) {
        if (!passwordEncoder.matches(passwordRequest.getPassword(), getAppCurrentUser().getPassword()))
            throw new BadRequestException("Wrong password");
    }

    @Override
    public void changePassword(PasswordRequest passwordRequest) {
        authRepository.changePassword(passwordRequest.getPassword(), getAppCurrentUser().getUserId());
    }

}
