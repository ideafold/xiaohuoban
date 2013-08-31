package com.heibuddy.xiaohuoband;

import java.util.Date;

import com.heibuddy.xiaohuoban.Xiaohuoban;
import com.heibuddy.xiaohuoban.error.XiaohuobanException;
import com.heibuddy.xiaohuoban.util.NotificationsUtil;
import com.heibuddy.xiaohuoband.preferences.Preferences;
import com.heibuddy.xiaohuoband.slidingmenuimp.SlidingMenuFragment;
import com.heibuddy.xiaohuoband.talk.BaseListItemEntity;
import com.heibuddy.xiaohuoband.talk.BaseSendEntity;
import com.heibuddy.xiaohuoband.talk.SimpleAnswerItemEntity;
import com.heibuddy.xiaohuoband.talk.TeachMsgSendEntity;
import com.heibuddy.xiaohuoband.talk.support.TalkProxy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
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

import com.heibuddy.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class TeachActivity extends SlidingFragmentActivity {
    public static final String TAG = "TeachActivity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;

    private AsyncTask<Void, Void, BaseListItemEntity> mTeachTask;

    private EditText mQuestionEditText;
    private EditText mAnswerEditText;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.teach_main);
        
        // Set up the UI.
        ensureUi();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new SlidingMenuFragment())
		.commit();
		
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		
        // Re-task if the request was cancelled.
        mTeachTask = (TeachTask) getLastNonConfigurationInstance();
        if (mTeachTask != null && mTeachTask.isCancelled()) {
            Log.d(TAG, "TeachTask previously cancelled, trying again.");
            mTeachTask = new TeachTask().execute();
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
	protected void onNewIntent(Intent intent) {
    	SlidingMenu sm = getSlidingMenu();
        if (sm != null &&sm.isMenuShowing())
        {
            sm.toggle();
        }
		
		setIntent(intent);
		super.onNewIntent(intent);
	}
    
    private ProgressDialog showProgressDialog() {
        if (mProgressDialog == null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(R.string.teach_dialog_title);
            dialog.setMessage(getString(R.string.teach_dialog_message));
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
        final Button teachButton = (Button) findViewById(R.id.teach_fragment_teach_button);
        teachButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTeachTask = new TeachTask().execute();
            }
        });
        teachButton.setEnabled(false);
        
        mQuestionEditText = ((EditText) findViewById(R.id.question));
        mAnswerEditText = ((EditText) findViewById(R.id.answer));

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	teachButton.setEnabled(usernameEditTextFieldIsValid()
                        && passwordEditTextFieldIsValid());
            }

            private boolean usernameEditTextFieldIsValid() {
                // This can be either a phone number or username so we don't
                // care too much about the
                // format.
                return !TextUtils.isEmpty(mQuestionEditText.getText());
            }

            private boolean passwordEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mAnswerEditText.getText());
            }
        };

        mQuestionEditText.addTextChangedListener(fieldValidatorTextWatcher);
        mAnswerEditText.addTextChangedListener(fieldValidatorTextWatcher);
    }

    private class TeachTask extends AsyncTask<Void, Void, BaseListItemEntity> {
        private static final String TAG = "TeachTask";
        private static final boolean DEBUG = XiaohuobandSettings.DEBUG;

        private Exception mReason;

        @Override
        protected void onPreExecute() {
        	Log.d(TAG, "onPreExecute()");
            showProgressDialog();
        }

        @Override
        protected BaseListItemEntity doInBackground(Void... params) {
            Log.d(TAG, "doInBackground()");
            
            Xiaohuoband xiaohuoband = (Xiaohuoband) TeachActivity.this.getApplication();
			String fromUserName = xiaohuoband.getUserName();
			long now = new Date().getTime() / 1000;	//in seconds
			String userId = xiaohuoband.getUserId(); 
			
			Log.e(TAG, "fromUserName: " + fromUserName);
			Log.e(TAG, "userid: " + userId);
			
            String question = mQuestionEditText.getText().toString();
            String answer = mAnswerEditText.getText().toString();
            
			BaseListItemEntity entity = null;
            try {
            	TeachMsgSendEntity msg = new TeachMsgSendEntity("server", fromUserName, String.valueOf(now), userId, 
															question, answer, null, false);
            	entity = TalkProxy.sendMessage(msg, TeachActivity.this);
            } catch (Exception e) {
                Log.d(TAG, "Caught Exception teaching in.", e);
                mReason = e;
            }
            
            return entity;
        }

        @Override
        protected void onPostExecute(BaseListItemEntity entity) {
            Log.d(TAG, "onPostExecute()");

            if (entity != null)
            {
            	SimpleAnswerItemEntity sa = (SimpleAnswerItemEntity)entity;
            	boolean isSuccess = Boolean.valueOf(sa.getContent());
            	if (isSuccess)
            	{
            		Toast.makeText(TeachActivity.this, "Teach success!", Toast.LENGTH_SHORT).show();
            		mQuestionEditText.setText("");
            		mAnswerEditText.setText("");
            	}
            	else
            	{
            		NotificationsUtil.ToastReasonForFailure(TeachActivity.this, mReason);
            	}
            }
            else
            {
            	NotificationsUtil.ToastReasonForFailure(TeachActivity.this, mReason);
            }
            
            dismissProgressDialog();
        }

        @Override
        protected void onCancelled() {
            dismissProgressDialog();
        }
    }
}
