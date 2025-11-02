package com.tranthanhsang.example304.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tranthanhsang.example304.entity.Bill;
import com.tranthanhsang.example304.entity.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("SELECT b FROM Bill b WHERE b.order.table.id = :tableId")
    List<Bill> findByTableId(@Param("tableId") Long tableId);

    @Query("SELECT b FROM Bill b WHERE b.order.table.id = :tableId AND b.paymentStatus = :paymentStatus")
    List<Bill> findByTableIdAndPaymentStatus(@Param("tableId") Long tableId,
            @Param("paymentStatus") PaymentStatus paymentStatus);

    List<Bill> findByPaymentStatus(PaymentStatus paymentStatus);

    Optional<Bill> findByOrderId(Long orderId);

}
