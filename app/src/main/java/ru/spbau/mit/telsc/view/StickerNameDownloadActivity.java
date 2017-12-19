package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;

public class StickerNameDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_name_download);
        findViewById(R.id.downloadFromDB).setOnClickListener(view -> {
            EditText name = findViewById(R.id.stickerName);
            StorageReference stickerRef = FirebaseStorage.getInstance().getReference().child(name.getText().toString());

            final long TEN_MEGABYTES = 1024 * 1024 * 10;
            stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(sticker -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(sticker, 0, sticker.length);
                saveStickerInFile(bitmap);
                finish();
            }).addOnFailureListener(exception -> {
                        // TODO
                        exception.printStackTrace();
                        //Log.e(LOG, "failed to download sticker " + name);
                    });
        });
    }

    private String saveStickerInFile(Bitmap bitmap) {
        String fileName = "newSticker";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }
}
