package com.paladin.appup.adapters;

public class NewAppListElement {

	final String offer_id;
	final String image;
	final String name;
	final String price;
	final String appUrl;
	final String description;

	public NewAppListElement(String offer_id, String image, String name,
			String price, String appUrl, String description) {
		this.offer_id = offer_id;
		this.image = image;
		this.name = name;
		this.price = price;
		this.appUrl = appUrl;
		this.description = description;

	}

	public String getOfferId() {
		return offer_id;
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
