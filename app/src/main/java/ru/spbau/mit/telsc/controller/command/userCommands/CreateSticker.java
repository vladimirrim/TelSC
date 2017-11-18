package ru.spbau.mit.telsc.controller.command.userCommands;

import ru.spbau.mit.telsc.controller.command.Command;
import ru.spbau.mit.telsc.controller.command.StickerCommand;
import ru.spbau.mit.telsc.model.Sticker;

/**
 * Created by mikhail on 18.11.17.
 */

public final class CreateSticker extends StickerCommand {
    private String user_id; // TODO: think if it is needed

    public CreateSticker(String user_id, Sticker sticker) {
        super(sticker);
        this.user_id = user_id;
    }

    @Override
    public void apply() {
        // TODO: creating sticker on telegram
    }
}
