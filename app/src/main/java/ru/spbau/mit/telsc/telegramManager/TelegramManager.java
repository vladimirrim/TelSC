package ru.spbau.mit.telsc.telegramManager;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;


public class TelegramManager extends DefaultAbsSender {
    public TelegramManager(DefaultBotOptions options) {
        super(options);
    }

    public void createSticker(InputStream pngSticker, int currentStickerNumber) throws IOException, TelegramApiException {
        TelegramManager manager = new TelegramManager(new DefaultBotOptions());
        CreateNewStickerSet creator = new CreateNewStickerSet();

        creator.setPngStickerStream("stickerSet" + currentStickerNumber + "_by_StickersCreatorBot", pngSticker);
        creator.setName("stickerSet" + currentStickerNumber + "_by_StickersCreatorBot");
        creator.setUserId(322713996);
        creator.setTitle("yourStickerName");
        creator.setEmojis("\uD83D\uDE00");
        creator.setContainsMasks(false);
        manager.createNewStickerSet(creator);
        SendMessage message = new SendMessage();
        message.setChatId((long) 322713996);
        message.setText("t.me/addstickers/" + creator.getName());
        manager.sendMessage(message);
    }


    @Override
    public String getBotToken() {
        return "488592108:AAGYlh_nekThSRsH5V9twX-7hxW8WA9C9oA";
    }
}
