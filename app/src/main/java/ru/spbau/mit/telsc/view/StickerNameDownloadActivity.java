package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;
import ru.spbau.mit.telsc.model.Sticker;

import static java.lang.Thread.sleep;

public class StickerNameDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_name_download);

        findViewById(R.id.downloadFromDB).setOnClickListener(view -> new DownloadStickerTask().execute());
    }

    private class DownloadStickerTask extends AsyncTask<Void, Void, Long> {
        private Exception exception = new Exception();
        private AtomicInteger checker = new AtomicInteger(0);
        private AtomicReference<Bitmap> bitRef = new AtomicReference<>();

        protected Long doInBackground(Void... voids) {
            DatabaseManager dbManager = new DatabaseManager();
            EditText name = findViewById(R.id.stickerName);
            dbManager.downloadSticker(checker, bitRef, exception, name.getText().toString());
            while (checker.get() == 0) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    Log.e("DownloadSticker", e.getLocalizedMessage());
                }
            }
            return 0L;
        }

        protected void onPostExecute(Long result) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            if (checker.get() == -1) {
                Toast.makeText(StickerNameDownloadActivity.this, "Error occurred during downloading sticker from database. Reason: "
                        + exception.getSuppressed()[0].getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(StickerNameDownloadActivity.this, ImageEditorActivity.class);
                try {
                    intent.putExtra("pathToImage",
                            Sticker.saveStickerInCache(bitRef.get(), StickerNameDownloadActivity.this));
                } catch (IOException e) {
                    Toast.makeText(StickerNameDownloadActivity.this, "Error occurred during saving sticker to file. Reason: "
                            + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
                startActivity(intent);
            }
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
