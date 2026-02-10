package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Map<String, Object> response = userService.getUserInfo();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
