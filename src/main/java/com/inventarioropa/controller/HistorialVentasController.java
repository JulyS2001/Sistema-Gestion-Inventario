package com.inventarioropa.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.inventarioropa.model.Venta;
import com.inventarioropa.service.VentaService;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class HistorialVentasController {

    private final VentaService ventaService;
    private final ConfigurableApplicationContext springContext;

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HistorialVentasController(
            VentaService ventaService,
            ConfigurableApplicationContext springContext) {

        this.ventaService = ventaService;
        this.springContext = springContext;
    }


    // =========================================================
    // CAMPOS
    // =========================================================

    @FXML
    private TableView<Venta> tablaVentas;

    @FXML
    private TableColumn<Venta, Long> colVentaId;

    @FXML
    private TableColumn<Venta, String> colVentaFecha;

    @FXML
    private TableColumn<Venta, BigDecimal> colVentaTotal;

    @FXML
    private TableColumn<Venta, Void> colVentaAcciones;


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @FXML
    public void initialize() {

        configurarTabla();

        cargarVentas();
    }


    // =========================================================
    // CONFIGURAR TABLA
    // =========================================================

    private void configurarTabla() {

        // -----------------------------------------------------
        // NÚMERO DE VENTA
        // -----------------------------------------------------

        colVentaId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );


        // -----------------------------------------------------
        // FECHA
        // -----------------------------------------------------

        colVentaFecha.setCellValueFactory(venta -> {

            if (venta.getValue().getFecha() == null) {

                return new ReadOnlyStringWrapper("");
            }

            return new ReadOnlyStringWrapper(
                    venta.getValue()
                            .getFecha()
                            .format(formatoFecha)
            );
        });


        // -----------------------------------------------------
        // TOTAL
        // -----------------------------------------------------

        colVentaTotal.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );


        colVentaTotal.setCellFactory(column ->
                new TableCell<Venta, BigDecimal>() {

                    @Override
                    protected void updateItem(
                            BigDecimal total,
                            boolean empty) {

                        super.updateItem(
                                total,
                                empty
                        );

                        if (empty || total == null) {

                            setText(null);

                        } else {

                            setText(
                                    "$ "
                                    + formatearPrecio(total)
                            );
                        }
                    }
                }
        );


        // -----------------------------------------------------
        // BOTÓN VER DETALLE
        // -----------------------------------------------------

        colVentaAcciones.setCellFactory(column ->
                new TableCell<Venta, Void>() {

                    private final Button btnVer =
                            new Button("Ver detalle");

                    {

                        btnVer.getStyleClass()
                                .add("btn-detail");

                        btnVer.setOnAction(event -> {

                            Venta venta =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            abrirDetalleVenta(venta);
                        });
                    }


                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {

                            setGraphic(null);

                        } else {

                            setGraphic(btnVer);
                        }
                    }
                }
        );


        // -----------------------------------------------------
        // AJUSTAR COLUMNAS
        // -----------------------------------------------------

        tablaVentas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
    }


    // =========================================================
    // CARGAR VENTAS
    // =========================================================

    private void cargarVentas() {

        List<Venta> ventas =
                ventaService.listarVentas();

        tablaVentas.setItems(
                FXCollections.observableArrayList(
                        ventas
                )
        );
    }


    // =========================================================
    // ABRIR DETALLE DE VENTA
    // =========================================================

    private void abrirDetalleVenta(Venta venta) {

        try {

            // -------------------------------------------------
            // BUSCAR LA VENTA COMPLETA
            // -------------------------------------------------

            Venta ventaCompleta =
                    ventaService.buscarVentaConDetalles(
                            venta.getId()
                    );

            if (ventaCompleta == null) {

                mostrarError(
                        "No se encontró la venta."
                );

                return;
            }


            // -------------------------------------------------
            // CARGAR FXML
            // -------------------------------------------------

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/view/VentaDetalle.fxml"
                            )
                    );

            loader.setControllerFactory(
                    springContext::getBean
            );

            Parent root = loader.load();


            // -------------------------------------------------
            // OBTENER CONTROLLER
            // -------------------------------------------------

            VentaDetalleController controller =
                    loader.getController();


            // -------------------------------------------------
            // PASAR LA VENTA
            // -------------------------------------------------

            controller.cargarVenta(
                    ventaCompleta
            );


            // -------------------------------------------------
            // CREAR VENTANA
            // -------------------------------------------------

            Stage stage =
                    new Stage();

            stage.setTitle(
                    "Detalle de venta #"
                            + ventaCompleta.getId()
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );


            // -------------------------------------------------
            // CREAR SCENE Y CARGAR CSS
            // -------------------------------------------------

            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    getClass()
                            .getResource("/css/main.css")
                            .toExternalForm()
            );

            stage.setScene(scene);


            // -------------------------------------------------
            // TAMAÑO
            // -------------------------------------------------

            stage.setMinWidth(700);
            stage.setMinHeight(450);


            // -------------------------------------------------
            // MOSTRAR
            // -------------------------------------------------

            stage.showAndWait();


        } catch (IOException e) {

            mostrarError(
                    "No se pudo abrir el detalle de la venta."
            );

            e.printStackTrace();
        }
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
    // MOSTRAR ERROR
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
}