package ru.spbau.mit.telsc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.telegram.api.engine.RpcException;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class CodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        Intent intent = getIntent();

        String phone = intent.getStringExtra("phone");
        TelegramManager manager = new TelegramManager(new DefaultBotOptions());
        try {
            manager.sendCode(phone);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RpcException e) {
            e.printStackTrace();
        }

        EditText editText = findViewById(R.id.codeNumber);

        editText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                String smsCode = editText.getText().toString();
                int userId = 0;
                try {
                    userId = manager.auth(phone, smsCode);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (RpcException e) {
                    e.printStackTrace();
                }
                SharedPreferences sp = getSharedPreferences("numberStorage", Activity.MODE_PRIVATE);
                int currentStickerNumber = sp.getInt("number", 32);
                try {
                    manager.createSticker(new ByteArrayInputStream(intent.getByteArrayExtra("sticker")), currentStickerNumber, userId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("number", currentStickerNumber + 1);
                editor.apply();
                finish();
                return true;
            }
            return false;
        });

    }
}
