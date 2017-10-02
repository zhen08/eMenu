package com.today.emenu;

import com.today.emenu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

public class TitleWidget extends EmenuWidget {

	private TextView textName;

	public TitleWidget(Context context) {
		super(context);
		widgetType = "title";
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.titlewidget, this, true);
		textName = (TextView)findViewById(R.id.titleTextName);
		textName.setTextColor(textColor);
	}
	
	@Override
	public void loadItem(ItemEntry item){
		super.loadItem(item);
		textName.setText(item.name);
	}
}
