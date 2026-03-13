package com.firefly.experience.distributor.infra;

import com.firefly.core.distributor.sdk.api.DistributorApi;
import com.firefly.core.distributor.sdk.api.DistributorBrandingApi;
import com.firefly.core.distributor.sdk.api.ShipmentApi;
import com.firefly.core.distributor.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Factory that creates and configures the core distributor SDK {@link ApiClient}
 * and exposes query-side API beans for dependency injection.
 *
 * <p>This factory targets the CRUD query service (core-common-distributor-mgmt),
 * complementing the {@link DistributorBrandingClientFactory} which targets the
 * command service (domain-distributor-branding).
 *
 * <p>ARCH-EXCEPTION: {@code core-common-distributor-mgmt-sdk} (core tier) is used directly because:
 * <ul>
 *   <li>{@link DistributorApi} — {@code domain-distributor-branding-sdk} exposes only write
 *       commands (onboard, reviseBranding, etc.); query-side CRUD (getDistributorById,
 *       updateDistributor, deleteDistributor) is only available in the core SDK.</li>
 *   <li>{@link DistributorBrandingApi} — no equivalent exists in any domain SDK;
 *       branding CRUD reads are only available in the core SDK.</li>
 *   <li>{@link ShipmentApi} — no equivalent exists in any domain SDK;
 *       shipment CRUD and status operations are only available in the core SDK.</li>
 * </ul>
 */
@Component
public class CoreDistributorClientFactory {

    private final ApiClient apiClient;

    public CoreDistributorClientFactory(CoreDistributorProperties properties) {
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(properties.getBasePath());
    }

    /**
     * Provides the {@link DistributorApi} bean from the core SDK for distributor CRUD operations.
     */
    @Bean("coreDistributorApi")
    public DistributorApi coreDistributorApi() {
        return new DistributorApi(apiClient);
    }

    /**
     * Provides the {@link DistributorBrandingApi} bean for branding CRUD operations.
     */
    @Bean("distributorBrandingApi")
    public DistributorBrandingApi distributorBrandingApi() {
        return new DistributorBrandingApi(apiClient);
    }

    /**
     * Provides the {@link ShipmentApi} bean for shipment CRUD and status operations.
     */
    @Bean("coreShipmentApi")
    public ShipmentApi coreShipmentApi() {
        return new ShipmentApi(apiClient);
    }
}
