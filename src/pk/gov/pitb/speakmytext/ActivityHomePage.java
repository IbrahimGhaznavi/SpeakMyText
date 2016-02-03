package pk.gov.pitb.speakmytext;

import me.leolin.shortcutbadger.ShortcutBadger;
import pk.gov.pitb.speakmytext.helper.Constants;
import pk.gov.pitb.speakmytext.helper.ServerCallbackListener;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityHomePage extends FragmentActivity implements ActionBar.TabListener, ServerCallbackListener {

	public static ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private Menu menu;
	private String v = "";
	static int up = 0;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_default, menu);
		this.menu = menu;
		if (viewPager.getCurrentItem() != 1 && viewPager.getCurrentItem() != 2) {
			this.menu.getItem(0).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case android.R.id.home:
				viewPager.setCurrentItem(0);
				return true;
			case R.id.action_message:
				viewPager.setCurrentItem(3);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (viewPager != null && viewPager.getCurrentItem() > 0) {
			viewPager.setCurrentItem(0);
		} else {
			finish();
		}
	}

//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Bundle bundle = intent.getExtras();
//			if (bundle != null) {
//				String resultCode = bundle.getString("res");
//				// String cnt = bundle.getString("count");
//				if (resultCode.contains("1")) {
//					try {
//						Constants.lod = true;
//						Constants.update_jobs_resources();
//						ShortcutBadger.setBadge(getApplicationContext(), Constants.jobsdone.size());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				} else {
//					try {
//						ShortcutBadger.setBadge(getApplicationContext(), 1);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager_xml);

		try {
			Constants.update_jobs_resources();
			ShortcutBadger.setBadge(getApplicationContext(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		viewPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(mAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
					case 0:
						getActionBar().setDisplayHomeAsUpEnabled(false);
						if (menu != null) {
							menu.getItem(0).setVisible(false);
						}
					break;
					case 1:
						if (Constants.lod == true) {
							Constants.lod = false;
							Intent i = new Intent(ActivityHomePage.this, ActivityHomePage.class);
							// parameters
							String v = "1";
							i.putExtra("v", v);
							startActivity(i);
							finish();

						} else {
							getActionBar().setDisplayHomeAsUpEnabled(true);
							if (menu != null) {
								menu.getItem(0).setVisible(true);
							}
							Constants.fragmentTakePicture.onFragmentShown();
						}
					break;
					case 2:
						Constants.fragmentRecordAudio.onFragmentShown();
						getActionBar().setDisplayHomeAsUpEnabled(true);
						if (menu != null) {
							menu.getItem(0).setVisible(true);
						}
					break;
					case 3:
					default:
						getActionBar().setDisplayHomeAsUpEnabled(true);
						if (menu != null) {
							menu.getItem(0).setVisible(false);
						}
					break;
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		Intent intent = getIntent();
		this.v = intent.getStringExtra("v");
		if (v.equals("1")) {
			viewPager.setCurrentItem(1);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
	}

	@Override
	protected void onPause() {
		super.onPause();
//		unregisterReceiver(receiver);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Constants.inProgress1=false;
		Constants.inProgress2=false;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onProgressChange(long max, int progress) {
		try {
			Constants.fragmentTakePicture.progressDialog.setMax((int) max);
			Constants.fragmentTakePicture.progressDialog.setProgress(progress);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		try {
			Constants.fragmentRecordAudio.progressDialog.setMax((int) max);
			Constants.fragmentRecordAudio.progressDialog.setProgress(progress);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
}