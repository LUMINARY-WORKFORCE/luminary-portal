package com.luminary.portal.service;

import com.luminary.portal.dto.AuthResponse;
import com.luminary.portal.dto.LoginRequest;
import com.luminary.portal.dto.RegisterRequest;
import com.luminary.portal.entity.User;
import com.luminary.portal.entity.enums.Role;
import com.luminary.portal.exception.ResourceNotFoundException;
import com.luminary.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.JOB_SEEKER)
                .build();

        userRepo.save(user);

        return AuthResponse.builder()
                .token(getToken(user))
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .name(user.getName())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }


        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return AuthResponse.builder()
                .token(getToken(user))
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .name(user.getName())
                .build();
    }

    private String getToken(User user) {
        return jwtService.generateToken(Map.of("role", user.getRole(),
                "name", user.getName()), user.getEmail());
    }
}
