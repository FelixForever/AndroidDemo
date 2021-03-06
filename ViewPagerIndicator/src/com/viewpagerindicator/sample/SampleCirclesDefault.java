package com.viewpagerindicator.sample;

import com.viewpagerindicator.CirclePageIndicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class SampleCirclesDefault extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_circles);
		
		mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
	}
}