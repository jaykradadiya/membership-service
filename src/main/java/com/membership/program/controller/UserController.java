package com.membership.program.controller;

import com.membership.program.constants.ApiEndpoints;
import com.membership.program.dto.request.UserRequestDTO;
import com.membership.program.dto.response.UserResponseDTO;
import com.membership.program.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoints.User.BASE_URL)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(ApiEndpoints.User.REGISTER_USER)
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    // Protected: Get current authenticated user
    @GetMapping(ApiEndpoints.User.CURRENT_USER)
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }


}
