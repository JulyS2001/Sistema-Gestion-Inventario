package com.inventarioropa.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.inventarioropa.model.DetalleVenta;
import com.inventarioropa.model.Venta;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class VentaDetalleController {

    // =========================================================
    // FORMATO DE FECHA
    // =========================================================

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );


    // =========================================================
    // CAMPOS
    // =========================================================

    @FXML
    private Label lblVentaId;

    @FXML
    private Label lblVentaFecha;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<DetalleVenta> tablaDetalles;

    @FXML
    private TableColumn<DetalleVenta, Object> colProducto;

    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;

    @FXML
    private TableColumn<DetalleVenta, BigDecimal> colPrecio;

    @FXML
    private TableColumn<DetalleVenta, BigDecimal> colSubtotal;


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @FXML
    public void initialize() {

        configurarTabla();
    }


    // =========================================================
    // CONFIGURAR TABLA
    // =========================================================

    private void configurarTabla() {

        colProducto.setCellValueFactory(
                new PropertyValueFactory<>("producto")
        );

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precioUnitario")
        );

        colSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subtotal")
        );


        tablaDetalles.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
    }


    // =========================================================
    // CARGAR VENTA
    // =========================================================

    public void cargarVenta(Venta venta) {

        // -----------------------------------------------------
        // INFORMACIÓN GENERAL
        // -----------------------------------------------------

        lblVentaId.setText(
                String.valueOf(
                        venta.getId()
                )
        );


        if (venta.getFecha() != null) {

            lblVentaFecha.setText(
                    venta.getFecha()
                            .format(formatoFecha)
            );

        } else {

            lblVentaFecha.setText("-");
        }


        // -----------------------------------------------------
        // DETALLES
        // -----------------------------------------------------

        tablaDetalles.setItems(
                FXCollections.observableArrayList(
                        venta.getDetalles()
                )
        );


        // -----------------------------------------------------
        // TOTAL
        // -----------------------------------------------------

        lblTotal.setText(
                "$ " + formatearPrecio(
                        venta.getTotal()
                )
        );
    }


    // =========================================================
    // FORMATEAR PRECIO
    // =========================================================

    private String formatearPrecio(BigDecimal valor) {

        NumberFormat formato =
                NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-AR"));

        return formato.format(valor);
    }
}