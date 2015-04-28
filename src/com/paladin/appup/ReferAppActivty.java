package com.paladin.appup;


import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.paladin.appup.adapters.ReferalListAdapter;
import com.paladin.appup.adapters.ReferalListElement;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ReferAppActivty extends Activity {

	Button copyReferals;
	private ProgressDialog dialog;
	ListView newAppListView;
	private static List<ReferalListElement> refsListElement;
	private ReferalListAdapter refsListAdapter;

	SharedPreferences userDetails;

	String user_id;
	String api_key;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.referal_activity);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.referals));

		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		api_key = userDetails.getString("api_key", "null");
		Log.d("api_key", userDetails.getString("api_key", "null"));
		Log.d("user_id", userDetails.getString("user_id", "null"));
		copyReferals = (Button) findViewById(R.id.button1);

		copyReferals.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText("http://appup.me/ref/" + user_id);
				Alerts.showAlert(
						getString(R.string.ref_copy_to_buffer)
								+ user_id, ReferAppActivty.this, getString(R.string.app_info));
			}
		});

		new LoadResponse().execute();

	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ReferAppActivty.this, MainScreenActivity.class);
		startActivity(i);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, getString(R.string.flurry_agent));
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	class LoadResponse extends AsyncTask<Void, Void, List<ReferalListElement>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ReferAppActivty.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected List<ReferalListElement> doInBackground(Void... params) {
			String result = null;
			refsListElement = new ArrayList<ReferalListElement>();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create("http://appup.me/user/api/get_refs/" + user_id
							+ "/" + api_key + "/5"));
			try {
				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity resEntity = httpresponse.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				result = responseString;
				Log.d("MIF", responseString);
				JSONArray ja = new JSONArray(result);
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					String email = jo.getString("email");
					String time = jo.getString("time");
					String bonus = jo.getString("mouney");
					Calendar mydate = Calendar.getInstance();
					mydate.setTimeInMillis(Long.valueOf(time) * 1000);
					SimpleDateFormat format1 = new SimpleDateFormat(
							"yyyy-MM-dd");
					String formatted = format1.format(mydate.getTime());
					ReferalListElement nale = new ReferalListElement(email,
							formatted, bonus);
					refsListElement.add(nale);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

			return refsListElement;

		}

		@Override
		protected void onPostExecute(List<ReferalListElement> refsListElement) {
			super.onPostExecute(refsListElement);

			refsListAdapter = new ReferalListAdapter(ReferAppActivty.this, 0,
					refsListElement);
			final ListView listIn = (ListView) findViewById(R.id.listView1);
			if (refsListElement.size() == 0) {
				setContentView(R.layout.no_referals);
				// getActionBar().hide();
				dialog.dismiss();
			} else {
				listIn.setAdapter(refsListAdapter);
				dialog.dismiss();

			}

		}
	}

}
