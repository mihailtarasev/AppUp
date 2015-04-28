package com.paladin.appup.adapters;


public class TicketMessagesElement {
	final String user_id;
	final String message;
	final String date;

	public TicketMessagesElement(String user_id, String message, String date) {
		this.user_id = user_id;
		this.message = message;
		this.date = date;

	}

	public String getUserId() {
		return user_id;
	}

	public String getMessage() {
		return message;
	}

	public String getDate() {
		return date;
	}
}