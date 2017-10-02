package com.today.emenu;

import java.util.ArrayList;

import com.today.emenu.R;

import android.content.*;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class SideButtonBar extends RelativeLayout {

	private Context context;
	private ScrollView scrollButtonBar;
	private LinearLayout buttonsLayout;
	private ArrayList<ImageButton> buttonList;
	private ArrayList<TextView> textList;
	private OnChangeListener onCatalogChangeListener;
	public ArrayList<CatalogEntry> catalogList;
	public CatalogEntry selectedCatalog;

	private final Handler mHandler = new Handler(); 
	
	public SideButtonBar(Context context) {
		super(context);
		this.context = context;
		setBackgroundColor(0xa0000000);
		buttonList = new ArrayList<ImageButton>();
		textList = new ArrayList<TextView>();
	}
	
	public void loadCatalog(ArrayList<CatalogEntry> catalogList){
		ImageButton imgButton;
		TextView catalogText;
		CatalogEntry currentCatalog;
		// Lay them out in the compound control.
        scrollButtonBar = new ScrollView(context);
        buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.VERTICAL);
        buttonsLayout.setGravity(Gravity.CENTER);
        scrollButtonBar.addView(buttonsLayout);
        this.catalogList = catalogList;
		if (catalogList != null) {
		for (int i=0;i<catalogList.size();i++)
		{
			currentCatalog = catalogList.get(i);
			imgButton = new ImageButton(context);
			imgButton.setImageBitmap(currentCatalog.icon);
			imgButton.setBackgroundColor(0x00000000);
			imgButton.setOnClickListener(new View.OnClickListener() {
					
				public void onClick(View v) {
					selectButton((ImageButton)v,true);
				}
			});
			LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, currentCatalog.iconHeight); 
	        buttonsLayout.addView(imgButton,lpButton);
			buttonList.add(imgButton);
			
			if (currentCatalog.showName){
				catalogText = new TextView(context);
				catalogText.setBackgroundColor(0x00000000);
				catalogText.setText(currentCatalog.name);
				catalogText.setGravity(Gravity.CENTER);
				catalogText.setTextSize(18);
				buttonsLayout.addView(catalogText);
				textList.add(catalogText);
			}
			
		}
        RelativeLayout.LayoutParams lpButton = new RelativeLayout.LayoutParams(160,896);
        lpButton.leftMargin = 0;
        lpButton.topMargin = 64;
        lpButton.alignWithParent = true;
        addView(scrollButtonBar,lpButton);

        selectButton(buttonList.get(0),true);
		}
	}
	
	public void startScroll(){
	    mHandler.post(scrollRunnable); 
	}
	
	public void selectCatalog(CatalogEntry catalog){
		selectButton(buttonList.get(catalogList.indexOf(catalog)),false);			
	}
	
	public void selectCatalog(int catalog){
		selectButton(buttonList.get(catalog),false);			
	}
	
	public CatalogEntry selectNextCatalog(){
		int idx;
		idx = catalogList.indexOf(selectedCatalog) + 1;
		if (idx>=catalogList.size())
		{
			idx = 0;
		}
		selectButton(buttonList.get(idx),false);			
		return catalogList.get(idx);
	}
	
	public CatalogEntry selectPreviousCatalog(){
		int idx;
		idx = catalogList.indexOf(selectedCatalog) - 1;
		if (idx<0)
		{
			idx = catalogList.size()-1;
		}
		selectButton(buttonList.get(idx),false);	
		return catalogList.get(idx);
	}
	
	public CatalogEntry getNextCatalog(){
		int idx;
		idx = catalogList.indexOf(selectedCatalog) + 1;
		if (idx>=catalogList.size())
		{
			idx = 0;
		}
		return catalogList.get(idx);
	}
	
	public CatalogEntry getPreviousCatalog(){
		int idx;
		idx = catalogList.indexOf(selectedCatalog) - 1;
		if (idx<0)
		{
			idx = catalogList.size()-1;
		}
		return catalogList.get(idx);
	}
	
	private void selectButton(ImageButton btn,boolean sendEvent){
		int idx = 0;
		for (ImageButton lButton:buttonList){
			if (lButton == btn) {
				selectedCatalog = catalogList.get(idx);
				lButton.setImageBitmap(selectedCatalog.icon);
				if (selectedCatalog.showName){
					textList.get(idx).setTextAppearance(context, R.style.boldText);
				}
			} else {
				CatalogEntry catalog = catalogList.get(idx);
				lButton.setImageBitmap(catalog.icond);
				if (catalog.showName){
					textList.get(idx).setTextAppearance(context, R.style.normalText);
				}
			}
			idx ++;
		}
		if (sendEvent&&(onCatalogChangeListener != null)){
			onCatalogChangeListener.onChange(btn,0);
		}
		mHandler.removeCallbacks(scrollRunnable); 
	}
	
	public void setOnButtonChangeListener(OnChangeListener onCatalogChangeListener){
		this.onCatalogChangeListener = onCatalogChangeListener;
	}

	private Runnable scrollRunnable = new Runnable() {
		private boolean reverse = false;
		public void run() {
			int off = buttonsLayout.getMeasuredHeight() - scrollButtonBar.getHeight();
			if (off > 0) {
				if (reverse) {
					scrollButtonBar.scrollBy(0, -5);
					if (scrollButtonBar.getScrollY() == 0) {
						reverse = false;
					}
				} 
				else {
					scrollButtonBar.scrollBy(0, 5);
					if (scrollButtonBar.getScrollY() == off) {
						reverse = true;
					}
				}
			}
			mHandler.postDelayed(this, 300);
		}
	};

	
}
