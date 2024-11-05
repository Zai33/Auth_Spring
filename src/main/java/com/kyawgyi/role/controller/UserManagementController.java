package com.kyawgyi.role.controller;

import com.kyawgyi.role.dto.ReqRes;
import com.kyawgyi.role.model.Users;
import com.kyawgyi.role.service.UserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reqRes) {
        return ResponseEntity.ok(userManagementService.register(reqRes));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(userManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(userManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable int userId) {
        return ResponseEntity.ok(userManagementService.getUserById(userId));
    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable int userId, @RequestBody Users req) {
        return ResponseEntity.ok(userManagementService.updateUser(userId,req));
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable int userId) {
        return ResponseEntity.ok(userManagementService.deleteUser(userId));
    }

    @GetMapping("/adminuser/profile")
    public ResponseEntity<ReqRes> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes resp = userManagementService.getMyInfo(email);
        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ReqRes> logout(@RequestHeader("Authorization") String token) {
        ReqRes res = userManagementService.logout(token);
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

}
