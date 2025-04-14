package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.UserDTO;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.UserAlreadyExistsException;
import com.example.iwork.repositories.UserRepository;
import com.example.iwork.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmailServiceImpl emailService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    @Override
    @Transactional
    public void registerNewUser(UserDTO userDTO) throws UserAlreadyExistsException {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        if (optionalUser.isPresent()) {
            if (optionalUser.get().getEmailVerified()) {
                throw new UserAlreadyExistsException("A user with that email already exists");
            } else {
                String code = generateCode();
                User user = optionalUser.get();
                user.setConfirmationCode(code);
                userRepository.save(user);
                emailService.sendEmail(userDTO.getEmail(), "Iwork Verify Email", "Your code is: " + code);
            }
        } else {
            User user = convertToUser(userDTO);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailVerified(false);
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            String code = generateCode();
            user.setConfirmationCode(code);
            userRepository.save(user);
            emailService.sendEmail(userDTO.getEmail(), "Iwork Verify Email", "Your code is: " + code);
        }
    }

    @Transactional
    @Override
    public void updatePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Scheduled(fixedRate = 3600000)
    @Override
    @Transactional
    public void deleteUnverifiedUsers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<User> unverifiedUsers = userRepository.findAllByEmailVerifiedFalseAndCreatedAtBefore(cutoffTime);
        if (!unverifiedUsers.isEmpty()) {
            userRepository.deleteAll(unverifiedUsers);
        }
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private String generateCode() {
        return Integer.toString((int)(Math.random() * 900000) + 100000);
    }

    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return (UserDetails) authentication.getPrincipal();
        }

        return null;
    }

    @Override
    @Transactional
    public void resentCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        String code = generateCode();
        user.setConfirmationCode(code);
        userRepository.save(user);
        emailService.sendEmail(email, "Iwork Resend Code", "Your code is: " + code);
    }
}
