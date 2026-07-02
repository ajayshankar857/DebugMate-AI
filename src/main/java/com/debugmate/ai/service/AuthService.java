package com.debugmate.ai.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtResponseDto login(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Update last login date / daily streak in profile
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null && user.getProfile() != null) {
            UserProfile profile = user.getProfile();
            LocalDate today = LocalDate.now();
            if (profile.getLastLoginDate() != null) {
                if (profile.getLastLoginDate().plusDays(1).equals(today)) {
                    profile.setDailyStreak(profile.getDailyStreak() + 1);
                } else if (!profile.getLastLoginDate().equals(today)) {
                    profile.setDailyStreak(1); // reset streak if missed a day, or set to 1 if it was 0
                }
            } else {
                profile.setDailyStreak(1);
            }
            profile.setLastLoginDate(today);
        }

        return new JwtResponseDto(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        // Create user profile
        UserProfile profile = UserProfile.builder()
                .user(user)
                .xpPoints(0)
                .dailyStreak(0)
                .totalErrorsSolved(0)
                .accuracyScore(100)
                .build();
        user.setProfile(profile);

        userRepository.save(user);
    }
}
