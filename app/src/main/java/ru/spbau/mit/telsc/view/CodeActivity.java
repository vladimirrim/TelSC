package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.telegram.api.engine.RpcException;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;
import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class CodeActivity extends AppCompatActivity {

    private final AtomicBoolean isFinished = new AtomicBoolean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        EditText editText = findViewById(R.id.codeNumber);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                DatabaseManager dbManager = new DatabaseManager();
                dbManager.downloadCurrentStickerNumber(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressBar.setVisibility(View.INVISIBLE);
                        String smsCode = editText.getText().toString();
                        new AuthTask().execute(smsCode, dbManager);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                    }
                });
                return true;
            }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        synchronized (isFinished) {
            isFinished.set(true);
            finish();
        }
    }

    private class AuthTask extends AsyncTask<Object, Void, Long> {

        private Exception exception = null;
        private int userId;
        private TelegramManager manager = new TelegramManager(new DefaultBotOptions());
        private DatabaseManager dbManager;

        protected Long doInBackground(Object... objs) {
            try {
                Intent intent = getIntent();
                String phone = intent.getStringExtra("phone");
                String phoneHash = intent.getStringExtra("phoneHash");
                dbManager = (DatabaseManager) objs[1];
                userId = manager.auth(phone, (String) objs[0], phoneHash);
            } catch (TimeoutException | RpcException e) {
                exception = e;
            }
            return 0L;
        }

        protected void onPostExecute(Long result) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            if (exception != null) {
                Toast.makeText(CodeActivity.this, "Error occurred during signing in your account. Reason: "
                        + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
            synchronized (isFinished) {
                if (!isFinished.get()) {
                    try {
                        long currentStickerNumber = dbManager.getCurrentStickerNumber();
                        Bitmap bitmap = BitmapFactory.decodeStream(openFileInput(getIntent().getStringExtra("stickerName")));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        manager.createSticker(new ByteArrayInputStream(stream.toByteArray()), (int) currentStickerNumber, userId);
                        dbManager.increaseCurrentStickerNumber();
                    } catch (IOException | TelegramApiException e) {
                        Toast.makeText(CodeActivity.this, "Error occurred during sending sticker. Reason: "
                                + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
