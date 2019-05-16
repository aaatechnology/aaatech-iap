package com.aaatech.iap.utils;

import android.text.TextUtils;

import com.aaatech.iap.IAPManager;
import com.aaatech.iap.play.util.Inventory;
import com.aaatech.iap.play.util.Purchase;
import com.aaatech.iap.play.util.SkuDetails;
import com.aaatech.iap.sku.IAPItem;

import java.util.ArrayList;
import java.util.Collection;

public class IAPUtil {

	public static ArrayList<IAPItem> getAvailableItems(ArrayList<IAPItem> items, Inventory inventory) {
		return getAvailableItems(items, inventory, true);
	}

	public static ArrayList<IAPItem> getAvailableItems(ArrayList<IAPItem> items, Inventory inventory, boolean removePurchasedItems) {
		if (items == null || inventory == null) return null;

		ArrayList<String> purchasedList = IAPManager.getInstance().getPurchaedList();
		ArrayList<IAPItem> list = new ArrayList<IAPItem>();
		IAPItem iap;
		for (IAPItem iapItem : items) {
			SkuDetails details = inventory.getSkuDetails(iapItem.getSku());

			if (details != null && !(removePurchasedItems && purchasedList.contains(details.getSku()))) {
				iap 			 = new IAPItem(iapItem);
				iap.price 		 = details.getPrice();
				iap.description  = details.getDescription();
				String title 	 = details.getTitle();
				iap.title 		 = title.substring(0, title.indexOf("(")).trim();
				iap.currencyCode = details.getCurrencyCode();

				list.add(iap);
			}
		}

		return list;
	}
	
	/*public static ArrayList<IAPItem> getAvailableItems(ArrayList<IAPItem> items, ArrayList<Product> products, boolean removePurchasedItems) {
		if (items == null || products == null) return null;

		ArrayList<String> purchasedList = IAPManager.getInstance().getPurchaedList();
		ArrayList<IAPItem> list = new ArrayList<IAPItem>();
		IAPItem iap;
		for (IAPItem iapItem : items) {
			
			Product product = null;
			for (Product productItem : products) {
				if (productItem.getSku().equals(iapItem.getSku())) {
					product = productItem;
					products.remove(productItem);
					break;
				}
			}
			
			if (product != null && !(removePurchasedItems && purchasedList.contains(product.getSku()))) {
				iap 			 = new IAPItem(iapItem);
				iap.price 		 = product.getPrice();
				iap.description  = product.getDescription();
				iap.title 		 = product.getTitle();
				iap.currencyCode = "";

				list.add(iap);
			}
		}

		return list;
	}*/
	

	public static IAPItem getFromList(ArrayList<IAPItem> items, String sku) {
		if (items == null || TextUtils.isEmpty(sku)) return null;

		for (IAPItem iapItem : items) {
			if (iapItem.equals(sku)) return iapItem;
		}

		return null;
	}

	public static void getPurchasedItemsAndSave(Inventory inventory) {
		try {
			if (inventory != null) {
				Collection<Purchase> purchases = inventory.getAllPurchase();
				ArrayList<String> list = new ArrayList<String>();
				for (Purchase purchase : purchases) {
					list.add(purchase.getSku());
				}
				IAPManager.getInstance().savePurchasedList(list);
			}
		} catch (Exception e) {}
	}

	public static void addToPurchasedList(String sku) {
		try {
			IAPManager.getInstance().getPurchaedList().add(sku);
		} catch (Exception e) {}
	}
}
