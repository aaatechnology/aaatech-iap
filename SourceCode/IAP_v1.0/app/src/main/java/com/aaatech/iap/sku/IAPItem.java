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
	private String itemId;
	private String bbSKU;

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY and AMAZON</code>
	 * @param itemId - for <code>SAMSUNG</code>
	 * @param bbSKU - <code>BALCKBERRY</code>
	 * <br/><br/>
	 * <b>NOTE:</b> <code>ProductType</code> set to <code>ProductType.NON_CONSUMABLE</code>.
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, String itemId, String bbSKU) {
		this.sku 		= sku;
		this.itemId 	= itemId;
		this.bbSKU		= bbSKU;
		this.productType 	= ProductType.NON_CONSUMABLE;
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY and AMAZON</code>
	 * @param itemId - for <code>SAMSUNG</code>
	 * @param bbSKU - <code>BALCKBERRY</code>
	 * @param productType - <code>ProductType</code>
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, String itemId, String bbSKU, ProductType productType) {
		this.sku 		= sku;
		this.itemId 	= itemId;
		this.bbSKU		= bbSKU;
		this.productType 	= productType;
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * @param itemId - for <code>SAMSUNG</code>
	 * <br/><br/>
	 * <b>NOTE:</b> <code>ProductType</code> set to <code>ProductType.NON_CONSUMABLE</code>.
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, String itemId) {
		this.sku 		= sku;
		this.itemId 	= itemId;
		this.bbSKU		= sku;
		this.productType 	= ProductType.NON_CONSUMABLE;
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * @param itemId - for <code>SAMSUNG</code>
	 * <br/><br/>
	 * <b>NOTE:</b> <code>ProductType</code> set to <code>ProductType.NON_CONSUMABLE</code>.
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, String itemId, int resId) {
		this.sku 		= sku;
		this.itemId 	= itemId;
		this.bbSKU		= sku;
		this.productType 	= ProductType.NON_CONSUMABLE;
		
		this.resId		= resId;
	}
	
	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * @param itemId - for <code>SAMSUNG</code>
	 * @param productType - <code>ProductType</code>
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, String itemId, ProductType productType) {
		this.sku 		= sku;
		this.itemId 	= itemId;
		this.bbSKU		= sku;
		this.productType 	= productType; 
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY, AMAZON and BALCKBERRY</code>
	 * @param itemId - for <code>SAMSUNG</code>
	 * @param productType - <code>ProductType</code>
	 * 
	 * @see ProductType
	 */
	public IAPItem(String sku, String itemId, ProductType productType, int resId) {
		this.sku 		= sku;
		this.itemId 	= itemId;
		this.bbSKU		= sku;
		this.productType 	= productType; 
		
		this.resId		= resId;
	}
	
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
		this.itemId 	= null;
		this.bbSKU		= sku;
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
		this.itemId 	= null;
		this.bbSKU		= sku;
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
		this.itemId 	= null;
		this.bbSKU		= sku;
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
		this.itemId 	= null;
		this.bbSKU		= sku;
		this.productType 	= productType;

		this.resId		= resId;
	}

	/**
	 * 
	 * @param sku - for <code>GOOGLE PLAY and AMAZON</code>
	 * @param itemId - for <code>SAMSUNG</code>
	 * @param bbSKU - <code>BALCKBERRY</code>
	 * <br/><br/>
	 * <b>NOTE:</b> <code>ProductType</code> set to <code>ProductType.NON_CONSUMABLE</code>.
	 * 
	 * @see ProductType
	 */
	public IAPItem(IAPItem item) {
		if (item != null) {
			this.sku 		= item.sku;
			this.itemId 	= item.itemId;
			this.bbSKU		= item.bbSKU;
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
	 * Gives the item id details for Samsung
	 * @return {@link String}
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * Gives the item id details for Blackberry
	 * @return {@link string}
	 */
	public String getBBsku() {
		return bbSKU;
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
	 * @return {@link string}
	 * 
	 * @see AppStoreType
	 */
	public String getIAPItem(AppStoreType type) {
		if (type == null) return null;

		String skuItem = null;
		switch (type) {
		case AMAZON_STORE:
		case PLAY_STORE:
			skuItem = sku;
			break;

		case SAMSUNG_STORE:
			skuItem = itemId;
			break;

		case BLACKBERRY_STORE:
			skuItem = bbSKU;
			break;
		} 

		return skuItem;
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

		if (itemId != null && itemId.equals(skuItemId))
			return true;

		if (bbSKU != null && bbSKU.equals(skuItemId))
			return true;

		return false;
	}

	@Override
	public String toString() {
		return "Product Type: " + productType + ", SKU: " + sku + ", Item Id: " + itemId + ", BB SKU: " + bbSKU;
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
