package com.aaatech.iap.bb.vo;

import com.aaatech.iap.bb.util.Consts.PurchaseState;

public class PurchaseVO {
	public String orderId;
	public PurchaseState purchaseState;
	public String productId;
	public long purchaseTime;
	public String developerPayload;
	//public String notificationId;
	//public int quantity;
}
