package com.paladin.appup.adapters;

public class ReferalListElement {
	final String email;
	final String date;
	final String price;

	public ReferalListElement(String email, String date, String price) {
		this.email = email;
		this.date = date;
		this.price = price;

	}

	public String getEmail() {
		return email;
	}

	public String getPrice() {
		return price;
	}

	public String getDate() {
		return date;
	}
}
