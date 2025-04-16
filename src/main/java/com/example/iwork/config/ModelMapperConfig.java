package com.example.iwork.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.entities.Review;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Устанавливаем строгую стратегию сопоставления
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        // Создаем специальное правило маппинга для CreateReviewDTO -> Review
        modelMapper.addMappings(new PropertyMap<CreateReviewDTO, Review>() {
            @Override
            protected void configure() {
                // Явно игнорируем поля companyId и jobId при маппинге в Review
                // так как они должны устанавливаться через связи с сущностями, а не напрямую
                skip(destination.getId());
                skip(destination.getCompany());
                skip(destination.getJob());
            }
        });

        return modelMapper;
    }
}