package com.example.gpt_test;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    ScrollView study_scroll;
    EditText study_view;
    Button reset, submit;
    ImageButton bookmark;
    String study;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        study_scroll = findViewById(R.id.study_scroll);
        study_view = findViewById(R.id.study_view);
        reset = findViewById(R.id.reset);
        submit = findViewById(R.id.submit);
        bookmark = findViewById(R.id.Button);

        // 스크롤 기능 추가
        study_scroll.post(new Runnable() {
            @Override
            public void run() {
                study_scroll.smoothScrollTo(0, 0);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                study_view.setText("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                study = String.valueOf(study_view.getText());
                if(study.equals("")){
                    Toast.makeText(getApplicationContext(), "학습 내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), Exam1.class);
                    intent.putExtra("study", study);
                    startActivity(intent);
                }

            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Bookmark.class);
                startActivity(intent);
            }
        });
    }

}

