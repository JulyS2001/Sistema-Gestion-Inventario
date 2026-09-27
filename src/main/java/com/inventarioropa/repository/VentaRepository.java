package com.inventarioropa.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inventarioropa.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // =========================================================
    // HISTORIAL
    // =========================================================

    List<Venta> findAllByOrderByFechaDesc();


    // =========================================================
    // DETALLE DE VENTA
    // =========================================================

    @Query("""
            SELECT DISTINCT v
            FROM Venta v
            LEFT JOIN FETCH v.detalles d
            LEFT JOIN FETCH d.producto
            WHERE v.id = :id
            """)
    Optional<Venta> buscarVentaConDetalles(
            @Param("id") Long id
    );


    // =========================================================
    // VENTAS DESDE UNA FECHA
    // =========================================================

    @Query("""
            SELECT COALESCE(SUM(v.total), 0)
            FROM Venta v
            WHERE v.fecha >= :desde
            """)
    BigDecimal calcularVentasDesde(
            @Param("desde") LocalDateTime desde
    );


    // =========================================================
    // CANTIDAD DE VENTAS DESDE UNA FECHA
    // =========================================================

    @Query("""
            SELECT COUNT(v)
            FROM Venta v
            WHERE v.fecha >= :desde
            """)
    long contarVentasDesde(
            @Param("desde") LocalDateTime desde
    );


    // =========================================================
    // UNIDADES VENDIDAS DESDE UNA FECHA
    // =========================================================

    @Query("""
            SELECT COALESCE(SUM(d.cantidad), 0)
            FROM DetalleVenta d
            JOIN d.venta v
            WHERE v.fecha >= :desde
            """)
    long calcularUnidadesVendidasDesde(
            @Param("desde") LocalDateTime desde
    );
}