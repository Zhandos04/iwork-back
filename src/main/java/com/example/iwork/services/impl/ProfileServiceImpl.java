package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.PasswordDTO;
import com.example.iwork.dto.requests.ProfileDTO;
import com.example.iwork.dto.responses.ProfileResponse;
import com.example.iwork.entities.Job;
import com.example.iwork.entities.Location;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.JobNotFoundException;
import com.example.iwork.repositories.*;
import com.example.iwork.services.LocationService;
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
    private final JobRepository jobRepository;
    private final LocationService locationService;
    private final LocationRepository locationRepository;

    @Override
    public ProfileResponse getProfile() {
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        ProfileResponse profileResponse = modelMapper.map(user, ProfileResponse.class);

        if (user.getJob() != null) {
            profileResponse.setJobTitle(user.getJob().getTitle());
        }

        if (user.getLocation() != null) {
            profileResponse.setLocation(user.getLocation().getLocationValue());
        } else if (user.getLocationString() != null) {
            profileResponse.setLocation(user.getLocationString());
        }

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
        if (profileDTO.getJobId() != null) {
            Job job = jobRepository.findById(profileDTO.getJobId())
                    .orElseThrow(() -> new JobNotFoundException(
                            "Должность с ID " + profileDTO.getJobId() + " не найдена"));
            updatedUser.setJob(job);
        }
        if (profileDTO.getLocationId() != null) {
            // Получаем локацию по ID
            Location location = locationRepository.findById(profileDTO.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Локация не найдена с ID: " + profileDTO.getLocationId()));
            updatedUser.setLocation(location);

            // Сохраняем строковое представление для удобства
            updatedUser.setLocationString(location.getLocationValue());
        } else if (profileDTO.getLocation() != null && !profileDTO.getLocation().trim().isEmpty()) {
            // Если передана строка локации, создаем или находим существующую запись
            Location location = locationService.getOrCreateLocation(profileDTO.getLocation());
            updatedUser.setLocation(location);

            // Сохраняем строковое представление для удобства
            updatedUser.setLocationString(profileDTO.getLocation());
        }
        updatedUser.setPhone(profileDTO.getPhone());
        updatedUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(updatedUser);

        ProfileResponse profileResponse = modelMapper.map(updatedUser, ProfileResponse.class);

        if (updatedUser.getJob() != null) {
            profileResponse.setJobTitle(updatedUser.getJob().getTitle());
        }

        if (updatedUser.getLocation() != null) {
            profileResponse.setLocation(updatedUser.getLocation().getLocationValue());
        } else if (updatedUser.getLocationString() != null) {
            profileResponse.setLocation(updatedUser.getLocationString());
        }

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
