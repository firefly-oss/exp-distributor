package com.firefly.experience.distributor.infra;

import com.firefly.domain.common.notifications.sdk.api.NotificationsApi;
import com.firefly.domain.common.notifications.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CommonNotificationsClientFactory {

    private final ApiClient apiClient;

    public CommonNotificationsClientFactory(CommonNotificationsProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    @Bean
    public NotificationsApi notificationsApi() {
        return new NotificationsApi(apiClient);
    }
}
