package com.today.emenu;

import com.today.emenu.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class QuarterPageWidget extends EmenuWidget {

	private Context context;
	private ImageView img;
	private TextView textCurrencyName;
	private TextView textName;
	private TextView textCode;
	private TextView textDescription;
	private TextView textPrice;
	private TextView textUnit;
	private	PopupWindow popupWindow;
	private String imageFile;

	public QuarterPageWidget(Context context) {
		super(context);
		this.context = context;
		widgetType = "quarterpage";
		this.context = context;
 		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater)getContext().getSystemService(infService);
		li.inflate(R.layout.quarterpagewidget, this, true);
		img = (ImageView) findViewById(R.id.quarterPageImg);
		img.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				showPopupImage(v);
			}
		});
		textCurrencyName = (TextView)findViewById(R.id.quarterPageTextCurrency);
		textCurrencyName.setTextColor(textColor);
		textName = (TextView)findViewById(R.id.quarterPageTextName);
		textName.setTextColor(textColor);
		textCode = (TextView)findViewById(R.id.quarterPageTextCode);
		if (menuData.showItemCode){
			textCode.setTextColor(textColor);
		} else {
			textCode.setVisibility(INVISIBLE);
		}
		textDescription = (TextView)findViewById(R.id.quarterPageTextDescription);
		textDescription.setTextColor(textColor);
		textPrice = (TextView)findViewById(R.id.quarterpageTextPrice);
		textPrice.setTextColor(textColor);
		textUnit = (TextView)findViewById(R.id.quarterpageTextUnit);
		textUnit.setTextColor(textColor);
		selectItem = (SelectItemWidget)findViewById(R.id.quarterPageSelectItem);
	}

	private void showPopupImage(View parent){
		View view;
		if (popupWindow == null)
		{
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.popupimage, null);
			ImageView imagePopup = (ImageView)view.findViewById(R.id.popupImg);
			popupWindow = new PopupWindow(view, 768, 675);

			try {
				imagePopup.setImageBitmap(BitmapFactory.decodeStream(context.openFileInput(imageFile)));
//				imagePopup.setImageBitmap(BitmapFactory.decodeFile(imageFile));
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(false);
			popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, 0, 170);
			imagePopup.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (popupWindow.isShowing()){
						popupWindow.dismiss();
						popupWindow = null;
//						System.gc();
					}
				}
			});
		}

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
		imageFile = item.img;
		
		if (!item.img.isEmpty()){
			try {
				img.setImageBitmap(BitmapFactory.decodeStream(context.openFileInput(item.img)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
