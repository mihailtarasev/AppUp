package com.paladin.appup;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends Activity {

	ProgressDialog dialog;
	SharedPreferences userDetails;

	EditText email;
	EditText password;
	EditText phoneNumber;
	Button regButton;

	String sEmail;
	String sPassword;
	String sPhNumber;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.registration_layout);
		userDetails = RegistrationActivity.this.getSharedPreferences(
				"userdetails", MODE_PRIVATE);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.registration));

		email = (EditText) findViewById(R.id.tvEmail);
		password = (EditText) findViewById(R.id.tvPassword);
		phoneNumber = (EditText) findViewById(R.id.editText1);
		phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		regButton = (Button) findViewById(R.id.regButton);

		regButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sEmail = email.getText().toString();
				sPassword = password.getText().toString();
				sPhNumber = phoneNumber.getText().toString();
				if (isValidEmail(sEmail)) {
					if (sPassword.length() >= 3) {
						if (sPhNumber.length() >= 6) {
							new LoadResponse().execute();
						} else {
							Alerts.showAlert(
									getString(R.string.war_phone_not_shot_six_symbol),
									RegistrationActivity.this, getString(R.string.appup_error));

						}
					} else {
						Alerts.showAlert(getString(R.string.war_pass_not_shot_free_symbol),
								RegistrationActivity.this, getString(R.string.appup_error));
					}
				} else {
					Alerts.showAlert(getString(R.string.war_not_correct_email),
							RegistrationActivity.this, getString(R.string.appup_error));
				}

			}
		});
	}

	public boolean isValidEmail(String email) {
		boolean isValidEmail = false;

		String emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(emailExpression,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValidEmail = true;
		}
		return isValidEmail;
	}
	@Override
	public void onBackPressed() {
		Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
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

	class LoadResponse extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(RegistrationActivity.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"registration"));
			JSONObject joPost = new JSONObject();
			try {
				joPost.put("email", sEmail);
				joPost.put("pass", sPassword);
				joPost.put("phone", sPhNumber);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			try {

				StringEntity se = new StringEntity(joPost.toString(),
						HTTP.UTF_8);
				se.setContentType("application/json");
				httppost.setEntity(se);

				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity resEntity = httpresponse.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				result = responseString;
				Log.d("MIF", responseString);
				Log.d("MIF1", joPost.toString());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("MIF", result);
			Log.d("MIF", "finish");
			dialog.dismiss();
			reg_ok();
		}
	}

	class Auth extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			dialog = new ProgressDialog(RegistrationActivity.this);
			dialog.setMessage(getString(R.string.uploading));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"auth"));
			// httppost.headerIterator("Service-Method: CreateInvoice");
			JSONObject joPost = new JSONObject();
			try {
				joPost.put("email", sEmail);
				joPost.put("pass", sPassword);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {

				StringEntity se = new StringEntity(joPost.toString(),
						HTTP.UTF_8);
				se.setContentType("application/json");
				httppost.setEntity(se);

				HttpResponse httpresponse = httpclient.execute(httppost);
				HttpEntity resEntity = httpresponse.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				result = responseString;
				Log.d("MIF", responseString);
				Log.d("MIF1", joPost.toString());
				JSONObject jo = new JSONObject(result);
				String user_id = jo.getString("user_id");
				String top_login = jo.getString("top_login");
				String api_key = jo.getString("api_key");
				String phone_number = jo.getString("phone");
				String my_bonus = jo.getString("bonus");
				String my_email = jo.getString("email");
				String my_country = jo.getString("country");
				Editor edit = userDetails.edit();
				edit.clear();
				edit.putString("user_id", user_id);
				edit.putString("top_login", top_login);
				edit.putString("api_key", api_key);
				edit.putString("phone_number", phone_number);
				edit.putString("my_bonus", my_bonus);
				edit.putString("my_email", my_email);
				edit.putString("my_country", my_country);
				edit.commit();
				Log.d("user_id", user_id);
				Log.d("top_login", top_login);
				Log.d("api_key", api_key);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("MIF", result);
			Log.d("MIF", "finish");
			if (userDetails.getString("api_key", "null").equals(null)
					|| userDetails.getString("api_key", "null").equals("null")) {
				dialog.dismiss();
				Toast.makeText(RegistrationActivity.this,
						getString(R.string.war_auth), 3000).show();

			} else {
				setContentView(R.layout.activity_main);
				dialog.dismiss();
				Intent i = new Intent(RegistrationActivity.this,
						MainScreenActivity.class);
				startActivity(i);

			}

		}
	}

	public void reg_ok() {
		Toast.makeText(RegistrationActivity.this, getString(R.string.successfully), 3000).show();
		new Auth().execute();
	}

}
