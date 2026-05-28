package com.pizzaria.sistema.controller;

import com.pizzaria.sistema.model.*;
import com.pizzaria.sistema.service.PizzaService;
import com.pizzaria.sistema.service.CategoriaService;
import com.pizzaria.sistema.service.IngredienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Controller
@RequestMapping("/pizzas")
public class PizzaViewController {

    private final PizzaService pizzaService;
    private final CategoriaService categoriaService;
    private final IngredienteService ingredienteService;

    public PizzaViewController(PizzaService pizzaService,
                               CategoriaService categoriaService,
                               IngredienteService ingredienteService) {
        this.pizzaService = pizzaService;
        this.categoriaService = categoriaService;
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pizzas", pizzaService.listarTodosComTamanhos());
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("ingredientes", ingredienteService.listarTodos());
        return "pizzas";
    }


    @GetMapping("/{id}/dados")
    @ResponseBody
    public Map<String, Object> dadosPizza(@PathVariable Long id) {
        Pizza pizza = pizzaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pizza não encontrada"));

        Map<String, Object> dados = new HashMap<>();
        dados.put("id", pizza.getId());
        dados.put("nome", pizza.getNome());
        dados.put("categoriaId", pizza.getCategoria() != null ? pizza.getCategoria().getId() : null);
        dados.put("ingredientes", pizza.getIngredientes() != null ?
                pizza.getIngredientes().stream().map(i -> i.getId().toString()).toList() :
                List.of());

        List<Map<String, Object>> tamanhos = new ArrayList<>();
        if (pizza.getTamanhos() != null) {
            for (PizzaTamanho t : pizza.getTamanhos()) {
                Map<String, Object> tm = new HashMap<>();
                tm.put("id", t.getId());
                tm.put("tamanho", t.getTamanho().name());
                tm.put("preco", t.getPreco());
                tamanhos.add(tm);
            }
        }
        dados.put("tamanhos", tamanhos);
        dados.put("fotoBase64", pizza.getFotoBase64());

        return dados;
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam String nome,
                         @RequestParam Long categoriaId,
                         @RequestParam(required = false) List<Long> ingredienteIds,
                         @RequestParam(required = false) List<String> tamanhos,
                         @RequestParam(required = false) Double precoBroto,
                         @RequestParam(required = false) Double precoGrande,
                         @RequestParam(required = false) MultipartFile foto,
                         Model model) {
        try {
            Pizza pizza = new Pizza();
            pizza.setNome(nome);

            Categoria categoria = new Categoria();
            categoria.setId(categoriaId);
            pizza.setCategoria(categoria);

            if (ingredienteIds != null) {
                pizza.setIngredientes(ingredienteIds.stream().map(id -> {
                    Ingrediente ing = new Ingrediente();
                    ing.setId(id);
                    return ing;
                }).toList());
            }

            List<PizzaTamanho> listaTamanhos = new ArrayList<>();
            if (tamanhos != null) {
                for (String t : tamanhos) {
                    PizzaTamanho pt = new PizzaTamanho();
                    pt.setPizza(pizza);
                    pt.setTamanho(TamanhoPizza.valueOf(t));
                    if (t.equals("BROTO")) pt.setPreco(precoBroto);
                    if (t.equals("GRANDE")) pt.setPreco(precoGrande);
                    listaTamanhos.add(pt);
                }
            }
            pizza.setTamanhos(listaTamanhos);

            if (foto != null && !foto.isEmpty()) {
                pizza.setFotoBase64(converterParaBase64(foto));
            }

            pizzaService.salvar(pizza);
            return "redirect:/pizzas";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("pizzas", pizzaService.listarTodos());
            model.addAttribute("categorias", categoriaService.listarTodos());
            model.addAttribute("ingredientes", ingredienteService.listarTodos());
            return "pizzas";
        }
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(@PathVariable Long id,
                            @RequestParam String nome,
                            @RequestParam Long categoriaId,
                            @RequestParam(required = false) List<Long> ingredienteIds,
                            @RequestParam(required = false) List<String> tamanhos,
                            @RequestParam(required = false) Double precoBroto,
                            @RequestParam(required = false) Double precoGrande,
                            @RequestParam(required = false) MultipartFile foto,
                            Model model) {
        try {
            Pizza dados = new Pizza();
            dados.setNome(nome);

            Categoria categoria = new Categoria();
            categoria.setId(categoriaId);
            dados.setCategoria(categoria);

            if (ingredienteIds != null) {
                dados.setIngredientes(ingredienteIds.stream().map(ingId -> {
                    Ingrediente ing = new Ingrediente();
                    ing.setId(ingId);
                    return ing;
                }).toList());
            }

            List<PizzaTamanho> listaTamanhos = new ArrayList<>();
            if (tamanhos != null) {
                for (String t : tamanhos) {
                    PizzaTamanho pt = new PizzaTamanho();
                    pt.setPizza(dados); // vínculo com a pizza
                    pt.setTamanho(TamanhoPizza.valueOf(t));
                    if (t.equals("BROTO") && precoBroto != null) pt.setPreco(precoBroto);
                    if (t.equals("GRANDE") && precoGrande != null) pt.setPreco(precoGrande);
                    listaTamanhos.add(pt);

                }
            }
            dados.setTamanhos(listaTamanhos);

            if (foto != null && !foto.isEmpty()) {
                dados.setFotoBase64(converterParaBase64(foto));
            }

            pizzaService.atualizar(id, dados);
            return "redirect:/pizzas";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("pizzas", pizzaService.listarTodos());
            model.addAttribute("categorias", categoriaService.listarTodos());
            model.addAttribute("ingredientes", ingredienteService.listarTodos());
            return "pizzas";
        }
    }

    private String converterParaBase64(MultipartFile foto) throws IOException {
        byte[] bytes = foto.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String tipo = foto.getContentType();
        return "data:" + tipo + ";base64," + base64;
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pizzaService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pizza excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Não é possível excluir esta pizza pois ela está vinculada a um pedido.");
        }
        return "redirect:/pizzas";
    }


}
