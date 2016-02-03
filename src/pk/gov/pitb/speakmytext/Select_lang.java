package pk.gov.pitb.speakmytext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pk.gov.pitb.speakmytext.helper.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class Select_lang extends Activity {

	int[] langs_id;
	String[] langs_name;
	private Select_lang context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_lang);
		context = this;

		getLanguagesFromServer();
		Constants.temp_lang.clear();
		Constants.temp_lang = new ArrayList<Boolean>();
		for (int i = 0; i < langs_id.length; i++) {
			Constants.temp_lang.add(false);
		}

		ListView ls = (ListView) findViewById(R.id.lang_listView);
		BinderData b = new BinderData(this, langs_name);
		ls.setAdapter(b);

		Button btn = (Button) findViewById(R.id.lang_next_btn);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String s = "";
				for (int i = 0; i < langs_id.length; i++) {
					if (Constants.temp_lang.get(i)) {
						if (i == langs_id.length - 1)
							s = s + langs_id[i];
						else
							s = s + langs_id[i] + "|";
					}
				}
				send_lang_data_to_server(s);
				Intent i = new Intent(Select_lang.this, ActivityHomePage.class);
				startActivity(i);
				finish();
			}
		});
	}

	protected void send_lang_data_to_server(String s) {
		// TODO Auto-generated method stub
		Asynctask_data2 qa = new Asynctask_data2();
		try {
			JSONObject json = qa.execute(s).get();
			if (json.has("lang_not_added")) {
			} else if (json.has("error")) {
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getLanguagesFromServer() {
		// TODO Auto-generated method stub
		Asynctask_data qa = new Asynctask_data();
		try {
			JSONObject json = qa.execute().get();
			JSONArray ja = json.getJSONArray("languages");
			langs_id = new int[ja.length()];
			langs_name = new String[ja.length()];
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				langs_id[i] = jo.getInt("lang_id");
				langs_name[i] = jo.getString("lang_name");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.select_lang, menu);
		return true;
	}

	public class Asynctask_data extends AsyncTask<Void, Void, JSONObject> {

		JSONParser js;

		public Asynctask_data() {
		}

		@Override
		protected void onPreExecute() {
			js = new JSONParser();
			super.onPreExecute();
		}

		String url = Constants.homeurl + "/poorman/index.php/webapp/get_all_languages";

		@Override
		protected JSONObject doInBackground(Void... args) {
			try {
				JSONObject j;

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_token", Constants.app_token));

				j = js.getJSONFromUrl(url, params, context);
				Log.e("wapsi JSON ", j.toString());
				return j;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
				// Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show(); ;
			}
		}
	}

	public class Asynctask_data2 extends AsyncTask<String, Void, JSONObject> {

		JSONParser js;

		public Asynctask_data2() {
		}

		@Override
		protected void onPreExecute() {
			js = new JSONParser();
			super.onPreExecute();
		}

		String url = Constants.homeurl + "/poorman/index.php/webapp/add_user_languages";

		@Override
		protected JSONObject doInBackground(String... args) {
			try {
				JSONObject j;

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("app_token", Constants.app_token));
				params.add(new BasicNameValuePair("user_id", Constants.user_id));
				params.add(new BasicNameValuePair("auth_token", Constants.auth_token));
				params.add(new BasicNameValuePair("languages", args[0]));

				j = js.getJSONFromUrl(url, params, context);
				Log.e("wapsi JSON ", j.toString());
				return j;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
				// Toast.makeText(c, e.toString(), Toast.LENGTH_LONG).show(); ;
			}
		}
	}

}
