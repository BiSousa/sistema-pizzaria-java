package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.Ingrediente;
import com.pizzaria.sistema.service.IngredienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public ResponseEntity<List<Ingrediente>> listarTodos() {
        List<Ingrediente> lista = ingredienteService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Ingrediente>> listarPaginado(Pageable pageable) {
        Page<Ingrediente> page = ingredienteService.listarTodos(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingrediente> buscarPorId(@PathVariable Long id) {
        return ingredienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Ingrediente> buscarPorNome(@RequestParam("nome") String nome) {
        return ingredienteService.buscarPorNome(nome)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ingrediente> salvar(@RequestBody Ingrediente ingrediente) {
        Ingrediente salvo = ingredienteService.salvar(ingrediente);
        URI location = URI.create("/ingredientes/" + salvo.getId());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingrediente> atualizar(@PathVariable Long id, @RequestBody Ingrediente dados) {
        Ingrediente atualizado = ingredienteService.atualizar(id, dados);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ingredienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
