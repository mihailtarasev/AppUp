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

public class TicketMessageListAdapter extends ArrayAdapter<TicketMessagesElement> {

	public TicketMessageListAdapter(Context context, int textViewResourceId,
			List<TicketMessagesElement> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null)
			convertView = new TMListElementView(getContext());

		((TMListElementView) convertView).showLayout(getItem(position));

		return convertView;

	}

	class TMListElementView extends FrameLayout {
		

		private final View usualLayout;
		private final TextView tvName;
		private final TextView tvMessage;
		private final TextView tvDate;

		public TMListElementView(final Context context) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			usualLayout = inflater.inflate(R.layout.ticket_messages_list_element, null);
			addView(usualLayout);
			tvName = (TextView) usualLayout.findViewById(R.id.tvName);
			tvDate = (TextView) usualLayout.findViewById(R.id.tvDate);
			tvMessage = (TextView) usualLayout.findViewById(R.id.tvMessage);

		}

		public void showLayout(final TicketMessagesElement element) {
			if(element.getUserId().equals("0")) {
				tvName.setText("Admin");
			} else {
				tvName.setText("Webmaster");
			}
			tvMessage.setText(element.getMessage());
			tvDate.setText(element.getDate());
		}

	}

}

