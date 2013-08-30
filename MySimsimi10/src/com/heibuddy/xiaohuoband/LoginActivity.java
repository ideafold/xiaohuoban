package com.heibuddy.xiaohuoband;

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
import android.widget.Toast;

public class LoginActivity extends Activity {
    public static final String TAG = "LoginActivity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;

    private AsyncTask<Void, Void, Boolean> mLoginTask;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        
        Preferences.logoutUser(((Xiaohuoband) getApplication()).getXiaohuoban(), 
                				PreferenceManager.getDefaultSharedPreferences(this).edit());
 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_main);
        
        // Set up the UI.
        ensureUi();

        // Re-task if the request was cancelled.
        //mLoginTask = (LoginTask) getLastNonConfigurationInstance();
        mLoginTask = new LoginTask();
        if (mLoginTask != null && mLoginTask.isCancelled()) {
            Log.d(TAG, "LoginTask previously cancelled, trying again.");
            mLoginTask = new LoginTask().execute();
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
        if (mLoginTask != null) {
            mLoginTask.cancel(true);
        }
        
        return mLoginTask;
    }

    private ProgressDialog showProgressDialog() {
        if (mProgressDialog == null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(R.string.login_dialog_title);
            dialog.setMessage(getString(R.string.login_dialog_message));
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
                Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        
        final Button loginButton = (Button) findViewById(R.id.login_fragment_login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginTask = new LoginTask().execute();
            }
        });
        loginButton.setEnabled(false);
        
        mUsernameEditText = ((EditText) findViewById(R.id.username));
        mPasswordEditText = ((EditText) findViewById(R.id.password));

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	loginButton.setEnabled(usernameEditTextFieldIsValid()
                        && passwordEditTextFieldIsValid());
            }

            private boolean usernameEditTextFieldIsValid() {
                // This can be either a phone number or username so we don't
                // care too much about the
                // format.
                return !TextUtils.isEmpty(mUsernameEditText.getText());
            }

            private boolean passwordEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mPasswordEditText.getText());
            }
        };

        mUsernameEditText.addTextChangedListener(fieldValidatorTextWatcher);
        mPasswordEditText.addTextChangedListener(fieldValidatorTextWatcher);
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "LoginTask";
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
                    .getDefaultSharedPreferences(LoginActivity.this);
            Editor editor = prefs.edit();
            Xiaohuoband xiaohuoband = (Xiaohuoband) getApplication();
            Xiaohuoban xiaohuoban = xiaohuoband.getXiaohuoban();
            
            try {
                String username = mUsernameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                boolean loggedIn = Preferences.loginUser(xiaohuoban, username, password, editor);

                // Make sure prefs makes a round trip.
                String userId = Preferences.getUserId(prefs);
                if (TextUtils.isEmpty(userId)) {
                    Log.d(TAG, "Preference store calls failed");
                    throw new XiaohuobanException(getResources().getString(
                            R.string.login_failed_login_toast));
                }

                return loggedIn;
            } catch (Exception e) {
                Log.d(TAG, "Caught Exception logging in.", e);
                mReason = e;
                Preferences.logoutUser(xiaohuoban, editor);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {
            Log.d(TAG, "onPostExecute(): " + loggedIn);
            if (loggedIn) {
                sendBroadcast(new Intent(Xiaohuoband.INTENT_ACTION_LOGGED_IN));
                Toast.makeText(LoginActivity.this, getString(R.string.login_welcome_toast),
                        Toast.LENGTH_LONG).show();

                // Launch the main activity to let the user do anything.
//                Intent intent = new Intent(LoginActivity.this, TalkActivity.class);
                Intent intent = new Intent(LoginActivity.this, TalkActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Be done with the activity.
                finish();
            } else {
                sendBroadcast(new Intent(Xiaohuoband.INTENT_ACTION_LOGGED_OUT));
                NotificationsUtil.ToastReasonForFailure(LoginActivity.this, mReason);
            }
            dismissProgressDialog();
        }

        @Override
        protected void onCancelled() {
            dismissProgressDialog();
        }
    }
}
