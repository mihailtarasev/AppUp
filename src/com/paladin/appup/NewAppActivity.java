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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.paladin.appup.adapters.NewAppListAdapter;
import com.paladin.appup.adapters.NewAppListElement;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class NewAppActivity extends Activity {

	SharedPreferences userDetails;
	String user_id;
	String api_key;

	ListView newAppListView;
	private static List<NewAppListElement> newAppListElement;
	private NewAppListAdapter newAppListAdapter;

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
		tvTitle.setText(getString(R.string.new_app));

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
		pullToRefreshView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView parent,
							View itemClicked, int position, long id) {
						Intent i = new Intent(NewAppActivity.this,
								DescriptionActivity.class);
						NewAppListElement element = newAppListElement
								.get(position - 1);
						i.putExtra("image", element.getImage());
						i.putExtra("name", element.getName());
						i.putExtra("description", element.getDescription());
						i.putExtra("price", element.getPrice());
						i.putExtra("app_url", element.getAppUrl());
						i.putExtra("offer_id", element.getOfferId());
						
						i.putExtra("shema", 1);
						startActivity(i);

					}

				});
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

	@Override
	public void onBackPressed() {
		Intent i = new Intent(NewAppActivity.this, MainScreenActivity.class);
		startActivity(i);
	}

	class LoadResponse extends AsyncTask<Void, Void, List<NewAppListElement>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(NewAppActivity.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected List<NewAppListElement> doInBackground(Void... params) {
			String result = null;
			newAppListElement = new ArrayList<NewAppListElement>();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"get_offers/" + user_id
							+ "/" + api_key + "/5"));
			Log.d("api_key", api_key);
			Log.d("user_id", user_id);

			try {

				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity resEntity = httpresponse.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				result = responseString;
				Log.d("MIF", responseString);
				JSONArray ja = new JSONArray(result);
				JSONArray ja1 = ja.getJSONArray(0);
				for (int i = 0; i < ja1.length(); i++) {
					JSONObject jo = ja1.getJSONObject(i);
					String status = jo.getString("status");
					String preview = jo.getString("preview");
					String name = jo.getString("name");
					String link = jo.getString("link");
					String bonus = jo.getString("bonus");
					String description = jo.getString("description");
					String offer_id = jo.getString("id");
					if (status.equals("0")) {
						NewAppListElement nale = new NewAppListElement(
								offer_id, preview, name, bonus, link,
								description);
						newAppListElement.add(nale);
					}
					Log.d("status" + i, status);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return newAppListElement;

		}

		@Override
		protected void onPostExecute(List<NewAppListElement> newAppListElement) {
			newAppListAdapter = new NewAppListAdapter(NewAppActivity.this, 0,
					newAppListElement);
			final PullToRefreshListView listIn = (PullToRefreshListView) findViewById(R.id.historyListView);
			if (newAppListElement.size() == 0) {
				setContentView(R.layout.no_new_apps);
				// getActionBar().hide();
				dialog.dismiss();
				listIn.onRefreshComplete();
			} else {
				listIn.setAdapter(newAppListAdapter);
				dialog.dismiss();
				listIn.onRefreshComplete();
			}

		}
	}

}
