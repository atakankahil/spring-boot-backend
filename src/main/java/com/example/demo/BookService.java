package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        List<Book> copiesList = bookRepository.getAllNonRentedCopies();
        List<Book> bookList = new ArrayList<>();
        for (Book book : copiesList) {
            if(bookList.stream().noneMatch((b-> b.equals(book)))){
                bookList.add(book);
            }
        }
        return bookList;
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> saveBooks(List<Book> books){
        return bookRepository.saveAll(books);
    }
    public Book updateBook(String id, Book bookDetails) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setGenre(bookDetails.getGenre());
            book.setYear(bookDetails.getYear());
            book.setDescription(bookDetails.getDescription());
            book.setPrice(bookDetails.getPrice());
            return bookRepository.save(book);
        }).orElseGet(() -> {
            bookDetails.setId(id);
            return bookRepository.save(bookDetails);
        });
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
}
