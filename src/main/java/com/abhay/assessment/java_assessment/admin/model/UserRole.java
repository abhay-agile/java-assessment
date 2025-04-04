package com.abhay.assessment.java_assessment.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum UserRole {
    ADMIN(Admin.class),
    STUDENT(Student.class);

    private final Class<? extends User> userClass;

    UserRole(Class<? extends User> userClass) {
        this.userClass = userClass;
    }

    public User createInstance() {
        try {
            return userClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user instance", e);
        }
    }
}
