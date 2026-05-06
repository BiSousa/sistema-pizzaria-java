package com.pizzaria.sistema.config;

import com.pizzaria.sistema.model.TipoUsuario;
import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.findByEmail("admin@pizzaria.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@pizzaria.com");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setTipo(TipoUsuario.ADMIN);
                usuarioRepository.save(admin);
                System.out.println(">>> Admin criado com sucesso!");
            } else {
                System.out.println(">>> Admin já existe.");
            }
        };
    }
}