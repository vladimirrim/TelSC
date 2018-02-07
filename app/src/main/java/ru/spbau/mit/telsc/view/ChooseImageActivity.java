package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ly.img.android.PESDK;
import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.model.Sticker;

public class ChooseImageActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;

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
            try {
                intent.putExtra("pathToImage", getRealPathFromURI(this, imageUri));
                startActivity(intent);
            } catch (CannotGetRealPathException e) {
                Toast.makeText(PESDK.getAppContext(), "Cannot get filename path to chosen image\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
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
                    Sticker.createEmptySticker(this));
            startActivity(intent);
        });
    }

    private static String getRealPathFromURI(Context context, Uri contentUri) throws CannotGetRealPathException {
        String[] data = {MediaStore.Images.Media.DATA};

        try (Cursor cursor = context.getContentResolver().query(contentUri, data, null, null, null)) {
            if (cursor != null) {
                int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else {
                throw new CannotGetRealPathException("Cannot get Cursor object from context.");
            }
        }
    }

    private static class CannotGetRealPathException extends Exception {
        CannotGetRealPathException(String message) {
            super(message);
        }
    }
}
