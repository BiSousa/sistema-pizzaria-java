package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.TipoUsuario;
import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve salvar usuário com senha criptografada")
    void deveSalvarUsuarioComSenhaCriptografada() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario salvo = usuarioService.salvar(usuario);

        assertEquals("senhaCriptografada", salvo.getSenha());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve definir tipo CLIENTE por padrão quando tipo não informado")
    void deveDefinirTipoClientePorPadrao() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario salvo = usuarioService.salvar(usuario);

        assertEquals(TipoUsuario.CLIENTE, salvo.getTipo());
    }

    @Test
    @DisplayName("Deve lançar exceção se email já estiver cadastrado")
    void deveLancarExcecaoSeEmailJaCadastrado() {
        Usuario existente = new Usuario();
        existente.setEmail("teste@teste.com");

        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(existente));

        Usuario novo = new Usuario();
        novo.setEmail("teste@teste.com");
        novo.setSenha("123456");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(novo));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se senha estiver em branco")
    void deveLancarExcecaoSeSenhaEmBranco() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("");

        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se email estiver em branco")
    void deveLancarExcecaoSeEmailEmBranco() {
        Usuario usuario = new Usuario();
        usuario.setEmail("");
        usuario.setSenha("123456");

        assertThrows(IllegalArgumentException.class, () -> usuarioService.salvar(usuario));
        verify(usuarioRepository, never()).save(any());
    }
}