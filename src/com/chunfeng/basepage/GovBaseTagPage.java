/**
 * 
 */
package com.chunfeng.basepage;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.chunfeng.zhjz.activity.MainActivity;
import com.example.test.R;

/**
 * @author Cfrjkj
 * @date 2016-4-29
 * @time 上午10:53:03
 * @todo TODO
 */
public class GovBaseTagPage extends BaseTagPage{

	/**
	 * @param context
	 */
	public GovBaseTagPage(MainActivity context) {
		super(context);
	}

	public void initData() {
		//设置标题
		textTitle.setText(activity.getString(R.string.mainBtn4));
		
		TextView tv = new TextView(activity);
		tv.setText(R.string.mainBtn4);
		tv.setTextColor(Color.RED);
		tv.setTextSize(50);
		tv.setGravity(Gravity.CENTER);
		flLayout.addView(tv);
		
		btnMenuButton.setVisibility(View.VISIBLE);
	}
}
