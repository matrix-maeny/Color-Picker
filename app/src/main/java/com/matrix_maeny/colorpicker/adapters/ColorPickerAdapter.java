package com.matrix_maeny.colorpicker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.colorpicker.ColorPickerDBHelper;
import com.matrix_maeny.colorpicker.R;
import com.matrix_maeny.colorpicker.models.ColorModel;
import com.skydoves.colorpickerview.AlphaTileView;

import java.util.ArrayList;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.viewHolder> {

    ArrayList<ColorModel> list;
    Context context;
    RefreshTheLayout refresh;

    public ColorPickerAdapter(ArrayList<ColorModel> list, Context context) {
        this.list = list;
        this.context = context;
        refresh = (RefreshTheLayout) context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_view_model, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        ColorModel model = list.get(position);
        holder.tileView.setBackgroundColor(model.getColor());
        holder.colorName.setText(model.getName());
        holder.hexCodeName.setText(model.getHexCode());
        holder.argbCodeName.setText(model.getArgbCode());

        holder.cardView.setOnClickListener(v -> {
            String name = "Color name : " + model.getName();
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
        });

        holder.cardView.setOnLongClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context, holder.cardView);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.shareColor:
                        Toast.makeText(context, "sharing...", Toast.LENGTH_SHORT).show();
                        startShare(model.getName(), model.getHexCode(), model.getArgbCode(), false);

                        break;
                    case R.id.shareAllColors:
                        Toast.makeText(context, "sharing...", Toast.LENGTH_SHORT).show();
                        startShare(model.getName(), model.getHexCode(), model.getArgbCode(), true);
                        break;

                    case R.id.deleteColor:
                        //delete color
                        deleteColor(model.getName());
                        refresh.refreshTheLayout();
                        Toast.makeText(context, "Color " + model.getName() + " deleted", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.deleteAll:
                        // deleteAll
                        deleteAll();
                        refresh.refreshTheLayout();
                        Toast.makeText(context, "All colors deleted", Toast.LENGTH_SHORT).show();

                        break;
                }

                return true;
            });
            popupMenu.show();

            return true;
        });


    }


    public interface RefreshTheLayout {
        void refreshTheLayout();
    }


    final void startShare(String colorName, String hexCode, String argbCode, boolean shareAll) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");

        String shareText = "";
        if (!shareAll) {
            shareText = "Color name: " + colorName + "\n" + hexCode + "\n" + argbCode;
        } else {

            for (ColorModel model : list) {
                shareText = shareText + "Color name: " + model.getName() + "\n" + model.getHexCode() + "\n" + model.getArgbCode() + "\n\n";
            }

        }
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(intent);

    }

    final void deleteColor(String name) {
        ColorPickerDBHelper dbHelper = new ColorPickerDBHelper(context.getApplicationContext());
        dbHelper.deleteColor(name);
        dbHelper.close();
    }

    final void deleteAll() {
        ColorPickerDBHelper dbHelper = new ColorPickerDBHelper(context.getApplicationContext());
        dbHelper.deleteAll();
        dbHelper.close();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        AlphaTileView tileView;
        CardView cardView;
        TextView colorName, hexCodeName, argbCodeName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tileView = itemView.findViewById(R.id.modelTileView);
            colorName = itemView.findViewById(R.id.modelColorName);
            hexCodeName = itemView.findViewById(R.id.modelHexCode);
            argbCodeName = itemView.findViewById(R.id.modelArgbCode);
        }
    }
}
