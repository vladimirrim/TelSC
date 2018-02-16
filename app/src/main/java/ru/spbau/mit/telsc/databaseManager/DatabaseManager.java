package ru.spbau.mit.telsc.databaseManager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileOutputStream;
import java.io.IOException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.model.Sticker;
import ru.spbau.mit.telsc.view.ImageEditorActivity;

public class DatabaseManager {
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final String STICKER_FOLDER_PREFIX = "stickers/";
    private static final String TEMPLATE_FOLDER_PREFIX = "templates/";
    private static final String LOG = "DatabaseManager";
    private static final int TEN_MEGABYTES = 1024 * 1024 * 10;
    private long stickerNumber;

    public DatabaseManager() {
        DatabaseReference dbReference = db.getReference("stickerNumber");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stickerNumber = dataSnapshot.getValue() == null ? 0 : (Long) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG, "failed to get sticker number.");
            }
        };
        dbReference.addListenerForSingleValueEvent(listener);
    }

    public void uploadSticker(Activity activity, byte[] sticker, String name) {
        upload(activity, sticker, storageRef.child(STICKER_FOLDER_PREFIX + name), name);
    }

    public void uploadTemplate(Activity activity, byte[] template, String name) {
        upload(activity, template, storageRef.child(TEMPLATE_FOLDER_PREFIX + name), name);
    }

    private void upload(Activity activity, byte[] bytes, StorageReference ref, String name) {
        ProgressBar progressBar = activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        UploadTask uploadTask = ref.putBytes(bytes);

        uploadTask.addOnFailureListener(exception -> {
            Log.e(LOG, "failed to upload " + name);

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(activity, "Error occurred during uploading sticker to database. Reason: "
                    + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Intent returnIntent = new Intent();
            activity.setResult(Activity.RESULT_OK, returnIntent);
            activity.finish();
        }).addOnSuccessListener(taskSnapshot -> {
            Log.i(LOG, "successful upload of " + name);

            progressBar.setVisibility(View.INVISIBLE);
            activity.finish();
        });
    }

    public void downloadSticker(Activity activity, String name) {
        StorageReference stickerRef = storageRef.child(STICKER_FOLDER_PREFIX + name);

        ProgressBar progressBar = activity.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(sticker -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(sticker, 0, sticker.length);

            Intent intent = new Intent(activity, ImageEditorActivity.class);
            try {
                intent.putExtra("pathToImage",
                        Sticker.saveStickerInCache(bitmap, activity));
            } catch (IOException e) {
                Toast.makeText(activity, "Error occurred during saving sticker to file. Reason: "
                        + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            progressBar.setVisibility(View.INVISIBLE);
            activity.startActivity(intent);
        }).addOnFailureListener(exception -> {
            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(activity, "Error occurred during downloading sticker from database. Reason: "
                    + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public void downloadTemplate(Activity activity, String name) {
        StorageReference stickerRef = storageRef.child(TEMPLATE_FOLDER_PREFIX + name);

        ProgressBar progressBar = activity.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(template -> {
            String fileName = "template";
            try {
                FileOutputStream fo = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
                fo.write(template);
                fo.close();
            } catch (IOException e) {
                Toast.makeText(activity, "Error occurred during downloading template from database. Reason: "
                        + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
            Intent returnIntent = new Intent();
            activity.setResult(Activity.RESULT_OK, returnIntent);
            activity.finish();
        }).addOnFailureListener(exception -> {
            progressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(activity, "Error occurred during downloading template from database. Reason: "
                    + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public long getCurrentStickerNumber() {
        return stickerNumber;
    }

    public void increaseCurrentStickerNumber() {
        DatabaseReference dbReference = db.getReference("stickerNumber");
        stickerNumber++;
        dbReference.setValue(stickerNumber);
    }
}
