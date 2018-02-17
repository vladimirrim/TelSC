package ru.spbau.mit.telsc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

public class TemplateNameDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_name_download);

        findViewById(R.id.downloadFromDB).setOnClickListener(view -> {
            DatabaseManager dbManager = new DatabaseManager();
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            EditText name = findViewById(R.id.stickerName);
            dbManager.downloadTemplate(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(TemplateNameDownloadActivity.this,
                                "Error occurred during downloading template from database. Reason: "
                                        + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    },
                    template -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            FileOutputStream fo = openFileOutput("template", Context.MODE_PRIVATE);
                            fo.write(template);
                            fo.close();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } catch (IOException e) {
                            Toast.makeText(TemplateNameDownloadActivity.this,
                                    "Error occurred during downloading template from database. Reason: "
                                            + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }, name.getText().toString());
        });
    }
}
