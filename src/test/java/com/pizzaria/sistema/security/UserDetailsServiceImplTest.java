package com.pizzaria.sistema.security;

import com.pizzaria.sistema.model.TipoUsuario;
import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UsuarioRepository usuarioRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        userDetailsService = new UserDetailsServiceImpl(usuarioRepository);
    }

    @Test
    @DisplayName("Deve carregar usuário pelo email com role correta")
    void deveCarregarUsuarioPeloEmail() {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@pizzaria.com");
        usuario.setSenha("hashSenha");
        usuario.setTipo(TipoUsuario.ADMIN);

        when(usuarioRepository.findByEmail("admin@pizzaria.com")).thenReturn(Optional.of(usuario));

        UserDetails details = userDetailsService.loadUserByUsername("admin@pizzaria.com");

        assertEquals("admin@pizzaria.com", details.getUsername());
        assertEquals("hashSenha", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve carregar usuário do tipo CLIENTE com role correta")
    void deveCarregarClienteComRoleCorreta() {
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@pizzaria.com");
        usuario.setSenha("hashSenha");
        usuario.setTipo(TipoUsuario.CLIENTE);

        when(usuarioRepository.findByEmail("cliente@pizzaria.com")).thenReturn(Optional.of(usuario));

        UserDetails details = userDetailsService.loadUserByUsername("cliente@pizzaria.com");

        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException se email não existir")
    void deveLancarExcecaoSeEmailNaoExistir() {
        when(usuarioRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inexistente@email.com"));
    }
}