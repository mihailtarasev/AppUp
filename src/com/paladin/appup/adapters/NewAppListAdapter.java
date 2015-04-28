package com.paladin.appup.adapters;


import java.util.List;

import com.paladin.appup.*;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.paladin.appup.DescriptionActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class NewAppListAdapter extends ArrayAdapter<NewAppListElement> {

	public NewAppListAdapter(Context context, int textViewResourceId,
			List<NewAppListElement> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = new NewAppElementView(getContext());

		((NewAppElementView) convertView).showLayout(getItem(position));
		return convertView;

	}

	class NewAppElementView extends FrameLayout {

		private final View usualLayout;
		private final ImageView appIcon;
		private final TextView appName;
		private final TextView appPrice;
		private final ImageView downloadButton;
		private final TextView appDescription;

		public NewAppElementView(final Context context) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			usualLayout = inflater.inflate(R.layout.new_apps_element, null);
			addView(usualLayout);

			appIcon = (ImageView) usualLayout.findViewById(R.id.app_icon);
			downloadButton = (ImageView) usualLayout
					.findViewById(R.id.imageView1);
			appName = (TextView) usualLayout.findViewById(R.id.textView1);
			appPrice = (TextView) usualLayout.findViewById(R.id.appPrice);
			appDescription = (TextView) usualLayout.findViewById(R.id.button1);

		}

		public void showLayout(final NewAppListElement element) {
			appName.setText(element.getName());
			appPrice.setText("+" + element.getPrice());
			// Add Domen
			UrlImageViewHelper.setUrlDrawable(appIcon,
					"http://my.appup.me/offers/" + element.getImage());

		}

	}

}
