package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {
    @Query(value = "select * from book where is_rented = 0;", nativeQuery = true)
    List<Book> getAllNonRentedCopies();
}
