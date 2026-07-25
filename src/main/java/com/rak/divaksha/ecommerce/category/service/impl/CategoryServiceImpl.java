package com.rak.divaksha.ecommerce.category.service.impl;

import com.rak.divaksha.ecommerce.category.dto.CreateCategoryRequest;
import com.rak.divaksha.ecommerce.category.dto.UpdateCategoryRequest;
import com.rak.divaksha.ecommerce.category.dto.CategoryResponse;
import com.rak.divaksha.ecommerce.category.entity.Category;
import com.rak.divaksha.ecommerce.category.repository.CategoryRepository;
import com.rak.divaksha.ecommerce.category.service.CategoryService;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {

        String slug = generateSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category already exists");
        }

        Category category = new Category();

        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

            category.setParent(parent);
        }

        category = categoryRepository.save(category);

        return map(category);
    }

    @Override
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String slug = generateSlug(request.getName());

        if (!slug.equals(category.getSlug()) && categoryRepository.existsBySlug(slug)) {
            throw new BadRequestException("Category already exists");
        }

        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setActive(request.getActive());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));

            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return map(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return map(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {

        return categoryRepository.findAllByActiveTrueOrderBySortOrderAscNameAsc()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        categoryRepository.delete(category);
    }

    private CategoryResponse map(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive())
                .sortOrder(category.getSortOrder())
                .parentId(category.getParent() == null ? null : category.getParent().getId())
                .build();
    }

    private String generateSlug(String value) {

        String slug = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        slug = slug.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        return StringUtils.defaultIfBlank(slug, "category");
    }

}