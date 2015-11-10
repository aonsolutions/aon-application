package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;

public interface IWarehouse {
	
	
	// 	***********************************************
	// 	*********************************** INCOME ***
	// 	***********************************************

	IncomeDetail getLastIncomeDetail(AONContext ctx, Item item);
	
	LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, Item item, Date startDate);
	
	LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId);
}
