package ru.spbau.mit.telsc.controller.command.userCommands;

import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public class SaveSticker extends StickerCommand {
    public SaveSticker(Sticker sticker) {
        super(sticker);
    }

    @Override
    public void apply() {
        // TODO: saving sticker to database
    }
}
