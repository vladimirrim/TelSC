package ru.spbau.mit.telsc.view.imageView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by mikhail on 19.12.17.
 */

public class ImageViewHelper {
    public static byte[] getRawData(ImageView imageView) {
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        //image = Bitmap.createScaledBitmap(image, 512, 512, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getStickerBitmap(ImageView imageView) {
        Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        return Bitmap.createScaledBitmap(image, 512, 512, false);
    }
}
