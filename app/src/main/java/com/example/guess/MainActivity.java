package com.example.guess;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MyDataBaseManager mDatabase;
    ArrayList<EditText> unknowns = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = new MyDataBaseManager(this);

        LinearLayout layout = findViewById(R.id.lay);

        final String level1  = mDatabase.getLevels().get(0);
        final TextView first = findViewById(R.id.first);
        first.setText(Character.toString(level1.charAt(0)));

        // generate views
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        for (int i = 0; i < level1.length()-2; i++){
            final EditText unknown = new EditText(this);
            unknown.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
            unknown.setLayoutParams(param);
            //unknown.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            unknown.setTextSize(first.getTextSize());
            unknown.setTextColor(Color.LTGRAY);
            layout.addView(unknown);

            unknowns.add(unknown);
        }

        for (EditText s : unknowns) {
            s.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    for (EditText sa : unknowns) sa.setTextColor(Color.LTGRAY);
                    return false;
                }
            });
        }

        final TextView last = new TextView(this);
        last.setLayoutParams(param);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(last, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM );
        last.setText(Character.toString(level1.charAt(level1.length()-1)));
        layout.addView(last);

        final Button guess = findViewById(R.id.guess);
        guess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guessed = first.getText().toString();
                for(EditText s: unknowns) guessed += s.getText().toString();
                guessed += last.getText().toString();
                Toast.makeText(v.getContext(),guessed,Toast.LENGTH_LONG).show();
                if (guessed.equals(level1)){
                    for (EditText s : unknowns) s.setVisibility(View.GONE);
                    last.setVisibility(View.GONE);
                    first.setText(guessed);
                    first.setTextColor(Color.parseColor("#50C878"));
                }else{
                    for (EditText s : unknowns) s.setTextColor(Color.RED);
                }
            }
        });
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        mDatabase.close();
    }
}
