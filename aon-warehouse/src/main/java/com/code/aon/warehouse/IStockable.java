package com.code.aon.warehouse;

import com.code.aon.product.Item;

public interface IStockable {

	public Item getItem();
	public Warehouse getWarehouse();
	public double getQuantity();
	public boolean isEntry();
	public String getTableName();
	public Integer getId();

}
