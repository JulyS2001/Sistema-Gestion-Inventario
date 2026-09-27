package com.inventarioropa.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventarioropa.model.DetalleVenta;
import com.inventarioropa.model.MotivoMovimiento;
import com.inventarioropa.model.MovimientoStock;
import com.inventarioropa.model.Producto;
import com.inventarioropa.model.TipoMovimiento;
import com.inventarioropa.model.Venta;
import com.inventarioropa.repository.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;
    private final MovimientoStockService movimientoStockService;

    public VentaService(
            VentaRepository ventaRepository,
            ProductoService productoService,
            MovimientoStockService movimientoStockService) {

        this.ventaRepository = ventaRepository;
        this.productoService = productoService;
        this.movimientoStockService = movimientoStockService;
    }

    @Transactional
    public Venta registrarVenta(
            List<DetalleVenta> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta debe tener al menos un producto."
            );
        }

        BigDecimal total = BigDecimal.ZERO;

        List<DetalleVenta> detallesVenta =
                new ArrayList<>();

        for (DetalleVenta detalle : detalles) {

            Producto producto =
                    detalle.getProducto();

            int cantidad =
                    detalle.getCantidad();

            if (producto == null) {
                throw new IllegalArgumentException(
                        "Hay un detalle sin producto."
                );
            }

            if (cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor a 0."
                );
            }

            if (cantidad > producto.getStock()) {
                throw new IllegalArgumentException(
                        "No hay suficiente stock de: "
                        + producto.getNombre()
                );
            }

            BigDecimal precio =
                    producto.getPrecio();

            BigDecimal subtotal =
                    precio.multiply(
                            BigDecimal.valueOf(cantidad)
                    );

            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);

            total = total.add(subtotal);

            detallesVenta.add(detalle);
        }

        Venta venta = new Venta();

        venta.setFecha(LocalDateTime.now());
        venta.setTotal(total);
        venta.setDetalles(detallesVenta);

        for (DetalleVenta detalle : detallesVenta) {

            detalle.setVenta(venta);

            movimientoStockService.registrarMovimiento(
                    detalle.getProducto(),
                    TipoMovimiento.SALIDA,
                    detalle.getCantidad(),
                    MotivoMovimiento.VENTA
            );
        }

        return ventaRepository.save(venta);
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }
    
    public Venta buscarVentaConDetalles(Long id) {

        return ventaRepository
                .buscarVentaConDetalles(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No se encontró la venta."
                        )
                );
    }
    
	 // =========================================================
	 // ESTADÍSTICAS
	 // =========================================================
	
	 public BigDecimal calcularVentasDesde(
	         LocalDateTime desde) {
	
	     return ventaRepository
	             .calcularVentasDesde(desde);
	 }
	
	
	 public long contarVentasDesde(
	         LocalDateTime desde) {
	
	     return ventaRepository
	             .contarVentasDesde(desde);
	 }
	
	
	 public long calcularUnidadesVendidasDesde(
	         LocalDateTime desde) {
	
	     return ventaRepository
	             .calcularUnidadesVendidasDesde(desde);
	 }
}