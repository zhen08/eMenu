package com.today.emenu;

import java.lang.Thread.UncaughtExceptionHandler;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CoverPageActivity extends Activity {

	private MenuData menuData;
	private ProgressBar progressBar;
	private ProgressBar progressDownload;
	private Handler mHandler = new Handler();
	private RelativeLayout layout;
	private String serverAddress;
	private StartMainActivityTask startMainActivityTask;
	private boolean allowStartMain;
	private PendingIntent intent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allowStartMain = true;
		intent = PendingIntent.getActivity(getBaseContext(), 0,
	            new Intent(getIntent()), getIntent().getFlags());
		Thread.setDefaultUncaughtExceptionHandler(
				new UncaughtExceptionHandler(){
					@Override
					public void uncaughtException(Thread thread, Throwable ex) {
						AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
						mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, intent);
						System.exit(2);						
					}});
		if (Constants.TRIAL){
			Date date = new Date();
			if (date.getMonth() != 8){ //Only valid for September
				this.finish();
				return;
			}
		}
		if (Constants.SETWIFI){
			setupWifi();
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		layout = new RelativeLayout(this);
		setContentView(layout);
		Drawable dr = getResources().getDrawable(R.drawable.cover); 
		layout.setBackgroundDrawable(dr);

		try {
			WifiManager wifiMan = (WifiManager) this.getSystemService(
					Context.WIFI_SERVICE);
			WifiInfo wifiInf = wifiMan.getConnectionInfo();
			String macAddr = wifiInf.getMacAddress();
			Sha1Hex sha = new Sha1Hex();
			String hash = "";
			try {
				hash = sha.makeSHA1Hash(macAddr);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			TextView textSN = new TextView(this);
			textSN.setText(hash);
			RelativeLayout.LayoutParams lpTextSN = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			lpTextSN.leftMargin = 5;
			lpTextSN.topMargin = 5;
			lpTextSN.alignWithParent = true;
			layout.addView(textSN,lpTextSN);
		} catch(Exception e){
			e.printStackTrace();
		}


		progressBar = new ProgressBar(this);
		RelativeLayout.LayoutParams lpProgressBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpProgressBar.leftMargin = 380;
		lpProgressBar.topMargin = 900;
		lpProgressBar.alignWithParent = true;
		layout.addView(progressBar,lpProgressBar);
		progressBar.setVisibility(android.view.View.VISIBLE);

		progressDownload = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
		RelativeLayout.LayoutParams lpProgressDownload = new RelativeLayout.LayoutParams(500,RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpProgressDownload.leftMargin = 134;
		lpProgressDownload.topMargin = 800;
		lpProgressDownload.alignWithParent = true;
		layout.addView(progressDownload,lpProgressDownload);
		progressDownload.setVisibility(android.view.View.INVISIBLE);

		serverAddress = "";

		((EMenuApplication)(getApplicationContext())).menuData = new MenuData(this.getApplicationContext());
		menuData =  ((EMenuApplication)(getApplicationContext())).menuData;

		new LoadDataTask().execute(0);
		((EMenuApplication)(getApplicationContext())).trx.clear();
	}

	private void quit(){
		this.finish();
	}

	private void quitQuery(){
		allowStartMain = false;
		if (startMainActivityTask != null){
			startMainActivityTask.cancel(true);
		}

		final EditText editText = new EditText(this);
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.enter_admin_password));
		dialog.setIcon(android.R.drawable.ic_dialog_info);
		dialog.setView(editText);
		editText.setText("");
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.confirm),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				final String password = editText.getText().toString();
				if (password.equals(Constants.PASSWORD)){
					quit();
				}
			}
		});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				allowStartMain = true;
			}
		});
		dialog.show();    	
	}

	private void updateFromServer(){
		allowStartMain = false;
		if (startMainActivityTask != null){
			startMainActivityTask.cancel(true);
		}
		final EditText editText = new EditText(this);
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.enter_server_url));
		dialog.setIcon(android.R.drawable.ic_dialog_info);
		dialog.setView(editText);
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);  
		serverAddress = settings.getString("SERVERADD", Constants.SERVERADD);
		editText.setText(serverAddress);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.confirm),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				serverAddress = editText.getText().toString();
				SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);  
				SharedPreferences.Editor editor = settings.edit();  
				editor.putString("SERVERADD", serverAddress);  
				editor.commit();
				progressBar.setVisibility(android.view.View.INVISIBLE);
				progressDownload.setVisibility(android.view.View.VISIBLE);
				progressDownload.setProgress(0);
				Thread initDataThread = new Thread(new Runnable(){
					public void run(){
						menuData.onDownloading = new OnDownloadingListener(){

							public void onDownloading(final String fileDownloading, final int progress) {
								mHandler.post(new Runnable(){
									public void run(){
										showDownloadingFile(fileDownloading,progress);
									}
								});
							}
						};
						menuData.loadData(serverAddress);
						mHandler.post(new Runnable(){
							public void run(){
								progressDownload.setVisibility(android.view.View.INVISIBLE);
								if (menuData != null){
									allowStartMain = true;
									startMain();
								}
							}
						});

					}
				});
				initDataThread.start();
			}
		});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				allowStartMain = true;
				quitQuery();
			}
		});
		dialog.show();    	
	}

	
//	@Override
//	public void onBackPressed() {
//		if (Constants.DEBUGLOG){
//			this.finish();
//		}
//	}
	 
	private void showDownloadingFile(String fileDownloading, int progress){
		Toast.makeText(getApplicationContext(), "Downloading " + fileDownloading,
				Toast.LENGTH_SHORT).show();
		progressDownload.setProgress(progress);
	}

	private void startMain(){
		if (!allowStartMain){
			return;
		}
		Debug.Log("StartMain");
		System.gc();
		startActivity(new Intent(this,MainActivity.class));
		this.finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) { 
			if (progressBar.getVisibility() == android.view.View.INVISIBLE){
				if ((event.getX()<100)&&(event.getY()<100)){
					updateFromServer();
				}else if ((event.getX()>500)&&(event.getY()<100)){
					quitQuery();
				}else{
					startMain();
				}
			}
		}
		return true;
	}

	private class StartMainActivityTask extends AsyncTask<Integer, Object, Object> {
		@Override
		protected Object doInBackground(Integer... arg) {
			try {
				Thread.sleep(arg[0].intValue());
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			startMain();
		}

	}	

	private class LoadDataTask extends AsyncTask<Integer, Object, Object> {
		@Override
		protected Object doInBackground(Integer... arg) {
			menuData.loadData(serverAddress);
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			progressBar.setVisibility(android.view.View.INVISIBLE);
			if ((menuData == null)||(menuData.numberOfPages == 0)){
				updateFromServer();
			} else {
				startMainActivityTask = new StartMainActivityTask();
				startMainActivityTask.execute(5000);
			} 
		}

	}	

	private void setupWifi(){
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (wifi.isWifiEnabled()){
			return;
		}

		wifi.setWifiEnabled(true);

		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\"" + Constants.WIFIAP + "\"";
		if (Constants.WIFIPASS.isEmpty()) {
			wc.preSharedKey = null;
		} else {
			wc.preSharedKey = Constants.WIFIPASS;
		}

		wc.hiddenSSID = false;
		wc.status = WifiConfiguration.Status.ENABLED;

		wc.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
		wc.allowedKeyManagement.set(KeyMgmt.WPA_EAP);

		wc.allowedProtocols.set(Protocol.WPA);
		wc.allowedProtocols.set(Protocol.RSN);

		wc.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);

		wc.allowedGroupCiphers.set(GroupCipher.WEP40);
		wc.allowedGroupCiphers.set(GroupCipher.WEP104);
		wc.allowedGroupCiphers.set(GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(GroupCipher.CCMP);

		int res = wifi.addNetwork(wc);
		Debug.Log("Wifi add Network returned " + res );
		boolean b = wifi.enableNetwork(res, true);
		Debug.Log("Wifi enableNetwork returned " + b );
	}

}
