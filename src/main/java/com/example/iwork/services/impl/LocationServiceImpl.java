package com.example.iwork.services.impl;

import com.example.iwork.dto.responses.LocationDTO;
import com.example.iwork.entities.Location;
import com.example.iwork.repositories.LocationRepository;
import com.example.iwork.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<LocationDTO> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
                .map(location -> modelMapper.map(location, LocationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Локация не найдена с ID: " + id));
        return modelMapper.map(location, LocationDTO.class);
    }

    @Override
    @Transactional
    public Location getOrCreateLocation(String locationValue) {
        if (locationValue == null || locationValue.trim().isEmpty()) {
            return null;
        }

        Optional<Location> existingLocation = locationRepository.findByLocationValue(locationValue.trim());

        if (existingLocation.isPresent()) {
            return existingLocation.get();
        } else {
            Location newLocation = new Location();
            newLocation.setLocationValue(locationValue.trim());
            return locationRepository.save(newLocation);
        }
    }

    @Override
    public List<LocationDTO> searchLocations(String search) {
        List<Location> locations;

        if (StringUtils.hasText(search)) {
            // Поиск по строке поиска
            locations = locationRepository.findByLocationValueContainingIgnoreCase(search.trim());
        } else {
            // Если строка поиска не указана, возвращаем все локации
            locations = locationRepository.findAll();
        }

        return locations.stream()
                .map(location -> modelMapper.map(location, LocationDTO.class))
                .collect(Collectors.toList());
    }
}