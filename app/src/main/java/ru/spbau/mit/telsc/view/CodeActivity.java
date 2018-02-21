package ru.spbau.mit.telsc.view;

import android.app.Activity;
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

import org.telegram.api.engine.RpcException;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeoutException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.databaseManager.DatabaseManager;
import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class CodeActivity extends AppCompatActivity {

    private static volatile boolean isFinished;

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
                dbManager.downloadAndIncreaseCurrentStickerNumber(isDone -> {
                    if (isDone) {
                        progressBar.setVisibility(View.INVISIBLE);
                        String smsCode = editText.getText().toString();
                        Intent intent = getIntent();
                        new AuthTask(CodeActivity.this, dbManager).execute(smsCode, intent.getStringExtra("phone"),
                                intent.getStringExtra("phoneHash"));
                    } else {
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
        isFinished = true;
        finish();
    }

    private static class AuthTask extends AsyncTask<String, Void, Integer> {

        private Exception exception = null;
        private TelegramManager manager = new TelegramManager(new DefaultBotOptions());
        private DatabaseManager dbManager;
        private WeakReference<Activity> activityRef;

        private AuthTask(Activity activity, DatabaseManager dbMan) {
            activityRef = new WeakReference<>(activity);
            dbManager = dbMan;
        }

        protected Integer doInBackground(String... strings) {
            try {
                return manager.auth(strings[1], strings[0], strings[2]);
            } catch (TimeoutException | RpcException e) {
                exception = e;
                return 0;
            }
        }

        protected void onPostExecute(Integer userId) {
            Activity activity = activityRef.get();
            if (activity != null) {
                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);
                if (exception != null) {
                    Toast.makeText(activity, "Error occurred during signing in your account. Reason: "
                            + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    activity.finish();
                }
                if (!isFinished) {
                    try {
                        long currentStickerNumber = dbManager.getCurrentStickerNumber();
                        Bitmap bitmap = BitmapFactory.decodeStream(activity.openFileInput(activity.
                                getIntent().getStringExtra("stickerName")));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        manager.createSticker(new ByteArrayInputStream(stream.toByteArray()), (int) currentStickerNumber, userId);
                    } catch (IOException | TelegramApiException e) {
                        Toast.makeText(activity, "Error occurred during sending sticker. Reason: "
                                + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    activity.finish();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            Activity activity = activityRef.get();
            if (activity != null) {
                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }
}
