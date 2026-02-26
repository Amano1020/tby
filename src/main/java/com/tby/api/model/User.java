package com.tby.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;
}
