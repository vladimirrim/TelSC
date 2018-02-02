package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import ly.img.android.PESDK;

public class Application extends android.app.Application {
    @Nullable
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] data = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = context.getContentResolver().query(contentUri, data, null, null, null)) {
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } else {
                Toast.makeText(PESDK.getAppContext(), "Cannot get real path from URI\n", Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PESDK.init(this, "PhotoEditorSDKLICENSE");
    }
}

