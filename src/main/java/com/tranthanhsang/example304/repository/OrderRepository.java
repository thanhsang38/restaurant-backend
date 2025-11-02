package com.tranthanhsang.example304.repository;

import com.tranthanhsang.example304.entity.Order;
import com.tranthanhsang.example304.entity.enums.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.table.id = :tableId")
    List<Order> findByTableId(@Param("tableId") Long tableId);

    Optional<Order> findFirstByTableIdAndStatus(Long tableId, OrderStatus status);
}
