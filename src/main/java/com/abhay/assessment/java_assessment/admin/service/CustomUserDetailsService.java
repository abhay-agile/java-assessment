package com.abhay.assessment.java_assessment.admin.service;

import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.repository.AdminRepository;
import com.abhay.assessment.java_assessment.admin.repository.StudentRepository;
import com.abhay.assessment.java_assessment.admin.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<? extends User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with this email.");
        }

        User foundUser = user.get();

        return new org.springframework.security.core.userdetails.User(
                foundUser.getEmail(),
                foundUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + foundUser.getRole().name()))
        );
    }
}
