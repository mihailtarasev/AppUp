package com.paladin.appup.adapters;

public class HistoryListElement {
	final String name;
	final String price;
	final String appUrl;

	public HistoryListElement(String name, String price, String appUrl) {
		this.name = name;
		this.price = price;
		this.appUrl = appUrl;

	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}

	public String getAppUrl() {
		return appUrl;
	}
}
