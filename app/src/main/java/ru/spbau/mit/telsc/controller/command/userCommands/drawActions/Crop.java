package ru.spbau.mit.telsc.controller.command.userCommands.drawActions;

import android.graphics.Rect;

import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public class Crop extends StickerCommand {
    private Rect rectangle;

    public Crop(Rect rectangle, Sticker sticker) {
        super(sticker);
        this.rectangle = rectangle;
    }

    @Override
    public void apply() {
        // TODO: rectangle Crop, add any Crop figure
    }
}
