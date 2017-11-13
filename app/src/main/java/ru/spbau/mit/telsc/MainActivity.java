package ru.spbau.mit.telsc;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;

    private Bitmap sticker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button chooseImageButton = (Button) findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        final Button uploadStickerButton = (Button) findViewById(R.id.uploadSticker);
        uploadStickerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                sticker.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                /* Vova, your code here. I suggest you create another class for this aims. */
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            Uri pickedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            final TextView selectedImagePath = (TextView) findViewById(R.id.selectedImagePath);
            selectedImagePath.setText(imagePath);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            sticker = Bitmap.createScaledBitmap(bitmap, 512, 512, false);
            final ImageView stickerImageView = (ImageView) findViewById(R.id.imageView);
            stickerImageView.setImageBitmap(sticker);

            cursor.close();
        }
    }
}
