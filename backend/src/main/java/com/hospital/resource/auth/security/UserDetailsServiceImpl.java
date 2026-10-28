package com.hospital.resource.auth.security;

import com.hospital.resource.auth.entity.User;
import com.hospital.resource.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
        String role = user.getRole();
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            if ("ADMINISTRATOR".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            if ("ADMIN".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
            if ("NURSING_OFFICER".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_NURSE"));
            if ("NURSE".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_NURSING_OFFICER"));
            if ("MEDICAL_DOCTOR".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_DOCTOR"));
            if ("DOCTOR".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_MEDICAL_DOCTOR"));
            if ("HYGIENE_STAFF".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_CLEANER"));
            if ("CLEANER".equals(role)) authorities.add(new SimpleGrantedAuthority("ROLE_HYGIENE_STAFF"));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                !user.isLocked(),
                authorities
        );
    }
}
