package com.tranthanhsang.example304.controllers;

import com.tranthanhsang.example304.entity.OrderItem;
import com.tranthanhsang.example304.payload.response.OrderItemDTO;
import com.tranthanhsang.example304.security.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN')")
public class OrderItemController {
    @Autowired
    private OrderItemService itemService;

    // ✅ Trả về danh sách OrderItemDTO
    @GetMapping
    public List<OrderItemDTO> getAll() {
        return itemService.getAll();
    }

    // Thêm OrderItem
    @PostMapping
    public OrderItem create(@RequestBody OrderItem item) {
        return itemService.create(item);
    }

    // Cập nhật OrderItem
    @PutMapping("/{id}")
    public OrderItem update(@PathVariable Long id, @RequestBody OrderItem item) {
        return itemService.update(id, item);
    }

    // Xóa OrderItem
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    // Lấy OrderItems theo ID đơn hàng
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<OrderItemDTO>> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(itemService.getByOrderId(orderId));
    }
}
