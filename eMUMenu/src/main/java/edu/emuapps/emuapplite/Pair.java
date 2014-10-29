package edu.emuapps.emuapplite;

/**
 * Created by sjoshua270 on 10/19/2014.
 */
public class Pair {
	private String item;
	private String price;
	public Pair(String item, String price){
		this.item = item;
		this.price = price;
	}

	public String getItem(){
		return item;
	}
	public String getPrice(){
		return price;
	}
}
