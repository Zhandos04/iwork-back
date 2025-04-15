package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.CreateSalaryDTO;
import com.example.iwork.dto.requests.UpdateSalaryStatusDTO;
import com.example.iwork.dto.responses.SalaryResponseDTO;
import com.example.iwork.entities.*;
import com.example.iwork.exceptions.CompanyNotFoundException;
import com.example.iwork.exceptions.JobNotFoundException;
import com.example.iwork.exceptions.SalaryNotFoundException;
import com.example.iwork.repositories.CompanyRepository;
import com.example.iwork.repositories.JobRepository;
import com.example.iwork.repositories.SalaryRepository;
import com.example.iwork.services.SalaryService;
import com.example.iwork.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;
    private final JobRepository jobRepository;

    private String validateAndUploadContractFile(MultipartFile file) throws FileUploadException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Проверка размера файла
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new FileUploadException("Файл слишком большой. Максимальный размер — 10MB.");
        }

        // Проверка типа файла
        String contentType = file.getContentType();
        if (contentType != null &&
                !(contentType.equals("application/pdf") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new FileUploadException("Недопустимый тип файла. Разрешены только PDF и DOCX документы.");
        }

        try {
            return s3Service.uploadFile(file);
        } catch (IOException e) {
            throw new FileUploadException("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public SalaryResponseDTO addSalary(CreateSalaryDTO createSalaryDTO, MultipartFile contractFile) throws FileUploadException {
        // Обработка файла договора, если он предоставлен
        String contractFileUrl = validateAndUploadContractFile(contractFile);

        // Находим компанию
        Company company = companyRepository.findById(createSalaryDTO.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Компания не найдена"));

        // Получаем текущего пользователя
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        Job job = jobRepository.findById(createSalaryDTO.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Должность с ID " + createSalaryDTO.getJobId() + " не найдена"));

        // Создаем объект зарплаты
        Salary salary = modelMapper.map(createSalaryDTO, Salary.class);
        if (contractFileUrl != null) {
            salary.setContractDocumentUrl(contractFileUrl);
        }
        salary.setCompany(company);
        salary.setUser(user);
        salary.setJob(job); // Устанавливаем должность
        salary.setApprovalStatus(ApprovalStatus.PENDING);
        salary.setCreatedAt(LocalDateTime.now());

        // Форматируем дату
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("LLLL yyyy", new Locale("ru"));
        String formattedDate = formatter.format(salary.getCreatedAt());
        salary.setDate(formattedDate.substring(0, 1).toUpperCase() +
                formattedDate.substring(1));

        // Сохраняем запись о зарплате
        Salary savedSalary = salaryRepository.save(salary);

        // Преобразуем в DTO для ответа
        SalaryResponseDTO responseDTO = modelMapper.map(savedSalary, SalaryResponseDTO.class);
        responseDTO.setCompanyName(company.getName());
        responseDTO.setCompanyId(company.getId());

        // Форматируем сумму с символом валюты
        String currencySymbol = getCurrencySymbol(savedSalary.getCurrency());
        responseDTO.setFormattedAmount(currencySymbol + savedSalary.getSalary() +
                " " + (savedSalary.getPayPeriod().equals("monthly") ? "в месяц" : "в год"));

        // Проверяем наличие подтверждающего документа
        responseDTO.setHasVerification(savedSalary.getContractDocumentUrl() != null &&
                !savedSalary.getContractDocumentUrl().isEmpty());

        // Устанавливаем статус одобрения
        responseDTO.setApprovalStatus("PENDING");

        return responseDTO;
    }

    @Override
    public List<SalaryResponseDTO> getMySalaries(String status) {
        // Получаем текущего пользователя
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        List<Salary> salaries;

        // Фильтрация по статусу, если указан
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("Все")) {
            ApprovalStatus approvalStatus = mapStatusToApprovalStatus(status);
            salaries = salaryRepository.findByUserAndApprovalStatus(user, approvalStatus);
        } else {
            salaries = salaryRepository.findByUser(user);
        }

        // Преобразуем список в DTO
        return salaries.stream()
                .map(this::convertToSalaryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSalary(Long id) throws FileUploadException {
        // Находим запись о зарплате
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new SalaryNotFoundException("Запись о зарплате не найдена с ID: " + id));

        // Получаем текущего пользователя
        User currentUser = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Проверка прав на удаление
        if (!salary.getUser().getId().equals(currentUser.getId()) && !userService.isAdmin()) {
            throw new AccessDeniedException("У вас нет прав на удаление этой записи о зарплате");
        }

        // Если есть файл договора, удаляем его из хранилища
        if (salary.getContractDocumentUrl() != null && !salary.getContractDocumentUrl().isEmpty()) {
            try {
                s3Service.deleteFromS3(salary.getContractDocumentUrl());
            } catch (Exception e) {
                throw new FileUploadException("Ошибка при удаление файла: " + e.getMessage());
            }
        }

        // Удаляем запись о зарплате
        salaryRepository.delete(salary);
    }

    @Override
    @Transactional
    public SalaryResponseDTO updateSalary(Long id, CreateSalaryDTO updateSalaryDTO, MultipartFile contractFile) throws FileUploadException {
        // Находим существующую запись о зарплате
        Salary existingSalary = salaryRepository.findById(id)
                .orElseThrow(() -> new SalaryNotFoundException("Запись о зарплате не найдена с ID: " + id));

        // Получаем текущего пользователя
        User currentUser = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Проверяем права на редактирование
        if (!existingSalary.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("У вас нет прав на редактирование этой записи о зарплате");
        }

        // Обработка файла договора, если он предоставлен
        if (contractFile != null && !contractFile.isEmpty()) {
            String contractFileUrl = validateAndUploadContractFile(contractFile);

            // Если был загружен новый файл договора и существует старый, удаляем его
            if (existingSalary.getContractDocumentUrl() != null &&
                    !existingSalary.getContractDocumentUrl().isEmpty()) {
                try {
                    s3Service.deleteFromS3(existingSalary.getContractDocumentUrl());
                } catch (Exception e) {
                    throw new FileUploadException("Ошибка при удаление файла: " + e.getMessage());
                }
            }
            existingSalary.setContractDocumentUrl(contractFileUrl);
        }

        // Проверяем, изменилась ли компания
        if (!existingSalary.getCompany().getId().equals(updateSalaryDTO.getCompanyId())) {
            Company company = companyRepository.findById(updateSalaryDTO.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException("Компания не найдена"));
            existingSalary.setCompany(company);
        }

        // Обновляем поля записи о зарплате
        existingSalary.setPosition(updateSalaryDTO.getPosition());
        existingSalary.setDepartment(updateSalaryDTO.getDepartment());
        existingSalary.setEmploymentStatus(updateSalaryDTO.getEmploymentStatus());
        existingSalary.setEmploymentType(updateSalaryDTO.getEmploymentType());
        existingSalary.setSalary(updateSalaryDTO.getSalary());
        existingSalary.setCurrency(updateSalaryDTO.getCurrency());
        existingSalary.setPayPeriod(updateSalaryDTO.getPayPeriod());
        existingSalary.setBonuses(updateSalaryDTO.getBonuses());
        existingSalary.setStockOptions(updateSalaryDTO.getStockOptions());
        existingSalary.setExperience(updateSalaryDTO.getExperience());
        existingSalary.setLocation(updateSalaryDTO.getLocation());
        existingSalary.setAnonymous(updateSalaryDTO.getAnonymous());

        // При редактировании запись снова должна быть проверена
        existingSalary.setApprovalStatus(ApprovalStatus.PENDING);

        // Обновляем время изменения
        existingSalary.setUpdatedAt(LocalDateTime.now());

        // Сохраняем обновленную запись
        Salary updatedSalary = salaryRepository.save(existingSalary);

        // Преобразуем в DTO для ответа
        return convertToSalaryResponseDTO(updatedSalary);
    }

    /**
     * Преобразует сущность Salary в DTO
     */
    private SalaryResponseDTO convertToSalaryResponseDTO(Salary salary) {
        SalaryResponseDTO dto = modelMapper.map(salary, SalaryResponseDTO.class);

        dto.setCompanyName(salary.getCompany().getName());
        dto.setCompanyId(salary.getCompany().getId());

        // Форматируем сумму с символом валюты
        String currencySymbol = getCurrencySymbol(salary.getCurrency());
        dto.setFormattedAmount(currencySymbol + salary.getSalary() +
                " " + (salary.getPayPeriod().equals("monthly") ? "в месяц" : "в год"));

        // Устанавливаем статус одобрения
        switch (salary.getApprovalStatus()) {
            case APPROVED:
                dto.setApprovalStatus("APPROVED");
                break;
            case REJECTED:
                dto.setApprovalStatus("REJECTED");
                break;
            case PENDING:
            default:
                dto.setApprovalStatus("PENDING");
                break;
        }

        // Проверяем наличие подтверждающего документа
        dto.setHasVerification(salary.getContractDocumentUrl() != null &&
                !salary.getContractDocumentUrl().isEmpty());

        return dto;
    }

    /**
     * Преобразует строковый статус в enum ApprovalStatus
     */
    private ApprovalStatus mapStatusToApprovalStatus(String status) {
        if (status == null) {
            return null;
        }

        return switch (status.toLowerCase()) {
            case "новые", "новый", "pending" -> ApprovalStatus.PENDING;
            case "одобренные", "одобрено", "approved" -> ApprovalStatus.APPROVED;
            case "отклоненные", "отказано", "rejected" -> ApprovalStatus.REJECTED;
            default -> throw new IllegalArgumentException("Неизвестный статус: " + status);
        };
    }

    /**
     * Возвращает символ валюты по её коду
     */
    private String getCurrencySymbol(String currency) {
        return switch (currency) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "KZT" -> "₸";
            case "RUB" -> "₽";
            default -> "";
        };
    }

    // Дополнительные методы для существующей имплементации SalaryServiceImpl

    @Override
    public List<SalaryResponseDTO> getAllSalaries(String status) {
        // Проверяем права администратора
        if (!userService.isAdmin()) {
            throw new AccessDeniedException("Доступ только для администраторов");
        }

        List<Salary> salaries;

        // Фильтрация по статусу, если указан
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("Все")) {
            ApprovalStatus approvalStatus = mapStatusToApprovalStatus(status);
            salaries = salaryRepository.findByApprovalStatus(approvalStatus);
        } else {
            salaries = salaryRepository.findAll();
        }

        // Преобразуем список в DTO
        return salaries.stream()
                .map(this::convertToSalaryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalaryResponseDTO updateSalaryStatus(Long id, UpdateSalaryStatusDTO updateStatusDTO) {
        // Проверяем права администратора
        if (!userService.isAdmin()) {
            throw new AccessDeniedException("Доступ только для администраторов");
        }

        // Находим запись о зарплате
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new SalaryNotFoundException("Запись о зарплате не найдена с ID: " + id));

        // Обновляем статус
        ApprovalStatus newStatus;
        try {
            newStatus = ApprovalStatus.valueOf(updateStatusDTO.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный статус: " + updateStatusDTO.getStatus());
        }

        salary.setApprovalStatus(newStatus);

        // Добавляем комментарий администратора, если он предоставлен
        if (updateStatusDTO.getAdminComment() != null && !updateStatusDTO.getAdminComment().trim().isEmpty()) {
            salary.setAdminComment(updateStatusDTO.getAdminComment());
            salary.setHasAdminComment(true);
        }

        // Обновляем время изменения
        salary.setUpdatedAt(LocalDateTime.now());

        // Сохраняем обновленную запись
        Salary updatedSalary = salaryRepository.save(salary);

        // Преобразуем в DTO для ответа
        return convertToSalaryResponseDTO(updatedSalary);
    }
}
