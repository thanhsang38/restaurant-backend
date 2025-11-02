package com.tranthanhsang.example304.controllers;

import com.tranthanhsang.example304.entity.Category;
import com.tranthanhsang.example304.payload.response.CategoryDTO;
import com.tranthanhsang.example304.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tranthanhsang.example304.security.services.CategoryService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ✅ Trả về danh sách
    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getCategories(
            // THAY ĐỔI 2: Thêm Pageable và thiết lập sắp xếp mặc định
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // THAY ĐỔI 3: Gọi service với tham số pageable
        Page<CategoryDTO> categoryPage = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categoryPage);
    }

    // thêm danh mục
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.create(category);
    }

    // cập nhật danh mục
    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.update(id, category);
    }

    // xóa danh mục
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    // lấy danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    // lấy danh mục theo Parent ID
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryDTO>> getByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getByParentId(parentId));
    }
}
