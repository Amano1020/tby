package com.tby.api.service;

import com.tby.api.dto.ProductResponse;
import com.tby.api.model.Product;
import com.tby.api.model.ProductCategory;
import com.tby.api.repository.ProductCategoryRepository;
import com.tby.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.tby.api.exception.BusinessException;
import com.tby.api.exception.ErrorCode;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
        private final ProductRepository productRepository;
        private final ProductCategoryRepository categoryRepository;

        @Cacheable(value = "products", cacheManager = "caffeineCacheManager")
        public ProductResponse getProductById(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

                ProductCategory category = categoryRepository.findById(product.getCategoryId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

                return ProductResponse.builder()
                                .productId(product.getProductId())
                                .categoryId(category.getCategoryId())
                                .categoryName(category.getCategoryName())
                                .unitPrice(product.getUnitPrice())
                                .taxRate(category.getTaxRate())
                                .build();
        }

        public Product getProductEntityById(Long productId) {
                return productRepository.findById(productId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        }

        @Cacheable(value = "categories", cacheManager = "caffeineCacheManager")
        public ProductCategory getCategoryById(Long categoryId) {
                return categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        public List<Product> getProductsByIds(List<Long> productIds) {
                if (productIds == null || productIds.isEmpty())
                        return Collections.emptyList();
                // Since it's a small list typically for a user, DB IN query is fine.
                // Caching lists requires more complex invalidation strategies, so we fetch from
                // DB.
                return productRepository.findAllById(productIds);
        }

        public List<ProductCategory> getCategoriesByIds(List<Long> categoryIds) {
                if (categoryIds == null || categoryIds.isEmpty())
                        return Collections.emptyList();
                return categoryRepository.findAllById(categoryIds);
        }
}
