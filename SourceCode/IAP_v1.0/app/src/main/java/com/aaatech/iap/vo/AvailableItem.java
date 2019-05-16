package com.aaatech.iap.vo;

import java.util.ArrayList;

import com.amazon.device.iap.model.Product;
import com.aaatech.iap.play.util.Inventory;
import com.aaatech.iap.sku.IAPItem;
import com.aaatech.iap.utils.IAPUtil;

public class AvailableItem {
	/**
	 * Samsung
	 */
	/*public ArrayList<ItemVo> itemList;

	public AvailableItem(ArrayList<ItemVo> list) {
		itemList = list;
	}*/

	/**
	 * Amazon
	 */
	public ArrayList<Product> response;
	public AvailableItem(ArrayList<Product> response) {
		this.response = response;
	}

	/**
	 * Google Play
	 */
	public Inventory inventory;
	public AvailableItem(Inventory inventory) {
		this.inventory = inventory;
	}

	public ArrayList<IAPItem> getPlayStoreItems(ArrayList<IAPItem> items, boolean removePurchasedItems) {
		return IAPUtil.getAvailableItems(items, inventory, removePurchasedItems);
	}
	
	public ArrayList<IAPItem> getAmazonItems(ArrayList<IAPItem> items, boolean removePurchasedItems) {
		return IAPUtil.getAvailableItems(items, response, removePurchasedItems);
	}
	
}
