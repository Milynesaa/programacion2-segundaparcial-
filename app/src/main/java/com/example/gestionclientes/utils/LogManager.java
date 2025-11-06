package com.example.gestionclientes.utils;

import android.content.Context;

import com.example.gestionclientes.database.AppDatabase;
import com.example.gestionclientes.database.entity.LogApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogManager {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void registrarError(Context context, String descripcion, String clase) {
        executor.execute(() -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String fechaHora = sdf.format(new Date());

                LogApp log = new LogApp(fechaHora, descripcion, clase);
                AppDatabase.getInstance(context).logAppDao().insert(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void registrarEvento(Context context, String descripcion, String clase) {
        registrarError(context, descripcion, clase);
    }
}