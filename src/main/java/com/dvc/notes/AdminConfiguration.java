package com.dvc.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
                        .requestMatchers("/make/**").hasRole("ADMIN")
                        .requestMatchers("/delete/**").hasRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin((form) -> form.loginPage("/login").permitAll())
                .logout((logout) -> logout.permitAll().logoutSuccessUrl("/"));

        return http.build();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public UserDetailsService userDetailsService() {
        String password = jdbcTemplate.queryForObject("SELECT password FROM editors WHERE username = 'admin'", String.class);

        assert password != null;
        UserDetails user = User.withUsername("admin")
                .password(password)
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
