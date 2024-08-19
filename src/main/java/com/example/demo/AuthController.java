package com.example.demo;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Invalid username or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setUserRole("REGULAR");
        userRepository.save(user);
        return user;
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody AuthRequest authRequest) {
        String refreshToken = authRequest.getRefreshToken();
        if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
            String username = jwtUtil.extractUsername(refreshToken, jwtUtil.getRefreshSecretKey());
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateToken(userDetails);
            return new AuthResponse(newAccessToken, refreshToken);
        } else {
            throw new RuntimeException("Refresh Token is invalid or expired");
        }
    }

    @GetMapping("/all-users")
    public List<User> getUsers(Authentication authentication) throws Exception {
        User loggedUser = userRepository.findByUsername(authentication.getName());
        boolean hasPermission = new UserDetailsServiceImpl(userRepository).hasPermission(loggedUser.getUsername(), Collections.singletonList("ADMIN"));
        if (hasPermission) {
            return userRepository.findAll();
        } else {
            throw new BadRequestException("User not allowed to see users");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all-users/{id}")
    public User getUserById(@PathVariable Long id, Authentication authentication) throws Exception {
        User loggedUser = userRepository.findByUsername(authentication.getName());
        boolean hasPermission = new UserDetailsServiceImpl(userRepository).hasPermission(loggedUser.getUsername(), Collections.singletonList("ADMIN"));
        if (hasPermission) {
            return userRepository.findById(id).orElseThrow(() -> new BadRequestException("User not found with id"));
        } else {
            throw new BadRequestException("User not allowed to modify another user");
        }
    }

    @PutMapping("/all-users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser, Authentication authentication) throws Exception {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        User loggedUser = userRepository.findByUsername(authentication.getName());
        boolean hasPermission = new UserDetailsServiceImpl(userRepository).hasPermission(loggedUser.getUsername(), Collections.singletonList("ADMIN"));

        if (!hasPermission) {
            throw new BadRequestException("User not allowed to modify another user");
        }

        // Update fields if provided in the request
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getUserRole() != null) {
            existingUser.setUserRole(updatedUser.getUserRole());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }
}
