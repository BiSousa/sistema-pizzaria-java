package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.model.Ingrediente;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.model.TamanhoPizza;
import com.pizzaria.sistema.repository.CategoriaRepository;
import com.pizzaria.sistema.repository.IngredienteRepository;
import com.pizzaria.sistema.repository.PizzaRepository;
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

    public List<Pizza> listarPorTamanho(TamanhoPizza tamanho) {
        return pizzaRepository.findByTamanho(tamanho);
    }

    public Page<Pizza> listarPorTamanho(TamanhoPizza tamanho, Pageable pageable) {
        return pizzaRepository.findByTamanho(tamanho, pageable);
    }

    public List<Pizza> listarPorCategoria(Categoria categoria) {
        return pizzaRepository.findByCategoria(categoria);
    }

    public Page<Pizza> listarPorCategoria(Categoria categoria, Pageable pageable) {
        return pizzaRepository.findByCategoria(categoria, pageable);
    }

    @Transactional
    public Pizza salvar(Pizza pizza) {
        validarPizzaParaSalvarOuAtualizar(pizza);

        // valida categoria se informada
        if (pizza.getCategoria() != null) {
            Long catId = pizza.getCategoria().getId();
            if (catId == null || !categoriaRepository.existsById(catId)) {
                throw new IllegalArgumentException("Categoria inválida ou não encontrada");
            }
            // opcional: carregar categoria gerenciada
            Categoria categoria = categoriaRepository.findById(catId).orElseThrow();
            pizza.setCategoria(categoria);
        }

        // valida ingredientes se informados (espera-se que cada ingrediente tenha id)
        if (pizza.getIngredientes() != null && !pizza.getIngredientes().isEmpty()) {
            for (Ingrediente ing : pizza.getIngredientes()) {
                if (ing == null || ing.getId() == null || !ingredienteRepository.existsById(ing.getId())) {
                    throw new IllegalArgumentException("Ingrediente inválido ou não encontrado: " + (ing != null ? ing.getId() : "null"));
                }
            }
            // opcional: carregar lista gerenciada
            List<Ingrediente> ingredientesGerenciados = ingredienteRepository.findAllById(
                    pizza.getIngredientes().stream().map(Ingrediente::getId).toList()
            );
            pizza.setIngredientes(ingredientesGerenciados);
        }

        return pizzaRepository.save(pizza);
    }

    @Transactional
    public Pizza atualizar(Long id, Pizza dados) {
        Pizza existente = pizzaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pizza não encontrada"));

        // atualiza campos simples
        if (dados.getTamanho() != null) existente.setTamanho(dados.getTamanho());
        if (dados.getPreco() != null) existente.setPreco(dados.getPreco());

        // atualizar categoria (mesma validação do salvar)
        if (dados.getCategoria() != null) {
            Long catId = dados.getCategoria().getId();
            if (catId == null || !categoriaRepository.existsById(catId)) {
                throw new IllegalArgumentException("Categoria inválida ou não encontrada");
            }
            Categoria categoria = categoriaRepository.findById(catId).orElseThrow();
            existente.setCategoria(categoria);
        }

        // atualizar ingredientes (substitui a lista se fornecida)
        if (dados.getIngredientes() != null) {
            for (Ingrediente ing : dados.getIngredientes()) {
                if (ing == null || ing.getId() == null || !ingredienteRepository.existsById(ing.getId())) {
                    throw new IllegalArgumentException("Ingrediente inválido ou não encontrado: " + (ing != null ? ing.getId() : "null"));
                }
            }
            List<Ingrediente> ingredientesGerenciados = ingredienteRepository.findAllById(
                    dados.getIngredientes().stream().map(Ingrediente::getId).toList()
            );
            existente.setIngredientes(ingredientesGerenciados);
        }

        return pizzaRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!pizzaRepository.existsById(id)) {
            throw new IllegalArgumentException("Pizza não encontrada");
        }
        pizzaRepository.deleteById(id);
    }

    private void validarPizzaParaSalvarOuAtualizar(Pizza pizza) {
        if (pizza == null) {
            throw new IllegalArgumentException("Pizza não pode ser nula");
        }
        if (pizza.getTamanho() == null) {
            throw new IllegalArgumentException("Tamanho é obrigatório");
        }
        if (pizza.getPreco() == null || pizza.getPreco() < 0) {
            throw new IllegalArgumentException("Preço inválido");
        }
    }
}
