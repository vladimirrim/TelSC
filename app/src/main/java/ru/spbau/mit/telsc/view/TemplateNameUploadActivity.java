package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

public class TemplateNameUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_name_upload);

        findViewById(R.id.uploadToDB).setOnClickListener(view -> {
            DatabaseManager dbManager = new DatabaseManager();
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = getIntent();
            try {
                byte[] template = IOUtils.toByteArray(openFileInput(intent.getStringExtra("templateName")));
                TextView name = findViewById(R.id.stickerName);
                dbManager.uploadTemplate(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(TemplateNameUploadActivity.this,
                            "Error occurred during uploading template to database. Reason: "
                                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }, taskSnapshot -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                }, template, name.getText().toString());
            } catch (IOException e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(TemplateNameUploadActivity.this,
                        "Error occurred during uploading template to database. Reason: "
                                + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
