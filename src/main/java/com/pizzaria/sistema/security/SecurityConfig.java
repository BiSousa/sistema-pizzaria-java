package com.pizzaria.sistema.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          LoginSuccessHandler loginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .userDetailsService(userDetailsService)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/", "/login").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/cadastro").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/cadastro").permitAll()
                        .requestMatchers("/api/pizzas/**", "/api/pedidos/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/pizzas/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/pizzas/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/pizzas/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/pizzas/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/pedidos/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/pedidos/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/pedidos/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/pedidos/**").hasRole("ADMIN")
                        .requestMatchers("/categorias/**", "/ingredientes/**", "/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/carrinho").hasRole("CLIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/pedidos/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/pedidos/finalizar").hasRole("CLIENTE")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/pedidos/*/confirmar").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/pedidos/*/rejeitar").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}