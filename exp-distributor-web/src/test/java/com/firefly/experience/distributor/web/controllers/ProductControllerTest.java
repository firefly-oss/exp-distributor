package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.products.ProductService;
import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateProductRequest;
import org.fireflyframework.web.error.config.ErrorHandlingProperties;
import org.fireflyframework.web.error.converter.ExceptionConverterService;
import org.fireflyframework.web.error.service.ErrorResponseNegotiator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ProductController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class
        })
class ProductControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProductService productService;

    @MockBean
    private ExceptionConverterService exceptionConverterService;
    @MockBean
    private ErrorHandlingProperties errorHandlingProperties;
    @MockBean
    private ErrorResponseNegotiator errorResponseNegotiator;

    private static final String BASE_PATH = "/api/v1/experience/distributors/{distributorId}/products";

    private ProductDetailDTO buildProductDetailDTO(UUID distributorId, UUID productId) {
        return ProductDetailDTO.builder()
                .id(productId)
                .distributorId(distributorId)
                .name("Test Product")
                .description("A test product")
                .isActive(true)
                .build();
    }

    @Test
    void GET_listProducts_shouldReturn200WithList() {
        UUID distributorId = UUID.randomUUID();
        ProductDetailDTO product = buildProductDetailDTO(distributorId, UUID.randomUUID());
        when(productService.listProducts(distributorId)).thenReturn(Flux.just(product));

        webClient.get()
                .uri(BASE_PATH, distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetailDTO.class)
                .hasSize(1);
    }

    @Test
    void POST_registerProduct_shouldReturn201WithId() {
        UUID distributorId = UUID.randomUUID();
        UUID newId = UUID.randomUUID();

        when(productService.registerProduct(eq(distributorId), any(RegisterProductRequest.class)))
                .thenReturn(Mono.just(newId));

        webClient.post()
                .uri(BASE_PATH, distributorId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Test Product","category":"Electronics"}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(newId));
    }

    @Test
    void GET_getProduct_shouldReturn200WithDTO() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ProductDetailDTO product = buildProductDetailDTO(distributorId, productId);

        when(productService.getProduct(distributorId, productId)).thenReturn(Mono.just(product));

        webClient.get()
                .uri(BASE_PATH + "/{productId}", distributorId, productId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDetailDTO.class)
                .value(dto -> assertThat(dto.getId()).isEqualTo(productId));
    }

    @Test
    void PUT_updateProduct_shouldReturn200WithId() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(productService.updateProduct(eq(distributorId), eq(productId), any(UpdateProductRequest.class)))
                .thenReturn(Mono.just(productId));

        webClient.put()
                .uri(BASE_PATH + "/{productId}", distributorId, productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name":"Updated Name","isActive":true}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UUID.class)
                .value(id -> assertThat(id).isEqualTo(productId));

        verify(productService).updateProduct(eq(distributorId), eq(productId), any());
    }

    @Test
    void DELETE_removeProduct_shouldReturn204() {
        UUID distributorId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(productService.removeProduct(distributorId, productId)).thenReturn(Mono.empty());

        webClient.delete()
                .uri(BASE_PATH + "/{productId}", distributorId, productId)
                .exchange()
                .expectStatus().isNoContent();

        verify(productService).removeProduct(distributorId, productId);
    }

    @Test
    void GET_listActiveProducts_shouldReturn200WithFilteredList() {
        UUID distributorId = UUID.randomUUID();
        ProductDetailDTO product = buildProductDetailDTO(distributorId, UUID.randomUUID());
        when(productService.listActiveProducts(distributorId)).thenReturn(Flux.just(product));

        webClient.get()
                .uri(BASE_PATH + "/active", distributorId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetailDTO.class)
                .hasSize(1);

        verify(productService).listActiveProducts(distributorId);
    }
}
