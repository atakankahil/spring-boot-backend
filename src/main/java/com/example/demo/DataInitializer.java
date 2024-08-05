package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BookService bookService;

    @Autowired
    public DataInitializer(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void run(String... args) throws Exception {
//        bookService.saveBook(new Book("The Catcher in the Rye", "J.D. Salinger"));
//        bookService.saveBook(new Book("To Kill a Mockingbird", "Harper Lee"));
//        bookService.saveBook(new Book("1984", "George Orwell"));
    }
}
