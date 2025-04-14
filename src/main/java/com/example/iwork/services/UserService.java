package com.example.iwork.services;

import com.example.iwork.dto.requests.UserDTO;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetails loadUserByUsername(String username);
    void registerNewUser(UserDTO userDTO) throws UserAlreadyExistsException;
    void updatePassword(User user);
    UserDetails getCurrentUser();
    void resentCode(String email);
    void deleteUnverifiedUsers();
}
