package com.heibuddy.xiaohuoband;

import com.heibuddy.xiaohuoband.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Xiaohuoband xd = (Xiaohuoband) getApplication();
        if (xd.isReady())
        {
        	//Intent intent = new Intent(getApplicationContext(), TalkActivity.class);
        	Intent intent = new Intent(getApplicationContext(), TalkActivity.class);
            startActivity(intent); 
            finish();
        }
        
        setContentView(R.layout.layout_landing);
        
        final Button register = (Button) findViewById(R.id.toolbar_button_sign_up);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);               
            }
        });

        final Button login = (Button) findViewById(R.id.toolbar_button_login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);               
            }
        });

    }
    
    
}