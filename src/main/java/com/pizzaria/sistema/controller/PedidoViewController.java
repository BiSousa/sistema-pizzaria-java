package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.*;
import com.pizzaria.sistema.repository.PizzaTamanhoRepository;
import com.pizzaria.sistema.service.PedidoService;
import com.pizzaria.sistema.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoViewController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final PizzaTamanhoRepository pizzaTamanhoRepository;

    public PedidoViewController(PedidoService pedidoService,
                                UsuarioService usuarioService,
                                PizzaTamanhoRepository pizzaTamanhoRepository) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.pizzaTamanhoRepository = pizzaTamanhoRepository;
    }

    @GetMapping
    public String listar(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername())
                .orElseThrow();

        if (usuario.getTipo() == TipoUsuario.ADMIN) {
            model.addAttribute("pedidos", pedidoService.listarTodos());
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("pedidos", pedidoService.listarPorCliente(usuario));
            model.addAttribute("isAdmin", false);
        }

        return "pedidos";
    }

    @PostMapping("/finalizar")
    public String finalizar(@RequestParam String enderecoEntrega,
                            @RequestParam List<Long> pizzaTamanhoIds,
                            @RequestParam List<Integer> quantidades,
                            @RequestParam Double precoTotal,
                            @AuthenticationPrincipal UserDetails userDetails,
                            HttpSession session,
                            Model model) {
        try {
            Usuario cliente = usuarioService.buscarPorEmail(userDetails.getUsername())
                    .orElseThrow();

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setEnderecoEntrega(enderecoEntrega);
            pedido.setStatus(StatusPedido.PENDENTE);
            pedido.setPrecoTotal(precoTotal);

            List<ItemPedido> itens = new ArrayList<>();
            for (int i = 0; i < pizzaTamanhoIds.size(); i++) {
                PizzaTamanho pt = pizzaTamanhoRepository.findById(pizzaTamanhoIds.get(i))
                        .orElseThrow();
                ItemPedido item = new ItemPedido();
                item.setPedido(pedido);
                item.setPizza(pt.getPizza());
                item.setQuantidade(quantidades.get(i));
                item.setPrecoUnitario(pt.getPreco());
                itens.add(item);
            }

            pedido.setItens(itens);
            pedidoService.salvar(pedido);

            session.removeAttribute("carrinho");

            return "redirect:/pedidos?sucesso=true";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "carrinho";
        }
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id).orElseThrow();
        pedido.setStatus(StatusPedido.CONFIRMADO);
        pedidoService.atualizar(id, pedido);
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/rejeitar")
    public String rejeitar(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id).orElseThrow();
        pedido.setStatus(StatusPedido.REJEITADO);
        pedidoService.atualizar(id, pedido);
        return "redirect:/pedidos";
    }
}
