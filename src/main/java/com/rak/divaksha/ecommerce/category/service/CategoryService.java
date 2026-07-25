package com.rak.divaksha.ecommerce.category.service;

import com.rak.divaksha.ecommerce.category.dto.CategoryResponse;
import com.rak.divaksha.ecommerce.category.dto.CreateCategoryRequest;
import com.rak.divaksha.ecommerce.category.dto.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CreateCategoryRequest request);

    CategoryResponse update(Long id, UpdateCategoryRequest request);

    CategoryResponse getById(Long id);

    List<CategoryResponse> getAll();

    void delete(Long id);

}