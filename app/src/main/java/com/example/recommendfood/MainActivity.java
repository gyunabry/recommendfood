package com.example.recommendfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnAll;
    Button btnKorean, btnWestern, btnChinese, btnJapanese;
    Button btnChicken, btnPork, btnBeef, btnSeafood;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAll = (Button) findViewById(R.id.btnall);
        btnKorean = (Button) findViewById(R.id.btnkorean);
        btnWestern = (Button) findViewById(R.id.btnwestern);
        btnChinese = (Button) findViewById(R.id.btnchinese);
        btnJapanese = (Button) findViewById(R.id.btnjapanese);
        btnChicken = (Button) findViewById(R.id.btnchicken);
        btnPork = (Button) findViewById(R.id.btnpork);
        btnBeef = (Button) findViewById(R.id.btnbeef);
        btnSeafood = (Button) findViewById(R.id.btnseafood);

        intent = new Intent(this, RecActivity.class);

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "all");
                startActivity(intent);
            }
        });

        btnKorean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "korean");
                startActivity(intent);
            }
        });

        btnWestern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "western");
                startActivity(intent);
            }
        });

        btnChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "chinese");
                startActivity(intent);
            }
        });

        btnJapanese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "japanese");
                startActivity(intent);
            }
        });

        btnChicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "chicken");
                startActivity(intent);
            }
        });

        btnPork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "pork");
                startActivity(intent);
            }
        });

        btnBeef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "beef");
                startActivity(intent);
            }
        });

        btnSeafood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("foodkind", "seafood");
                startActivity(intent);
            }
        });
    }
}