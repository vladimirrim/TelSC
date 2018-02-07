package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import ly.img.android.PESDK;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PESDK.init(this, "PhotoEditorSDKLICENSE");
    }
}

