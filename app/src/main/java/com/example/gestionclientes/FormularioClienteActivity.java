package com.example.gestionclientes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.example.gestionclientes.network.RetrofitClient;
import com.example.gestionclientes.utils.LogManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioClienteActivity extends AppCompatActivity {

    private EditText etCi, etNombre, etDireccion, etTelefono;
    private ImageView ivFoto1, ivFoto2, ivFoto3;
    private Button btnCapturarFoto1, btnCapturarFoto2, btnCapturarFoto3, btnEnviar;

    private String currentPhotoPath1, currentPhotoPath2, currentPhotoPath3;
    private int currentPhotoIndex = 0;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_cliente);

        initViews();
        setupLaunchers();
        setupListeners();
    }

    private void initViews() {
        etCi = findViewById(R.id.etCi);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etTelefono = findViewById(R.id.etTelefono);

        ivFoto1 = findViewById(R.id.ivFoto1);
        ivFoto2 = findViewById(R.id.ivFoto2);
        ivFoto3 = findViewById(R.id.ivFoto3);

        btnCapturarFoto1 = findViewById(R.id.btnCapturarFoto1);
        btnCapturarFoto2 = findViewById(R.id.btnCapturarFoto2);
        btnCapturarFoto3 = findViewById(R.id.btnCapturarFoto3);
        btnEnviar = findViewById(R.id.btnEnviar);
    }

    private void setupLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String photoPath = getCurrentPhotoPath();
                        if (photoPath != null) {
                            File file = new File(photoPath);
                            Uri photoUri = Uri.fromFile(file);
                            updateImageView(photoUri);
                        }
                    }
                }
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        dispatchTakePictureIntent();
                    } else {
                        Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupListeners() {
        btnCapturarFoto1.setOnClickListener(v -> {
            currentPhotoIndex = 1;
            checkCameraPermission();
        });

        btnCapturarFoto2.setOnClickListener(v -> {
            currentPhotoIndex = 2;
            checkCameraPermission();
        });

        btnCapturarFoto3.setOnClickListener(v -> {
            currentPhotoIndex = 3;
            checkCameraPermission();
        });

        btnEnviar.setOnClickListener(v -> enviarFormulario());
    }

    private void checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                LogManager.registrarError(this, "Error al crear archivo: " + ex.getMessage(),
                        "FormularioClienteActivity");
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        String currentPhotoPath = image.getAbsolutePath();
        setCurrentPhotoPath(currentPhotoPath);

        return image;
    }

    private void setCurrentPhotoPath(String path) {
        switch (currentPhotoIndex) {
            case 1:
                currentPhotoPath1 = path;
                break;
            case 2:
                currentPhotoPath2 = path;
                break;
            case 3:
                currentPhotoPath3 = path;
                break;
        }
    }

    private String getCurrentPhotoPath() {
        switch (currentPhotoIndex) {
            case 1:
                return currentPhotoPath1;
            case 2:
                return currentPhotoPath2;
            case 3:
                return currentPhotoPath3;
            default:
                return null;
        }
    }

    private void updateImageView(Uri uri) {
        switch (currentPhotoIndex) {
            case 1:
                ivFoto1.setImageURI(uri);
                break;
            case 2:
                ivFoto2.setImageURI(uri);
                break;
            case 3:
                ivFoto3.setImageURI(uri);
                break;
        }
    }

    private void enviarFormulario() {
        String ci = etCi.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (ci.isEmpty() || nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPhotoPath1 == null || currentPhotoPath2 == null || currentPhotoPath3 == null) {
            Toast.makeText(this, "Debe capturar las 3 fotos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Crear JSON con los datos del formulario
            Map<String, String> clienteData = new HashMap<>();
            clienteData.put("ci", ci);
            clienteData.put("nombre", nombre);
            clienteData.put("direccion", direccion);
            clienteData.put("telefono", telefono);

            String jsonString = new Gson().toJson(clienteData);
            RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"), jsonString);

            // Crear MultipartBody.Part para cada foto
            File file1 = new File(currentPhotoPath1);
            RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/jpeg"), file1);
            MultipartBody.Part foto1 = MultipartBody.Part.createFormData("fotoCasa1", file1.getName(), requestFile1);

            File file2 = new File(currentPhotoPath2);
            RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"), file2);
            MultipartBody.Part foto2 = MultipartBody.Part.createFormData("fotoCasa2", file2.getName(), requestFile2);

            File file3 = new File(currentPhotoPath3);
            RequestBody requestFile3 = RequestBody.create(MediaType.parse("image/jpeg"), file3);
            MultipartBody.Part foto3 = MultipartBody.Part.createFormData("fotoCasa3", file3.getName(), requestFile3);

            // Enviar al servidor
            Call<ResponseBody> call = RetrofitClient.getApiService().enviarCliente(jsonBody, foto1, foto2, foto3);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(FormularioClienteActivity.this, "Cliente enviado exitosamente", Toast.LENGTH_SHORT).show();
                        LogManager.registrarEvento(FormularioClienteActivity.this, "Cliente enviado: " + ci, "FormularioClienteActivity");
                        limpiarFormulario();
                    } else {
                        Toast.makeText(FormularioClienteActivity.this, "Error al enviar: " + response.code(), Toast.LENGTH_SHORT).show();
                        LogManager.registrarError(FormularioClienteActivity.this, "Error al enviar cliente: " + response.code(), "FormularioClienteActivity");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(FormularioClienteActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    LogManager.registrarError(FormularioClienteActivity.this, "Error de conexión: " + t.getMessage(), "FormularioClienteActivity");
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error al preparar envío: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            LogManager.registrarError(this, "Error al preparar envío: " + e.getMessage(), "FormularioClienteActivity");
        }
    }

    private void limpiarFormulario() {
        etCi.setText("");
        etNombre.setText("");
        etDireccion.setText("");
        etTelefono.setText("");
        ivFoto1.setImageResource(android.R.drawable.ic_menu_camera);
        ivFoto2.setImageResource(android.R.drawable.ic_menu_camera);
        ivFoto3.setImageResource(android.R.drawable.ic_menu_camera);
        currentPhotoPath1 = null;
        currentPhotoPath2 = null;
        currentPhotoPath3 = null;
    }
}