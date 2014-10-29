package edu.emuapps.emuapplite;

import java.util.List;

/**
 * Created by sjoshua270 on 10/18/2014.
 */
public class Card {
	private String title;
	private String subTitle;
	private List<Pair> data;

	public Card(String title, String subTitle, List<Pair> items){
		this.title = title;
		this.subTitle = subTitle;
		this.data = items;
	}

	/**
	 * Gets the title of a card
	 * @return String Title for the given card
	 */
	public String getTitle(){
		return title;
	}

	/**
	 * Gets the subtitle of a card
	 * @return
	 */
	public String getSubTitle(){
		return subTitle;
	}
	/**
	 * Gets the data of a card
	 * @return List<String> List of data for the given card
	 */
	public List<Pair> getData(){
		return data;
	}
}
