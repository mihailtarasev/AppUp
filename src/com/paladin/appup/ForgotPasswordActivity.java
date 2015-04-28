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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ForgotPasswordActivity extends Activity {

	EditText email;
	Button sendEmail;

	String sEmail;

	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.forgot_password);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.replace_pass));

		email = (EditText) findViewById(R.id.editText1);
		sendEmail = (Button) findViewById(R.id.button1);

		sendEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sEmail = email.getText().toString();
				new GetPassword().execute();
			}
		});

	}
	@Override
	public void onBackPressed() {
		Intent i = new Intent(ForgotPasswordActivity.this, MainActivity.class);
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

	class GetPassword extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ForgotPasswordActivity.this);
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
				joPost.put("ticket_id", sEmail);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"recovery_password"));
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
			Intent i = new Intent(ForgotPasswordActivity.this,
					MainActivity.class);
			startActivity(i);

		}
	}

}
