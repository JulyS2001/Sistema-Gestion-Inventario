package com.inventarioropa.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventarioropa.model.MotivoMovimiento;
import com.inventarioropa.model.MovimientoStock;
import com.inventarioropa.model.Producto;
import com.inventarioropa.model.TipoMovimiento;
import com.inventarioropa.repository.MovimientoStockRepository;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoRepository;
    private final ProductoService productoService;

    public MovimientoStockService(
            MovimientoStockRepository movimientoRepository,
            ProductoService productoService) {

        this.movimientoRepository = movimientoRepository;
        this.productoService = productoService;
    }

    public List<MovimientoStock> listarMovimientos() {

        return movimientoRepository.findAll();
    }

    @Transactional
    public MovimientoStock registrarMovimiento(
            Producto producto,
            TipoMovimiento tipo,
            int cantidad,
            MotivoMovimiento motivo) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto es obligatorio."
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de movimiento es obligatorio."
            );
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a 0."
            );
        }

        if (motivo == null) {
            throw new IllegalArgumentException(
                    "El motivo es obligatorio."
            );
        }

        int stockAnterior = producto.getStock();

        int stockResultante;

        switch (tipo) {

            case ENTRADA:

                stockResultante =
                        stockAnterior + cantidad;

                break;

            case SALIDA:

                if (cantidad > stockAnterior) {

                    throw new IllegalArgumentException(
                            "No hay suficiente stock disponible."
                    );
                }

                stockResultante =
                        stockAnterior - cantidad;

                break;

            case AJUSTE:

                stockResultante = cantidad;

                break;

            default:

                throw new IllegalArgumentException(
                        "Tipo de movimiento no válido."
                );
        }

        producto.setStock(stockResultante);

        productoService.guardarProducto(producto);

        MovimientoStock movimiento =
                new MovimientoStock();

        movimiento.setProducto(producto);
        movimiento.setTipo(tipo);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setCantidad(cantidad);
        movimiento.setStockResultante(stockResultante);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setMotivo(motivo);

        return movimientoRepository.save(movimiento);
    }

    public void eliminarMovimiento(Long id) {

        movimientoRepository.deleteById(id);
    }
}