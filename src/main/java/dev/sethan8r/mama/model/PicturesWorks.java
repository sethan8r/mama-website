package dev.sethan8r.mama.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pictures_works")
public class PicturesWorks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "image", nullable = false, columnDefinition = "bytea")
    private byte[] image;
}