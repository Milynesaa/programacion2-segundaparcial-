package com.example.gestionclientes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.gestionclientes.utils.LogManager;
import com.example.gestionclientes.workers.SyncLogsWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button btnFormularioCliente;
    private Button btnCargaArchivos;
    private Button btnVerLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
        inicializarWorkManager();

        LogManager.registrarEvento(this, "Aplicación iniciada", "MainActivity");
    }

    private void initViews() {
        btnFormularioCliente = findViewById(R.id.btnFormularioCliente);
        btnCargaArchivos = findViewById(R.id.btnCargaArchivos);
        btnVerLogs = findViewById(R.id.btnVerLogs);
    }

    private void setupListeners() {
        btnFormularioCliente.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FormularioClienteActivity.class));
        });

        btnCargaArchivos.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CargaArchivosActivity.class));
        });

        btnVerLogs.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LogsActivity.class));
        });
    }

    private void inicializarWorkManager() {
        // Crear tarea periódica cada 5 minutos (mínimo 15 minutos en producción)
        // Para testing usamos 15 minutos (el mínimo permitido)
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                SyncLogsWorker.class,
                15, TimeUnit.MINUTES // Cambiar a 15 (mínimo real) o usar OneTimeWorkRequest para pruebas
        ).build();

        WorkManager.getInstance(this).enqueue(syncWorkRequest);

        LogManager.registrarEvento(this, "WorkManager inicializado", "MainActivity");
    }
}