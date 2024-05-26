package com.example.gpt_test;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Exam1 extends Activity {

    ScrollView question_scroll, answer_scroll;
    String explanation_response;
    //String test;
    String ques;
    long duration;
    int API_number;
    //문제와 해설 API를 구분하기 위한 변수
    TextView question_view;
    TextView answer_view;
    Button answer_button, new_question;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;
    private static final String MY_SECRET_KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam1);

        question_scroll = findViewById(R.id.question_scroll);
        answer_scroll = findViewById(R.id.answer_scroll);
        question_view = findViewById(R.id.question_view);
        answer_view = findViewById(R.id.answer_view);
        answer_button = findViewById(R.id.answer_button);
        new_question = findViewById(R.id.new_question);

        // 첫 페이지에서 학습내용 받아오기
        Intent intent = getIntent();
        String study = intent.getStringExtra("study");
        // (디버그용) 임시 학습 자료
        // test = "운영체제 개념";
        ques = study.trim();
        // 앞뒤 공백을 제거

        // (디버그용) 매개 값 전달 확인용
        // question_view.setText(ques);

        question_scroll.post(new Runnable() {
            @Override
            public void run() {
                question_scroll.smoothScrollTo(0, 0);
            }
        });
        answer_scroll.post(new Runnable() {
            @Override
            public void run() {
                answer_scroll.smoothScrollTo(0, 0);
            }
        });

        client = new OkHttpClient().newBuilder()
                .connectTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        //서버에 접속하기 위한 클라이언트 설정
        if (true) {
            callAPI(ques);
        }
        answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 3초 뒤에 실행될 코드
                        addResponse("해설 생성 중");
                    }
                }, 200); // 200ms = 0.2초

                // Handler를 사용하여 3초(3000ms) 뒤에 실행될 작업을 예약
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 3초 뒤에 실행될 코드
                        addResponse(explanation_response);
                    }
                }, 3000); // 3000ms = 3초
            }
        });
        //정답 및 해설 버튼 click 이벤트 설정

        new_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question_view.setText("문제: ");
                answer_view.setText("정답: ");
                //question_view, answer_view 초기화
                callAPI(ques);
                //새로운 문제 API 호출
            }
        });
        //새로운 문제 요청 버튼 click 이벤트 설정
    }

    void addResponse(String response) {
        if (API_number == 1) {
            question_view.setText("문제: " + response);
        }
        else {
            answer_view.setText(response);
        }
        //API_number 변수를 통해 문제와 해설 response를 구분
    }

    //문제 생성 API 호출 함수 정의
    void callAPI(String question) {
        question_view.setText("문제: 문제 생성 중");
        //callAPI 함수를 통해 문제 생성시 문제 생성 중 문구 표시
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        //API 요청을 위해 AI 설정 및 사용자 요청을 포함한 JSON 객체, Array를 생성

        try {
            baseAi.put("role", "user");
            baseAi.put("content", "너는 제공된 데이터에 관한 주관식 문제 하나를 답이나 힌트 없이 만들어주는 AI 도우미야." +
                    "그리고 이전에 만들었던 문제와 중복되지 않는 문제를 만들어." +
                    "그리고 가장 중요한 것은 너는 문제에 대한 답이나 힌트를 제공하지 않 아."+
                    "그리고 문제는 하오체를 써서 만들어야해."+
                    "그리고 함수가 되었든 아니면 어떠한 개념이 되었든 문제를 풀기 위한 조건들이 필요한 경우 조건들을 절대 누락시키면 안돼!");
            //AI 속성 설정 -> AI의 역할을 정의 및 지시
            //AI 속성 설정 조정 -> 문제 생성시 문제에 출력 부분에서 오류 해결
            userMsg.put("role", "user");
            userMsg.put("content", question);
            //사용자 요청 설정 -> 사용자의 요청을 정의 및 지시
            arr.put(baseAi);
            arr.put(userMsg);
            //앞서 설정한 것들을 JSON Array에 추가
        } catch (JSONException e) {
            e.printStackTrace();
            addResponse("Failed to create JSON request: " + e.getMessage());
            return;
        }

        JSONObject object = new JSONObject();
        //API 요청을 위해 최종 요청 객체인 JSON 객체를 생성
        try {
            object.put("model", "gpt-3.5-turbo-0125");
            object.put("messages", arr);
            //object 객체에 AI 모델과 AI 속성 설정과 사용자 요청을 포함한 JSON 배열을 추가
        } catch (JSONException e) {
            e.printStackTrace();
            addResponse("Failed to create JSON request: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);
        //최종 JSON 객체를 string 타입 변환 -> API 요청 본문 body 생성, JSON 은 미디어 타입
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(body)
                .build();
        //Request 객체를 빌드
        API_number = 1;
        //API_number 변수를 통해 문제 생성 요청임을 표시

        client.newCall(request).enqueue(new Callback() {
            //비동기 방식 API 요청 실행, 콜백을 설정
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addResponse("Failed to load response due to " + e.getMessage());
                    }
                });
            }
            // API 요청 실패시 호출 함수 정의
            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    //API response body를 string 타입으로 변환
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        //string 타입으로 바꾼 API response body를 JSON 객체로 변환
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        JSONObject choice = choices.getJSONObject(0).getJSONObject("message");
                        String content = choice.getString("content");
                        //jsonResponse에서 key값 index등을 통해 해당 content 추출

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addResponse(content.trim()+"\n");
                                API_number = 2;
                                fetchExplanation(content.trim());
                            }
                        });
                        /*runOnUiThread를 통해 UI 스레드 에서 작업을 수행
                          -> 문제 생성 결과로 얻은 content의 공백을 제거하고 response 함수를 통해 question_view에 출력
                          -> fetchExplanation함수를 호출해 해설 생성 */

                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addResponse("Failed to parse response: " + e.getMessage());
                            }
                        });
                        //JSON 객체로 변환 실패시 호출 함수 정의
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                addResponse("Failed to load response: " + response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //API 요청 실패시 호출 함수 정의
                }
            }
        });
    }

    //해설 생성 API 호출 함수 정의
    void fetchExplanation(String content) {
        long startTime = System.currentTimeMillis(); // 요청 시작 시간 기록
        JSONArray explanationArr = new JSONArray();
        JSONObject explanationBaseAi = new JSONObject();
        JSONObject explanationMsg = new JSONObject();
        //API 요청을 위해 AI 설정 및 사용자 요청을 포함한 JSON 객체, Array를 생성

        try {
            explanationBaseAi.put("role", "user");
            explanationBaseAi.put("content", "너는 제공된 데이터에 관한 정답을 만드는 AI 도우미야."
                    + "문제에 대한 간단한 정답을 만들어"
                    + "그리고 줄바꿈을 하고 문제의 풀이 과정이나 해설을 만들어"
                    + "그리고 한글로 만들어야해."
            );
            explanationMsg.put("role", "user");
            explanationMsg.put("content", content);
            //AI 속성, 사용자 요청 설정
            explanationArr.put(explanationBaseAi);
            explanationArr.put(explanationMsg);
            //앞서 설정한 것들을 JSON Array에 추가
        } catch (JSONException e) {
            e.printStackTrace();
            addResponse("Failed to create JSON request: " + e.getMessage());
            return;
        }

        JSONObject explanationObject = new JSONObject();
        //API 요청을 위해 최종 요청 객체인 JSON 객체를 생성
        try {
            explanationObject.put("model", "gpt-3.5-turbo-0125");
            explanationObject.put("messages", explanationArr);
            //object 객체에 AI 모델과 AI 속성 설정과 사용자 요청을 포함한 JSON 배열을 추가
        } catch (JSONException e) {
            e.printStackTrace();
            addResponse("Failed to create JSON request: " + e.getMessage());
            return;
        }

        RequestBody explanationBody = RequestBody.create(explanationObject.toString(), JSON);
        //최종 JSON 객체를 string 타입 변환 -> API 요청 본문 body 생성, JSON 은 미디어 타입
        Request explanationRequest = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + MY_SECRET_KEY)
                .post(explanationBody)
                .build();
        //Request 객체를 빌드
        client.newCall(explanationRequest).enqueue(new Callback() {
            //비동기 방식 API 요청 실행, 콜백을 설정
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addResponse("Failed to load explanation due to " + e.getMessage());
                    }
                });
                // API 요청 실패시 호출 함수 정의
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                long endTime = System.currentTimeMillis(); // 요청 성공 시간 기록
                duration = endTime - startTime; // 경과 시간 계산
                // API 요청 성공 시간 기록

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    //API response body를 string 타입으로 변환

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        JSONObject choice = jsonArray.getJSONObject(0).getJSONObject("message");
                        String explanation_result = choice.getString("content");
                        //jsonResponse body에서 key값 index등을 통해 해당 content 추출

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                explanation_response = explanation_result.trim()+"\n";
                                //해설 생성 요청으로 얻은 explanation_result의 공백을 제거 및 글로벌 변수 explanation_response에 값으로 선언
                                //출력에서 해설 부분의 마지막 줄이 일부분 잘려서 나오는 문제를 "\n"을 추가해서 해결
                            }
                        });
                        //이하 callAPI와 동일
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addResponse("Failed to parse explanation: " + e.getMessage());
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                addResponse("Failed to load explanation: " + response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}

