package com.abhay.assessment.java_assessment.admin.service;

import com.abhay.assessment.java_assessment.admin.dto.LoginResponseDto;
import com.abhay.assessment.java_assessment.admin.dto.RequestRegisterDto;
import com.abhay.assessment.java_assessment.admin.model.Admin;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.model.UserRole;
import com.abhay.assessment.java_assessment.admin.repository.AdminRepository;
import com.abhay.assessment.java_assessment.admin.repository.StudentRepository;
import com.abhay.assessment.java_assessment.admin.repository.UserRepository;
import com.abhay.assessment.java_assessment.common.util.EmailService;
import com.abhay.assessment.java_assessment.common.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AuthService(
            AdminRepository adminRepository,
            StudentRepository studentRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User registerUser(RequestRegisterDto registerDto) {
        String encodedPassword = bCryptPasswordEncoder.encode(registerDto.getPassword());
        LocalDateTime now = LocalDateTime.now();

        UserRole userRole = registerDto.getRole();

        if (!userRole.equals(UserRole.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Admin is only created.");
        }

        boolean userExist = adminRepository.findByEmail(registerDto.getEmail()).isPresent();

        if (userExist) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin already exists.");
        }

        User newUser = registerDto.getRole().createInstance();

        newUser.setName(registerDto.getName());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setPhoneNumber(registerDto.getPhoneNumber());
        newUser.setRole(registerDto.getRole());
        newUser.setCreatedAt(registerDto.getCreatedAt());
        newUser.setUpdatedAt(registerDto.getUpdatedAt());

        if (newUser instanceof Admin admin) {
            admin.setActive(registerDto.isActive());
        }

        return adminRepository.save((Admin) newUser);
    }

    public LoginResponseDto login(String userId, String password) {
        Optional<? extends User> user = adminRepository.findByEmail(userId);

        if (user.isEmpty()) {
            user = studentRepository.findByEmail(userId);
        }

        if (user.isEmpty()) {
            user = studentRepository.findByEnrollmentNumber(userId);
        }

        if (user.isEmpty() || !bCryptPasswordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException("User id or password is invalid.");
        }

        User currentUser = user.get();
        String token = JwtUtil.generateToken(currentUser.getId(), currentUser.getEmail(), currentUser.getRole());

        return new LoginResponseDto(currentUser, token);
    }

    public String forgotPassword(String email) throws AccountNotFoundException {
        Optional<? extends User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new AccountNotFoundException("User not found.");
        }

        String resetPasswordToken = UUID.randomUUID().toString().replace("-", "");

        User currentUser = user.get();
        currentUser.setResetPasswordToken(resetPasswordToken);

        emailService.sendResetPasswordMail(currentUser.getEmail(), currentUser.getName(), resetPasswordToken);

        userRepository.save(currentUser);

        return resetPasswordToken;
    }

    public void resetPassword(String resetPasswordToken, String newPassword) throws AccountNotFoundException {
        Optional<? extends User> user = userRepository.findByResetPasswordToken(resetPasswordToken);

        if (user.isEmpty()) {
            throw new AccountNotFoundException("Reset password link is wrong or account not found");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);

        user.get().setPassword(encodedPassword);
        user.get().setResetPasswordToken("");

        userRepository.save(user.get());
    }
}
