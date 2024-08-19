package com.example.demo;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public BookController(BookService bookService, UserRepository userRepository) {
        this.bookService = bookService;
        this.userRepository = userRepository;
        this.userDetailsService = new UserDetailsServiceImpl(userRepository);
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
            boolean hasPermission = userDetailsService.hasPermission(username, Collections.singletonList("ADMIN"));
            if (hasPermission) {
                return ResponseEntity.ok(bookService.saveBook(book));
            } else {
                throw new BadRequestException("User not allowed to add book");
            }
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
            boolean hasPermission = userDetailsService.hasPermission(username, Collections.singletonList("ADMIN"));
            if (hasPermission) {
                return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
            } else {
                throw new BadRequestException("User not allowed to update book");
            }
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
            boolean hasPermission = userDetailsService.hasPermission(username, Collections.singletonList("ADMIN"));
            if (hasPermission) {
                bookService.deleteBook(id);
                return ResponseEntity.noContent().build();
            } else {
                throw new BadRequestException("User not allowed to delete book");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Return 400 if an exception occurs
        }
    }

    @PutMapping("/rent/{id}")
    public ResponseEntity<Book> rentBook(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("User " + username + " is attempting to rent book with id: " + id);
        try {
            boolean hasPermission = userDetailsService.hasPermission(username, Arrays.asList("USER", "ADMIN"));
            if (hasPermission) {
                return ResponseEntity.ok(bookService.rentBook(id));
            } else {
                throw new BadRequestException("User not allowed to rent book");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return 400 if an exception occurs
        }
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<Book> returnBook(@PathVariable String id, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("User " + username + " is attempting to return book with id: " + id);
        try {
            boolean hasPermission = userDetailsService.hasPermission(username, Arrays.asList("USER", "ADMIN"));
            if (hasPermission) {
                return ResponseEntity.ok(bookService.returnBook(id));
            } else {
                throw new BadRequestException("User not allowed to return book");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Return 400 if an exception occurs
        }
    }

    @GetMapping("/rented")
    public ResponseEntity<List<Book>> getRentedBooks(Authentication authentication) throws Exception {
        String username = authentication.getName();
        System.out.println("User " + username + " is fetching all rented books.");
        boolean hasPermission = userDetailsService.hasPermission(username, Arrays.asList("USER", "ADMIN"));
        if (hasPermission) {
            List<Book> rentedBooks = bookService.getAllRentedBooks();
            return ResponseEntity.ok(rentedBooks);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
