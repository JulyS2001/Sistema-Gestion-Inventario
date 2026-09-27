package com.inventarioropa.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.inventarioropa.model.MovimientoStock;
import com.inventarioropa.model.Producto;
import com.inventarioropa.model.TipoMovimiento;
import com.inventarioropa.service.MovimientoStockService;
import com.inventarioropa.service.ProductoService;
import com.inventarioropa.model.MotivoMovimiento;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

@Component
public class MovimientoStockController {

    private final MovimientoStockService movimientoService;
    private final ProductoService productoService;

    public MovimientoStockController(
            MovimientoStockService movimientoService,
            ProductoService productoService) {

        this.movimientoService = movimientoService;
        this.productoService = productoService;
    }

    @FXML
    private ComboBox<Producto> cmbProducto;

    @FXML
    private ComboBox<TipoMovimiento> cmbTipo;

    @FXML
    private TextField txtCantidad;

    @FXML
    private ComboBox<MotivoMovimiento> cmbMotivo;

    @FXML
    private TableView<MovimientoStock> tablaMovimientos;

    @FXML
    private TableColumn<MovimientoStock, Object> colFecha;

    @FXML
    private TableColumn<MovimientoStock, Producto> colProducto;

    @FXML
    private TableColumn<MovimientoStock, TipoMovimiento> colTipo;

    @FXML
    private TableColumn<MovimientoStock, Integer> colAnterior;

    @FXML
    private TableColumn<MovimientoStock, Integer> colCantidad;

    @FXML
    private TableColumn<MovimientoStock, Integer> colResultante;

    @FXML
    private TableColumn<MovimientoStock, String> colMotivo;


    @FXML
    public void initialize() {

        cargarProductos();

        cargarTipos();
        
        cargarMotivos();

        configurarTabla();

        cargarMovimientos();
    }
    
    private void cargarMotivos() {

        cmbMotivo.setItems(
                FXCollections.observableArrayList(
                        MotivoMovimiento.values()
                )
        );
    }


    private void cargarProductos() {

        List<Producto> productos =
                productoService.listarProductos();

        cmbProducto.setItems(
                FXCollections.observableArrayList(
                        productos
                )
        );
    }


    private void cargarTipos() {

        cmbTipo.setItems(
                FXCollections.observableArrayList(
                        TipoMovimiento.values()
                )
        );
    }


    private void configurarTabla() {

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fecha")
        );

        colProducto.setCellValueFactory(
                new PropertyValueFactory<>("producto")
        );

        colTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipo")
        );

        colAnterior.setCellValueFactory(
                new PropertyValueFactory<>("stockAnterior")
        );

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colResultante.setCellValueFactory(
                new PropertyValueFactory<>("stockResultante")
        );

        colMotivo.setCellValueFactory(
                new PropertyValueFactory<>("motivo")
        );
        
        tablaMovimientos.setColumnResizePolicy(
        	    TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        	);
    }


    private void cargarMovimientos() {

        tablaMovimientos.setItems(
                FXCollections.observableArrayList(
                        movimientoService.listarMovimientos()
                )
        );
    }


    @FXML
    private void registrarMovimiento() {

        try {

            Producto producto =
                    cmbProducto.getValue();

            TipoMovimiento tipo =
                    cmbTipo.getValue();

            String cantidadTexto =
                    txtCantidad.getText().trim();

            MotivoMovimiento motivo =
                    cmbMotivo.getValue();


            if (producto == null) {

                mostrarError(
                        "Seleccioná un producto."
                );

                return;
            }


            if (tipo == null) {

                mostrarError(
                        "Seleccioná un tipo de movimiento."
                );

                return;
            }


            if (cantidadTexto.isEmpty()) {

                mostrarError(
                        "Ingresá una cantidad."
                );

                return;
            }


            if (motivo == null) {

                mostrarError(
                        "Ingresá un motivo."
                );

                return;
            }


            int cantidad =
                    Integer.parseInt(cantidadTexto);


            movimientoService.registrarMovimiento(
                    producto,
                    tipo,
                    cantidad,
                    motivo
            );


            mostrarInformacion(
                    "Movimiento registrado correctamente."
            );


            limpiarFormulario();

            cargarProductos();

            cargarMovimientos();


        } catch (NumberFormatException e) {

            mostrarError(
                    "La cantidad debe ser un número entero."
            );

        } catch (IllegalArgumentException e) {

            mostrarError(
                    e.getMessage()
            );

        } catch (Exception e) {

            mostrarError(
                    "Ocurrió un error al registrar el movimiento."
            );

            e.printStackTrace();
        }
    }


    private void limpiarFormulario() {

        cmbProducto.getSelectionModel().clearSelection();

        cmbTipo.getSelectionModel().clearSelection();

        txtCantidad.clear();

        cmbMotivo.getSelectionModel().clearSelection();
    }


    private void mostrarError(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error");

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }


    private void mostrarInformacion(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle("Información");

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}