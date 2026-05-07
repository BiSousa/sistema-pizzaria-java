package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.security.SecurityConfig;
import com.pizzaria.sistema.security.UserDetailsServiceImpl;
import com.pizzaria.sistema.service.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(CategoriaController.class)
@Import(SecurityConfig.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("GET /categorias sem autenticação deve redirecionar para login")
    void getSemAutenticacaoDeveRedirecionar() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /categorias como ADMIN deve retornar 200")
    void getComoAdminDeveRetornar200() throws Exception {
        when(categoriaService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /categorias como CLIENTE deve retornar 403")
    void getComoClienteDeveRetornar403() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /categorias/{id} deve retornar 200 quando existe")
    void getByIdDeveRetornar200() throws Exception {
        Categoria c = new Categoria();
        c.setDescricao("Tradicional");
        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(c));

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /categorias/{id} deve retornar 404 quando não existe")
    void getByIdDeveRetornar404() throws Exception {
        when(categoriaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/categorias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /categorias/{id} como ADMIN deve retornar 204")
    void deleteComoAdminDeveRetornar204() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(Optional.of(new Categoria()));
        doNothing().when(categoriaService).deletar(1L);

        mockMvc.perform(delete("/categorias/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("DELETE /categorias/{id} como CLIENTE deve retornar 403")
    void deleteComoClienteDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/categorias/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /categorias deve listar todas")
    void getDeveListarTodas() throws Exception {
        Categoria c = new Categoria();
        c.setDescricao("Tradicional");
        when(categoriaService.listarTodos()).thenReturn(List.of(c));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /categorias/{id} como ADMIN deve retornar 200")
    void putComoAdminDeveRetornar200() throws Exception {
        Categoria c = new Categoria();
        c.setDescricao("Especial");

        when(categoriaService.atualizar(eq(1L), any())).thenReturn(c);

        mockMvc.perform(put("/categorias/1")
                        .with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Especial\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("PUT /categorias/{id} como CLIENTE deve retornar 403")
    void putComoClienteDeveRetornar403() throws Exception {
        mockMvc.perform(put("/categorias/1")
                        .with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Especial\"}"))
                .andExpect(status().isForbidden());
    }
}