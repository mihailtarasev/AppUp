package com.paladin.appup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Alerts {
	static AlertDialog.Builder builder;
	static AlertDialog ad;

	public static void showAlert(String message, Context ctx, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		builder.setIcon(R.drawable.ic_launcher);
		EmptyListener pl = new EmptyListener();
		builder.setPositiveButton("OK", pl);
		builder.setMessage(message);
		AlertDialog ad = builder.create();
		ad.show();
	}

}

class EmptyListener implements OnClickListener {

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
