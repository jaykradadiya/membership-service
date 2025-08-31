package com.membership.program.service;

import com.membership.program.dto.request.UserRequestDTO;
import com.membership.program.dto.response.UserResponseDTO;
import com.membership.program.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    User loadUserByUsername(String username) throws UsernameNotFoundException;

    UserResponseDTO createUser(UserRequestDTO dto);

    UserResponseDTO getCurrentUser();
}
