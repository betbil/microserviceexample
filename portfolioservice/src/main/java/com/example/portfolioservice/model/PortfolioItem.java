package com.example.portfolioservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//TODO: Add indexes for userId and stockId
public class PortfolioItem {
    @Id
    @GeneratedValue
    private Integer id;
    public Integer userId;
    public Integer stockId;
}