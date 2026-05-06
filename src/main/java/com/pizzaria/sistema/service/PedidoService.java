package com.pizzaria.sistema.service;

import com.pizzaria.sistema.model.Pedido;
import com.pizzaria.sistema.model.StatusPedido;
import com.pizzaria.sistema.model.Usuario;
import com.pizzaria.sistema.repository.PedidoRepository;
import com.pizzaria.sistema.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarPorCliente(Usuario cliente) {
        return pedidoRepository.findByCliente(cliente);
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    @Transactional
    public Pedido salvar(Pedido pedido) {
        validarPedido(pedido);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizar(Long id, Pedido dados) {
        Pedido existente = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (dados.getEnderecoEntrega() != null) existente.setEnderecoEntrega(dados.getEnderecoEntrega());
        if (dados.getStatus() != null) existente.setStatus(dados.getStatus());
        if (dados.getPrecoTotal() != null) existente.setPrecoTotal(dados.getPrecoTotal());

        if (dados.getCliente() != null) {
            Long clienteId = dados.getCliente().getId();
            if (clienteId == null || !usuarioRepository.existsById(clienteId)) {
                throw new IllegalArgumentException("Cliente inválido");
            }
            existente.setCliente(usuarioRepository.findById(clienteId).orElseThrow());
        }

        if (dados.getItens() != null) {
            existente.setItens(dados.getItens());
        }

        return pedidoRepository.save(existente);
    }

    @Transactional
    public void deletar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    private void validarPedido(Pedido pedido) {
        if (pedido == null) throw new IllegalArgumentException("Pedido não pode ser nulo");
        if (pedido.getEnderecoEntrega() == null || pedido.getEnderecoEntrega().isBlank()) {
            throw new IllegalArgumentException("Endereço de entrega é obrigatório");
        }
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null
                || !usuarioRepository.existsById(pedido.getCliente().getId())) {
            throw new IllegalArgumentException("Cliente inválido");
        }
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter ao menos um item");
        }
        if (pedido.getPrecoTotal() == null || pedido.getPrecoTotal() < 0) {
            throw new IllegalArgumentException("Preço total inválido");
        }
    }
}
