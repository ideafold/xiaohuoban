package com.heibuddy.xiaohuoband;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.heibuddy.R;
import com.heibuddy.xiaohuoband.slidingmenuimp.SlidingMenuFragment;

public class TalkActivity extends SlidingFragmentActivity {
    public static final String TAG = "TalkActivity";
    public static final boolean DEBUG = XiaohuobandSettings.DEBUG;
    
	static public Fragment mContent = new TalkFragment();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the Above View
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new TalkFragment();	
		
		// set the Above View
		setContentView(R.layout.test_content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.test_content_frame, mContent)
		.commit();
		
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
		
		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setSlidingActionBarEnabled(true);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        this.show();
    }
    
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.test_content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}

	public void show()
	{
		if (mContent == null)
		{
			mContent = new TalkFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.test_content_frame, mContent)
			.commit();
		}
		getSlidingMenu().showContent();
	}
}
