package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.telegram.api.engine.RpcException;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;
import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class CodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        TelegramManager manager = new TelegramManager(new DefaultBotOptions());
        DatabaseManager dbManager = new DatabaseManager();
        try {
            manager.sendCode(phone);
        } catch (TimeoutException | RpcException e) {
            Toast.makeText(this, "Error occurred during sending code. Reason: "
                    + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        EditText editText = findViewById(R.id.codeNumber);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                String smsCode = editText.getText().toString();
                int userId = 0;
                try {
                    userId = manager.auth(phone, smsCode);
                } catch (TimeoutException | RpcException e) {
                    Toast.makeText(this, "Error occurred during signing in your account. Reason: "
                            + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }

                long currentStickerNumber = dbManager.getCurrentStickerNumber();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(openFileInput(intent.getStringExtra("stickerName")));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    manager.createSticker(new ByteArrayInputStream(stream.toByteArray()), (int) currentStickerNumber, userId);
                } catch (IOException | TelegramApiException e) {
                    Toast.makeText(this, "Error occurred during sending sticker. Reason: "
                            + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
                dbManager.increaseCurrentStickerNumber();
                finish();
                return true;
            }
            return false;
        });

    }
}
