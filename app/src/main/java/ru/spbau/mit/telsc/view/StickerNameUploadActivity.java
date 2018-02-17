package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

public class StickerNameUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_name_upload);

        findViewById(R.id.downloadFromDB).setOnClickListener(view -> {
            DatabaseManager dbManager = new DatabaseManager();

            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = getIntent();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(openFileInput(intent.getStringExtra("stickerName")));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] sticker = stream.toByteArray();
                TextView name = findViewById(R.id.stickerName);
                dbManager.uploadSticker(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(StickerNameUploadActivity.this,
                            "Error occurred during uploading sticker to database. Reason: "
                                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }, taskSnapshot -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                }, sticker, name.getText().toString());
            } catch (FileNotFoundException e) {
                Toast.makeText(StickerNameUploadActivity.this,
                        "Error occurred during uploading sticker to database. Reason: "
                                + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
