package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(Authentication authentication) {
        String username = authentication.getName();
        System.out.println("User " + username + " is fetching all books.");
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("User " + username + " is attempting to add a book.");
        try {
            return ResponseEntity.ok(bookService.saveBook(book));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Return 400 if an exception occurs
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("User " + username + " is fetching book with id: " + id);
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Return 404 if not found
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book bookDetails, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("User " + username + " is attempting to update book with id: " + id);
        try {
            return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Return 400 if an exception occurs
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("User " + username + " is attempting to delete book with id: " + id);
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Return 400 if an exception occurs
        }
    }
}
