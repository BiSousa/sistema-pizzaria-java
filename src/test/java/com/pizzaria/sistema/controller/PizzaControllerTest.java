package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.model.TamanhoPizza;
import com.pizzaria.sistema.security.SecurityConfig;
import com.pizzaria.sistema.security.UserDetailsServiceImpl;
import com.pizzaria.sistema.service.PizzaService;
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

@WebMvcTest(PizzaController.class)
@Import(SecurityConfig.class)
class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PizzaService pizzaService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("GET /pizzas sem autenticação deve redirecionar para login")
    void getSemAutenticacaoDeveRedirecionar() throws Exception {
        mockMvc.perform(get("/pizzas"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /pizzas autenticado como ADMIN deve retornar 200")
    void getAutenticadoDeveRetornar200() throws Exception {
        when(pizzaService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pizzas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("GET /pizzas autenticado como CLIENTE deve retornar 200")
    void getClienteAutenticadoDeveRetornar200() throws Exception {
        when(pizzaService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pizzas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /pizzas/{id} deve retornar 200 quando pizza existe")
    void getByIdDeveRetornar200() throws Exception {
        Pizza pizza = new Pizza();
        pizza.setTamanho(TamanhoPizza.GRANDE);
        pizza.setPreco(45.0);
        when(pizzaService.buscarPorId(1L)).thenReturn(Optional.of(pizza));

        mockMvc.perform(get("/pizzas/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /pizzas/{id} deve retornar 404 quando pizza não existe")
    void getByIdDeveRetornar404() throws Exception {
        when(pizzaService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/pizzas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("DELETE /pizzas/{id} como CLIENTE deve retornar 403")
    void deleteComoClienteDeveRetornar403() throws Exception {
        mockMvc.perform(delete("/pizzas/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /pizzas/{id} como ADMIN deve retornar 200")
    void putComoAdminDeveRetornar200() throws Exception {
        Pizza pizza = new Pizza();
        pizza.setTamanho(TamanhoPizza.BROTO);
        pizza.setPreco(25.0);

        when(pizzaService.atualizar(eq(1L), any())).thenReturn(pizza);

        mockMvc.perform(put("/pizzas/1")
                        .with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"tamanho\":\"BROTO\",\"preco\":25.0}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("PUT /pizzas/{id} como CLIENTE deve retornar 403")
    void putComoClienteDeveRetornar403() throws Exception {
        mockMvc.perform(put("/pizzas/1")
                        .with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"tamanho\":\"BROTO\",\"preco\":25.0}"))
                .andExpect(status().isForbidden());
    }
}