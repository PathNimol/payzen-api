package org.aub.payrollapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aub.payrollapi.model.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser implements UserDetails {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String profileImgUrl;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private UserRole role = UserRole.MANAGER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(STR."ROLE_\{role.name().toUpperCase()}"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
