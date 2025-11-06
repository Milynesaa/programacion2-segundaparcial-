package com.example.gestionclientes.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.gestionclientes.database.entity.LogApp;

import java.util.List;

@Dao
public interface LogAppDao {

    @Insert
    void insert(LogApp logApp);

    @Query("SELECT * FROM logs_app ORDER BY id DESC")
    List<LogApp> getAllLogs();

    @Query("DELETE FROM logs_app")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM logs_app")
    int getLogCount();
}