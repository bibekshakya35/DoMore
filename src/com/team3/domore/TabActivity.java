package com.team3.domore;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

public class TabActivity extends FragmentActivity {
	TabHost mTabHost;
	TabManager mTabManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tabs);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		
		mTabManager = new TabManager(this, mTabHost, android.R.id.tabcontent);

		// Tab for alarms and nearby places
		mTabManager.addTab(
				mTabHost.newTabSpec("alarms").setIndicator("Alarms"),
				AlarmFrag.class, null);
		mTabManager.addTab(
				mTabHost.newTabSpec("nearby").setIndicator("Nearby"),
				NearbyList.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
		
		// Create a check to see if this is the first time running the app
		SharedPreferences prefs = getSharedPreferences("FirstLaunch", MODE_PRIVATE);
	    PackageInfo pInfo;
	    try {
	        pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
	        if (prefs.getLong("lastRunVersionCode", 0) < pInfo.versionCode) {
	        	
	        	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
	    		alertDialog.setTitle("First Launch");
	    		alertDialog.setMessage("It appears this is your first time using Eat More. Would you like to see how to use this app?");
	            
	    		// On pressing "Yeah!" button
	    		alertDialog.setPositiveButton("Yeah!",
	    				new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				// Go to the about activity
	    				Intent intent = new Intent(TabActivity.this, About.class);
	    				startActivity(intent);
	    			}
	    		});

	    		// On pressing "No Thanks" button
	    		alertDialog.setNegativeButton("No Thanks",
	    				new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				// Dismiss the dialog
	    				dialog.cancel();
	    			}
	    		});
	    		
	    		alertDialog.show();
	    		SharedPreferences.Editor editor = prefs.edit();
	            editor.putLong("lastRunVersionCode", pInfo.versionCode);
	            editor.commit();
	        }
	    } catch (PackageManager.NameNotFoundException e) {
	        Log.e("Version Check", "Error reading versionCode");
	        e.printStackTrace();
	    }
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	public static class TabManager implements TabHost.OnTabChangeListener {
		private final FragmentActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		TabInfo mLastTab;

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabManager(FragmentActivity activity, TabHost tabHost,
				int containerId) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerId = containerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);

			info.fragment = mActivity.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			if (mLastTab != newTab) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				if (mLastTab != null) {
					if (mLastTab.fragment != null) {
						ft.detach(mLastTab.fragment);
					}
				}
				if (newTab != null) {
					if (newTab.fragment == null) {
						newTab.fragment = Fragment.instantiate(mActivity,
								newTab.clss.getName(), newTab.args);
						ft.add(mContainerId, newTab.fragment, newTab.tag);
					} else {
						ft.attach(newTab.fragment);
					}
				}

				mLastTab = newTab;
				ft.commit();
				mActivity.getSupportFragmentManager()
						.executePendingTransactions();
			}
		}
	}
}
