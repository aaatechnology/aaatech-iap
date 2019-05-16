package com.aaatech.iap.enums;

/**
 * Type of Appstores available.
 * 
 * <li> {@link #AMAZON_STORE} </li>
 * <li> {@link #PLAY_STORE} </li>
 * <li> {@link #SAMSUNG_STORE} </li>
 * <li> {@link #BLACKBERRY_STORE} </li>
 */
public enum AppStoreType {
	/**
	 * Google Play AppStore
	 */
	PLAY_STORE,

	/**
	 * Amazon AppStore
	 */
	AMAZON_STORE, 

	/**
	 * Samsung AppStore
	 */
	SAMSUNG_STORE,
	
	/**
	 * Blackberry AppStore (Blackberry World)
	 */
	BLACKBERRY_STORE;

	public String toString() {
		String string = "";
		switch (this) {
		case AMAZON_STORE:
			string = "Amazon AppStore";
			break;

		case PLAY_STORE:
			string = "Google Play AppStore";
			break;

		case SAMSUNG_STORE:
			string = "Samsung AppStore";
			break;
			
		case BLACKBERRY_STORE:
			string = "Blackberry AppStore";
			break;
		}

		return string;
	};
}
