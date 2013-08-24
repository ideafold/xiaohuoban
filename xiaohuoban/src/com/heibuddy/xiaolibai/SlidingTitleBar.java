package com.heibuddy.xiaolibai;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.heibuddy.R;

public class SlidingTitleBar extends BaseActivity {

	public SlidingTitleBar() {
		super(R.string.title_bar_slide);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Above View
		setContentView(R.layout.test_content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.test_content_frame, new SampleListFragment())
		.commit();
		
		setSlidingActionBarEnabled(true);
	}
	
}
