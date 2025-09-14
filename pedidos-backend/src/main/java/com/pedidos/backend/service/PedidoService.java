package com.pedidos.backend.service;

import com.pedidos.backend.model.Pedido;
import com.pedidos.backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> obtenerTodosPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (pedido.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
        return pedidoRepository.findById(id)
                .map(pedidoExistente -> {
                    pedidoExistente.setClienteNombre(pedidoActualizado.getClienteNombre());
                    pedidoExistente.setProducto(pedidoActualizado.getProducto());
                    pedidoExistente.setCantidad(pedidoActualizado.getCantidad());
                    pedidoExistente.setPrecio(pedidoActualizado.getPrecio());
                    pedidoExistente.setEstado(pedidoActualizado.getEstado());
                    return pedidoRepository.save(pedidoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    public void eliminarPedido(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    public List<Pedido> buscarPedidosPorCliente(String clienteNombre) {
        return pedidoRepository.findByClienteNombreContainingIgnoreCase(clienteNombre);
    }

    public List<Pedido> buscarPedidosPorProducto(String producto) {
        return pedidoRepository.findByProductoContainingIgnoreCase(producto);
    }

    public List<Pedido> buscarPedidosPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> obtenerPedidosRecientes() {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        return pedidoRepository.findPedidosRecientes(hace24Horas);
    }

    public Pedido cambiarEstadoPedido(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    try {
                        pedido.setEstado(Enum.valueOf(com.pedidos.backend.model.EstadoPedido.class, nuevoEstado.toUpperCase()));
                        return pedidoRepository.save(pedido);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Estado inválido: " + nuevoEstado);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    public Object obtenerEstadisticas() {
        return new Object() {
            public final long totalPedidos = pedidoRepository.count();
            public final double totalVentas = pedidoRepository.calcularTotalVentas();
            public final long pedidosPendientes = pedidoRepository.countByEstado("PENDIENTE");
            public final long pedidosEntregados = pedidoRepository.countByEstado("ENTREGADO");
        };
    }
}