package com.firefly.experience.distributor.infra;

import com.firefly.domain.common.notifications.sdk.api.NotificationPreferencesApi;
import com.firefly.domain.common.notifications.sdk.api.NotificationsApi;
import com.firefly.domain.common.notifications.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Factory that creates and configures the Common Notifications SDK {@link ApiClient}
 * and exposes domain API beans for dependency injection.
 */
@Component
public class CommonNotificationsClientFactory {

    private final ApiClient apiClient;

    /**
     * Initialises the API client with the base path from configuration properties.
     *
     * @param properties connection properties for the Common Notifications service
     */
    public CommonNotificationsClientFactory(CommonNotificationsProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    /**
     * Provides the {@link NotificationsApi} bean for sending notifications.
     *
     * @return a ready-to-use NotificationsApi instance
     */
    @Bean
    public NotificationsApi notificationsApi() {
        return new NotificationsApi(apiClient);
    }

    /**
     * Provides the {@link NotificationPreferencesApi} bean for managing notification preferences.
     *
     * @return a ready-to-use NotificationPreferencesApi instance
     */
    @Bean
    public NotificationPreferencesApi notificationPreferencesApi() {
        return new NotificationPreferencesApi(apiClient);
    }
}
