package com.inventarioropa.controller;

import org.springframework.stereotype.Component;

import com.inventarioropa.service.ProductoService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

@Component
public class DashboardController {

    private final ProductoService productoService;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public DashboardController(
            ProductoService productoService) {

        this.productoService = productoService;
    }


    // =====================================================
    // LABELS
    // =====================================================

    @FXML
    private Label lblCantidadProductos;

    @FXML
    private Label lblStockTotal;

    @FXML
    private Label lblValorInventario;

    @FXML
    private Label lblStockBajo;


    // =====================================================
    // INICIALIZACIÓN
    // =====================================================

    @FXML
    public void initialize() {

        actualizarDashboard();
    }


    // =====================================================
    // ACTUALIZAR DASHBOARD
    // =====================================================

    private void actualizarDashboard() {

        long cantidadProductos =
                productoService.contarProductos();

        int stockTotal =
                productoService.obtenerStockTotal();

        var valorInventario =
                productoService.calcularValorInventario();

        long stockBajo =
                productoService.contarStockBajo();


        lblCantidadProductos.setText(
                String.valueOf(cantidadProductos)
        );

        lblStockTotal.setText(
                String.valueOf(stockTotal)
        );

        lblValorInventario.setText(
                "$ " + valorInventario.toString()
        );

        lblStockBajo.setText(
                String.valueOf(stockBajo)
        );
    }
}