package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.TipoUsuario;
import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    private final UsuarioService usuarioService;

    public UsuarioViewController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios";
    }

    @PostMapping("/{id}/alterar-tipo")
    public String alterarTipo(@PathVariable Long id,
                              @RequestParam TipoUsuario tipo) {
        usuarioService.buscarPorId(id).ifPresent(usuario -> {
            usuarioService.atualizar(id, criarDadosAtualizacao(usuario, tipo));
        });
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Não é possível excluir este usuário pois ele está vinculado a um pedido.");
        }
        return "redirect:/usuarios";
    }

    private Usuario criarDadosAtualizacao(Usuario usuario, TipoUsuario novoTipo) {
        Usuario dados = new Usuario();
        dados.setNome(usuario.getNome());
        dados.setEmail(usuario.getEmail());
        dados.setTipo(novoTipo);
        return dados;
    }
}
