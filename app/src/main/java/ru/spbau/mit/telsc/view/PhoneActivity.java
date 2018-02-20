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
import java.util.concurrent.atomic.AtomicBoolean;

import ru.spbau.mit.telsc.R;
import ru.spbau.mit.telsc.telegramManager.TelegramManager;

public class PhoneActivity extends AppCompatActivity {

    private static final AtomicBoolean isFinished = new AtomicBoolean();

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
        isFinished.set(true);
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
            ProgressBar progressBar = activityRef.get().findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            if (exception != null) {
                Toast.makeText(activityRef.get(), "Error occurred during sending code. Reason: "
                        + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                activityRef.get().finish();
            }
            if (isFinished.compareAndSet(false, false)) {
                Intent oldIntent = activityRef.get().getIntent();
                Intent intent = new Intent(activityRef.get(), CodeActivity.class);
                intent.putExtra("stickerName", oldIntent.getStringExtra("stickerName"));
                intent.putExtra("phone", phone);
                intent.putExtra("phoneHash", phoneHash);
                activityRef.get().startActivity(intent);
                Intent returnIntent = new Intent();
                activityRef.get().setResult(Activity.RESULT_OK, returnIntent);
                activityRef.get().finish();
            }
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = activityRef.get().findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
