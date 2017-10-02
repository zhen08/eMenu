package com.today.emenu;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.LinearLayout;

public class ItemPage extends LinearLayout {

	private Context context;
	private String backgroundImg;
	private Bitmap lowResBmp;
	private Bitmap highResBmp;
	private BitmapDrawable lowResDrawable;
	private BitmapDrawable highResDrawable;
	private List<EmenuWidget> itemWidgets;
	private Boolean isWholePage;
	private BitmapFactory.Options highResOptions;
	private BitmapFactory.Options lowResOptions;
	private LoadHighResBgTask loadHighResBgTask;
	private EMenuApplication application;
	private boolean isCatalogCover;
	public int pageId;

	public ItemPage(Context context) {
		super(context);
		this.context = context;
		application = (EMenuApplication)(context.getApplicationContext());
		backgroundImg = "";
		isWholePage = false;
		isCatalogCover = false;
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(0x00000000);
		itemWidgets = new ArrayList<EmenuWidget>();

		lowResOptions = new BitmapFactory.Options();
		lowResOptions.inJustDecodeBounds = false;
		lowResOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		lowResOptions.inPurgeable = true;
		lowResOptions.inInputShareable = true;
		if (application.menuData.numberOfPages < 40){
			lowResOptions.inSampleSize = 2; 
		} else {
			lowResOptions.inSampleSize = 4; 
		}

		highResOptions = new BitmapFactory.Options();
		highResOptions.inJustDecodeBounds = false;
		highResOptions.inPreferredConfig = Bitmap.Config.RGB_565;
		highResOptions.inPurgeable = true;
		highResOptions.inInputShareable = true;
		highResOptions.inSampleSize = 1;
	}

	public void loadPage(PageEntry pageEntry){
		EmenuWidget widget;
		pageId = pageEntry.id;
		isCatalogCover = (pageEntry.catalogEntry.pageList.get(0).id == pageId);
		for (ItemEntry item : pageEntry.itemList){
			if (item.itemType.equals("wholepage")){
				widget = new WholePageWidget(context);
				backgroundImg = item.img;
				isWholePage = true;
			} else if (item.itemType.equals("quarterpage")){
				widget = new QuarterPageWidget(context);
			} else if (item.itemType.equals("title")){
				widget = new TitleWidget(context);
			} else if (item.itemType.equals("textonly")){
				widget = new TextOnlyWidget(context);
			} else {
				widget = new EmenuWidget(context);
			}
			widget.loadItem(item);
			itemWidgets.add(widget);
		}

		for (EmenuWidget view:itemWidgets){
			addView(view);
		}

		if (backgroundImg.isEmpty()){
			backgroundImg = pageEntry.background;
		}

		if (!backgroundImg.isEmpty()){
			if (isWholePage && (application.menuData.allowDirectLoad||(isCatalogCover&&Constants.CACHECATALOGCOVER))){
				loadHighResBackground();
				updateUI(true);
			} else {
				loadLowResBackground();
				updateUI(false);
			}
		}

		//Debug.Log("loadPage Basic Contents End");
	}

	private void loadLowResBackground(){
		//Debug.Log("loadLowResBackground Begin "+pageId);
		if (backgroundImg.isEmpty()){
			return;
		}
		try {
			final FileInputStream fis = context.openFileInput(backgroundImg);
			lowResBmp = BitmapFactory.decodeStream(fis,null,lowResOptions);
			lowResDrawable = new BitmapDrawable(lowResBmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Debug.Log("loadLowResBackground End "+pageId);
	}

	public void clearHighResBackground(){
		//Debug.Log("clearHighResBackground "+pageId);
		if (isCatalogCover&&Constants.CACHECATALOGCOVER){
			return;
		}
		if (loadHighResBgTask != null){
			cancelHighResBgTask();
		} else if (highResBmp != null) {
			updateUI(false);
			highResDrawable = null;
			highResBmp = null;
		}
	}

	private void loadHighResBackground(){
		//Debug.Log("loadHighResBackground Begin "+pageId);
		if ((backgroundImg.isEmpty())||(highResDrawable != null)||(!isWholePage)){
			return;
		}
		try {
			final FileInputStream fis = context.openFileInput(backgroundImg);
			highResBmp = BitmapFactory.decodeStream(fis,null,highResOptions);
			highResDrawable = new BitmapDrawable(highResBmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Debug.Log("loadHighResBackground End "+pageId);
	}

	public void updateUI(boolean highRes){
		if ((highRes)&&(highResDrawable != null)){
			//Debug.Log("updateUI HighRes "+pageId);
			setBackgroundDrawable(highResDrawable);
		}else{
			//Debug.Log("updateUI LowRes "+pageId);
			setBackgroundDrawable(lowResDrawable);
		}
		refreshButtons();
	}

	public void refreshButtons(){
		for (EmenuWidget widget:itemWidgets){
			widget.refreshButtons();
		}
	}

	public void startHighResBgTask(){
		//Debug.Log("startHighResBgTask " + pageId);
		if (loadHighResBgTask == null){
			loadHighResBgTask = new LoadHighResBgTask();
			loadHighResBgTask.execute(0);
		}
	}

	public void cancelHighResBgTask(){
		//Debug.Log("cancelHighResBgTask " + pageId);
		if (loadHighResBgTask != null){
			loadHighResBgTask.cancel(true);
		}
		loadHighResBgTask = null;
	}

	private class LoadHighResBgTask extends AsyncTask<Integer, Object, Object> {
		@Override
		protected Object doInBackground(Integer... arg) {
			//Debug.Log("LoadHighResBgTask Begin "+pageId);
			while (application.mutex == true){
				try {
					//Debug.Log("Waiting... "+pageId);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
				if (this.isCancelled()){
					return null;
				}
			}
			application.mutex = true;
			if (this.isCancelled()){
				return null;
			}
			try {
				loadHighResBackground();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			System.gc();
			application.mutex = false;
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			//Debug.Log("LoadHighResBgTask Cancelled "+pageId);
			if (highResBmp != null) {
				highResDrawable = null;
				//highResBmp.recycle();
				highResBmp = null;
			}
			updateUI(false);
			loadHighResBgTask = null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			updateUI(true);
			loadHighResBgTask = null;
			//Debug.Log("LoadHighResBgTask End");
		}

	}	
}

