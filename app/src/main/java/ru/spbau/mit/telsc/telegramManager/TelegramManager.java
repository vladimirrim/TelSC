package ru.spbau.mit.telsc.telegramManager;

import android.util.Log;

import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.peer.TLInputPeerSelf;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import ru.spbau.mit.telsc.telegramManager.core.MemoryApiState;


public class TelegramManager extends DefaultAbsSender {

    public TelegramManager(DefaultBotOptions options) {
        super(options);
    }

    public String sendCode(String phone) throws TimeoutException, RpcException {
        TLRequestAuthSendCode code = new TLRequestAuthSendCode();
        code.setApiHash(APIHASH);
        code.setApiId(APIID);
        code.setPhoneNumber(phone);
        return api.doRpcCallNonAuth(code).getPhoneCodeHash();
    }

    public int auth(String phone, String smsCode, String phoneHash) throws TimeoutException, RpcException {
        TLRequestAuthSignIn sign = new TLRequestAuthSignIn();
        sign.setPhoneCode(smsCode);
        sign.setPhoneCodeHash(phoneHash);
        sign.setPhoneNumber(phone);
        return api.doRpcCallNonAuth(sign).getUser().getId();
    }

    public void createSticker(InputStream pngSticker, int currentStickerNumber, int userId) throws IOException, TelegramApiException {
        TelegramManager manager = new TelegramManager(new DefaultBotOptions());
        CreateNewStickerSet creator = new CreateNewStickerSet();
        creator.setPngStickerStream("stickerSet" + currentStickerNumber + STICKER_SET_SUFFIX, pngSticker);
        creator.setName("stickerSet" + currentStickerNumber + STICKER_SET_SUFFIX);
        creator.setUserId(userId);
        creator.setTitle("yourStickerName");
        creator.setEmojis("\uD83D\uDE00");
        creator.setContainsMasks(false);

        manager.createNewStickerSet(creator);
        SendMessage message = new SendMessage();
        message.setChatId((long) userId);
        message.setText(STICKER_SET_PREFIX + creator.getName());
        TLRequestMessagesSendMessage request = new TLRequestMessagesSendMessage();
        request.setPeer(new TLInputPeerSelf());
        request.setMessage(STICKER_SET_PREFIX + creator.getName());
        request.setRandomId(ThreadLocalRandom.current().nextInt());
        try {
            api.doRpcCallNonAuth(request);
        } catch (TimeoutException e) {
            Log.e(LOG, e.getLocalizedMessage());
        }
    }

    private static final String LOG = "TelegramManager";
    static final private int APIID = 124211;
    static final private String APIHASH = "eab4b49dc43c47ea4feb57631a42b07d";
    static final private String STICKER_SET_PREFIX = "t.me/addstickers/";
    static final private String STICKER_SET_SUFFIX = "_by_StickersCreatorBot";

    private static TelegramApi api = new TelegramApi(new MemoryApiState("149.154.167.50:443"), new AppInfo(APIID, "TelSC", "1.0",
            "0.5", "en"), new ApiCallback() {
        @Override
        public void onAuthCancelled(TelegramApi api) {
            Log.w(LOG, "auth cancelled");
        }

        @Override
        public void onUpdatesInvalidated(TelegramApi api) {
            Log.w(LOG, "updates invalidated");
        }

        @Override
        public void onUpdate(TLAbsUpdates updates) {
            Log.i(LOG, "update");
        }
    });

    @Override
    public String getBotToken() {
        return "488592108:AAGYlh_nekThSRsH5V9twX-7hxW8WA9C9oA";
    }
}
