package com.paladin.appup;


import java.io.IOException;
import java.net.URI;

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
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;
import com.vk.sdk.dialogs.VKShareDialog;
import com.vk.sdk.util.VKUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends FragmentActivity {

	//vk share
	//public static String API_ID="4749543";
	private static String sTokenKey = "165FD74620D14AF2D74763E9FE7C56FC3890FE41";
    private static String[] sMyScope = new String[]{VKScope.WALL, VKScope.NOHTTPS};
	
	//other
	
	
	SharedPreferences userDetails;
	String user_id;
	String api_key;
	String my_bonus;

	int newAppCount;
	int activeAppCount;

	RelativeLayout newAppButton;
	RelativeLayout activeAppButton;
	RelativeLayout historyAppButton;
	RelativeLayout referalAppButton;
	RelativeLayout supportAppButton;
	RelativeLayout coinsAppButton;
	TextView newCount;
	TextView newActive;
	TextView myCoinsCount;

	ImageView settingsButton;
	ImageView vkButton;
	//ImageView fbButton;
	//ImageView twButton;

	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		
		VKUIHelper.onCreate(MainScreenActivity.this);
		
		setContentView(R.layout.main_screen);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		newAppCount = 0;
		activeAppCount = 0;
		getActionBar().hide();
		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		api_key = userDetails.getString("api_key", "null");
		my_bonus = userDetails.getString("my_bonus", "null");
		Log.d("api_key", userDetails.getString("api_key", "null"));
		Log.d("user_id", userDetails.getString("user_id", "null"));
		Log.d("my_bonus", userDetails.getString("my_bonus", "null"));
		initializeView();
		clickListeners();
		new LoadResponse().execute();
		
		VKSdk.initialize(sdkListener, getString(R.string.vk_api_id));
		
		String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
		Log.i("fingerprints", "fingerprints");
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			MainScreenActivity.this.finish();
			System.exit(0);
			return true;
		}

		return super.onKeyDown(keyCode, event);
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
	protected void onResume() {
	    super.onResume();
	    VKUIHelper.onResume(MainScreenActivity.this);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    VKUIHelper.onDestroy(MainScreenActivity.this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    VKUIHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	private VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(MainScreenActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(MainScreenActivity.this, sTokenKey);
            //Intent i = new Intent(MainScreenActivity.this, MainActivity.class);
            //startActivity(i);
            
			 new VKShareDialog()
		        .setText(getString(R.string.earning_on_uploading_free_mob_app))
		        .setUploadedPhotos(null)
		        .setAttachmentImages(null)
		        .setAttachmentLink("AppUp", "http://vk.com/appup")
		        .setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
		            public void onVkShareComplete(int postId) {
		            	Toast.makeText(getApplicationContext(), getString(R.string.note_add_success), Toast.LENGTH_LONG).show();
		            }
		            public void onVkShareCancel() {}
		        }).show(getSupportFragmentManager(), getString(R.string.share));
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Intent i = new Intent(MainScreenActivity.this, MainActivity.class);
            startActivity(i);
        }
    };
	

	public void initializeView() {
		newAppButton = (RelativeLayout) findViewById(R.id.newAppButton);
		activeAppButton = (RelativeLayout) findViewById(R.id.activeAppButton);
		historyAppButton = (RelativeLayout) findViewById(R.id.historyAppButton);
		referalAppButton = (RelativeLayout) findViewById(R.id.referalAppButton);
		supportAppButton = (RelativeLayout) findViewById(R.id.supportAppButton);
		coinsAppButton = (RelativeLayout) findViewById(R.id.coinsAppButton);

		newCount = (TextView) findViewById(R.id.new_count1);
		newActive = (TextView) findViewById(R.id.new_count2);
		myCoinsCount = (TextView) findViewById(R.id.myCoinsCount);
		myCoinsCount.setText(my_bonus);

		settingsButton = (ImageView) findViewById(R.id.imageView8);
		vkButton = (ImageView) findViewById(R.id.vk);
		//fbButton = (ImageView) findViewById(R.id.fb);
		//twButton = (ImageView) findViewById(R.id.tw);

		settingsButton.setClickable(true);
		vkButton.setClickable(true);
		//fbButton.setClickable(true);
		//twButton.setClickable(true);

	}

	public void clickListeners() {
		final MediaPlayer mp = MediaPlayer.create(this, R.raw.drop);
		newAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.start();
				Intent i = new Intent(MainScreenActivity.this,
						NewAppActivity.class);
				startActivity(i);

			}
		});

		activeAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.start();
				Intent i = new Intent(MainScreenActivity.this,
						ActiveAppActivity.class);
				startActivity(i);

			}
		});

		historyAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.start();
				Intent i = new Intent(MainScreenActivity.this,
						HistoryAppActivity.class);
				startActivity(i);

			}
		});

		referalAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.start();
				Intent i = new Intent(MainScreenActivity.this,
						ReferAppActivty.class);
				startActivity(i);

			}
		});

		supportAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.start();
				Intent i = new Intent(MainScreenActivity.this,
						TicketActivity.class);
				startActivity(i);

			}
		});

		coinsAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.start();
				Intent i = new Intent(MainScreenActivity.this,
						CashBackActivity.class);
				startActivity(i);

			}
		});

		vkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				VKSdk.initialize(sdkListener, getString(R.string.vk_api_id));
				
				 if (VKSdk.isLoggedIn()) {
					 new VKShareDialog()
				        .setText(getString(R.string.earning_on_uploading_free_mob_app))
				        .setUploadedPhotos(null)
				        .setAttachmentImages(null)
				        .setAttachmentLink("AppUp", "http://vk.com/appup")
				        .setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
				            public void onVkShareComplete(int postId) {
				            	Toast.makeText(getApplicationContext(), getString(R.string.note_add_success), Toast.LENGTH_LONG).show();
				            }
				            public void onVkShareCancel() {}
				        }).show(getSupportFragmentManager(), getString(R.string.share));
					 
					 
				 } else {
					 VKSdk.authorize(sMyScope);
				 }
			}
		});

		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainScreenActivity.this,
						SettingsScreen.class);
				MainScreenActivity.this.finish();
				startActivity(i);

			}
		});
	}

	public void shareViaIntent() {
		String message = getString(R.string.earning_on_uploading_free_mob_app);
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, message);
		startActivity(Intent.createChooser(share, getString(R.string.share)));
	}

	class LoadResponse extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainScreenActivity.this);
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
					URI.create("http://appup.me/user/api/get_offers/" + user_id
							+ "/" + api_key + "/5"));

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
					if (status.equals("0")) {
						newAppCount = newAppCount + 1;
					} else if (status.equals("1")) {
						activeAppCount = activeAppCount + 1;
					}
				}

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
			super.onPostExecute(result);
			Log.d("MIF", result);
			Log.d("MIF", "finish");
			newCount.setText(String.valueOf(newAppCount));
			newActive.setText(String.valueOf(activeAppCount));
			dialog.dismiss();
		}
	}

}
