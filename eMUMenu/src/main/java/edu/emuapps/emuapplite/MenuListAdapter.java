package edu.emuapps.emuapplite;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class MenuListAdapter extends BaseAdapter {

	List<String> items, favs;
	Context mContext;
	LayoutInflater mInflater;
	int mode;

	public MenuListAdapter(StorageManager sm, Context context, int page, List<String> itms, int layoutViewResourceId, int md) {
		mContext = context;
		mode = md;
		if (md != 4) {
			int count = 0, start = 0, end = 0;
			for (int i = 0; i < itms.size(); i++) {
				if (count < page) {
					if (itms.get(i) == "start_of_section")
						start = i + 1;
					if (itms.get(i) == "end_of_section") {
						end = i;
						count++;
					}
				}
			}
			items = itms.subList(start, end);
			favs = sm.getArray("FAVORITES");
		}
		else {
			items = sm.getArray("FAVORITES");
			favs = items;
		}


		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		String str = items.get(position);
		View t = mInflater.inflate(R.layout.list_view_item, null);
		TextView title = (TextView) t.findViewById(R.id.title);
		TextView price = (TextView) t.findViewById(R.id.price);
		TextView subText = (TextView) t.findViewById(R.id.sub_text);
		View bar = (View) t.findViewById(R.id.bar);
		CheckBox check = (CheckBox) t.findViewById(R.id.faved);



		/*if(mode == 0 || mode == 4){
			if(favs.contains(str))
				check.setChecked(true);
		}
		*/
		/*
		 * This is to hide a feature
		 */
		//else
		check.setVisibility(View.INVISIBLE);
		if (str.contains("-$")) {
			char dol = ' ';
			int i = 0;
			while (dol != '$') {
				i++;
				dol = str.charAt(i);
			}
			price.setText(str.substring(i + 1, str.length()));
			price.setTextSize(15);
			str = str.substring(0, i - 1);
		}
		if (str.contains("-#")) {
			char dol = ' ';
			int i = 0;
			while (dol != '#') {
				i++;
				dol = str.charAt(i);
			}
			subText.setText(str.substring(i + 1, str.length()));
			subText.setTextSize(15);
			str = str.substring(0, i - 1);
			subText.setVisibility(View.VISIBLE);
		}
		if (str.startsWith("*")) {
			title.setPadding(10, 10, 0, 0);
			title.setText("  " + str.substring(1, str.length()));
			title.setTextSize(23);
			title.setTypeface(Typeface.DEFAULT_BOLD);
			bar.setVisibility(View.VISIBLE);
			check.setVisibility(View.GONE);
		}
		else {
			title.setText(str);
		}
		return t;
	}


}
