package com.example.trabajofinal.ui.theme.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trabajofinal.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.example.trabajofinal.ui.theme.models.Reading;

public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.ViewHolder> {

    private final List<Reading> readings;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    public ReadingAdapter(List<Reading> readings) {
        this.readings = readings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reading, parent, false);
        return new ViewHolder(v);
    }
    public void updateList(List<Reading> newList) {
        this.readings.clear();
        this.readings.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reading r = readings.get(position);
        holder.tvValor.setText(String.format(Locale.getDefault(), "%.2f lx", r.getValue()));
        holder.tvFecha.setText(sdf.format(r.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return readings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvValor, tvFecha;

        ViewHolder(View itemView) {
            super(itemView);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
