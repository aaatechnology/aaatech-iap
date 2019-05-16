package com.aaatech.iap.bb.vo;

import java.util.ArrayList;

public class PurchasedResponse {

	private ArrayList<PurchaseVO> list;
	public PurchasedResponse(ArrayList<PurchaseVO> list) {
		this.list = list;
	}
	
	public ArrayList<PurchaseVO> getPurchsedList() {
		return list;
	}
}
