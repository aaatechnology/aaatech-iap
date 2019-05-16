package com.aaatech.iap;

import com.aaatech.iap.enums.Operation;
import com.aaatech.iap.enums.ProductType;
import com.aaatech.iap.enums.Response;
import com.aaatech.iap.play.util.IabResult;
import com.aaatech.iap.vo.AvailableItem;
import com.aaatech.iap.vo.PurchaseItem;
import com.aaatech.iap.vo.PurchasedItem;
import com.amazon.device.iap.model.UserDataResponse;

/**
 * Common IAP Listener for all IAP operation.
 *
 */
public interface CommonIAPListener {

	// for available IAP list
	//void onIAPListPresent(List<ItemVO> itemList);
	void onIAPListPresent(Response response, AvailableItem item);

	// for purchase
	void onPurchaseSuccess(Response response, ProductType productType, PurchaseItem item);
	void onPurchaseCanceled(String message);

	// for purchased IAP
	void onPurchsedListPresent(Response response, PurchasedItem item);

	// common error
	void onError(Operation operation, Response response, String message);

	void onUserDataResponse(UserDataResponse user);

	/**
	 * When the setup is finished then this will be called <br/>
	 * This is common for <code>Google Play</code> and <code>Blackberry</code> App store.
	 * @param result
	 * 
	 * @see IabResult
	 */
	void OnIabSetupFinished(IabResult result);
}