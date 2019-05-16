package com.aaatech.iap.enums;

/**
 * Samsung AppStore Mode
 *
 * <li> {@link #LIVE}
 * <li> {@link #TEST_SUCCESS}
 * <li> {@link #TEST_FAIL}
 */
public enum AppStoreMode {
	/**
	 * Real service mode
	 * for all platforms
	 * */
	LIVE(0),
	/**
	 * Developer mode for test. Always return failed result
	 * for <code>SAMSUNG</code> and <code>GOOGLE PLAY</code>.
	 */
	TEST_SUCCESS(1), 

	/**
	 * Developer mode for test. Always return failed result.
	 * For <code>SAMSUNG</code> and <code>GOOGLE PLAY</code>.
	 */
	TEST_FAIL(-1),
	
	/**
	 * Developer mode for test. Always return failed result.
	 * For <code>SAMSUNG</code> and <code>GOOGLE PLAY</code>.
	 * When you make an In-app Billing request with this product ID, Google Play responds as though the purchase 
	 * was refunded. Refunds cannot be initiated through Google Play's in-app billing service. Refunds must be 
	 * initiated by you (the merchant). After you process a refund request through your Google Wallet merchant 
	 * account, a refund message is sent to your application by Google Play. This occurs only when Google Play 
	 * gets notification from Google Wallet that a refund has been made. For more information about refunds.
	 * <br/><br/><b>see: </b> 
	 * <a href="https://developer.android.com/google/play/billing/v2/api.html#billing-action-notify">Handling 
	 * IN_APP_NOTIFY messages</a> and 
	 * <a href="http://support.google.com/googleplay/android-developer/bin/answer.py?hl=en&answer=1153485">
	 * In-app Billing Pricing</a>.
	 */
	TEST_ITEM_UNAVALABLE(-2),

	/**
	 * Developer mode for test. Always return failed result
	 * for <code>GOOGLE PLAY</code> only. <br/><br/>
	 * When you make an In-app Billing request with this product ID, 
	 * Google Play responds as though the item being purchased was not listed in your application's product list.
	 */
	TEST_REFUNDED(-3);
	
	public int code;
	AppStoreMode(int code){
		this.code = code;
	}
}
