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

    /**
     * Lista todos os ingredientes (sem paginação).
     */
    public List<Ingrediente> listarTodos() {
        return ingredienteRepository.findAll();
    }

    /**
     * Lista ingredientes paginados.
     */
    public Page<Ingrediente> listarTodos(Pageable pageable) {
        return ingredienteRepository.findAll(pageable);
    }

    /**
     * Busca por id.
     */
    public Optional<Ingrediente> buscarPorId(Long id) {
        return ingredienteRepository.findById(id);
    }

    /**
     * Busca por nome exato.
     */
    public Optional<Ingrediente> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return Optional.empty();
        }
        return ingredienteRepository.findByNome(nome);
    }

    /**
     * Salva um novo ingrediente.
     * Valida nome não nulo e unicidade por nome.
     */
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

    /**
     * Atualiza um ingrediente existente.
     * Só atualiza campos não nulos do objeto recebido.
     */
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

    /**
     * Deleta por id. Lança exceção se não existir.
     */
    @Transactional
    public void deletar(Long id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Ingrediente não encontrado");
        }
        ingredienteRepository.deleteById(id);
    }
}
