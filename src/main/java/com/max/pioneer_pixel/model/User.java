package com.max.pioneer_pixel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "\"user\"") // Экранирование имени таблицы двойными кавычками
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String name;

    private LocalDate dateOfBirth;

    @Column(length = 500)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    // Здесь можно добавить связи к email и phone, если нужно
}
