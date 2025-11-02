package com.tranthanhsang.example304.repository;

import com.tranthanhsang.example304.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tranthanhsang.example304.entity.enums.Status;
import java.util.List;
import com.tranthanhsang.example304.entity.Order;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
    List<TableEntity> findByStatus(Status status);

}
