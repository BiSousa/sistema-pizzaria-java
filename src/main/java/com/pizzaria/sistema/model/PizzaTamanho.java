package com.pizzaria.sistema.model;

import jakarta.persistence.*;

@Entity
public class PizzaTamanho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TamanhoPizza tamanho;

    private Double preco;

    @ManyToOne
    @JoinColumn(name = "pizza_id")
    private Pizza pizza;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TamanhoPizza getTamanho() { return tamanho; }
    public void setTamanho(TamanhoPizza tamanho) { this.tamanho = tamanho; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Pizza getPizza() { return pizza; }
    public void setPizza(Pizza pizza) { this.pizza = pizza; }
}
