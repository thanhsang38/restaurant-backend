package com.tranthanhsang.example304.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    // Constructor không tham số
    public Role() {
    }

    // Constructor có tham số
    public Role(ERole name) {
        this.name = name;
    }

    // Getter cho id
    public Integer getId() {
        return id;
    }

    // Setter cho id
    public void setId(Integer id) {
        this.id = id;
    }

    // Getter cho name
    public ERole getName() {
        return name;
    }

    // Setter cho name
    public void setName(ERole name) {
        this.name = name;
    }
}
