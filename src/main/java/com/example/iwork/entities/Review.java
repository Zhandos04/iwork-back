package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;


    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String pros;

    @Column(columnDefinition = "TEXT")
    private String cons;

    @Column(columnDefinition = "TEXT")
    private String advice;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "career_opportunities")
    private Double careerOpportunities;

    @Column(name = "work_life_balance")
    private Double workLifeBalance;

    @Column(name = "compensation")
    private Double compensation;

    @Column(name = "job_security")
    private Double jobSecurity;

    @Column(name = "management")
    private Double management;

    @Column
    private String position;

    @Column(name = "employment_status")
    private String employmentStatus; // current, former

    @Column(name = "employment_type")
    private String employmentType; // full-time, part-time, contract, etc.

    @Column(name = "recommend_to_friend")
    private Boolean recommendToFriend;

    @Column
    private Boolean anonymous;

    @Column(name = "helpful_count")
    private Integer helpfulCount;

    @Column(name = "not_helpful_count")
    private Integer notHelpfulCount;

    @Column(name = "comments_count")
    private Integer commentsCount;

    @Column
    private String author; // Displayed name, depends on anonymous flag

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "contract_document_url")
    private String contractDocumentUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "has_admin_comment")
    private Boolean hasAdminComment;

    @Column
    private String date;

    @Column(name = "ai_analysis", columnDefinition = "TEXT")
    private String aiAnalysis;
}