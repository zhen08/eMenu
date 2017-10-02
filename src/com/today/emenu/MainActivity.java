package com.today.emenu;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.today.emenu.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private SideButtonBar sideButtonBar;
	private OrderPage orderPage;
	private PopupWindow orderPopup;
	private ImageButton myMenuButton;
	private ImageButton imageLeftButton;
	private ImageButton imageRightButton;
	private ImageView imageBattery;
	private ImageView imageCover;
	private ImageView imageFront;
	private ImageView imageBack;
	private int currentPageId;
	private int selectedPageId;
	private CatalogEntry currentCatalog;
	private ViewPager viewPager;
	private MyPagerAdapter myAdapter;
	private List<View> mListViews;
	private MenuData menuData;
	private BadgeView badge;
	private EMenuApplication application;
	private int batLevel;
	private int batScale;
	private int batStatus;
	private final Handler badgeHandler = new Handler(); 
	private int numberOfCovers;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Debug.Log("onCreate Begin");
		application = (EMenuApplication)(getApplicationContext());
		menuData =  application.menuData;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		RelativeLayout layout = new RelativeLayout(this);
		setContentView(layout);

		numberOfCovers = 0;

		imageCover = new ImageView(this);
		try {
			if (menuData.coverImage.isEmpty()){
				imageCover.setImageResource(R.drawable.cover);
			} else {
				FileInputStream fis = openFileInput(menuData.coverImage);
				imageCover.setImageBitmap(BitmapFactory.decodeStream(fis));
			}
			imageCover.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					viewPager.setCurrentItem(1);
				}
			});
			numberOfCovers ++;
		} catch (Exception e) {
			imageCover = null;
			e.printStackTrace();
		}


//		imageFront = new ImageView(this);
//		try {
//			FileInputStream fis = new FileInputStream(Constants.DATAFOLDER.concat("front.png"));
//			imageFront.setImageBitmap(BitmapFactory.decodeStream(fis));
//			imageFront.setOnClickListener(new View.OnClickListener() {
//				public void onClick(View v) {
//					viewPager.setCurrentItem(2);
//				}
//			});
//			numberOfCovers ++;
//		} catch (Exception e) {
//			imageFront = null;
//			e.printStackTrace();
//		}

		mListViews = new ArrayList<View>();

		if (imageCover != null)
		{
			mListViews.add(imageCover);
		}
		if (imageFront != null)
		{
			mListViews.add(imageFront);
		}
		
		final int nPages = menuData.numberOfPages;
		int hPages = nPages;
//		int hPages = nPages / 2;
//		if (hPages > 20){
//			hPages = 20;
//		}
		for (int i =0;i<nPages;i++){
			if (i<hPages){
				mListViews.add(newItem(i));
			}else{
				mListViews.add(null);
			}
		}
		imageBack = new ImageView(this);
		mListViews.add(imageBack);
		new LoadItemsTask().execute(hPages);

		selectedPageId = -1;
		currentPageId = -1;

		myAdapter = new MyPagerAdapter(this,mListViews);
		viewPager = new ViewPager(this);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(myAdapter);

		RelativeLayout.LayoutParams lpVFPage = new RelativeLayout.LayoutParams(768,1024);
		lpVFPage.leftMargin = 0;
		lpVFPage.topMargin = 0;
		lpVFPage.alignWithParent = true;
		layout.addView(viewPager,lpVFPage);

		viewPager.setOnPageChangeListener(new OnPageChangeListener(){

			public void onPageScrollStateChanged(int arg0) {
				//Debug.Log("onPageScrollStateChanged - " + arg0);
				switch (arg0){
				case 0:
					currentPageId = selectedPageId;
					if (currentPageId >= 0){
						sideButtonBar.selectCatalog(menuData.getPageById(currentPageId).catalogEntry);
					}
					break;
				case 1:
					if (!menuData.allowDirectLoad){
						if (Constants.BGLATELOAD){
							((ItemPage)mListViews.get(selectedPageId+numberOfCovers)).clearHighResBackground();
						}
					}
					break;
				case 2: 
					break;
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//Log.d("k", "onPageScrolled - " + arg0);
			}

			public void onPageSelected(int arg0) {
				//Debug.Log("onPageSelected - " + arg0);
				if (arg0 >= mListViews.size() - 1) {
					viewPager.setCurrentItem(0);
					return;
				}

				selectedPageId = arg0-numberOfCovers;
				if (selectedPageId >= 0){
					final ItemPage itemPage = ((ItemPage)mListViews.get(selectedPageId+numberOfCovers)); 
					itemPage.refreshButtons();
					if (!menuData.allowDirectLoad){
						if (Constants.BGLATELOAD){
							itemPage.startHighResBgTask();
						}
					}
				}
				checkCover();
			}

		});

		sideButtonBar = new SideButtonBar(this);
		RelativeLayout.LayoutParams lpSideButtonBar = new RelativeLayout.LayoutParams(160,1024);
		lpSideButtonBar.leftMargin = 608;
		lpSideButtonBar.topMargin = 0;
		lpSideButtonBar.alignWithParent = true;
		layout.addView(sideButtonBar,lpSideButtonBar);

		sideButtonBar.setOnButtonChangeListener(new OnChangeListener(){

			public void onChange(View v,int Param) {
				doCatalogChange();
			}
		});

		imageBattery = new ImageView(this);
		imageBattery.setImageResource(R.drawable.battery100);
		RelativeLayout.LayoutParams lpImageBattery = new RelativeLayout.LayoutParams(40,40);
		lpImageBattery.leftMargin = 670;
		lpImageBattery.topMargin = 10;
		lpImageBattery.alignWithParent = true;
		layout.addView(imageBattery,lpImageBattery);

		imageLeftButton = new ImageButton(this);
		imageLeftButton.setBackgroundColor(0x00000000);
		imageLeftButton.setScaleType(ImageView.ScaleType.FIT_XY);
		imageLeftButton.setImageResource(R.drawable.pgup);
		imageLeftButton.setAlpha(0x80);
		RelativeLayout.LayoutParams lpLeftButton = new RelativeLayout.LayoutParams(80,80);
		lpLeftButton.leftMargin = 32;
		lpLeftButton.topMargin = 944;
		lpLeftButton.alignWithParent = true;
		layout.addView(imageLeftButton,lpLeftButton);
		imageLeftButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				viewPager.setCurrentItem(currentPageId+numberOfCovers-1);
			}
		});

		imageRightButton = new ImageButton(this);
		imageRightButton.setBackgroundColor(0x00000000);
		imageRightButton.setScaleType(ImageView.ScaleType.FIT_XY);
		imageRightButton.setImageResource(R.drawable.pgdn);
		imageRightButton.setAlpha(0x80);
		RelativeLayout.LayoutParams lpRightButton = new RelativeLayout.LayoutParams(80,80);
		lpRightButton.leftMargin = 512;
		lpRightButton.topMargin = 944;
		lpRightButton.alignWithParent = true;
		layout.addView(imageRightButton,lpRightButton);
		imageRightButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				viewPager.setCurrentItem(currentPageId+numberOfCovers+1);
			}
		});

		myMenuButton = new ImageButton(this);
		myMenuButton.setBackgroundColor(0x00000000);
		myMenuButton.setScaleType(ImageView.ScaleType.FIT_XY);
		myMenuButton.setImageResource(R.drawable.mymenu);
		myMenuButton.setAlpha(0x80);
		RelativeLayout.LayoutParams lpMyMenuButton = new RelativeLayout.LayoutParams(80,80);
		lpMyMenuButton.leftMargin = 272;
		lpMyMenuButton.topMargin = 944;
		lpMyMenuButton.alignWithParent = true;
		layout.addView(myMenuButton,lpMyMenuButton);
		myMenuButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				orderPage.setVisibility(android.view.View.VISIBLE);
				orderPage.loadOrder();
				if (orderPopup == null)
				{
					orderPopup = new PopupWindow(orderPage,608,1024);
					orderPopup.setFocusable(true);
					orderPopup.setOutsideTouchable(false);
				}
				orderPopup.showAtLocation(myMenuButton, Gravity.TOP | Gravity.LEFT, 0, 170);
			}
		});
		badge = new BadgeView(this, myMenuButton);


		sideButtonBar.loadCatalog(menuData.catalogEntryList);

		orderPage = new OrderPage(this);
		orderPage.setOnOrderPageCloseListener(new OnChangeListener(){

			public void onChange(View v,int Param) {
				if (orderPopup.isShowing()){
					orderPopup.dismiss();
					orderPopup = null;
					System.gc();
				}
				if (Param==1){
					viewPager.setCurrentItem(0);
				}					
				((ItemPage)mListViews.get(currentPageId+numberOfCovers)).refreshButtons();
			}
		});

		sideButtonBar.startScroll();
		badgeHandler.postDelayed(badgeRunnable,4000);
		checkCover();
		this.registerReceiver(mBatteryInfoReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		viewPager.setCurrentItem(0);
		Debug.Log("onCreate End");
	}

//	@Override
//	public void onBackPressed() {
//	}
//
	private BroadcastReceiver mBatteryInfoReceiver=new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(Intent.ACTION_BATTERY_CHANGED.equals(action))
			{
				batLevel=intent.getIntExtra("level",0);
				batScale=intent.getIntExtra("scale", 100);
				batStatus = intent.getIntExtra("status", -1);
				onBatteryInfoReceiver();
			}
		}
	};

	private void checkCover(){
		if (selectedPageId < 0){
			sideButtonBar.setVisibility(android.view.View.INVISIBLE);
			myMenuButton.setVisibility(android.view.View.INVISIBLE);
			imageLeftButton.setVisibility(android.view.View.INVISIBLE);
			//imageRightButton.setVisibility(android.view.View.INVISIBLE);
		}else{
			sideButtonBar.setVisibility(android.view.View.VISIBLE);
			myMenuButton.setVisibility(android.view.View.VISIBLE);
			imageLeftButton.setVisibility(android.view.View.VISIBLE);
			//imageRightButton.setVisibility(android.view.View.VISIBLE);
		}

	}

	private void onBatteryInfoReceiver()
	{
		final float battery = (float)batLevel/(float)batScale;
		if (batStatus == BatteryManager.BATTERY_STATUS_CHARGING){
			if (battery > 0.98){
				imageBattery.setImageResource(R.drawable.battery100);
			} else {
				imageBattery.setImageResource(R.drawable.batterychg);
			}
		} else {
			if (battery > 0.70){
				imageBattery.setImageResource(R.drawable.battery100);
			} else if (battery > 0.40){
				imageBattery.setImageResource(R.drawable.battery75);
			} else if (battery > 0.10){
				imageBattery.setImageResource(R.drawable.battery25);
			} else {
				imageBattery.setImageResource(R.drawable.battery0);
			}
		}
	}

	private void updateBadge(){
		int count = application.getTrxItemCount();
		badge.setText(String.valueOf(count));
		if (count > 0){
			badge.show();
		}else{
			badge.hide();
		}
	}

	private void doCatalogChange(){
		currentCatalog = sideButtonBar.selectedCatalog;
		final PageEntry currPage = currentCatalog.pageList.get(0);
		Debug.Log("Switch to Page "+currPage.id);
		viewPager.setCurrentItem(currPage.id+numberOfCovers);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBatteryInfoReceiver);
		super.onDestroy();
	}

	private ItemPage newItem(int pageId){
		final ItemPage itemPage = new ItemPage(this);
		itemPage.loadPage(menuData.getPageById(pageId));
		return itemPage;
	}

	private class LoadItemsTask extends AsyncTask<Integer, Object, Object> {
		@Override
		protected Object doInBackground(Integer... arg) {
			try {
				int startPage = arg[0].intValue();
				int menuPages = menuData.numberOfPages;
				for (int i =startPage;i<menuPages;i++){
					mListViews.set(i+numberOfCovers, newItem(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Object result) {
		}

	}	


	private Runnable badgeRunnable = new Runnable() {
		public void run() {
			updateBadge();
			badgeHandler.postDelayed(this, 1000);
		}
	};

	private class MyPagerAdapter extends PagerAdapter{

		public List<View> views;
		int mCount;

		public MyPagerAdapter(Context context,List<View> views) {
			this.views = views;
			mCount = views.size();
		}		

		@Override
		public void destroyItem(View viewPager, int position, Object arg2) {
			final ItemPage itemPage;
			//Debug.Log("destroyItem" + position);
			if ((position >= numberOfCovers)&&(position < mCount - 1)){
				itemPage = ((ItemPage)views.get(position));
				((ViewPager) viewPager).removeView(itemPage);
				if (!menuData.allowDirectLoad){
					if (itemPage != null){
						itemPage.clearHighResBackground();
					}
				}
			} else {
				((ViewPager) viewPager).removeView(views.get(position));
			}
		}

		@Override
		public Object instantiateItem(View viewPager, int position) {
			//Debug.Log("instantiateItem" + position);
			if ((position >= numberOfCovers)&&(position < mCount - 1)){
				ItemPage itemPage = (ItemPage)(views.get(position));
				while (itemPage == null){
					Debug.Log("Error Getting Item Page!!!"+position);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
					itemPage = (ItemPage)(views.get(position));
				}
				if ( itemPage != null){
					((ViewPager) viewPager).addView(itemPage,0);
					if (!menuData.allowDirectLoad){
						if (!Constants.BGLATELOAD){
							itemPage.startHighResBgTask();
						}
					}
				}
				return itemPage;
			} else {
				((ViewPager) viewPager).addView(views.get(position),0);
				return views.get(position);
			}
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==(arg1);

		}
	}
}
