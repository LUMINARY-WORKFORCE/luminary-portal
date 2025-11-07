package com.luminary.portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Luminary Workforce Solutions API")
                        .description("Backend API documentation for Luminary Job Portal")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Luminary Workforce Solutions")
                                .email("support@luminaryworkforcesolutions.com")
                                .url("https://luminaryworkforcesolutions.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER))
                )
                .tags(List.of(
                        new Tag().name("Authentication").description("Public APIs for login/register"),
                        new Tag().name("Job Management").description("Employer and Job Seeker APIs for managing jobs"),
                        new Tag().name("Application Management").description("APIs for applying and managing job applications"),
                        new Tag().name("Admin APIs").description("Administrative endpoints")
                ));
    }
}
