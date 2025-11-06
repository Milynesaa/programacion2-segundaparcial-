package com.example.gestionclientes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestionclientes.adapters.LogsAdapter;
import com.example.gestionclientes.database.AppDatabase;
import com.example.gestionclientes.database.entity.LogApp;

import java.util.ArrayList;
import java.util.List;

public class LogsActivity extends AppCompatActivity {

    private TextView tvTotalLogs;
    private RecyclerView rvLogs;
    private Button btnActualizar;
    private Button btnLimpiarLogs;

    private LogsAdapter adapter;
    private List<LogApp> logsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        initViews();
        setupRecyclerView();
        setupListeners();
        cargarLogs();
    }

    private void initViews() {
        tvTotalLogs = findViewById(R.id.tvTotalLogs);
        rvLogs = findViewById(R.id.rvLogs);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnLimpiarLogs = findViewById(R.id.btnLimpiarLogs);
    }

    private void setupRecyclerView() {
        adapter = new LogsAdapter(logsList);
        rvLogs.setLayoutManager(new LinearLayoutManager(this));
        rvLogs.setAdapter(adapter);
    }

    private void setupListeners() {
        btnActualizar.setOnClickListener(v -> cargarLogs());

        btnLimpiarLogs.setOnClickListener(v -> limpiarLogs());
    }

    private void cargarLogs() {
        new Thread(() -> {
            List<LogApp> logs = AppDatabase.getInstance(this).logAppDao().getAllLogs();

            runOnUiThread(() -> {
                logsList.clear();
                logsList.addAll(logs);
                adapter.notifyDataSetChanged();
                tvTotalLogs.setText("Total de logs: " + logs.size());
            });
        }).start();
    }

    private void limpiarLogs() {
        new Thread(() -> {
            AppDatabase.getInstance(this).logAppDao().deleteAll();

            runOnUiThread(() -> {
                Toast.makeText(this, "Logs eliminados", Toast.LENGTH_SHORT).show();
                cargarLogs();
            });
        }).start();
    }
}