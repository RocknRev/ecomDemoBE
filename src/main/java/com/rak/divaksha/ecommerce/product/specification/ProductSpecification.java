package com.rak.divaksha.ecommerce.product.specification;

import com.rak.divaksha.ecommerce.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> search(
            String search,
            Long categoryId) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            if (search != null && !search.isBlank()) {

                String value = "%" + search.toLowerCase() + "%";

                predicate = cb.and(
                        predicate,
                        cb.or(
                                cb.like(cb.lower(root.get("name")), value),
                                cb.like(cb.lower(root.get("brand")), value),
                                cb.like(cb.lower(root.get("sku")), value)
                        )
                );
            }

            if (categoryId != null) {

                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("category").get("id"), categoryId)
                );

            }

            return predicate;

        };

    }

}