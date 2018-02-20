package ru.spbau.mit.telsc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeoutException;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class PhoneActivity extends AppCompatActivity {

    private static volatile boolean isFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        EditText editText = findViewById(R.id.phoneNumber);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                TelegramManager manager = new TelegramManager(new DefaultBotOptions());
                String phone = editText.getText().toString();
                new SendCodeTask(PhoneActivity.this, phone).execute(manager);
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

    private static class SendCodeTask extends AsyncTask<TelegramManager, Void, Long> {

        private Exception exception = null;
        private String phone;
        private String phoneHash;
        private WeakReference<Activity> activityRef;

        private SendCodeTask(Activity activity, String phoneNumber) {
            activityRef = new WeakReference<>(activity);
            phone = phoneNumber;
        }

        protected Long doInBackground(TelegramManager... telManager) {
            try {
                phoneHash = telManager[0].sendCode(phone);
            } catch (TimeoutException | RpcException e) {
                exception = e;
            }
            return 0L;
        }

        protected void onPostExecute(Long result) {
            Activity activity = activityRef.get();
            if (activity != null) {
                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);
                if (exception != null) {
                    Toast.makeText(activity, "Error occurred during sending code. Reason: "
                            + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    activity.finish();
                }
                if (!isFinished) {
                    Intent oldIntent = activity.getIntent();
                    Intent intent = new Intent(activity, CodeActivity.class);
                    intent.putExtra("stickerName", oldIntent.getStringExtra("stickerName"));
                    intent.putExtra("phone", phone);
                    intent.putExtra("phoneHash", phoneHash);
                    activity.startActivity(intent);
                    Intent returnIntent = new Intent();
                    activity.setResult(Activity.RESULT_OK, returnIntent);
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
