package com.matrix_maeny.colorpicker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.matrix_maeny.colorpicker.ColorPickerDBHelper;
import com.matrix_maeny.colorpicker.R;
import com.matrix_maeny.colorpicker.adapters.ColorPickerAdapter;
import com.matrix_maeny.colorpicker.models.ColorModel;

import java.util.ArrayList;
import java.util.Objects;

public class SavedDataActivity extends AppCompatActivity implements ColorPickerAdapter.RefreshTheLayout {

    RecyclerView recyclerView;
    ArrayList<ColorModel> list;
    ColorPickerDBHelper dbHelper = null;
    ColorPickerAdapter adapter = null;
    TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_data);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My colors");

        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);

        emptyView.setVisibility(View.GONE);

        list = new ArrayList<>();

        adapter = new ColorPickerAdapter(list, SavedDataActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SavedDataActivity.this));

        fetchSavedData();


    }

    @SuppressLint("NotifyDataSetChanged")
    final void fetchSavedData() {
        dbHelper = new ColorPickerDBHelper(SavedDataActivity.this);
        Cursor cursor = dbHelper.getData();

        if (cursor.getCount() != 0) {
            int position = 0;
            list.clear();
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                int color = cursor.getInt(1);
                String hexCode = cursor.getString(2);
                String argbCode = cursor.getString(3);

                list.add(new ColorModel(position, name, color, hexCode, argbCode));
                position++;
            }
        } else {
            list.clear();
        }

        if(list.size() == 0) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void refreshTheLayout() {
        fetchSavedData();
    }
}