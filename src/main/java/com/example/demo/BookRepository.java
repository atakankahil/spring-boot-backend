package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    @Query(value = "select * from book where is_rented = 0;", nativeQuery = true)
    List<Book> getAllNonRentedCopies();

    @Query(value = "select * from book where title = :title and author = :author and year = :year", nativeQuery = true)
    List<Book> findByTitleAuthorYear(@Param("title") String title, @Param("author") String author, @Param("year") Integer year);

    @Query(value = "select * from book where is_rented = 1;", nativeQuery = true)
    List<Book> getAllRentedCopies();
}
