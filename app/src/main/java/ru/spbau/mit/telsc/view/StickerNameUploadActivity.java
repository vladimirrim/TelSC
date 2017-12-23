package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

public class StickerNameUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_name_upload);

        Intent intent = getIntent();
        DatabaseManager dbManager = new DatabaseManager();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.openFileInput(intent.getStringExtra("stickerName")));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] sticker = stream.toByteArray();
            findViewById(R.id.downloadFromDB).setOnClickListener(view -> {
                TextView name = findViewById(R.id.stickerName);
                dbManager.uploadSticker(sticker, name.getText().toString());
                finish();
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
