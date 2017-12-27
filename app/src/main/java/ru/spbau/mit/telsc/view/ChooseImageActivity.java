package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.model.Sticker;

import static ru.spbau.mit.telsc.view.MainActivity.PICK_IMAGE;

public class ChooseImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);

        setOnClickListeners();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();

            Intent intent = new Intent(this, ImageEditorActivity.class);
            intent.putExtra("pathToImage", Application.getRealPathFromURI(this, imageUri));
            startActivity(intent);
        }
    }

    private void setOnClickListeners() {
        setCameraOnClickListener();
        setDatabaseOnClickListener();
        setGalleryOnClickListener();
        setEmptyOnClickListener();
    }

    private void setGalleryOnClickListener() {
        final Button chooseImageButton = findViewById(R.id.galleryImageButton);
        chooseImageButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });
    }

    private void setDatabaseOnClickListener() {
        final Button chooseImageButton = findViewById(R.id.databaseImageButton);
        chooseImageButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, StickerNameDownloadActivity.class);
            startActivity(intent);
        });
    }

    private void setCameraOnClickListener() {
        final Button chooseImageButton = findViewById(R.id.cameraImageButton);
        chooseImageButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, ImageEditorActivity.class);
            startActivity(intent);
        });
    }

    private void setEmptyOnClickListener() {
        final Button chooseImageButton = findViewById(R.id.emptyImageButton);
        chooseImageButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, ImageEditorActivity.class);
            intent.putExtra("pathToImage",
                    Sticker.getEmptySticker(this));
            startActivity(intent);
        });
    }
}
