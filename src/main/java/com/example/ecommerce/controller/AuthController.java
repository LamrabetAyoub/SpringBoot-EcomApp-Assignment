package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.AuthRequest;
import com.example.ecommerce.security.AuthResponse;
import com.example.ecommerce.security.JwtService;
import com.example.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public AuthResponse authenticateUser(@RequestBody AuthRequest authRequest) {
        // Authenticate the user using AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        // Set the authentication context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Extract the UserDetails (principal) from the authentication object
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate the JWT token
        String jwtToken = jwtService.generateToken(userDetails);

        // Return the token in the response
        return new AuthResponse(jwtToken);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(400).body("Username already exists");
        }

        userService.createUser(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
