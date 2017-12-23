package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import ru.spbau.mit.telsc.R;

public class StickerNameDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_name_download);
        findViewById(R.id.downloadFromDB).setOnClickListener(view -> {
            EditText name = findViewById(R.id.stickerName);
            StorageReference stickerRef = FirebaseStorage.getInstance().getReference().child(name.getText().toString());
            ProgressBar progressBar = findViewById(R.id.progressBar);

            final long TEN_MEGABYTES = 1024 * 1024 * 10;
            stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(sticker -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(sticker, 0, sticker.length);
                saveStickerInFile(bitmap);
                progressBar.setVisibility(View.INVISIBLE);
                finish();
            }).addOnFailureListener(exception -> {
                        // TODO
                        exception.printStackTrace();
                        //Log.e(LOG, "failed to download sticker " + name);
                    });

            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private String saveStickerInFile(Bitmap bitmap) {
        String fileName = "newSticker";
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }
}
