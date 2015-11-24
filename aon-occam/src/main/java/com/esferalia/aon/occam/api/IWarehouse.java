package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;

public interface IWarehouse {
	
	
	// 	***********************************************
	// 	****************************** INCOME DETAIL***
	// 	***********************************************

	IncomeDetail getLastIncomeDetail(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId);
	LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, Item item, Date startDate, Integer workplaceId, Integer warehouseId);
	LinkedList<IncomeDetail> getIncomeDetailList(AONContext ctx, Item item, Integer workplaceId, Integer warehouseId);

	// 	***********************************************
	// 	************************** INVENTORY DETAIL ***
	// 	***********************************************
	
	LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId);
	
	// 	***********************************************
	// 	********************************* INVENTORY ***
	// 	***********************************************
	
	LinkedList<Inventory> getTwoLastInventory(AONContext ctx, Integer warehouseID);
	void updateInventory(AONContext ctx, Inventory inventory);
	void deleteInventory(AONContext ctx, Integer inventoryId);
	
	// 	***********************************************
	// 	************************ WAREHOUSE TRANSFER ***
	// 	***********************************************
	
	void deleteWarehouseTransfer(AONContext ctx, Integer inventoryId);

}
