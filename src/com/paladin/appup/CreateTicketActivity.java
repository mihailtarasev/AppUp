package com.paladin.appup;


import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.flurry.android.FlurryAgent;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateTicketActivity extends Activity {

	ProgressDialog dialog;

	EditText subject;
	EditText message;

	Spinner category;
	Spinner s_category;

	Button createTicket;

	String sSubject, sMessage, sCategory, sSCategory;

	String user_id;

	SharedPreferences userDetails;

	/*
	 * http://appup.me/user/api/set_ticket
	 * {"user_id":"12","category_id":"1","subject"
	 * :"subjectapi1","status":"1","priority":"1","message":"messageapi1"}
	 */

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.create_ticket);
		FlurryAgent.init(this, getString(R.string.flurry_agent));

		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.create_tickets));

		subject = (EditText) findViewById(R.id.tvTheme);
		message = (EditText) findViewById(R.id.tvMessage);

		category = (Spinner) findViewById(R.id.tvCategory);
		s_category = (Spinner) findViewById(R.id.tvSrochno);

		createTicket = (Button) findViewById(R.id.saveButton);

		createTicket.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sSubject = subject.getText().toString();
				sMessage = message.getText().toString();

				sCategory = String.valueOf(category.getSelectedItemPosition() + 1);
				sSCategory = String.valueOf(s_category
						.getSelectedItemPosition() + 1);
				new CreateTicket().execute();

			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(CreateTicketActivity.this, TicketActivity.class);
		startActivity(i);
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this,  getString(R.string.flurry_agent));
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	class CreateTicket extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(CreateTicketActivity.this);
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
				joPost.put("user_id", user_id);
				joPost.put("category_id", sCategory);
				joPost.put("subject", sSubject);
				joPost.put("status", "1");
				joPost.put("priority", sSCategory);
				joPost.put("message", sMessage);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create( getString(R.string.base_uri_api)+"ticket"));
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
			Intent i = new Intent(CreateTicketActivity.this,
					TicketActivity.class);
			startActivity(i);

		}
	}

}
