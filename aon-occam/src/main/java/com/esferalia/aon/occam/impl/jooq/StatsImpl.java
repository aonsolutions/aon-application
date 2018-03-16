package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IStats;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.impl.jooq.dao.StatDAO;

public class StatsImpl implements IStats {

	@Override
	public StatParams createStatParams(AONContext ctx) {
		return StatDAO.createStatParams(ctx);
	}

	@Override
	public StatData<String, String, Double> getStatData(AONContext ctx,StatParams params) {
		return StatDAO.getStatData(ctx, params);
	}

	@Override
	public String getInvoicesReport(AONContext ctx, StatParams params) {
		return StatDAO.getInvoicesReport(ctx, params);
	}

	@Override
	public Stream<Task> getStatTaskStream(AONContext ctx, StatParams params) {
		return StatDAO.getStatTaskStream(ctx, params);
	}

	@Override
	public StatData<Integer, String, Double> getProductStat(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter, SalesFilter salesFilter,
			PurchaseFilter purchaseFilter) {
		return StatDAO.getProductStat(ctx, productFilter, itemFilter, invoiceFilter, deliveryFilter, salesFilter,
				purchaseFilter);
	}

	@Override
	public StatData<Integer, String, Double> getProductMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter,
			IncomeFilter incomeFilter) {
		return StatDAO.getWarehouseProductMovements(ctx, productFilter, itemFilter, invoiceFilter, deliveryFilter,
				incomeFilter);
	}
	
	@Override
	public StatData<Integer, String, Double> getItemMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFilter invoiceFilter, DeliveryFilter deliveryFilter,
			IncomeFilter incomeFilter) {
		return StatDAO.getWarehouseItemMovements(ctx, productFilter, itemFilter, invoiceFilter, deliveryFilter,
				incomeFilter);
	}


}
