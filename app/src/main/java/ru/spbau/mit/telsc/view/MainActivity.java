package ru.spbau.mit.telsc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import ly.img.android.PESDK;
import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;
import ru.spbau.mit.telsc.model.Sticker;

import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class MainActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    private static final int IMAGE_DOWNLOADED = 2;

    private Sticker sticker;
    private ArrayList<Sticker.Actions> savedTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button chooseImageButton = findViewById(R.id.selectImage);
        chooseImageButton.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        final Button uploadStickerButton = findViewById(R.id.uploadSticker);
        uploadStickerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PhoneActivity.class);
            intent.putExtra("stickerName", saveStickerInFile(sticker.getStickerImage()));
            startActivity(intent);
        });

        final Button stickersDatabaseButton = findViewById(R.id.stickersDatabase);
        stickersDatabaseButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DatabaseActivity.class);
            intent.putExtra("stickerName", saveStickerInFile(sticker.getStickerImage()));
            startActivityForResult(intent, IMAGE_DOWNLOADED);
        });

        final Button grayScalingFilter = findViewById(R.id.edit);
        grayScalingFilter.setOnClickListener(this::showPopupMenu);

        final Button templatesDatabase = findViewById(R.id.templatesDatabase);
        templatesDatabase.setOnClickListener(v -> {
                /*savedTemplate = sticker.getTemplate()*/
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            try {
                if (data == null)
                    return;

                final Uri imageUri = data.getData();

                Intent intent = new Intent(this, ImageEditorActivity.class);
                //intent.putExtra("pathToImage", getRealPathFromURI(this, imageUri));
                startActivity(intent);
                if (imageUri == null)
                    return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
/*
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = Bitmap.createScaledBitmap(selectedImage, 512, 512, false);
                ((ImageView) findViewById(R.id.stickerImageView)).setImageBitmap(selectedImage);
                sticker = new Sticker(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }

        if (requestCode == IMAGE_DOWNLOADED){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput("newSticker"));
                final ImageView stickerImageView = findViewById(R.id.stickerImageView);
                stickerImageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "Technical difficulties occur.Please reload the sticker.", Toast.LENGTH_LONG).show();
            }
        }*/

    }

    private String saveStickerInFile(Bitmap bitmap) {
        String fileName = "sticker";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private void updateImageView() {
        final ImageView stickerImageView = (ImageView) findViewById(R.id.stickerImageView);
        stickerImageView.setImageBitmap(sticker.getStickerImage());
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu);

        popupMenu.setOnMenuItemClickListener(item -> {
            Sticker.Actions action = null;

            switch (item.getItemId()) {
                case R.id.loadTemplate:
                    ArrayList<Sticker.Actions> loadedTemplate = savedTemplate;
                    sticker.applyActions(loadedTemplate);
                    break;
                case R.id.line:
                    //Drawing Line Mode
                    break;
                case R.id.crop:
                    //Crop Mode
                    break;
                case R.id.eraser:
                    //Eraser Mode
                    break;
                case R.id.rotate:
                    action = Sticker.Actions.ROTATION_90_DEGREES_CLOCKWISE;
                    break;
                // Filters part
                case R.id.grayScaling:
                    action = Sticker.Actions.GRAY_SCALING;
                    break;
                case R.id.blur:
                    //Blur filter
                    break;
                case R.id.gauss:
                    //Gauss filter
                    break;
                default:
                    return false;
            }
            if (action != null)
                sticker.applyAction(action);
            updateImageView();
            return true;
        });

        popupMenu.show();
    }
}
