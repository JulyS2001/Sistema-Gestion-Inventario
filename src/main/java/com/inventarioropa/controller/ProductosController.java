package com.inventarioropa.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.inventarioropa.model.Categoria;
import com.inventarioropa.model.Producto;
import com.inventarioropa.service.ProductoService;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
public class ProductosController {

    private final ProductoService productoService;
    private final ConfigurableApplicationContext springContext;

    private FilteredList<Producto> productosFiltrados;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ProductosController(
            ProductoService productoService,
            ConfigurableApplicationContext springContext) {

        this.productoService = productoService;
        this.springContext = springContext;
    }


    // =====================================================
    // BUSCADOR
    // =====================================================

    @FXML
    private TextField txtBuscar;


    // =====================================================
    // TABLA
    // =====================================================

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, Long> colId;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Categoria> colCategoria;

    @FXML
    private TableColumn<Producto, String> colTalle;

    @FXML
    private TableColumn<Producto, String> colColor;

    @FXML
    private TableColumn<Producto, Object> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;


    // =====================================================
    // INICIALIZACIÓN
    // =====================================================

    @FXML
    public void initialize() {

        configurarTablaProductos();

        cargarProductos();

        txtBuscar.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filtrarProductos(newValue)
        );
    }


    // =====================================================
    // CONFIGURAR TABLA
    // =====================================================

    private void configurarTablaProductos() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );

        colTalle.setCellValueFactory(
                new PropertyValueFactory<>("talle")
        );

        colColor.setCellValueFactory(
                new PropertyValueFactory<>("color")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock")
        );

        tablaProductos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
    }


    // =====================================================
    // CARGAR PRODUCTOS
    // =====================================================

    public void cargarProductos() {

        List<Producto> productos =
                productoService.listarProductos();

        productosFiltrados = new FilteredList<>(
                FXCollections.observableArrayList(productos),
                producto -> true
        );

        tablaProductos.setItems(productosFiltrados);
    }


    // =====================================================
    // BUSCAR PRODUCTOS
    // =====================================================

    private void filtrarProductos(String texto) {

        String textoBuscado =
                texto.toLowerCase().trim();

        if (productosFiltrados == null) {
            return;
        }

        productosFiltrados.setPredicate(producto -> {

            if (textoBuscado.isEmpty()) {
                return true;
            }

            return producto.getNombre()
                    .toLowerCase()
                    .contains(textoBuscado)

                    || producto.getTalle()
                    .toLowerCase()
                    .contains(textoBuscado)

                    || producto.getColor()
                    .toLowerCase()
                    .contains(textoBuscado);
        });
    }


    // =====================================================
    // NUEVO PRODUCTO
    // =====================================================

    @FXML
    private void abrirNuevoProducto() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/view/ProductoForm.fxml"
                    )
            );

            loader.setControllerFactory(
                    springContext::getBean
            );

            Parent root = loader.load();

            ProductoFormController controller =
                    loader.getController();

            controller.prepararNuevoProducto();

            controller.setAlGuardar(
                    this::cargarProductos
            );


            javafx.stage.Stage stage =
                    new javafx.stage.Stage();

            stage.setTitle("Nuevo producto");


            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            String css = getClass()
                    .getResource("/css/main.css")
                    .toExternalForm();

            scene.getStylesheets().add(css);

            stage.setScene(scene);
            
            stage.sizeToScene();
            stage.setMinWidth(500);
            stage.setMinHeight(400);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =====================================================
    // EDITAR PRODUCTO
    // =====================================================

    @FXML
    private void editarProducto() {

        Producto productoSeleccionado =
                tablaProductos
                        .getSelectionModel()
                        .getSelectedItem();


        if (productoSeleccionado == null) {

            mostrarInformacion(
                    "Seleccioná un producto para editar."
            );

            return;
        }


        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/view/ProductoForm.fxml"
                    )
            );

            loader.setControllerFactory(
                    springContext::getBean
            );

            Parent root = loader.load();


            ProductoFormController controller =
                    loader.getController();

            controller.setProductoEditar(
                    productoSeleccionado
            );

            controller.setAlGuardar(
                    this::cargarProductos
            );


            javafx.stage.Stage stage =
                    new javafx.stage.Stage();

            stage.setTitle("Editar producto");


            javafx.scene.Scene scene = new javafx.scene.Scene(root);


            String css = getClass()
                    .getResource("/css/main.css")
                    .toExternalForm();

            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.sizeToScene();
            stage.setMinWidth(500);
            stage.setMinHeight(400);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =====================================================
    // ELIMINAR PRODUCTO
    // =====================================================

    @FXML
    private void eliminarProducto() {

        Producto productoSeleccionado =
                tablaProductos
                        .getSelectionModel()
                        .getSelectedItem();


        if (productoSeleccionado == null) {

            mostrarInformacion(
                    "Seleccioná un producto para eliminar."
            );

            return;
        }


        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmacion.setTitle(
                "Eliminar producto"
        );

        confirmacion.setHeaderText(
                "¿Querés eliminar este producto?"
        );

        confirmacion.setContentText(
                "Producto: "
                        + productoSeleccionado.getNombre()
                        + "\nTalle: "
                        + productoSeleccionado.getTalle()
                        + "\nColor: "
                        + productoSeleccionado.getColor()
        );


        Optional<ButtonType> resultado =
                confirmacion.showAndWait();


        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            productoService.eliminarProducto(
                    productoSeleccionado.getId()
            );

            cargarProductos();
        }
    }


    // =====================================================
    // MENSAJE
    // =====================================================

    private void mostrarInformacion(String mensaje) {

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