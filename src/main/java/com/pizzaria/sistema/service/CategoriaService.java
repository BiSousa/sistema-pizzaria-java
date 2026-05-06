package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodos() {
        return categoriaRepository.findAll();
    }

    public Page<Categoria> listarTodos(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> buscarPorDescricao(String descricao) {
        return categoriaRepository.findByDescricao(descricao);
    }

    @Transactional
    public Categoria salvar(Categoria categoria) {
        if (categoria == null || categoria.getDescricao() == null || categoria.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Descrição da categoria é obrigatória");
        }
        if (categoriaRepository.existsByDescricao(categoria.getDescricao())) {
            throw new IllegalArgumentException("Categoria já existe");
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, Categoria dados) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        if (dados.getDescricao() != null && !dados.getDescricao().isBlank()) {
            if (!dados.getDescricao().equals(existente.getDescricao())
                    && categoriaRepository.existsByDescricao(dados.getDescricao())) {
                throw new IllegalArgumentException("Outra categoria com essa descrição já existe");
            }
            existente.setDescricao(dados.getDescricao());
        }

        return categoriaRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}
