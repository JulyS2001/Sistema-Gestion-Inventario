package com.inventarioropa.controller;

import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

@Component
public class MainController {

    private final ConfigurableApplicationContext springContext;

    @FXML
    private StackPane contenidoPrincipal;

    @FXML
    private Label lblTituloVista;

    @FXML
    private Label lblSubtituloVista;


    public MainController(
            ConfigurableApplicationContext springContext) {

        this.springContext = springContext;
    }


    @FXML
    public void initialize() {

        abrirInicio();
    }


    // =====================================================
    // NAVEGACIÓN
    // =====================================================

    @FXML
    private void abrirInicio() {

        cargarVista(
                "/view/Dashboard.fxml",
                "Dashboard",
                "Resumen general del inventario"
        );
    }


    @FXML
    private void abrirProductos() {

        cargarVista(
                "/view/Productos.fxml",
                "Productos",
                "Gestioná los productos y el stock"
        );
    }


    @FXML
    private void abrirCategorias() {

        cargarVista(
                "/view/Categoria.fxml",
                "Categorías",
                "Gestioná las categorías de productos"
        );
    }


    @FXML
    private void abrirMovimientos() {

        cargarVista(
                "/view/MovimientoStock.fxml",
                "Movimientos de stock",
                "Consultá y registrá movimientos de inventario"
        );
    }


    @FXML
    private void abrirVentas() {

        cargarVista(
                "/view/Venta.fxml",
                "Ventas",
                "Registrá nuevas ventas y consultá el historial"
        );
    }
    
    @FXML
    private void abrirConfiguracion() {

        cargarVista(
                "/view/Configuracion.fxml",
                "Configuración",
                "Administrá la base de datos y las copias de seguridad"
        );
    }


    // =====================================================
    // CARGAR VISTA
    // =====================================================

    private void cargarVista(
            String ruta,
            String titulo,
            String subtitulo) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(ruta)
            );

            loader.setControllerFactory(
                    springContext::getBean
            );

            Parent vista = loader.load();

            contenidoPrincipal
                    .getChildren()
                    .setAll(vista);

            lblTituloVista.setText(titulo);
            lblSubtituloVista.setText(subtitulo);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}