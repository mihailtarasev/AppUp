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
import com.paladin.appup.adapters.TicketListAdapter;
import com.paladin.appup.adapters.TicketListElement;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TicketActivity extends Activity {

	Button createTicket;
	private ProgressDialog dialog;
	ListView newAppListView;
	private static List<TicketListElement> ticketListElement;
	private TicketListAdapter ticketListAdapter;

	SharedPreferences userDetails;

	String user_id;
	String api_key;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.ticket_activity);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.tickets));

		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		api_key = userDetails.getString("api_key", "null");
		Log.d("api_key", userDetails.getString("api_key", "null"));
		Log.d("user_id", userDetails.getString("user_id", "null"));
		createTicket = (Button) findViewById(R.id.createTicket);

		createTicket.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(TicketActivity.this,
						CreateTicketActivity.class);
				startActivity(i);
			}
		});

		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View itemClicked,
					int position, long id) {
				TicketListElement tle = ticketListElement.get(position);
				String ticket_id = tle.getTicketId();
				Log.d("Ticket_id", ticket_id);
				Intent i = new Intent(TicketActivity.this,
						TicketMessageActivity.class);
				i.putExtra("TICKET_ID", ticket_id);
				startActivity(i);
			}

		});

		new LoadResponse().execute();

	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(TicketActivity.this, MainScreenActivity.class);
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

	class LoadResponse extends AsyncTask<Void, Void, List<TicketListElement>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(TicketActivity.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected List<TicketListElement> doInBackground(Void... params) {
			String result = null;
			ticketListElement = new ArrayList<TicketListElement>();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"get_tickets/"
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
					String ticket_id = jo.getString("ticket_id");
					String time = jo.getString("time_edited");
					String subject = jo.getString("subject");
					Calendar mydate = Calendar.getInstance();
					mydate.setTimeInMillis(Long.valueOf(time) * 1000);
					SimpleDateFormat format1 = new SimpleDateFormat(
							"yyyy-MM-dd");
					String formatted = format1.format(mydate.getTime());
					Log.d("MIF", subject);
					TicketListElement nale = new TicketListElement(ticket_id,
							subject, formatted);
					ticketListElement.add(nale);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ticketListElement;

		}

		@Override
		protected void onPostExecute(List<TicketListElement> ticketListElement) {
			super.onPostExecute(ticketListElement);

			ticketListAdapter = new TicketListAdapter(TicketActivity.this, 0,
					ticketListElement);
			final ListView listIn = (ListView) findViewById(R.id.listView1);
			if (ticketListElement.size() == 0) {
				setContentView(R.layout.no_tickets);
				// getActionBar().hide();
				dialog.dismiss();
			} else {
				listIn.setAdapter(ticketListAdapter);
				dialog.dismiss();

			}

		}
	}

}
