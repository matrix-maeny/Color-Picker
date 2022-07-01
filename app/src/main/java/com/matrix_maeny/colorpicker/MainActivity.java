package com.matrix_maeny.colorpicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix_maeny.colorpicker.activities.AboutActivity;
import com.matrix_maeny.colorpicker.activities.SavedDataActivity;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1;

    Button saveBtn;
    ColorPickerView colorPicker;
    AlphaSlideBar alphaSlideBar;
    BrightnessSlideBar brightnessSlideBar;
    AlphaTileView tileView;
    TextView hexCodeView, argbCodeView, colorHeading;
    EditText enteredColorName;

    int IMAGE_CODE = 1;
    String colorName, hexCode = "Hex : FFFFFFFF", argbCode = "ARGB : (255,255,255,255)", imageUri = "";
    int pointX = 0, pointY = 0;
    int colorCode = -1;

    boolean isUpdating = false, isSaved = false;
    boolean isPressed = false;


    ColorPickerDBHelper dbHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestStoragePermission();
        }

        saveBtn = findViewById(R.id.saveBtn);
        colorPicker = findViewById(R.id.colorPicker);
        alphaSlideBar = findViewById(R.id.alpha1);
        brightnessSlideBar = findViewById(R.id.brightnesBar);
        tileView = findViewById(R.id.tileView);
        hexCodeView = findViewById(R.id.hexCodeView);
        argbCodeView = findViewById(R.id.rgbCodeView);
        colorHeading = findViewById(R.id.colorName);
        enteredColorName = findViewById(R.id.enterColorName);

        hexCodeView.setText(hexCode);
        argbCodeView.setText(argbCode);
        colorHeading.setVisibility(View.GONE);
        enteredColorName.setVisibility(View.VISIBLE);


        colorPicker.attachAlphaSlider(alphaSlideBar);
        colorPicker.attachBrightnessSlider(brightnessSlideBar);
        colorPicker.setColorListener(envelopeListener);
        saveBtn.setOnClickListener(saveOnClickListener);


    }

    ColorEnvelopeListener envelopeListener = new ColorEnvelopeListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
            if (fromUser) {
                tileView.setBackgroundColor(envelope.getColor());
                hexCode = envelope.getHexCode();
                colorCode = envelope.getColor();
                hexCode = "Hex : " + hexCode;
                hexCodeView.setText(hexCode);

                int[] rgb = envelope.getArgb();
                argbCode = "(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + "," + rgb[3] + ")";
                argbCode = "ARGB : " + argbCode;
                argbCodeView.setText(argbCode);

                pointX = colorPicker.getSelectedPoint().x;
                pointY = colorPicker.getSelectedPoint().y;
                isSaved = false;
                isPressed = false;

            }
        }
    };

    View.OnClickListener saveOnClickListener = v -> {
        // save code

        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            checkAndSaveColor();
        } else {
            requestStoragePermission();
        }
    };


    final void requestStoragePermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("Storage permission needed to WRITE colors")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    final void pickFromGallery() {

        Intent photoPickerIntent = new Intent();
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");

        startActivityForResult(photoPickerIntent, IMAGE_CODE);
    }

    final void setTheWheelImage() {
        colorPicker.setPaletteDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.wheel));
    }

    final void checkAndSaveColor() {

        try {
            colorName = enteredColorName.getText().toString().trim();

            if (colorName.equals("")) {
                tempToast("Please enter color name", 0);
                return;
            }
            if (colorName.length() > 20) {
                tempToast("Color name should not exceed 20 characters", 1);
                return;
            }


            saveColor(colorName, colorCode, hexCode, argbCode, imageUri, pointX, pointY);

        } catch (Exception e) {
            tempToast("Please enter color name", 0);
        }


    }

    final void saveColor(String name, int color, String hexCode, String argbCode, String imageUri, int pointX, int pointY) {

        dbHelper = new ColorPickerDBHelper(MainActivity.this);

        if (dbHelper.insertColor(name, color, hexCode, argbCode, imageUri, pointX, pointY)) {
            tempToast("New color saved", 0);
            isSaved = true;
        } else {
            // error saving color data

            if (dbHelper.updateColor(name, color, hexCode, argbCode, imageUri, pointX, pointY)) {
                tempToast("Color updated", 0);
                isSaved = true;
            } else {
                tempToast("Error saving color", 1);
            }
        }
        dbHelper.close();

    }

    final void tempToast(String m, int time) {
        if (time == 0) {
            Toast.makeText(MainActivity.this, m, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, m, Toast.LENGTH_LONG).show();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                final Uri tempImageUri = data.getData();
                imageUri = tempImageUri.toString();

                final InputStream imageStream = getContentResolver().openInputStream(tempImageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Drawable drawable = new BitmapDrawable(getResources(), selectedImage);
                colorPicker.setPaletteDrawable(drawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length <=0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                tempToast("Permission DENIED.. please ENABLE manually",1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isUpdating) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.toolbar_menu, menu);
            return super.onCreateOptionsMenu(menu);
        } else return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.pickFromGallery:
                // pick from gallery
                pickFromGallery();
                break;
            case R.id.pickFromWheel:
                setTheWheelImage();
                break;
            case R.id.savedColors:
                startActivity(new Intent(MainActivity.this, SavedDataActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isSaved) {
            super.onBackPressed();
        } else {
            if (isPressed) {
                super.onBackPressed();
            } else {
                tempToast("Press again to exit", 1);
                isPressed = true;
            }
        }
    }
}