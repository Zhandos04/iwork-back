package com.example.iwork.services;

import com.example.iwork.dto.responses.JobDTO;

import java.util.List;

public interface JobService {
    List<JobDTO> getAllJobs(String category, String search);
    JobDTO getJobById(Long id);
}