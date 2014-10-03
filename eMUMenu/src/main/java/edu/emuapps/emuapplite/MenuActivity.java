package edu.emuapps.emuapplite;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MenuActivity extends ActionBarActivity {

	private int mode;
	private ViewPager pager;
	private Context mContext;
	private boolean refreshVisible;
	private Element mealData;
	private String URL;
	private String[] titles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private StorageManager sm;
	private MenuPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);
		//Store context for other classes to use
		mContext = this;
		//Storage Manager cached
		sm = new StorageManager(this);
		//Whether the refresh button should be visible
		refreshVisible = true;
		//Section of the app (Caf, Den, Hours, etc.)
		mode = 0;
		//Section titles
		titles = this.getResources().getStringArray(R.array.navigation_list);

		// URL of the site
		URL = "http://goo.gl/T8bwaO";

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		                                                R.layout.drawer_list_item,
		                                                this.getResources().getStringArray(R.array.navigation_list)));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close
		) {
			// Called when a drawer has settled in a completely closed state.
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(titles[mode]);
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			// Called when a drawer has settled in a completely open state.
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mContext.getResources().getString(R.string.drawer_open));
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		loadCaf();
		sendAnalytics();
	}

	/**
	 * setting up Analytics Trackers
	 */
	public enum TrackerName {
		APP_TRACKER
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = analytics.newTracker(R.xml.tracker);
			mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}

	public void sendAnalytics(){
		Tracker t = getTracker(
				TrackerName.APP_TRACKER);

		t.setScreenName("Main Activity");

		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	/**
	 * Gets the data to populate the menu from storage, or gets fresher data if it is more than one
	 * day old
	 */
	private void loadCaf() {
		// Get the time-stamp of the data
		long timestamp = sm.getLong("TIMESTAMP");
		String data = sm.getString("DATA");

		if (data != "") {
			// If the data is more than a day old, get new data
			if ((new Date().getTime() - timestamp) > 86400000) {
				new getDoc().execute(URL);
			}
			// Otherwise, load immediately
			else {
				mealData = Jsoup.parse(data).getElementsByClass("Menu").first().child(0);
				populatePager(mode);
			}
		}
		else
			new getDoc().execute(URL);
	}

	/**
	 * Gets the data for each meal and day
	 *
	 * @param meal Breakfast, Lunch, Dinner (1,3,5)
	 * @param day  Day of the week
	 * @return
	 */
	public ArrayList<String> getMealData(int meal, int day) {
		ArrayList<String> food = new ArrayList<String>();
		//Add food items from obtained mealDat
		if (mealData != null) {
			Element aMeal = mealData.children().get(meal).child(day);
			for (Element item : aMeal.children()) {
				food.add(item.text());
			}
		}
		else {
			food.add(mContext.getResources().getString(R.string.no_connection));
		}
		return food;
	}

	public void favorite(View v) {
		if (mode == 0 || mode == 4) {
			TextView tv = (TextView) ((ViewGroup) v).getChildAt(0);
			String s = tv.getText().toString();
			List<String> favs = sm.getArray("FAVORITES");
			CheckBox cb = (CheckBox) (((ViewGroup) v).getChildAt(3));
			cb.toggle();
			if (!cb.isChecked() && favs.contains(s)) {
				favs.remove(s);
			}
			if (cb.isChecked() && !favs.contains(s)) {
				favs.add(s);
			}

			sm.putArray(favs, "FAVORITES");
		}
	}

	/**
	 * Fills up the respective List<String> of items for each category
	 *
	 * @param mode Determines which section of the app
	 * @return List of Strings to populate the views
	 */
	private List<String> getItems(int mode) {
		switch (mode) {
			case 0:
				return cafeteria();
			case 1:
				return royalsDen();
			case 2:
				return commonGrounds();
			case 3:
				return hours();
			case 4:
				return favs();
		}
		return null;
	}

	/**
	 * Fills the cafeteria list
	 *
	 * @return
	 */
	private ArrayList<String> cafeteria() {
		ArrayList<String> caf = new ArrayList<String>();
		for (int page = 1; page < 7; page++) {
			caf.add("start_of_section");
			caf.add("*Breakfast");
			caf.addAll(getMealData(1, page));
			caf.add("*Lunch");
			caf.addAll(getMealData(3, page));
			caf.add("*Dinner");
			caf.addAll(getMealData(5, page));
			caf.add("end_of_section");
		}
		return caf;
	}


	private ArrayList<String> royalsDen() {
		ArrayList<String> den = new ArrayList<String>();
		den.add("start_of_section");
		den.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.den_breakfast)));
		den.add("end_of_section");
		den.add("start_of_section");
		den.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.den_subs)));
		den.add("end_of_section");
		den.add("start_of_section");
		den.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.den_sandwiches)));
		den.add("end_of_section");
		den.add("start_of_section");
		den.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.den_wraps)));
		den.add("end_of_section");
		den.add("start_of_section");
		den.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.den_fryer)));
		den.add("end_of_section");
		den.add("start_of_section");
		den.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.den_pizza)));
		den.add("end_of_section");

		return den;
	}

	private ArrayList<String> commonGrounds() {
		ArrayList<String> cg = new ArrayList<String>();
		cg.add("start_of_section");
		cg.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.drinks)));
		cg.add("end_of_section");
		cg.add("start_of_section");
		cg.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.food)));
		cg.add("end_of_section");
		cg.add("start_of_section");
		cg.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.combos_snacks)));
		cg.add("end_of_section");
		cg.add("start_of_section");
		cg.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.smoothies)));
		cg.add("end_of_section");

		return cg;
	}

	private ArrayList<String> hours() {
		ArrayList<String> hours = new ArrayList<String>();
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.caf_times)));
		hours.add("end_of_section");
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.den_times)));
		hours.add("end_of_section");
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.lib_times)));
		hours.add("end_of_section");
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.fit_times)));
		hours.add("end_of_section");
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.post_times)));
		hours.add("end_of_section");
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.cg_times)));
		hours.add("end_of_section");
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.business_times)));
		hours.add("end_of_section");
		hours.add("start_of_section");
		hours.addAll(Arrays.asList(mContext.getResources().getStringArray(R.array.gameroom_times)));
		hours.add("end_of_section");

		return hours;
	}

	private List<String> favs() {
		return sm.getArray("FAVORITES");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void selectItem(int position) {
		mDrawerList.setItemChecked(position, true);
		mode = position;
		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		refreshVisible = false;
		if (position == 0) {
			refreshVisible = true;
			loadCaf();
		}
		populatePager(mode);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void populatePager(int mode) {

		List<String> items = new ArrayList<String>();
		items = getItems(mode);

		adapter = new MenuPagerAdapter(sm, mode, items, mContext);

		pager = (ViewPager) findViewById(R.id.menu_pager);

		pager.setAdapter(adapter);

		// Bind the tabs to the ViewPager
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		//Color of the tabs
		tabs.setIndicatorColor(Color.parseColor("#60a6bc"));
		//And... Couple
		tabs.setViewPager(pager);

		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		if (day == 1) day = 6;
		else day = day - 2;

		if (mode == 0)
			pager.setCurrentItem(day);

		pager.setOffscreenPageLimit(6);
		pager.setVisibility(View.VISIBLE);

		AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(500);
		pager.startAnimation(anim);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		if (refreshVisible)
			menu.getItem(0).setVisible(true);
		else
			menu.getItem(0).setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == R.id.refresh) {
			AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
			aa.setDuration(1000);
			new getDoc().execute(URL);
			pager.startAnimation(aa);
			pager.setVisibility(View.GONE);
		}
		return super.onOptionsItemSelected(item);
	}

	//Downloads data and stores it in SharedPreferences
	private class getDoc extends AsyncTask<String, Void, Boolean> {
		ProgressDialog dialog = new ProgressDialog(MenuActivity.this);

		@Override
		protected void onPreExecute() {
			//Shows loading dialog
			dialog.setMessage("Loading data...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... url) {
			try {
				String data = Jsoup.connect(url[0]).get().toString();

				sm.putString("DATA", data);
				sm.putLong("TIMESTAMP", new Date().getTime());
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (dialog.isShowing())
				dialog.dismiss();

			if (success) {
				String data = sm.getString("DATA");
				mealData = Jsoup.parse(data).getElementsByClass("Menu").first().child(0);
				mode = 0;
				populatePager(mode);
			}
			if (!success) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
				// Set the pieces of the dialog box.
				builder
						.setTitle("Network Error")
						.setMessage("There was an problem updating your information. Please check your internet connection")
						.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new getDoc().execute(URL);
							}
						})
						.setNegativeButton("Keep old data", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								populatePager(0);
							}
						});
				builder.create();
				builder.show();
			}
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}
}
