package com.paladin.appup;

import com.flurry.android.FlurryAgent;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;
import com.vk.sdk.dialogs.VKShareDialog;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DescriptionActivity extends FragmentActivity {

	ImageView appIcon;
	ImageView shareVk;
	ImageView downloadButton;
	TextView tvPrice;
	TextView description;
	TextView tvDescription;

	SharedPreferences userDetails;

	String user_id;
	String api_key;
	String offer_id;
	String app_name;
	String uuid;
	String app_url;
	
	int shema;

	//vk share
		private static String sTokenKey = "165FD74620D14AF2D74763E9FE7C56FC3890FE41";
	    private static String[] sMyScope = new String[]{VKScope.WALL, VKScope.NOHTTPS};
		
	
	@Override
	public void onCreate(Bundle si) {
		super.onCreate(si);
		
		VKUIHelper.onCreate(DescriptionActivity.this);
		
		
		
		setContentView(R.layout.full_info_activity);
		
		VKSdk.initialize(sdkListenerDesc, getString(R.string.vk_api_id));
		
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.ab_back);

		userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
		user_id = userDetails.getString("user_id", "null");
		api_key = userDetails.getString("api_key", "null");
		Log.d("api_key", userDetails.getString("api_key", "null"));
		Log.d("user_id", userDetails.getString("user_id", "null"));

		String url = getIntent().getExtras().getString("image");
		app_name = getIntent().getExtras().getString("name");
		offer_id = getIntent().getExtras().getString("offer_id");
		String price = getIntent().getExtras().getString("price");
		String sDescription = getIntent().getExtras().getString("description");
		app_url = getIntent().getExtras().getString("app_url");
		
		shema = getIntent().getExtras().getInt("shema");
		
		TextView tvTitle = (TextView) getActionBar().getCustomView()
				.findViewById(R.id.tvTitle);
		tvTitle.setText(app_name);
		appIcon = (ImageView) findViewById(R.id.appIcon);
		shareVk = (ImageView) findViewById(R.id.share_vk);
		shareVk.setClickable(true);
		shareVk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//VKSdk.initialize(sdkListenerDesc, "4749543");
				
				 if (VKSdk.isLoggedIn()) {
					 new VKShareDialog()
				        .setText(getString(R.string.help_me_upload_app_for_link))
				        .setUploadedPhotos(null)
				        .setAttachmentImages(null)
				        .setAttachmentLink(app_name, "http://appup.me")
				        .setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
				            public void onVkShareComplete(int postId) {
				            	Toast.makeText(getApplicationContext(), getString(R.string.success_add_text), Toast.LENGTH_LONG).show();
				            }
				            public void onVkShareCancel() {}
				        }).show(getSupportFragmentManager(), getString(R.string.share));
					 
					 
				 } else {
					 VKSdk.authorize(sMyScope);
				 }
				
			}
		});
		downloadButton = (ImageView) findViewById(R.id.downloadApp);
		tvPrice = (TextView) findViewById(R.id.appPrice);
		description = (TextView) findViewById(R.id.appDescription);
		tvDescription = (TextView) findViewById(R.id.tvDescription);

		UrlImageViewHelper.setUrlDrawable(appIcon, "http://my.appup.me/offers/"
				+ url);

		tvPrice.setText("+" + price);
		description.setText(app_name);
		tvDescription.setText(sDescription);

		downloadButton.setClickable(true);
		downloadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://appup.me/load/"+user_id+"/"+offer_id));
				startActivity(browserIntent);
				
				
				//new GenerateTracking().execute();
			}
		});

		//new LogLoad().execute();

	}

	@Override
	public void onBackPressed() {
		Intent i;
		
		if(shema==1)
			i = new Intent(DescriptionActivity.this, NewAppActivity .class);
		else
			 i = new Intent(DescriptionActivity.this, ActiveAppActivity .class);
		startActivity(i);
		
		finish();
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
	    VKUIHelper.onResume(DescriptionActivity.this);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    VKUIHelper.onDestroy(DescriptionActivity.this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    VKUIHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private VKSdkListener sdkListenerDesc = new VKSdkListener() {
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
            new AlertDialog.Builder(DescriptionActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(DescriptionActivity.this, sTokenKey);
            //Intent i = new Intent(MainScreenActivity.this, MainActivity.class);
            //startActivity(i);
            
			 new VKShareDialog()
		        .setText(getString(R.string.help_me_upload_app_for_link))
		        .setUploadedPhotos(null)
		        .setAttachmentImages(null)
		        .setAttachmentLink(app_name, "http://appup.me/user/api/generate_tracking/"
						+ user_id + "/" + api_key)
		        .setShareDialogListener(new VKShareDialog.VKShareDialogListener() {
		            public void onVkShareComplete(int postId) {
		            	Toast.makeText(getApplicationContext(), getString(R.string.success_add_text), Toast.LENGTH_LONG).show();
		            }
		            public void onVkShareCancel() {}
		        }).show(getSupportFragmentManager(), getString(R.string.share));
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Intent i = new Intent(DescriptionActivity.this, MainActivity.class);
            startActivity(i);
        }
    };
    /*
	class GenerateTracking extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			dialog = new ProgressDialog(DescriptionActivity.this);
			dialog.setMessage("����������...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create("http://appup.me/user/api/generate_tracking/"
							+ user_id + "/" + api_key));
			JSONObject joPost = new JSONObject();
			try {
				joPost.put("offer_id", offer_id);
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
				JSONObject jo = new JSONObject(result);
				uuid = jo.getString("uuid");
				Log.d("MIF", responseString);
				Log.d("MIF1", joPost.toString());

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
			Log.d("MIF_GT", result);
			Log.d("MIF_GT", "finish");
			//new LogTracking().execute();

		}
	}

	class LogLoad extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(DescriptionActivity.this);
			dialog.setMessage("����������...");
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
					URI.create("http://appup.me/user/api/log_load/" + user_id
							+ "/" + api_key));
			JSONObject joPost = new JSONObject();
			// {\"user_id\":\"%d\", \"offer_name\":\"%@\",
			// \"agent_referrer\":\"%@\", \"ip_address\":\"%@\",
			// \"agent_string\":\"%@\"}
			try {
				joPost.put("user_id", user_id);
				joPost.put("offer_name", app_name);
				joPost.put("agent_referrer", "android");
				joPost.put("ip_address", "api_address");
				joPost.put("agent_string", "Device android");
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
			Log.d("MIF_logLoad", result);
			Log.d("MIF_logLoad", "finish");
			dialog.dismiss();
			// TODO Auto-generated catch block
		}
	}

	class LogTracking extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					URI.create("http://appup.me/user/api/log_tracking/"
							+ user_id + "/" + api_key));
			JSONObject joPost = new JSONObject();
			// {\"user_id\":\"%d\", \"offer_name\":\"%@\",
			// \"agent_referrer\":\"%@\", \"ip_address\":\"%@\",
			// \"agent_string\":\"%@\"}
			try {
				// joPost.put("user_id", user_id);
				joPost.put("offer_name", app_name);
				joPost.put("agent_referrer", "android");
				joPost.put("ip_address", uuid);
				joPost.put("agent_string", "Device android");
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
			Log.d("MIF_logT", result);
			Log.d("MIF_logT", "finish");
			Log.d("MIF_URL", app_url);
			Log.d("MIF_UUID", uuid);
			String new_app_url = app_url.replace("{uuid}", uuid);
			String last_app_url = new_app_url.replace("{user_id}", user_id);
			Log.d("MIF_NAURL", last_app_url);
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(last_app_url));
			dialog.dismiss();
			startActivity(i);
		}
	}
*/
}
