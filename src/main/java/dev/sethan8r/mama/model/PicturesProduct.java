package dev.sethan8r.mama.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pictures_product")
public class PicturesProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image", nullable = false, columnDefinition = "bytea")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
