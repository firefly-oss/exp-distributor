package com.firefly.experience.distributor.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api-configuration.domain-platform.common-notifications")
@Data
public class CommonNotificationsProperties {
    private String basePath;
}
