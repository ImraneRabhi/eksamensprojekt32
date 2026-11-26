package com.example.examensprojekt32.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import com.example.examensprojekt32.service.TreatmentData;


import java.math.BigDecimal;

@Entity
public class Treatment implements TreatmentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int durationMinutes;

    public Treatment(){
    }

    public Treatment(String name, String description, BigDecimal price, int durationMinutes){
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}

