package ru.spbau.mit.telsc.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

public class TemplateNameDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_name_download);

        DatabaseManager dbManager = new DatabaseManager();
        findViewById(R.id.downloadFromDB).setOnClickListener(view -> {
            EditText name = findViewById(R.id.stickerName);
            dbManager.downloadTemplate(this, name.getText().toString());
        });
    }
}
