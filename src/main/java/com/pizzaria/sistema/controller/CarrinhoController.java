package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.Carrinho;
import com.pizzaria.sistema.model.Pizza;
import com.pizzaria.sistema.model.PizzaTamanho;
import com.pizzaria.sistema.service.PizzaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final PizzaService pizzaService;

    public CarrinhoController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public String exibir(Model model, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        model.addAttribute("carrinho", carrinho);
        return "carrinho";
    }

    @PostMapping("/adicionar")
    public String adicionar(@RequestParam Long pizzaId,
                            @RequestParam Long tamanhoId,
                            HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) carrinho = new Carrinho();

        Pizza pizza = pizzaService.buscarPorId(pizzaId).orElseThrow();
        Double preco = pizza.getTamanhos().stream()
                .filter(t -> t.getId().equals(tamanhoId))
                .findFirst()
                .map(PizzaTamanho::getPreco)
                .orElse(0.0);

        carrinho.adicionarItem(pizza, 1, preco);
        session.setAttribute("carrinho", carrinho);

        return "redirect:/carrinho";
    }
}
