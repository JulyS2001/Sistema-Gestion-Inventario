package com.inventarioropa.controller;

import java.math.BigDecimal;

import com.inventarioropa.model.Categoria;

import org.springframework.stereotype.Component;

import com.inventarioropa.model.Producto;
import com.inventarioropa.service.ProductoService;
import com.inventarioropa.service.CategoriaService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import javafx.scene.control.Label;

@Component
public class ProductoFormController {

	private final ProductoService productoService;
	private final CategoriaService categoriaService;

    private Runnable alGuardar;
    
    private Producto productoEditar;

    public ProductoFormController(
            ProductoService productoService,
            CategoriaService categoriaService) {

        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }
    
    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtNombre;
    
    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private TextField txtTalle;

    @FXML
    private TextField txtColor;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;
    
    @FXML
    public void initialize() {

        cmbCategoria.setItems(
                FXCollections.observableArrayList(
                        categoriaService.listarCategorias()
                )
        );
    }

    public void setAlGuardar(Runnable alGuardar) {
        this.alGuardar = alGuardar;
    }
    
    public void prepararNuevoProducto() {

        productoEditar = null;

        lblTitulo.setText("Nuevo producto");

        txtNombre.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtTalle.clear();
        txtColor.clear();
        txtPrecio.clear();
        txtStock.clear();
    }
    
    public void setProductoEditar(Producto producto) {

        this.productoEditar = producto;

        lblTitulo.setText("Editar producto");

        txtNombre.setText(
                producto.getNombre()
        );

        cmbCategoria.setValue(
                producto.getCategoria()
        );

        txtTalle.setText(
                producto.getTalle()
        );

        txtColor.setText(
                producto.getColor()
        );

        txtPrecio.setText(
                producto.getPrecio().toString()
        );

        txtStock.setText(
                producto.getStock().toString()
        );
    }

    @FXML
    private void guardarProducto() {

        try {

            String nombre = txtNombre.getText().trim();

            String talle = txtTalle.getText().trim();

            String color = txtColor.getText().trim();

            String precioTexto = txtPrecio.getText().trim();

            String stockTexto = txtStock.getText().trim();

            Categoria categoriaSeleccionada =
                    cmbCategoria.getValue();


            // Validar campos vacíos

            if (nombre.isEmpty() ||
                talle.isEmpty() ||
                color.isEmpty() ||
                precioTexto.isEmpty() ||
                stockTexto.isEmpty()) {

                mostrarError(
                        "Todos los campos son obligatorios."
                );

                return;
            }


            // Validar categoría

            if (categoriaSeleccionada == null) {

                mostrarError(
                        "Seleccioná una categoría."
                );

                return;
            }


            // Convertir precio

            BigDecimal precio =
                    new BigDecimal(precioTexto);


            // Convertir stock

            int stock =
                    Integer.parseInt(stockTexto);


            // Validar precio

            if (precio.compareTo(BigDecimal.ZERO) <= 0) {

                mostrarError(
                        "El precio debe ser mayor a 0."
                );

                return;
            }


            // Validar stock

            if (stock < 0) {

                mostrarError(
                        "El stock no puede ser negativo."
                );

                return;
            }


            // Si es nuevo, crear producto

            if (productoEditar == null) {

                productoEditar = new Producto();
                
            	System.out.println(
            		    "GUARDANDO -> ID: " + productoEditar.getId()
            		);
            }


            // Cargar datos

            productoEditar.setNombre(nombre);

            productoEditar.setCategoria(
                    categoriaSeleccionada
            );

            productoEditar.setTalle(talle);

            productoEditar.setColor(color);

            productoEditar.setPrecio(precio);

            productoEditar.setStock(stock);
            
            System.out.println(
            	    "GUARDANDO -> ID: " + productoEditar.getId()
            	);


            // Guardar

            productoService.guardarProducto(
                    productoEditar
            );


            if (alGuardar != null) {

                alGuardar.run();
            }


            cerrarVentana();


        } catch (NumberFormatException e) {

            mostrarError(
                    "Precio o stock tienen un formato incorrecto."
            );

        } catch (Exception e) {

            mostrarError(
                    "Ocurrió un error al guardar el producto."
            );

            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}