package com.inventarioropa.controller;

import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

@Component
public class VentaController {

    private final ConfigurableApplicationContext springContext;

    public VentaController(
            ConfigurableApplicationContext springContext) {

        this.springContext = springContext;
    }

    // =========================================================
    // COMPONENTES
    // =========================================================

    @FXML
    private StackPane contenidoVentas;

    @FXML
    private Button btnNuevaVenta;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnEstadisticas;


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @FXML
    public void initialize() {

        mostrarNuevaVenta();
    }


    // =========================================================
    // NUEVA VENTA
    // =========================================================

    @FXML
    private void mostrarNuevaVenta() {

        cargarVista(
                "/view/NuevaVenta.fxml"
        );

        activarBoton(btnNuevaVenta);
    }


    // =========================================================
    // HISTORIAL
    // =========================================================

    @FXML
    private void mostrarHistorial() {

        cargarVista(
                "/view/HistorialVentas.fxml"
        );

        activarBoton(btnHistorial);
    }


    // =========================================================
    // ESTADÍSTICAS
    // =========================================================

    @FXML
    private void mostrarEstadisticas() {

        cargarVista(
                "/view/EstadisticasVentas.fxml"
        );

        activarBoton(btnEstadisticas);
    }


    // =========================================================
    // CARGAR VISTA
    // =========================================================

    private void cargarVista(String ruta) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(ruta)
                    );

            loader.setControllerFactory(
                    springContext::getBean
            );

            Parent vista = loader.load();

            contenidoVentas
                    .getChildren()
                    .setAll(vista);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // BOTÓN ACTIVO
    // =========================================================

    private void activarBoton(Button botonActivo) {

        btnNuevaVenta
                .getStyleClass()
                .remove("btn-active");

        btnHistorial
                .getStyleClass()
                .remove("btn-active");

        btnEstadisticas
                .getStyleClass()
                .remove("btn-active");


        if (!botonActivo
                .getStyleClass()
                .contains("btn-active")) {

            botonActivo
                    .getStyleClass()
                    .add("btn-active");
        }
    }
}