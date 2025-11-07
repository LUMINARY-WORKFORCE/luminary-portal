package com.luminary.portal.config;

import com.luminary.portal.entity.*;
import com.luminary.portal.entity.enums.Role;
import com.luminary.portal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;

    @Bean
    CommandLineRunner loadInitialData() {
        return args -> {
            if (userRepo.count() == 0) {
                User employer = userRepo.save(
                        User.builder()
                                .name("John Employer")
                                .email("john@luminary.com")
                                .password("12345")
                                .role(Role.EMPLOYER)
                                .build()
                );

                Company company = companyRepo.save(
                        Company.builder()
                                .name("Luminary Tech")
                                .location("Pune")
                                .description("Leading staffing company")
                                .owner(employer)
                                .build()
                );
            } else {
                System.out.println("✅ Existing data found — skipping seeding.");
            }
        };
    }
}
