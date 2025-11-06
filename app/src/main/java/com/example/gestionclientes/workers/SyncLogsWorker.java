package com.example.gestionclientes.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.gestionclientes.database.AppDatabase;
import com.example.gestionclientes.database.entity.LogApp;
import com.example.gestionclientes.network.RetrofitClient;
import com.example.gestionclientes.utils.LogManager;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class SyncLogsWorker extends Worker {

    private static final String TAG = "SyncLogsWorker";

    public SyncLogsWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d(TAG, "Iniciando sincronización de logs...");

            // 1. Obtener todos los registros de la tabla logs_app
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<LogApp> logs = db.logAppDao().getAllLogs();

            if (logs.isEmpty()) {
                Log.d(TAG, "No hay logs para sincronizar");
                return Result.success();
            }

            Log.d(TAG, "Enviando " + logs.size() + " logs al servidor");

            // 2. Enviar los datos al servidor mediante Retrofit
            Response<ResponseBody> response = RetrofitClient.getApiService()
                    .enviarLogs(logs)
                    .execute();

            if (response.isSuccessful()) {
                Log.d(TAG, "Logs enviados exitosamente");

                // 3. Eliminar los registros una vez confirmada la sincronización
                db.logAppDao().deleteAll();
                Log.d(TAG, "Logs eliminados de la base de datos local");

                // Registrar evento de sincronización exitosa
                LogManager.registrarEvento(
                        getApplicationContext(),
                        "Sincronización exitosa: " + logs.size() + " logs enviados",
                        "SyncLogsWorker"
                );

                return Result.success();
            } else {
                Log.e(TAG, "Error al enviar logs: " + response.code());
                LogManager.registrarError(
                        getApplicationContext(),
                        "Error en sincronización: " + response.code(),
                        "SyncLogsWorker"
                );
                return Result.retry();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error en sincronización de logs", e);
            LogManager.registrarError(
                    getApplicationContext(),
                    "Excepción en sincronización: " + e.getMessage(),
                    "SyncLogsWorker"
            );
            return Result.retry();
        }
    }
}