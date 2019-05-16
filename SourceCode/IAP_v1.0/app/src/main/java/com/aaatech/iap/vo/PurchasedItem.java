package com.aaatech.iap.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.text.TextUtils;

import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.aaatech.iap.IAPManager;
import com.aaatech.iap.bb.vo.PurchaseVO;
import com.aaatech.iap.bb.vo.PurchasedResponse;
import com.aaatech.iap.play.util.Inventory;
import com.aaatech.iap.sku.IAPItem;

public class PurchasedItem {

	HashMap<String, Object> purchasedHashMap;
	public boolean hasPurchased(String itemSKU) {
		if (TextUtils.isEmpty(itemSKU)) return false;

		if (purchasedHashMap != null) {
			return purchasedHashMap.containsKey(itemSKU);
		} else if (inventory != null) {
			return inventory.hasPurchase(itemSKU);
		}

		return false; 
	}

	public boolean hasPurchased(IAPItem iapItem) {
		if (iapItem == null) return false;

		return hasPurchased(iapItem.getIAPItem(IAPManager.getInstance().getAppStoreType()));
	}

	/**
	 * Samsung
	 */
	/*private ArrayList<InboxVo> inboxList;
	public ArrayList<InboxVo> getSamsungItems() {
		return inboxList;
	}*/

	/**
	 * Samsung
	 */
	/*public PurchasedItem(ArrayList<InboxVo> _inboxList) {
		inboxList = _inboxList;

		purchasedHashMap = new HashMap<String, Object>();
		try {
			if (_inboxList != null) {
				for (InboxVo inBoxVo : _inboxList) {
					purchasedHashMap.put(inBoxVo.getItemId(), inBoxVo);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}*/

	/**
	 * Google Play
	 */
	private Inventory inventory;

	public Inventory getGoogleItems() {
		return inventory;
	}

	/**
	 * Google Play
	 */
	public PurchasedItem(Inventory inventory) {
		this.inventory = inventory;
		purchasedHashMap = null;
	}

	private PurchaseUpdatesResponse response;

	/**
	 * Amazon
	 */
	public PurchaseUpdatesResponse getAmazonItems() {
		return response;
	}

	/**
	 * Amazon
	 */
	public PurchasedItem(PurchaseUpdatesResponse response) {
		this.response = response;

		purchasedHashMap = new HashMap<String, Object>();
		try {
			Iterator<Receipt> it = response.getReceipts().iterator();
			while (it.hasNext()) {
				Receipt receipt = it.next();
				if (!receipt.isCanceled()) // remove the cancelled purchases
					purchasedHashMap.put(receipt.getSku(), receipt);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private PurchasedResponse purchasedResponse;
	/**
	 * bb
	 * @param response
	 */
	public PurchasedItem(PurchasedResponse response) {
		purchasedResponse = response;

		ArrayList<PurchaseVO> list = response.getPurchsedList();
		purchasedHashMap = new HashMap<String, Object>();
		if (list != null) {
			for (PurchaseVO vo : list) {
				purchasedHashMap.put(vo.productId, vo);
			}
		}
	}

	/**
	 * bb
	 * @return
	 */
	public PurchasedResponse getBlackberryItem() {
		return purchasedResponse;
	}
}
