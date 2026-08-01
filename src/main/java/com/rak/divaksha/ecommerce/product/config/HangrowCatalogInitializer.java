package com.rak.divaksha.ecommerce.product.config;

import com.rak.divaksha.ecommerce.category.entity.Category;
import com.rak.divaksha.ecommerce.category.repository.CategoryRepository;
import com.rak.divaksha.ecommerce.product.entity.Product;
import com.rak.divaksha.ecommerce.product.entity.ProductImage;
import com.rak.divaksha.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Keeps one selectable product per Hangrow range; flavours are variants, not separate storefront products. */
@Configuration
@RequiredArgsConstructor
public class HangrowCatalogInitializer {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionTemplate transactionTemplate;

    @Bean
    ApplicationRunner seedHangrowCatalog() {
        return args -> transactionTemplate.executeWithoutResult(status -> replaceCatalog());
    }

    void replaceCatalog() {
        Category thins = category("Hangrow Thins", "hangrow-thins", "Light, crunchy Hangrow Thins in vibrant flavours.", "/images/handgrowthins.png", 1);
        Category miniThins = category("Hangrow Mini Thins", "hangrow-mini-thins", "Heartily puffed Mini Thins that are baked, not fried.", "/images/mini-thins1.png", 2);

        Map<String, String> thinFlavours = new LinkedHashMap<>();
        thinFlavours.put("Tomato Chilli", "/images/hh1.jpg");
        thinFlavours.put("Cheese Magic", "/images/hh2.jpg");
        thinFlavours.put("Golden Corn", "/images/hh3.jpg");
        thinFlavours.put("Chaat Masala", "/images/hh4.jpg");
        thinFlavours.put("Ancient Grains", "/images/hh5.jpg");
        thinFlavours.put("Lime N Lemon", "/images/hh6.jpg");
        Product thinsProduct = existingProduct("HANGROW-THINS", "HANGROW-THINS-TOMATO-CHILLI");
        configure(thinsProduct, "HANGROW-THINS", thins, "HANGROW Thins", "Crunchy corn thins in six irresistible flavours.", "Choose your favourite Hangrow Thins flavour for a light, satisfying crunch. Enjoy straight from the pack, with a dip, or alongside your favourite dish.", "/images/hh1.jpg", "150 g", BigDecimal.valueOf(150), thinFlavours, "/images/handgrowthins.png", List.of("Made with corn", "Crunchy and flavourful", "Six flavours to choose from"));

        Map<String, String> miniFlavours = new LinkedHashMap<>();
        miniFlavours.put("BBQ", "/images/hnbbq.jpg");
        miniFlavours.put("Cheese Magic", "/images/hncheese.jpg");
        miniFlavours.put("Cream N Onion", "/images/hncream-onion.jpg");
        Product miniProduct = existingProduct("HANGROW-MINI-THINS", "HANGROW-MINI-THINS-BBQ");
        configure(miniProduct, "HANGROW-MINI-THINS", miniThins, "HANGROW Mini Thins", "Heartily puffed Mini Thins that are baked, not fried.", "Pick a bold Mini Thins flavour for a light, crunchy snack. Hangrow Mini Thins are heartily puffed, baked and never fried.", "/images/hnbbq.jpg", "50 g", BigDecimal.valueOf(50), miniFlavours, "/images/mini-thins1.png", List.of("Heartily puffed", "Baked, not fried", "Three flavours to choose from"));

        productRepository.findAll().stream()
                .filter(product -> product.getSku() != null && product.getSku().startsWith("HANGROW-")
                        && !product.getId().equals(thinsProduct.getId()) && !product.getId().equals(miniProduct.getId()))
                .forEach(product -> product.setActive(false));
    }

    private Product existingProduct(String sku, String legacySku) {
        return productRepository.findBySku(sku)
                .or(() -> productRepository.findBySku(legacySku))
                .orElseGet(Product::new);
    }

    private Category category(String name, String slug, String description, String image, int sortOrder) {
        Category category = categoryRepository.findBySlug(slug).orElseGet(Category::new);
        category.setName(name);
        category.setSlug(slug);
        category.setDescription(description);
        category.setImageUrl(image);
        category.setActive(true);
        category.setSortOrder(sortOrder);
        return categoryRepository.save(category);
    }

    private void configure(Product product, String sku, Category category, String name, String shortDescription, String description,
                           String thumbnail, String netWeight, BigDecimal price, Map<String, String> flavourImages,
                           String rangeImage, List<String> highlights) {
        product.setCategory(category);
        product.setName(name);
        product.setSlug(name.toLowerCase().replace(" ", "-"));
        product.setSku(sku);
        product.setBrand("Hangrow");
        product.setShortDescription(shortDescription);
        product.setDescription(description);
        product.setNetWeight(netWeight);
        product.setNutritionInfo("Please refer to the product pack for complete nutrition information.");
        product.setAllergenInfo("Please refer to the product pack for allergen information.");
        product.setStorageInstructions("Store in a cool, dry place. Keep the pack tightly closed after opening.");
        product.setPrice(price);
        product.setDiscountPrice(null);
        product.setStock(100);
        product.setThumbnailUrl(thumbnail);
        product.setActive(true);
        product.setFeatured(true);
        product.setFlavors(new ArrayList<>(flavourImages.keySet()));
        product.setFlavorImages(new LinkedHashMap<>(flavourImages));
        product.setHighlights(new ArrayList<>(highlights));
        product.setIngredients(new ArrayList<>());
        product.getImages().clear();
        addImage(product, thumbnail, 1);
        addImage(product, rangeImage, 2);
        productRepository.save(product);
    }

    private void addImage(Product product, String url, int displayOrder) {
        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(url);
        image.setDisplayOrder(displayOrder);
        product.getImages().add(image);
    }
}
