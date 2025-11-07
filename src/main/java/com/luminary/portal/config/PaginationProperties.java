package com.luminary.portal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.pagination")
@Getter
@Setter
public class PaginationProperties {
    private int defaultPage;
    private int defaultSize;
}
