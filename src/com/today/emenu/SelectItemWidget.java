package com.today.emenu;

import com.today.emenu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class SelectItemWidget extends RelativeLayout {

	private ImageButton buttonSelect;
	private ImageButton buttonLeft;
	private ImageButton buttonRight;
	private EditText editQty;
	private ItemEntry item;
	private OnChangeListener onSelectedListener;
	
	static EMenuApplication application;

	public SelectItemWidget(Context context) {
		super(context);
		init(context);
	}

	public SelectItemWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SelectItemWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context){
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.selectitemwidget, this, true);
		this.setBackgroundColor(0x00000000);
		editQty = (EditText)findViewById(R.id.selectEditQty);
		editQty.setTextColor(0xffff0000);
		buttonSelect = (ImageButton)findViewById(R.id.selectOrderButton);
		buttonLeft = (ImageButton)findViewById(R.id.selectLeftButton);
		buttonRight = (ImageButton)findViewById(R.id.selectRightButton);
		editQty.setVisibility(INVISIBLE);
		buttonLeft.setVisibility(INVISIBLE);
		buttonRight.setVisibility(INVISIBLE);
		item = null;
		application = ((EMenuApplication)(context.getApplicationContext()));
	}

	public void loadItem(ItemEntry item){
		this.item = item;
		buttonSelect.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				changeQuantity(1);
			}
		});
		buttonLeft.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				changeQuantity(-1);
			}
		});
		buttonRight.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				changeQuantity(1);
			}
		});
		refreshButtons();
	}

	public void setOnSelectedListener(OnChangeListener onCatalogChangeListener){
		this.onSelectedListener = onCatalogChangeListener;
	}

	public void refreshButtons(){
		TrxLineEntry orderLine = application.findTrxLineById(item.id);
		if ((orderLine != null)&&(orderLine.quantity>0)){
			editQty.setVisibility(VISIBLE);
			buttonLeft.setVisibility(VISIBLE);
			buttonRight.setVisibility(VISIBLE);
			buttonSelect.setVisibility(INVISIBLE);
			editQty.setText(Integer.toString(orderLine.quantity));			
		}else{
			editQty.setVisibility(INVISIBLE);
			buttonLeft.setVisibility(INVISIBLE);
			buttonRight.setVisibility(INVISIBLE);
			buttonSelect.setVisibility(VISIBLE);
			editQty.setText("0");
		}
	}
	
	private void changeQuantity(int deltaQuantity){
		application.changeTrxLine(item, deltaQuantity);
		refreshButtons();
		if ((deltaQuantity == 1)&&(onSelectedListener != null)) {
			onSelectedListener.onChange(this,1);
		}
	}
}
