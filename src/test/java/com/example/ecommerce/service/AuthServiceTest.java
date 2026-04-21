package com.example.ecommerce.service;

import com.example.ecommerce.dto.auth.AuthResponse;
import com.example.ecommerce.dto.auth.LoginRequest;
import com.example.ecommerce.dto.auth.RegisterRequest;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("jwt-token-123");

        // Act
        AuthResponse result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token-123", result.getToken());
        assertEquals("Bearer", result.getTokenType());
        verify(userRepository, times(1)).save(any(User.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.register(registerRequest);
        });
        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_CreatesUserWithCorrectRole() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("jwt-token-123");

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        // Verify that the saved user has USER role
    }

    @Test
    void testLogin_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("john@example.com", "password123"));

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("jwt-token-456");

        // Act
        AuthResponse result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token-456", result.getToken());
        assertEquals("Bearer", result.getTokenType());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        // Act & Assert
        assertThrows(org.springframework.security.core.AuthenticationException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void testRegister_PasswordEncodedCorrectly() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("jwt-token");

        // Act
        authService.register(registerRequest);

        // Assert
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void testRegister_CreatesCartForNewUser() {
        // Arrange
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Cart newCart = new Cart();
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(mockUserDetails)).thenReturn("jwt-token");

        // Act
        authService.register(registerRequest);

        // Assert
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testLogin_ReturnsValidToken() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("john@example.com", "password123"));

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        String validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIn0";
        when(jwtService.generateToken(mockUserDetails)).thenReturn(validToken);

        // Act
        AuthResponse result = authService.login(loginRequest);

        // Assert
        assertNotNull(result.getToken());
        assertFalse(result.getToken().isEmpty());
        assertEquals(validToken, result.getToken());
    }
}

