package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ly.img.android.PESDK;
import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

public class TemplateNameUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_name_upload);

        Intent intent = getIntent();
        DatabaseManager dbManager = new DatabaseManager();
        try {
            byte[] template = IOUtils.toByteArray(openFileInput(intent.getStringExtra("templateName")));
            findViewById(R.id.uploadToDB).setOnClickListener(view -> {
                TextView name = findViewById(R.id.stickerName);
                dbManager.uploadTemplate(this, template, name.getText().toString());
            });
        } catch (IOException e) {
            Toast.makeText(PESDK.getAppContext(),
                    "No template will be applied. Error during downloading template: \n" + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
