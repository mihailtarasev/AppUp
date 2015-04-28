package com.paladin.appup.adapters;


import java.util.List;

import com.paladin.appup.*;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ReferalListAdapter extends ArrayAdapter<ReferalListElement> {

	public ReferalListAdapter(Context context, int textViewResourceId,
			List<ReferalListElement> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = new ReferalListElementView(getContext());

		((ReferalListElementView) convertView).showLayout(getItem(position));

		return convertView;

	}

	class ReferalListElementView extends FrameLayout {

		private final View usualLayout;
		private final TextView tvEmail;
		private final TextView tvDate;
		private final TextView tvPrice;

		public ReferalListElementView(final Context context) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			usualLayout = inflater.inflate(R.layout.referal_list_element, null);
			addView(usualLayout);
			tvEmail = (TextView) usualLayout.findViewById(R.id.tvEmail);
			tvDate = (TextView) usualLayout.findViewById(R.id.tvDate);
			tvPrice = (TextView) usualLayout.findViewById(R.id.tvGoldCount);

		}

		public void showLayout(final ReferalListElement element) {
			if (element.getEmail().equals(null)
					|| element.getEmail().equals("null")) {
				tvEmail.setText(getResources().getString(R.string.war_not_found_email));
			} else {
				tvEmail.setText(element.getEmail());
			}
			tvDate.setText(element.getDate());
			tvPrice.setText(element.getPrice());
		}

	}

}
