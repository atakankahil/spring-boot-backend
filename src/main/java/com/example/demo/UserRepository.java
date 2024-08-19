package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Override
    List<User> findAll();

    @Override
    Optional<User> findById(Long aLong);
}
