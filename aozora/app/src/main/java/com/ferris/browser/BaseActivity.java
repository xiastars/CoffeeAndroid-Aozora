package com.ferris.browser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.ferris.browser.constant.Constants;
import com.ferris.browser.constant.HistoryPage;
import com.ferris.browser.controller.BrowserController;
import com.ferris.browser.database.HistoryDatabase;
import com.ferris.browser.database.HistoryItem;
import com.ferris.browser.object.ClickHandler;
import com.ferris.browser.object.SearchAdapter;
import com.ferris.browser.preference.PreferenceManager;
import com.ferris.browser.utils.Utils;
import com.ferris.browser.webview.view.AnimatedProgressBar;
import com.ferris.browser.webview.view.FastView;
import com.summer.app.wuteai.utils.SUtils;
import com.summer.asozora.livedoor.R;
import com.summer.asozora.livedoor.UIApplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.provider.Browser;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebIconDatabase;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewDatabase;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import info.guardianproject.onionkit.ui.OrbotHelper;
import info.guardianproject.onionkit.web.WebkitProxy;
import net.i2p.android.ui.I2PAndroidHelper;

public class BaseActivity extends FragmentActivity implements
		BrowserController, OnClickListener {

	// Layout

	private FrameLayout mBrowserFrame;
	private FullscreenHolder mFullscreenContainer;

	// List
	private final List<FastView> mWebViews = new ArrayList<FastView>();

	private FastView mCurrentView;

	private AnimatedProgressBar mProgressBar;

	private VideoView mVideoView;
	private View mCustomView, mVideoProgressView;

	private SearchAdapter mSearchAdapter;

	// Callback
	private ClickHandler mClickHandler;
	private CustomViewCallback mCustomViewCallback;
	private ValueCallback<Uri> mUploadMessage;
	private ValueCallback<Uri[]> mFilePathCallback;

	// Context
	private Activity mActivity;

	// Native
	private boolean mSystemBrowser = false, mIsNewIntent = false, mColorMode,
			mDarkTheme;
	private int mOriginalOrientation, mIdGenerator;
	private String mSearchText, mUntitledTitle, mHomepage, mCameraPhotoPath;

	// Storage
	private HistoryDatabase mHistoryDatabase;

	private PreferenceManager mPreferences;

	// Image
	private Bitmap mDefaultVideoPoster, mWebpageBitmap;

	private Drawable mDeleteIcon, mRefreshIcon, mCopyIcon, mIcon;

	// Helper
	private I2PAndroidHelper mI2PHelper;
	private boolean mI2PHelperBound;
	private boolean mI2PProxyInitialized;

	// Constant
	private static final int API = android.os.Build.VERSION.SDK_INT;
	private static final LayoutParams MATCH_PARENT = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private synchronized void initialize() {
		setContentView(R.layout.activity_web);
		RelativeLayout rlParent = (RelativeLayout) findViewById(R.id.rl_parent);
		FrameLayout.LayoutParams clp = ( FrameLayout.LayoutParams) rlParent.getLayoutParams();
		if(VERSION.SDK_INT >= 19){			  
		    clp.topMargin = SUtils.getStatusBarHeight(this);
		}

		mPreferences = PreferenceManager.getInstance();
		mDarkTheme = mPreferences.getUseDarkTheme() || isIncognito();
		mActivity = this;
		mWebViews.clear();

		mClickHandler = new ClickHandler(this);
		mBrowserFrame = (FrameLayout) findViewById(R.id.container);
		mProgressBar = (AnimatedProgressBar) findViewById(R.id.progress_view);

		mWebpageBitmap = Utils.getWebpageBitmap(getResources(), mDarkTheme);

		mHomepage = mPreferences.getHomepage();

		mHistoryDatabase = HistoryDatabase.getInstance(getApplicationContext());

		mI2PHelper = new I2PAndroidHelper(this);

		mUntitledTitle = getString(R.string.untitled);

		mDeleteIcon = getResources().getDrawable(
				R.drawable.ic_action_delete);
		mRefreshIcon = getResources().getDrawable(
				R.drawable.ic_action_refresh);
		mCopyIcon = getResources().getDrawable(R.drawable.ic_action_copy);

		int iconBounds = Utils.convertDpToPixels(24);
		mDeleteIcon.setBounds(0, 0, iconBounds, iconBounds);
		mRefreshIcon.setBounds(0, 0, iconBounds, iconBounds);
		mCopyIcon.setBounds(0, 0, iconBounds, iconBounds);
		mIcon = mRefreshIcon;

		mSystemBrowser = getSystemBrowser();

		initializeTabs();

		if (API <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			WebIconDatabase.getInstance().open(
					getDir("icons", MODE_PRIVATE).getPath());
		}

		checkForProxy();
	}

	/*
	 * If Orbot/Tor or I2P is installed, prompt the user if they want to enable
	 * proxying for this session
	 */
	private void checkForProxy() {
		boolean useProxy = mPreferences.getUseProxy();

		OrbotHelper oh = new OrbotHelper(this);
		final boolean orbotInstalled = oh.isOrbotInstalled();
		boolean orbotChecked = mPreferences.getCheckedForTor();
		boolean orbot = orbotInstalled && !orbotChecked;

		boolean i2pInstalled = mI2PHelper.isI2PAndroidInstalled();
		boolean i2pChecked = mPreferences.getCheckedForI2P();
		boolean i2p = i2pInstalled && !i2pChecked;

		// TODO Is the idea to show this per-session, or only once?
		if (!useProxy && (orbot || i2p)) {
			if (orbot)
				mPreferences.setCheckedForTor(true);
			if (i2p)
				mPreferences.setCheckedForI2P(true);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			if (orbotInstalled && i2pInstalled) {
				String[] proxyChoices = this.getResources().getStringArray(
						R.array.proxy_choices_array);
				builder.setTitle(getResources().getString(R.string.http_proxy))
						.setSingleChoiceItems(proxyChoices,
								mPreferences.getProxyChoice(),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mPreferences.setProxyChoice(which);
									}
								})
						.setNeutralButton(
								getResources().getString(R.string.action_ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (mPreferences.getUseProxy())
											initializeProxy();
									}
								});
			} else {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							mPreferences
									.setProxyChoice(orbotInstalled ? Constants.PROXY_ORBOT
											: Constants.PROXY_I2P);
							initializeProxy();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							mPreferences.setProxyChoice(Constants.NO_PROXY);
							break;
						}
					}
				};

				builder.setMessage(
						orbotInstalled ? R.string.use_tor_prompt
								: R.string.use_i2p_prompt)
						.setPositiveButton(R.string.yes, dialogClickListener)
						.setNegativeButton(R.string.no, dialogClickListener);
			}
			builder.show();
		}
	}

	/*
	 * Initialize WebKit Proxying
	 */
	private void initializeProxy() {
		String host;
		int port;

		switch (mPreferences.getProxyChoice()) {
		case Constants.NO_PROXY:
			// We shouldn't be here
			return;

		case Constants.PROXY_ORBOT:
			OrbotHelper oh = new OrbotHelper(this);
			if (!oh.isOrbotRunning()) {
				oh.requestOrbotStart(this);
			}
			host = "localhost";
			port = 8118;
			break;

		case Constants.PROXY_I2P:
			mI2PProxyInitialized = true;
			if (mI2PHelperBound && !mI2PHelper.isI2PAndroidRunning()) {
				mI2PHelper.requestI2PAndroidStart(this);
			}
			host = "localhost";
			port = 4444;
			break;

		default:
			host = mPreferences.getProxyHost();
			port = mPreferences.getProxyPort();
		}

		try {
			WebkitProxy.setProxy(UIApplication.class.getName(),
					getApplicationContext(), host, port);
		} catch (Exception e) {
			Log.d(Constants.TAG, "error enabling web proxying", e);
		}

	}

	public boolean isProxyReady() {
		if (mPreferences.getProxyChoice() == Constants.PROXY_I2P) {
			if (!mI2PHelper.isI2PAndroidRunning()) {
				Utils.showToast(this, getString(R.string.i2p_not_running));
				return false;
			} else if (!mI2PHelper.areTunnelsActive()) {
				
				return false;
			}
		}

		return true;
	}

	private boolean isTablet() {
		return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/*
	 * Override this class
	 */
	public synchronized void initializeTabs() {
	}

	public void restoreOrNewTab(String url) {
		mIdGenerator = 0;
		newTab(url, true);
//		if (getIntent() != null) {
//			url = getIntent().getDataString();
//			if (url != null) {
//				if (url.startsWith(Constants.FILE)) {
//					Utils.showToast(
//							this,
//							getResources().getString(
//									R.string.message_blocked_local));
//					url = null;
//				}
//			}
//		}
//		if (mPreferences.getRestoreLostTabsEnabled()) {
//			String mem = mPreferences.getMemoryUrl();
//			mPreferences.setMemoryUrl("");
//			String[] array = Utils.getArray(mem);
//			int count = 0;
//			for (String urlString : array) {
//				if (urlString.length() > 0) {
//					if (url != null && url.compareTo(urlString) == 0) {
//						url = null;
//					}
//					newTab(urlString, true);
//					count++;
//				}
//			}
//			if (url != null) {
//				newTab(url, true);
//			} else if (count == 0) {
//				newTab(null, true);
//			}
//		} else {
//			newTab(url, true);
//		}
	}

	public void initializePreferences() {
		if (mPreferences == null) {
			mPreferences = PreferenceManager.getInstance();
		}

		mColorMode = mPreferences.getColorModeEnabled();
		mColorMode &= !mDarkTheme;

		if (mPreferences.getHideStatusBarEnabled()) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		switch (mPreferences.getSearchChoice()) {
		case 0:
			mSearchText = mPreferences.getSearchUrl();
			if (!mSearchText.startsWith(Constants.HTTP)
					&& !mSearchText.startsWith(Constants.HTTPS)) {
				mSearchText = Constants.GOOGLE_SEARCH;
			}
			break;
		case 1:
			mSearchText = Constants.GOOGLE_SEARCH;
			break;
		case 2:
			mSearchText = Constants.ASK_SEARCH;
			break;
		case 3:
			mSearchText = Constants.BING_SEARCH;
			break;
		case 4:
			mSearchText = Constants.YAHOO_SEARCH;
			break;
		case 5:
			mSearchText = Constants.STARTPAGE_SEARCH;
			break;
		case 6:
			mSearchText = Constants.STARTPAGE_MOBILE_SEARCH;
			break;
		case 7:
			mSearchText = Constants.DUCK_SEARCH;
			break;
		case 8:
			mSearchText = Constants.DUCK_LITE_SEARCH;
			break;
		case 9:
			mSearchText = Constants.BAIDU_SEARCH;
			break;
		case 10:
			mSearchText = Constants.YANDEX_SEARCH;
			break;
		}

		updateCookiePreference();
		if (mPreferences.getUseProxy()) {
			initializeProxy();
		} else {
			try {
				WebkitProxy.resetProxy(UIApplication.class.getName(),
						getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			mI2PProxyInitialized = false;
		}
	}

	/*
	 * Override this if class overrides BrowserActivity
	 */
	public void updateCookiePreference() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {

		} else if ((keyCode == KeyEvent.KEYCODE_MENU)
				&& (Build.VERSION.SDK_INT <= 16)
				&& (Build.MANUFACTURER.compareTo("LGE") == 0)) {
			// Workaround for stupid LG devices that crash
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_MENU) && (Build.VERSION.SDK_INT <= 16)
				&& (Build.MANUFACTURER.compareTo("LGE") == 0)) {
			// Workaround for stupid LG devices that crash
			openOptionsMenu();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * method that shows a dialog asking what string the user wishes to search
	 * for. It highlights the text entered.
	 */
	private void findInPage() {
		final AlertDialog.Builder finder = new AlertDialog.Builder(mActivity);
		finder.setTitle(getResources().getString(R.string.action_find));
		final EditText getHome = new EditText(this);
		getHome.setHint(getResources().getString(R.string.search_hint));
		finder.setView(getHome);
		finder.setPositiveButton(
				getResources().getString(R.string.search_hint),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String query = getHome.getText().toString();
						if (query.length() > 0) {

						}

					}
				});
		finder.show();
	}

	private void showCloseDialog(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
				android.R.layout.simple_dropdown_item_1line);
		adapter.add(mActivity.getString(R.string.close_tab));
		adapter.add(mActivity.getString(R.string.close_all_tabs));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// deleteTab(position);
					break;
				case 1:
					// closeBrowser();
					break;
				default:
					break;
				}
			}
		});
		builder.show();
	}



	/**
	 * displays the WebView contained in the LightningView Also handles the
	 * removal of previous views
	 * 
	 * @param view
	 *            the LightningView to show
	 */
	private synchronized void showTab(FastView view) {
		// Set the background color so the color mode color doesn't show through

		if (view == null) {
			return;
		}
		mBrowserFrame.removeAllViews();
		if (mCurrentView != null) {
			mCurrentView.setForegroundTab(false);
			mCurrentView.onPause();
		}
		mCurrentView = view;
		mCurrentView.setForegroundTab(true);
		if (mCurrentView.getWebView() != null) {
			updateUrl(mCurrentView.getUrl(), true);
			updateProgress(mCurrentView.getProgress());
		} else {
			updateUrl("", true);
			updateProgress(0);
		}

		mBrowserFrame.addView(mCurrentView.getWebView(), MATCH_PARENT);
		// Remove browser frame background to reduce overdraw
		mBrowserFrame.setBackgroundColor(0);
		mCurrentView.requestFocus();
		mCurrentView.onResume();

	}

	/**
	 * creates a new tab with the passed in URL if it isn't null
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	public void handleNewIntent(Intent intent) {

		String url = null;
		if (intent != null) {
			url = intent.getDataString();
		}
		int num = 0;
		if (intent != null && intent.getExtras() != null) {
			num = intent.getExtras().getInt(getPackageName() + ".Origin");
		}
		if (num == 1) {
			mCurrentView.loadUrl(url);
		} else if (url != null) {
			if (url.startsWith(Constants.FILE)) {
				Utils.showToast(this,
						getResources()
								.getString(R.string.message_blocked_local));
				url = null;
			}
			newTab(url, true);
			mIsNewIntent = true;
		}
	}

	@Override
	public void closeEmptyTab() {
		if (mCurrentView != null
				&& mCurrentView.getWebView().copyBackForwardList().getSize() == 0) {
			closeCurrentTab();
		}
	}

	private void closeCurrentTab() {
		// don't delete the tab because the browser will close and mess stuff up
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTrimMemory(int level) {
		if (level > TRIM_MEMORY_MODERATE
				&& Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			Log.d(Constants.TAG, "Low Memory, Free Memory");
			for (FastView view : mWebViews) {
				view.getWebView().freeMemory();
			}
		}
	}

	protected synchronized boolean newTab(String url, boolean show) {
		// Limit number of tabs for limited version of app
		if (!Constants.FULL_VERSION && mWebViews.size() >= 10) {
			Utils.showToast(this, this.getString(R.string.max_tabs));
			return false;
		}
		mIsNewIntent = false;
		FastView startingTab = new FastView(mActivity, url, mDarkTheme);
		if (mIdGenerator == 0) {
			startingTab.resumeTimers();
		}
		mIdGenerator++;
		mWebViews.add(startingTab);

		if (show) {
			showTab(startingTab);
		}
		return true;
	}

	// private synchronized void deleteTab(int position) {
	// if (position >= mWebViews.size()) {
	// return;
	// }
	//
	// int current = mDrawerListLeft.getCheckedItemPosition();
	// FastView reference = mWebViews.get(position);
	// if (reference == null) {
	// return;
	// }
	// if (reference.getUrl() != null &&
	// !reference.getUrl().startsWith(Constants.FILE)
	// && !isIncognito()) {
	// mPreferences.setSavedUrl(reference.getUrl());
	// }
	// boolean isShown = reference.isShown();
	// if (isShown) {
	// mBrowserFrame.setBackgroundColor(mBackgroundColor);
	// }
	// if (current > position) {
	// mWebViews.remove(position);
	// mDrawerListLeft.setItemChecked(current - 1, true);
	// reference.onDestroy();
	// } else if (mWebViews.size() > position + 1) {
	// if (current == position) {
	// showTab(mWebViews.get(position + 1));
	// mWebViews.remove(position);
	// mDrawerListLeft.setItemChecked(position, true);
	// } else {
	// mWebViews.remove(position);
	// }
	//
	// reference.onDestroy();
	// } else if (mWebViews.size() > 1) {
	// if (current == position) {
	// showTab(mWebViews.get(position - 1));
	// mWebViews.remove(position);
	// mDrawerListLeft.setItemChecked(position - 1, true);
	// } else {
	// mWebViews.remove(position);
	// }
	//
	// reference.onDestroy();
	// } else {
	// if (mCurrentView.getUrl() == null ||
	// mCurrentView.getUrl().startsWith(Constants.FILE)
	// || mCurrentView.getUrl().equals(mHomepage)) {
	// closeActivity();
	// } else {
	// mWebViews.remove(position);
	// if (mPreferences.getClearCacheExit() && mCurrentView != null &&
	// !isIncognito()) {
	// mCurrentView.clearCache(true);
	// Log.d(Constants.TAG, "Cache Cleared");
	//
	// }
	// if (mPreferences.getClearHistoryExitEnabled() && !isIncognito()) {
	// clearHistory();
	// Log.d(Constants.TAG, "History Cleared");
	//
	// }
	// if (mPreferences.getClearCookiesExitEnabled() && !isIncognito()) {
	// clearCookies();
	// Log.d(Constants.TAG, "Cookies Cleared");
	//
	// }
	// reference.pauseTimers();
	// reference.onDestroy();
	// mCurrentView = null;
	// mTitleAdapter.notifyDataSetChanged();
	// finish();
	//
	// }
	// }
	// mTitleAdapter.notifyDataSetChanged();
	//
	// if (mIsNewIntent && isShown) {
	// mIsNewIntent = false;
	// closeActivity();
	// }
	//
	// Log.d(Constants.TAG, "deleted tab");
	// }

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return true;
	}
	
	private void navigatePrevious() {
		if (mCurrentView.canGoBack()) {
			mCurrentView.goBack();
		} else {
			finish();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			navigatePrevious();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	private void closeBrowser() {

		if (mPreferences.getClearCacheExit() && mCurrentView != null
				&& !isIncognito()) {
			mCurrentView.clearCache(true);
			Log.d(Constants.TAG, "Cache Cleared");

		}
		if (mPreferences.getClearHistoryExitEnabled() && !isIncognito()) {
			clearHistory();
			Log.d(Constants.TAG, "History Cleared");

		}
		if (mPreferences.getClearCookiesExitEnabled() && !isIncognito()) {
			clearCookies();
			Log.d(Constants.TAG, "Cookies Cleared");

		}
		mCurrentView = null;
		for (int n = 0; n < mWebViews.size(); n++) {
			if (mWebViews.get(n) != null) {
				mWebViews.get(n).onDestroy();
			}
		}
		mWebViews.clear();

		finish();
	}

	@SuppressWarnings("deprecation")
	public void clearHistory() {
		this.deleteDatabase(HistoryDatabase.DATABASE_NAME);
		WebViewDatabase m = WebViewDatabase.getInstance(this);
		m.clearFormData();
		m.clearHttpAuthUsernamePassword();
		if (API < 18) {
			m.clearUsernamePassword();
			WebIconDatabase.getInstance().removeAllIcons();
		}
		if (mSystemBrowser) {
			try {
				Browser.clearHistory(getContentResolver());
			} catch (NullPointerException ignored) {
			}
		}
		Utils.trimCache(this);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void clearCookies() {
		// TODO Break out web storage deletion into its own option/action
		// TODO clear web storage for all sites that are visited in Incognito
		// mode
		WebStorage storage = WebStorage.getInstance();
		storage.deleteAllData();
		CookieManager c = CookieManager.getInstance();
		CookieSyncManager.createInstance(this);
		c.removeAllCookie();
	}

	@Override
	public void onBackPressed() {

		// if (mCurrentView != null) {
		// Log.d(Constants.TAG, "onBackPressed");
		// if (mSearch.hasFocus()) {
		// mCurrentView.requestFocus();
		// } else if (mCurrentView.canGoBack()) {
		// if (!mCurrentView.isShown()) {
		// onHideCustomView();
		// } else {
		// mCurrentView.goBack();
		// }
		// } else {
		// deleteTab(mDrawerListLeft.getCheckedItemPosition());
		// }
		// } else {
		// Log.e(Constants.TAG, "This shouldn't happen ever");
		// super.onBackPressed();
		// }

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(Constants.TAG, "onPause");
		if (mCurrentView != null) {
			mCurrentView.pauseTimers();
			mCurrentView.onPause();
		}
	}

	public void saveOpenTabs() {
		if (mPreferences.getRestoreLostTabsEnabled()) {
			String s = "";
			for (int n = 0; n < mWebViews.size(); n++) {
				if (mWebViews.get(n).getUrl() != null) {
					s = s + mWebViews.get(n).getUrl() + "|$|SEPARATOR|$|";
				}
			}
			mPreferences.setMemoryUrl(s);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mI2PHelper.unbind();
		mI2PHelperBound = false;
	}

	@Override
	protected void onDestroy() {
		Log.d(Constants.TAG, "onDestroy");
		if (mHistoryDatabase != null) {
			mHistoryDatabase.close();
		}
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mPreferences.getProxyChoice() == Constants.PROXY_I2P) {
			// Try to bind to I2P Android
			mI2PHelper.bind(new I2PAndroidHelper.Callback() {
				@Override
				public void onI2PAndroidBound() {
					mI2PHelperBound = true;
					if (mI2PProxyInitialized
							&& !mI2PHelper.isI2PAndroidRunning())
						mI2PHelper.requestI2PAndroidStart(BaseActivity.this);
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(Constants.TAG, "onResume");
		if (mSearchAdapter != null) {
			mSearchAdapter.refreshPreferences();
			mSearchAdapter.refreshBookmarks();
		}
		if (mCurrentView != null) {
			mCurrentView.resumeTimers();
			mCurrentView.onResume();

			mHistoryDatabase = HistoryDatabase
					.getInstance(getApplicationContext());
		

		}
		initializePreferences();
		if (mWebViews != null) {
			for (int n = 0; n < mWebViews.size(); n++) {
				if (mWebViews.get(n) != null) {
					mWebViews.get(n).initializePreferences(this);
				} else {
					mWebViews.remove(n);
				}
			}
		}

		supportInvalidateOptionsMenu();
	}

	/**
	 * searches the web for the query fixing any and all problems with the input
	 * checks if it is a search, url, etc.
	 */
	void searchTheWeb(String query) {
		if (query.equals("")) {
			return;
		}
		String SEARCH = mSearchText;
		query = query.trim();
		mCurrentView.stopLoading();

		if (query.startsWith("www.")) {
			query = Constants.HTTP + query;
		} else if (query.startsWith("ftp.")) {
			query = "ftp://" + query;
		}

		boolean containsPeriod = query.contains(".");
		boolean isIPAddress = (TextUtils.isDigitsOnly(query.replace(".", ""))
				&& (query.replace(".", "").length() >= 4) && query
				.contains("."));
		boolean aboutScheme = query.contains("about:");
		boolean validURL = (query.startsWith("ftp://")
				|| query.startsWith(Constants.HTTP)
				|| query.startsWith(Constants.FILE) || query
					.startsWith(Constants.HTTPS)) || isIPAddress;
		boolean isSearch = ((query.contains(" ") || !containsPeriod) && !aboutScheme);

		if (isIPAddress
				&& (!query.startsWith(Constants.HTTP) || !query
						.startsWith(Constants.HTTPS))) {
			query = Constants.HTTP + query;
		}

		if (isSearch) {
			try {
				query = URLEncoder.encode(query, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			mCurrentView.loadUrl(SEARCH + query);
		} else if (!validURL) {
			mCurrentView.loadUrl(Constants.HTTP + query);
		} else {
			mCurrentView.loadUrl(query);
		}
	}

	public static boolean isColorTooDark(int color) {
		final byte RED_CHANNEL = 16;
		final byte GREEN_CHANNEL = 8;
		// final byte BLUE_CHANNEL = 0;

		int r = ((int) ((float) (color >> RED_CHANNEL & 0xff) * 0.3f)) & 0xff;
		int g = ((int) ((float) (color >> GREEN_CHANNEL & 0xff) * 0.59)) & 0xff;
		int b = ((int) ((float) (color & 0xff) * 0.11)) & 0xff;
		int gr = (r + g + b) & 0xff;
		int gray = gr + (gr << GREEN_CHANNEL) + (gr << RED_CHANNEL);

		return gray < 0x727272;
	}

	public static int mixTwoColors(int color1, int color2, float amount) {
		final byte ALPHA_CHANNEL = 24;
		final byte RED_CHANNEL = 16;
		final byte GREEN_CHANNEL = 8;
		// final byte BLUE_CHANNEL = 0;

		final float inverseAmount = 1.0f - amount;

		int r = ((int) (((float) (color1 >> RED_CHANNEL & 0xff) * amount) + ((float) (color2 >> RED_CHANNEL & 0xff) * inverseAmount))) & 0xff;
		int g = ((int) (((float) (color1 >> GREEN_CHANNEL & 0xff) * amount) + ((float) (color2 >> GREEN_CHANNEL & 0xff) * inverseAmount))) & 0xff;
		int b = ((int) (((float) (color1 & 0xff) * amount) + ((float) (color2 & 0xff) * inverseAmount))) & 0xff;

		return 0xff << ALPHA_CHANNEL | r << RED_CHANNEL | g << GREEN_CHANNEL
				| b;
	}

	private void getImage(ImageView image, HistoryItem web) {
		new DownloadImageTask(image, web).execute(web.getUrl());
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		final ImageView bmImage;
		final HistoryItem mWeb;

		public DownloadImageTask(ImageView bmImage, HistoryItem web) {
			this.bmImage = bmImage;
			this.mWeb = web;
		}

		protected Bitmap doInBackground(String... urls) {
			String url = urls[0];
			Bitmap mIcon = null;
			// unique path for each url that is bookmarked.
			String hash = String.valueOf(Utils.getDomainName(url).hashCode());
			File image = new File(mActivity.getCacheDir(), hash + ".png");
			String urldisplay;
			try {
				urldisplay = Utils.getProtocol(url) + getDomainName(url)
						+ "/favicon.ico";
			} catch (URISyntaxException e) {
				e.printStackTrace();
				urldisplay = "https://www.google.com/s2/favicons?domain_url="
						+ url;
			}
			// checks to see if the image exists
			if (!image.exists()) {
				try {
					// if not, download it...
					URL urlDownload = new URL(urldisplay);
					HttpURLConnection connection = (HttpURLConnection) urlDownload
							.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream in = connection.getInputStream();

					if (in != null) {
						mIcon = BitmapFactory.decodeStream(in);
					}
					// ...and cache it
					if (mIcon != null) {
						FileOutputStream fos = new FileOutputStream(image);
						mIcon.compress(Bitmap.CompressFormat.PNG, 100, fos);
						fos.flush();
						fos.close();
						Log.d(Constants.TAG, "Downloaded: " + urldisplay);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// if it exists, retrieve it from the cache
				mIcon = BitmapFactory.decodeFile(image.getPath());
			}
			if (mIcon == null) {
				try {
					// if not, download it...
					URL urlDownload = new URL(
							"https://www.google.com/s2/favicons?domain_url="
									+ url);
					HttpURLConnection connection = (HttpURLConnection) urlDownload
							.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream in = connection.getInputStream();

					if (in != null) {
						mIcon = BitmapFactory.decodeStream(in);
					}
					// ...and cache it
					if (mIcon != null) {
						FileOutputStream fos = new FileOutputStream(image);
						mIcon.compress(Bitmap.CompressFormat.PNG, 100, fos);
						fos.flush();
						fos.close();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (mIcon == null) {
				return mWebpageBitmap;
			} else {
				return mIcon;
			}
		}

		protected void onPostExecute(Bitmap result) {
			Bitmap fav = Utils.padFavicon(result);
			bmImage.setImageBitmap(fav);
			mWeb.setBitmap(fav);

		}
	}

	static String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		if (domain == null) {
			return url;
		}
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	@Override
	public void updateUrl(String url, boolean shortUrl) {
		if (url == null) {
			return;
		}
		if (shortUrl && !url.startsWith(Constants.FILE)) {
			switch (mPreferences.getUrlBoxContentChoice()) {
			case 0: // Default, show only the domain
				url = url.replaceFirst(Constants.HTTP, "");
				url = Utils.getDomainName(url);
				// mSearch.setText(url);
				break;
			case 1: // URL, show the entire URL
				// mSearch.setText(url);
				break;
			case 2: // Title, show the page's title
				if (mCurrentView != null && !mCurrentView.getTitle().isEmpty()) {
					// mSearch.setText(mCurrentView.getTitle());
				} else {
					// mSearch.setText(mUntitledTitle);
				}
				break;
			}

		} else {
			if (url.startsWith(Constants.FILE)) {
				url = "";
			}
			// mSearch.setText(url);
		}
	}

	@Override
	public void updateProgress(int n) {
		if (n >= 100) {
			setIsFinishedLoading();
		} else {
			setIsLoading();
		}
		mProgressBar.setProgress(n);
	}

	@Override
	public void updateHistory(final String title, final String url) {

	}

	public void addItemToHistory(final String title, final String url) {
		Runnable update = new Runnable() {
			@Override
			public void run() {
				if (isSystemBrowserAvailable()
						&& mPreferences.getSyncHistoryEnabled()) {
					try {
						Browser.updateVisitedHistory(getContentResolver(), url,
								true);
					} catch (NullPointerException ignored) {
					}
				}
				try {
					if (mHistoryDatabase == null) {
						mHistoryDatabase = HistoryDatabase
								.getInstance(mActivity);
					}
					mHistoryDatabase.visitHistoryItem(url, title);
				} catch (IllegalStateException e) {
					Log.e(Constants.TAG,
							"IllegalStateException in updateHistory");
				} catch (NullPointerException e) {
					Log.e(Constants.TAG,
							"NullPointerException in updateHistory");
				} catch (SQLiteException e) {
					Log.e(Constants.TAG, "SQLiteException in updateHistory");
				}
			}
		};
		if (url != null && !url.startsWith(Constants.FILE)) {
			new Thread(update).start();
		}
	}

	public boolean isSystemBrowserAvailable() {
		return mSystemBrowser;
	}

	public boolean getSystemBrowser() {
		Cursor c = null;
		String[] columns = new String[] { "url", "title" };
		boolean browserFlag;
		try {

			Uri bookmarks = Browser.BOOKMARKS_URI;
			c = getContentResolver()
					.query(bookmarks, columns, null, null, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}catch(IllegalStateException e){
			
		}catch(NullPointerException e){
			
		}

		if (c != null) {
			Log.d("Browser", "System Browser Available");
			browserFlag = true;
		} else {
			Log.e("Browser", "System Browser Unavailable");
			browserFlag = false;
		}
		if (c != null) {
			c.close();
		}
		mPreferences.setSystemBrowserPresent(browserFlag);
		return browserFlag;
	}

	@Override
	public boolean isIncognito() {
		return false;
	}

	/**
	 * function that opens the HTML history page in the browser
	 */
	private void openHistory() {
		// use a thread so that history retrieval doesn't block the UI
		Thread history = new Thread(new Runnable() {

			@Override
			public void run() {
				mCurrentView.loadUrl(HistoryPage.getHistoryPage(mActivity));
				// mSearch.setText("");
			}

		});
		history.run();
	}

	@Override
	/**
	 * open the HTML bookmarks page, parameter view is the WebView that should show the page
	 */
	public void openBookmarkPage(WebView view) {
	
	}

	@Override
	public void update() {

	}

	@Override
	/**
	 * opens a file chooser
	 * param ValueCallback is the message from the WebView indicating a file chooser
	 * should be opened
	 */
	public void openFileChooser(ValueCallback<Uri> uploadMsg) {
		mUploadMessage = uploadMsg;
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("*/*");
		startActivityForResult(
				Intent.createChooser(i, getString(R.string.title_file_chooser)),
				1);
	}

	@Override
	/**
	 * used to allow uploading into the browser
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 1) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;

		}

		if (requestCode != 1 || mFilePathCallback == null) {
			super.onActivityResult(requestCode, resultCode, intent);
			return;
		}

		Uri[] results = null;

		// Check that the response is a good one
		if (resultCode == Activity.RESULT_OK) {
			if (intent == null) {
				// If there is not data, then we may have taken a photo
				if (mCameraPhotoPath != null) {
					results = new Uri[] { Uri.parse(mCameraPhotoPath) };
				}
			} else {
				String dataString = intent.getDataString();
				if (dataString != null) {
					results = new Uri[] { Uri.parse(dataString) };
				}
			}
		}

		mFilePathCallback.onReceiveValue(results);
		mFilePathCallback = null;
	}

	@Override
	public void showFileChooser(ValueCallback<Uri[]> filePathCallback) {
		if (mFilePathCallback != null) {
			mFilePathCallback.onReceiveValue(null);
		}
		mFilePathCallback = filePathCallback;

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent
				.resolveActivity(getActivity().getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = Utils.createImageFile();
				takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
			} catch (IOException ex) {
				// Error occurred while creating the File
				Log.e(Constants.TAG, "Unable to create Image File", ex);
			}

			// Continue only if the File was successfully created
			if (photoFile != null) {
				mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
			} else {
				takePictureIntent = null;
			}
		}

		Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
		contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
		contentSelectionIntent.setType("image/*");

		Intent[] intentArray;
		if (takePictureIntent != null) {
			intentArray = new Intent[] { takePictureIntent };
		} else {
			intentArray = new Intent[0];
		}

		Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
		chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

		mActivity.startActivityForResult(chooserIntent, 1);
	}

	@Override
	/**
	 * handles long presses for the browser, tries to get the
	 * url of the item that was clicked and sends it (it can be null)
	 * to the click handler that does cool stuff with it
	 */
	public void onLongPress() {
		if (mClickHandler == null) {
			mClickHandler = new ClickHandler(mActivity);
		}
		Message click = mClickHandler.obtainMessage();
		if (click != null) {
			click.setTarget(mClickHandler);
			mCurrentView.getWebView().requestFocusNodeHref(click);
		}
	}

	@Override
	public void onShowCustomView(View view, int requestedOrientation,
			CustomViewCallback callback) {
		if (view == null) {
			return;
		}
		if (mCustomView != null && callback != null) {
			callback.onCustomViewHidden();
			return;
		}
		try {
			view.setKeepScreenOn(true);
		} catch (SecurityException e) {
			Log.e(Constants.TAG, "WebView is not allowed to keep the screen on");
		}
		mOriginalOrientation = getRequestedOrientation();
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		mFullscreenContainer = new FullscreenHolder(this);
		mCustomView = view;
		mFullscreenContainer.addView(mCustomView, COVER_SCREEN_PARAMS);
		decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
		setFullscreen(true);
		mCurrentView.setVisibility(View.GONE);
		if (view instanceof FrameLayout) {
			if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
				mVideoView = (VideoView) ((FrameLayout) view).getFocusedChild();
				mVideoView.setOnErrorListener(new VideoCompletionListener());
				mVideoView
						.setOnCompletionListener(new VideoCompletionListener());
			}
		}
		mCustomViewCallback = callback;
	}

	@Override
	public void onHideCustomView() {
		if (mCustomView == null || mCustomViewCallback == null
				|| mCurrentView == null) {
			return;
		}
		Log.d(Constants.TAG, "onHideCustomView");
		mCurrentView.setVisibility(View.VISIBLE);
		try {
			mCustomView.setKeepScreenOn(false);
		} catch (SecurityException e) {
			Log.e(Constants.TAG, "WebView is not allowed to keep the screen on");
		}
		setFullscreen(mPreferences.getHideStatusBarEnabled());
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		if (decor != null) {
			decor.removeView(mFullscreenContainer);
		}

		if (API < 19) {
			try {
				mCustomViewCallback.onCustomViewHidden();
			} catch (Throwable ignored) {

			}
		}
		mFullscreenContainer = null;
		mCustomView = null;
		if (mVideoView != null) {
			mVideoView.setOnErrorListener(null);
			mVideoView.setOnCompletionListener(null);
			mVideoView = null;
		}
		setRequestedOrientation(mOriginalOrientation);
	}

	private class VideoCompletionListener implements
			MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			return false;
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			onHideCustomView();
		}

	}

	/**
	 * turns on fullscreen mode in the app
	 * 
	 * @param enabled
	 *            whether to enable fullscreen or not
	 */
	public void setFullscreen(boolean enabled) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (enabled) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
			if (mCustomView != null) {
				mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			} else {
				mBrowserFrame
						.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			}
		}
		win.setAttributes(winParams);
	}

	/**
	 * a class extending FramLayout used to display fullscreen videos
	 */
	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(
					android.R.color.black));
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}

	}

	@Override
	/**
	 * a stupid method that returns the bitmap image to display in place of
	 * a loading video
	 */
	public Bitmap getDefaultVideoPoster() {
		if (mDefaultVideoPoster == null) {
			mDefaultVideoPoster = BitmapFactory.decodeResource(getResources(),
					android.R.drawable.ic_media_play);
		}
		return mDefaultVideoPoster;
	}

	@SuppressLint("InflateParams")
	@Override
	/**
	 * dumb method that returns the loading progress for a video
	 */
	public View getVideoLoadingProgressView() {
		if (mVideoProgressView == null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			mVideoProgressView = inflater.inflate(
					R.layout.video_loading_progress, null);
		}
		return mVideoProgressView;
	}

	@Override
	/**
	 * handles javascript requests to create a new window in the browser
	 */
	public void onCreateWindow(boolean isUserGesture, Message resultMsg) {
		if (resultMsg == null) {
			return;
		}
		if (newTab("http://www.baidu.com", true)) {
			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(mCurrentView.getWebView());
			resultMsg.sendToTarget();
		}
	}

	@Override
	/**
	 * returns the Activity instance for this activity,
	 * very helpful when creating things in other classes... I think
	 */
	public Activity getActivity() {
		return mActivity;
	}

	/**
	 * it hides the action bar, seriously what else were you expecting
	 */
	@Override
	public void hideActionBar() {
	}

	@Override
	public void toggleActionBar() {
	}

	@Override
	/**
	 * obviously it shows the action bar if it's hidden
	 */
	public void showActionBar() {
	}

	@Override
	/**
	 * handles a long click on the page, parameter String url
	 * is the url that should have been obtained from the WebView touch node
	 * thingy, if it is null, this method tries to deal with it and find a workaround
	 */
	public void longClickPage(final String url) {
		HitTestResult result = null;
		if (mCurrentView.getWebView() != null) {
			result = mCurrentView.getWebView().getHitTestResult();
		}
		if (url != null) {
			if (result != null) {
				if (result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE
						|| result.getType() == HitTestResult.IMAGE_TYPE) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								newTab(url, false);
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								mCurrentView.loadUrl(url);
								break;

							case DialogInterface.BUTTON_NEUTRAL:
								if (API > 8) {
									Utils.downloadFile(mActivity, url,
											mCurrentView.getUserAgent(),
											"attachment", false);
								}
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							mActivity); // dialog
					builder.setTitle(url.replace(Constants.HTTP, ""))
							.setMessage(
									getResources().getString(
											R.string.dialog_image))
							.setPositiveButton(
									getResources().getString(
											R.string.action_new_tab),
									dialogClickListener)
							.setNegativeButton(
									getResources().getString(
											R.string.action_open),
									dialogClickListener)
							.setNeutralButton(
									getResources().getString(
											R.string.action_download),
									dialogClickListener).show();

				} else {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								newTab(url, false);
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								mCurrentView.loadUrl(url);
								break;

							case DialogInterface.BUTTON_NEUTRAL:
								ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
								ClipData clip = ClipData.newPlainText("label",
										url);
								clipboard.setPrimaryClip(clip);
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							mActivity); // dialog
					builder.setTitle(url)
							.setMessage(
									getResources().getString(
											R.string.dialog_link))
							.setPositiveButton(
									getResources().getString(
											R.string.action_new_tab),
									dialogClickListener)
							.setNegativeButton(
									getResources().getString(
											R.string.action_open),
									dialogClickListener)
							.setNeutralButton(
									getResources().getString(
											R.string.action_copy),
									dialogClickListener).show();
				}
			} else {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							newTab(url, false);
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							mCurrentView.loadUrl(url);
							break;

						case DialogInterface.BUTTON_NEUTRAL:
							ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
							ClipData clip = ClipData.newPlainText("label", url);
							clipboard.setPrimaryClip(clip);

							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
				builder.setTitle(url)
						.setMessage(
								getResources().getString(R.string.dialog_link))
						.setPositiveButton(
								getResources().getString(
										R.string.action_new_tab),
								dialogClickListener)
						.setNegativeButton(
								getResources().getString(R.string.action_open),
								dialogClickListener)
						.setNeutralButton(
								getResources().getString(R.string.action_copy),
								dialogClickListener).show();
			}
		} else if (result != null) {
			if (result.getExtra() != null) {
				final String newUrl = result.getExtra();
				if (result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE
						|| result.getType() == HitTestResult.IMAGE_TYPE) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								newTab(newUrl, false);
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								mCurrentView.loadUrl(newUrl);
								break;

							case DialogInterface.BUTTON_NEUTRAL:
								if (API > 8) {
									Utils.downloadFile(mActivity, newUrl,
											mCurrentView.getUserAgent(),
											"attachment", false);
								}
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							mActivity); // dialog
					builder.setTitle(newUrl.replace(Constants.HTTP, ""))
							.setMessage(
									getResources().getString(
											R.string.dialog_image))
							.setPositiveButton(
									getResources().getString(
											R.string.action_new_tab),
									dialogClickListener)
							.setNegativeButton(
									getResources().getString(
											R.string.action_open),
									dialogClickListener)
							.setNeutralButton(
									getResources().getString(
											R.string.action_download),
									dialogClickListener).show();

				} else {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								newTab(newUrl, false);
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								mCurrentView.loadUrl(newUrl);
								break;

							case DialogInterface.BUTTON_NEUTRAL:
								ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
								ClipData clip = ClipData.newPlainText("label",
										newUrl);
								clipboard.setPrimaryClip(clip);

								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(
							mActivity); // dialog
					builder.setTitle(newUrl)
							.setMessage(
									getResources().getString(
											R.string.dialog_link))
							.setPositiveButton(
									getResources().getString(
											R.string.action_new_tab),
									dialogClickListener)
							.setNegativeButton(
									getResources().getString(
											R.string.action_open),
									dialogClickListener)
							.setNeutralButton(
									getResources().getString(
											R.string.action_copy),
									dialogClickListener).show();
				}

			}

		}

	}

	/**
	 * This method lets the search bar know that the page is currently loading
	 * and that it should display the stop icon to indicate to the user that
	 * pressing it stops the page from loading
	 */
	public void setIsLoading() {

	}

	/**
	 * This tells the search bar that the page is finished loading and it should
	 * display the refresh icon
	 */
	public void setIsFinishedLoading() {

	}

	/**
	 * handle presses on the refresh icon in the search bar, if the page is
	 * loading, stop the page, if it is done loading refresh the page.
	 * 
	 * See setIsFinishedLoading and setIsLoading for displaying the correct icon
	 */
	public void refreshOrStop() {
		if (mCurrentView != null) {
			if (mCurrentView.getProgress() < 100) {
				mCurrentView.stopLoading();
			} else {
				mCurrentView.reload();
			}
		}
	}

	// Override this, use finish() for Incognito, moveTaskToBack for Main
	public void closeActivity() {
		finish();
	}

	public class SortIgnoreCase implements Comparator<HistoryItem> {

		public int compare(HistoryItem o1, HistoryItem o2) {
			return o1.getTitle().toLowerCase(Locale.getDefault())
					.compareTo(o2.getTitle().toLowerCase(Locale.getDefault()));
		}

	}

	@Override
	public int getMenu() {
		return 0;
	}

	@Override
	public void onClick(View v) {

	}
}
