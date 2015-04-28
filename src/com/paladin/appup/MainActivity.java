package com.paladin.appup;



import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flurry.android.FlurryAgent;
import com.viewpagerindicator.CirclePageIndicator;

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

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
//import android.support.v7.appcompat.*; //app.ActionBarActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView regButton;
	TextView fpButton;
	EditText email;
	EditText password;

	String sEmail;
	String sPassword;
	SharedPreferences userDetails;

	private ProgressDialog dialog;
	
    private class ImagePagerAdapter extends PagerAdapter {
        private final int[] mImages = new int[] {
                R.drawable.intro_logo,
                R.drawable.intro_apps,
                R.drawable.intro_money,
                R.drawable.intro_ref
        };
        
        private final String[] mTitle = new String[] {
                getString(R.string.intro_title_one),
                getString(R.string.intro_title_two),
                getString(R.string.intro_title_tree),
                getString(R.string.intro_title_four)
        };
        
        private final String[] mText = new String[] {
        		getString(R.string.intro_text_one),
        		getString(R.string.intro_text_two),
        		getString(R.string.intro_text_tree),
        		getString(R.string.intro_text_four)
        };

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            ((ViewPager) container).removeView((LinearLayout) object);
        }

        @Override
        public int getCount() {
            return this.mImages.length;
        }
        
        TextView intro_text;
        TextView intro_title;
        LayoutInflater inflater;
        

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final Context context = MainActivity.this;
            
            final int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_medium);
            
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.intro_view, container,
                    false);
            
            final ImageView imageView = (ImageView) itemView.findViewById(R.id.intro_image);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(this.mImages[position]);
            
            
            intro_title = (TextView) itemView.findViewById(R.id.intro_title);
            intro_title.setText(mTitle[position]);
            
            intro_text = (TextView) itemView.findViewById(R.id.intro_text);
            intro_text.setText(mText[position]);
            
            ((ViewPager) container).addView(itemView, 0);
            return itemView;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == ((LinearLayout) object);
        }
    }	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FlurryAgent.init(this, getString(R.string.flurry_agent));
		userDetails = MainActivity.this.getSharedPreferences("userdetails",
				MODE_PRIVATE);
		getActionBar().hide();
		Log.d("api_key", userDetails.getString("api_key", "null"));
		Log.d("user_id", userDetails.getString("user_id", "null"));
		
		setContentView(R.layout.activity_main);
		
		// Introduction
		
		boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
	    if (firstrun){
	    	
	    	InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    	imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	    	
			final LinearLayout containerIntro = (LinearLayout) findViewById(R.id.container_intro);
			containerIntro.setVisibility(View.VISIBLE);
			
			final Button buttonSkipIntro = (Button) findViewById(R.id.buttonSkipIntro);
			buttonSkipIntro.setClickable(true);
			buttonSkipIntro.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					containerIntro.setVisibility(View.INVISIBLE);
				}
			});

	        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
	        final ImagePagerAdapter adapter = new ImagePagerAdapter();
	        viewPager.setAdapter(adapter);

	        final CirclePageIndicator circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
	        circleIndicator.setStrokeColor(getResources().getColor(R.color.button_non_pressed_color));
	        circleIndicator.setFillColor(getResources().getColor(R.color.button_non_pressed_color)); // tochka
	        circleIndicator.setViewPager(viewPager);
	        
		    // Save the state
		    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
		        .edit()
		        .putBoolean("firstrun", false)
		        .commit();
	    }
		
		

        // Other
		
		
		if (userDetails.getString("api_key", "null").equals(null)
				|| userDetails.getString("api_key", "null").equals("null")) {
		} else {
			Intent i = new Intent(MainActivity.this, MainScreenActivity.class);
			startActivity(i);
		}

		email = (EditText) findViewById(R.id.tvEmail);
		password = (EditText) findViewById(R.id.tvPassword);

		regButton = (TextView) findViewById(R.id.textView2);
		regButton.setClickable(true);
		regButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						RegistrationActivity.class);
				startActivity(i);

			}
		});

		fpButton = (TextView) findViewById(R.id.textView1);
		fpButton.setClickable(true);
		fpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						ForgotPasswordActivity.class);
				startActivity(i);

			}
		});

		Button enter = (Button) findViewById(R.id.button1);
		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sEmail = email.getText().toString();
				sPassword = password.getText().toString();
				if (isValidEmail(sEmail)) {
					if (sPassword.length() >= 3) {
						new LoadResponse().execute();
					} else {
						Alerts.showAlert(getString(R.string.war_pass_not_shot_free_symbol),
								MainActivity.this, getString(R.string.appup_error));
					}
				} else {
					Alerts.showAlert(getString(R.string.war_not_correct_email), MainActivity.this,
							getString(R.string.appup_error));
				}

				Log.d("MIF", "Hello");

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class LoadResponse extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			dialog = new ProgressDialog(MainActivity.this);
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
					URI.create("http://appup.me/user/api/auth"));
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
				Toast.makeText(MainActivity.this,
						getString(R.string.war_auth), 3000).show();

			} else {
				setContentView(R.layout.activity_main);
				dialog.dismiss();
				Intent i = new Intent(MainActivity.this,
						MainScreenActivity.class);
				MainActivity.this.finish();
				startActivity(i);

			}

		}
	}
}
