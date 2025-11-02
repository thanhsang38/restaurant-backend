package com.tranthanhsang.example304.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.tranthanhsang.example304.security.services.OrderService;
import com.tranthanhsang.example304.entity.Order;
import com.tranthanhsang.example304.entity.enums.OrderStatus;
import com.tranthanhsang.example304.payload.response.OrderDTO;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN')")

public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Trả về danh sách OrderDTO
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAll(
            // Nhận số trang, mặc định là trang 0 (giống hệt ProductController)
            @RequestParam(defaultValue = "0") int page) {
        Page<OrderDTO> orders = orderService.getAll(page);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/submit-draft")
    public ResponseEntity<Void> submitDraftOrder(@RequestBody OrderDTO draftOrder) { // SỬA: Đổi kiểu tham số thành

        System.out.println("Nhận được đơn nháp (dạng OrderDTO) từ bàn: " + draftOrder.getTableNumber());

        // Gửi đối tượng draftOrder đến topic mà các client nhân viên đang lắng nghe
        messagingTemplate.convertAndSend("/topic/draft-orders", draftOrder);

        return ResponseEntity.ok().build();
    }

    // 💳 Bước 5: Khách yêu cầu thanh toán
    @PostMapping("/request-payment/{tableId}")
    @PreAuthorize("permitAll()") // Cho phép cả bên khách gọi
    public ResponseEntity<Void> requestPayment(@PathVariable Long tableId) {
        System.out.println("💬 Bàn " + tableId + " yêu cầu thanh toán!");

        // Gửi thông báo đến tất cả nhân viên đang mở giao diện
        messagingTemplate.convertAndSend("/topic/payment-request", tableId);

        return ResponseEntity.ok().build();
    }

    // 💬 Khi nhân viên chấp nhận thanh toán
    @PostMapping("/accept-payment/{tableId}")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN')")
    public ResponseEntity<Void> acceptPayment(@PathVariable Long tableId) {
        System.out.println("✅ Nhân viên đã chấp nhận yêu cầu thanh toán cho bàn " + tableId);

        // Gửi thông báo đến bàn cụ thể qua topic riêng
        messagingTemplate.convertAndSend("/topic/payment-response/" + tableId, "accepted");

        return ResponseEntity.ok().build();
    }

    // Thêm đơn hàng
    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody Order order) {
        OrderDTO dto = orderService.create(order);
        return ResponseEntity.ok(dto);
    }

    // Cập nhật đơn hàng
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> update(@PathVariable Long id, @RequestBody Order order) {

        // 2. Gọi Service (Service của bạn đã được sửa để trả về OrderDTO)
        OrderDTO dto = orderService.update(id, order);

        // 3. Trả về ResponseEntity<OrderDTO>
        return ResponseEntity.ok(dto);
    }

    // Xóa đơn hàng
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

    // Lấy đơn hàng theo trạng thái
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<OrderDTO> orders = orderService.getOrdersByStatus(orderStatus);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ: " + status);
        }
    }

    // Lấy đơn hàng theo ID bàn
    @GetMapping("/tables/{tableId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getOrdersByTable(tableId));
    }

    // Lấy đơn hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/tables/{tableId}/active-edit")
    public ResponseEntity<OrderDTO> getActiveOrderForEdit(@PathVariable Long tableId) {
        try {
            // Gọi hàm Service mới
            OrderDTO dto = orderService.getActiveOrderForEdit(tableId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}
