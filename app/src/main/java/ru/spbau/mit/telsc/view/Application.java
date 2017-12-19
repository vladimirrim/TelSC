package ru.spbau.mit.telsc;

import ly.img.android.PESDK;

/**
 * Created by mikhail on 14.12.17.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //PESDK.init(this);
    }
}
