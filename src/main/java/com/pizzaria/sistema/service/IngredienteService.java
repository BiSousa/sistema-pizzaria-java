package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.Ingrediente;
import com.pizzaria.sistema.repository.IngredienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    public List<Ingrediente> listarTodos() {
        return ingredienteRepository.findAll();
    }

    public Page<Ingrediente> listarTodos(Pageable pageable) {
        return ingredienteRepository.findAll(pageable);
    }

    public Optional<Ingrediente> buscarPorId(Long id) {
        return ingredienteRepository.findById(id);
    }

    public Optional<Ingrediente> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return Optional.empty();
        }
        return ingredienteRepository.findByNome(nome);
    }

    @Transactional
    public Ingrediente salvar(Ingrediente ingrediente) {
        if (ingrediente == null) {
            throw new IllegalArgumentException("Ingrediente não pode ser nulo");
        }
        if (ingrediente.getNome() == null || ingrediente.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do ingrediente é obrigatório");
        }

        if (ingredienteRepository.existsByNome(ingrediente.getNome())) {
            throw new IllegalArgumentException("Ingrediente com esse nome já existe");
        }

        return ingredienteRepository.save(ingrediente);
    }

    @Transactional
    public Ingrediente atualizar(Long id, Ingrediente dados) {
        Ingrediente existente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente não encontrado"));

        String novoNome = dados.getNome();
        if (novoNome != null && !novoNome.isBlank() && !novoNome.equals(existente.getNome())) {
            if (ingredienteRepository.existsByNome(novoNome)) {
                throw new IllegalArgumentException("Outro ingrediente com esse nome já existe");
            }
            existente.setNome(novoNome);
        }

        return ingredienteRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Ingrediente não encontrado");
        }
        ingredienteRepository.deleteById(id);
    }
}
