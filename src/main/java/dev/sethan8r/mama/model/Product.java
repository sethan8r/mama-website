package dev.sethan8r.mama.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    private String price;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> characteristics;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<PicturesProduct> picturesProduct = new ArrayList<>(); //нужно будет сортировать картинки

    @Column(name = "available", nullable = false)
    private boolean available;

    @Column(name = "deleted",  nullable = false)
    private boolean deleted;

    @Column(unique = true, nullable = false)
    private String slug; //для кастомной ссылки
}
