package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import ly.img.android.ui.widgets.ImgLyTitleBar;
import ru.spbau.mit.telsc.R;

import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.DOWNLOAD_TEMPLATE_FROM_DB;
import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.UPLOAD_STICKER_TO_DB;
import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.UPLOAD_STICKER_TO_TELEGRAM;
import static ru.spbau.mit.telsc.view.ImageEditorActivity.ButtonType.UPLOAD_TEMPLATE_TO_DB;

public class TelSCImgLyTitleBar extends ImgLyTitleBar {
    {
        setUploadStickerToDBButtonOnClickListener();
        setUploadStickerButtonOnClickListener();
        setUploadTemplateButtonOnClickListener();
        setDownloadTemplateButtonOnClickListener();
    }

    public TelSCImgLyTitleBar(Context context) {
        super(context);
    }

    public TelSCImgLyTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TelSCImgLyTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setUploadStickerButtonOnClickListener() {
        final ImageButton uploadStickerButton = findViewById(R.id.uploadSticker);
        uploadStickerButton.setOnClickListener(v -> {
            ImageEditorActivity.buttonType = UPLOAD_STICKER_TO_TELEGRAM;
            findViewById(R.id.acceptButton).callOnClick();
        });
    }

    private void setUploadStickerToDBButtonOnClickListener() {
        final ImageButton uploadStickerToDBButton = findViewById(R.id.uploadStickerToDB);
        uploadStickerToDBButton.setOnClickListener(v -> {
            ImageEditorActivity.buttonType = UPLOAD_STICKER_TO_DB;
            findViewById(R.id.acceptButton).callOnClick();
        });
    }


    private void setUploadTemplateButtonOnClickListener() {
        final ImageButton uploadTemplateButton = findViewById(R.id.uploadTemplate);
        uploadTemplateButton.setOnClickListener(v -> {
            ImageEditorActivity.buttonType = UPLOAD_TEMPLATE_TO_DB;
            findViewById(R.id.acceptButton).callOnClick();
        });
    }

    private void setDownloadTemplateButtonOnClickListener() {
        final ImageButton downloadTemplateButton = findViewById(R.id.downloadTemplate);
        downloadTemplateButton.setOnClickListener(v -> {
            ImageEditorActivity.buttonType = DOWNLOAD_TEMPLATE_FROM_DB;
            findViewById(R.id.acceptButton).callOnClick();
        });
    }
}
