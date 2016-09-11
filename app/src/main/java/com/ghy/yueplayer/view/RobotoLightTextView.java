package com.ghy.yueplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoLightTextView extends TextView {

	public RobotoLightTextView(Context context) {
		super(context);
		setUseRoboto();
	}

	public RobotoLightTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setUseRoboto();
	}

	public RobotoLightTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUseRoboto();
	}

	private void setUseRoboto() {
		setTypeface(FontsFactory.getRoboto(getContext()));
	}
}
