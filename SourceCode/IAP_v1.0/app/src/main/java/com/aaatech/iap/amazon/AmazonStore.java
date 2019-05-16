package com.aaatech.iap.amazon;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aaatech.iap.BaseAppStore;
import com.aaatech.iap.CommonIAPListener;
import com.aaatech.iap.enums.Operation;
import com.aaatech.iap.enums.Response;
import com.aaatech.iap.vo.AvailableItem;
import com.aaatech.iap.vo.PurchaseItem;
import com.aaatech.iap.vo.PurchasedItem;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AmazonStore used to buy digital goods from Amazon AppStore.
 * <br/><br/>
 * <b>Permission:</b><br/>
 * &lt;receiver android:name="com.amazon.inapp.purchasing.ResponseReceiver" ><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;intent-filter><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;action<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * android:name="com.amazon.inapp.purchasing.NOTIFY"<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * android:permission="com.amazon.inapp.purchasing.Permission.NOTIFY" /><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/intent-filter><br/>
 * &lt;/receiver>
 * <br/> <br/>
 * Above permission(s) is/are need to add inside the &lt;application> tag.
 * @see <a href="https://developer.amazon.com/sdk/in-app-purchasing.html">Amazon In-App Purchasing API</a>
 * @see <a href="https://developer.amazon.com/sdk/in-app-purchasing/documentation/flowchart.html">In-App Purchasing API Flowchart</a>
 *
 */
public class AmazonStore extends BaseAppStore {

	final static String TAG = AmazonStore.class.getSimpleName();

	private String purchaseSKU;

	public AmazonStore(Context context, CommonIAPListener listener) {
		setIAPListener(listener);

		//PurchasingManager.registerObserver(new AmazonPurchaseObserver(context));
		PurchasingService.registerListener(context, new AmanzonPurchaseListener());
		PurchasingService.getUserData();

		printI(TAG, "Amazon Store instance created");
	}

	@Override
	protected void enableDebugMode(boolean mode) {}

	@Override
	public void getAvailableItems(List<String> list, String itemGroupId, com.aaatech.iap.enums.ProductType productType) {
		if (list == null || list.size() == 0) {
			if (mIAPListener != null) {
				mIAPListener.onError(Operation.GET_AVAILABLE_ITEM, Response.NULL_OR_EMPTY_INPUT, "SKU set is " + (list == null ? "null" : "empty"));
			}

			return;
		} 

		//PurchasingManager.initiateItemDataRequest(new HashSet<>(list));
		PurchasingService.getProductData(new HashSet<>(list));
	}


	@Override
	synchronized public void purchase(com.aaatech.iap.enums.ProductType productType, String sku, String payload, String itemGroupId, String itemId, boolean consumePurchase) {
		printI(TAG, "Purchase " + productType + " Item");
		if (TextUtils.isEmpty(sku)) {
			printE(TAG, "SKU is Empty");
			if (mIAPListener != null) {
				mIAPListener.onError(Operation.PURCHASE, Response.NULL_OR_EMPTY_INPUT, "SKU is null");
			}

			return;
		}

		this.productType = productType;
		this.purchaseSKU = sku;
		//String requestId = PurchasingManager.initiatePurchaseRequest(sku);
		RequestId requestId = PurchasingService.purchase(sku);
		printI(TAG, "Purchase request started for SKU: " + sku + " with RequestId :" + requestId);
	}

	@Override
	public void getPurchasedItems(String itemGroupId) {
		//PurchasingManager.initiatePurchaseUpdatesRequest(Offset.BEGINNING);
		PurchasingService.getPurchaseUpdates(true);
	}

	private class AmanzonPurchaseListener implements PurchasingListener {
		/**
		 * This is the callback for {@link PurchasingService#getUserData}. For
		 * successful case, get the current user from {@link UserDataResponse} and
		 * user and related purchase information
		 * 
		 * @param response
		 */
		@Override
		public void onUserDataResponse(final UserDataResponse response) {
			printD(TAG, "onGetUserDataResponse: requestId (" + response.getRequestId()
					+ ") userIdRequestStatus: "
					+ response.getRequestStatus()
					+ ")");

			final UserDataResponse.RequestStatus status = response.getRequestStatus();
			switch (status) {
			case SUCCESSFUL:
				printD(TAG, "onUserDataResponse: get user id (" + response.getUserData().getUserId()
						+ ", marketplace ("
						+ response.getUserData().getMarketplace()
						+ ") ");
				//iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
				break;

			case FAILED:
			case NOT_SUPPORTED:
				printD(TAG, "onUserDataResponse failed, status code is " + status);
				//iapManager.setAmazonUserId(null, null);
				break;
			}
			if (mIAPListener != null) {
				mIAPListener.onUserDataResponse(response);
			}
		}

		/**
		 * This is the callback for {@link PurchasingService#getProductData}. After
		 * SDK sends the product details and availability to this method, it will
		 * status accordingly.
		 */
		@Override
		public void onProductDataResponse(final ProductDataResponse response) {
			final ProductDataResponse.RequestStatus status = response.getRequestStatus();
			printD(TAG, "onProductDataResponse: RequestStatus (" + status + ")");

			switch (status) {
			case SUCCESSFUL:
				printD(TAG, "onProductDataResponse: successful.  The item data map in this response includes the valid SKUs");
				final Set<String> unavailableSkus = response.getUnavailableSkus();
				printD(TAG, "onProductDataResponse: " + unavailableSkus.size() + " unavailable skus");
				//iapManager.enablePurchaseForSkus(response.getProductData());
				//iapManager.disablePurchaseForSkus(response.getUnavailableSkus());
				//iapManager.refreshLevel2Availability();
				if (mIAPListener != null) {
					ArrayList<Product> list = new ArrayList<Product>();
					try {
						Map<String, Product> map = response.getProductData();
						if (map != null)
							list.addAll(map.values());
					} catch (Exception e) {
						printE(TAG, e.getMessage());
					}
					mIAPListener.onIAPListPresent(Response.SUCCESSFUL, new AvailableItem(list));
				}
				break;

			case FAILED:
			case NOT_SUPPORTED:
				printD(TAG, "onProductDataResponse: failed, should retry request");
				//iapManager.disableAllPurchases();
				if (mIAPListener != null) {
					mIAPListener.onError(Operation.GET_AVAILABLE_ITEM, Response.FAILED, response == null ? "Error" : response.toString());
				}
				break;
			}
		}

		/**
		 * This is the callback for {@link PurchasingService#getPurchaseUpdates}.
		 * 
		 * You will receive Entitlement receipts from this callback.
		 * 
		 */
		@Override
		public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse response) {
			printD(TAG, "onPurchaseUpdatesResponse: requestId (" + response.getRequestId()
					+ ") purchaseUpdatesResponseStatus ("
					+ response.getRequestStatus()
					+ ") userId ("
					+ response.getUserData().getUserId()
					+ ")");
			final PurchaseUpdatesResponse.RequestStatus status = response.getRequestStatus();
			switch (status) {
			case SUCCESSFUL:

				if (isDebugMode) {
					//iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
					for (final Receipt receipt : response.getReceipts()) {
						//iapManager.handleReceipt(response.getRequestId().toString(), receipt, response.getUserData());
						printD(TAG, receipt.getReceiptId());
					}
				}
				if (response.hasMore()) {
					PurchasingService.getPurchaseUpdates(false);
				}
				//iapManager.refreshLevel2Availability();
				if (mIAPListener != null) {
					mIAPListener.onPurchsedListPresent(Response.SUCCESSFUL, new PurchasedItem(response));
				}
				break;

			case FAILED:
			case NOT_SUPPORTED:
				Log.d(TAG, "onProductDataResponse: failed, should retry request");
				//iapManager.disableAllPurchases();
				if (mIAPListener != null) {
					mIAPListener.onError(Operation.GET_PURCHASED_ITEM, Response.FAILED, status != null ? status.name() : "Error");
				}
				break;
			}
		}

		/**
		 * This is the callback for {@link PurchasingService#purchase}. For each
		 * time the application sends a purchase request
		 * {@link PurchasingService#purchase}, Amazon Appstore will call this
		 * callback when the purchase request is completed. If the RequestStatus is
		 * Successful or AlreadyPurchased then application needs to call
		 * fulfillment. If the RequestStatus is INVALID_SKU, NOT_SUPPORTED, or
		 */
		@Override
		public void onPurchaseResponse(final PurchaseResponse response) {
			final String requestId = response.getRequestId().toString();
			final String userId = response.getUserData().getUserId();
			final PurchaseResponse.RequestStatus status = response.getRequestStatus();
			printD(TAG, "onPurchaseResponse: requestId (" + requestId + ") userId ("
					+ userId + ") purchaseRequestStatus (" + status + ")");

			switch (status) {
			case SUCCESSFUL:
				final Receipt receipt = response.getReceipt();
				/*iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
				Log.d(TAG, "onPurchaseResponse: receipt json:" + receipt.toJSON());
				iapManager.handleReceipt(response.getRequestId().toString(), receipt, response.getUserData());
				iapManager.refreshLevel2Availability();*/
				if (receipt.getSku().equals(purchaseSKU)){
					if (mIAPListener != null) {
						mIAPListener.onPurchaseSuccess(Response.SUCCESSFUL, com.aaatech.iap.enums.ProductType.getProductType(receipt.getProductType()), new PurchaseItem(response));
						if (receipt.getProductType() == ProductType.CONSUMABLE)
							PurchasingService.notifyFulfillment(receipt.getReceiptId(), FulfillmentResult.FULFILLED);
					}
				} else {
					if (mIAPListener != null) {
						mIAPListener.onError(Operation.PURCHASE, Response.SUCCESSFUL_WITH_UNAVAILABLE_SKUS, status.name());
						//PurchasingService.notifyFulfillment(receipt.getReceiptId(), FulfillmentResult.UNAVAILABLE);
					}
				}

				break;

			case ALREADY_PURCHASED:
				printI(TAG, "onPurchaseResponse: already purchased, you should verify the entitlement purchase on your side and make sure the purchase was granted to customer");
				if (mIAPListener != null) {
					mIAPListener.onPurchaseSuccess(Response.ALREADY_PURCHASED, productType, new PurchaseItem(response));
				}
				break;

			case INVALID_SKU:
				printD(TAG,
						"onPurchaseResponse: invalid SKU!  onProductDataResponse should have disabled buy button already.");
				//final Set<String> unavailableSkus = new HashSet<String>();
				//unavailableSkus.add(response.getReceipt().getSku());
				//iapManager.disablePurchaseForSkus(unavailableSkus);
				if (mIAPListener != null) {
					mIAPListener.onError(Operation.PURCHASE, Response.INVALID_SKU, status.name());
				}
				break;

			case FAILED:
			case NOT_SUPPORTED:
				printD(TAG, "onPurchaseResponse: failed so remove purchase request from local storage");
				//iapManager.purchaseFailed(response.getReceipt().getSku());
				if (mIAPListener != null) {
					mIAPListener.onError(Operation.PURCHASE, Response.FAILED, status.name());
				}
				break;
			}
		}
	}

	/*
	 * SAMPLES

	 PURCHASE RESPONSE:
	 CONSUMABLE :
	 -----------------------------
	 SUCESS:

	 Purchase Response: (com.amazon.inapp.purchasing.PurchaseResponse@405fe720, 
	 	requestId: "8c99e9d1-61ad-4bac-93ee-324b2754d9cf", 
	 	purchaseRequestStatus: "SUCCESSFUL", 
	 	userId: "DefaultTestUser", 
	 	receipt: (com.amazon.inapp.purchasing.Receipt@405fe328, 
	 		sku: "com.aaatech.moodoscope.adfree",
	 		itemType: "CONSUMABLE", 
	 		subscriptionPeriod: null, 
	 		purchaseToken: "eyJ0eXBlIjoiQ09OU1VNQUJMRSIsInNrdSI6ImNvbS54bGFiei5tb29kb3Njb3BlLmFkZnJlZSJ9"
	 	)
	 )

	 NON-CONSIMABLE (ENTITLED)
	 -------------------------
	 ALREADY_ENTITLED:

	 Purchase Response: (com.amazon.inapp.purchasing.PurchaseResponse@40665250, 
		 requestId: "02fd8b47-b078-4686-a938-273188bb232f", 
		 purchaseRequestStatus: "ALREADY_ENTITLED", 
		 userId: "DefaultTestUser", 
		 receipt: null
	 )
	 */
}