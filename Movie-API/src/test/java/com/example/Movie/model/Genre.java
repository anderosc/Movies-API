package com.example.Movie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Genre {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Add the 'id' field here

    // Optional: Add a 'name' field or other properties for your Genre entity
    private String name;

    // Getters and Setters for id and name
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
