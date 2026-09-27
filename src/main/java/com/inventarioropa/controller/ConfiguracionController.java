package com.inventarioropa.controller;


import java.io.IOException;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.inventarioropa.service.BackupService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

@Component
public class ConfiguracionController {

    private final BackupService backupService;

    public ConfiguracionController(
            BackupService backupService) {

        this.backupService = backupService;
    }

    @FXML
    private Label lblRutaBaseDatos;

    @FXML
    public void initialize() {

        lblRutaBaseDatos.setText(
                backupService
                        .obtenerRutaBaseDatos()
                        .toString()
        );
    }

    @FXML
    private void crearBackup() {

        try {

            Path backup =
                    backupService.crearBackup();

            mostrarInformacion(
                    "Backup realizado correctamente.",
                    "Se creó la copia de seguridad:\n\n"
                    + backup
            );

        } catch (IOException e) {

            mostrarError(
                    "No se pudo realizar el backup.\n\n"
                    + e.getMessage()
            );
        }
    }

    @FXML
    private void abrirCarpetaBackups() {

        try {

            Path carpeta =
                    backupService
                            .obtenerCarpetaBackups();

            java.nio.file.Files.createDirectories(carpeta);

            new ProcessBuilder(
                    "explorer.exe",
                    carpeta.toAbsolutePath().toString()
            ).start();

        } catch (IOException e) {

            mostrarError(
                    "No se pudo abrir la carpeta de backups.\n\n"
                    + e.getMessage()
            );
        }
    }
    private void mostrarInformacion(
            String titulo,
            String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.INFORMATION);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {

        Alert alerta =
                new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}