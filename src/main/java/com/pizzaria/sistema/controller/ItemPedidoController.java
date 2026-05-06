package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.ItemPedido;
import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.service.ItemPedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping
    public ResponseEntity<List<ItemPedido>> listarTodos() {
        return ResponseEntity.ok(itemPedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable Long id) {
        return itemPedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<ItemPedido>> listarPorPedido(@PathVariable Long pedidoId) {
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        return ResponseEntity.ok(itemPedidoService.listarPorPedido(pedido));
    }

    @GetMapping("/pizza/{pizzaId}")
    public ResponseEntity<List<ItemPedido>> listarPorPizza(@PathVariable Long pizzaId) {
        Pizza pizza = new Pizza();
        pizza.setId(pizzaId);
        return ResponseEntity.ok(itemPedidoService.listarPorPizza(pizza));
    }

    @PostMapping
    public ResponseEntity<ItemPedido> salvar(@RequestBody ItemPedido item) {
        ItemPedido salvo = itemPedidoService.salvar(item);
        URI location = URI.create("/itens-pedido/" + salvo.getId());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedido> atualizar(@PathVariable Long id, @RequestBody ItemPedido dados) {
        ItemPedido atualizado = itemPedidoService.atualizar(id, dados);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        itemPedidoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
