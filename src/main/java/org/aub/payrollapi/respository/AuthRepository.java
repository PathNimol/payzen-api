package org.aub.payrollapi.respository;

import org.apache.ibatis.annotations.*;
import org.aub.payrollapi.model.dto.request.AppUserRequest;
import org.aub.payrollapi.model.entity.AppUser;

import java.util.UUID;

@Mapper
public interface AuthRepository {

    @Results(id = "authMapper", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "password"),
            @Result(property = "isVerified", column = "is_verified"),
            @Result(property = "profileImgUrl", column = "profile_img_url"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
        INSERT INTO app_users (first_name, last_name, email, password)
        VALUES (#{req.firstName}, #{req.lastName}, #{req.email}, #{req.password})
        RETURNING *;
    """)
    AppUser register(@Param("req") AppUserRequest request);

    @ResultMap("authMapper")
    @Select("""
        UPDATE app_users SET password = #{password} WHERE email = #{email}
        RETURNING *;
    """)
    AppUser updatePassword(@Param("email") String email, @Param("password") String password);

    @ResultMap("authMapper")
    @Select("""
        SELECT * FROM app_users WHERE LOWER(email) = LOWER(#{email})
    """)
    AppUser getUserByEmail(String email);

    @ResultMap("authMapper")
    @Select("""
        SELECT * FROM app_users WHERE user_id = #{userId}
    """)
    AppUser getUserById(UUID userId);

    @Update("""
        UPDATE app_users SET password = #{password} WHERE user_id = #{appUserId}
    """)
    void changePassword(@Param("password") String password, @Param("appUserId") UUID appUserId);
}
