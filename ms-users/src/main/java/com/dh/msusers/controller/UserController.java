package com.dh.msusers.controller;

import com.dh.msusers.model.User;
import com.dh.msusers.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public User getUser(@PathVariable String userId) {
        log.info("user controller");
        return userService.findBillByUserId(userId);
    }

    @GetMapping("/allBills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> allBills() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllBills());
    }

}
