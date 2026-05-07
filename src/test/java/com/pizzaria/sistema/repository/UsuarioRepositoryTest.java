package com.pizzaria.sistema.repository;

import com.pizzaria.sistema.model.TipoUsuario;
import com.pizzaria.sistema.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Salvar e buscar por id")
    void salvarEBuscarPorId() {
        Usuario u = new Usuario();
        u.setNome("Beatriz");
        u.setEmail("b@example.com");
        u.setSenha("senha");
        u.setTipo(TipoUsuario.CLIENTE);

        Usuario salvo = usuarioRepository.save(u);
        Optional<Usuario> encontrado = usuarioRepository.findById(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("b@example.com");
    }

    @Test
    @DisplayName("findByEmail retorna usuario quando existe")
    void findByEmail() {
        Usuario u = new Usuario();
        u.setNome("Admin");
        u.setEmail("admin@pizzaria.com");
        u.setSenha("x");
        u.setTipo(TipoUsuario.ADMIN);
        usuarioRepository.save(u);

        Optional<Usuario> opt = usuarioRepository.findByEmail("admin@pizzaria.com");
        assertThat(opt).isPresent();
        assertThat(opt.get().getTipo()).isEqualTo(TipoUsuario.ADMIN);
    }

    @Test
    @DisplayName("findByTipo retorna apenas admins")
    void findByTipo() {
        Usuario a = new Usuario(); a.setNome("A"); a.setEmail("a@a"); a.setSenha("s"); a.setTipo(TipoUsuario.ADMIN);
        Usuario c = new Usuario(); c.setNome("C"); c.setEmail("c@c"); c.setSenha("s"); c.setTipo(TipoUsuario.CLIENTE);
        usuarioRepository.save(a);
        usuarioRepository.save(c);

        List<Usuario> admins = usuarioRepository.findByTipo(TipoUsuario.ADMIN);
        assertThat(admins).hasSize(1).extracting("email").containsExactly("a@a");
    }
}
