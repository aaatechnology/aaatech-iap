package com.aaatech.iap.enums;


public enum Operation {
	GET_AVAILABLE_ITEM, PURCHASE, GET_PURCHASED_ITEM;
	
	private ProductType productType;
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	
	public ProductType getProductType() {
		return productType;
	}
}
