package com.tranthanhsang.example304.security.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tranthanhsang.example304.entity.OrderItem;
import com.tranthanhsang.example304.entity.Product;
import com.tranthanhsang.example304.payload.response.OrderItemDTO;
import com.tranthanhsang.example304.repository.OrderItemRepository;
import com.tranthanhsang.example304.repository.OrderRepository;
import com.tranthanhsang.example304.repository.ProductRepository;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository itemRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private OrderRepository orderRepo;

    // Lấy tất cả OrderItemDTO
    public List<OrderItemDTO> getAll() {
        List<OrderItem> items = itemRepo.findAll();
        return items.stream().map(this::convertItemToDTO).toList();
    }

    // Thêm OrderItem
    public OrderItem create(OrderItem item) {
        item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return itemRepo.save(item);
    }

    public OrderItem update(Long id, OrderItem item) {
        // 1. Tìm món ăn đã tồn tại
        OrderItem existing = itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OrderItem với id: " + id));

        // 2. Lấy lại sản phẩm từ DB để đảm bảo hợp lệ
        Long productId = item.getProduct().getId();
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + productId));

        // 3. Cập nhật dữ liệu
        existing.setProduct(product);
        existing.setQuantity(item.getQuantity());
        existing.setPrice(item.getPrice());
        existing.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        existing.setUpdatedAt(LocalDateTime.now());

        return itemRepo.save(existing);
    }

    // Xóa OrderItem
    public void delete(Long id) {
        itemRepo.deleteById(id);
    }

    // Chuyển đổi OrderItem entity sang OrderItemDTO
    public OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId()); // ✅ gán id
        dto.setProductId(item.getProduct().getId());
        dto.setImageUrl(item.getProduct().getImageUrl());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSubtotal(item.getSubtotal());
        dto.setOrderId(item.getOrder().getId());
        return dto;
    }

    // Lấy OrderItems theo ID đơn hàng
    public List<OrderItemDTO> getByOrderId(Long orderId) {
        List<OrderItem> items = itemRepo.findByOrder_Id(orderId);

        return items.stream().map(item -> {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setId(item.getId()); // ✅ thêm dòng này
            dto.setProductId(item.getProduct().getId());
            dto.setImageUrl(item.getProduct().getImageUrl());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            dto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            dto.setOrderId(orderId);
            return dto;
        }).toList();
    }
}
