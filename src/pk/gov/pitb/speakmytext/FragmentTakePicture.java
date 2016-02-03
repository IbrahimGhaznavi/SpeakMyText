package pk.gov.pitb.speakmytext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

public class FragmentTakePicture extends Fragment{

	public static final int CAMERA_REQUEST = 1888;
	public static ImageView imageView;
	public static Bitmap photo;
	public static ImageView ivTakePicture;
	public static ImageView ivAcceptImage, ivCancelImage;

	private View rootView;
	public ProgressDialog progressDialog;
	public Context context;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_take_picture, container, false);
		
		try {
			context = getActivity();
			imageView = (ImageView) rootView.findViewById(R.id.iv_picture);

			Display display = getActivity().getWindowManager().getDefaultDisplay();
			int mScreenWidth = display.getWidth();
			int mScreenHeight = display.getHeight();

			LinearLayout.LayoutParams lpImageView = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.90), (int) (mScreenWidth * 1.15));
			lpImageView.setMargins(0, 0, 0, (int) (mScreenHeight * 0.05));
			imageView.setLayoutParams(lpImageView);

			LinearLayout.LayoutParams lpBtnSmall = new LinearLayout.LayoutParams((int) (mScreenWidth * 0.15), (int) (mScreenWidth * 0.15));
			lpBtnSmall.setMargins((int) (mScreenWidth * 0.05), 0, (int) (mScreenWidth * 0.05), 0);
			ivAcceptImage = (ImageView) rootView.findViewById(R.id.iv1);
			ivTakePicture = (ImageView) rootView.findViewById(R.id.iv2);
			ivCancelImage = (ImageView) rootView.findViewById(R.id.iv3);

			ivAcceptImage.setLayoutParams(lpBtnSmall);
			ivTakePicture.setLayoutParams(lpBtnSmall);
			ivCancelImage.setLayoutParams(lpBtnSmall);
			ivAcceptImage.setPadding(0, 0, 0, 0);
			ivTakePicture.setPadding(0, 0, 0, 0);
			ivCancelImage.setPadding(0, 0, 0, 0);

			// iv1.setClickable(false);
			// iv3.setClickable(false);

			ivTakePicture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(camera, CAMERA_REQUEST);

				}
			});
			ivAcceptImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// pd.show();
					try {
						ivAcceptImage.setEnabled(false);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						photo.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm
						byte[] b = baos.toByteArray();
						String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

						Asynctask_data qa = new Asynctask_data();
						qa.execute(encodedImage);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			ivCancelImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ivAcceptImage.setVisibility(View.GONE);
					ivCancelImage.setVisibility(View.GONE);
					ivTakePicture.setVisibility(View.VISIBLE);
					imageView.setImageResource(R.drawable.takepic);
					Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(camera, CAMERA_REQUEST);
				}
			});
			ivAcceptImage.setVisibility(View.GONE);
			ivCancelImage.setVisibility(View.GONE);
			ivTakePicture.setVisibility(View.VISIBLE);
			if (photo != null) {
				imageView.setImageBitmap(photo);
				ivTakePicture.setVisibility(View.GONE);
				ivAcceptImage.setVisibility(View.VISIBLE);
				ivCancelImage.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootView;
	}

	public void onFragmentShown() {
		if (ivTakePicture.getVisibility() == View.VISIBLE) {
			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(camera, CAMERA_REQUEST);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
			try {
				photo = (Bitmap) data.getExtras().get("data");
				if (photo != null) {
					Constants.get_current_job();
					Constants.createDirIfNotExists("/pmr/" + Constants.currentjob);
					File sdCardDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/pmr/" + Constants.currentjob);
					String fileName = sdCardDirectory + "job.jpg";
					FileOutputStream fos = new FileOutputStream(new File(fileName));
					photo.compress(CompressFormat.JPEG, 100, fos);
					imageView.setImageBitmap(photo);
					ivTakePicture.setVisibility(View.GONE);
					ivAcceptImage.setVisibility(View.VISIBLE);
					ivCancelImage.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(getActivity(), "Error Loading Image. Please Retry", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), "Error Loading Image. Please Retry", Toast.LENGTH_LONG).show();
			}
		}
		else
			try{
			ActivityHomePage.viewPager.setCurrentItem(0);
			}catch (Exception e) {
				e.printStackTrace();
			}
	}

	public class Asynctask_data extends AsyncTask<String, Void, JSONObject> {

		private boolean bIsFailed = false;
		JSONParser js;

		public Asynctask_data() {
		}

		@Override
		protected void onPreExecute() {
			try {
				Constants.inProgress2=true;
				js = new JSONParser();
				progressDialog = new ProgressDialog(context);
				progressDialog.setProgressNumberFormat(null);
//				progressDialog.setProgressPercentFormat(null);
				progressDialog.setTitle("Uploading image");
				progressDialog.setMessage("Please Wait...");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setCancelable(false);
				progressDialog.show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), "Error Uploading Image", Toast.LENGTH_LONG).show();
			}
		}

		String url = Constants.url + "add_job";
		private String res = "";

		@Override
		protected JSONObject doInBackground(String... args) {
			try {
				JSONObject j;

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_token", Constants.app_token));
				params.add(new BasicNameValuePair("user_id", Constants.user_id));
				params.add(new BasicNameValuePair("auth_token", Constants.auth_token));
				params.add(new BasicNameValuePair("from_lang", "1"));
				params.add(new BasicNameValuePair("to_lang", "2"));
				params.add(new BasicNameValuePair("image", args[0]));
				j = js.getJSONFromUrl(url, params, context);
				Log.e("wapsi JSON ", j.toString());
				res  = j.toString();
				bIsFailed = false;
				return j;
			} catch (Exception e) {
				e.printStackTrace();
				bIsFailed = true;
				return null;
			}

		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				progressDialog.dismiss();
				ivAcceptImage.setEnabled(true);
				if(res!=null && res.contains("Not Authentic"))
					Util.showExitDialog(context, "Session has expired, please login again.");
				if (bIsFailed) {
					Toast.makeText(getActivity(), "Internet not available. Please Retry", Toast.LENGTH_LONG).show();
				} else {
					if (json != null && json.has("job_id")) {
						Constants.job_id = json.getString("job_id");
						Constants.update_jobs();
						Constants.update_jobs_id();
						Constants.increase_current_job();
						Constants.lod = true;
						Constants.update_jobs_resources();
						ivAcceptImage.setVisibility(View.GONE);
						ivCancelImage.setVisibility(View.GONE);
						ivTakePicture.setVisibility(View.VISIBLE);
						imageView.setImageResource(R.drawable.takepic);
						photo = null;
						Toast.makeText(getActivity(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
						ActivityHomePage.viewPager.setCurrentItem(0);
						// Constants.fragmentTakePicture = new FragmentTakePicture();
						// Constants.fragmentTakePicture.onCreateView(inflater, container, savedInstanceState);
					} else {
						Toast.makeText(getActivity(), "Internet not available. Please Retry", Toast.LENGTH_LONG).show();
					}
				}
				Constants.inProgress2=false;
			} catch (Exception e) {
				Constants.inProgress2=false;
				e.printStackTrace();
				Toast.makeText(getActivity(), "Error Uploading Image", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.e("on pause", "onPause fr take picture");
		Constants.inProgress2=false;
	}
}
