package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class final1 extends AppCompatActivity {
    TextView ab;
    public int counter;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final1);
        ab=(TextView) findViewById(R.id.editTextTextPersonName2);
        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new CountDownTimer(1000, 3000){
                    public void onTick(long millisUntilFinished){
                        ab.setText(String.valueOf(counter));
                        counter--;
                    }
                    public  void onFinish(){
                        ab.setText("Ambulance is Here");
                    }
                }.start();
            }
        });
    }

}