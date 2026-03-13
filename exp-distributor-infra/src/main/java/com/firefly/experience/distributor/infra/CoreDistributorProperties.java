package com.firefly.experience.distributor.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api-configuration.domain-platform.core-distributor")
@Data
public class CoreDistributorProperties {
    private String basePath;
}
