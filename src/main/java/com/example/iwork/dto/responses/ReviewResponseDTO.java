package com.example.iwork.dto.responses;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;

    private Long companyId;

    private String companyName;

    private String title;

    private String body;

    private String pros;

    private String cons;

    private String advice;

    private Double rating;

    private Double careerOpportunities;

    private Double workLifeBalance;

    private Double compensation;

    private Double jobSecurity;

    private Double management;

    private String position;

    private String employmentStatus;

    private String employmentType;

    private Boolean recommendToFriend;

    private Boolean anonymous;

    private Integer helpfulCount;

    private Integer notHelpfulCount;

    private Integer commentsCount;

    private String author;

    private String approvalStatus;

    private Boolean hasVerification;

    private String contractDocumentUrl;
}