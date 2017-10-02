package com.today.emenu;

import com.today.emenu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;


public class TextOnlyWidget extends EmenuWidget {

	private TextView textCurrencyName;
	private TextView textName;
	private TextView textCode;
	private TextView textPrice;
	private TextView textUnit;

	public TextOnlyWidget(Context context) {
		super(context);
		widgetType = "textonly";
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.textonlywidget, this, true);
		textCurrencyName = (TextView)findViewById(R.id.textOnlyTextCurrency);
		textCurrencyName.setTextColor(textColor);
		textName = (TextView)findViewById(R.id.textOnlyTextName);
		textName.setTextColor(textColor);
		textCode = (TextView)findViewById(R.id.textOnlyTextCode);
		if (menuData.showItemCode){
			textCode.setTextColor(textColor);
		} else {
			textCode.setVisibility(INVISIBLE);
		}
		textPrice = (TextView)findViewById(R.id.textOnlyTextPrice);
		textPrice.setTextColor(textColor);
		textUnit = (TextView)findViewById(R.id.textOnlyTextUnit);
		textUnit.setTextColor(textColor);
		selectItem = (SelectItemWidget)findViewById(R.id.textOnlySelectItem);
	}
	
	@Override
	public void loadItem(ItemEntry item){
		super.loadItem(item);
		selectItem.loadItem(item);
		textName.setText(item.name);
		textCode.setText(item.code);
		int roundedPrice = Math.round(item.price);
		if (roundedPrice == item.price) {
			textPrice.setText(Integer.toString(roundedPrice));
		} else {
			textPrice.setText(Float.toString(item.price));
		}
		textUnit.setText(item.unit);
	}

}
