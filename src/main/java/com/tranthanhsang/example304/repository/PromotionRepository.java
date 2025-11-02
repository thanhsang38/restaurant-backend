package com.tranthanhsang.example304.repository;

import com.tranthanhsang.example304.entity.Promotion;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByStartDateBeforeAndEndDateAfter(LocalDate start, LocalDate end);

    @Query(value = "SELECT p FROM Promotion p LEFT JOIN FETCH p.products", countQuery = "SELECT COUNT(p) FROM Promotion p")
    Page<Promotion> findAllWithProducts(Pageable pageable);

    @Query("SELECT p FROM Promotion p LEFT JOIN FETCH p.products WHERE p.id = :id")
    Optional<Promotion> findByIdWithProducts(@Param("id") Long id);
}
