package org.aub.payrollapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@OpenAPIDefinition(info = @Info(title = "PayZen API",
        version = "v1",
        description = "The PayZen API powers a collaborative platform where developers can build professional profiles, share projects, solve coding challenges, and connect with peers, mentors, and recruiters. Designed with flexibility and scalability in mind, this RESTful API enables seamless integration across web and mobile platforms.\n"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)

@SpringBootApplication
@EnableCaching
public class PayrollApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayrollApiApplication.class, args);
    }

}
