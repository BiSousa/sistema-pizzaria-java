package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.*;
import com.pizzaria.sistema.repository.CategoriaRepository;
import com.pizzaria.sistema.repository.IngredienteRepository;
import com.pizzaria.sistema.repository.PizzaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final CategoriaRepository categoriaRepository;
    private final IngredienteRepository ingredienteRepository;

    public PizzaService(PizzaRepository pizzaRepository,
                        CategoriaRepository categoriaRepository,
                        IngredienteRepository ingredienteRepository) {
        this.pizzaRepository = pizzaRepository;
        this.categoriaRepository = categoriaRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    public List<Pizza> listarTodos() {
        return pizzaRepository.findAll();
    }

    public Page<Pizza> listarTodos(Pageable pageable) {
        return pizzaRepository.findAll(pageable);
    }

    public Optional<Pizza> buscarPorId(Long id) {
        return pizzaRepository.findById(id);
    }

    public List<Pizza> listarPorCategoria(Categoria categoria) {
        return pizzaRepository.findByCategoria(categoria);
    }

    public Page<Pizza> listarPorCategoria(Categoria categoria, Pageable pageable) {
        return pizzaRepository.findByCategoria(categoria, pageable);
    }

    public List<Pizza> listarPorTamanho(TamanhoPizza tamanho) {
        return pizzaRepository.findByTamanhos_Tamanho(tamanho);
    }

    public Page<Pizza> listarPorTamanho(TamanhoPizza tamanho, Pageable pageable) {
        return pizzaRepository.findByTamanhos_Tamanho(tamanho, pageable);
    }

    @Transactional
    public Pizza salvar(Pizza pizza) {
        validarPizza(pizza);

        if (pizza.getCategoria() != null) {
            Long catId = pizza.getCategoria().getId();
            if (catId == null || !categoriaRepository.existsById(catId)) {
                throw new IllegalArgumentException("Categoria inválida ou não encontrada");
            }
            Categoria categoria = categoriaRepository.findById(catId).orElseThrow();
            pizza.setCategoria(categoria);
        }

        if (pizza.getIngredientes() != null && !pizza.getIngredientes().isEmpty()) {
            List<Long> ids = pizza.getIngredientes().stream().map(Ingrediente::getId).toList();
            pizza.setIngredientes(ingredienteRepository.findAllById(ids));
        }

        if (pizza.getTamanhos() == null || pizza.getTamanhos().isEmpty()) {
            throw new IllegalArgumentException("É necessário informar ao menos um tamanho com preço");
        }

        return pizzaRepository.save(pizza);
    }

    @Transactional
    public Pizza atualizar(Long id, Pizza dados) {
        Pizza existente = pizzaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pizza não encontrada"));

        if (dados.getNome() != null) existente.setNome(dados.getNome());

        if (dados.getCategoria() != null) {
            Long catId = dados.getCategoria().getId();
            existente.setCategoria(categoriaRepository.findById(catId).orElseThrow());
        }

        if (dados.getIngredientes() != null) {
            List<Long> ids = dados.getIngredientes().stream()
                    .map(Ingrediente::getId).toList();
            existente.setIngredientes(ingredienteRepository.findAllById(ids));
        }

        if (dados.getTamanhos() != null) {
            existente.getTamanhos().clear();
            for (PizzaTamanho t : dados.getTamanhos()) {
                t.setPizza(existente);
                existente.getTamanhos().add(t);
            }
        }

        if (dados.getFotoBase64() != null) {
            existente.setFotoBase64(dados.getFotoBase64());
        }

        return pizzaRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        try {
            pizzaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Não é possível excluir uma pizza que já foi usada em pedidos");
        }
    }

    private void validarPizza(Pizza pizza) {
        if (pizza == null) {
            throw new IllegalArgumentException("Pizza não pode ser nula");
        }
        if (pizza.getNome() == null || pizza.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
    }

    public List<Pizza> listarTodosComTamanhos() {
        return pizzaRepository.findAllComTamanhos();
    }

}
