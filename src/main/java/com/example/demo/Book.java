package com.example.demo;

import jakarta.persistence.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;

import java.util.Objects;

@Entity
public class Book implements Cloneable {
    @Id
    private String id;
    private String title;
    private String author;
    private Integer year;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String genre;
    private Double price;
    @Column(nullable = false)
    private boolean isRented = false;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String base64QrCode;

    @Transient
    private Integer Quantity;

    // Constructors
    public Book() {
    }

    public Book(String id, String title, String author, Integer year, String description, String genre, Double price, Boolean isRented, String base64QrCode) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.description = description;
        this.genre = genre;
        this.price = price;
        this.isRented = isRented;
        this.base64QrCode = base64QrCode;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public Boolean getRented() {
        return isRented;
    }

    public void setRented(Boolean rented) {
        isRented = rented;
    }

    public String getBase64QrCode() {
        return base64QrCode;
    }

    public void setBase64QrCode(String base64QrCode) {
        this.base64QrCode = base64QrCode;
    }

    public Integer getQuantity() {
        return Quantity;
    }

    public void setQuantity(Integer quantity) {
        Quantity = quantity;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", genre='" + genre + '\'' +
                '}';
    }

    @Override
    public Book clone() {
        try {
            Book clone = (Book) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        if (!this.title.equals(book.getTitle())) {
            return false;
        }
        if (!this.author.equals(book.getAuthor())) {
            return false;
        }
        if (!this.year.equals(book.getYear())) {
            return false;
        }
        if (!this.description.equals(book.getDescription())) {
            return false;
        }
        if (!this.genre.equals(book.getGenre())) {
            return false;
        }
        if (!this.price.equals(book.getPrice())) {
            return false;
        }
        return true;
    }
}