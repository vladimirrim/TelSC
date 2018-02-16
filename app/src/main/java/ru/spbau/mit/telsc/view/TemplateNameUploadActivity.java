package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

import static java.lang.Thread.sleep;

public class TemplateNameUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_name_upload);

        findViewById(R.id.uploadToDB).setOnClickListener(view -> new UploadTemplateTask().execute());

    }

    private class UploadTemplateTask extends AsyncTask<Void, Void, Long> {
        private Exception exception = new Exception();
        private AtomicInteger checker = new AtomicInteger(0);

        protected Long doInBackground(Void... voids) {
            DatabaseManager dbManager = new DatabaseManager();
            Intent intent = getIntent();
            try {
                byte[] template = IOUtils.toByteArray(openFileInput(intent.getStringExtra("templateName")));
                TextView name = findViewById(R.id.stickerName);
                dbManager.uploadTemplate(checker, template, name.getText().toString());
            } catch (IOException e) {
                checker.set(-1);
                exception.addSuppressed(e);
            }
            while (checker.get() == 0) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    Log.e("UploadTemplate", e.getLocalizedMessage());
                }
            }
            return 0L;
        }

        protected void onPostExecute(Long result) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            if (checker.get() == -1) {
                Toast.makeText(TemplateNameUploadActivity.this, "Error occurred during uploading template to database. Reason: "
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
