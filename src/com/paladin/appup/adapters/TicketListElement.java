package com.paladin.appup.adapters;

public class TicketListElement {
	final String ticket_id;
	final String subject;
	final String date;

	public TicketListElement(String ticket_id, String subject, String date) {
		this.ticket_id = ticket_id;
		this.subject = subject;
		this.date = date;

	}

	public String getTicketId() {
		return ticket_id;
	}

	public String getSubject() {
		return subject;
	}

	public String getDate() {
		return date;
	}
}
