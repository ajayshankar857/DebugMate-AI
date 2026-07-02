package com.debugmate.ai;

import com.debugmate.ai.dto.JwtResponseDto;
import com.debugmate.ai.dto.LoginRequestDto;
import com.debugmate.ai.dto.UserRegistrationDto;
import com.debugmate.ai.entity.ERole;
import com.debugmate.ai.entity.Role;
import com.debugmate.ai.entity.User;
import com.debugmate.ai.entity.UserProfile;
import com.debugmate.ai.exception.BadRequestException;
import com.debugmate.ai.repository.RoleRepository;
import com.debugmate.ai.repository.UserRepository;
import com.debugmate.ai.security.JwtTokenProvider;
import com.debugmate.ai.security.UserDetailsImpl;
import com.debugmate.ai.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private UserRegistrationDto regDto;
    private LoginRequestDto loginDto;
    private Role userRole;

    @BeforeEach
    void setUp() {
        regDto = new UserRegistrationDto();
        regDto.setUsername("testuser");
        regDto.setEmail("test@example.com");
        regDto.setPassword("password");

        loginDto = new LoginRequestDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        userRole = new Role(1, ERole.ROLE_USER);
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(regDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(regDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(regDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        assertDoesNotThrow(() -> authService.register(regDto));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateUsername_ThrowsBadRequest() {
        when(userRepository.existsByUsername(regDto.getUsername())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(regDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsBadRequest() {
        when(userRepository.existsByUsername(regDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(regDto.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(regDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "testuser", "test@example.com", "encodedPassword",
                Collections.emptyList()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateJwtToken(authentication)).thenReturn("jwtTokenString");
        
        User mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .profile(UserProfile.builder().xpPoints(0).dailyStreak(0).build())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        JwtResponseDto result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("jwtTokenString", result.getToken());
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getId());
    }
}
