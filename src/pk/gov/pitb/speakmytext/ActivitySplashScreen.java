package pk.gov.pitb.speakmytext;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import pk.gov.pitb.speakmytext.helper.Constants;
import pk.gov.pitb.speakmytext.helper.ServerCallbackListener;
import pk.gov.pitb.speakmytext.service.DownloadService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ActivitySplashScreen extends Activity implements ServerCallbackListener{

	private LoadingTask mTask;
	ProgressBar progress;
	private ActivitySplashScreen context;
	public ProgressDialog pd;

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		context = this;
		progress = (ProgressBar) findViewById(R.id.progress_bar);

		/*
		 * Constants.auth_token="nisl356hmxvyl1fs12ht4iti8ueqe78szme3ason";
		 * Constants.user_id="12"; Intent i1=new
		 * Intent(MainActivity.this,Home2.class); startActivity(i1); finish();
		 * 
		 * return;
		 */

	}

	void startmyactivity() {
		Asynctask_data qa = new Asynctask_data(ActivitySplashScreen.this);
		try {
			qa.execute(Constants.imie);
		} catch (Exception es) {

		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mTask = new LoadingTask(progress);
		mTask.execute();

	}

	public class LoadingTask extends AsyncTask<Void, Integer, Void> {

		private final ProgressBar progressBar;

		public LoadingTask(ProgressBar progressBar) {
			this.progressBar = progressBar;
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < 100; i++) {
				try {
					Thread.sleep(50);
					this.publishProgress(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				Constants.imie = tm.getDeviceId();
				WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = manager.getConnectionInfo();
				Constants.mac = info.getMacAddress();
				Constants.createDirIfNotExists("/pmr");
				Constants.createFileIfNotExists("jobsdone.txt");
				Constants.createFileIfNotExists("jobsnotdone.txt");
				Constants.createFileIfNotExists("jobsid.txt");
				Constants.get_current_job();

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progressBar.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(isInternetAvailable())
				startmyactivity();
			else{
				Intent i = new Intent(ActivitySplashScreen.this, ActivityHomePage.class);
				String v = "0";
				i.putExtra("v", v);
				startActivity(i);
				finish();
			}
		}
	}

	public class Asynctask_data extends AsyncTask<String, Void, JSONObject> {

		JSONParser js;
		Context _c;

		public Asynctask_data(Context c) {
			_c = c;
		}

		@Override
		protected void onPreExecute() {
			js = new JSONParser();
			super.onPreExecute();
			pd = new ProgressDialog(_c);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setProgressNumberFormat(null);
			//			pd.setProgressPercentFormat(null);
			pd.setMessage("Logging in  ...");
			pd.setCancelable(false);
			pd.show();
		}

		String url = Constants.url + "login";

		@Override
		protected JSONObject doInBackground(String... args) {
			try {
				JSONObject j;

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_token", Constants.app_token));
				params.add(new BasicNameValuePair("imei", args[0]));
				params.add(new BasicNameValuePair("mac", Constants.mac));

				j = js.getJSONFromUrl(url, params, _c);
				Log.e("wapsi JSON ", j.toString());
				return j;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
				// Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show(); ;
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result == null) {
				pd.dismiss();
				showInternetDialog();
			} else {
				try {
					String s = result.getString("status");
					if (s.compareTo("success") == 0) {
						Constants.auth_token = result.getString("auth_token");
						Constants.user_id = result.getString("user_id");
						Asynctask_data3 zxc = new Asynctask_data3(pd);
						zxc.execute();
					} else {
						JSONObject jb = result.getJSONObject("errors");
						if (jb.has("authentication")) {
							Toast.makeText(ActivitySplashScreen.this, jb.getString("authentication"), Toast.LENGTH_SHORT).show();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public class Asynctask_data3 extends AsyncTask<Void, Void, Void> {

		ProgressDialog pd;
		public Asynctask_data3(ProgressDialog pd) {
			this.pd = pd;
		}

		@Override
		protected void onPostExecute(Void result) {
			Asynctask_add_lang qa1 = new Asynctask_add_lang(pd);
			qa1.execute();
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... args) {
			try {
				Intent intent = new Intent(ActivitySplashScreen.this, DownloadService.class);
				intent.putExtra("user_id", Constants.user_id);
				intent.putExtra("auth_token", Constants.auth_token);
				startService(intent);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
				// Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show(); ;
			}
			return null;
		}
	}

	public class Asynctask_add_lang extends AsyncTask<String, Void, JSONObject> {

		private boolean bIsFailed = false;
		JSONParser js;
		private ProgressDialog pd;

		public Asynctask_add_lang(ProgressDialog pd) {
			this.pd = pd;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			pd.dismiss();
			if (bIsFailed) {
				showInternetDialog();
			} else {
				Intent i = new Intent(ActivitySplashScreen.this, ActivityHomePage.class);
				String v = "0";
				i.putExtra("v", v);
				startActivity(i);
				Toast.makeText(ActivitySplashScreen.this, "Login Successfull", Toast.LENGTH_SHORT).show();
				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			js = new JSONParser();
			super.onPreExecute();
		}

		String url = Constants.url + "add_user_languages";

		@Override
		protected JSONObject doInBackground(String... args) {
			try {
				JSONObject j;

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_token", Constants.app_token));
				params.add(new BasicNameValuePair("user_id", Constants.user_id));
				params.add(new BasicNameValuePair("auth_token", Constants.auth_token));
				params.add(new BasicNameValuePair("languages", "1|2"));

				j = js.getJSONFromUrl(url, params, context);
				Log.e("wapsi JSON ", j.toString());
				bIsFailed = false;
				return j;
			} catch (Exception e) {
				e.printStackTrace();
				bIsFailed = true;
				return null;
			}
		}
	}

	private boolean isInternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void showInternetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySplashScreen.this);
		builder.setTitle("Error in Connection");
		builder.setMessage("Please Check Internet Connection and Try Again");
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(ActivitySplashScreen.this, ActivitySplashScreen.class);
				startActivity(intent);
				finish();
			}
		});
		builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	}

	@Override
	public void onProgressChange(long max, int progress) {
		try {
			pd.setMax((int) max);
			pd.setProgress(progress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}