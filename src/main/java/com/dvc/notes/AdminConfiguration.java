package com.dvc.notes;

import java.net.http.HttpRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AdminConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	http.csrf().disable()
	    .authorizeHttpRequests((requests) -> requests
				   .requestMatchers("/edit/**").hasRole("ADMIN")
				   .anyRequest().permitAll())
	    .formLogin((form) -> form.loginPage("/login").permitAll())
	    .logout((logout) -> logout.permitAll().logoutSuccessUrl("/"));

	return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
	UserDetails user = User.withUsername("admin")
	    .password("{bcrypt}$2a$16$AbwQYwp9zO62yPDT/N2EbeACtGjbX7Nb4p.5s3GgvDG2m9dpeUHVq")
	    .roles("ADMIN")
	    .build();

	return new InMemoryUserDetailsManager(user);
    }
}
