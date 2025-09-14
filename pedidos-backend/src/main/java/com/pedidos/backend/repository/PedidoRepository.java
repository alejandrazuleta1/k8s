package com.pedidos.backend.repository;

import com.pedidos.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteNombreContainingIgnoreCase(String clienteNombre);

    List<Pedido> findByProductoContainingIgnoreCase(String producto);

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE p.fechaCreacion >= :fecha ORDER BY p.fechaCreacion DESC")
    List<Pedido> findPedidosRecientes(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") String estado);

    @Query("SELECT COALESCE(SUM(p.cantidad * p.precio), 0) FROM Pedido p WHERE p.estado != 'CANCELADO'")
    Double calcularTotalVentas();
}