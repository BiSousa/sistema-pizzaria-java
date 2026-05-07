package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.Ingrediente;
import com.pizzaria.sistema.repository.IngredienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngredienteServiceTest {

    private IngredienteRepository ingredienteRepository;
    private IngredienteService ingredienteService;

    @BeforeEach
    void setUp() {
        ingredienteRepository = Mockito.mock(IngredienteRepository.class);
        ingredienteService = new IngredienteService(ingredienteRepository);
    }

    private Ingrediente ingredienteValido() {
        Ingrediente i = new Ingrediente();
        i.setId(1L);
        i.setNome("Queijo");
        return i;
    }

    @Test
    @DisplayName("Deve salvar ingrediente válido")
    void deveSalvarIngredienteValido() {
        when(ingredienteRepository.existsByNome("Queijo")).thenReturn(false);
        when(ingredienteRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Ingrediente salvo = ingredienteService.salvar(ingredienteValido());

        assertNotNull(salvo);
        verify(ingredienteRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se nome estiver em branco")
    void deveLancarExcecaoSeNomeEmBranco() {
        Ingrediente i = new Ingrediente();
        i.setNome("");

        assertThrows(IllegalArgumentException.class, () -> ingredienteService.salvar(i));
        verify(ingredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se nome for nulo")
    void deveLancarExcecaoSeNomeNulo() {
        Ingrediente i = new Ingrediente();
        i.setNome(null);

        assertThrows(IllegalArgumentException.class, () -> ingredienteService.salvar(i));
        verify(ingredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se ingrediente já existir")
    void deveLancarExcecaoSeIngredienteJaExistir() {
        when(ingredienteRepository.existsByNome("Queijo")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> ingredienteService.salvar(ingredienteValido()));
        verify(ingredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar nome do ingrediente")
    void deveAtualizarNome() {
        Ingrediente existente = ingredienteValido();
        Ingrediente dados = new Ingrediente();
        dados.setNome("Mussarela");

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(ingredienteRepository.existsByNome("Mussarela")).thenReturn(false);
        when(ingredienteRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Ingrediente atualizado = ingredienteService.atualizar(1L, dados);

        assertEquals("Mussarela", atualizado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar ingrediente inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        when(ingredienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ingredienteService.atualizar(99L, ingredienteValido()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar ingrediente inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(ingredienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> ingredienteService.deletar(99L));
        verify(ingredienteRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve deletar ingrediente existente")
    void deveDeletarIngredienteExistente() {
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> ingredienteService.deletar(1L));
        verify(ingredienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve listar todos os ingredientes")
    void deveListarTodosOsIngredientes() {
        when(ingredienteRepository.findAll()).thenReturn(List.of(ingredienteValido(), ingredienteValido()));

        List<Ingrediente> resultado = ingredienteService.listarTodos();

        assertEquals(2, resultado.size());
    }
}