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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CashBackActivity extends FragmentActivity {

	SharedPreferences userDetails;

	String user_id;
	String api_key;
	String my_bonus;

	TextView tvBonus;
	Button cashQIWI;
	Button cashPhone;
	
	String commentPayment;
	
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		setContentView(R.layout.cash_back);
		FlurryAgent.init(this, getString(R.string.flurry_agent));

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.push_mouney));

		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		api_key = userDetails.getString("api_key", "null");
		my_bonus = userDetails.getString("my_bonus", "null");
		Log.d("api_key", userDetails.getString("api_key", "null"));
		Log.d("user_id", userDetails.getString("user_id", "null"));
		Log.d("my_bonus", userDetails.getString("my_bonus", "null"));

		tvBonus = (TextView) findViewById(R.id.textView1);
		cashQIWI = (Button) findViewById(R.id.button1);
		cashPhone = (Button) findViewById(R.id.button2);

		tvBonus.setText(my_bonus);

		cashQIWI.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Double.valueOf(my_bonus) <= 50) {
					Alerts.showAlert(getString(R.string.limit_mouney),
							CashBackActivity.this, getString(R.string.appup_error));
				}else {
					commentPayment = getString(R.string.push_qiwi_mouney);
					new LoadResponse().execute();
				}
			}
		});

		cashPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Double.valueOf(my_bonus) <= 50) {
					Alerts.showAlert(getString(R.string.limit_mouney),
							CashBackActivity.this, getString(R.string.appup_error));
				}else {
				    DialogFragment newFragment = new phoneDialogFragment();
				    newFragment.show(getSupportFragmentManager(), getString(R.string.input_phone_number));	
				}
			}
		});
	}
	
	
	public class phoneDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    // Get the layout inflater
		    LayoutInflater inflater = getActivity().getLayoutInflater();
	
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    builder.setView(inflater.inflate(R.layout.dialog_phone, null))
		    // Add action buttons
		           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		               @Override
		               public void onClick(DialogInterface dialog, int id) {
		            	   final EditText txtAccName = (EditText)  ((AlertDialog) dialog).findViewById(R.id.phone_number);
		            	   commentPayment = getString(R.string.push_phone_mouney)+ txtAccName.getText();
		            	   new LoadResponse().execute();
		               }
		           })
		           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	   phoneDialogFragment.this.getDialog().cancel();
		               }
		           });
		    
		    
		    final EditText txtAccName = (EditText)  ((AlertDialog) dialog).findViewById(R.id.phone_number);
		    txtAccName.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		    
		    return builder.create();
		}
	}
	
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(CashBackActivity.this, MainScreenActivity.class);
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

			super.onPreExecute();
			dialog = new ProgressDialog(CashBackActivity.this);
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
					URI.create(getString(R.string.base_uri_api)+"payments"));
			// httppost.headerIterator("Service-Method: CreateInvoice");
			JSONObject joPost = new JSONObject();
			try {
				joPost.put("user_id", user_id);
				joPost.put("status", 1);
				joPost.put("summa", my_bonus);
				joPost.put("comment", commentPayment);
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
				//String user_mouney = jo.getString("user_id");
				Editor edit = userDetails.edit();
				edit.putString("my_bonus", "0");
				edit.commit();

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

			Log.d("MIF", "finish");
				dialog.dismiss();
				Toast.makeText(CashBackActivity.this,
						getString(R.string.successfully), 3000).show();
		}
	}

}
