package com.heibuddy.xiaohuoband;

import com.heibuddy.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.heibuddy.xiaohuoband.talk.autocomplete.SuggestObject;
import com.heibuddy.xiaohuoband.talk.autocomplete.BackButtonPressedEventListener;
import com.heibuddy.xiaohuoband.talk.autocomplete.AutoCompleteResultsAdapter;
import com.heibuddy.xiaohuoband.talk.autocomplete.TalkAutoCompleteTextView;
import com.heibuddy.xiaohuoband.talk.autocomplete.KeyboardService;
import com.heibuddy.xiaohuoban.error.XiaohuobanException;
import com.heibuddy.xiaohuoban.util.LocationService;
import com.heibuddy.xiaohuoban.util.NewsService;
import com.heibuddy.xiaohuoban.util.NotificationsUtil;
import com.heibuddy.xiaohuoband.location.BestLocationListener;
import com.heibuddy.xiaohuoband.talk.BaseSendEntity;
import com.heibuddy.xiaohuoband.talk.ListItemAdapter;
import com.heibuddy.xiaohuoband.talk.BaseListItemEntity;
import com.heibuddy.xiaohuoband.talk.LocationMsgSendEntity;
import com.heibuddy.xiaohuoband.talk.QuestionItemEntity;
import com.heibuddy.xiaohuoband.talk.SimpleAnswerItemEntity;
import com.heibuddy.xiaohuoband.talk.TextMsgSendEntity;
import com.heibuddy.xiaohuoband.talk.NewsMsgSendEntity;
import com.heibuddy.xiaohuoband.talk.ThinkingItemEntity;
import com.heibuddy.xiaohuoband.talk.support.TalkProxy;

public class TalkFragment extends Fragment {
    public static final String TAG = "TalkFragment";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
    public static final long MIN_GAP_TO_DISPLAY_NEWS = 1 * 180 * 60 * 1000;	//unit is millisecond
    public static final int MAX_DISPLAY_NEWS_TIME = 4;
    public static final float MIN_DISTANCE_TO_HOME = 3000.00f;
    public static final float MIN_DISTANCE_TO_LAST_LOCATION = 3000.00f;
	public static final String AUTO_COMPLETE_URL = "http://42.96.142.194:6767/face/suggest/";
	public static final int SUGGESTION_LIMIT = 5;
	
    public static enum TalkQueryType {PUBTEXT, PUBLOCATION, PUBNEWS, UNKNOWN};
    
    private StateHolder mStateHolder = new StateHolder();
    private SearchLocationObserver mSearchLocationObserver = new SearchLocationObserver();

    private KeyboardService mKeyboardService;
    
	private ListView mTalkView;
	private List<BaseListItemEntity> mList = null;
	private ListItemAdapter mListItemAdapter;
    private TalkAutoCompleteTextView mSearchField = null;
	private ImageView mAskButton;
	 
    private Drawable mStopDrawable;
    private AutoCompleteResultsAdapter mAcAdapter;
    public boolean mCleanSearchBar = false;
    
    public static final String INTENT_ACTION_PASTE_SUGGESTION = "com.heibuddy.xiaohuoband.intent.action.PASTE_SUGGESTION";
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent);
            //finish();	TODO
        }
    };
    
   private BroadcastReceiver mPasteSuggestionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent);
            if (intent.hasExtra("query"))
            {
            	String query = intent.getStringExtra("query");
            	getSearchField().pasteQuery(query);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate()");
        getActivity().setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        
        getActivity().registerReceiver(mLoggedOutReceiver, new IntentFilter(Xiaohuoband.INTENT_ACTION_LOGGED_OUT));
        getActivity().registerReceiver(mPasteSuggestionReceiver, new IntentFilter(TalkFragment.INTENT_ACTION_PASTE_SUGGESTION));
        
        mKeyboardService = new KeyboardService(getActivity());
       
    	mStopDrawable = getActivity().getResources().getDrawable(R.drawable.stop);
    	mAcAdapter = new AutoCompleteResultsAdapter(getActivity());
				
        mStateHolder = new StateHolder();
        tryToCheckAndPullNews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d(TAG, "onCreateView");
    	View root = inflater.inflate(R.layout.activity_first_tab, container, false);
		listInit(root);
		initSearchField(root);
		mAskButton = (ImageView) root.findViewById(R.id.fst_tab_buttom);
		mAskButton.setOnClickListener(mButtonAskListener);
		
    	return root;
    }
	
    private void initSearchField(View root){
    	mSearchField = (TalkAutoCompleteTextView) root.findViewById(R.id.searchEditText);
        getSearchField().setAdapter(mAcAdapter);
        
        getSearchField().setOnBackButtonPressedEventListener(new BackButtonPressedEventListener() {
			@Override
			public void onBackButtonPressed() {
				if(getSearchField().isPopupShowing()){
                    getSearchField().dismissDropDown();
				}
			}
        });

        getSearchField().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Hide the keyboard and perform a search
				getSearchField().dismissDropDown();
					
				SuggestObject suggestObject = mAcAdapter.getItem(position);
				if (suggestObject != null) {
					String text = suggestObject.getPhrase().trim();
					mKeyboardService.hideKeyboard(getSearchField());
					clearSearchBar();
					mStateHolder.startTask(TalkFragment.this, TalkQueryType.PUBLOCATION, text);
				}
			}
		});

        // This makes a little (X) to clear the search bar.
        mStopDrawable.setBounds(0, 0, (int)Math.floor(mStopDrawable.getIntrinsicWidth()/1.5), 
									  (int)Math.floor(mStopDrawable.getIntrinsicHeight()/1.5));
        getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mStopDrawable, null);

        getSearchField().setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
            	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    				mCleanSearchBar = true;
                }
            	
                if (getSearchField().getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > getSearchField().getWidth() - getSearchField().getPaddingRight() - mStopDrawable.getIntrinsicWidth()) {
                	if(getSearchField().getCompoundDrawables()[2] == mStopDrawable) {
	                	stopAction();
                	}
                	else {
                		reloadAction();
                	}
                }
                return false;
            }

        });

        getSearchField().addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mStopDrawable, null);
            }

            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        
        ((Xiaohuoband) getActivity().getApplication()).requestLocationUpdates(mSearchLocationObserver);
    }

    @Override
    public void onPause() {
    	super.onPause();
        ((Xiaohuoband) getActivity().getApplication()).removeLocationUpdates(mSearchLocationObserver);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mLoggedOutReceiver);
        getActivity().unregisterReceiver(mPasteSuggestionReceiver);
    }
    
	public void clearSearchBar() {
		getSearchField().setText("");
    	getSearchField().setCompoundDrawables(null, null, null, null);
	}
	
	public void setSearchBarText(String text) {
		getSearchField().setFocusable(false);
		getSearchField().setFocusableInTouchMode(false);
		getSearchField().setText(text);
		getSearchField().setFocusable(true);
		getSearchField().setFocusableInTouchMode(true);
	}
	
	public void reloadAction() {
		mCleanSearchBar = false;
        mStopDrawable.setBounds(0, 0, (int) Math.floor(mStopDrawable.getIntrinsicWidth() / 1.5), 
									  (int) Math.floor(mStopDrawable.getIntrinsicHeight() / 1.5));
		getSearchField().setCompoundDrawables(null, null, getSearchField().getText().toString().equals("") ? null : mStopDrawable, null);
	}
	
	private void stopAction() {
		mCleanSearchBar = true;
    	getSearchField().setText("");

    	// This makes a little (X) to clear the search bar.
    	getSearchField().setCompoundDrawables(null, null, null, null);
	}
	
	public TalkAutoCompleteTextView getSearchField() {
		return mSearchField;
	}
    
    private void tryToCheckAndPullNews()
    {
		if (NewsService.isNeedingToPullNews(getActivity(), MIN_GAP_TO_DISPLAY_NEWS, MAX_DISPLAY_NEWS_TIME) || DEBUG)
		{
			Log.d(TAG, "Need to pull news");
            startTask(TalkQueryType.PUBNEWS);
		}
    }
    
    private void startTask(TalkQueryType type) {
    	switch (type)
        {
            case PUBTEXT:
            	String query = mSearchField.getTrimmedText();
            	clearSearchBar();
            	mStateHolder.startTask(this, TalkQueryType.PUBLOCATION, query);
            	break;
            case PUBLOCATION:
            	mStateHolder.startTask(this, TalkQueryType.PUBLOCATION, "");
            	break;
            case PUBNEWS:
            	mStateHolder.startTask(this, TalkQueryType.PUBNEWS, "");
            	break;
            default:
            	break;
       }
    }
    
	private void listInit(View root)
	{
		mTalkView = (ListView) root.findViewById(R.id.list_first_tab);
		mList = new ArrayList<BaseListItemEntity>();
		mListItemAdapter = new ListItemAdapter(getActivity(), mList);		
		mTalkView.setDivider(null);
	}

	private OnClickListener mButtonAskListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mSearchField.hasText()) {
				startTask(TalkQueryType.PUBTEXT);
			}
		}
	};

	private void addRecvMsg(BaseListItemEntity listItemEntity){
		mList.add(listItemEntity);
		mTalkView.setAdapter(mListItemAdapter);
	}

    /** If location changes, auto-start a nearby venues search. */
    private class SearchLocationObserver implements Observer {
        private boolean mRequestedFirstSearch = false;

        @Override
        public void update(Observable observable, Object data) {
            Location location = (Location) data;
            // Fire a search if we haven't done so yet.
            if (!mRequestedFirstSearch
                    && ((BestLocationListener) observable).isAccurateEnough(location)) {
                mRequestedFirstSearch = true;
                
                boolean isNeed = LocationService.isGoingFarAway((Xiaohuoband) getActivity().getApplication(), 
                												 location, 
                												 MIN_DISTANCE_TO_HOME, 
                												 MIN_DISTANCE_TO_LAST_LOCATION); 
                if (isNeed || DEBUG) {
                	startTask(TalkQueryType.PUBLOCATION);
                }
            }
        }
    }
    
	private Handler updateHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof BaseListItemEntity)
			{
				BaseListItemEntity listItemEntity = (BaseListItemEntity)msg.obj;
				if (!mList.isEmpty()){
					BaseListItemEntity i= mList.get(mList.size() - 1);
					if (i instanceof ThinkingItemEntity)
					{
						mList.remove(mList.size() - 1);
					}
				}
				addRecvMsg(listItemEntity);
			}
			else if (msg.obj instanceof ButtonProxy) 
			{
				ButtonProxy bp = (ButtonProxy)msg.obj;
				mAskButton.setEnabled(bp.mIsEnable);
			}
			else if (msg.obj instanceof Exception)
			{
				IOException reason = (IOException)msg.obj;
				NotificationsUtil.ToastReasonForFailure(getActivity(), reason);
			}
		}
	};
	
	private static class ButtonProxy {
		public boolean mIsEnable;
		
		public ButtonProxy(boolean isEnable){
			mIsEnable = isEnable;
		}
	}
	
    private static class GetMsgTask implements Runnable {
        private static final String TAG = "GetMsgTask";
        private static final boolean DEBUG = XiaohuobandSettings.DEBUG;

        private TalkFragment mFragment;
        private Exception mReason;
        private TalkQueryType mQueryType;
        private String mQuery;

        public GetMsgTask(TalkFragment fragment, TalkQueryType type, String query) {
        	mFragment = fragment;
            mQueryType = type;
            mQuery = query;
        }
        
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()" + ", This guy is running: " + mQueryType.toString());
            
            Message msg;
            switch (mQueryType)
            {
            	case PUBTEXT:
            		msg = mFragment.updateHandler.obtainMessage(0, new QuestionItemEntity(mQuery));
            		mFragment.updateHandler.sendMessage(msg);
            		msg = mFragment.updateHandler.obtainMessage(0, new ThinkingItemEntity("让我想想..."));
            		mFragment.updateHandler.sendMessage(msg);
            		break;
            	case PUBLOCATION:
            		if (mQuery.isEmpty()){
            			msg = mFragment.updateHandler.obtainMessage(0, new QuestionItemEntity("正在获取周边信息..."));
            		}
            		else{
            			msg = mFragment.updateHandler.obtainMessage(0, new QuestionItemEntity(mQuery));
            		}
            		mFragment.updateHandler.sendMessage(msg);
            		msg = mFragment.updateHandler.obtainMessage(0, new ThinkingItemEntity("让我想想..."));
            		mFragment.updateHandler.sendMessage(msg);
            		break;
            	case PUBNEWS:
            		msg = mFragment.updateHandler.obtainMessage(0, new QuestionItemEntity("正在获取新闻..."));
            		mFragment.updateHandler.sendMessage(msg);
            		msg = mFragment.updateHandler.obtainMessage(0, new ThinkingItemEntity("让我想想..."));
            		mFragment.updateHandler.sendMessage(msg);
            		break;
            	default:
            		break;
            }
        }

        private LocationMsgSendEntity getLocationMsgInstance(String fromUserName, long now, String userId){
			// Get last known location.
            Location location = ((Xiaohuoband) mFragment.getActivity().getApplication()).getLastKnownLocation();
            if (location == null) {
            	mReason = new XiaohuobanException("No location!");
            	return null;
            }
            
            double locX = location.getLatitude();
            double locY = location.getLongitude();
            double scale = 0.0f;
            String label = "";
            return new LocationMsgSendEntity("server", fromUserName, String.valueOf(now), userId, 
            													mQuery, locX, locY, scale, label);
        }
        
        protected BaseListItemEntity doInBackground() {
            Log.d(TAG, "doInBackground()");
            
            Xiaohuoband xiaohuoband = (Xiaohuoband) mFragment.getActivity().getApplication();
			String fromUserName = xiaohuoband.getUserName();
			long now = new Date().getTime() / 1000;	//in seconds
			String userId = xiaohuoband.getUserId();    
			
			BaseSendEntity msg = null;
            switch (mQueryType)
            {
            	case PUBTEXT:
            		msg = new TextMsgSendEntity("server", fromUserName, String.valueOf(now), userId, 
							   mQuery, false);
            		break;
            	case PUBLOCATION:
            		msg = getLocationMsgInstance(fromUserName, now, userId);
            		break;
            	case PUBNEWS:
            		msg = new NewsMsgSendEntity("server", fromUserName, String.valueOf(now), userId);
            		break;
            	default:
            		break;
            }
            
            BaseListItemEntity entity = null;
            try {
            	entity = TalkProxy.sendMessage(msg, mFragment.getActivity());
            }catch (Exception e) {
                Log.d(TAG, "Caught Exception logging in.", e);
                mReason = e;
            }
            
            return entity;
        }

        protected void onPostExecute(BaseListItemEntity entity) {
            if (entity != null)
            {
                if (DEBUG) Log.d(TAG, "onPostExecute(): " + entity);
                mFragment.updateHandler.sendMessage(mFragment.updateHandler.obtainMessage(0, entity));
                if (mQueryType == TalkQueryType.PUBNEWS)
                {
                	if (!NewsService.updateLastDisplayTimeToPreferencesDB(mFragment.getActivity(), new Date().getTime()))
                	{
                		if (DEBUG) Log.e(TAG, "updateLastDisplayTimeToPreferencesDB failed!");
                	}
                	Log.d(TAG, "updateLastDisplayTimeToPreferencesDB success!");
                	
                	int displayedCountToday = NewsService.getTodayNewsDisplayTimesFromPreferencesDB(mFragment.getActivity());
                	if (!NewsService.updateTodayNewsDisplayTimesToPreferencesDB(mFragment.getActivity(), displayedCountToday+1))
                	{
                		Log.e(TAG, "updateTodayNewsDisplayTimesToPreferencesDB failed!");
                	}
                	Log.d(TAG, "updateTodayNewsDisplayTimesToPreferencesDB success!");
                }
                else if (mQueryType == TalkQueryType.PUBLOCATION)
                {
                	Location location = ((Xiaohuoband) mFragment.getActivity().getApplication()).getLastKnownLocation();
                    if (location == null) {
                    	mReason = new XiaohuobanException("No location!");
                    	return;
                    }
                    
                    if (!LocationService.updateLastValidLocationToPreferencesDB(mFragment.getActivity(), location))
                    {
                    	Log.e(TAG, "updateLastValidLocationToPreferencesDB failed!");
                    }
                    Log.d(TAG, "updateLastValidLocationToPreferencesDB success!");
                }
            }
            else
            {
            	Log.d(TAG, "Oops, listItemEntity is null");
            	Message msg = mFragment.updateHandler.obtainMessage(0, new SimpleAnswerItemEntity("目测网络有问题啊..."));
            	mFragment.updateHandler.sendMessage(msg);
            	msg = mFragment.updateHandler.obtainMessage(0, mReason);
            	mFragment.updateHandler.sendMessage(msg);
            }
        }

		@Override
		public void run() {
          	Log.d(TAG, "This guy is running: " + mQueryType.toString());
          	mFragment.updateHandler.sendMessage(mFragment.updateHandler.obtainMessage(0, new ButtonProxy(false)));
          	
			try
			{
				onPreExecute();
				BaseListItemEntity entity = doInBackground();
				onPostExecute(entity);
			}finally
			{
				mFragment.updateHandler.sendMessage(mFragment.updateHandler.obtainMessage(0, new ButtonProxy(true)));
				Log.d(TAG, "This guy ends: " + mQueryType.toString());
			}
		}
    }

    private static class StateHolder {
        private GetMsgTask mGetMsgTask;
        
        private ExecutorService mExecutorService;
      
        public StateHolder() {
        	mGetMsgTask = null;
        	mExecutorService = Executors.newSingleThreadExecutor();
        }
      
        public void startTask(TalkFragment fragment, TalkQueryType type, String query) {
        	mGetMsgTask = new GetMsgTask(fragment, type, query);
        	mExecutorService.execute(mGetMsgTask);
        }
    }    
}
