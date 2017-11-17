package ru.spbau.mit.telsc.model.stickerManager;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import ru.spbau.mit.telsc.model.core.Filters;
import ru.spbau.mit.telsc.model.core.Transformations;

/**
 * Created by mikhail on 14.11.17.
 */

public class StickerManager {
    private Bitmap stickerImage;

    public Bitmap getStickerImage() {
        return stickerImage;
    }

    public StickerManager(Bitmap sticker) {
        this.stickerImage = sticker;
    }

    public void applyGrayScaleFilter() {
        stickerImage = Filters.grayScale(stickerImage);
    }

    public void rotate90DegreesClockwise() {
        stickerImage = Transformations.rotate(stickerImage, 90);
    }

    public byte[] getRawData() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stickerImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        stickerImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
