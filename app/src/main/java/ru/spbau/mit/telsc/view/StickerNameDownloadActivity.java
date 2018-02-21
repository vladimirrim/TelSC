package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;
import ru.spbau.mit.telsc.model.Sticker;

public class StickerNameDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_name_download);

        findViewById(R.id.downloadFromDB).setOnClickListener(view -> {
                    DatabaseManager dbManager = new DatabaseManager();
                    EditText name = findViewById(R.id.stickerName);
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    dbManager.downloadSticker(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(StickerNameDownloadActivity.this,
                                        "Error occurred during downloading sticker from database. Reason: "
                                                + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            },
                            sticker -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(StickerNameDownloadActivity.this, ImageEditorActivity.class);
                                try {
                                    intent.putExtra("pathToImage",
                                            Sticker.saveStickerInCache(BitmapFactory.decodeByteArray(sticker, 0, sticker.length),
                                                    StickerNameDownloadActivity.this));
                                } catch (IOException e) {
                                    Toast.makeText(StickerNameDownloadActivity.this,
                                            "Error occurred during saving sticker to file. Reason: "
                                                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                startActivity(intent);
                            }, name.getText().toString());
                }
        );
    }
}
