package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.TipoUsuario;
import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Page<Usuario> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return usuarioRepository.findByTipo(tipo);
    }

    public List<Usuario> listarAdmins() {
        return usuarioRepository.findByTipo(TipoUsuario.ADMIN);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (usuario.getTipo() == null) {
            usuario.setTipo(TipoUsuario.CLIENTE);
        }

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, Usuario dados) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (dados.getNome() != null) existente.setNome(dados.getNome());
        if (dados.getEmail() != null && !dados.getEmail().equals(existente.getEmail())) {
            if (usuarioRepository.findByEmail(dados.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email já cadastrado");
            }
            existente.setEmail(dados.getEmail());
        }
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            existente.setSenha(passwordEncoder.encode(dados.getSenha()));
        }
        if (dados.getTipo() != null) {
            existente.setTipo(dados.getTipo());
        }

        return usuarioRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
