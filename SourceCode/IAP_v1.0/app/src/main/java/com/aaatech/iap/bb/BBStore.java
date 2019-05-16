package com.aaatech.iap.bb;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.aaatech.iap.BaseAppStore;
import com.aaatech.iap.CommonIAPListener;
import com.aaatech.iap.bb.util.BillingService;
import com.aaatech.iap.bb.util.BillingService.RequestPurchase;
import com.aaatech.iap.bb.util.BillingService.RestoreTransactions;
import com.aaatech.iap.bb.util.Consts;
import com.aaatech.iap.bb.util.Consts.PurchaseState;
import com.aaatech.iap.bb.util.Consts.ResponseCode;
import com.aaatech.iap.bb.util.PurchaseDatabase;
import com.aaatech.iap.bb.util.PurchaseObserver;
import com.aaatech.iap.bb.util.ResponseHandler;
import com.aaatech.iap.bb.vo.PurchaseVO;
import com.aaatech.iap.bb.vo.PurchasedResponse;
import com.aaatech.iap.enums.Operation;
import com.aaatech.iap.enums.ProductType;
import com.aaatech.iap.enums.Response;
import com.aaatech.iap.vo.PurchaseItem;
import com.aaatech.iap.vo.PurchasedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * AmazonStore used to buy digital goods from Amazon AppStore.
 * <br/><br/>
 * <b>Permission:</b><br/>
 * &lt;service android:name="com.aaatech.iap.bb.util.BillingService" />
 * <br/<br/><br/>
 * &lt;receiver android:name="com.aaatech.iap.bb.util.BillingReceiver" ><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;intent-filter><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &lt;action android:name="com.android.vending.billing.IN_APP_NOTIFY" /><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &lt;action android:name="com.android.vending.billing.RESPONSE_CODE" /><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * &lt;action android:name="com.android.vending.billing.PURCHASE_STATE_CHANGED" /><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/intent-filter><br/>
 * &lt;/receiver>
 * <br/> <br/>
 * Above permission(s) is/are need to add inside the &lt;application> tag.
 *
 * @author A.Arun
 */
public class BBStore extends BaseAppStore {

	final String TAG = BBStore.class.getSimpleName();

	private Context mContext;

	private BillingService mBillingService;

	private PurchaseDatabase mPurchaseDatabase;

	private final HashMap<String, Boolean> purchseList = new HashMap<String, Boolean>();

	public BBStore(Context context, CommonIAPListener listener) {
		setIAPListener(listener);
		mContext = context;

		printD(TAG, "Create service");
		mBillingService = new BillingService();
		mBillingService.setContext(mContext);
		mPurchaseDatabase = new PurchaseDatabase(mContext);

		ResponseHandler.register(new InAppPurchaseObserver(new Handler()));
	}

	@Override
	protected void enableDebugMode(boolean mode) {
		Consts.DEBUG = mode;
	}

	@Override
	public void getAvailableItems(List<String> list, String itemGroupId, ProductType productType) {}

	@Override
	synchronized public void purchase(ProductType productType, String sku, String payload, String itemGroupId, String itemId, boolean consumePurchase) {
		if (!mBillingService.checkBillingSupported(Consts.ITEM_TYPE_INAPP)) {
			if (mIAPListener != null) 
				mIAPListener.onError(Operation.PURCHASE, Response.BILLING_NOT_SUPPORTED, "Billing not supported");
		} else {
			if (mBillingService.requestPurchase(sku, Consts.ITEM_TYPE_INAPP, null)) {
				this.productType = productType;
				purchseList.put(sku, false);
			} else {
				if (mIAPListener != null) 
					mIAPListener.onError(Operation.PURCHASE, Response.BINDING_ERROR, "Unable to bind to MarketBillingService");
			}
		}
	}

	@Override
	public void getPurchasedItems(String itemGroupId) {
		SharedPreferences prefs = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE);
		if (prefs.getBoolean(Consts.DB_INITIALIZED, false)) {
			if (mIAPListener != null) 
				mIAPListener.onPurchsedListPresent(Response.SUCCESSFUL, new PurchasedItem(getPurchasedItemList()));
		} else {
			mBillingService.restoreTransactions();
		}
	}

	/**
	 * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
	 * messages to this application so that we can update the UI.
	 */
	private class InAppPurchaseObserver extends PurchaseObserver {

		public InAppPurchaseObserver(Handler mHandler) {
			super((Activity) mContext, mHandler);
		}


		@Override
		public void onBillingSupported(boolean supported, String type) {
			if (supported) {
				//restoreDatabase();
			} else {
				if (mIAPListener != null) 
					mIAPListener.onError(Operation.PURCHASE, Response.BILLING_NOT_SUPPORTED, "Billing not supported");
			}
		}

		@Override
		public void onPurchaseStateChange(PurchaseState purchaseState, String itemId, int quantity,
				long purchaseTime,	String developerPayload) {
			printI(TAG, "ItemId: " + itemId + "\nQuantity: " + quantity + "\nPurchaseTime: " + purchaseTime +
					"\nPurchaseState: " + purchaseState);

			switch (purchaseState) {
			case PURCHASED:
			case REFUNDED:
				if (mIAPListener != null && !TextUtils.isEmpty(itemId) && !purchseList.get(itemId)) {
					purchseList.put(itemId, true);
					PurchaseVO purchaseVO 		= new PurchaseVO();
					purchaseVO.developerPayload = developerPayload;
					purchaseVO.productId 		= itemId;
					purchaseVO.purchaseState 	= purchaseState;
					purchaseVO.purchaseTime 	= purchaseTime;

					mIAPListener.onPurchaseSuccess(Response.SUCCESSFUL, productType, new PurchaseItem(purchaseVO));
				}
				break;

			case CANCELED:
				printI(TAG, "User Canceled the " + itemId + " Purchse");
				mIAPListener.onPurchaseCanceled("Purchse Canceled!");
				break;
			}
		}

		@Override
		public void onRequestPurchaseResponse(RequestPurchase request, ResponseCode responseCode) {

			switch (responseCode) {
			case RESULT_OK:
				if (request != null)
					printI(TAG, request.mProductId + " purchase was successfully sent to server");
				break;

			case RESULT_USER_CANCELED:
				if (request != null)
					printI(TAG, request.mProductId + " user canceled purchase");

				if (mIAPListener != null) {
					mIAPListener.onPurchaseCanceled("Purchase Cancelled!");
				}
				break;

			default:
				if (request != null)
					printI(TAG, request.mProductId + " purchase failed");

				if (mIAPListener != null) {
					Operation.PURCHASE.setProductType(productType);
					mIAPListener.onError(Operation.PURCHASE, Response.FAILED, request.mProductId + " Purchase failed!");
				}
				break;
			}
		}

		@Override
		public void onRestoreTransactionsResponse(RestoreTransactions request, ResponseCode responseCode) {

			if (responseCode == ResponseCode.RESULT_OK) {
				printD(TAG, "completed RestoreTransactions request");
				// Update the shared preferences so that we don't perform
				// a RestoreTransactions again.
				SharedPreferences.Editor edit = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE).edit();
				edit.putBoolean(Consts.DB_INITIALIZED, true);
				edit.commit();

				if (mIAPListener != null) 
					mIAPListener.onPurchsedListPresent(Response.SUCCESSFUL, new PurchasedItem(getPurchasedItemList()));
			} else {

				//Toast.makeText(getApplicationContext(),	"Purchased.", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "RestoreTransactions error: " + responseCode);
				if (mIAPListener != null) 
					mIAPListener.onError(Operation.GET_PURCHASED_ITEM, Response.FAILED, "RestoreTransactions Error");
			}

		}
	}

	private PurchasedResponse getPurchasedItemList() {
		ArrayList<PurchaseVO> list = new ArrayList<PurchaseVO>(); 
		PurchasedResponse response = new PurchasedResponse(list);

		Cursor cursor = null; 
		try {
			cursor = mPurchaseDatabase.getAllPurchasedItems();
			cursor.moveToFirst();
			PurchaseVO vo;

			while (cursor.moveToNext()) {
				vo 					= new PurchaseVO();
				vo.orderId 			= cursor.getString(0);
				vo.productId 		= cursor.getString(1);
				vo.purchaseState 	= PurchaseState.valueOf(cursor.getInt(2));
				vo.purchaseTime 	= cursor.getLong(3);
				vo.developerPayload = cursor.getString(4);

				list.add(vo);
			}
		} catch (Exception exception) {}

		if (cursor != null)
			cursor.close();

		return response;
	}

	@Override
	protected void onDestroy() {
		if(mBillingService != null) {
			mBillingService.unbind();
			mBillingService = null;
		}
	}

	@Override
	protected void onResume() {
		if(mBillingService != null) {
			mBillingService.registerBillingReceiver();
		}
	}

	@Override
	protected void onPause() {
		if(mBillingService != null) {
			//mBillingService.unRegisterBillingReceiver();
		}
	}
}
