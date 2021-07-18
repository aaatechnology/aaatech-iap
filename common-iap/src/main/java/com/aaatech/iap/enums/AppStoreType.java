package com.aaatech.iap.enums;

/**
 * Type of Appstores available.
 * 
 * <li> {@link #AMAZON_STORE} </li>
 * <li> {@link #PLAY_STORE} </li>
 */
public enum AppStoreType {

	/**
	 * Google Play AppStore
	 */
	PLAY_STORE,

	/**
	 * Amazon AppStore
	 */
	AMAZON_STORE;

	public String toString() {
		String string = "";
		switch (this) {
		case AMAZON_STORE:
			string = "Amazon AppStore";
			break;

		case PLAY_STORE:
			string = "Google Play AppStore";
			break;
		}

		return string;
	};
}
