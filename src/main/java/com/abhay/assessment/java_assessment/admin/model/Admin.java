package com.abhay.assessment.java_assessment.admin.model;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@TypeAlias("admin")
public class Admin extends User {
    private boolean active;

    @Builder
    public Admin(
            String name,
            String email,
            String phoneNumber,
            String password,
            UserRole role,
            Instant createdAt,
            Instant updatedAt,
            boolean active
    ) {
        super(name, email, phoneNumber, password, role, createdAt, updatedAt);
        this.active = active;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
