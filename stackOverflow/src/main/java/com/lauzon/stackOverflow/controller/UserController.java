package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.request.UpdateAccountRequest;
import com.lauzon.stackOverflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/info")
    public ResponseEntity<?> updateUserInfo(@RequestBody UpdateAccountRequest request) {
        try {
            userService.updateUserInfo(request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
