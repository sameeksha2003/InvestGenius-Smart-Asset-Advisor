package com.smartassetadvisor.service;

import com.smartassetadvisor.dto.AuthRequest;
import com.smartassetadvisor.dto.AuthResponse;
import com.smartassetadvisor.dto.LoginRequest;
import com.smartassetadvisor.dto.LoginResponse;
import com.smartassetadvisor.model.User;
import com.smartassetadvisor.repository.UserRepository;
import com.smartassetadvisor.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    
    @Autowired
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, @Lazy AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(AuthRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAge(request.getAge());  
        user.setOccupation(request.getOccupation());  
        user.setAnnualIncome(request.getAnnualIncome());  
        user.setInvestmentGoal(request.getInvestmentGoals()); 
        user.setRiskCategory(request.getRiskCategory());  
        userRepository.save(user);
    
        UserDetails userDetails = loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
    
        return new AuthResponse(token);
    }
    

    public LoginResponse login(LoginRequest request) {
        System.out.println("🔵 Checking user: " + request.getEmail());
    
        User user = userRepository.findByEmail(request.getEmail())
            .orElse(null);
    
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null; 
        }
    
        System.out.println("🟢 User found: " + user.getEmail());
    
        String token = jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(), new ArrayList<>()));

        
        return new LoginResponse(token, user); 
    }
    
    

    public User getUserByEmail(String email) {
        System.out.println("🔍 Fetching user for email: " + email);
        return userRepository.findByEmail(email)
            .orElse(null);
    }

    @Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), authorities
    );
}

}
