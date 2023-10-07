package com.example.userservice.repository;

import com.example.userservice.model.APPUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<APPUser, Long> {
}
