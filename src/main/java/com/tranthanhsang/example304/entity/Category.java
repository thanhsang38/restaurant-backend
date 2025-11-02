package com.tranthanhsang.example304.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;

    private String imageUrl; // ✅ thêm trường ảnh

    @ManyToOne // ✅ thêm quan hệ danh mục cha
    @JoinColumn(name = "parent_id")

    private Category parentCategory;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}