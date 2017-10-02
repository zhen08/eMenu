package com.today.emenu;

import java.util.ArrayList;

public class PageEntry {
	public int id;
	public String background;
	public ArrayList<ItemEntry> itemList;
	public CatalogEntry catalogEntry;
	
	public PageEntry(CatalogEntry catalogEntry,int id, String background) {
		itemList = new ArrayList<ItemEntry>();
		this.id = id;
		this.background = background;
		this.catalogEntry = catalogEntry;
	}	
}
