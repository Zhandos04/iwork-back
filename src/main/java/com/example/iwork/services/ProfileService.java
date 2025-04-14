package com.example.iwork.services;


import com.example.iwork.dto.requests.PasswordDTO;
import com.example.iwork.dto.requests.ProfileDTO;
import com.example.iwork.dto.responses.ProfileResponse;

public interface ProfileService {
    ProfileResponse getProfile();

    ProfileResponse editProfile(ProfileDTO profileDTO);

    void updatePassword(PasswordDTO passwordDTO);
}
