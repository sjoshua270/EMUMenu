package edu.emuapps.emuapplite;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import java.util.List;

public class MenuPagerAdapter extends PagerAdapter {
	private Context mContext;
	private String[] titles;
	private int mode;
	private List<String> items;
	private StorageManager sm;

	public MenuPagerAdapter(StorageManager sm, int mode, List<String> items, Context cntxt) {
		mContext = cntxt;
		this.sm = sm;
		this.items = items;
		this.mode = mode;
		getTitles();
	}

	private void getTitles() {
		//Cafeteria
		if (mode == 0)
			titles = mContext.getResources().getStringArray(R.array.days_list);
		//Royals Den
		if (mode == 1)
			titles = mContext.getResources().getStringArray(R.array.den_list);
		//Common Grounds
		if (mode == 2)
			titles = mContext.getResources().getStringArray(R.array.common_g_list);
		//Hours
		if (mode == 3)
			titles = mContext.getResources().getStringArray(R.array.place_list);
		//Favorites
		if (mode == 4)
			titles = mContext.getResources().getStringArray(R.array.favorites_title);
	}

	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public Object instantiateItem(View collection, int position) {
		ListView list = new ListView(mContext);
		list.setAdapter(new MenuListAdapter(sm, mContext, position + 1, items, R.layout.list_view_item, mode));
		list.setTag(position);
		((ViewPager) collection).addView(list);

		return list;
	}

	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
	}

	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == ((View) arg1);
	}

	public Parcelable saveState() {
		return null;
	}

	public int getCount() {
		return titles.length;
	}
}
