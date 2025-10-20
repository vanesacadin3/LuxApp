package com.example.trabajofinal.ui.theme.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trabajofinal.R;
import com.example.trabajofinal.ui.theme.adapter.ReadingAdapter;
import com.example.trabajofinal.ui.theme.database.MyDatabaseHelper;
import com.example.trabajofinal.ui.theme.models.Reading;
import com.example.trabajofinal.ui.theme.services.DataUploadService;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ReadingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Cargar datos
        try (MyDatabaseHelper dbHelper = new MyDatabaseHelper(this)) {
            List<Reading> list = dbHelper.getAllReadings();
            adapter = new ReadingAdapter(list);
            rv.setAdapter(adapter);
        }

        // Verificar permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1
                );
            }
        }

        // Botón para enviar los datos (inicia el servicio)
        findViewById(R.id.btnSend).setOnClickListener(v -> {
            Intent intent = new Intent(this, DataUploadService.class);
            startForegroundService(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Actualizar lista al volver a la pantalla
        if (adapter != null) {
            try (MyDatabaseHelper dbHelper = new MyDatabaseHelper(this)) {
                adapter.updateList(dbHelper.getAllReadings());
            }
        }
    }
}
