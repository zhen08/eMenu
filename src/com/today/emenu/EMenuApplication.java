package com.today.emenu;

import java.util.ArrayList;
import android.app.Application;

public class EMenuApplication extends Application {
	
	public ArrayList<TrxLineEntry> trx;
	public MenuData menuData;
	public boolean mutex;
	
	public EMenuApplication(){
		trx = new ArrayList<TrxLineEntry>();
		menuData = null;
	}

	public TrxLineEntry findTrxLineById(int id){
		for (int i=0;i<trx.size();i++){
			if (trx.get(i).id == id){
				return trx.get(i);
			}
		}
		return null;
	}
	
	public TrxLineEntry changeTrxLine(ItemEntry item, int delta){
		TrxLineEntry trxLine = findTrxLineById(item.id);
		if (trxLine != null){
			trxLine.quantity += delta;
		} else {
			trxLine = new TrxLineEntry(item.id, item.name, item.code, item.price, delta);
			trx.add(trxLine);
		}
		if (trxLine.quantity <= 0){
			trx.remove(trxLine);
			return null;
		} else {
			return trxLine;
		}
	}
	
	public TrxLineEntry changeTrxLine(TrxLineEntry trxLine, int delta){
		trxLine.quantity += delta;
		if (trxLine.quantity <= 0){
			trx.remove(trxLine);
			return null;
		} else {
			return trxLine;
		}
	}
	
	public int getTrxItemCount(){
		int count = 0;
		for (TrxLineEntry trxLine:trx ){
			count += trxLine.quantity;
		}
		return count;
	}
}
