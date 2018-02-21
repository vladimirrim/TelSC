package ru.spbau.mit.telsc.databaseManager;


import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.function.Consumer;

public class DatabaseManager {
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final String STICKER_FOLDER_PREFIX = "stickers/";
    private static final String TEMPLATE_FOLDER_PREFIX = "templates/";
    private static final String LOG = "DatabaseManager";
    private static final int TEN_MEGABYTES = 1024 * 1024 * 10;
    private volatile long stickerNumber;

    public void uploadSticker(OnFailureListener onFailureListener, OnSuccessListener<? super UploadTask.TaskSnapshot> onSuccessListener,
                              byte[] sticker, String name) {
        UploadTask uploadTask = storageRef.child(STICKER_FOLDER_PREFIX + name).putBytes(sticker);
        uploadTask.addOnFailureListener(onFailureListener).addOnSuccessListener(onSuccessListener);
    }

    public void uploadTemplate(OnFailureListener onFailureListener, OnSuccessListener<? super UploadTask.TaskSnapshot> onSuccessListener,
                               byte[] template, String name) {
        UploadTask uploadTask = storageRef.child(TEMPLATE_FOLDER_PREFIX + name).putBytes(template);
        uploadTask.addOnFailureListener(onFailureListener).addOnSuccessListener(onSuccessListener);
    }

    public void downloadSticker(OnFailureListener onFailureListener, OnSuccessListener<? super byte[]> onSuccessListener,
                                String name) {
        StorageReference stickerRef = storageRef.child(STICKER_FOLDER_PREFIX + name);
        stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void downloadTemplate(OnFailureListener onFailureListener, OnSuccessListener<? super byte[]> onSuccessListener,
                                 String name) {
        StorageReference stickerRef = storageRef.child(TEMPLATE_FOLDER_PREFIX + name);
        stickerRef.getBytes(TEN_MEGABYTES).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void downloadAndIncreaseCurrentStickerNumber(Consumer<Boolean> uiListener) {
        DatabaseReference dbReference = db.getReference("stickerNumber");
        dbReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentNumber) {
                stickerNumber = currentNumber.getValue() == null ? 0 : (Long) currentNumber.getValue();
                currentNumber.setValue(stickerNumber + 1);
                return Transaction.success(currentNumber);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                uiListener.accept(b);
                Log.d(LOG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public long getCurrentStickerNumber() {
        return stickerNumber;
    }

}
