package com.aaatech.iap.play;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aaatech.iap.BaseAppStore;
import com.aaatech.iap.CommonIAPListener;
import com.aaatech.iap.enums.AppStoreMode;
import com.aaatech.iap.enums.Operation;
import com.aaatech.iap.enums.ProductType;
import com.aaatech.iap.enums.Response;
import com.aaatech.iap.play.util.IabHelper;
import com.aaatech.iap.play.util.IabResult;
import com.aaatech.iap.play.util.Inventory;
import com.aaatech.iap.play.util.Purchase;
import com.aaatech.iap.utils.IAPUtil;
import com.aaatech.iap.vo.AvailableItem;
import com.aaatech.iap.vo.PurchaseItem;
import com.aaatech.iap.vo.PurchasedItem;

import java.util.HashMap;
import java.util.List;

/**
 * Playstore is used to make inapp purchase with Google Play Store
 * 
 * <br/><br/>
 * <b>Permission:</b><br/>
 * &lt;uses-permission android:name="com.android.vending.BILLING"/>
 * <br/> <br/>
 * Above permission(s) is/are need to add outside of the &lt;application> tag.
 * @author A.Arun
 *
 */
public class PlayStore extends BaseAppStore {

	final static String TAG = PlayStore.class.getSimpleName();

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	private Context mContext;

	private IabHelper mHelper;

	private boolean isReady = false;
	private ProgressDialog progressDialog;
	private HashMap<String, Object> pendingVars;
	private boolean isAvailable = false;
	private AppStoreMode storeMode;
	private boolean consumeAfterPurchase;
	public PlayStore(Context context, String base64_string, AppStoreMode storeMode, CommonIAPListener listener) {
		mContext = context;
		this.storeMode = storeMode;

		setIAPListener(listener);

		// Create the helper, passing it our context and the public key to verify signatures with
		printD(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(mContext, base64_string);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		printD(TAG, "Starting setup.");
		isAvailable = false;
		isReady = false;
		progressDialog = null;
		pendingVars = null;
		try {
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onIabSetupFinished(IabResult result) {
					printD(TAG, "Setup finished.");
					if (result != null && result.isSuccess()) {
						isAvailable = true;
						isReady = true;
					} else {
						showErrorAlert();
					}
					if (mIAPListener != null) {
						mIAPListener.OnIabSetupFinished(result);
					}

					// Pending tasks
					try {
						if (progressDialog != null) {
							progressDialog.dismiss();
							progressDialog = null;
						}
						if (pendingVars != null && !pendingVars.isEmpty()) {
							if (pendingVars.get("action") == "purchase") {
								purchase((ProductType) pendingVars.get("productType"), (String) pendingVars.get("sku"),
										(String) pendingVars.get("payload"), (boolean) pendingVars.get("consumePurchase"));
							} else if (pendingVars.get("action") == "getPurchasedItems") {
								getPurchasedItems();
							} else if (pendingVars.get("action") == "getAvailableItems") {
								getAvailableItems((List<String>) pendingVars.get("list"), null);
							}
						}
					} catch (IllegalStateException exception ) {
					} catch (Exception exception) {}
				}
			});
		} catch (Exception exception) {
			if (mIAPListener != null) {
				mIAPListener.OnIabSetupFinished(new IabResult(IabHelper.BILLING_RESPONSE_RESULT_ERROR, exception.getMessage()));
			}
		}
	}

	void showErrorAlert() {
		try {
			new AlertDialog.Builder(mContext)
			.setTitle("Google Play Services")
			.setMessage("Unable to connect to Google Play Services. Please ensure you have the latest version of Google Play and try again")
			.setPositiveButton("OK", null)
			.create()
			.show();
		} catch (Exception exception) {}
	}

	@Override
	protected boolean isAvailabble() {
		return mHelper != null;
	}

	protected void enableDebugMode(boolean mode) {
		// enable debug logging (for a production application, you should set this to false).
		if (mHelper != null)
			mHelper.enableDebugLogging(mode);
	};

	@Override
	public void getPurchasedItems() {
		try {
			if (progressDialog != null) progressDialog.dismiss();
			// Check availability
			if (!isAvailable) {
				showErrorAlert();
				return;
			} else if (!isReady) {
				// Check for is ready otherwise wait for initilize
				progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true, true);

				pendingVars = new HashMap<String, Object>();
				pendingVars.put("action", "getPurchasedItems");
				return;
			}
		} catch (Exception exception) {}

		mHelper.flagEndAsync();

		try {
			mHelper.queryInventoryAsync(mGotInventoryListener);
		} catch (Exception exception) {
			exception.printStackTrace();
			if (mIAPListener != null) {
				mIAPListener.onError(Operation.GET_PURCHASED_ITEM, Response.FAILED, "");
			}
		}
	}

	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			printD(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null) return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				if (mIAPListener != null) {
					mIAPListener.onError(Operation.GET_PURCHASED_ITEM, Response.FAILED, result.getMessage());
				}
				return;
			}

			printD(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */


			/*
            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            printD(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // Do we have the infinite gas plan?
            Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
            mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
                    verifyDeveloperPayload(infiniteGasPurchase));
            printD(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                        + " infinite gas subscription.");
            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                printD(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
                return;
            }
			 */
			printD(TAG, "Initial inventory query finished; enabling main UI.");

			try {
				IAPUtil.getPurchasedItemsAndSave(inventory);
			} catch (Exception e) {}

			if (mIAPListener != null) {
				mIAPListener.onPurchasedListPresent(Response.SUCCESSFUL, new PurchasedItem(inventory));
			}
		}
	};

	@Override
	public void getAvailableItems(List<String> list, ProductType productType) {
		try {
			// Check availability
			if (!isAvailable) {
				showErrorAlert();
				return;
			} else if (!isReady) {
				// Check for is ready otherwise wait for initilize
				progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true, true);

				pendingVars = new HashMap<String, Object>();
				pendingVars.put("action", "getAvailableItems");
				pendingVars.put("list", list);
				return;
			}
		} catch (Exception exception) {}

		mHelper.queryInventoryAsync(true, list, new IabHelper.QueryInventoryFinishedListener() {

			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				if (mIAPListener != null) {
					if (result.isFailure()) {
						mIAPListener.onError(Operation.GET_AVAILABLE_ITEM, Response.FAILED, result.getMessage());
						return;
					}

					mIAPListener.onIAPListPresent(Response.SUCCESSFUL, new AvailableItem(inv));
				}
			}
		});
	}

	@Override
	synchronized public void purchase(ProductType productType, String sku, String payload, boolean consumePurchase) {
		if (TextUtils.isEmpty(sku)) {
			if (mIAPListener != null) {
				mIAPListener.onError(Operation.PURCHASE, Response.NULL_OR_EMPTY_INPUT, "SKU is null");
			}

			return;
		}

		if (progressDialog != null) progressDialog.dismiss();
		try {
			// Check availability
			if (!isAvailable) {
				showErrorAlert();
				return;
			} else if (!isReady) {
				// Check for is ready otherwise wait for initilize
				progressDialog = ProgressDialog.show(mContext, "", "Please wait...", true, true);

				pendingVars = new HashMap<String, Object>();
				pendingVars.put("action", "purchase");
				pendingVars.put("productType", productType);
				pendingVars.put("sku", sku);
				pendingVars.put("payload", payload);
				pendingVars.put("consumePurchase", consumePurchase);
				return;
			}
		} catch (Exception exception) {}

		this.productType = productType;
		consumeAfterPurchase = consumePurchase;
		mHelper.flagEndAsync();
		try {
			switch (productType) {
			case SUBSCRIPTION:
				mHelper.launchSubscriptionPurchaseFlow((Activity) mContext, sku,
						RC_REQUEST, mPurchaseFinishedListener, payload);
				break;

			default:
				mHelper.launchPurchaseFlow((Activity) mContext, sku, 
						RC_REQUEST, mPurchaseFinishedListener, payload);
				break;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			if (mIAPListener != null) {
				mIAPListener.onError(Operation.PURCHASE, Response.FAILED, exception.getMessage());
			}
		}
	}

	/*private String getSKU(String sku) {
		switch (storeMode) {
		case LIVE:
			return sku;

		case TEST_SUCCESS:
			return "android.test.purchased";

		case TEST_FAIL:
			return "android.test.canceled";

		case TEST_ITEM_UNAVALABLE:
			return "android.test.refunded";

		case TEST_REFUNDED:
			return "android.test.item_unavailable";
		}

		return sku;
	}*/

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			printD(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;

			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				if (mIAPListener != null) {
					if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
						mIAPListener.onPurchaseSuccess(Response.ALREADY_PURCHASED, productType, new PurchaseItem(purchase));
					} else if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED) {
						mIAPListener.onPurchaseCanceled(result.getMessage());
					} else {
						mIAPListener.onError(Operation.PURCHASE, Response.FAILED, result.getMessage());
					}
				}
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				if (mIAPListener != null) {
					mIAPListener.onError(Operation.PURCHASE, Response.PURCHASE_VIRIFICATION_FAILED, "");
				}
				return;
			}

			printD(TAG, "Purchase successful.");

			if (consumeAfterPurchase && productType == ProductType.CONSUMABLE) {
				consumeThePurchase(purchase);
			} else {
				// save sku to purchased list
				IAPUtil.addToPurchasedList(purchase.getSku());
				if (mIAPListener != null) 
					mIAPListener.onPurchaseSuccess(Response.SUCCESSFUL, productType, new PurchaseItem(purchase));
			}
		}
	};

	@Override
	public void consumeThePurchase(Purchase purchase) {
		if (mHelper == null) return;

		if (purchase == null) {
			Operation.PURCHASE.setProductType(ProductType.CONSUMABLE);
			mIAPListener.onError(Operation.PURCHASE, Response.NULL_OR_EMPTY_INPUT, "");
			return;
		}

		mHelper.consumeAsync(purchase, mConsumeFinishedListener);
	}

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			printD(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;

			// We know this is the "gas" sku because it's the only one we consume,
			// so we don't check which sku was consumed. If you have more than one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in our
				// game world's logic, which in our case means filling the gas tank a bit
				printD(TAG, "Consumption successful. Provisioning.");
				if (mIAPListener != null) {
					mIAPListener.onPurchaseSuccess(Response.SUCCESSFUL, ProductType.CONSUMABLE, new PurchaseItem(purchase));
				}
			}
			else {
				complain("Error while consuming: " + result);
				if (mIAPListener != null) {
					Operation.PURCHASE.setProductType(ProductType.CONSUMABLE);
					mIAPListener.onError(Operation.PURCHASE, Response.FAILED, "");
				}
			}
			printD(TAG, "End consumption flow.");
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mHelper != null)
			mHelper.handleActivityResult(requestCode, resultCode, data);
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		// Note: Whether need to add developer playload or not?
		//String payload = p.getDeveloperPayload();
		return true;

		/*
		 * verify that the developer payload of the purchase is correct. It will be
		 * the same one that you sent when initiating the purchase.
		 *
		 * WARNING: Locally generating a random string when starting a purchase and
		 * verifying it here might seem like a good approach, but this will fail in the
		 * case where the user purchases an item on one device and then uses your app on
		 * a different device, because on the other device you will not have access to the
		 * random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 *
		 * 1. If two different users purchase an item, the payload is different between them,
		 *    so that one user's purchase can't be replayed to another user.
		 *
		 * 2. The payload must be such that you can verify it even when the app wasn't the
		 *    one who initiated the purchase flow (so that items purchased by the user on
		 *    one device work on other devices owned by the user).
		 *
		 * Using your own server to store and verify developer payloads across app
		 * installations is recommended.
		 */
	}



	void complain(String message) {
		printE(TAG, "**** TrivialDrive Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		if (showAlert) {
			AlertDialog.Builder bld = new AlertDialog.Builder(mContext);
			bld.setMessage(message);
			bld.setNeutralButton("OK", null);
			printD(TAG, "Showing alert dialog: " + message);
			bld.create().show();
		}
	}

	private boolean showAlert;
	@Override
	protected void showDefaultAlets(boolean showAlert) {
		this.showAlert = showAlert;
	}

	@Override
	protected void onDestroy() {
		// very important:
		printI(TAG, "Destroying helper.");

		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

}

