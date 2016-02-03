package pk.gov.pitb.speakmytext.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import me.leolin.shortcutbadger.ShortcutBadger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pk.gov.pitb.speakmytext.ActivitySplashScreen;
import pk.gov.pitb.speakmytext.R;
import pk.gov.pitb.speakmytext.helper.Constants;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class DownloadService extends IntentService {

	public static final long NOTIFY_INTERVAL = 10 * 60 * 1000; // 60 seconds

	public static List<Integer> jobsnotdone = new ArrayList<Integer>(0);
	public static List<Integer> jobsdone = new ArrayList<Integer>(0);
	public static List<String> jobsid = new ArrayList<String>(0);
	public static List<String> job_ids = new ArrayList<String>(0);
	public static String url = Constants.url + "get_chunks";
	public static String app_token = "k234jj24bjkjhkjds9u3904jijeifj034fnkdng";
	static String user_id = null;
	static String auth_token = null;
	static HttpClient httpclient = null;
	static HttpPost httppost = null;
	static HttpResponse response = null;
	static List<String> url_imgs = new ArrayList<String>();;
	static List<String> url_wavs = new ArrayList<String>();
	static int fold = 0;
	static int lock = 0;
	static int rest = 0;
	static int finish = 0;
	static int counts = 0;
	static int flag = 0;
	// run on another Thread to avoid crash
	private Handler mHandler = new Handler();
	// timer handling
	private Timer mTimer = null;
	public static final String NOTIFICATION = "pk.gov.pitb.poormansreader";
	boolean error = false;
	// public static final String NOTIFICATION =
	// "com.vogella.android.service.receiver";

	public DownloadService() {
		super("DownloadService");
	}

	// will be called asynchronously by Android
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("service", "onHandleIntent");
		user_id = intent.getStringExtra("user_id");
		auth_token = intent.getStringExtra("auth_token");

		if (mTimer != null) {
			mTimer.cancel();
		} else {
			// recreate new
			mTimer = new Timer();
		}
		// schedule task
		mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 60 * 1000, NOTIFY_INTERVAL);

	}

	class TimeDisplayTimerTask extends TimerTask {

		@Override
		public void run() {
			// run on another thread
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					rest = 0;
					counts = 0;
					try{
						if (read()) {
							if (make_url()) {

								// Log.e("job_ids 0 ", String.valueOf(job_ids.get(0)));
								// Log.e("job_ids 1 ", String.valueOf(job_ids.get(1)));
								if (flag == 0) {
									flag = 1;
									Asynctask_data2 a = new Asynctask_data2();
									try {
										a.execute().get();
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (ExecutionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									flag = 0;
								}
							}
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}

			});
		}

		void checkandgetchunks() {
			try {
				if (isInternetAvailable()) {
					httpclient = new DefaultHttpClient();
					httppost = new HttpPost(url);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					for (int i = 0; i < job_ids.size(); i++) {
						nameValuePairs.clear();
						try {
							nameValuePairs.add(new BasicNameValuePair("app_token", app_token));
							nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
							nameValuePairs.add(new BasicNameValuePair("auth_token", auth_token));
							nameValuePairs.add(new BasicNameValuePair("job_id", job_ids.get(i)));
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

							try {
								response = httpclient.execute(httppost);
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							HttpEntity resEntityGet = response.getEntity();
							String sourceString = "";
							if (resEntityGet != null) {

								try {
									sourceString = new String(EntityUtils.toString(resEntityGet));
								} catch (org.apache.http.ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
							String result = sourceString;
							if (!sourceString.equals(""))// yahan not equal ana hai
							{
								try {
									JSONObject jo = new JSONObject(result);
									if (jo.has("chunks")) {
										JSONArray jsonarraybyeng = jo.getJSONArray("chunks");
										url_wavs.clear();
										url_imgs.clear();
										for (int i1 = 0; i1 < jsonarraybyeng.length(); i1++) {
											JSONObject jo2 = jsonarraybyeng.getJSONObject(i1);
											url_wavs.add(jo2.getString("audio_url"));
											url_imgs.add(jo2.getString("image_url"));

										}

										fold = jobsid.indexOf(job_ids.get(i)) + 1;
										Asynctask_data qa = new Asynctask_data();
										if(!Constants.inProgress1 && !Constants.inProgress2)
											qa.execute();
										Log.e("************", Constants.inProgress1+";;"+Constants.inProgress2);
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		void removeit(int folder) {

			String dir = Environment.getExternalStorageDirectory() + "/pmr";
			File f = new File(dir, "jobsnotdone.txt");

			List<String> jobs = new ArrayList<String>(0);

			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null) {
					jobs.add(line);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("hello");

			}

			jobsnotdone.clear();
			for (int i = 0; i < jobs.size(); i++) {
				jobsnotdone.add(Integer.valueOf(jobs.get(i)));
			}

			if (jobsnotdone.contains(Integer.valueOf(folder))) {
				jobsnotdone.remove(Integer.valueOf(folder));
			}

			int[] arr1 = new int[jobsnotdone.size()];
			// option 1
			for (int i = 0; i < jobsnotdone.size(); i++) {
				arr1[i] = jobsnotdone.get(i);
			}

			Arrays.sort(arr1);
			System.out.println(Arrays.toString(arr1));

			jobsnotdone.clear();
			for (int i = (arr1.length) - 1; i >= 0; i--) {
				jobsnotdone.add(Integer.valueOf(arr1[i]));
			}
			f.delete();
			try {
				if (!f.createNewFile()) {
					Log.e("TravellerLog :: ", "Problem creating files");

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();

			}

			try {
				FileOutputStream fOut = new FileOutputStream(f);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

				for (int i = (arr1.length) - 1; i >= 0; i--) {
					myOutWriter.write(String.valueOf(arr1[i]));
					myOutWriter.write("\n");
				}

				myOutWriter.close();

			} catch (IOException e) {
				Log.e("Exception", "File write failed: " + e.toString());
			}

			f = new File(dir, "jobsdone.txt");

			jobs.clear();
			jobs = new ArrayList<String>(0);

			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null) {
					jobs.add(line);

				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("hello");

			}

			jobsdone.clear();
			for (int i = 0; i < jobs.size(); i++) {
				jobsdone.add(Integer.valueOf(jobs.get(i)));
			}
			if (!jobsdone.contains(Integer.valueOf(folder)))
				jobsdone.add(Integer.valueOf(folder));

			int[] arr2 = new int[jobsdone.size()];
			// option 1
			for (int i = 0; i < jobsdone.size(); i++) {
				arr2[i] = jobsdone.get(i);
			}

			Arrays.sort(arr2);
			System.out.println(Arrays.toString(arr1));

			jobsdone.clear();
			for (int i = (arr2.length) - 1; i >= 0; i--) {
				jobsdone.add(Integer.valueOf(arr2[i]));
			}
			f.delete();
			try {
				if (!f.createNewFile()) {
					Log.e("TravellerLog :: ", "Problem creating files");

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();

			}

			try {
				FileOutputStream fOut = new FileOutputStream(f);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

				for (int i = (arr2.length) - 1; i >= 0; i--) {
					myOutWriter.write(String.valueOf(arr2[i]));
					myOutWriter.write("\n");
				}

				myOutWriter.close();
				rest++;

			} catch (IOException e) {
				Log.e("Exception", "File write failed: " + e.toString());
			}

		}

		void downloadthem() {
			int folder = fold;
			Log.e("in downthem folder", String.valueOf(folder));
			//			long totalData=0;
			int chunk = 0;
			//			FragmentRecordAudio.updateProgress(60, 0);
			for (int i = 0; i < 3; i++) {
				int count;
				try {
					chunk = chunk + 10;
					String dir = Environment.getExternalStorageDirectory() + "/pmr" + "/" + String.valueOf(folder);
					String data1 = String.valueOf(String.format("%s.mp3", i + 1));
					File mypath = new File(dir, data1);
					URL url = new URL(Constants.homeurl + "/poorman-webapp" + url_wavs.get(i));

					URLConnection conexion = url.openConnection();
					Log.e("in downthem audio", "1");
					conexion.connect();
					Log.e("in downthem audio", "2");
					// this will be useful so that you can show a tipical 0-100%
					// progress bar
					int lenghtOfFile = conexion.getContentLength();

					// downlod the file
					InputStream input = new BufferedInputStream(url.openStream());
					OutputStream output = new FileOutputStream(mypath);

					byte data[] = new byte[1024];

					long total = 0;

					while ((count = input.read(data)) != -1) {
						total += count;
						//						totalData = totalData + count;
						output.write(data, 0, count);
						//						FragmentRecordAudio.updateProgress(60, (int) ((total/lenghtOfFile)*chunk));
					}

					output.flush();
					output.close();
					input.close();

					// successfully finished

				} catch (Exception e) {
					e.printStackTrace();
					rest = -1;
					error=true;
					return;
				}

			}

			for (int i = 0; i < 3; i++) {

				int count;
				try {
					chunk = chunk + 10;

					String dir = Environment.getExternalStorageDirectory() + "/pmr" + "/" + String.valueOf(folder);
					String data1 = String.valueOf(String.format("%s.jpg", i + 1));
					File mypath = new File(dir, data1);
					URL url = new URL(Constants.homeurl + "/poorman-webapp" + url_imgs.get(i));
					URLConnection conexion = url.openConnection();
					Log.e("in downthem image", "1");
					conexion.connect();
					Log.e("in downthem image", "2");
					// this will be useful so that you can show a tipical 0-100%
					// progress bar
					int lenghtOfFile = conexion.getContentLength();

					// downlod the file
					InputStream input = new BufferedInputStream(url.openStream());
					OutputStream output = new FileOutputStream(mypath);

					byte data[] = new byte[1024];

					long total = 0;

					while ((count = input.read(data)) != -1) {
						total += count;

						output.write(data, 0, count);
						//						FragmentRecordAudio.updateProgress(60, (int) ((total/lenghtOfFile)*chunk));
					}

					output.flush();
					output.close();
					input.close();
					// successfully finished

				} catch (Exception e) {
					rest = -1;
					error=true;
					return;
				}
			}
			removeit(folder);

		}

		public class Asynctask_data2 extends AsyncTask<Void, Void, Void> {

			public Asynctask_data2() {
			}

			@Override
			protected void onPreExecute() {

				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... args) {
				try {
					checkandgetchunks();
					jobsdone.clear();
					jobsnotdone.clear();
					job_ids.clear();
					//					if (counts > 0) {
					//						publishResults(String.valueOf(rest));
					//					}

				} catch (Exception e) {
					e.printStackTrace();
					return null;
					// Toast.makeText(c, e.toString(),
					// Toast.LENGTH_LONG).show(); ;
				}
				return null;
			}
		}

		public class Asynctask_data extends AsyncTask<Void, Void, Void> {

			public Asynctask_data() {
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				error=false;
			}

			@Override
			protected Void doInBackground(Void... args) {
				try {
					lock = 0;
					downloadthem();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
					// Toast.makeText(c, e.toString(),
					// Toast.LENGTH_LONG).show(); ;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(!error){
					counts++;
					if (counts > 0) {
						publishResults(String.valueOf(rest));
					}
				}
			}
		}

		boolean make_url() {

			for (int i = 0; i < jobsnotdone.size(); i++) {

				job_ids.add(jobsid.get(jobsnotdone.get(i) - 1));
			}
			return true;
		}

		boolean read() {

			String dir = Environment.getExternalStorageDirectory() + "/pmr";
			File f = new File(dir, "jobsnotdone.txt");

			List<String> jobs = new ArrayList<String>(0);
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null) {
					jobs.add(line);

				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("hello");

			}

			jobsnotdone.clear();
			for (int i = 0; i < jobs.size(); i++) {
				jobsnotdone.add(Integer.valueOf(jobs.get(i)));
			}

			jobs.clear();
			f = new File(dir, "jobsid.txt");

			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null) {
					jobs.add(line);

				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("hello");

			}

			jobsid.clear();
			for (int i = 0; i < jobs.size(); i++) {
				jobsid.add(String.valueOf(jobs.get(i)));
			}

			return true;
		}

		void publishResults(String result) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DownloadService.this).setSmallIcon(R.drawable.icon_launcher_small).setContentTitle("Poor Man's Reader")
					.setContentText("You have " + String.valueOf(counts) + " translation available.");
			Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);
			// Creates an explicit intent for an Activity in your app

			Intent resultIntent = new Intent(DownloadService.this, ActivitySplashScreen.class);
			PendingIntent resultPendingIntent = PendingIntent.getService(DownloadService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
			mBuilder.setContentIntent(resultPendingIntent);

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.

			mNotificationManager.notify(123, mBuilder.build());

			Intent intent = new Intent(NOTIFICATION);
			intent.putExtra("res", "1");
			intent.putExtra("count", String.valueOf(counts));
			sendBroadcast(intent);

			try {
				ShortcutBadger.setBadge(getApplicationContext(), counts);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isInternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}