package com.example.iwork.services;

import com.example.iwork.dto.requests.CreateSalaryDTO;
import com.example.iwork.dto.responses.SalaryResponseDTO;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SalaryService {
    SalaryResponseDTO addSalary(CreateSalaryDTO createSalaryDTO, MultipartFile contractFile) throws FileUploadException;
    List<SalaryResponseDTO> getMySalaries(String status);
    void deleteSalary(Long id) throws FileUploadException;
    SalaryResponseDTO updateSalary(Long id, CreateSalaryDTO updateSalaryDTO, MultipartFile contractFile) throws FileUploadException;

}
