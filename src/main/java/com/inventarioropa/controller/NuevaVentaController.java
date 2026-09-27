package com.inventarioropa.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.inventarioropa.model.DetalleVenta;
import com.inventarioropa.model.Producto;
import com.inventarioropa.service.ProductoService;
import com.inventarioropa.service.VentaService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class NuevaVentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;

    private final List<DetalleVenta> detallesVenta = new ArrayList<>();

    public NuevaVentaController(
            VentaService ventaService,
            ProductoService productoService) {

        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    @FXML
    private ComboBox<Producto> cmbProducto;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TableView<DetalleVenta> tablaDetalles;

    @FXML
    private TableColumn<DetalleVenta, Producto> colProducto;

    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;

    @FXML
    private TableColumn<DetalleVenta, BigDecimal> colPrecio;

    @FXML
    private TableColumn<DetalleVenta, BigDecimal> colSubtotal;

    @FXML
    private Label lblTotal;


    @FXML
    public void initialize() {

        configurarTabla();

        cargarProductos();

        actualizarTotal();
    }


    // =========================================================
    // CONFIGURACIÓN DE TABLA
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
    // CARGAR PRODUCTOS
    // =========================================================

    private void cargarProductos() {

        cmbProducto.setItems(
                FXCollections.observableArrayList(
                        productoService.listarProductos()
                )
        );
    }


    // =========================================================
    // AGREGAR PRODUCTO
    // =========================================================

    @FXML
    private void agregarProducto() {

        Producto producto = cmbProducto.getValue();

        String cantidadTexto =
                txtCantidad.getText().trim();


        // -----------------------------------------------------
        // VALIDAR PRODUCTO
        // -----------------------------------------------------

        if (producto == null) {

            mostrarError(
                    "Seleccioná un producto."
            );

            return;
        }


        // -----------------------------------------------------
        // VALIDAR CANTIDAD VACÍA
        // -----------------------------------------------------

        if (cantidadTexto.isEmpty()) {

            mostrarError(
                    "Ingresá una cantidad."
            );

            return;
        }


        // -----------------------------------------------------
        // CONVERTIR CANTIDAD
        // -----------------------------------------------------

        int cantidad;

        try {

            cantidad =
                    Integer.parseInt(cantidadTexto);

        } catch (NumberFormatException e) {

            mostrarError(
                    "La cantidad debe ser un número entero."
            );

            return;
        }


        // -----------------------------------------------------
        // VALIDAR CANTIDAD
        // -----------------------------------------------------

        if (cantidad <= 0) {

            mostrarError(
                    "La cantidad debe ser mayor a 0."
            );

            return;
        }


        // -----------------------------------------------------
        // VALIDAR STOCK
        // -----------------------------------------------------

        if (cantidad > producto.getStock()) {

            mostrarError(
                    "No hay suficiente stock disponible.\n\n"
                    + "Stock actual: "
                    + producto.getStock()
            );

            return;
        }


        // -----------------------------------------------------
        // BUSCAR SI EL PRODUCTO YA ESTÁ EN LA VENTA
        // -----------------------------------------------------

        DetalleVenta existente =
                detallesVenta.stream()
                        .filter(detalle ->
                                detalle.getProducto()
                                        .getId()
                                        .equals(producto.getId())
                        )
                        .findFirst()
                        .orElse(null);


        // =====================================================
        // PRODUCTO YA EXISTENTE
        // =====================================================

        if (existente != null) {

            int nuevaCantidad =
                    existente.getCantidad()
                            + cantidad;


            // -------------------------------------------------
            // VALIDAR STOCK TOTAL
            // -------------------------------------------------

            if (nuevaCantidad > producto.getStock()) {

                mostrarError(
                        "La cantidad total supera "
                        + "el stock disponible.\n\n"
                        + "Stock actual: "
                        + producto.getStock()
                );

                return;
            }


            // -------------------------------------------------
            // ACTUALIZAR CANTIDAD
            // -------------------------------------------------

            existente.setCantidad(
                    nuevaCantidad
            );


            // -------------------------------------------------
            // ACTUALIZAR SUBTOTAL
            // -------------------------------------------------

            existente.setSubtotal(
                    existente.getPrecioUnitario()
                            .multiply(
                                    BigDecimal.valueOf(
                                            nuevaCantidad
                                    )
                            )
            );

        } else {

            // =================================================
            // PRODUCTO NUEVO EN LA VENTA
            // =================================================

            DetalleVenta detalle =
                    new DetalleVenta();

            BigDecimal precio =
                    producto.getPrecio();


            detalle.setProducto(producto);

            detalle.setCantidad(cantidad);

            detalle.setPrecioUnitario(precio);

            detalle.setSubtotal(
                    precio.multiply(
                            BigDecimal.valueOf(cantidad)
                    )
            );


            detallesVenta.add(detalle);
        }


        // -----------------------------------------------------
        // ACTUALIZAR INTERFAZ
        // -----------------------------------------------------

        actualizarTabla();

        actualizarTotal();

        limpiarEntradaProducto();
    }


    // =========================================================
    // QUITAR PRODUCTO
    // =========================================================

    @FXML
    private void quitarProducto() {

        DetalleVenta seleccionado =
                tablaDetalles
                        .getSelectionModel()
                        .getSelectedItem();


        if (seleccionado == null) {

            mostrarInformacion(
                    "Seleccioná un producto de la venta "
                    + "para quitarlo."
            );

            return;
        }


        detallesVenta.remove(
                seleccionado
        );


        actualizarTabla();

        actualizarTotal();
    }


    // =========================================================
    // REGISTRAR VENTA
    // =========================================================

    @FXML
    private void registrarVenta() {

        // -----------------------------------------------------
        // VALIDAR QUE HAYA PRODUCTOS
        // -----------------------------------------------------

        if (detallesVenta.isEmpty()) {

            mostrarError(
                    "Agregá al menos un producto a la venta."
            );

            return;
        }


        try {

            ventaService.registrarVenta(
                    new ArrayList<>(detallesVenta)
            );


            mostrarInformacion(
                    "Venta registrada correctamente."
            );


            // -------------------------------------------------
            // LIMPIAR VENTA ACTUAL
            // -------------------------------------------------

            detallesVenta.clear();

            actualizarTabla();

            actualizarTotal();


            // -------------------------------------------------
            // RECARGAR PRODUCTOS
            // -------------------------------------------------

            cargarProductos();


            limpiarEntradaProducto();


        } catch (IllegalArgumentException e) {

            mostrarError(
                    e.getMessage()
            );

        } catch (Exception e) {

            mostrarError(
                    "Ocurrió un error al registrar la venta."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // ACTUALIZAR TABLA
    // =========================================================

    private void actualizarTabla() {

        tablaDetalles.setItems(
                FXCollections.observableArrayList(
                        detallesVenta
                )
        );

        tablaDetalles.refresh();
    }


    // =========================================================
    // ACTUALIZAR TOTAL
    // =========================================================

    private void actualizarTotal() {

        BigDecimal total =
                detallesVenta.stream()
                        .map(DetalleVenta::getSubtotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        lblTotal.setText(
                "$ " + formatearPrecio(total)
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

    // =========================================================
    // LIMPIAR CAMPOS
    // =========================================================

    private void limpiarEntradaProducto() {

        cmbProducto
                .getSelectionModel()
                .clearSelection();

        txtCantidad.clear();

        cmbProducto.requestFocus();
    }


    // =========================================================
    // MENSAJE DE ERROR
    // =========================================================

    private void mostrarError(
            String mensaje) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alerta.setTitle("Error");

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }


    // =========================================================
    // MENSAJE DE INFORMACIÓN
    // =========================================================

    private void mostrarInformacion(
            String mensaje) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle("Información");

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}