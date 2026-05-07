package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.StatusPedido;
import com.pizzaria.sistema.security.SecurityConfig;
import com.pizzaria.sistema.security.UserDetailsServiceImpl;
import com.pizzaria.sistema.service.PedidoService;
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

@WebMvcTest(PedidoController.class)
@Import(SecurityConfig.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("GET /pedidos sem autenticação deve redirecionar para login")
    void getSemAutenticacaoDeveRedirecionar() throws Exception {
        mockMvc.perform(get("/pedidos"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /pedidos como ADMIN deve retornar 200")
    void getComoAdminDeveRetornar200() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /pedidos como CLIENTE deve retornar 200")
    void getComoClienteDeveRetornar200() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /pedidos/{id} deve retornar 200 quando pedido existe")
    void getByIdDeveRetornar200() throws Exception {
        Pedido pedido = new Pedido();
        pedido.setStatus(StatusPedido.PENDENTE);
        when(pedidoService.buscarPorId(1L)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /pedidos/{id} deve retornar 404 quando pedido não existe")
    void getByIdDeveRetornar404() throws Exception {
        when(pedidoService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pedidos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /pedidos/status/{status} deve retornar lista filtrada")
    void getByStatusDeveRetornarLista() throws Exception {
        when(pedidoService.listarPorStatus(StatusPedido.PENDENTE)).thenReturn(List.of());

        mockMvc.perform(get("/pedidos/status/PENDENTE"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("DELETE /pedidos/{id} como CLIENTE deve retornar 403")
    void deleteComoClienteDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/pedidos/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}