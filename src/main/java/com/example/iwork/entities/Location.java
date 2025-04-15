package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"location_value"})
})
@Getter
@Setter
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_value", nullable = false)
    private String locationValue; // город, страна (например: "Москва, Россия")
}