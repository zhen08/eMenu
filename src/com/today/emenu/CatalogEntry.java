package com.today.emenu;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class CatalogEntry {
	public int id;
	public String name;
	public Bitmap icon;
	public Bitmap icond;
	public int iconHeight;
	public boolean showName;
	public ArrayList<PageEntry> pageList;
	
	public CatalogEntry(int id, String name, Bitmap icon, Bitmap icond, int iconHeight, boolean showName){
		pageList = new ArrayList<PageEntry>();
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.icond = icond;
		this.iconHeight = iconHeight;
		this.showName = showName;
	}
}
