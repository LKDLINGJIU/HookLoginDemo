package com.android.lj.hooklogindemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by lingjiu on 2018/11/12.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText phoneEt;
    private EditText psdEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneEt = ((EditText) findViewById(R.id.phoneEt));
        psdEt = ((EditText) findViewById(R.id.psdEt));
    }


    public void login(View view) {
        if (TextUtils.isEmpty(phoneEt.getText()) || TextUtils.isEmpty(psdEt.getText())) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.equals("yucheng", phoneEt.getText().toString()) &&
                TextUtils.equals("123465", psdEt.getText().toString())) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();

            getSharedPreferences("user", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("login", true)
                    .commit();
            finish();
        }

    }
}
