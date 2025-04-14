package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.UserDTO;
import com.example.iwork.entities.User;
import com.example.iwork.entities.UserRole;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
        User user = getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    @Transactional
    @Override
    public void registerNewUser(UserDTO userDTO) throws UserAlreadyExistsException {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException("A user with that username already exists!");
        }

        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());

        if (optionalUser.isPresent()) {
            if (optionalUser.get().getEmailVerified()) {
                throw new UserAlreadyExistsException("A user with that email already exists");
            } else {
                User user = optionalUser.get();
                user.setUsername(userDTO.getUsername());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                String code = generateCode();
                saveUserConfirmationCode(user.getId(), code);
                emailService.sendEmail(userDTO.getEmail(), "iWork Verify Email", "Your code is: " + code);
            }
        } else {
            User user = convertToUser(userDTO);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailVerified(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setRole(UserRole.ROLE_USER);
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("LLLL yyyy", new Locale("ru"));
            String formattedDate = formatter.format(user.getCreatedAt());
            user.setWithUsSince(formattedDate.substring(0, 1).toUpperCase() +
                    formattedDate.substring(1));
            userRepository.save(user);
            String code = generateCode();
            saveUserConfirmationCode(user.getId(), code);
            emailService.sendEmail(userDTO.getEmail(), "iWork Verify Email", "Your code is: " + code);
        }
    }

    @Transactional
    @Override
    public void update(User user){
        userRepository.save(user);
    }

    @Override
    public void saveUserConfirmationCode(Long id, String code) {
        User user = userRepository.getUserById(id);
        user.setConfirmationCode(code);
        userRepository.save(user);
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

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
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
        saveUserConfirmationCode(user.getId(), code);
        emailService.sendEmail(email, "iWork Resend Code", "Your code is: " + code);
    }
}
