package ru.spbau.mit.telsc.databaseManager;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class DatabaseManager {
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private long stickerNumber;

    public DatabaseManager() {
        DatabaseReference dbref = db.getReference("stickerNumber");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stickerNumber = (Long) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG, "failed to get sticker number.");
            }
        };
        dbref.addValueEventListener(listener);
    }


    public void uploadSticker(byte[] sticker, String name) {

        StorageReference stickerRef = storageRef.child(name);

        UploadTask uploadTask = stickerRef.putBytes(sticker);
        uploadTask.addOnFailureListener(exception -> {
            // TODO
            Log.e(LOG, "failed to upload sticker " + name);
        }).addOnSuccessListener(taskSnapshot -> {
            Log.i(LOG, "successful upload of sticker " + name);
        });
    }

    public void downloadSticker(final ArrayList<Byte> sticker,String name) {
        StorageReference stickerRef = FirebaseStorage.getInstance().getReference().child(name);

        final long TEN_MEGABYTES = 1024 * 1024 * 10;
        stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(bytes -> {
            for(byte b:bytes)
                sticker.add(b);
            Log.i(LOG,sticker.size() + "");
        }).
                addOnFailureListener(exception -> {
                    // TODO
                    exception.printStackTrace();
                    Log.e(LOG, "failed to download sticker " + name);
                });
    }

    public void uploadTemplate() {
        //TODO
    }


    public long getCurrentStickerNumber() {
        return stickerNumber;
    }

    public void increaseCurrentStickerNumber() {
        DatabaseReference dbref = db.getReference("stickerNumber");
        stickerNumber++;
        dbref.setValue(stickerNumber);
    }

    private static final String LOG = "DatabaseManager";
}
