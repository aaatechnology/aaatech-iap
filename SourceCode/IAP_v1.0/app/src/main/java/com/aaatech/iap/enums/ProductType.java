package com.aaatech.iap.enums;


/**
 * Available Product types are, 
 * 
 *   <li> {@link #CONSUMABLE}
 *   <li> {@link #NON_CONSUMABLE}
 *   <li> {@link #SUBSCRIPTION}
 *   <li> {@link #ALL}
 */
public enum ProductType {
	/**
	 * Consumable Item
	 */
	CONSUMABLE("00"),
	/**
	 * Non-Consumable Item
	 */
	NON_CONSUMABLE("01"),
	/**
	 * Subscription Item
	 */
	SUBSCRIPTION("02"),
	/**
	 * All type of Items
	 */
	ALL("10");

	public String code;

	private ProductType(String string) {
		code = string;
	}

	public static ProductType getProductType(com.amazon.device.iap.model.ProductType productType) {
		switch (productType) {
		case CONSUMABLE:
			return CONSUMABLE;

		case SUBSCRIPTION:
			return SUBSCRIPTION;

		default:
			return NON_CONSUMABLE;
		}
	}
}
