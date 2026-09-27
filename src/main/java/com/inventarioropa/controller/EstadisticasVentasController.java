package com.inventarioropa.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.inventarioropa.service.VentaService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

@Component
public class EstadisticasVentasController {

    private final VentaService ventaService;


    public EstadisticasVentasController(
            VentaService ventaService) {

        this.ventaService = ventaService;
    }


    // =========================================================
    // CAMPOS
    // =========================================================

    @FXML
    private ComboBox<String> cmbPeriodo;

    @FXML
    private Label lblVentas;

    @FXML
    private Label lblCantidadVentas;

    @FXML
    private Label lblUnidadesVendidas;

    @FXML
    private Label lblTicketPromedio;

    @FXML
    private Label lblResumen;


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @FXML
    public void initialize() {

        cargarPeriodos();

        cmbPeriodo.setValue("Hoy");

        actualizarEstadisticas();

        cmbPeriodo.setOnAction(
                event -> actualizarEstadisticas()
        );
    }


    // =========================================================
    // CARGAR PERÍODOS
    // =========================================================

    private void cargarPeriodos() {

        cmbPeriodo.setItems(
                FXCollections.observableArrayList(
                        "Hoy",
                        "Esta semana",
                        "Este mes"
                )
        );
    }


    // =========================================================
    // ACTUALIZAR ESTADÍSTICAS
    // =========================================================

    private void actualizarEstadisticas() {

        String periodo =
                cmbPeriodo.getValue();

        if (periodo == null) {
            return;
        }


        LocalDateTime desde =
                calcularFechaInicio(periodo);


        // -----------------------------------------------------
        // VENTAS
        // -----------------------------------------------------

        BigDecimal ventas =
                ventaService.calcularVentasDesde(
                        desde
                );


        // -----------------------------------------------------
        // CANTIDAD DE VENTAS
        // -----------------------------------------------------

        long cantidadVentas =
                ventaService.contarVentasDesde(
                        desde
                );


        // -----------------------------------------------------
        // UNIDADES
        // -----------------------------------------------------

        long unidades =
                ventaService.calcularUnidadesVendidasDesde(
                        desde
                );


        // -----------------------------------------------------
        // TICKET PROMEDIO
        // -----------------------------------------------------

        BigDecimal ticketPromedio =
                BigDecimal.ZERO;


        if (cantidadVentas > 0) {

            ticketPromedio =
                    ventas.divide(
                            BigDecimal.valueOf(
                                    cantidadVentas
                            ),
                            2,
                            java.math.RoundingMode.HALF_UP
                    );
        }


        // -----------------------------------------------------
        // MOSTRAR DATOS
        // -----------------------------------------------------

        lblVentas.setText(
                "$ " + formatearPrecio(ventas)
        );


        lblCantidadVentas.setText(
                String.valueOf(
                        cantidadVentas
                )
        );


        lblUnidadesVendidas.setText(
                String.valueOf(
                        unidades
                )
        );


        lblTicketPromedio.setText(
                "$ " + formatearPrecio(
                        ticketPromedio
                )
        );


        lblResumen.setText(
                generarResumen(
                        periodo,
                        ventas,
                        cantidadVentas,
                        unidades
                )
        );
    }


    // =========================================================
    // CALCULAR INICIO DEL PERÍODO
    // =========================================================

    private LocalDateTime calcularFechaInicio(
            String periodo) {

        LocalDate hoy =
                LocalDate.now();


        switch (periodo) {

            case "Hoy":

                return hoy
                        .atStartOfDay();


            case "Esta semana":

                return hoy
                        .with(
                                TemporalAdjusters
                                        .previousOrSame(
                                                DayOfWeek.MONDAY
                                        )
                        )
                        .atStartOfDay();


            case "Este mes":

                return hoy
                        .withDayOfMonth(1)
                        .atStartOfDay();


            default:

                return hoy
                        .atStartOfDay();
        }
    }


    // =========================================================
    // GENERAR RESUMEN
    // =========================================================

    private String generarResumen(
            String periodo,
            BigDecimal ventas,
            long cantidadVentas,
            long unidades) {

        if (cantidadVentas == 0) {

            return "No hay ventas registradas durante "
                    + periodo.toLowerCase()
                    + ".";
        }


        return "Durante "
                + periodo.toLowerCase()
                + " se realizaron "
                + cantidadVentas
                + " ventas, con "
                + unidades
                + " unidades vendidas y una facturación total de "
                + "$ "
                + formatearPrecio(ventas)
                + ".";
    }


    // =========================================================
    // FORMATEAR PRECIO
    // =========================================================

    private String formatearPrecio(
            BigDecimal valor) {

        NumberFormat formato =
                NumberFormat.getNumberInstance(
                        new Locale("es", "AR")
                );

        formato.setMinimumFractionDigits(2);

        formato.setMaximumFractionDigits(2);

        return formato.format(valor);
    }
}