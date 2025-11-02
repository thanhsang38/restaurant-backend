package com.tranthanhsang.example304.security.services;

import com.tranthanhsang.example304.entity.Promotion;
import com.tranthanhsang.example304.entity.Product;
import com.tranthanhsang.example304.repository.PromotionRepository;
import com.tranthanhsang.example304.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private ProductRepository productRepository;

    // Lấy tất cả Promotion
    @Transactional(readOnly = true)
    public Page<Promotion> getAll(Pageable pageable) {
        // Gọi phương thức mới trong repository
        return promotionRepository.findAllWithProducts(pageable);
    }

    // Tạo mới Promotion
    @Transactional
    public Promotion create(Promotion promotion) {
        List<Product> attachedProducts = new ArrayList<>();

        if (promotion.getProducts() != null) {
            for (Product p : promotion.getProducts()) {
                Product product = productRepository.findById(p.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + p.getId()));
                attachedProducts.add(product);
                List<Promotion> promos = product.getPromotions();
                if (!promos.contains(promotion)) {
                    promos.add(promotion);
                }
            }
        }

        promotion.setProducts(attachedProducts);
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        return promotionRepository.save(promotion);
    }

    // Cập nhật Promotion
    @Transactional
    public Promotion update(Long id, Promotion promotion) {
        Promotion existing = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + id));

        existing.setName(promotion.getName());
        existing.setDiscountPercentage(promotion.getDiscountPercentage());
        existing.setDiscountAmount(promotion.getDiscountAmount());
        existing.setStartDate(promotion.getStartDate());
        existing.setEndDate(promotion.getEndDate());
        existing.setIsActive(promotion.getIsActive());
        existing.setUpdatedAt(LocalDateTime.now());

        List<Product> updatedProducts = new ArrayList<>();
        if (promotion.getProducts() != null) {
            for (Product p : promotion.getProducts()) {
                Product product = productRepository.findById(p.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + p.getId()));
                updatedProducts.add(product);
                List<Promotion> promos = product.getPromotions();
                if (!promos.contains(existing)) {
                    promos.add(existing);
                }
            }
        }

        existing.setProducts(updatedProducts);
        return promotionRepository.save(existing);
    }

    // Xóa Promotion
    @Transactional
    public void delete(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + id));

        for (Product p : promotion.getProducts()) {
            p.getPromotions().remove(promotion);
        }

        promotionRepository.deleteById(id);
    }

    // Lấy khuyến mãi đang hoạt động
    public List<Promotion> getActivePromotions() {
        LocalDate today = LocalDate.now();
        return promotionRepository.findByStartDateBeforeAndEndDateAfter(today, today);
    }
}
