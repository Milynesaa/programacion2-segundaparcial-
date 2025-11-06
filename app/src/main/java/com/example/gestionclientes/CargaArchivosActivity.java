package com.example.gestionclientes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestionclientes.network.RetrofitClient;
import com.example.gestionclientes.utils.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CargaArchivosActivity extends AppCompatActivity {

    private EditText etCiCliente;
    private Button btnSeleccionarArchivos;
    private TextView tvArchivosSeleccionados;
    private Button btnEnviarArchivos;

    private List<Uri> archivosSeleccionados = new ArrayList<>();
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_archivos);

        initViews();
        setupLauncher();
        setupListeners();
    }

    private void initViews() {
        etCiCliente = findViewById(R.id.etCiCliente);
        btnSeleccionarArchivos = findViewById(R.id.btnSeleccionarArchivos);
        tvArchivosSeleccionados = findViewById(R.id.tvArchivosSeleccionados);
        btnEnviarArchivos = findViewById(R.id.btnEnviarArchivos);
    }

    private void setupLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        archivosSeleccionados.clear();

                        if (data.getClipData() != null) {
                            // Múltiples archivos
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri uri = clipData.getItemAt(i).getUri();
                                archivosSeleccionados.add(uri);
                            }
                        } else if (data.getData() != null) {
                            // Un solo archivo
                            archivosSeleccionados.add(data.getData());
                        }

                        actualizarListaArchivos();
                    }
                }
        );
    }

    private void setupListeners() {
        btnSeleccionarArchivos.setOnClickListener(v -> seleccionarArchivos());
        btnEnviarArchivos.setOnClickListener(v -> enviarArchivos());
    }

    private void seleccionarArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private void actualizarListaArchivos() {
        StringBuilder sb = new StringBuilder();
        sb.append("Archivos seleccionados (").append(archivosSeleccionados.size()).append("):\n\n");

        for (Uri uri : archivosSeleccionados) {
            String fileName = getFileName(uri);
            sb.append("• ").append(fileName).append("\n");
        }

        tvArchivosSeleccionados.setText(sb.toString());
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void enviarArchivos() {
        String ci = etCiCliente.getText().toString().trim();

        if (ci.isEmpty()) {
            Toast.makeText(this, "Ingrese el CI del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (archivosSeleccionados.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Comprimiendo archivos...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                File zipFile = crearArchivoZip();

                runOnUiThread(() -> {
                    if (zipFile != null) {
                        enviarZipAlServidor(ci, zipFile);
                    } else {
                        Toast.makeText(this, "Error al crear archivo ZIP", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    LogManager.registrarError(this, "Error al comprimir archivos: " + e.getMessage(),
                            "CargaArchivosActivity");
                });
            }
        }).start();
    }

    private File crearArchivoZip() {
        try {
            File zipFile = new File(getCacheDir(), "archivos_cliente_" + System.currentTimeMillis() + ".zip");
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));

            for (Uri uri : archivosSeleccionados) {
                String fileName = getFileName(uri);
                InputStream inputStream = getContentResolver().openInputStream(uri);

                if (inputStream != null) {
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    inputStream.close();
                    zos.closeEntry();
                }
            }

            zos.close();
            return zipFile;

        } catch (Exception e) {
            LogManager.registrarError(this, "Error al crear ZIP: " + e.getMessage(), "CargaArchivosActivity");
            return null;
        }
    }

    private void enviarZipAlServidor(String ci, File zipFile) {
        RequestBody ciBody = RequestBody.create(MediaType.parse("text/plain"), ci);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/zip"), zipFile);
        MultipartBody.Part zipPart = MultipartBody.Part.createFormData("archivo", zipFile.getName(), requestFile);

        Call<ResponseBody> call = RetrofitClient.getApiService().enviarArchivosZip(ciBody, zipPart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CargaArchivosActivity.this, "Archivos enviados exitosamente", Toast.LENGTH_SHORT).show();
                    LogManager.registrarEvento(CargaArchivosActivity.this,
                            "Archivos enviados para CI: " + ci, "CargaArchivosActivity");
                    limpiarFormulario();
                } else {
                    Toast.makeText(CargaArchivosActivity.this, "Error al enviar: " + response.code(), Toast.LENGTH_SHORT).show();
                    LogManager.registrarError(CargaArchivosActivity.this,
                            "Error al enviar archivos: " + response.code(), "CargaArchivosActivity");
                }

                // Eliminar archivo temporal
                zipFile.delete();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CargaArchivosActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                LogManager.registrarError(CargaArchivosActivity.this,
                        "Error de conexión: " + t.getMessage(), "CargaArchivosActivity");
                zipFile.delete();
            }
        });
    }

    private void limpiarFormulario() {
        etCiCliente.setText("");
        archivosSeleccionados.clear();
        tvArchivosSeleccionados.setText("No hay archivos seleccionados");
    }
}