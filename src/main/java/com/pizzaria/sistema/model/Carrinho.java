package com.pizzaria.sistema.model;

import java.util.ArrayList;
import java.util.List;

public class Carrinho {
    private List<ItemPedido> itens = new ArrayList<>();

    public void adicionarItem(Pizza pizza, Integer quantidade, Double precoUnitario) {
        ItemPedido item = new ItemPedido();
        item.setPizza(pizza);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(precoUnitario);
        itens.add(item);
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public Double getTotal() {
        return itens.stream()
                .mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade())
                .sum();
    }
}
