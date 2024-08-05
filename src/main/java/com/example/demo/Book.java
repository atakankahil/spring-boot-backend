package com.example.demo;

import jakarta.persistence.*;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String author;
    private Integer year;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String  genre;
    private Double price;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String base64QrCode;

    // Constructors
    public Book() {
    }

    public Book(int id, String title, String author, Integer year, String description, String genre, Double price, String base64QrCode) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.description = description;
        this.genre = genre;
        this.price = price;
        this.base64QrCode = base64QrCode;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getBase64QrCode() {
        return base64QrCode;
    }

    public void setBase64QrCode(String base64QrCode) {
        this.base64QrCode = base64QrCode;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                ", price=" + price +
                '}';
    }
}
