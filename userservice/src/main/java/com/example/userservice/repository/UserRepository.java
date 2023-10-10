package com.example.userservice.repository;

import com.example.userservice.model.APPUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<APPUser, Integer> {
    Optional<APPUser> findByEmail(String email);
}
