package com.example.gpt_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class Bookmark extends Activity {
    final static String folder_name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/MOIRAI";
    final static String file_name = "count.txt";
    ScrollView bookmark_scroll;
    TextView Question_view;

    Button backToMain;
    LinearLayout Question_linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        Intent intent = getIntent();

        int count = countFiles(folder_name);

        backToMain = findViewById(R.id.backToMain);

        bookmark_scroll = findViewById(R.id.bookmark_scroll);
        Question_view = findViewById(R.id.Question_view);
        Question_linear = findViewById(R.id.Question_Linear);

        try {
            ArrayList<String> fileContents = readAllFilesFromFolder(folder_name, file_name);
            for(int i = 0;i < fileContents.size();i++){
                TextView textView = new TextView(this);
                textView.setText(fileContents.get(i));

                //drawable 에서 사전에 만든 텍스트 뷰 속성을 넣음
                textView.setBackgroundResource(R.drawable.border);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,     // width
                        LinearLayout.LayoutParams.WRAP_CONTENT      // height
                );

                // Convert 10dp to pixels
                int marginInPixels = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
                params.setMargins(marginInPixels, 0, marginInPixels, 0);

                // 위아래 간격을 넓히기 위해 추가 마진 설정
                params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);

                textView.setLayoutParams(params);
                Question_linear.addView(textView);
            }

            /*
            이제 여기에 recycleview를 쓰든 scroll을 쓰던 해서 리스트 길이만큼 각각의 textview를 만들고
            거기에 fileContents 배열에 있는 내용을 하나씩 넣어주면 됨.
             */

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        bookmark_scroll.post(new Runnable() {
            @Override
            public void run() {
                bookmark_scroll.smoothScrollTo(0, 0);
            }
        });

        backToMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });


    }

    private ArrayList<String> readAllFilesFromFolder(String foldername, String excludefilename) {
        ArrayList<String> fileContents = new ArrayList<>();
        File folder = new File(foldername);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile() && !file.getName().equals(excludefilename)) {
                        try {
                            //Question_view.setVisibility(View.GONE);
                            FileInputStream fis = new FileInputStream(file);
                            byte[] result = new byte[fis.available()];
                            int resultLength = fis.read(result);
                            String saved_question = new String(result, 0, resultLength, "utf-8");
                            fileContents.add(saved_question);
                            fis.close();
                            Question_view.setVisibility(View.GONE);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error reading file: " + file.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "읽어올 파일이 없음", Toast.LENGTH_SHORT).show();
        }
        return fileContents;
    }

    public static int countFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            int count = 0;
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                    count++;
                }
            }
            return count;
        } else {
            System.out.println("₩");
            return 0;
        }
    }



}