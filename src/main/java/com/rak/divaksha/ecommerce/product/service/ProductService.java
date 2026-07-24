package com.rak.divaksha.ecommerce.product.service;

import com.rak.divaksha.ecommerce.product.dto.CreateProductRequest;
import com.rak.divaksha.ecommerce.product.dto.ProductResponse;
import com.rak.divaksha.ecommerce.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    ProductResponse getById(Long id);

    ProductResponse getBySlug(String slug);

    Page<ProductResponse> getProducts(
            int page,
            int size,
            String search,
            Long categoryId);

    void delete(Long id);

}