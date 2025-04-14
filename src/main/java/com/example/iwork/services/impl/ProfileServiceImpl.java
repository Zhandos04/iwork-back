package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.PasswordDTO;
import com.example.iwork.dto.requests.ProfileDTO;
import com.example.iwork.dto.responses.ProfileResponse;
import com.example.iwork.entities.User;
import com.example.iwork.repositories.ReviewRepository;
import com.example.iwork.repositories.SalaryRepository;
import com.example.iwork.repositories.UserRepository;
import com.example.iwork.services.ProfileService;
import com.example.iwork.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SalaryRepository salaryRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ProfileResponse getProfile() {
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        ProfileResponse profileResponse = modelMapper.map(user, ProfileResponse.class);
        profileResponse.setReviewsCount(reviewRepository.countByUser(user));
        profileResponse.setSalaryCount(salaryRepository.countByUser(user));

        return profileResponse;
    }

    @Override
    @Transactional
    public ProfileResponse editProfile(ProfileDTO profileDTO) {
        User updatedUser = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        updatedUser.setCompany(profileDTO.getCompany());
        updatedUser.setFullName(profileDTO.getFullName());
        updatedUser.setJobTitle(profileDTO.getJobTitle());
        updatedUser.setLocation(profileDTO.getLocation());
        updatedUser.setPhone(profileDTO.getPhone());
        updatedUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(updatedUser);

        ProfileResponse profileResponse = modelMapper.map(updatedUser, ProfileResponse.class);
        profileResponse.setReviewsCount(reviewRepository.countByUser(updatedUser));
        profileResponse.setSalaryCount(salaryRepository.countByUser(updatedUser));

        return profileResponse;
    }

    @Override
    @Transactional
    public void updatePassword(PasswordDTO passwordDTO) {
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        } else {
            throw new BadCredentialsException("Старый пароль неверный");
        }
        userRepository.save(user);
    }
}
