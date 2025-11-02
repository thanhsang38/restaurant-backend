package com.tranthanhsang.example304.security.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tranthanhsang.example304.entity.TableEntity;
import com.tranthanhsang.example304.repository.TableRepository;
import com.tranthanhsang.example304.entity.enums.Status;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.tranthanhsang.example304.entity.Order;

import com.tranthanhsang.example304.repository.TableRepository;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepo;

    @Autowired
    private TableRepository orderRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Lấy tất cả bàn
    public List<TableEntity> getAll() {
        return tableRepo.findAll();
    }

    // Thêm bàn
    public TableEntity create(TableEntity table) {
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        return tableRepo.save(table);
    }

    // Cập nhật bàn
    public TableEntity update(Long id, TableEntity table) {
        TableEntity existing = tableRepo.findById(id).orElseThrow();
        existing.setNumber(table.getNumber());
        existing.setCapacity(table.getCapacity());
        existing.setStatus(table.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        TableEntity updatedTable = tableRepo.save(existing);
        messagingTemplate.convertAndSend("/topic/table-status", updatedTable);
        return tableRepo.save(existing);
    }

    // Xóa bàn
    public void delete(Long id) {
        tableRepo.deleteById(id);
    }

    // Lấy bàn theo trạng thái
    public List<TableEntity> getTablesByStatus(Status status) {
        return tableRepo.findByStatus(status);
    }

    public TableEntity getById(Long id) {
        return tableRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn với ID: " + id));
    }
}