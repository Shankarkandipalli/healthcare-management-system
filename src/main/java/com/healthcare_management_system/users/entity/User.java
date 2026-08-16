package com.healthcare_management_system.users.entity;

import com.healthcare_management_system.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true)
    private String password;
    private String profilePicture;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    @Column(unique = true, nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
    @NotBlank(message = "Phone number is required")
    @Column(unique = true, nullable = false)
    private String phoneNumber;


}
