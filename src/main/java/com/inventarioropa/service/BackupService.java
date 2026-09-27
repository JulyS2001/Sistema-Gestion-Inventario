package com.inventarioropa.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class BackupService {

    private final Path baseDatos = Path.of("inventario.db");
    private final Path carpetaBackups = Path.of("backups");

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public Path crearBackup() throws IOException {

        if (!Files.exists(baseDatos)) {
            throw new IOException(
                    "No se encontró la base de datos: "
                    + baseDatos.toAbsolutePath()
            );
        }

        Files.createDirectories(carpetaBackups);

        String nombreBackup =
                "backup_"
                + LocalDateTime.now().format(formatoFecha)
                + ".db";

        Path destino =
                carpetaBackups.resolve(nombreBackup);

        Files.copy(
                baseDatos,
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        return destino;
    }

    public Path obtenerRutaBaseDatos() {
        return baseDatos.toAbsolutePath();
    }

    public Path obtenerCarpetaBackups() {
        return carpetaBackups.toAbsolutePath();
    }
}