package com.today.emenu;

public class TrxLineEntry {

	public int id;
	public String name;
	public String code;
	public float price;
	public int quantity;
	
	public TrxLineEntry(int id,String name, String code, float price, int quantity) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.price = price;
		this.quantity = quantity;
	}

}
