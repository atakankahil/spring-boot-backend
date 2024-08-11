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
                book.setQuantity(getBookQuantity(book.getTitle(), book.getAuthor(), book.getYear()));
                bookList.add(book);
            }
        }
        return bookList;
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
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

    private List<Book> updateBooks(Book bookDetails, List<Book> booksToUpdate) throws Exception {
        List<Book> updatedBooks = new ArrayList<>();
        for (Book book : booksToUpdate) {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setGenre(bookDetails.getGenre());
            book.setYear(bookDetails.getYear());
            book.setDescription(bookDetails.getDescription());
            book.setPrice(bookDetails.getPrice());
            book.setQuantity(bookDetails.getQuantity());
            book.setBase64QrCode(createQrCode(book));
            updatedBooks.add(book);
        }
        return bookRepository.saveAll(updatedBooks);
    }

    private Integer getBookQuantity(String title, String author, Integer year) {
        return bookRepository.findByTitleAuthorYear(title, author, year).size();
    }
}
