package com.inventarioropa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventarioropa.model.DetalleVenta;

public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Long> {
}