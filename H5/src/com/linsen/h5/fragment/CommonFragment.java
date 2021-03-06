package com.linsen.h5.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linsen.h5.BaseFragment;
import com.linsen.h5.R;

@SuppressLint("ValidFragment")
public class CommonFragment extends BaseFragment {
	private TextView headerTitleTv;

	public CommonFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_common, container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void initViews() {
		headerTitleTv = (TextView) findViewById(R.id.header_title_tv);
		headerTitleTv.setText("首页");
	}

	@Override
	protected void initEvents() {

	}

	@Override
	protected void initDatas() {
		
	}

}
