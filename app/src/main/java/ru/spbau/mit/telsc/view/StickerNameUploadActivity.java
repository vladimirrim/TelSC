package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

import static java.lang.Thread.sleep;

public class StickerNameUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_name_upload);

        findViewById(R.id.downloadFromDB).setOnClickListener(view -> new UploadStickerTask().execute());
    }

    private class UploadStickerTask extends AsyncTask<Void, Void, Long> {
        private Exception exception = new Exception();
        private AtomicInteger checker = new AtomicInteger(0);

        protected Long doInBackground(Void... voids) {
            DatabaseManager dbManager = new DatabaseManager();
            Intent intent = getIntent();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(openFileInput(intent.getStringExtra("stickerName")));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] sticker = stream.toByteArray();
                TextView name = findViewById(R.id.stickerName);
                dbManager.uploadSticker(checker, sticker, name.getText().toString());
            } catch (FileNotFoundException e) {
                checker.set(-1);
                exception.addSuppressed(e);
            }
            while (checker.get() == 0) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    Log.e("UploadSticker", e.getLocalizedMessage());
                }
            }
            return 0L;
        }

        protected void onPostExecute(Long result) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            if (checker.get() == -1) {
                Toast.makeText(StickerNameUploadActivity.this, "Error occurred during uploading sticker to database. Reason: "
                        + exception.getSuppressed()[0].getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
