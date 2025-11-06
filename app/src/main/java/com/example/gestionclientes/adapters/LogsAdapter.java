package com.example.gestionclientes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionclientes.R;
import com.example.gestionclientes.database.entity.LogApp;

import java.util.List;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogViewHolder> {

    private List<LogApp> logs;

    public LogsAdapter(List<LogApp> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogApp log = logs.get(position);
        holder.tvFechaHora.setText(log.getFechaHora());
        holder.tvDescripcion.setText(log.getDescripcionError());
        holder.tvClase.setText(log.getClaseOrigen());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaHora;
        TextView tvDescripcion;
        TextView tvClase;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaHora = itemView.findViewById(R.id.tvFechaHora);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvClase = itemView.findViewById(R.id.tvClase);
        }
    }
}