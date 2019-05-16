package com.aaatech.iap.sku;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.aaatech.iap.enums.AppStoreType;
import com.aaatech.iap.enums.ProductType;


/**
 * Common item for all AppStores. <br/>
 * <br/>
 * <b>Example:</b><br/>
 * public final static IAPItem ITEM1 = new IAPItem("sku", "itemId", ProductType.CONSUMABLE);
 * <br/>...<br/> 
 *
 */
public class IAPItem {

	private ProductType productType;
	private String sku;

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * <br/><br/>
	 * <b>NOTE:</b> <code>ProductType</code> set to <code>ProductType.NON_CONSUMABLE</code>.
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku) {
		this.sku 		= sku;
		this.productType 	= ProductType.NON_CONSUMABLE;
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * <br/><br/>
	 * <b>NOTE:</b> <code>ProductType</code> set to <code>ProductType.NON_CONSUMABLE</code>.
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, int resId) {
		this.sku 		= sku;
		this.productType 	= ProductType.NON_CONSUMABLE;

		this.resId		= resId;
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, ProductType productType) {
		this.sku 		= sku;
		this.productType 	= productType;
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, ProductType productType, int resId) {
		this.sku 		= sku;
		this.productType 	= productType;

		this.resId		= resId;
	}

	/**
	 * 
	 * <br/><br/>
	 * <b>NOTE:</b> <code>ProductType</code> set to <code>ProductType.NON_CONSUMABLE</code>.
	 * 
	 * @see ProductType
	 */
	public IAPItem(IAPItem item) {
		if (item != null) {
			this.sku 		= item.sku;
			this.productType 	= item.getProductType();

			bitmap = item.bitmap;
			currencyCode = item.currencyCode;
			description = item.description;
			price = item.price;
			resId = item.resId;
			title = item.title;
			url = item.url;
		}
	}

	/**
	 * Gives the sku details for Google play and Amazon
	 * @return {@link String}
	 */
	public String getSku() {
		return sku;
	}

	/**
	 * Gives the ProductType
	 * @return {@link ProductType}
	 */
	public ProductType getProductType() {
		return productType;		
	}

	/**
	 * Gives sku/itemId based on the appstore type passed.
	 * @param type - <code>AppStoreType</code>.
	 * 
	 *
	 * @see AppStoreType
	 */
	public String getIAPItem(AppStoreType type) {
		if (type == null) return null;

		return sku;
	}

	/**
	 * Checks the given string with the sku/itemid of all appstore and
	 * returns <code>TRUE</code> if any of one matched.
	 * 
	 * @param skuItemId - sku/itemid
	 * 
	 * @return {@link Boolean}
	 */
	public boolean equals(String skuItemId) {
		if (skuItemId == null) return false;

		if (sku != null && sku.equals(skuItemId))
			return true;

		return false;
	}

	@Override
	public String toString() {
		return "Product Type: " + productType + ", SKU: " + sku;
	}

	public String price;
	public String currencyCode;
	public String title;
	public String description;
	public int resId;
	public Bitmap bitmap;
	public String url;
	public Number getPrice() {
		if (TextUtils.isEmpty(price)) return 0;

		boolean dotAdded = false;
		String str = "";
		for (int i = 0; i < price.length(); i++) {
			char c = price.charAt(i);
			if (Character.isDigit(c)) {
				str += c;
			} else if (c == '.') {
				if (str.length() > 0 && !dotAdded) {
					str += c;
					dotAdded = true;
				}
			}
		}

		return Float.parseFloat(str);
	}
}
