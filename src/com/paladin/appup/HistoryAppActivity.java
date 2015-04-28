package com.paladin.appup;



import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.paladin.appup.adapters.HistoryListAdapter;
import com.paladin.appup.adapters.HistoryListElement;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryAppActivity extends Activity {

	ListView newAppListView;
	private static List<HistoryListElement> historyAppListElement;
	private HistoryListAdapter historyAppListAdapter;

	SharedPreferences userDetails;
	String user_id;
	String api_key;

	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.history);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.history));
		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		api_key = userDetails.getString("api_key", "null");
		Log.d("api_key", userDetails.getString("api_key", "null"));
		Log.d("user_id", userDetails.getString("user_id", "null"));
		new LoadResponse().execute();

		PullToRefreshListView pullToRefreshView = (PullToRefreshListView) findViewById(R.id.historyListView);
		pullToRefreshView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// Do work to refresh the list here.
						new LoadResponse().execute();
					}
				});
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(HistoryAppActivity.this, MainScreenActivity.class);
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

	class LoadResponse extends AsyncTask<Void, Void, List<HistoryListElement>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(HistoryAppActivity.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected List<HistoryListElement> doInBackground(Void... params) {
			String result = null;
			historyAppListElement = new ArrayList<HistoryListElement>();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create("http://appup.me/user/api/get_history/"
							+ user_id + "/" + api_key + "/5"));

			try {

				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity resEntity = httpresponse.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				result = responseString;
				Log.d("MIF", responseString);
				JSONArray ja = new JSONArray(result);
				for (int i = ja.length() - 1; i >= 0; i--) {
					JSONObject jo = ja.getJSONObject(i);
					String reason = jo.getString("reason");
					String time = jo.getString("time");
					String bonus = jo.getString("bonus");
					Log.d("MIF_HISTORY", reason);
					HistoryListElement nale = new HistoryListElement(reason,
							bonus, time);
					historyAppListElement.add(nale);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return historyAppListElement;

		}

		@Override
		protected void onPostExecute(
				List<HistoryListElement> historyAppListElement) {
			historyAppListAdapter = new HistoryListAdapter(
					HistoryAppActivity.this, 0, historyAppListElement);
			final PullToRefreshListView listIn = (PullToRefreshListView) findViewById(R.id.historyListView);
			if (historyAppListElement.size() == 0) {
				setContentView(R.layout.no_history);
				//getActionBar().hide();
				dialog.dismiss();
				listIn.onRefreshComplete();
			} else {
				listIn.setAdapter(historyAppListAdapter);
				dialog.dismiss();
				listIn.onRefreshComplete();
			}

		}
	}

}
