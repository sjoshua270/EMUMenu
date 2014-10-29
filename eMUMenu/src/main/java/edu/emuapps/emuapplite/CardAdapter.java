package edu.emuapps.emuapplite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sjoshua270 on 10/25/2014.
 */
public class CardAdapter extends BaseAdapter {

	Context context;
	List<Pair> contents;

	public CardAdapter(Context context, List<Pair> contents) {
		this.context = context;
		this.contents = contents;
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position Position of the item whose data we want within the adapter's data set.
	 * @return The data at the specified position.
	 */
	@Override
	public Pair getItem(int position) {
		return contents.get(position);
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 *
	 * @param position The position of the item within the adapter's data set whose row id we want.
	 * @return The id of the item at the specified position.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data set. You can either
	 * create a View manually or inflate it from an XML layout file. When the View is inflated, the
	 * parent View (GridView, ListView...) will apply default layout parameters unless you use
	 * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)} to specify
	 * a root view and to prevent attachment to the root.
	 *
	 * @param position    The position of the item within the adapter's data set of the item whose
	 *                    view we want.
	 * @param convertView The old view to reuse, if possible. Note: You should check that this view
	 *                    is non-null and of an appropriate type before using. If it is not possible
	 *                    to convert this view to display the correct data, this method can create a
	 *                    new view. Heterogeneous lists can specify their number of view types, so
	 *                    that this View is always of the right type (see {@link
	 *                    #getViewTypeCount()} and {@link #getItemViewType(int)}).
	 * @param parent      The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout cardItem = (RelativeLayout) LayoutInflater.from(parent.getContext())
		                                                         .inflate(R.layout.card_item, parent, false);
		TextView title = (TextView) cardItem.findViewById(R.id.item);
		title.setText(getItem(position).getItem());
		TextView price = (TextView) cardItem.findViewById(R.id.price);
		if (getItem(position).getPrice() != ""){
			price.setText(getItem(position).getPrice());
		}
		else {
			price.setVisibility(View.GONE);
		}
		return cardItem;
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return contents.size();
	}
}
