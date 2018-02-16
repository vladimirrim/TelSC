package ru.spbau.mit.telsc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

import static java.lang.Thread.sleep;

public class TemplateNameDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_name_download);

        findViewById(R.id.downloadFromDB).setOnClickListener(view -> new DownloadTemplateTask().execute());
    }

    private class DownloadTemplateTask extends AsyncTask<Void, Void, Long> {
        private Exception exception = new Exception();
        private AtomicInteger checker = new AtomicInteger(0);

        protected Long doInBackground(Void... voids) {
            DatabaseManager dbManager = new DatabaseManager();
            EditText name = findViewById(R.id.stickerName);
            String fileName = "template";
            FileOutputStream fo;
            try {
                fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                dbManager.downloadTemplate(fo, checker, exception, name.getText().toString());
            } catch (FileNotFoundException e) {
                checker.set(-1);
                exception.addSuppressed(e);
                Log.e("DownloadTemplate", e.getLocalizedMessage());
            }
            while (checker.get() == 0) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    Log.e("DownloadTemplate", e.getLocalizedMessage());
                }
            }
            return 0L;
        }

        protected void onPostExecute(Long result) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            if (checker.get() == -1) {
                Toast.makeText(TemplateNameDownloadActivity.this, "Error occurred during downloading template from database. Reason: "
                        + exception.getSuppressed()[0].getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } else {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
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
