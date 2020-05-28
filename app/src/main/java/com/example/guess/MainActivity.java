package com.example.guess;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.TextViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    int currentLevel = 0;
    NavigationView navigationView;
    SharedPreferences prefs;

    TextView last;
    TextView first;
    String level;
    ArrayList<String> levels;
    LinearLayout layout;
    MainActivity mainActivity = this;
    Button guess;

    MyDataBaseManager mDatabase;
    ArrayList<EditText> unknowns = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menu);

        mDatabase = new MyDataBaseManager(this,this);

        layout = findViewById(R.id.unknown);

        navigationView = findViewById(R.id.nav_view);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        currentLevel = prefs.getInt("level",0);
        // TODO: delete this line
        currentLevel = 0;
        // preparing the menu
        for (int i = 0; i < currentLevel;i++){
            MenuItem passedItem = navigationView.getMenu().add(getString(R.string.level)+i);
            passedItem.setIcon(R.drawable.passed);
            passedItem.setChecked(true);
        }
        for(int i = currentLevel; i < mDatabase.getLevels().size(); i++){
            MenuItem item = navigationView.getMenu().add(getString(R.string.level)+i);
            item.setIcon(R.drawable.not);
            item.setEnabled(false);
        }
        navigationView.getMenu().getItem(currentLevel).setEnabled(true).setIcon(R.drawable.in);

        levels = mDatabase.getLevels();
        level  = mDatabase.getLevels().get(currentLevel);
        first = findViewById(R.id.first);

        // preparing level
        first.setText(Character.toString(level.charAt(0)));

        // fixing the display according to  ext size
        layout.setWeightSum(level.length()-2);
        // generate views
        final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        for (int i = 0; i < level.length()-2; i++){
            final EditText unknown = new EditText(this);
            unknown.setFilters(new InputFilter[] {new InputFilter.LengthFilter(1)});
            unknown.setLayoutParams(param);
            TextViewCompat.setAutoSizeTextTypeWithDefaults(unknown, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM );
            unknown.setTextColor(Color.LTGRAY);
            unknown.setGravity(View.TEXT_ALIGNMENT_CENTER);
            unknown.setTextSize(unknown.getTextSize() +1);
            layout.addView(unknown);
            unknowns.add(unknown);
        }

        for (EditText s : unknowns) {
            s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (EditText sa : unknowns) sa.setTextColor(Color.LTGRAY);
                }
            });
           /* s.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    return false;
                }
            });*/
        }

        last = findViewById(R.id.last);
        //last.setLayoutParams(param);
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(last, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM );
        //last.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        last.setText(Character.toString(level.charAt(level.length()-1)));
        //layout.addView(last);


        guess = findViewById(R.id.guess);
        guess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guessed = first.getText().toString();
                for(EditText s: unknowns) guessed += s.getText().toString();
                guessed += last.getText().toString();
                Toast.makeText(v.getContext(),guessed,Toast.LENGTH_LONG).show();
                if (guessed.equals(level)){
                    for (EditText s : unknowns) ((ViewManager) s.getParent()).removeView(s);
                    last.setVisibility(View.GONE);
                    first.setText(guessed);
                    first.setTextColor(Color.parseColor("#29E4B5"));
                    layout.setVisibility(View.GONE);
                    // making toast for congrats
                    hideKeyboard(mainActivity);
                    LinearLayout view = new LinearLayout(mainActivity);
                    view.setLayoutParams(param);
                    view.setBackgroundColor(Color.WHITE);
                    view.setOrientation(LinearLayout.VERTICAL);
                    ImageView emoji = new ImageView(mainActivity);
                    emoji.setImageResource(R.drawable.smile);
                    emoji.setMinimumWidth(layout.getWidth());
                    emoji.setMinimumHeight(layout.getWidth());
                    view.addView(emoji);
                    TextView text = new TextView(mainActivity);
                    text.setText(R.string.msg_win);
                    text.setTextSize(50);
                    text.setTextColor(Color.parseColor("#FBFF80"));
                    view.addView(text);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                    /*first.setLayoutParams(
                            new TableLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    );*/
                    nextLevel();
                }else{
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f
                    );
                    for (EditText s : unknowns) s.setTextColor(Color.RED);
                    LinearLayout view = new LinearLayout(mainActivity);
                    view.setLayoutParams(param);
                    view.setBackgroundColor(Color.WHITE);
                    view.setOrientation(LinearLayout.VERTICAL);
                    ImageView emoji = new ImageView(mainActivity);
                    emoji.setImageResource(R.drawable.fals);
                    emoji.setMinimumWidth(layout.getWidth());
                    emoji.setMinimumHeight(layout.getWidth());
                    view.addView(emoji);
                    TextView text = new TextView(mainActivity);
                    text.setText(R.string.msg_fail);
                    text.setTextSize(50);
                    text.setTextColor(Color.parseColor("#9D131A"));
                    view.addView(text);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();
                }
            }
        });

       /* navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        if(menuItem.getItemId()==R.id.level1)
                            Toast.makeText(getApplicationContext(),"level1",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mDrawerLayout.isOpen()) {mDrawerLayout.close();}
                else {mDrawerLayout.openDrawer(GravityCompat.START);}
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        mDatabase.close();
    }

    private void nextLevel(){
        if ( currentLevel != levels.size()-1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout.open();
                        MenuItem currentItem = navigationView.getMenu().getItem(currentLevel);
                        currentItem.setIcon(R.drawable.passed);
                        currentItem.setChecked(true);
                        MenuItem nextItem = navigationView.getMenu().getItem(++currentLevel);
                        nextItem.setIcon(R.drawable.in);
                        nextItem.setEnabled(true);
                        prefs.edit().putInt("level", currentLevel).apply();
                        level = levels.get(currentLevel);
            /*first.setLayoutParams(
                    new TableLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            );*/
                        // preparing level
                        first.setText(Character.toString(level.charAt(0)));
                        first.setTextColor(Color.GRAY);
                        // fixing the display according to  ext size
                        layout.setWeightSum(level.length() - 2);
                        // generate views
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1.0f
                        );
                        unknowns.clear();
                        for (int i = 0; i < level.length() - 2; i++) {
                            final EditText unknown = new EditText(mainActivity);
                            unknown.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
                            unknown.setLayoutParams(param);
                            TextViewCompat.setAutoSizeTextTypeWithDefaults(unknown, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                            unknown.setTextColor(Color.LTGRAY);
                            unknown.setTextSize(unknown.getTextSize() + 1);
                            layout.addView(unknown);
                            unknowns.add(unknown);
                        }

                        for (EditText s : unknowns) {
                            s.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (EditText sa : unknowns) sa.setTextColor(Color.LTGRAY);
                                }
                            });
                        }
            /* s.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        return false;
                    }
                });*/
                        layout.setVisibility(View.VISIBLE);
                        //last.setLayoutParams(param);
                        //TextViewCompat.setAutoSizeTextTypeWithDefaults(last, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM );
                        //last.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                        last.setText(Character.toString(level.charAt(level.length() - 1)));
                        //layout.addView(last);
                        last.setVisibility(View.VISIBLE);
                    }
                }, true, 3000);
            } else {
                MenuItem currentItem = navigationView.getMenu().getItem(currentLevel);
                currentItem.setIcon(R.drawable.passed);
                currentItem.setChecked(true);
                MenuItem nextItem = navigationView.getMenu().getItem(++currentLevel);
                nextItem.setIcon(R.drawable.in);
                nextItem.setEnabled(true);
                prefs.edit().putInt("level", currentLevel).apply();
                level = levels.get(currentLevel);
            /*first.setLayoutParams(
                    new TableLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            );*/
                // preparing level
                first.setText(Character.toString(level.charAt(0)));
                first.setTextColor(Color.GRAY);
                // fixing the display according to  ext size
                layout.setWeightSum(level.length() - 2);
                // generate views
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                unknowns.clear();
                for (int i = 0; i < level.length() - 2; i++) {
                    final EditText unknown = new EditText(this);
                    unknown.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
                    unknown.setLayoutParams(param);
                    TextViewCompat.setAutoSizeTextTypeWithDefaults(unknown, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    unknown.setTextColor(Color.LTGRAY);
                    unknown.setTextSize(unknown.getTextSize() + 1);
                    layout.addView(unknown);
                    unknowns.add(unknown);
                }

                for (EditText s : unknowns) {
                    s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (EditText sa : unknowns) sa.setTextColor(Color.LTGRAY);
                        }
                    });
                }
            /* s.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        return false;
                    }
                });*/
                layout.setVisibility(View.VISIBLE);
                //last.setLayoutParams(param);
                //TextViewCompat.setAutoSizeTextTypeWithDefaults(last, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM );
                //last.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                last.setText(Character.toString(level.charAt(level.length() - 1)));
                //layout.addView(last);
                last.setVisibility(View.VISIBLE);
            }
        }else{
            hideKeyboard(mainActivity);
            LinearLayout view = new LinearLayout(mainActivity);
            view.setBackgroundColor(Color.WHITE);
            view.setOrientation(LinearLayout.VERTICAL);
            ImageView emoji = new ImageView(mainActivity);
            emoji.setMinimumWidth(layout.getWidth());
            emoji.setMinimumHeight(layout.getWidth());
            view.addView(emoji);
            TextView text = new TextView(mainActivity);
            text.setText(R.string.finish);
            text.setTextSize(50);
            text.setTextColor(Color.parseColor("#03DAC5"));
            view.addView(text);
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(view);
            toast.show();
            guess.setOnClickListener(null);
        }

        for (EditText s : unknowns) {
            s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (EditText sa : unknowns) sa.setTextColor(Color.LTGRAY);
                }
            });
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
