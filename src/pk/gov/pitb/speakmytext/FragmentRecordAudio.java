package pk.gov.pitb.speakmytext;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import pk.gov.pitb.speakmytext.helper.Constants;
import pk.gov.pitb.speakmytext.helper.Util;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentRecordAudio extends Fragment {

	private TouchImageView touchViewImage;
	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;

	private ImageView imgViewAcceptRecording, imgViewRecord, imgViewPlayPause, imgViewNext;
	private String dir = Environment.getExternalStorageDirectory() + "/pmr";
	private String data1 = String.valueOf(String.format("%s.jpg", "temp"));

	private boolean flg = false;
	private int iflg = 1;
	private int start = 0;
	private int audio_flag = 0;
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private static Context context;
	private static ProgressDialog progressDialogService;
	public ProgressDialog progressDialog;
	public ProgressDialog progressDialog1;

	/*
	 * 1 -> recording start 2-> recording 3-> recording stop
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_record_audio, container, false);
		context = getActivity();
		Constants.inProgress1=true;
		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp_audio.3gpp";

		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		touchViewImage = (TouchImageView) rootView.findViewById(R.id.tv_image);

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		int mScreenWidth = display.getWidth();
		int mScreenHeight = display.getHeight();

		imgViewAcceptRecording = (ImageView) rootView.findViewById(R.id.iv_accept);
		imgViewRecord = (ImageView) rootView.findViewById(R.id.iv_record);
		imgViewPlayPause = (ImageView) rootView.findViewById(R.id.iv_play_pause);
		imgViewNext = (ImageView) rootView.findViewById(R.id.iv_next);

		LinearLayout.LayoutParams lpImageView = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.90), (int) (mScreenWidth * 1.15));
		lpImageView.setMargins(0, 0, 0, (int) (mScreenHeight * 0.05));
		touchViewImage.setLayoutParams(lpImageView);

		LinearLayout.LayoutParams lpBtnSmallFirst = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
		lpBtnSmallFirst.setMargins(0, 0, (int) (mScreenWidth * 0.05), 0);
		LinearLayout.LayoutParams lpBtnSmall = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
		lpBtnSmall.setMargins((int) (mScreenWidth * 0.05), 0, (int) (mScreenWidth * 0.05), 0);
		LinearLayout.LayoutParams lpBtnSmallLast = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
		lpBtnSmallLast.setMargins((int) (mScreenWidth * 0.05), 0, 0, 0);

		imgViewAcceptRecording.setPadding(0, 0, 0, 0);
		imgViewRecord.setPadding(0, 0, 0, 0);
		imgViewPlayPause.setPadding(0, 0, 0, 0);
		imgViewNext.setPadding(0, 0, 0, 0);

		imgViewAcceptRecording.setLayoutParams(lpBtnSmallLast);
		imgViewRecord.setLayoutParams(lpBtnSmall);
		imgViewPlayPause.setLayoutParams(lpBtnSmall);
		imgViewNext.setLayoutParams(lpBtnSmallFirst);

		imgViewNext.setClickable(false);
		imgViewRecord.setClickable(false);
		imgViewNext.setEnabled(false);
		imgViewRecord.setEnabled(false);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				imgViewPlayPause.setImageResource(R.drawable.plaaay);
			}
		});

		imgViewAcceptRecording.setVisibility(View.GONE);
		imgViewPlayPause.setVisibility(View.GONE);

		imgViewNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				touchViewImage.setImageResource(R.drawable.get);
				start++;
				imgViewAcceptRecording.setVisibility(View.GONE);
				imgViewPlayPause.setVisibility(View.GONE);
				imgViewNext.setClickable(false);
				imgViewRecord.setClickable(false);
				imgViewNext.setEnabled(false);
				imgViewRecord.setEnabled(false);
				request_chunk();
			}
		});
		imgViewRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {

					if (iflg == 1) {
						if (mediaPlayer.isPlaying())
							mediaPlayer.stop();
						mediaPlayer.reset();
						myRecorder = new MediaRecorder();
						myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
						myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
						myRecorder.setOutputFile(outputFile);

						myRecorder.prepare();
						myRecorder.start();
						imgViewRecord.setImageResource(R.drawable.recordingstop);
						iflg = 2;
					} else if (iflg == 2) {
						try {
							myRecorder.stop();
							myRecorder.release();
						} catch (Exception e) {
							e.printStackTrace();
						}
						myRecorder = null;
						imgViewRecord.setImageResource(R.drawable.recordingstart);
						imgViewAcceptRecording.setVisibility(View.VISIBLE);
						imgViewPlayPause.setVisibility(View.VISIBLE);
						imgViewPlayPause.setImageResource(R.drawable.plaaay);
						audio_flag = 0;
						iflg = 3;
					} else if (iflg == 3) {
						imgViewAcceptRecording.setVisibility(View.GONE);
						imgViewRecord.setImageResource(R.drawable.recording);
						imgViewPlayPause.setVisibility(View.GONE);
						imgViewPlayPause.setImageResource(R.drawable.plaaay);
						iflg = 1;
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		imgViewPlayPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (audio_flag == 0) {
					try {
						mediaPlayer.reset();
						mediaPlayer.setDataSource(outputFile);
						mediaPlayer.prepare();
						audio_flag = 1;
					} catch (IllegalStateException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					imgViewPlayPause.setImageResource(R.drawable.plaaay);
				} else {
					mediaPlayer.start();
					imgViewPlayPause.setImageResource(R.drawable.paause);
				}
			}
		});
		imgViewAcceptRecording.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				FileInputStream fis;
				try {
					fis = new FileInputStream(new File(outputFile));
					byte[] buf = new byte[1024];
					int n;
					while (-1 != (n = fis.read(buf)))
						baos.write(buf, 0, n);

					byte[] videoBytes = baos.toByteArray();
					String encodedAudio = Base64.encodeToString(videoBytes, 0);
					Asynctask_data2 qa = new Asynctask_data2();
					qa.execute(encodedAudio);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		return rootView;
	}

	public void onFragmentShown() {
		request_chunk();
	}

	public void request_chunk() {
		Asynctask_data qa = new Asynctask_data();
		try {
			touchViewImage.setImageResource(R.drawable.get);
			qa.execute(touchViewImage);
			/*
			 * if (json.getString("status").compareTo("success") == 0) {
			 * 
			 * image.setImageBitmap(BitmapFactory
			 * .decodeFile(dir + "/" + data1));
			 * 
			 * } else {
			 * start = 0;
			 * image.setImageResource(R.drawable.no);
			 * File mypath = new File(dir, data1);
			 * if (mypath.exists()) {
			 * mypath.delete();
			 * }
			 * 
			 * }
			 * } catch (InterruptedException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * } catch (ExecutionException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * } catch (JSONException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class Asynctask_data2 extends AsyncTask<String, Void, JSONObject> {

		private boolean bIsFailed = false;
		JSONParser js;

		public Asynctask_data2() {
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				progressDialog.dismiss();
				if (bIsFailed) {
					Toast.makeText(getActivity(), "Internet not available. Please Retry", Toast.LENGTH_LONG).show();
				} else {
					if (json.getString("status").compareTo("success") == 0) {
						imgViewAcceptRecording.setVisibility(View.GONE);
						imgViewPlayPause.setVisibility(View.GONE);
						imgViewRecord.setImageResource(R.drawable.recording);
						iflg = 1;
						imgViewPlayPause.setImageResource(R.drawable.plaaay);
						Toast.makeText(getActivity(), "Data Sent", Toast.LENGTH_SHORT).show();
						request_chunk();
					} else {
						Toast.makeText(getActivity(), "Internet not available. Please Retry", Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			js = new JSONParser();
			progressDialog = new ProgressDialog(context);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setProgressNumberFormat(null);
			//			progressDialog.setProgressPercentFormat(null);
			progressDialog.setMessage("Please Wait...");
			progressDialog.setCancelable(false);
			progressDialog.setTitle("Sending Data");
			progressDialog.show();
		}

		String url = Constants.url + "submit_chunk";

		@Override
		protected JSONObject doInBackground(String... args) {
			try {
				JSONObject j;
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_token", Constants.app_token));
				params.add(new BasicNameValuePair("user_id", Constants.user_id));
				params.add(new BasicNameValuePair("auth_token", Constants.auth_token));
				params.add(new BasicNameValuePair("chunk_id", Constants.chunk_id));
				params.add(new BasicNameValuePair("recording", args[0]));
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

	public class Asynctask_data extends AsyncTask<TouchImageView, Void, Bitmap> {

		private boolean bIsFailed = false;
		JSONParser js;
		TouchImageView imageView = null;
		Bitmap bmp;

		public Asynctask_data() {
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			progressDialog1.dismiss();
			if (bIsFailed) {
				Toast.makeText(getActivity(), "Internet not available. Please Retry", Toast.LENGTH_LONG).show();
			} else {
				if(res!=null && res.contains("Not Authentic"))
					Util.showExitDialog(context, "Session has expired, please login again.");
				if (result != null) {
					try {
						if (result.getWidth() > 2048 || result.getHeight() > 2048) {
							Bitmap resizedBitmap = Bitmap.createScaledBitmap(result, result.getWidth() / 2, result.getHeight() / 2, true);
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
							imageView.setImageBitmap(resizedBitmap);
						} else {
							imageView.setImageBitmap(result);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					imgViewNext.setClickable(true);
					imgViewRecord.setClickable(true);
					imgViewNext.setEnabled(true);
					imgViewRecord.setEnabled(true);
				} else {
					imageView.setImageResource(R.drawable.no);
					imgViewNext.setEnabled(false);
					imgViewRecord.setEnabled(false);
					imgViewNext.setClickable(false);
					imgViewRecord.setClickable(false);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			js = new JSONParser();
			progressDialog1 = new ProgressDialog(context);
			progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog1.setProgressNumberFormat(null);
			//			progressDialog.setProgressPercentFormat(null);
			progressDialog1.setMessage("Please Wait...");
			progressDialog1.setCancelable(false);
			progressDialog1.setTitle("Fetching Image");
			progressDialog1.show();
		}

		String url = Constants.url + "request_chunk";
		private String res = "";

		@Override
		protected Bitmap doInBackground(TouchImageView... args) {
			try {
				JSONObject j;
				this.imageView = args[0];

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_token", Constants.app_token));
				params.add(new BasicNameValuePair("user_id", Constants.user_id));
				params.add(new BasicNameValuePair("auth_token", Constants.auth_token));
				params.add(new BasicNameValuePair("start", String.valueOf(start)));

				j = js.getJSONFromUrl(url, params, context);

				bIsFailed = false;
				if (j.getString("status").compareTo("success") == 0) {
					int count;
					try {
						String dir = Environment.getExternalStorageDirectory() + "/pmr";
						String data1 = String.valueOf(String.format("%s.jpg", "temp"));
						File mypath = new File(dir, data1);
						JSONObject jsz = j.getJSONObject("chunk");
						Constants.chunk_id = jsz.getString("chunk_id");
						URL url = new URL(Constants.homeurl + "/poorman-webapp" + jsz.getString("image_url"));
						URLConnection conexion = url.openConnection();
						Log.e("in downthem image", "1");
						conexion.connect();
						Log.e("in downthem image", "2");
						// this will be useful so that you can show a tipical
						// 0-100%
						// progress bar
						int lenghtOfFile = conexion.getContentLength();

						// downlod the file
						InputStream is = conexion.getInputStream();
						bmp = BitmapFactory.decodeStream(is);
						InputStream input = new BufferedInputStream(
								url.openStream());

						OutputStream output = new FileOutputStream(mypath);
						byte data[] = new byte[1024];

						long total = 0;
						while ((count = input.read(data)) != -1) {
							total += count;
							output.write(data, 0, count);
							Constants.fragmentRecordAudio.progressDialog1.setMax((int) total);
							Constants.fragmentRecordAudio.progressDialog1.setProgress((int)((total*100)/lenghtOfFile));
						}

						output.flush();
						output.close();
						input.close();
						/// successfully finished
						return bmp;
					} catch (Exception e) {
						e.printStackTrace();
						bIsFailed = true;
					}
				} else {
					start = 0;
				}
				res  = j.toString();
				Log.e("wapsi JSON ", j.toString());
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				bIsFailed = true;
				return null;
			}
		}
	}

	public static void showServiceDialog() {
		((Activity) context).runOnUiThread(new Runnable() {
			public void run ()
			{
				try{
					progressDialogService = new ProgressDialog(context);
					progressDialogService.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialogService.setProgressNumberFormat(null);
					progressDialogService.setMessage("Please wait for the background downloads to complete, this activity will resume afterwards...");
					progressDialogService.setCancelable(false);
					progressDialogService.setTitle("Download in progress");
					progressDialogService.show();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void dismissServiceDialog() {
		((Activity) context).runOnUiThread(new Runnable() {
			public void run ()
			{
				try{
					progressDialogService.dismiss();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.e("on pause", "onPause fr record audio");
		Constants.inProgress1=false;
	}
	
//	public static void updateProgress(int max, int progress){
//		try{
//			progressDialogService.setMax((int) max);
//			progressDialogService.setProgress(progress);
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
