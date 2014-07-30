package navigation;

import java.util.ArrayList;

import activities.AboutActivity;
import activities.ChangeLocationActivity;
import activities.FeedbackActivity;
import activities.LoginActivity;
import activities.LogoutActivity;
import activities.MainActivity;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseUser;
import com.thebarapp.R;

public class NavDrawer extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private String callingClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		callingClass = this.getClass().getSimpleName();
		if (callingClass.equalsIgnoreCase("MapActivity")) {
			setContentView(R.layout.activity_map);
		}

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		if (callingClass.equalsIgnoreCase("DetailsActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_tabs);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_tabs);
		} else if (callingClass.equalsIgnoreCase("MainActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		} else if (callingClass.equalsIgnoreCase("DealSearchActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_deal_search);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_deal_search);
		} else if (callingClass.equalsIgnoreCase("LoginActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_login);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_login);
		} else if (callingClass.equalsIgnoreCase("DealAddActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_deal_add);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_deal_add);
		} else if (callingClass.equalsIgnoreCase("DealDetailsActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_deal);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_deal);
		} else if (callingClass.equalsIgnoreCase("MapActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_map);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_map);
		} else if (callingClass.equalsIgnoreCase("DealActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_deal_list);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_deal_list);
		} else if (callingClass.equalsIgnoreCase("ListSearchActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_list_search);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_list_search);
		} else if (callingClass.equalsIgnoreCase("ListActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_list);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_list);
		} else if (callingClass.equalsIgnoreCase("LogoutActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_logout);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_logout);
		} else if (callingClass.equalsIgnoreCase("MapSearchActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_map_search);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_map_search);
		} else if (callingClass.equalsIgnoreCase("RandomSearchActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_random_search);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_random_search);
		} else if (callingClass.equalsIgnoreCase("FeedbackActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_feedback);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_feedback);
		} else if (callingClass.equalsIgnoreCase("AboutActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_about);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_about);
		} else if (callingClass.equalsIgnoreCase("ChangeLocationActivity")) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_change_location);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu_change_location);
		}

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, // nav
																								// menu
																								// toggle
																								// icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		switch (position) {
		case 0:
			if (callingClass.equalsIgnoreCase("MainActivity")) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			} else {
				Intent home = new Intent(this, MainActivity.class);
				startActivity(home);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			}
		case 1:
			if (callingClass.equalsIgnoreCase("LogoutActivity")) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			} else {
				if (ParseUser.getCurrentUser().getCreatedAt() == null) {
					Intent userLogin = new Intent(this, LoginActivity.class);
					startActivity(userLogin);
				} else {
					Intent userLogout = new Intent(this, LogoutActivity.class);
					startActivity(userLogout);
				}
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			}
		case 2:
			if (callingClass.equalsIgnoreCase("ChangeLocationActivity")) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			} else {
				Intent changeLocation = new Intent(this, ChangeLocationActivity.class);
				startActivity(changeLocation);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			}
		case 3:
			if (callingClass.equalsIgnoreCase("FeedbackActivity")) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			} else {
				Intent feedback = new Intent(this, FeedbackActivity.class);
				startActivity(feedback);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			}
		case 4:
			if (callingClass.equalsIgnoreCase("AboutActivity")) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			} else {
				Intent about = new Intent(this, AboutActivity.class);
				startActivity(about);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				break;
			}
		default:
			break;
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		}
		super.onBackPressed();
	}
}
