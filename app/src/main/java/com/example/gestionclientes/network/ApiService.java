package com.example.gestionclientes.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    // Requerimiento 1: Envío de formulario con fotos
    @Multipart
    @POST("your-unique-webhook-id") // Cambiar por tu ID de webhook.site
    Call<ResponseBody> enviarCliente(
            @Part("data") RequestBody jsonData,
            @Part MultipartBody.Part fotoCasa1,
            @Part MultipartBody.Part fotoCasa2,
            @Part MultipartBody.Part fotoCasa3
    );

    // Requerimiento 2: Envío de archivos comprimidos
    @Multipart
    @POST("your-unique-webhook-id") // Cambiar por tu ID de webhook.site
    Call<ResponseBody> enviarArchivosZip(
            @Part("ci") RequestBody ci,
            @Part MultipartBody.Part archivoZip
    );

    // Requerimiento 4: Envío de logs
    @POST("your-unique-webhook-id") // Cambiar por tu ID de webhook.site
    Call<ResponseBody> enviarLogs(@Body List<com.example.gestionclientes.database.entity.LogApp> logs);
}