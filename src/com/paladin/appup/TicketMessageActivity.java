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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;
import com.paladin.appup.adapters.TicketMessageListAdapter;
import com.paladin.appup.adapters.TicketMessagesElement;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TicketMessageActivity extends Activity {

	EditText tvMessage;
	ImageButton sendMessage;

	private static List<TicketMessagesElement> TMListElement;
	private TicketMessageListAdapter TMListAdapter;
	ProgressDialog dialog;
	String ticket_id;

	String message;
	String user_id;

	SharedPreferences userDetails;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.ticket_messages);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		Log.d("user_id", userDetails.getString("user_id", "null"));

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		tvMessage = (EditText) findViewById(R.id.editText1);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.mess));

		ticket_id = getIntent().getStringExtra("TICKET_ID");
		new RefreshMessages().execute();

		sendMessage = (ImageButton) findViewById(R.id.imageButton1);
		sendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				message = tvMessage.getText().toString();
				new SendMessage().execute();
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(TicketMessageActivity.this, TicketActivity.class);
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

	class RefreshMessages extends
			AsyncTask<Void, Void, List<TicketMessagesElement>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(TicketMessageActivity.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected List<TicketMessagesElement> doInBackground(Void... params) {
			String result = null;
			TMListElement = new ArrayList<TicketMessagesElement>();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"get_mess_ticket/"
							+ ticket_id));
			try {
				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity resEntity = httpresponse.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				result = responseString;
				Log.d("MIF", responseString);
				JSONArray ja = new JSONArray(result);
				for (int i = ja.length() - 1; i >= 0; i--) {
					JSONObject jo = ja.getJSONObject(i);
					String user_id = jo.getString("user_id");
					String message = jo.getString("message");
					String time = jo.getString("time");
					Calendar mydate = Calendar.getInstance();
					mydate.setTimeInMillis(Long.valueOf(time) * 1000);
					SimpleDateFormat format1 = new SimpleDateFormat(
							"yyyy-MM-dd");
					String formatted = format1.format(mydate.getTime());
					TicketMessagesElement nale = new TicketMessagesElement(
							user_id, message, formatted);
					TMListElement.add(nale);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

			return TMListElement;

		}

		@Override
		protected void onPostExecute(List<TicketMessagesElement> TMListElement) {
			super.onPostExecute(TMListElement);

			TMListAdapter = new TicketMessageListAdapter(
					TicketMessageActivity.this, 0, TMListElement);
			final ListView listIn = (ListView) findViewById(R.id.listView1);
			if (TMListElement.size() == 0) {
				setContentView(R.layout.no_new_apps);
				getActionBar().hide();
				dialog.dismiss();
			} else {
				listIn.setAdapter(TMListAdapter);
				dialog.dismiss();
				tvMessage.setText("");
			}

		}
	}

	class SendMessage extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(TicketMessageActivity.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;

			JSONObject joPost = new JSONObject();
			try {
				joPost.put("ticket_id", ticket_id);
				joPost.put("user_id", user_id);
				joPost.put("message", message);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"set_mess_ticket"));
			try {
				StringEntity se = new StringEntity(joPost.toString(),
						HTTP.UTF_8);
				se.setContentType("application/json");
				httppost.setEntity(se);
				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity resEntity = httpresponse.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				result = responseString;
				Log.d("MIF_Message", result);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			Log.d("MIF_Message", result);
			new RefreshMessages().execute();

		}
	}

}
