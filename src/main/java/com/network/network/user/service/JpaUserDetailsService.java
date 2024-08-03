package com.network.network.user.service;

import com.network.network.user.User;
import com.network.network.user.exception.UserNotFoundException;
import com.network.network.user.resource.UserRepository;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return new JpaUserDetails(user);
    }

    static class JpaUserDetails implements UserDetails {
        @Getter private final int id;
        private final String email;
        private final String password;
        private final List<GrantedAuthority> authorities;

        public JpaUserDetails(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.password = user.getPassword();
            this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        }

        @Override
        public String getUsername() {
            return this.email;
        }

        @Override
        public String getPassword() {
            return this.password;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return this.authorities;
        }
    }
}
