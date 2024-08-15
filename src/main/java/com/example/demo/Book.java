package com.example.demo;

import jakarta.persistence.*;

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

    private String section;
    private Integer shelf;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String base64QrCode;

    @Transient
    private Integer quantity;

    // Constructors
    public Book() {}

    public Book(String id, String title, String author, Integer year, String description, String genre, Double price, Boolean isRented, String base64QrCode, String section, Integer shelf) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.description = description;
        this.genre = genre;
        this.price = price;
        this.isRented = isRented;
        this.base64QrCode = base64QrCode;
        this.section = section;
        this.shelf = shelf;
    }

    // Getters and setters for the new fields
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getShelf() {
        return shelf;
    }

    public void setShelf(Integer shelf) {
        this.shelf = shelf;
    }

    // Other getters, setters, and methods...
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
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", genre='" + genre + '\'' +
                ", section='" + section + '\'' +  // Include section in toString
                ", shelf=" + shelf +               // Include shelf in toString
                '}';
    }

    @Override
    public Book clone() {
        try {
            return (Book) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        if (!Objects.equals(this.title, book.getTitle())) {
            return false;
        }
        if (!Objects.equals(this.author, book.getAuthor())) {
            return false;
        }
        if (!Objects.equals(this.year, book.getYear())) {
            return false;
        }
        if (!Objects.equals(this.description, book.getDescription())) {
            return false;
        }
        if (!Objects.equals(this.genre, book.getGenre())) {
            return false;
        }
        if (!Objects.equals(this.price, book.getPrice())) {
            return false;
        }
        if (!Objects.equals(this.section, book.getSection())) { // Handle null-safe comparison
            return false;
        }
        if (!Objects.equals(this.shelf, book.getShelf())) { // Handle null-safe comparison
            return false;
        }
        return true;
    }

}
