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

public class TicketListAdapter extends ArrayAdapter<TicketListElement> {

	public TicketListAdapter(Context context, int textViewResourceId,
			List<TicketListElement> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = new TicketListElementView(getContext());

		((TicketListElementView) convertView).showLayout(getItem(position));

		return convertView;

	}

	class TicketListElementView extends FrameLayout {

		private final View usualLayout;
		private final TextView tvSubject;
		private final TextView tvDate;

		public TicketListElementView(final Context context) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			usualLayout = inflater.inflate(R.layout.tickets_list_element, null);
			addView(usualLayout);
			tvSubject = (TextView) usualLayout.findViewById(R.id.textView1);
			tvDate = (TextView) usualLayout.findViewById(R.id.textView2);

		}

		public void showLayout(final TicketListElement element) {
			tvSubject.setText(element.getSubject());
			tvDate.setText(element.getDate());
		}

	}

}
