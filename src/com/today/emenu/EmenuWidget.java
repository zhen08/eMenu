package com.today.emenu;

import android.content.Context;
import android.widget.RelativeLayout;

public class EmenuWidget extends RelativeLayout{
	protected String widgetType;
	protected MenuData menuData;
	protected int textColor;
	protected int backgroundColor1;
	protected int backgroundColor2;
	protected SelectItemWidget selectItem;

	public EmenuWidget(Context context) {
		super(context);
		widgetType = "Unknown";
		menuData =  ((EMenuApplication)(context.getApplicationContext())).menuData;
		textColor = menuData.globalTextColor;
		backgroundColor1 = menuData.globalBackgroundColor1; 
		backgroundColor2 = menuData.globalBackgroundColor2; 
	}

	public void refreshButtons(){
		if (selectItem != null){
			selectItem.refreshButtons();
		}
	}

	public void loadItem(ItemEntry item){
		if (!widgetType.equals("wholepage")){
			if (item.id % 2 == 0){
				setBackgroundColor(backgroundColor1);
			} else {
				setBackgroundColor(backgroundColor2);
			}
		}
	}
}
