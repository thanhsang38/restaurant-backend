package com.tranthanhsang.example304.controllers;

import com.tranthanhsang.example304.entity.TableEntity;
import com.tranthanhsang.example304.security.services.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.tranthanhsang.example304.entity.enums.Status;
import com.tranthanhsang.example304.payload.response.BillDTO;
import com.tranthanhsang.example304.entity.Order;
import com.tranthanhsang.example304.security.services.TableService;
import com.tranthanhsang.example304.security.services.BillService;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    @Autowired
    private TableService tableService;
    @Autowired
    private BillService billService;

    // ✅ Trả về danh sách TableEntity
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN')")
    public List<TableEntity> getAll() {
        return tableService.getAll();
    }

    // Thêm bàn
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public TableEntity create(@RequestBody TableEntity table) {
        return tableService.create(table);
    }

    // Cập nhật bàn
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN')")
    public TableEntity update(@PathVariable Long id, @RequestBody TableEntity table) {
        return tableService.update(id, table);
    }

    // Xóa bàn
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void delete(@PathVariable Long id) {
        tableService.delete(id);
    }

    // Lấy bàn theo trạng thái
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN')")
    public ResponseEntity<?> getByStatus(@RequestParam String status) {
        try {
            Status enumStatus = Status.valueOf(status.toUpperCase());
            return ResponseEntity.ok(tableService.getTablesByStatus(enumStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ: " + status);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_ADMIN', 'ROLE_USER')") // Cho phép tất cả user đã đăng nhập
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            TableEntity table = tableService.getById(id);
            return ResponseEntity.ok(table);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}