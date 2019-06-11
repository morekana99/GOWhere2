package com.example.ryan.gomap3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class Choose_LanguageActivity extends AppCompatActivity {
    public  static final int TO_JA = 1;
    public  static final int TO_EN = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        setTitle("选择翻译语言");

        Button enButton = (Button) findViewById(R.id.en_botton);
        Button jaButton = (Button) findViewById(R.id.ja_botton);

        enButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enIntent = new Intent(Choose_LanguageActivity.this,TransActivity.class);
                enIntent.putExtra("language",2);
                startActivity(enIntent);
            }
        });
        jaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jaIntent = new Intent(Choose_LanguageActivity.this,TransActivity.class);
                jaIntent.putExtra("language",1);
                startActivity(jaIntent);
            }
        });

    }
}
