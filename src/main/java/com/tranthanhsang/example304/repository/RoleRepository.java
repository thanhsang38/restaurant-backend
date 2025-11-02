package com.tranthanhsang.example304.repository;

import com.tranthanhsang.example304.model.ERole;
import com.tranthanhsang.example304.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}
