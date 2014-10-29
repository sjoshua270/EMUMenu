package edu.emuapps.emuapplite;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {
	private Context context;
	private List<Page> pages;

	public MyPagerAdapter(Context context, List<Page> pages) {
		this.context = context;
		this.pages = pages;
	}

	public int getCount() {
		return pages.size();
	}

	public String getPageTitle(int position) {
		return pages.get(position).getTitle();
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(container.getContext())
		                                                         .inflate(R.layout.recyclerview, container, false);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(new MyRecyclerAdapter(context, pages.get(position).getCards()));
		container.addView(recyclerView);
		return recyclerView;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((RecyclerView) object);
	}

}
