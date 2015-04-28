package com.paladin.appup.adapters;


import java.util.List;
import com.paladin.appup.*;

import com.paladin.appup.DescriptionActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class HistoryListAdapter extends ArrayAdapter<HistoryListElement> {

	public HistoryListAdapter(Context context, int textViewResourceId,
			List<HistoryListElement> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = new HistoryListElementView(getContext());

		((HistoryListElementView) convertView).showLayout(getItem(position));

		/*
		 * convertView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * 
		 * } });
		 */

		return convertView;

	}

	class HistoryListElementView extends FrameLayout {

		private final View usualLayout;
		private final TextView appDescription;
		private final TextView appPrice;

		public HistoryListElementView(final Context context) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			usualLayout = inflater.inflate(R.layout.default_history_element,
					null);
			addView(usualLayout);
			appDescription = (TextView) usualLayout
					.findViewById(R.id.tvDescription);
			appPrice = (TextView) usualLayout.findViewById(R.id.tvPrice);

		}

		public void showLayout(final HistoryListElement element) {
			appDescription.setText(element.getName());
			appPrice.setText(element.getPrice());

		}

	}

}
