package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.security.SecurityConfig;
import com.pizzaria.sistema.security.UserDetailsServiceImpl;
import com.pizzaria.sistema.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CadastroController.class)
@Import({SecurityConfig.class})
class CadastroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("GET /cadastro deve retornar a view de cadastro")
    void getDeveRetornarViewCadastro() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"));
    }

    @Test
    @DisplayName("POST /cadastro com cliente válido deve redirecionar para login")
    void postClienteValidoDeveRedirecionarParaLogin() throws Exception {
        when(usuarioService.salvar(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "João")
                        .param("email", "joao@email.com")
                        .param("senha", "123456")
                        .param("tipo", "CLIENTE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?cadastro=ok"));
    }

    @Test
    @DisplayName("POST /cadastro com código admin correto deve redirecionar para login")
    void postAdminComCodigoCorretoDeveRedirecionar() throws Exception {
        when(usuarioService.salvar(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "Admin")
                        .param("email", "admin@email.com")
                        .param("senha", "123456")
                        .param("tipo", "ADMIN")
                        .param("codigoAdmin", "aK9mX3pQ7bT2wR6d"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?cadastro=ok"));
    }

    @Test
    @DisplayName("POST /cadastro com código admin errado deve retornar erro")
    void postAdminComCodigoErradoDeveRetornarErro() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "Admin")
                        .param("email", "admin@email.com")
                        .param("senha", "123456")
                        .param("tipo", "ADMIN")
                        .param("codigoAdmin", "codigoErrado"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("POST /cadastro com senha curta deve retornar erro")
    void postSenhaCurtaDeveRetornarErro() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "João")
                        .param("email", "joao@email.com")
                        .param("senha", "123")
                        .param("tipo", "CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @DisplayName("POST /cadastro com email duplicado deve retornar erro")
    void postEmailDuplicadoDeveRetornarErro() throws Exception {
        when(usuarioService.salvar(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Email já cadastrado"));

        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "João")
                        .param("email", "joao@email.com")
                        .param("senha", "123456")
                        .param("tipo", "CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().attributeExists("erro"));
    }
}