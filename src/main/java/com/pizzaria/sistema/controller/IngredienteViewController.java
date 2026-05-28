package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.service.IngredienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ingredientes")
public class IngredienteViewController {

    private final IngredienteService ingredienteService;

    public IngredienteViewController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ingredientes", ingredienteService.listarTodos());
        return "ingredientes";
    }
}
