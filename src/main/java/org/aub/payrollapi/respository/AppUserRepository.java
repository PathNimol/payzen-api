package org.aub.payrollapi.respository;

import org.apache.ibatis.annotations.*;
import org.aub.payrollapi.model.dto.response.AppUserResponse;
import org.aub.payrollapi.model.dto.response.UserResponse;
import org.aub.payrollapi.model.entity.AppUser;

import java.util.UUID;

@Mapper
public interface AppUserRepository {

    // =======================
    // 1. Get user by ID
    // =======================
    @Results(id = "appUserMapper", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "profileImageUrl", column = "profile_img_url"),
            @Result(property = "isVerified", column = "is_verified"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
        SELECT * FROM app_users WHERE user_id = #{id}
    """)
    AppUserResponse getUserById(UUID id);

    // =======================
    // 2. Update verification status
    // =======================
    @Update("""
        UPDATE app_users SET is_verified = true WHERE email = #{email}
    """)
    void updateVerificationStatus(String email);

    // =======================
    // 3. Get basic UserResponse by ID
    // =======================
    @ResultMap("appUserMapper")
    @Select("""
        SELECT * FROM app_users WHERE user_id = #{id}
    """)
    UserResponse getUserResponseById(UUID id);

    // =======================
    // 4. Update user profile
    // =======================
    @Update("""
        UPDATE app_users
        SET
            first_name = #{appUser.firstName},
            last_name = #{appUser.lastName},
            profile_img_url = #{appUser.profileImageUrl}
        WHERE user_id = #{userId}
    """)
    void updateAppUser(@Param("appUser") AppUser appUserRequest, UUID userId);
}