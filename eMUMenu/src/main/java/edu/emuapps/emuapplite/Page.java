package edu.emuapps.emuapplite;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sjoshua270 on 10/18/2014.
 */
public class Page {
	private String title;
	private List<Card> cards;

	public Page(String title, List<Card> cards){
			this.title = title;
			this.cards = cards;
	}

	/**
	 * Gets the title for a page
	 * @return String title of the page
	 */
	public String getTitle(){
		return title;
	}
	/**
	 * Gets the cards to populate a page
	 * @return List<Card> list of cards
	 */
	public List<Card> getCards(){
		return cards;
	}
}
