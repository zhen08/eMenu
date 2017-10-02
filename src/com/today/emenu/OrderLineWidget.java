package com.today.emenu;
import com.today.emenu.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class OrderLineWidget extends RelativeLayout {

	private Context context;
	private TextView textName;
	private TextView textCode;
	private TextView textPrice;
	private ImageButton buttonLeft;
	private ImageButton buttonRight;
	private Button buttonDelete;
	private EditText editQty;
	private OnChangeListener onLineDeleteListener;

	private TrxLineEntry orderLine;
	
	public OrderLineWidget(Context context) {
		super(context);
		init(context);
	}

	public OrderLineWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public OrderLineWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context){
		this.context = context;
		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.orderlinewidget, this, true);
		textName = (TextView)findViewById(R.id.orderLineTextName);
		textCode = (TextView)findViewById(R.id.orderLineTextCode);
		textPrice = (TextView)findViewById(R.id.orderLineTextPrice);
		buttonLeft = (ImageButton)findViewById(R.id.orderLineButtonLeft);
		buttonRight = (ImageButton)findViewById(R.id.orderLineButtonRight);
		editQty = (EditText)findViewById(R.id.orderLineEditQty);
		buttonDelete = (Button)findViewById(R.id.orderLineButtonDelete);
		orderLine = null;
	}
	
	public void loadOrderLine(TrxLineEntry orderLine, int lineNumber){
		if (orderLine == null){
			textName.setVisibility(INVISIBLE);
			textCode.setVisibility(INVISIBLE);
			textPrice.setVisibility(INVISIBLE);
			buttonLeft.setVisibility(INVISIBLE);
			buttonRight.setVisibility(INVISIBLE);
			editQty.setVisibility(INVISIBLE);
			buttonDelete.setVisibility(INVISIBLE);
			if (lineNumber %2 == 0) {
				this.setBackgroundColor(0x00000000);
			} else {
				this.setBackgroundResource(R.drawable.orderlinebg1);
			}
		} else {
			this.orderLine = orderLine;
			textName.setText(orderLine.name);
			textCode.setText(orderLine.code);
			editQty.setText(Integer.toString(orderLine.quantity));
			float totalPrice = orderLine.price*orderLine.quantity;
			int roundedPrice = Math.round(totalPrice);
			if (roundedPrice == totalPrice) {
				textPrice.setText(Integer.toString(roundedPrice));
			} else {
				textPrice.setText(Float.toString(totalPrice));
			}
			buttonDelete.setOnClickListener(new View.OnClickListener() {			
				public void onClick(View v) {
					changeQuantity(-99999);
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
			textName.setVisibility(VISIBLE);
			textCode.setVisibility(VISIBLE);
			textPrice.setVisibility(VISIBLE);
			buttonLeft.setVisibility(VISIBLE);
			buttonRight.setVisibility(VISIBLE);
			editQty.setVisibility(VISIBLE);
			buttonDelete.setVisibility(VISIBLE);
		}
	}
	
	public void changeQuantity(int deltaQty){
		if (orderLine != null) {
			orderLine = ((EMenuApplication)(context.getApplicationContext())).changeTrxLine(orderLine, deltaQty);
			if (orderLine != null){
				editQty.setText(Integer.toString(orderLine.quantity));
				final float totalPrice = orderLine.price*orderLine.quantity;
				final int roundedPrice = Math.round(totalPrice);
				if (roundedPrice == totalPrice) {
					textPrice.setText(Integer.toString(roundedPrice));
				} else {
					textPrice.setText(Float.toString(totalPrice));
				}
			}
		}
		if (onLineDeleteListener != null){
			onLineDeleteListener.onChange(null,0);
		}
	}

	public void setOnLineDeleteListener(OnChangeListener onLineDeleteListener){
		this.onLineDeleteListener = onLineDeleteListener;
	}

}
