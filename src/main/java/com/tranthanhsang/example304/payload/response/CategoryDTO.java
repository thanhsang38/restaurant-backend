package com.tranthanhsang.example304.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long parentId; // ✅ chỉ lấy ID của danh mục cha
    private String parentName; // ✅ thêm tên danh mục cha
}
