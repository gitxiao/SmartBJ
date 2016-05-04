/**
 * 
 */
package com.chunfeng.newsChildPage;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap.Config;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chunfeng.dataLogic.NewsBasicData;
import com.chunfeng.dataLogic.NewsDetailData;
import com.chunfeng.utils.DensityUtil;
import com.chunfeng.utils.MyConstants;
import com.chunfeng.utils.SPTools;
import com.chunfeng.utils.ViewPagerSuperNotIntereptEvent;
import com.chunfeng.zhjz.activity.MainActivity;
import com.example.test.R;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @author Cfrjkj
 * @date 2016-5-3
 * @time 上午11:34:30
 * @todo TODO
 */
public class VPINewsChildPage {

	private View layoutNewsContentMain;
	private MainActivity mainActivity;
	private NewsBasicData.NewsType.NewsTag newsTag;
	
	private Gson gson;
//	private NewsDetailData detailData;
	
	@ViewInject(R.id.tv_news_content_picinfo)
	private TextView tvDesc;
	
	@ViewInject(R.id.vp_news_content_lunbo)
	private ViewPagerSuperNotIntereptEvent vp_lunbo;
	
	@ViewInject(R.id.ll_news_content_points)
	private LinearLayout ll_points;					//轮播图顺序点
	
	@ViewInject(R.id.lv_news_content_detail)
	private ListView listViewNews;					//显示新闻的listView
	
	private List<NewsDetailData.Detail.Topnews> lunboDataList = new ArrayList<NewsDetailData.Detail.Topnews>();
	private LunBoAdapter lunBoAdapter;
	private BitmapUtils bitmapUtils;
	private int picSelectIndex;
	private NewsDetailData detailData;
	
	
	public VPINewsChildPage(MainActivity mainActivity,NewsBasicData.NewsType.NewsTag newsTag){
		this.mainActivity = mainActivity;
		layoutNewsContentMain = initView();
		this.newsTag = newsTag;
		
		bitmapUtils = new BitmapUtils(mainActivity);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
		
		initEvent();
		initData();
	}
	
	
	
	public View getRootView(){
		return layoutNewsContentMain;
	}
	
	/**
	 * 
	 */
	public View initView() {
		View view = View.inflate(mainActivity, R.layout.news_pager_content, null);
		ViewUtils.inject(this, view);
		return view;
	}

	/**
	 * 
	 */
	public void initData(){
		//这些数据只需要初始化一次
		gson = new Gson();
		lunBoAdapter = new LunBoAdapter();
		vp_lunbo.setAdapter(lunBoAdapter);
		vp_lunbo.setOnPageChangeListener(new LunBoListener());
//		vp_lunbo.requestParentDisallowInterceptTouchEvent(true);
		
		//获取网络数据之前先取出本地缓存的数据
		String cacheString = SPTools.getString(mainActivity, newsTag.url, null);		//因为数据比较多, 所以用url作为数据存储的key值
		if(cacheString != null) {
			parseJsonData(cacheString);
		}else {
			//可以在这里添加一个loading提示
			System.out.println("还没有缓存数据" + MyConstants.NEWS_DETAIL_DATA);
		}
		initHttpData(newsTag.url);
	}
	
	/**
	 * 
	 */
	public void initEvent() {
		
	}
	
	/**
	 * 获取网络数据
	 * @param <T>
	 */
	private <T> void initHttpData(final String url) {
		
		HttpUtils httpUtils = new HttpUtils();
		
		try {
			httpUtils.send(HttpMethod.GET, MyConstants.URL_SERVER + url, new RequestCallBack<T>(){
				
				@Override
				public void onSuccess(ResponseInfo<T> responseInfo) {
					System.out.println("网络访问成功");				
					NewsDetailData detailData = parseJsonData((String)(responseInfo.result));
					SPTools.setString(mainActivity, url, (String)(responseInfo.result));
					operateData(detailData);
				}
				
				@Override
				public void onFailure(HttpException error, String msg) {
					System.out.println("网络访问失败 msg = " + msg);				
					operateData(null); 		//网络访问失败也要进行对应的操作
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析json数据,
	 * @param jsonData
	 */
	private NewsDetailData parseJsonData(String jsonData){
		detailData = gson.fromJson(jsonData, NewsDetailData.class);
		
		return detailData;
	}
	
	/**
	 * 处理数据并显示
	 * @param detailData
	 */
	private void operateData(NewsDetailData detailData){
		//1.设置轮播图的数据
		setLunboData(detailData);
		
		//2.轮播图对应点的处理
		initPoints(detailData);
		
		//3.设置图片描述文字和点的选中效果,这个函数应该在页面切换事件中调用
		setPicDescAndPointSelect(detailData,picSelectIndex);
	}


	/**
	 * 设置轮播图的描述信息和点选择状态
	 */
	private void setPicDescAndPointSelect(NewsDetailData detailData,int picSelectIndex) {
		tvDesc.setText(detailData.data.topnews.get(picSelectIndex).title);
		for (int i = 0; i < detailData.data.topnews.size(); i++) {
			ll_points.getChildAt(i).setEnabled(i == picSelectIndex);
		}
	}


	/**
	 * 几个轮播图的点
	 */
	private void initPoints(NewsDetailData detailData) {
		this.ll_points.removeAllViews();		//切换页面后需要先清空以前的点, 再生成新的点
		for (int i = 0; i < detailData.data.topnews.size(); i++) {
			View v_pointView = new View(mainActivity);
			//设置点的背景选择器
			v_pointView.setBackgroundResource(R.drawable.point_selector);
			v_pointView.setEnabled(false); //默认都是灰色点

			//设置点的大小
			android.widget.LinearLayout.LayoutParams params = new  android.widget.LinearLayout.LayoutParams(DensityUtil.dip2px(mainActivity, 5),DensityUtil.dip2px(mainActivity, 5));
			//设置点与点之间的距离
			params.rightMargin = DensityUtil.dip2px(mainActivity, 10);
			params.leftMargin = DensityUtil.dip2px(mainActivity, 10);
			v_pointView.setLayoutParams(params);
			this.ll_points.addView(v_pointView);
		}
		ll_points.getChildAt(0).setEnabled(true);	//默认显示第一张图片,第一个点为红色
	}



	/**
	 * @param detailData2
	 */
	private void setLunboData(NewsDetailData detailData) {
		if(detailData != null){
			lunboDataList = detailData.data.topnews;
		}
		lunBoAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 轮播图的适配器
	 */
	private class LunBoAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return lunboDataList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
//			return super.instantiateItem(container, position);
			
//			System.out.println("加载本地图片 position = " + position + ", " + System.currentTimeMillis());
			ImageView imageViewTemp = new ImageView(mainActivity);
			//设置默认图片,防止网速较慢. 好像不起作用???
			imageViewTemp.setImageResource(R.drawable.home_scroll_default);
			imageViewTemp.setScaleType(ScaleType.FIT_XY);  		//设置图片显示适配坐标
			
//			ImageView httpImageView = null;
			String urlString = lunboDataList.get(position).topimage;
			String newUrlString = urlString.replaceAll(MyConstants.OLD_IP, MyConstants.NEW_IP);
			//异步加载网络图片并显示
//			System.out.println("加载网络图片 position = " + position + ", "  + System.currentTimeMillis());
			bitmapUtils.display(imageViewTemp, newUrlString);
			container.addView(imageViewTemp);
			
			
//			System.out.println("返回图片 position = " + position + ", "  + System.currentTimeMillis());
			return imageViewTemp;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			super.destroyItem(container, position, object);
			container.removeView((View)object);
		}
	}
	
	/**
	 * 监听轮播图的翻页状态
	 * @author Cfrjkj
	 * @date 2016-5-3
	 * @time 下午9:38:49
	 * @todo TODO
	 */
	private class LunBoListener implements OnPageChangeListener{

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			
		}

		@Override
		public void onPageSelected(int position) {
//			if(position == 0) {
//				mainActivity.getSlidingMenu().setSlidingEnabled(true);
//			}else {
//				mainActivity.getSlidingMenu().setSlidingEnabled(false);
//			}
			picSelectIndex = position;		//点的选中状态
			setPicDescAndPointSelect(detailData,position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
		
	}
}