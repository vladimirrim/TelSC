package org.telegram.telegrambots.api.methods.stickers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.telegram.telegrambots.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.api.objects.replykeyboard.ApiResponse;
import org.telegram.telegrambots.api.objects.stickers.MaskPosition;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.exceptions.TelegramApiValidationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


/**
 * @author Ruben Bermudez
 * @version 1.0
 * Use this method to add a new sticker to a set created by the bot. Returns True on success.
 */
public class AddStickerToSet extends PartialBotApiMethod<Boolean> {
    public static final String PATH = "addStickerToSet";

    public static final String USERID_FIELD = "user_id";
    public static final String NAME_FIELD = "name";
    public static final String PNGSTICKER_FIELD = "png_sticker";
    public static final String EMOJIS_FIELD = "emojis";
    public static final String MASKPOSITION_FIELD = "mask_position";

    private Integer userId; ///< User identifier of sticker set owner
    private String name; ///< Sticker set name
    private String emojis; ///< One or more emoji corresponding to the sticker
    private MaskPosition maskPosition; ///< Position where the mask should be placed on faces
    /**
     * Png image with the sticker, must be up to 512 kilobytes in size, dimensions must not exceed 512px,
     * and either width or height must be exactly 512px. Pass a file_id as a String to send a file
     * that already exists on the Telegram servers, pass an HTTP URL as a String for Telegram
     * to get a file from the Internet, or upload a new one using multipart/form-data.
     */
    private Boolean isNewPngSticker;
    private String pngSticker;
    private File pngStickerFile; ///< New sticker file
    private InputStream pngStickerStream; ///< New sticker stream
    private String pngStickerName; ///< New sticker stream name

    public AddStickerToSet() {
        super();
    }

    public AddStickerToSet(Integer userId, String name, String emojis) {
        this.userId = (userId);
        this.name = (name);
        this.emojis = (emojis);
    }

    public Integer getUserId() {
        return userId;
    }

    public AddStickerToSet setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getPngSticker() {
        return pngSticker;
    }

    public AddStickerToSet setPngSticker(String pngSticker) {
        this.pngSticker = pngSticker;
        this.isNewPngSticker = false;
        return this;
    }

    public File getPngStickerFile() {
        return pngStickerFile;
    }

    public AddStickerToSet setPngStickerFile(File pngStickerFile) {
        Objects.requireNonNull(pngStickerFile, "pngStickerFile cannot be null!");
        this.pngStickerFile = pngStickerFile;
        this.isNewPngSticker = true;
        return this;
    }

    public InputStream getPngStickerStream() {
        return pngStickerStream;
    }

    public AddStickerToSet setPngStickerStream(String pngStickerName, InputStream pngStickerStream) {
        Objects.requireNonNull(pngStickerName, "pngStickerName cannot be null!");
        Objects.requireNonNull(pngStickerStream, "pngStickerStream cannot be null!");
        this.pngStickerStream = pngStickerStream;
        this.pngStickerName = pngStickerName;
        this.isNewPngSticker = true;
        return this;
    }

    public String getPngStickerName() {
        return pngStickerName;
    }

    public Boolean isNewPngSticker() {
        return isNewPngSticker;
    }

    public String getName() {
        return name;
    }

    public AddStickerToSet setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmojis() {
        return emojis;
    }

    public AddStickerToSet setEmojis(String emojis) {
        this.emojis = emojis;
        return this;
    }

    public MaskPosition getMaskPosition() {
        return maskPosition;
    }

    public AddStickerToSet setMaskPosition(MaskPosition maskPosition) {
        this.maskPosition = maskPosition;
        return this;
    }

    @Override
    public Boolean deserializeResponse(String answer) throws TelegramApiRequestException {
        try {
            ApiResponse<Boolean> result = OBJECT_MAPPER.readValue(answer,
                    new TypeReference<ApiResponse<Boolean>>(){});
            if (result.getOk()) {
                return result.getResult();
            } else {
                throw new TelegramApiRequestException("Error creating new sticker set", result);
            }
        } catch (IOException e) {
            throw new TelegramApiRequestException("Unable to deserialize response", e);
        }
    }

    @Override
    public void validate() throws TelegramApiValidationException {
        if (userId == null || userId <= 0) {
            throw new TelegramApiValidationException("userId can't be empty", this);
        }
        if (name == null || name.isEmpty()) {
            throw new TelegramApiValidationException("name can't be empty", this);
        }
        if (emojis == null || emojis.isEmpty()) {
            throw new TelegramApiValidationException("emojis can't be empty", this);
        }
        if (isNewPngSticker) {
            if (pngStickerFile == null && pngStickerStream == null) {
                throw new TelegramApiValidationException("PngSticker can't be empty", this);
            }
            if (pngStickerStream != null && (pngStickerName == null || pngStickerName.isEmpty())) {
                throw new TelegramApiValidationException("PngSticker name can't be empty", this);
            }
        } else if (pngSticker == null) {
            throw new TelegramApiValidationException("PngSticker can't be empty", this);
        }
        if (maskPosition != null) {
            maskPosition.validate();
        }
    }

    @Override
    public String toString() {
        return "AddStickerToSet{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", emojis='" + emojis + '\'' +
                ", maskPosition=" + maskPosition +
                ", isNewPngSticker=" + isNewPngSticker +
                ", pngSticker='" + pngSticker + '\'' +
                ", pngStickerFile=" + pngStickerFile +
                ", pngStickerStream=" + pngStickerStream +
                ", pngStickerName='" + pngStickerName + '\'' +
                '}';
    }
}
