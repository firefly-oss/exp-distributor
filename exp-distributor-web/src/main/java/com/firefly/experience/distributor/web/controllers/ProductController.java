package com.firefly.experience.distributor.web.controllers;

import com.firefly.experience.distributor.core.products.ProductService;
import com.firefly.experience.distributor.interfaces.dtos.ProductDetailDTO;
import com.firefly.experience.distributor.interfaces.dtos.RegisterProductRequest;
import com.firefly.experience.distributor.interfaces.dtos.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experience/distributors/{distributorId}/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Distributor product management operations")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products", description = "List all products for a distributor")
    public Mono<ResponseEntity<List<ProductDetailDTO>>> listProducts(@PathVariable UUID distributorId) {
        return productService.listProducts(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(summary = "Register product", description = "Register a new product for a distributor")
    public Mono<ResponseEntity<UUID>> registerProduct(
            @PathVariable UUID distributorId,
            @Valid @RequestBody RegisterProductRequest request) {
        return productService.registerProduct(distributorId, request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product", description = "Retrieve a specific product by ID")
    public Mono<ResponseEntity<ProductDetailDTO>> getProduct(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId) {
        return productService.getProduct(distributorId, productId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product", description = "Update an existing product")
    public Mono<ResponseEntity<UUID>> updateProduct(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(distributorId, productId, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove product", description = "Remove a product from the distributor")
    public Mono<ResponseEntity<Void>> removeProduct(
            @PathVariable UUID distributorId,
            @PathVariable UUID productId) {
        return productService.removeProduct(distributorId, productId)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }

    @GetMapping("/active")
    @Operation(summary = "List active products", description = "List all active products for a distributor")
    public Mono<ResponseEntity<List<ProductDetailDTO>>> listActiveProducts(@PathVariable UUID distributorId) {
        return productService.listActiveProducts(distributorId)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
