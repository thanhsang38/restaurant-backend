package com.tranthanhsang.example304.security.services;

import com.tranthanhsang.example304.model.ERole;

import com.tranthanhsang.example304.model.User;
import com.tranthanhsang.example304.payload.request.SignupRequest;
import com.tranthanhsang.example304.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import com.tranthanhsang.example304.model.Role;
import com.tranthanhsang.example304.repository.RoleRepository;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FileUploadService fileUploadService;

    // Lấy tất cả Employees
    public Page<User> getEmployees(Pageable pageable) {
        return userRepository.findByRole(ERole.ROLE_EMPLOYEE, pageable);
    }

    // Lấy tất cả Admins
    public Page<User> getAdmins(Pageable pageable) {
        return userRepository.findByRole(ERole.ROLE_ADMIN, pageable);
    }

    // Cập nhật thông tin tài khoản
    public User updateUserInfo(Long id, User userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        // Kiểm tra nếu không phải admin thì chỉ được sửa chính mình
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Bạn không có quyền sửa tài khoản này");
        }
        String newImageUrl = userUpdate.getImageUrl();
        String oldImageUrl = user.getImageUrl();

        // Nếu có ảnh mới, có ảnh cũ, và chúng khác nhau -> xóa ảnh cũ
        if (newImageUrl != null && oldImageUrl != null && !newImageUrl.equals(oldImageUrl)) {
            fileUploadService.deleteImage(oldImageUrl);
        }

        if (userUpdate.getUsername() != null && !userUpdate.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userUpdate.getUsername())) {
                throw new RuntimeException("Username '" + userUpdate.getUsername() + "' đã tồn tại!");
            }
            user.setUsername(userUpdate.getUsername()); // Cập nhật username mới
        }
        // Cập nhật thông tin
        user.setFullName(userUpdate.getFullName());
        user.setPhone(userUpdate.getPhone());
        user.setImageUrl(newImageUrl);
        user.setEmail(userUpdate.getEmail());

        // Nếu có mật khẩu mới
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userUpdate.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Cập nhật quyền của user
    public User updateRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        Set<Role> roles = new HashSet<>();
        for (String name : roleNames) {
            Role role = roleRepository.findByName(ERole.valueOf(name))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền: " + name));
            roles.add(role);
        }

        user.setRoles(roles);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        if (user.getImageUrl() != null && !user.getImageUrl().isBlank()) {
            fileUploadService.deleteImage(user.getImageUrl());
        }
        userRepository.delete(user);
    }

    public User registerNewUser(SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setFullName(signUpRequest.getFullName());
        user.setPhone(signUpRequest.getPhone());
        user.setImageUrl(signUpRequest.getImageUrl());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role defaultRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền mặc định"));
            roles.add(defaultRole);
        } else {
            for (String roleName : strRoles) {
                Role role = roleRepository.findByName(ERole.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền: " + roleName));
                roles.add(role);
            }
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
}
