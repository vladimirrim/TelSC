package ru.spbau.mit.telsc.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import ru.spbau.mit.telsc.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class PhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Intent oldIntent = getIntent();

        Intent intent = new Intent(this, CodeActivity.class);
        intent.putExtra("stickerName", oldIntent.getStringExtra("stickerName"));
        EditText editText = findViewById(R.id.phoneNumber);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if((event.getAction()== KeyEvent.ACTION_DOWN)&&
                    (keyCode==KeyEvent.KEYCODE_ENTER)){
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                intent.putExtra("phone", editText.getText().toString());
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
