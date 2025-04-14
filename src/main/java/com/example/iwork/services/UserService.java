package com.example.iwork.services;

import com.example.iwork.dto.requests.UserDTO;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.UserAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserService {
    UserDetails loadUserByUsername(String username);
    void registerNewUser(UserDTO userDTO) throws UserAlreadyExistsException;
    void update(User user);
    void saveUserConfirmationCode(Long id, String code);
    void updatePassword(User user);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    UserDetails getCurrentUser();
    void resentCode(String email);
    void deleteUnverifiedUsers();
    boolean isAdmin();
}
