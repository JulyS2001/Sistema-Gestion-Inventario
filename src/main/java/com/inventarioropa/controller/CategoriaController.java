package com.inventarioropa.controller;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.inventarioropa.model.Categoria;
import com.inventarioropa.service.CategoriaService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class CategoriaController {

    private final CategoriaService categoriaService;

    private Categoria categoriaEnEdicion;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @FXML
    private TextField txtNombre;

    @FXML
    private Button btnAgregar;

    @FXML
    private TableView<Categoria> tablaCategorias;

    @FXML
    private TableColumn<Categoria, Long> colId;

    @FXML
    private TableColumn<Categoria, String> colNombre;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );
        
        tablaCategorias.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        cargarCategorias();
    }

    private void cargarCategorias() {

        tablaCategorias.setItems(
                FXCollections.observableArrayList(
                        categoriaService.listarCategorias()
                )
        );
    }

    @FXML
    private void agregarCategoria() {

        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {

            mostrarError(
                    "El nombre de la categoría es obligatorio."
            );

            return;
        }

        // Verificar si ya existe
        boolean existe = categoriaService.listarCategorias()
                .stream()
                .anyMatch(categoria ->
                        categoria.getNombre().equalsIgnoreCase(nombre)
                        &&
                        (categoriaEnEdicion == null
                                || !categoria.getId()
                                        .equals(categoriaEnEdicion.getId()))
                );

        if (existe) {

            mostrarError(
                    "Ya existe una categoría con ese nombre."
            );

            return;
        }

        // CREAR
        if (categoriaEnEdicion == null) {

            Categoria categoria = new Categoria();

            categoria.setNombre(nombre);

            categoriaService.guardarCategoria(categoria);

        }

        // EDITAR
        else {

            categoriaEnEdicion.setNombre(nombre);

            categoriaService.guardarCategoria(
                    categoriaEnEdicion
            );

            categoriaEnEdicion = null;

            btnAgregar.setText("+ Agregar");
        }

        txtNombre.clear();

        cargarCategorias();
    }

    @FXML
    private void editarCategoria() {

        Categoria seleccionada =
                tablaCategorias
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mostrarInformacion(
                    "Seleccioná una categoría para editar."
            );

            return;
        }

        categoriaEnEdicion = seleccionada;

        txtNombre.setText(
                seleccionada.getNombre()
        );

        txtNombre.requestFocus();

        txtNombre.selectAll();

        btnAgregar.setText("Guardar cambios");
    }

    @FXML
    private void eliminarCategoria() {

        Categoria seleccionada =
                tablaCategorias
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionada == null) {

            mostrarInformacion(
                    "Seleccioná una categoría para eliminar."
            );

            return;
        }

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Eliminar categoría");

        confirmacion.setHeaderText(
                "¿Querés eliminar esta categoría?"
        );

        confirmacion.setContentText(
                "Categoría: "
                + seleccionada.getNombre()
        );

        Optional<ButtonType> resultado =
                confirmacion.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            try {

                categoriaService.eliminarCategoria(
                        seleccionada.getId()
                );

                cargarCategorias();

            } catch (DataIntegrityViolationException e) {

                mostrarError(
                        "No se puede eliminar esta categoría " +
                        "porque tiene productos asociados."
                );
            }
        }
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