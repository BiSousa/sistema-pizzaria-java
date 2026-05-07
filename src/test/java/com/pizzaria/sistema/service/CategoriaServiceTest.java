package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    private CategoriaRepository categoriaRepository;
    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        categoriaRepository = Mockito.mock(CategoriaRepository.class);
        categoriaService = new CategoriaService(categoriaRepository);
    }

    private Categoria categoriaValida() {
        Categoria c = new Categoria();
        c.setId(1L);
        c.setDescricao("Tradicional");
        return c;
    }

    @Test
    @DisplayName("Deve salvar categoria válida")
    void deveSalvarCategoriaValida() {
        Categoria categoria = categoriaValida();
        when(categoriaRepository.existsByDescricao("Tradicional")).thenReturn(false);
        when(categoriaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Categoria salva = categoriaService.salvar(categoria);

        assertNotNull(salva);
        verify(categoriaRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se descrição estiver em branco")
    void deveLancarExcecaoSeDescricaoEmBranco() {
        Categoria categoria = new Categoria();
        categoria.setDescricao("");

        assertThrows(IllegalArgumentException.class, () -> categoriaService.salvar(categoria));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se descrição for nula")
    void deveLancarExcecaoSeDescricaoNula() {
        Categoria categoria = new Categoria();
        categoria.setDescricao(null);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.salvar(categoria));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se categoria já existir")
    void deveLancarExcecaoSeCategoriaJaExistir() {
        when(categoriaRepository.existsByDescricao("Tradicional")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.salvar(categoriaValida()));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar descrição da categoria")
    void deveAtualizarDescricao() {
        Categoria existente = categoriaValida();
        Categoria dados = new Categoria();
        dados.setDescricao("Especial");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.existsByDescricao("Especial")).thenReturn(false);
        when(categoriaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Categoria atualizada = categoriaService.atualizar(1L, dados);

        assertEquals("Especial", atualizada.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria inexistente")
    void deveLancarExcecaoAoAtualizarInexistente() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> categoriaService.atualizar(99L, categoriaValida()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.deletar(99L));
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve deletar categoria existente")
    void deveDeletarCategoriaExistente() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> categoriaService.deletar(1L));
        verify(categoriaRepository).deleteById(1L);
    }
}