package ru.spbau.mit.telsc.controller.command.userCommands;

import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public class DownloadSticker extends StickerCommand {
    String stickerName;

    public DownloadSticker(String stickerName, Sticker sticker) {
        super(sticker);
        this.stickerName = stickerName;
    }

    @Override
    public void apply() {

        // TODO: download sticker
    }
}
