package com.example.trabajofinal.ui.theme.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.trabajofinal.R;
import com.example.trabajofinal.ui.theme.database.MyDatabaseHelper;
import com.example.trabajofinal.ui.theme.models.Reading;
import com.example.trabajofinal.ui.theme.utils.SensorManagerHelper;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManagerHelper sensorHelper;
    private TextView textView;
    private LinearLayout rootLayout;

    private float currentLightValue = 0f;
    private MyDatabaseHelper dbHelper;

    private static final int REQUEST_CODE_NOTIFICATIONS = 100; // 👈 código de solicitud

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        solicitarPermisoNotificaciones();

        inicializarVistas();
        configurarSensor();
        configurarBotones();
    }

   
    private void solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATIONS
                );
            }
        }
    }

    private void inicializarVistas() {
        textView = findViewById(R.id.txtLectura);
        textView.setText(fromHtml(getString(R.string.valor_inicial)));
        rootLayout = findViewById(R.id.root_view);
        dbHelper = new MyDatabaseHelper(this);
    }

    private void configurarSensor() {
        sensorHelper = new SensorManagerHelper(this);
        if (!sensorHelper.isLightSensorAvailable()) {
            textView.setText(R.string.sensor_no_disponible);
        }
    }

    private void configurarBotones() {
        Button btnGuardar = findViewById(R.id.btnRegistrar);
        Button btnHistorial = findViewById(R.id.btnVer);

        btnGuardar.setOnClickListener(v -> {
            Reading lectura = new Reading("", currentLightValue, System.currentTimeMillis());
            long id = dbHelper.addReading(lectura);
            Toast.makeText(
                    this,
                    (id > 0) ? R.string.lectura_guardada : R.string.error_guardar,
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnHistorial.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float valor = event.values[0];
        currentLightValue = valor;

        DecimalFormat formato = new DecimalFormat("0.00");
        String valorFormateado = formato.format(valor);

        String textoHtml = getString(R.string.valor_actual_html, valorFormateado, getDescripcionBrillo(valor));
        textView.setText(fromHtml(textoHtml));

        actualizarColorFondo((int) valor);
    }

    private void actualizarColorFondo(int valor) {
        int bgColor;
        int textColor;

        if (valor == 0) {
            bgColor = Color.BLACK;
            textColor = Color.WHITE;
        } else if (valor <= 10) {
            bgColor = Color.DKGRAY;
            textColor = Color.WHITE;
        } else if (valor <= 50) {
            bgColor = Color.GRAY;
            textColor = Color.WHITE;
        } else if (valor <= 5000) {
            bgColor = Color.WHITE;
            textColor = Color.BLACK;
        } else if (valor <= 25000) {
            bgColor = Color.YELLOW;
            textColor = Color.BLACK;
        } else {
            bgColor = Color.RED;
            textColor = Color.WHITE;
        }

        rootLayout.setBackgroundColor(bgColor);
        textView.setTextColor(textColor);
    }

    private String getDescripcionBrillo(float value) {
        int v = (int) value;
        if (v == 0) return getString(R.string.oscuridad_total);
        else if (v <= 10) return getString(R.string.oscuro);
        else if (v <= 50) return getString(R.string.tenue);
        else if (v <= 5000) return getString(R.string.normal);
        else if (v <= 25000) return getString(R.string.muy_brilante);
        else return getString(R.string.demasiada_luz);
    }

    private Spanned fromHtml(String html) {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorHelper.registerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHelper.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {}

}
