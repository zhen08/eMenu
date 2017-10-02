package com.today.emenu;

import java.util.ArrayList;

import com.today.emenu.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OrderPage extends RelativeLayout {

	private Context context;
	private ArrayList<OrderLineWidget> orderLineList;
	private OnChangeListener onOrderPageCloseListener;
	private TextView totalPriceText;
	private ArrayList<TrxLineEntry> trx;

	public OrderPage(Context context) {
		super(context);
		init(context);
	}

	public OrderPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public OrderPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context){
		this.context = context;
		setBackgroundColor(0xffd8d8d9);

		trx = ((EMenuApplication)(context.getApplicationContext())).trx;
		orderLineList = new ArrayList<OrderLineWidget>();

		ImageView titleImage = new ImageView(context);
		titleImage.setImageResource(R.drawable.ordertitle);
		RelativeLayout.LayoutParams lpTitleImage = new RelativeLayout.LayoutParams(608,58);
		lpTitleImage.leftMargin = 0;
		lpTitleImage.topMargin = 0;
		lpTitleImage.alignWithParent = true;
		addView(titleImage,lpTitleImage);

		ImageView backgroundImage = new ImageView(context);
		backgroundImage.setImageResource(R.drawable.orderlinebg);
		RelativeLayout.LayoutParams lpBackgroundImage = new RelativeLayout.LayoutParams(608,966);
		lpBackgroundImage.leftMargin = 0;
		lpBackgroundImage.topMargin = 58;
		lpBackgroundImage.alignWithParent = true;
		addView(backgroundImage,lpBackgroundImage);

		TextView titleText = new TextView(context);
		titleText.setText(context.getString(R.string.myorder));
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,40);
		titleText.setTextColor(0xffffffff);
		RelativeLayout.LayoutParams lpTitleText = new RelativeLayout.LayoutParams(500,58);
		lpTitleText.leftMargin = 32;
		lpTitleText.topMargin = 6;
		lpTitleText.alignWithParent = true;
		addView(titleText,lpTitleText);


		ImageButton closeButton = new ImageButton(context);
		closeButton.setImageResource(R.drawable.orderclose);
		closeButton.setBackgroundColor(0x00000000);
		closeButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				if (onOrderPageCloseListener != null) {
					onOrderPageCloseListener.onChange(v,0);
				}
			}
		});
		RelativeLayout.LayoutParams lpCloseButton = new RelativeLayout.LayoutParams(46,46);
		lpCloseButton.leftMargin = 560;
		lpCloseButton.topMargin = 6;
		lpCloseButton.alignWithParent = true;
		addView(closeButton,lpCloseButton);


		OrderLineWidget orderLine;
		RelativeLayout.LayoutParams lpOrderLine;
		for (int i=0;i<20;i++) {
			orderLine = new OrderLineWidget(context);
			orderLine.setOnLineDeleteListener(new OnChangeListener(){

				public void onChange(View v,int Param) {
					loadOrder();
				}
			});
			orderLine.loadOrderLine(null, i);
			lpOrderLine = new RelativeLayout.LayoutParams(608,46);
			lpOrderLine.leftMargin = 0;
			lpOrderLine.topMargin = i * 46 + 58;
			lpOrderLine.alignWithParent = true;
			addView(orderLine,lpOrderLine);
			orderLineList.add(orderLine);
		}

		TextView totalText = new TextView(context);
		totalText.setText(context.getString(R.string.total));
		totalText.setTextSize(TypedValue.COMPLEX_UNIT_PX,30);
		totalText.setTextColor(0xff939494);
		RelativeLayout.LayoutParams lpTotalText = new RelativeLayout.LayoutParams(300,46);
		lpTotalText.leftMargin = 32;
		lpTotalText.topMargin = 980;
		lpTotalText.alignWithParent = true;
		addView(totalText,lpTotalText);

		totalPriceText = new TextView(context);
		totalPriceText.setText("0");
		totalPriceText.setTextSize(TypedValue.COMPLEX_UNIT_PX,22);
		totalPriceText.setTextColor(0xffff0000);
		RelativeLayout.LayoutParams lpTotalPriceText = new RelativeLayout.LayoutParams(300,46);
		lpTotalPriceText.leftMargin = 370;
		lpTotalPriceText.topMargin = 980;
		lpTotalPriceText.alignWithParent = true;
		addView(totalPriceText,lpTotalPriceText);

		Button clearButton = new Button(context);
		clearButton.setBackgroundColor(0x00000000);
		clearButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,20);
		clearButton.setText(context.getString(R.string.clear));
		clearButton.setTextColor(0xffff0000);
		clearButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				clearOrder();
			}
		});
		RelativeLayout.LayoutParams lpClearButton = new RelativeLayout.LayoutParams(80,60);
		lpClearButton.leftMargin = 530;
		lpClearButton.topMargin = 975;
		lpClearButton.alignWithParent = true;
		addView(clearButton,lpClearButton);


	}

	public final void loadOrder() {
		float totalPrice = 0;
		for (TrxLineEntry orderLine: trx){
			totalPrice += orderLine.price * orderLine.quantity;
		}

		int orderSize = trx.size();
		for (int i=0;i<20;i++){
			if (i<orderSize) {
				orderLineList.get(i).loadOrderLine(trx.get(i), i);
			} else {
				orderLineList.get(i).loadOrderLine(null, i);
			}
		}

		int roundedPrice = Math.round(totalPrice);
		if (roundedPrice == totalPrice) {
			totalPriceText.setText(Integer.toString(roundedPrice));
		} else {
			totalPriceText.setText(Float.toString(totalPrice));
		}
	}

	private void clearOrder(){
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setMessage(context.getString(R.string.erase_order_confirmation));
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.confirm),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				trx.clear();
				if (onOrderPageCloseListener != null) {
					onOrderPageCloseListener.onChange(null,1);
				}
			}
		});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.show();
	}

	public void setOnOrderPageCloseListener(OnChangeListener onOrderPageCloseListener){
		this.onOrderPageCloseListener = onOrderPageCloseListener;
	}
}
