package com.rak.divaksha.ecommerce.product.service.impl;


import com.rak.divaksha.ecommerce.category.entity.Category;
import com.rak.divaksha.ecommerce.category.repository.CategoryRepository;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import com.rak.divaksha.ecommerce.product.dto.CreateProductRequest;
import com.rak.divaksha.ecommerce.product.dto.UpdateProductRequest;
import com.rak.divaksha.ecommerce.product.dto.ProductResponse;
import com.rak.divaksha.ecommerce.product.entity.Product;
import com.rak.divaksha.ecommerce.product.entity.ProductImage;
import com.rak.divaksha.ecommerce.product.repository.ProductRepository;
import com.rak.divaksha.ecommerce.product.service.ProductService;
import com.rak.divaksha.ecommerce.product.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse create(CreateProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("SKU already exists");
        }

        String slug = generateSlug(request.getName());

        if (productRepository.existsBySlug(slug)) {
            throw new BadRequestException("Product already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();

        product.setCategory(category);
        product.setName(request.getName());
        product.setSlug(slug);
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStock(request.getStock());
        product.setThumbnailUrl(request.getThumbnailUrl());
        product.setActive(request.getActive());
        product.setFeatured(request.getFeatured());

        if (request.getImages() != null) {

            int order = 1;

            for (String url : request.getImages()) {

                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(url);
                image.setDisplayOrder(order++);

                product.getImages().add(image);

            }

        }

        product = productRepository.save(product);

        return map(product);

    }

    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (request.getCategoryId() != null) {

            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            product.setCategory(category);

        }

        if (request.getName() != null) {

            String slug = generateSlug(request.getName());

            if (!slug.equals(product.getSlug())
                    && productRepository.existsBySlug(slug)) {

                throw new BadRequestException("Product already exists");

            }

            product.setName(request.getName());
            product.setSlug(slug);

        }

        if (request.getShortDescription() != null)
            product.setShortDescription(request.getShortDescription());

        if (request.getDescription() != null)
            product.setDescription(request.getDescription());

        if (request.getBrand() != null)
            product.setBrand(request.getBrand());

        if (request.getPrice() != null)
            product.setPrice(request.getPrice());

        if (request.getDiscountPrice() != null)
            product.setDiscountPrice(request.getDiscountPrice());

        if (request.getStock() != null)
            product.setStock(request.getStock());

        if (request.getThumbnailUrl() != null)
            product.setThumbnailUrl(request.getThumbnailUrl());

        if (request.getActive() != null)
            product.setActive(request.getActive());

        if (request.getFeatured() != null)
            product.setFeatured(request.getFeatured());

        if (request.getSku() != null &&
                !request.getSku().equals(product.getSku())) {

            if (productRepository.existsBySku(request.getSku())) {
                throw new BadRequestException("SKU already exists");
            }

            product.setSku(request.getSku());

        }

        if (request.getImages() != null) {

            product.getImages().clear();

            int order = 1;

            for (String url : request.getImages()) {

                ProductImage image = new ProductImage();

                image.setProduct(product);
                image.setImageUrl(url);
                image.setDisplayOrder(order++);

                product.getImages().add(image);

            }

        }

        product = productRepository.save(product);

        return map(product);

    }

    private String generateSlug(String value) {

        String slug = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        slug = slug.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return StringUtils.defaultIfBlank(slug, "product");

    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return map(product);

    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return map(product);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            int page,
            int size,
            String search,
            Long categoryId) {

        return productRepository.findAll(
                        ProductSpecification.search(search, categoryId),
                        PageRequest.of(page, size))
                .map(this::map);

    }

    @Override
    public void delete(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productRepository.delete(product);

    }

    private ProductResponse map(Product product) {

        List<String> images = new ArrayList<>();

        if (product.getImages() != null) {

            images = product.getImages()
                    .stream()
                    .sorted((a, b) -> a.getDisplayOrder().compareTo(b.getDisplayOrder()))
                    .map(ProductImage::getImageUrl)
                    .toList();

        }

        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .slug(product.getSlug())
                .shortDescription(product.getShortDescription())
                .description(product.getDescription())
                .sku(product.getSku())
                .brand(product.getBrand())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .stock(product.getStock())
                .thumbnailUrl(product.getThumbnailUrl())
                .active(product.getActive())
                .featured(product.getFeatured())
                .images(images)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

    }

}