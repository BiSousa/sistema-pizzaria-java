package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.repository.ItemPedidoRepository;
import com.pizzaria.sistema.repository.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public DashboardController(PedidoRepository pedidoRepository,
                               ItemPedidoRepository itemPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Object[]> pizzasVendidas = pedidoRepository.findPizzasMaisVendidas();
        model.addAttribute("pizzasLabels", pizzasVendidas.stream()
                .map(r -> r[1] != null ? r[1].toString() : "Sem categoria")
                .toList());
        model.addAttribute("pizzasData", pizzasVendidas.stream()
                .map(r -> r[2].toString())
                .toList());

        List<Object[]> clientesCompras = pedidoRepository.findClientesQueMailsCompram();
        model.addAttribute("clientesLabels", clientesCompras.stream()
                .map(r -> r[1] != null ? r[1].toString() : "Cliente")
                .toList());
        model.addAttribute("clientesData", clientesCompras.stream()
                .map(r -> r[2].toString())
                .toList());

        List<Object[]> pedidosStatus = pedidoRepository.findPedidosPorStatus();
        model.addAttribute("statusLabels", pedidosStatus.stream()
                .map(r -> r[0].toString())
                .toList());
        model.addAttribute("statusData", pedidosStatus.stream()
                .map(r -> r[1].toString())
                .toList());

        List<Object[]> ingredientes = itemPedidoRepository.findIngredientesMaisUtilizados();
        model.addAttribute("ingredientesLabels", ingredientes.stream()
                .map(r -> r[0].toString())
                .toList());
        model.addAttribute("ingredientesData", ingredientes.stream()
                .map(r -> r[1].toString())
                .toList());

        List<Object[]> tamanhos = itemPedidoRepository.findPedidosPorTamanho();
        model.addAttribute("tamanhosLabels", tamanhos.stream()
                .map(r -> r[0].toString())
                .toList());
        model.addAttribute("tamanhosData", tamanhos.stream()
                .map(r -> r[1].toString())
                .toList());

        return "dashboard";
    }
}
