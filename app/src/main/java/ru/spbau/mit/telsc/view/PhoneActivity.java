package ru.spbau.mit.telsc.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import ru.spbau.mit.telsc.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class PhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Intent intent = new Intent(this, CodeActivity.class);
        EditText editText = findViewById(R.id.phoneNumber);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}
