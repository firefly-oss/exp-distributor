package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.products.ProductService;
import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management operations")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    @Operation(summary = "Get product", description = "Retrieve a specific product by ID")
    public Mono<ResponseEntity<ProductDetailDTO>> getProduct(@PathVariable UUID productId) {
        return productService.getProduct(productId)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Register product", description = "Register a new product")
    public Mono<ResponseEntity<UUID>> registerProduct(
            @Valid @RequestBody RegisterProductRequest request) {
        return productService.registerProduct(request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }
}
