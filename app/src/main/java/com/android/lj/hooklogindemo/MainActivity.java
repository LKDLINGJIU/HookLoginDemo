package com.android.lj.hooklogindemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toFirstActivity(View view) {
        startActivity(new Intent(this, FirstActivity.class));
    }

    public void toSecondActivity(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    public void logout(View view) {
        getSharedPreferences("user", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("login", false)
                .commit();
    }
}
