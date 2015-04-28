package com.paladin.appup;

import com.parse.Parse;
import com.parse.PushService;

public class Application extends android.app.Application {

	public Application() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize the Parse SDK.
		Parse.initialize(this, getString(R.string.parse_key_one),getString(R.string.parse_key_two));

		// Specify an Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, MainActivity.class);
	}
}