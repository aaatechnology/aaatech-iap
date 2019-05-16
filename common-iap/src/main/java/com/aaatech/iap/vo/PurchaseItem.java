package com.aaatech.iap.vo;

import com.aaatech.iap.play.util.Purchase;

public class PurchaseItem {

	/**
	 * Common to all App Stores. It return itemid/sku based on the appstore.<br/>
	 * For <code>Samsung Appstore</code> - it returns purchased Itemid.<br/>
	 * For <code>Other AppStore</code> - it returns purchased sku.
	 */
	private String purchasedItemSKU;

	/**
	 * Common to all App Stores.<br/>
	 * 
	 * @return It return Item id/SKU based on the AppStore. <br/>
	 * For <code>Samsung Appstore</code> - it returns purchased Itemid.<br/>
	 * For <code>Other AppStore</code> - it returns purchased sku.
	 */
	public String getPurchasedItemSKU() {
		if (purchasedItemSKU != null) 
			return purchasedItemSKU;
		else
			return "";
	}

	/**
	 * Amazon
	 */
	/*private PurchaseResponse response;

	public PurchaseResponse getAmazonResult() {
		return response;
	}*/

	/**
	 * Google Play
	 */
	private Purchase purchase;

	public Purchase getGooglePlayResult() {
		return purchase;
	}

	/**
	 * Samsung
	 */
	/*private PurchaseVo purchaseVO;

	public PurchaseVo getSamsungResult() {
		return purchaseVO;
	}*/

	/**
	 * Samsung
	 */
	public String message;

	/**
	 * Amazon
	 */
	/*public PurchaseItem(PurchaseResponse response) {
		this.response = response;

		try {
			if (response != null && response.getReceipt() != null)
				purchasedItemSKU = response.getReceipt().getSku();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}*/

	/**
	 * Google Play
	 */
	public PurchaseItem(Purchase purchase) {
		this.purchase = purchase;

		try {
			purchasedItemSKU = purchase.getSku();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Samsung
	 */
	/*public PurchaseItem(PurchaseVo purchaseVO, String message) {
		this.purchaseVO = purchaseVO;
		this.message = message;

		try {
			purchasedItemSKU = purchaseVO.getItemId();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}*/

	/**
	 * Samsung
	 */
	public PurchaseItem(String itemId, String message) {
		this.message = message;

		this.purchasedItemSKU = itemId;
	}
}

