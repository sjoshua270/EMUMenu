package edu.emuapps.emuapplite;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
	private Toolbar toolbar;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private StorageManager sm;
	List<Page> pages;
	MyPagerAdapter adapter;
	private PagerSlidingTabStrip tabs;

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

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		pager = (ViewPager) findViewById(R.id.menu_pager);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		                                                android.R.layout.simple_list_item_1,
		                                                this.getResources().getStringArray(R.array.navigation_list)));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				toolbar,
				R.string.drawer_open,
				R.string.drawer_close
		) {
			// Called when a drawer has settled in a completely closed state.
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			// Called when a drawer has settled in a completely open state.
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerSlide(View drawerView, float slideOffSet) {
				super.onDrawerSlide(drawerView, slideOffSet);
				if (slideOffSet > 0.5)
					toolbar.setTitle(mContext.getResources().getString(R.string.drawer_open));
				else
					toolbar.setTitle(titles[mode]);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		loadCaf();
		toolbar.setBackgroundColor(getResources().getColor(R.color.light_blue_500));
		toolbar.setTitleTextColor(getResources().getColor(R.color.light_blue_50));
		tabs.setIndicatorColorResource(R.color.light_blue_300);
		tabs.setBackgroundColor(getResources().getColor(R.color.light_blue_50));
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

	public void sendAnalytics() {
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

		if (!data.equals("")) {
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
	 * Fills up the respective List<String> of items for each category
	 *
	 * @param mode Determines which section of the app
	 * @return List of Strings to populate the views
	 */
	private List<Page> getItems(int mode) {
		if (mode == 0) return cafeteria();
		return getPages(mode);
	}

	/**
	 * Fills the cafeteria list
	 *
	 * @return Pages for the caf
	 */
	private List<Page> cafeteria() {
		List<Page> pages = new ArrayList<Page>();
		List<Card> cards;
		String[] pageTitles = getResources().getStringArray(R.array.days_list);
		String[] cardTitles = {
				"Breakfast",
				"Lunch",
				"Dinner"
		};
		String[] times = {"7:00AM - 9:00AM", "11:00AM - 1:00PM", "5:00PM - 7:00PM"};
		for (int page = 0; page < pageTitles.length; page++) {
			cards = new ArrayList<Card>();
			for (int meal = 0; meal < cardTitles.length; meal++) {
				cards.add(new Card(cardTitles[meal], times[meal], getMealData(meal * 2 + 1, page + 1)));
			}
			pages.add(new Page(pageTitles[page], cards));
		}
		return pages;
	}

	/**
	 * Gets the data for each meal and day
	 *
	 * @param meal Breakfast, Lunch, Dinner (1,3,5)
	 * @param day  Day of the week
	 * @return Pairs of meal items
	 */
	public List<Pair> getMealData(int meal, int day) {
		List<Pair> pairs = new ArrayList<Pair>();
		//Add pairs items from obtained mealDat
		if (mealData != null) {
			Element aMeal = mealData.children().get(meal).child(day);
			for (Element item : aMeal.children()) {
				pairs.add(new Pair(item.text(), ""));
			}
		}
		else {
			pairs.add(new Pair(mContext.getResources().getString(R.string.no_connection), ""));
		}
		return pairs;
	}

	private List<Page> getPages(int mode) {
		List<Page> pages = new ArrayList<Page>();
		List<Card> cards;
		List<String> data;
		int[] lists = new int[0];
		String[] pageTitles = new String[0];
		Resources res = getResources();
		int[] denLists = {
				R.array.den_breakfast,
				R.array.den_subs,
				R.array.den_sandwiches,
				R.array.den_wraps,
				R.array.den_fryer,
				R.array.den_pizza
		};
		int[] cGLists = {
				R.array.drinks,
				R.array.food,
				R.array.combos_snacks,
				R.array.smoothies
		};
		int[] hoursLists = {
				R.array.caf_times,
				R.array.den_times,
				R.array.lib_times,
				R.array.fit_times,
				R.array.post_times,
				R.array.cg_times,
				R.array.business_times,
				R.array.gameroom_times
		};
		switch (mode) {
			case 1:
				pageTitles = res.getStringArray(R.array.den_list);
				lists = denLists;
				break;
			case 2:
				pageTitles = res.getStringArray(R.array.common_g_list);
				lists = cGLists;
				break;
			case 3:
				pageTitles = res.getStringArray(R.array.place_list);
				lists = hoursLists;
				break;
		}
		for (int page = 0; page < pageTitles.length; page++) { // Fills out per page
			cards = new ArrayList<Card>();
			data = Arrays.asList(res.getStringArray(lists[page])); // Get the list for the page
			String title;
			for (int itemCount = 0; itemCount < data.size(); itemCount++) { // Get each item
				List<Pair> items = new ArrayList<Pair>();
				if (data.get(itemCount).contains("*")) { // This is to find a "title"
					String subTitle = "";
					int i = 0;
					if (data.get(itemCount).contains("-#")) { // Subtitle
						while (data.get(itemCount).charAt(i) != '#') { // Find out where the '#' is
							i++;
						}
						title = data.get(itemCount).substring(1, i - 1);
						subTitle = data.get(itemCount).substring(i + 1, data.get(itemCount).length());
					}
					else {
						title = data.get(itemCount).substring(1, data.get(itemCount).length());
					}
					int offSet = 1;
					String item = data.get(itemCount + offSet);
					// While none of the other items have a '*', add them to the data
					while (itemCount + offSet + 1 != data.size() && !item.contains("*")) {
						if (item.contains("-$")) { // Price
							i = 0;
							while (item.charAt(i) != '$') { // Find out where the '$' is
								i++;
							}
							items.add(new Pair(item.substring(0, i - 1), item.substring(i + 1, item.length())));
						}
						else {
							items.add(new Pair(item, ""));
						}
						offSet++;
						item = data.get(itemCount + offSet);
					}
					if (itemCount + offSet < data.size()) {
						itemCount += offSet;
					}
					cards.add(new Card(title, subTitle, items));
				}
			}
			pages.add(new Page(pageTitles[page], cards));
		}
		return pages;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		mode = position;
		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		if (position == 0) {
			refreshVisible = true;
			loadCaf();
		}
		else refreshVisible = false;
		populatePager(mode);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@SuppressLint("NewApi")
	private void populatePager(int mode) {
		pages = getItems(mode);
		adapter = new MyPagerAdapter(mContext, pages);
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);

		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		if (day == 1) day = 6;
		else day = day - 2;

		if (mode == 0)
			pager.setCurrentItem(day);

		pager.setOffscreenPageLimit(10);
		pager.setVisibility(View.VISIBLE);

		AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(1000);
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

}
