package com.example.iwork.services;

import com.example.iwork.dto.responses.LocationDTO;
import com.example.iwork.entities.Location;

import java.util.List;

public interface LocationService {
    List<LocationDTO> getAllLocations();
    LocationDTO getLocationById(Long id);
    Location getOrCreateLocation(String locationValue);
    List<LocationDTO> searchLocations(String search);
}