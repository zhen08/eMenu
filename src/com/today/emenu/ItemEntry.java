package com.today.emenu;

public class ItemEntry {
	
	public int id;
	public String itemType;
	public String name;
	public String code;
	public String description;
	public float price;
	public String unit;
	public String img;
	public boolean showCode;
	
	public ItemEntry(int id, String itemType,String name, String code, String description, float price, String unit, String img, boolean showCode) {
		this.id = id;
		this.itemType = itemType;
		this.name = name;
		this.code = code;
		this.description = description;
		this.price = price;
		this.unit = unit;
		this.img = img;
		this.showCode = showCode;
	}
	
}
