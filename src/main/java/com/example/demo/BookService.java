package com.example.demo;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
            if (bookList.stream().noneMatch(b -> b.equals(book))) {
                book.setQuantity(getNoneRentedBooks(book.getTitle(), book.getAuthor(), book.getYear()));
                bookList.add(book);
            }
        }
        return bookList;
    }

    public Optional<Book> getBookById(String id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setQuantity(getNoneRentedBooks(book.getTitle(), book.getAuthor(), book.getYear()));
            return Optional.of(book);
        }
        return Optional.empty();
    }

    public List<Book> getAllRentedBooks() {
        return bookRepository.findAll().stream()
                .filter(Book::getRented)
                .collect(Collectors.toList());
    }

    public Book saveBook(Book book) throws Exception {
        List<Book> existingBooks = bookRepository.findByTitleAuthorYear(book.getTitle(), book.getAuthor(), book.getYear());
        if (existingBooks != null && !existingBooks.isEmpty()) {
            throw new Exception("Book already exists, update the quantity only");
        }
        List<Book> bookList = new ArrayList<>();
        for (int i = 0; i < book.getQuantity(); i++) {
            Book clone = book.clone();
            clone.setId(UUID.randomUUID().toString());
            clone.setBase64QrCode(createQrCode(clone));
            bookList.add(clone);
        }
        return saveBooks(bookList).get(0);
    }

    private String createQrCode(Book clone) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(clone.toString(), BarcodeFormat.QR_CODE, 250, 250);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

        byte[] qrCodeImage = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(qrCodeImage);
    }

    public List<Book> saveBooks(List<Book> books) {
        return bookRepository.saveAll(books);
    }

    public Book updateBook(String id, Book bookDetails) throws Exception {
        Book book = bookRepository.findById(id).orElseThrow(() -> new Exception("Book not found"));
        List<Book> booksToUpdate = bookRepository.findByTitleAuthorYear(book.getTitle(), book.getAuthor(), bookDetails.getYear());

        if (booksToUpdate.isEmpty()) {
            throw new Exception("No books found to update with the given title, author, and year.");
        }

        return updateBooks(bookDetails, booksToUpdate).get(0);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    public Book rentBook(String id) throws Exception {
        Book book = bookRepository.findById(id).orElseThrow(() -> new Exception("Book not found"));
        if (book.getRented()) {
            throw new Exception("Book is already rented");
        }
        book.setRented(true);
        bookRepository.save(book);
        return getBookById(id).orElse(null);
    }

    public Book returnBook(String id) throws Exception {
        Book book = bookRepository.findById(id).orElseThrow(() -> new Exception("Book not found"));
        if (!book.getRented()) {
            throw new Exception("Book is not rented");
        }
        book.setRented(false);
        bookRepository.save(book);
        return getBookById(id).orElse(null);
    }

    private List<Book> updateBooks(Book bookDetails, List<Book> booksToUpdate) throws Exception {
        List<Book> updatedBooks = new ArrayList<>();
        for (Book book : booksToUpdate) {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setGenre(bookDetails.getGenre());
            book.setYear(bookDetails.getYear());
            book.setDescription(bookDetails.getDescription());
            book.setPrice(bookDetails.getPrice());
            book.setSection(bookDetails.getSection()); // Set the section
            book.setShelf(bookDetails.getShelf());     // Set the shelf
            book.setQuantity(bookDetails.getQuantity());
            book.setBase64QrCode(createQrCode(book));
            updatedBooks.add(book);
        }
        return bookRepository.saveAll(updatedBooks);
    }

    private Integer getNoneRentedBooks(String title, String author, Integer year) {
        List<Book> nonRentedBooks = bookRepository.findByTitleAuthorYear(title, author, year);
        int count = 0;
        for (Book book : nonRentedBooks) {
            if (!book.getRented()) {
                count++;
            }
        }
        return count;
    }
}
