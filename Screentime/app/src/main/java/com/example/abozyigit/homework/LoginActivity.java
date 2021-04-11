package com.example.abozyigit.homework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class LoginActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    EditText password;

    public static final String EXTRA_MESSAGE = "PARENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        radioGroup = findViewById(R.id.radioGroup);
        password = findViewById(R.id.password);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.childRadioBtn:
                        password.setVisibility(View.GONE);
                        findViewById(R.id.textView).setVisibility(View.GONE);
                        break;

                    case R.id.parentRadioBtn:
                        password.setVisibility(View.VISIBLE);
                        findViewById(R.id.textView).setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        Intent service = new Intent(this, ScreenListenService.class);
        startService(service);
    }

    public void btnLogin_onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.childRadioBtn:
                intent.putExtra(EXTRA_MESSAGE, false);
                startActivity(intent);
                break;
            case R.id.parentRadioBtn:
                if (!password.getText().toString().equals("admin")){
                    break;
                }
                intent.putExtra(EXTRA_MESSAGE, true);
                startActivity(intent);
                break;
        }
    }
}
