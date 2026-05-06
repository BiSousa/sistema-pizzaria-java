package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.TipoUsuario;
import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    private static final String CODIGO_ADMIN = "aK9mX3pQ7bT2wR6d";

    private final UsuarioService usuarioService;

    public CadastroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String exibirCadastro() {
        return "cadastro";
    }

    @PostMapping
    public String realizarCadastro(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String tipo,
            @RequestParam(required = false) String codigoAdmin,
            Model model) {

        try {
            if (senha.length() < 6) {
                model.addAttribute("erro", "A senha deve ter pelo menos 6 caracteres.");
                return "cadastro";
            }

            TipoUsuario tipoUsuario = TipoUsuario.valueOf(tipo);

            if (tipoUsuario == TipoUsuario.ADMIN) {
                if (codigoAdmin == null || !codigoAdmin.equals(CODIGO_ADMIN)) {
                    model.addAttribute("erro", "Código de administrador inválido.");
                    return "cadastro";
                }
            }

            Usuario novo = new Usuario();
            novo.setNome(nome);
            novo.setEmail(email);
            novo.setSenha(senha);
            novo.setTipo(tipoUsuario);
            usuarioService.salvar(novo);

            return "redirect:/login?cadastro=ok";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
    }
}