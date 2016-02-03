package pk.gov.pitb.speakmytext.helper;

import pk.gov.pitb.speakmytext.ActivitySplashScreen;
import pk.gov.pitb.speakmytext.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class Util {

	public static void showExitDialog(final Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.app_name));
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				((Activity) context).finish();
				context.startActivity(new Intent(context, ActivitySplashScreen.class));
			}
		});
		builder.setPositiveButton("Cancel", null);
		builder.create();
		builder.show();
	}

	public static void showInfoDialog(final Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.app_name));
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setNegativeButton("OK", null);
		builder.create();
		builder.show();
	}

	public static boolean isNetworkAvailable(Context c) {
		try{
			ConnectivityManager connectivityManager= (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			return activeNetworkInfo != null;			
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static String getDeviceId(Context c) {
		TelephonyManager telephonyManager = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
		String device_id = telephonyManager.getDeviceId();
		if(device_id==null || device_id.isEmpty())
			device_id = "0000000000";
		return device_id;
	}	
}
