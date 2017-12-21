package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.spbau.mit.telsc.R;

public class DatabaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        Intent oldIntent = getIntent();
        String stickerName = oldIntent.getStringExtra("stickerName");

        findViewById(R.id.downloadSticker).setOnClickListener(view -> {
            Intent intent = new Intent(this, StickerNameDownloadActivity.class);
            intent.putExtra("stickerName", stickerName);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.uploadSticker).setOnClickListener(view -> {
            Intent intent = new Intent(this, StickerNameUploadActivity.class);
            intent.putExtra("stickerName", stickerName);
            startActivity(intent);
        });
    }
}
