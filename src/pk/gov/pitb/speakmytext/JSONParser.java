/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package pk.gov.pitb.speakmytext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import me.leolin.shortcutbadger.util.AndroidMultiPartEntity;
import me.leolin.shortcutbadger.util.AndroidMultiPartEntity.ProgressListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import pk.gov.pitb.speakmytext.helper.ServerCallbackListener;
import android.content.Context;
import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	JSONObject jObj = null;
	protected long totalSize=0;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params, final Context context) throws Exception {

		// Making HTTP request
		/*
		 * HttpParams httpParameters = new BasicHttpParams();
		 * int timeoutConnection = 3000;
		 * HttpConnectionParams.setConnectionTimeout(httpParameters,
		 * timeoutConnection);
		 * /*int timeoutSocket = 5000;
		 * HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		 */
		// DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		AndroidMultiPartEntity entity = null;
		try {
			entity = new AndroidMultiPartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							((ServerCallbackListener) context).onProgressChange(totalSize, (int) num);
						}
					});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < params.size(); i++) {
			entity.addPart(params.get(i).getName(), new StringBody(params.get(i).getValue()));
		}
		totalSize = entity.getContentLength();
		httpPost.setEntity(entity);
		//				httpPost.setEntity(new UrlEncodedFormEntity(params));

		HttpResponse httpResponse = httpClient.execute(httpPost);
		//		json = EntityUtils.toString(httpResponse.getEntity());
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		long contentLength = httpEntity.getContentLength();
		int progress = 0;
		//		if(context!=null)
		//			((ServerCallbackListener) context).onProgressChange(contentLength, progress);
		Log.e("total", contentLength+";;");
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
			progress = progress + line.length();
			Log.e("preogres", progress+";;");
			//			if(context!=null)
			//				((ServerCallbackListener) context).onProgressChange(contentLength, progress);
		}
		is.close();
		json = sb.toString();
		Log.e("JSON", json);

		// try parse the string to a JSON object
		jObj = new JSONObject(json);

		// return JSON String
		return jObj;

	}
}
