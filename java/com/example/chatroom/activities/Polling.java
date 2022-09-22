package com.example.chatroom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.chatroom.R;
import com.example.chatroom.databinding.PollBinding;
import com.example.chatroom.models.User;
import com.example.chatroom.utilities.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Polling extends AppCompatActivity {

    SeekBar sb1,sb2,sb3,sb4;
    EditText op1,op2,op3,op4;
    TextView p1,p2,p3,p4;
    Button b,b1,b2,b3,b4,b5,b6,b7;

    double c1=0,c2=0,c3=0,c4=0;
    boolean f1=true,f2=true,f3=true,f4=true;
    private PollBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("User");
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PollBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         //setContentView(R.layout.activity_main);
        //User receive = (com.example.chatroom.models.User) getIntent().getSerializableExtra(Constants.KEY_USER);
        sb1=findViewById(R.id.bar);
        sb2=findViewById(R.id.bar1);
        sb3=findViewById(R.id.bar2);
        sb4=findViewById(R.id.bar3);
        op1=findViewById(R.id.option);
        op2=findViewById(R.id.option1);
        op3=findViewById(R.id.option2);
        op4=findViewById(R.id.option3);
        p1=findViewById(R.id.percent1);
        p2=findViewById(R.id.percent2);
        p3=findViewById(R.id.percent3);
        p4=findViewById(R.id.percent4);
        b=findViewById(R.id.button);
        b1=findViewById(R.id.button1);
        b2=findViewById(R.id.button2);
        b3=findViewById(R.id.button3);
        b4=findViewById(R.id.button4);
        b5=findViewById(R.id.button5);
        b6=findViewById(R.id.button6);
        b7=findViewById(R.id.button7);

        sb1.setOnTouchListener((view, motionEvent) -> false);
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        b.setOnClickListener(view -> {
            String name = op1.getText().toString();
            op1.setText(name);
        });
        b1.setOnClickListener(view -> {
            if(f1){
                c1++;
                f1=false;
                f2=false;
                f3=false;
                f4=false;
                calculatepercentage();
                //databasesend(receive,1);
            }
        });
        sb2.setOnTouchListener((view, motionEvent) -> false);
        b2.setOnClickListener(view -> {
            String name = op2.getText().toString();
            op2.setText(name);
        });
        b3.setOnClickListener(view -> {
            if(f2){
                c2++;
                f1=false;
                f2=false;
                f3=false;
                f4=false;
                calculatepercentage();
                //databasesend(receive,2);
            }
        });
        sb3.setOnTouchListener((view, motionEvent) -> false);
        b4.setOnClickListener(view -> {
            String name = op3.getText().toString();
            op3.setText(name);
        });
        b5.setOnClickListener(view -> {
            if(f3){
                c3++;
                f1=false;
                f2=false;
                f3=false;
                f4=false;
                calculatepercentage();
                //databasesend(receive,3);
            }
        });
        sb4.setOnTouchListener((view, motionEvent) -> false);
        b6.setOnClickListener(view -> {
            String name = op4.getText().toString();
            op4.setText(name);
        });
        b7.setOnClickListener(view -> {
            if(f4){
                c4++;
                f1=false;
                f2=false;
                f3=false;
                f4=false;
                calculatepercentage();
                //databasesend(receive,4);
            }
        });


    }
    private void databasesend(User receive,Integer option){
        String id = myRef.push().getKey();
        //problem is here   |    ????
        //                  v
        User send = new User(receive.toString(),option);
        assert id != null;
        myRef.child(id).setValue(send);
       // Intent intent = new Intent(Polling.this,GroupChatActivity.class);
       // startActivity(intent);


    }


    private void calculatepercentage() {
        double per1=(c1);
        double per2=(c2);
        double per3=(c3);
        double per4=(c4);
        p1.setText(String.format("%.0f%%",per1));
        sb1.setProgress((int)per1);
        p2.setText(String.format("%.0f%%",per2));
        sb2.setProgress((int)per2);
        p3.setText(String.format("%.0f%%",per3));
        sb3.setProgress((int)per3);
        p4.setText(String.format("%.0f%%",per4));
        sb4.setProgress((int)per4);
    }
}
