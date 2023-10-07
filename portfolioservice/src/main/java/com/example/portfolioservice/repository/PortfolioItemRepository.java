package com.example.portfolioservice.repository;

import com.example.portfolioservice.model.PortfolioItem;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Integer> {
    Optional<PortfolioItem> findByUserId(Integer userId);
}
