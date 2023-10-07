package com.example.userservice.repository;

import com.example.userservice.model.UserOrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserOrderRequestRepository extends JpaRepository<UserOrderRequest, UUID>{
}
