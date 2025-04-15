package com.example.iwork.services.impl;

import com.example.iwork.dto.responses.JobDTO;
import com.example.iwork.entities.Job;
import com.example.iwork.exceptions.JobNotFoundException;
import com.example.iwork.repositories.JobRepository;
import com.example.iwork.services.JobService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<JobDTO> getAllJobs(String category, String search) {
        List<Job> jobs;

        if (StringUtils.hasText(category)) {
            // Фильтрация по категории
            jobs = jobRepository.findByCategory(category);
        } else if (StringUtils.hasText(search)) {
            // Поиск по названию
            jobs = jobRepository.findByTitleContainingIgnoreCase(search);
        } else {
            // Получение всех должностей
            jobs = jobRepository.findAll();
        }

        // Преобразование в DTO
        return jobs.stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Должность с ID " + id + " не найдена"));

        return modelMapper.map(job, JobDTO.class);
    }
}