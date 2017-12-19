package ru.spbau.mit.telsc.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.nio.Buffer;

import ly.img.android.sdk.views.EditorPreview;
import ly.img.android.ui.widgets.EditorRootView;
import ly.img.android.ui.widgets.ImgLyTitleBar;
import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.view.imageView.ImageViewHelper;

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
                final ImageView imageView = ((View) TelSCImgLyTitleBar.this.getParent()).findViewById(ly.img.android.R.id.image);
                Intent intent = new Intent(v.getContext(), PhoneActivity.class);
                intent.putExtra("sticker", ImageViewHelper.getRawData(imageView));
                v.getContext().startActivity(intent); // v or ((View) TelSCImgLyTitleBar.this.getParent()) ??
            }
        });
    }

    private void setUploadStickerToDBButtonOnClickListener() {
        final ImageButton uploadStickerToDBButton = findViewById(R.id.uploadStickerToDB);
        uploadStickerToDBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: upload sticker to database button is pressed, realize logic
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
