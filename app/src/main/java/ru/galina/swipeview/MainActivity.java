package ru.galina.swipeview;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomAdapter adapter;
    private List<String> data = new ArrayList<>();
    private float startX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация данных
        for (int i = 1; i <= 20; i++) {
            data.add("Item " + i);
        }

        // Настройка адаптера
        adapter = new CustomAdapter(this, data);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // Swipe to Refresh
        swipeRefreshLayout = findViewById(R.id.main);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                data.add(0, "Item " + System.currentTimeMillis());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }, 1000);
        });

        // Swipe to Delete
        listView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    if (Math.abs(startX - endX) > 100) { // Определение свайпа
                        int position = listView.pointToPosition((int) event.getX(), (int) event.getY());
                        if (position != AdapterView.INVALID_POSITION) {
                            showDeleteDialog(position);
                        }
                    }
                    break;
            }
            return false;
        });
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление элемента " + data.get(position))
                .setMessage("Вы уверены?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    data.remove(position);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}