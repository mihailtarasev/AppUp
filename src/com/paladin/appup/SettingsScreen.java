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
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/*http://appup.me/user/api/settings/12/ac627ab1ccbdb6
 {"email":"mail@mail.ru", "country":"1", "phone":"905 344", "top_login":"new login", "pass":"12345"}*/

public class SettingsScreen extends Activity {

	ProgressDialog dialog;

	Button saveButton;
	Button exitButton;

	EditText email;
	EditText phoneNumber;
	EditText topLogin;
	EditText password;
	Spinner country;

	String sTopLogin;
	String sEmail;
	String sPhoneNumber;
	String sCountry;

	String user_id;
	String api_key;

	SharedPreferences userDetails;

	String editEmail;
	String editLogin;
	String editPassword;
	String editCountryCode;
	String editPhNumber;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.settings);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.settings));
		
		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		api_key = userDetails.getString("api_key", "null");

		sTopLogin = userDetails.getString("top_login", "null");
		sEmail = userDetails.getString("my_email", "null");
		sPhoneNumber = userDetails.getString("phone_number", "null");
		sCountry = userDetails.getString("my_country", "null");

		saveButton = (Button) findViewById(R.id.saveButton);
		exitButton = (Button) findViewById(R.id.exitButton);
		email = (EditText) findViewById(R.id.tvEmail);
		
		phoneNumber = (EditText) findViewById(R.id.tvPhNumber);
		phoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		
		
		topLogin = (EditText) findViewById(R.id.tvNickName);
		password = (EditText) findViewById(R.id.tvNewPassWord);

		country = (Spinner) findViewById(R.id.tvCountry);
		if (sCountry.equals("3")) {
			country.setSelection(0);
		} else if (sCountry.equals("4")) {
			country.setSelection(1);
		} else if (sCountry.equals("5")) {
			country.setSelection(2);
		} else if (sCountry.equals("9")) {
			country.setSelection(3);
		}

		email.setText(sEmail);
		phoneNumber.setText(sPhoneNumber);
		topLogin.setText(sTopLogin);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editEmail = email.getText().toString();
				editLogin = topLogin.getText().toString();
				editPhNumber = phoneNumber.getText().toString();
				editPassword = password.getText().toString();
				String spinnerText = country.getSelectedItem().toString();
				if (spinnerText.equals(getString(R.string.russia))) {
					editCountryCode = "3";
				} else if (spinnerText.equals(getString(R.string.ukraina))) {
					editCountryCode = "4";
				} else if (spinnerText.equals(getString(R.string.belorussia))) {
					editCountryCode = "5";
				} else if (spinnerText.equals(getString(R.string.kazahstan))) {
					editCountryCode = "9";
				}
				new SaveSettings().execute();
			}
		});

		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exit();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(SettingsScreen.this, MainScreenActivity.class);
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

	public void exit() {
		Editor edit = userDetails.edit();
		edit.clear();
		edit.putString("user_id", "null");
		edit.putString("top_login", "null");
		edit.putString("api_key", "null");
		edit.putString("phone_number", "null");
		edit.putString("my_bonus", "null");
		edit.putString("my_email", "null");
		edit.commit();
		Intent i = new Intent(SettingsScreen.this, MainActivity.class);
		startActivity(i);
		finish();
	}

	class SaveSettings extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(SettingsScreen.this);
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
				joPost.put("email", editEmail);
				joPost.put("country", editCountryCode);
				joPost.put("phone", editPhNumber);
				joPost.put("top_login", editLogin);
				joPost.put("pass", editPassword);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create(getString(R.string.base_uri_api)+"settings/" + user_id
							+ "/" + api_key));
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
			Toast.makeText(SettingsScreen.this, getString(R.string.successfully), 3000).show();
			Editor edit = userDetails.edit();
			edit.putString("top_login", editLogin);
			edit.putString("phone_number", editPhNumber);
			edit.putString("my_email", editEmail);
			edit.putString("my_country", editCountryCode);
			Intent i = new Intent(SettingsScreen.this, MainScreenActivity.class);
			startActivity(i);
			edit.commit();

		}
	}

}
