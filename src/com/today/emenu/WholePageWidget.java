package com.today.emenu;

import com.today.emenu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

public class WholePageWidget extends EmenuWidget {

	private TextView textCurrencyName;
	private TextView textName;
	private TextView textCode;
	private TextView textDescription;
	private TextView textPrice;
	private TextView textUnit;

	public WholePageWidget(Context context) {
		super(context);
		widgetType = "wholepage";
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.wholepagewidget, this, true);
		setBackgroundColor(0x00000000);
		textCurrencyName = (TextView)findViewById(R.id.wholePageTextCurrency);
		textCurrencyName.setTextColor(textColor);
		textName = (TextView)findViewById(R.id.wholePageTextName);
		textName.setTextColor(textColor);
		textCode = (TextView)findViewById(R.id.wholePageTextCode);
		if (menuData.showItemCode){
			textCode.setTextColor(textColor);
		} else {
			textCode.setVisibility(INVISIBLE);
		}
		textDescription = (TextView)findViewById(R.id.wholePageTextDescription);
		textDescription.setTextColor(textColor);
		textPrice = (TextView)findViewById(R.id.wholepageTextPrice);
		textPrice.setTextColor(textColor);
		textUnit = (TextView)findViewById(R.id.wholepageTextUnit);
		textUnit.setTextColor(textColor);
		selectItem = (SelectItemWidget)findViewById(R.id.wholePageSelectItem);
	}
	
	@Override
	public void loadItem(ItemEntry item){
		super.loadItem(item);
		selectItem.loadItem(item);
		textName.setText(item.name);
		textCode.setText(item.code);
		textDescription.setText(item.description);
		int roundedPrice = Math.round(item.price);
		if (roundedPrice == item.price) {
			textPrice.setText(Integer.toString(roundedPrice));
		} else {
			textPrice.setText(Float.toString(item.price));
		}
		textUnit.setText(item.unit);
	}

}
