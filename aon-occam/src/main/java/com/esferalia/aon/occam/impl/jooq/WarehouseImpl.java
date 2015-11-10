package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IWarehouse;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.IncomeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InventoryDAO;

public class WarehouseImpl implements IWarehouse {

	@Override
	public IncomeDetail getLastIncomeDetail(AONContext ctx, Item item) {
		return IncomeDAO.getLastIncomeDetail(ctx, item);
	}
	
	@Override
	public LinkedList<IncomeDetail> getLastIncomeDetailList(AONContext ctx, Item item, Date startDate) {
		return IncomeDAO.getLastIncomeDetailList(ctx, item, startDate);
	}

	@Override
	public LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId) {
		return InventoryDAO.getInventoryDetailList(ctx,inventoryId);
	}
}
