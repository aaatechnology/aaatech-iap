package com.aaatech.iap;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.aaatech.iap.enums.AppStoreMode;
import com.aaatech.iap.enums.AppStoreType;
import com.aaatech.iap.enums.ProductType;
import com.aaatech.iap.play.util.Purchase;

import java.util.List;

public abstract class BaseAppStore {

	protected AppStoreType appStoreType;
	protected AppStoreMode appStoreMode;
	protected ProductType productType;
	protected Activity appActivity;
	
	public BaseAppStore() {
		//throw new Error("This is base class. Extend this class to use!");
	}

	protected CommonIAPListener mIAPListener;
	/**
	 * Common listener for all AppStore.
	 * 
	 * @param iapListener
	 * 
	 * @see CommonIAPListener
	 */
	protected void setIAPListener(CommonIAPListener iapListener) {
		this.mIAPListener = iapListener;
	}

	protected boolean showAlert;
	protected void showDefaultAlets(boolean showAlert) {
		this.showAlert = showAlert;
	}

	protected boolean isAvailabble() {
		return true;
	}
	
	abstract public void getAvailableItems(List<String> list, ProductType productType);
	
	abstract public void purchase(ProductType productType, String sku, String payload, boolean consumePurchase);

	abstract public void getPurchasedItems();

	abstract protected void enableDebugMode(boolean mode);
	
	protected boolean isDebugMode;
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {}
	protected void onDestroy() {}
	protected void onPause() {}
	protected void onResume() {}
	
	public void consumeThePurchase(Purchase purchase) {}
	
	protected void setDebugMode(boolean mode) {
		isDebugMode = mode;
		enableDebugMode(mode);
	}

	protected void printD(String tag,  String string) {
		if (isDebugMode) Log.d(tag, string);
	}

	protected void printI(String tag, String string) {
		if (isDebugMode) Log.i(tag, string);
	}

	protected void printE(String tag, String string) {
		if (isDebugMode) Log.e(tag, string);
	}
}


