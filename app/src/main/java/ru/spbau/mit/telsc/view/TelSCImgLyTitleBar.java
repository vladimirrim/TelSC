package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import ly.img.android.ui.widgets.ImgLyTitleBar;
import ru.spbau.mit.telsc.R;

/**
 * Created by mikhail on 18.12.17.
 */

public class TelSCImgLyTitleBar extends ImgLyTitleBar {
    {
        setUploadStickerButtonOnClickListener();
        setUploadStickerToDBButtonOnClickListener();
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
        uploadStickerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ImageEditorActivity.buttonType = ImageEditorActivity.ButtonType.UPLOAD_STICKER_TO_TELEGRAM;
                findViewById(R.id.acceptButton).callOnClick();
            }
        });
    }

    private void setUploadStickerToDBButtonOnClickListener() {
        final ImageButton uploadStickerToDBButton = findViewById(R.id.uploadStickerToDB);
        uploadStickerToDBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ImageEditorActivity.buttonType = ImageEditorActivity.ButtonType.UPLOAD_STICKER_TO_DB;
                findViewById(R.id.acceptButton).callOnClick();
            }
        });
    }


    private void setUploadTemplateButtonOnClickListener() {
        final ImageButton uploadTemplateButton = findViewById(R.id.uploadTemplate);
        uploadTemplateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: upload sticker template to database button is pressed, realize logic
            }
        });
    }


    private void setDownloadTemplateButtonOnClickListener() {
        final ImageButton downloadTemplateButton = findViewById(R.id.downloadTemplate);
        downloadTemplateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: download sticker template to database button is pressed, realize logic
            }
        });
    }
}
