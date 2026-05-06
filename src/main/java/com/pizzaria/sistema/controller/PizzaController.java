package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.Categoria;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.model.TamanhoPizza;
import com.pizzaria.sistema.service.PizzaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pizzas")
public class PizzaController {

    private final PizzaService pizzaService;

    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public ResponseEntity<List<Pizza>> listarTodos() {
        List<Pizza> lista = pizzaService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Pizza>> listarPaginado(Pageable pageable) {
        Page<Pizza> page = pizzaService.listarTodos(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pizza> buscarPorId(@PathVariable Long id) {
        return pizzaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tamanho")
    public ResponseEntity<List<Pizza>> listarPorTamanho(@RequestParam("tamanho") String tamanho) {
        try {
            TamanhoPizza t = TamanhoPizza.valueOf(tamanho.toUpperCase());
            List<Pizza> lista = pizzaService.listarPorTamanho(t);
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Pizza>> listarPorCategoria(@PathVariable Long categoriaId) {
        Categoria c = new Categoria();
        c.setId(categoriaId);
        List<Pizza> lista = pizzaService.listarPorCategoria(c);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<Pizza> salvar(@RequestBody Pizza pizza) {
        Pizza salvo = pizzaService.salvar(pizza);
        URI location = URI.create("/pizzas/" + salvo.getId());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pizza> atualizar(@PathVariable Long id, @RequestBody Pizza dados) {
        Pizza atualizado = pizzaService.atualizar(id, dados);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pizzaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
