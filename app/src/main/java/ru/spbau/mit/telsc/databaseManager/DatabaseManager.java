package ru.spbau.mit.telsc.databaseManager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

    public void uploadSticker(AtomicInteger checker, byte[] sticker, String name) {
        upload(checker, sticker, storageRef.child(STICKER_FOLDER_PREFIX + name), name);
    }

    public void uploadTemplate(AtomicInteger checker, byte[] template, String name) {
        upload(checker, template, storageRef.child(TEMPLATE_FOLDER_PREFIX + name), name);
    }

    private void upload(AtomicInteger checker, byte[] bytes, StorageReference ref, String name) {
        UploadTask uploadTask = ref.putBytes(bytes);

        uploadTask.addOnFailureListener(exception -> {
            Log.e(LOG, "failed to upload " + name);
            checker.set(-1);
        }).addOnSuccessListener(taskSnapshot -> {
            Log.i(LOG, "successful upload of " + name);
            checker.set(1);
        });
    }

    public void downloadSticker(AtomicInteger checker, AtomicReference<Bitmap> bitRef, Exception exception, String name) {
        StorageReference stickerRef = storageRef.child(STICKER_FOLDER_PREFIX + name);
        stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(sticker -> {
            bitRef.set(BitmapFactory.decodeByteArray(sticker, 0, sticker.length));
            checker.set(1);
        }).addOnFailureListener(e -> {
            checker.set(-1);
            exception.addSuppressed(e);
        });
    }

    public void downloadTemplate(FileOutputStream fo, AtomicInteger checker, Exception exception, String name) {
        StorageReference stickerRef = storageRef.child(TEMPLATE_FOLDER_PREFIX + name);
        stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(template -> {
            try {
                fo.write(template);
                fo.close();
                checker.set(1);
            } catch (IOException e) {
                checker.set(-1);
                exception.addSuppressed(e);
            }
        }).addOnFailureListener(e -> {
            checker.set(-1);
            exception.addSuppressed(e);
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
