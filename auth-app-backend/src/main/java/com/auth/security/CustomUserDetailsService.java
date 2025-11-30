package com.auth.security;

import com.auth.entity.User;
import com.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		log.info("Authenticating user with email: {}", email);

		User user = userRepository.findByEmailWithRoles(email).orElseThrow(() -> {
			log.warn("User not found: {}", email);
			return new UsernameNotFoundException("User not found with email: " + email);
		});

		Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());

		log.info("User '{}' authenticated with roles {}", user.getEmail(), authorities);

		return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
				.password(user.getPassword()).authorities(authorities).accountExpired(false).accountLocked(false)
				.credentialsExpired(false).disabled(!user.isEnabled()).build();
	}
}
