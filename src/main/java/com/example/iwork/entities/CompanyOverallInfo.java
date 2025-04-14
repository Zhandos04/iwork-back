package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
public class CompanyOverallInfo {
    @Column
    private String founded;

    @Column
    private String revenue;

    @Column
    private String mission;
}