package com.linsen.h5.fragment;

import github.chenupt.dragtoplayout.DragTopLayout;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.linsen.h5.BaseFragment;
import com.linsen.h5.R;
import com.linsen.h5.fragment.style.BaseModelFragment;
import com.linsen.h5.view.tabstrip.FragmentViewPagerAdapter;
import com.ypy.eventbus.EventBus;

@SuppressLint("ValidFragment")
public class PersonalFragment extends BaseFragment {
	private PagerSlidingTabStrip tabs;
	private FragmentViewPagerAdapter adapter;
	private ViewPager pager;
	private DragTopLayout dragLayout;

	public PersonalFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_personal, container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void initViews() {
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new FragmentViewPagerAdapter(
				this.getActivity().getSupportFragmentManager(), pager, getFragments(), getTitles());
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		dragLayout = (DragTopLayout) findViewById(R.id.drag_layout);
	}

	@Override
	protected void initEvents() {

	}

	@Override
	protected void initDatas() {

	}

	private String[] getTitles() {
		return new String[] { "作品", "草稿", "收藏" };
	}

	private List<BaseModelFragment> getFragments() {
		List<BaseModelFragment> list = new ArrayList<BaseModelFragment>();
		BaseModelFragment webViewFragment0 = new WebViewFragment();
		BaseModelFragment webViewFragment1 = new WebViewFragment();
		BaseModelFragment webViewFragment2 = new WebViewFragment();
		list.add(webViewFragment0);
		list.add(webViewFragment1);
		list.add(webViewFragment2);
		return list;
	}

	// Handle scroll event from fragments
    public void onEvent(Boolean b){
        dragLayout.setTouchMode(b);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
