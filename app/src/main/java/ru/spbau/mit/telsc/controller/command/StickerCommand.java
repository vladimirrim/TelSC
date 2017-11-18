package ru.spbau.mit.telsc.controller.command;

import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public abstract class StickerCommand implements Command {
    protected Sticker sticker;

    public StickerCommand(Sticker sticker) {
        this.sticker = sticker;
    }
}
