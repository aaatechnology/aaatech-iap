package com.aaatech.iap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aaatech.iap.amazon.AmazonStore;
import com.aaatech.iap.bb.BBStore;
import com.aaatech.iap.enums.AppStoreMode;
import com.aaatech.iap.enums.AppStoreType;
import com.aaatech.iap.enums.ProductType;
import com.aaatech.iap.play.PlayStore;
import com.aaatech.iap.play.util.Purchase;
import com.aaatech.iap.sku.IAPItem;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * 
 * Common IAP Class for all App Stores (Google Play Store, Amazon App Store, Samsung App Store).
 * 
 * <br/><br/>
 *
 * <font color="red"><b>WARNING:</b></font><br/>
 * <code>SUBSCRIBTION</code> need to test. 
 * <br/><br/>
 * <b><font color="green" >For <u>Permission</u> Details See:</font></b>
 * <li> {@link PlayStore} </li>
 * <li> {@link AmazonStore} </li>
 * <li> {@link BBStore} </li>
 */
public class IAPManager {

	final static String TAG = IAPManager.class.getSimpleName();

	private BaseAppStore appStore = null;

	private IAPManager() {}

	private static IAPManager _instance;
	public static IAPManager getInstance() {
		if (_instance == null) {
			_instance = new IAPManager();
			Log.i(TAG, "IAP Manager created and returned");
		} else {
			Log.i(TAG, "IAP Manager instance returned");
		}

		return _instance;
	}

	//=====================================================================================================
	// COMMON SETTINGS
	//=====================================================================================================


	/**
	 * Common init method for all three AppStores
	 * <br/><br/>
	 * <b>NOTE:</b> This method must called from <code>onResume()</code>. 
	 * @param appStoreType - {@link AppStoreType#PLAY_STORE} / {@link AppStoreType#AMAZON_STORE}
	 * @param base64PublicKey - This is specific to <code>Play store</code>.
	 * @param mode - This is specific to <code>Samsung Store</code>.
	 * @param listener - Common listener for all IAP operation. 
	 * 	 
	 * @see AppStoreType
	 * @see PlayStore#PlayStore(Context, String, AppStoreMode, CommonIAPListener)
	 * @see AmazonStore#AmazonStore(Context, CommonIAPListener)
	 * @see CommonIAPListener
	 */
	public void initStore(Activity activity, AppStoreType appStoreType, String base64PublicKey, AppStoreMode mode, CommonIAPListener listener) {
		setAppStoreType(activity, appStoreType, mode, base64PublicKey, listener);
		_isInitialized = true;
	}

	/**
	 * Common init method for all three AppStores. <br/>
	 * <br/>
	 * <b>NOTE:</b> This method must called from <code>onResume()</code>.<br/> 
	 * <code>SamsungStore</code> will set to <code>AppStoreMode.LIVE</code> mode.
	 * 
	 * @param appStoreType - {@link AppStoreType#PLAY_STORE} / {@link AppStoreType#AMAZON_STORE} / {@link AppStoreType#SAMSUNG_STORE}.
	 * @param base64PublicKey - This is specific to <code>Play Store</code>.
	 * @param listener - Common listener for all IAP operation. 
	 * 	 
	 * @see AppStoreType
	 * @see PlayStore#PlayStore(Context, String, AppStoreMode, CommonIAPListener)
	 * @see AmazonStore#AmazonStore(Context, CommonIAPListener)
	 * @see CommonIAPListener
	 */
	public void initStore(Activity activity, AppStoreType appStoreType, String base64PublicKey, CommonIAPListener listener) {
		setAppStoreType(activity, appStoreType, AppStoreMode.LIVE, base64PublicKey, listener);
		_isInitialized = true;
	}

	/**
	 * Ite creates new instance of specified Appsotore({@link PlayStore} / {@link AmazonStore}
	 * 
	 * @param appStoreType - Type of app store ({@link AppStoreType}).
	 * @param storeMode - Type of store mode ({@link AppStoreMode}). <br/> <code>This is field is mandorary for 
	 * <b>Samsung Appstore</b>.</code>
	 * @param base64PublicKey - <code>This field is mandorary for <b>Google Playstore</b>.</code></br>
	 * Your application's public key, encoded in base64. This is used for verification of purchase signatures. 
	 * You can find your app's base64-encoded public key in your application's page on Google Play Developer Console. 
	 * Note that this is NOT your "developer public key".
	 *
	 * @see AppStoreType
	 * @see AppStoreMode
	 * 
	 */
	private void setAppStoreType(Activity activity, AppStoreType appStoreType, AppStoreMode storeMode, 
			String base64PublicKey, CommonIAPListener listener) {

		try {
			if (appStore != null && activity.equals(appStore.appActivity) && appStore.isAvailabble()) {
				appStore.onResume();
				return;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		switch (appStoreType) {
		case PLAY_STORE:
			this.appStore = new PlayStore(activity, base64PublicKey, storeMode, listener);
			break;

		case AMAZON_STORE:
			this.appStore = new AmazonStore(activity, listener);
			break;

		case SAMSUNG_STORE:
			//this.appStore = new SamsungStore(activity, storeMode.code, listener);
			break;

		case BLACKBERRY_STORE:
			this.appStore = new BBStore(activity, listener);
		}

		appStore.appStoreType = appStoreType;
		appStore.appStoreMode = storeMode;
		appStore.appActivity = activity;
	}

	/**
	 * Set common IAP Listener for all action.
	 * 
	 * @param listener
	 * 
	 * @see CommonIAPListener
	 * @see BaseAppStore#setIAPListener(CommonIAPListener)
	 */
	public void setIAPListener(CommonIAPListener listener) {
		checkInitialization();

		appStore.setIAPListener(listener);
	}

	/**
	 * Print logs on Screen, If set to <code>true</code>.
	 * 
	 * @param mode
	 */
	public void setDebugMode(boolean mode) {
		if (isInitialized()) {
			appStore.setDebugMode(mode);
		}
	}

	/**
	 * Option to show default Alert/Popup on operation Success or Fail. <br/>
	 * <b>NOTE:</b> You can't prevent <code>Network not found</code> alert from Samsung appstore.
	 * 
	 * @param showAlert
	 * <br/><code>Default: false</code>. i.e never shows alert/dialog popup
	 */
	public void showAlerts(boolean showAlert) {
		appStore.showDefaultAlets(showAlert);
	}

	/**
	 * Check the instance is already created and initialized.
	 * 
	 * @return <code>boolean</code>.
	 * 
	 * @see {@link #initStore(Activity, AppStoreType, String, AppStoreMode, CommonIAPListener)}
	 */
	public boolean isInitialized() {
		return _instance != null && _isInitialized;
	}

	/**
	 * Gives the current appstore instance.
	 * @return instance of {@link PlayStore} / {@link AmazonStore}
	 */
	public AppStoreType getAppStoreType() {
		return appStore.appStoreType;
	}

	private boolean _isInitialized = false;
	/**
	 * Check for the appStore is already initialized
	 */
	private void checkInitialization() {
		if (!_isInitialized || appStore == null) {
			throw new Error("First call initStore() method");
		}
	}

	//-----------------------------------------------------------------------------------------------------------------
	// GET AVAILABLE ITEMS
	//-----------------------------------------------------------------------------------------------------------------

	private ArrayList<IAPItem> iapList;
	/**
	 * set all IAPItem to Google Play and Amazon
	 * @param iapList
	 */
	public void setIAPList(ArrayList<IAPItem> iapList) {
		this.iapList = iapList;
	}

	/**
	 * set all IAPItem to Google Play and Amazon
	 * @param items
	 */
	public void setIAPList(IAPItem... items) {
		this.iapList = new ArrayList<>(Arrays.asList(items));
	}

	private String groupId;
	/**
	 * set Group Id for Samsung AppStore 
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * Common to all.
	 *
	 * @param iapList - <code>Amazon AppStore, Play Store</code>
	 */
	public void getAvailableItems(ArrayList<IAPItem> iapList) {
		getAvailableItems(iapList, null, ProductType.ALL);
	}

	/**
	 * Common to all.
	 * 
	 * @param itemGroupId - <code>Samsung AppStore</code>
	 */
	public void getAvailableItems(String itemGroupId) {
		getAvailableItems(iapList, itemGroupId, ProductType.ALL);
	}

	/**
	 * Common to all.
	 *
	 * @param iapList - <code>Amazon AppStore, Play Store</code>
	 * @param itemGroupId - <code>Samsung AppStore</code>
	 */
	public void getAvailableItems(ArrayList<IAPItem> iapList, String itemGroupId) {
		getAvailableItems(iapList, itemGroupId, ProductType.ALL);
	}

	/**
	 * Common to all.
	 * 
	 * @param iapList - <code>Amazon AppStore, Play Store</code>
	 * @param itemGroupId - <code>Samsung AppStore</code>
	 * @param productType - <code>Samsung AppStore</code>
	 */
	public void getAvailableItems(ArrayList<IAPItem> iapList, String itemGroupId, ProductType productType) {
		checkInitialization();

		ArrayList<String> list = null;
		if (iapList != null) {
			list = new ArrayList<String>();
			for (IAPItem item : iapList) {
				list.add(item.getSku());
			}
		}
		appStore.getAvailableItems(list, itemGroupId, productType);
	}


	//-----------------------------------------------------------------------------------------------------------------
	// PURCHASE
	//-----------------------------------------------------------------------------------------------------------------

	private String _requestedSKU;
	/**
	 * Returns last requested sku for purchase.<br/>
	 * <b>Samsung:</b> ItemId <br/>
	 * <b>Others:</b> SKU
	 * 
	 * @return
	 */
	public String getRequestedSKU() {
		return _requestedSKU;
	}

	/**
	 * Purchase a item from AppStore <br/>
	 * Common to all except SUMSANG. <br/>
	 * 
	 * @param item - <code>IAPItem</code> contains sku and itemId.
	 * <br/><br/>
	 * This purchase consumes the purchase after successfull operation
	 * <b>NOTE:</b> Google play developer payload set to <code>NULL</code>. 
	 * @see IAPItem
	 * @see PlayStore#purchase(ProductType, String, String, String, String, boolean)
	 * @see AmazonStore#purchase(ProductType, String, String, String, String, boolean)
	 *
	 */
	synchronized public void purchase(IAPItem item) {
		purchase(item, null, null, true);
	}

	/**
	 * Purchase a item from AppStore <br/>
	 * Common to all. <br/>
	 * 
	 * @param item - <code>IAPItem</code> contains sku and itemId.
	 * @param groupId - <code>Samsung AppStore</code>
	 * <br/><br/>
	 * This purchase consumes the purchase after successfull operation
	 * <b>NOTE:</b> Google play developer payload set to <code>NULL</code>. 
	 * @see IAPItem
	 * @see PlayStore#purchase(ProductType, String, String, String, String, boolean)
	 * @see AmazonStore#purchase(ProductType, String, String, String, String, boolean)
	 *
	 */
	synchronized public void purchase(IAPItem item, String groupId) {
		purchase(item, null, groupId, true);
	}

	/**
	 * Purchase a item from AppStore <br/>
	 * Common to all. <br/>
	 * 
	 * @param item - <code>IAPItem</code> contains sku and itemId.
	 * @param groupId - <code>Samsung AppStore</code>
	 * @param consumePurchase - <code>boolean</code> whether the purchase need to consume automatically or not.  
	 * <br/><br/>
	 * <b>NOTE:</b> Google play developer payload set to <code>NULL</code>. 
	 * @see IAPItem
	 * @see PlayStore#purchase(ProductType, String, String, String, String, boolean)
	 * @see AmazonStore#purchase(ProductType, String, String, String, String, boolean)
	 *
	 */
	synchronized public void purchase(IAPItem item, String groupId, boolean consumePurchase) {
		purchase(item, null, groupId, consumePurchase);
	}

	/**
	 * Purchase a item from AppStore <br/>
	 * Common to all. <br/>
	 * 
	 * @param item - <code>IAPItem</code> contains sku and itemId.
	 * @param payload - <code>Google PlayStore</code> (Developer payload)
	 * @param groupId - <code>Samsung AppStore</code>
	 * 
	 * This purchase consumes the purchase after successfull operation
	 * @see #setGroupId(String)
	 * @see PlayStore#purchase(ProductType, String, String, String, String, boolean)
	 * @see AmazonStore#purchase(ProductType, String, String, String, String, boolean)
	 */
	synchronized public void purchase(IAPItem item, String payload, String groupId) {
		purchase(item, payload, groupId, true);
	}

	/**
	 * Purchase a item from AppStore <br/>
	 * Common to all. <br/>
	 * 
	 * @param item - <code>IAPItem</code> contains sku and itemId.
	 * @param payload - <code>Google PlayStore</code> (Developer payload)
	 * @param groupId - <code>Samsung AppStore</code>
	 * @param consumePurchase - <code>boolean</code> whether the purchase need to consume automatically or not.  
	 * 
	 * @see #setGroupId(String)
	 * @see PlayStore#purchase(ProductType, String, String, String, String, boolean)
	 * @see AmazonStore#purchase(ProductType, String, String, String, String, boolean)
	 */
	synchronized public void purchase(IAPItem item, String payload, String groupId, boolean consumePurchase) {
		checkInitialization();

		switch (appStore.appStoreType) {
		case SAMSUNG_STORE:
			_requestedSKU = item.getItemId();
			break;

		case BLACKBERRY_STORE:
			_requestedSKU = item.getBBsku();
			break;

		default:
			_requestedSKU = item.getSku();
			break;
		}

		appStore.purchase(item.getProductType(), item.getSku(), payload, groupId, item.getItemId(), consumePurchase);
	}

	/**
	 * Common to all
	 * <br/><br/>
	 * <b>WARNING:</b> This must happen after the <code>purchase</code>.<br/>
	 * To do purchase and consume in single chain use <code>purchaseAndConsume</code> method.
	 * 
	 * @param purchase - Google play purchased item.
	 * 
	 * @see IAPManager#purchase(IAPItem, String, String)
	 * @see IAPManager#consumeThePurchase(Purchase)
	 */
	public synchronized void consumeThePurchase(Purchase purchase) {
		checkInitialization();

		appStore.consumeThePurchase(purchase);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// GET PURCHASED ITEMS
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Get all items purchased by the user. <br/>
	 * Common to all. <br/> <br/>
	 * <b>For Samsung:</b> If <code>groupId</code> is already set by <code>setGroupId()</code>, then
	 * takes that id otherwise <code>null</code>.
	 * 
	 * @see PlayStore#getPurchasedItems(String)
	 * @see AmazonStore#getPurchasedItems(String)
	 */
	public void getPurchasedItems() {
		getPurchasedItems(groupId);
	}

	/**
	 * Get all items purchased by the user. <br/>
	 * Common to all.
	 * 
	 * @param itemGroupId - <code>Samsung AppStore</code>
	 * 
	 * @see PlayStore#getPurchasedItems(String)
	 * @see AmazonStore#getPurchasedItems(String)
	 */
	public void getPurchasedItems(String itemGroupId) {
		checkInitialization();

		appStore.getPurchasedItems(itemGroupId);
	}


	//-----------------------------------------------------------------------------------------------------------------
	// OVERRIDE METHOS
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * This should call from <code>onActivityResult()</code> method.
	 * 
	 * <br/><br/> <b>WARNING:</b> If you not called this method, IAP could not complete any operation.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (appStore != null) {
			appStore.printI(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
			try {
				appStore.onActivityResult(requestCode, resultCode, data);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * This should call from <code>onDestroy()</code> method.
	 * 
	 * <br/><br/> <b>WARNING:</b> If you not called this method, continous call will make to current appstore 
	 * and this drains the battery.
	 */
	public void onDestroy() {
		if (_isInitialized) {
			try {
				appStore.onDestroy();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void onPause() {
		if (_isInitialized) {
			try {
				appStore.onPause();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public void onResume() {
		if (_isInitialized) {
			try {
				appStore.onResume();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	// TO save some extra data
	private ArrayList<String> purchasedList;
	public void savePurchasedList(ArrayList<String> list) {
		purchasedList = list;
	}

	public ArrayList<String> getPurchaedList() {
		if (purchasedList == null) purchasedList = new ArrayList<String>();
		return purchasedList;
	}
	
	private boolean _isRestored;
	public void setRestores(boolean isRestored) {
		_isRestored = isRestored;
	}
	
	public boolean isRestored() {
		return _isRestored;
	}
}
