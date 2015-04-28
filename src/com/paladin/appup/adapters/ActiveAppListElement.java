package com.paladin.appup.adapters;

public class ActiveAppListElement {
	final String image;
	final String name;
	final String price;
	final String appUrl;
	final String description;

	public ActiveAppListElement(String image, String name, String price,
			String appUrl, String description) {
		this.image = image;
		this.name = name;
		this.price = price;
		this.appUrl = appUrl;
		this.description = description;

	}

	public String getImage() {
		return image;
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

	public String getDescription() {
		return description;
	}
}
