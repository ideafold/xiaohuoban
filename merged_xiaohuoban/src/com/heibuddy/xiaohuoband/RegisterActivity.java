package com.heibuddy.xiaohuoband;

import com.heibuddy.R;

import com.heibuddy.xiaohuoban.Xiaohuoban;
import com.heibuddy.xiaohuoban.error.XiaohuobanException;
import com.heibuddy.xiaohuoban.util.NotificationsUtil;
import com.heibuddy.xiaohuoband.preferences.Preferences;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    public static final String TAG = "RegisterActivity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;

    private AsyncTask<Void, Void, Boolean> mRegisterTask;

    private EditText mUsernameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Preferences.logoutUser(((Xiaohuoband) getApplication()).getXiaohuoban(), 
                				PreferenceManager.getDefaultSharedPreferences(this).edit());
        
        setContentView(R.layout.register_main);
        // Set up the UI.
        ensureUi();

        // Re-task if the request was cancelled.
        mRegisterTask = (RegisterTask) getLastNonConfigurationInstance();
        if (mRegisterTask != null && mRegisterTask.isCancelled()) {
            Log.d(TAG, "RegisterTask previously cancelled, trying again.");
            mRegisterTask = new RegisterTask().execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Xiaohuoband) getApplication()).requestLocationUpdates(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Xiaohuoband) getApplication()).removeLocationUpdates();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        Log.d(TAG, "onRetainNonConfigurationInstance()");
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        return mRegisterTask;
    }

    private ProgressDialog showProgressDialog() {
        if (mProgressDialog == null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(R.string.register_dialog_title);
            dialog.setMessage(getString(R.string.register_dialog_message));
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            mProgressDialog = dialog;
        }
        mProgressDialog.show();
        return mProgressDialog;
    }

    private void dismissProgressDialog() {
        try {
            mProgressDialog.dismiss();
        } catch (IllegalArgumentException e) {
            // We don't mind. android cleared it for us.
        }
    }

    private void ensureUi() {
    	final ImageView backButton = (ImageView) findViewById(R.id.action_bar_button_back);
    	backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    	
        final Button registerButton = (Button) findViewById(R.id.signUpButton);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterTask = new RegisterTask().execute();
            }
        });
        registerButton.setEnabled(false);
        
        mUsernameEditText = ((EditText) findViewById(R.id.username));
        mPasswordEditText = ((EditText) findViewById(R.id.password));
        mEmailEditText = ((EditText) findViewById(R.id.email));

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	registerButton.setEnabled(usernameEditTextFieldIsValid()
                        && passwordEditTextFieldIsValid()
                        && emailEditTextFieldIsValid());
            }

            private boolean usernameEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mUsernameEditText.getText());
            }

            private boolean passwordEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mPasswordEditText.getText());
            }
            
            private boolean emailEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mEmailEditText.getText());
            }            
        };

        mUsernameEditText.addTextChangedListener(fieldValidatorTextWatcher);
        mPasswordEditText.addTextChangedListener(fieldValidatorTextWatcher);
        mEmailEditText.addTextChangedListener(fieldValidatorTextWatcher);
    }

    private class RegisterTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "RegisterTask";
        private static final boolean DEBUG = XiaohuobandSettings.DEBUG;

        private Exception mReason;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "doInBackground()");
            
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(RegisterActivity.this);
            Editor editor = prefs.edit();
            Xiaohuoband xiaohuoband = (Xiaohuoband) getApplication();
            Xiaohuoban xiaohuoban = xiaohuoband.getXiaohuoban();
            
            try {
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String email = mEmailEditText.getText().toString();
                boolean registered = Preferences.registerUser(xiaohuoban, username, email, password, editor);

                // Make sure prefs makes a round trip.
                String userId = Preferences.getUserId(prefs);
                if (TextUtils.isEmpty(userId)) {
                    Log.d(TAG, "Preference store calls failed");
                    throw new XiaohuobanException(getResources().getString(
                            R.string.register_failed_register_toast));
                }

                return registered;
            } catch (Exception e) {
                Log.d(TAG, "Caught Exception registering.", e);
                mReason = e;
                Preferences.logoutUser(xiaohuoban, editor);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean registered) {
            Log.d(TAG, "onPostExecute(): " + registered);
            if (registered) {
                sendBroadcast(new Intent(Xiaohuoband.INTENT_ACTION_LOGGED_IN));
                Toast.makeText(RegisterActivity.this, getString(R.string.register_welcome_toast),
                        Toast.LENGTH_LONG).show();

                // Launch the main activity to let the user do anything.
                Intent intent = new Intent(RegisterActivity.this, TalkActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Be done with the activity.
                finish();
            } else {
                sendBroadcast(new Intent(Xiaohuoband.INTENT_ACTION_LOGGED_OUT));
                NotificationsUtil.ToastReasonForFailure(RegisterActivity.this, mReason);
            }
            dismissProgressDialog();
        }

        @Override
        protected void onCancelled() {
            dismissProgressDialog();
        }
    }
}
