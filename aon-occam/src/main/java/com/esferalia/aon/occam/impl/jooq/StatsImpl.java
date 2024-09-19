package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.IStats;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.ElaborationFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.OldTask;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceFlatFilter;
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
	public String getInvoicesReport(AONContext ctx, StatParams params, IDAOCallback callback) {
		return StatDAO.getInvoicesReport(ctx, params, callback);
	}

	@Override
	public Stream<OldTask> getStatTaskStream(AONContext ctx, StatParams params) {
		return StatDAO.getStatTaskStream(ctx, params);
	}

	@Override
	public StatData<Integer, String, Double> getProductStat(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFlatFilter invoiceFlatFilter, DeliveryFilter deliveryFilter, SalesFilter salesFilter,
			PurchaseFilter purchaseFilter) {
		return StatDAO.getProductStat(ctx, productFilter, itemFilter, invoiceFlatFilter, deliveryFilter, salesFilter,
				purchaseFilter);
	}

	@Override
	public StatData<Integer, String, Double> getProductMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFlatFilter invoiceFlatFilter, DeliveryFilter deliveryFilter,
			IncomeFilter incomeFilter) {
		return StatDAO.getWarehouseProductMovements(ctx, productFilter, itemFilter, invoiceFlatFilter, deliveryFilter,
				incomeFilter);
	}
	
	@Override
	public StatData<Integer, String, Double> getItemMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFlatFilter invoiceFlatFilter, DeliveryFilter deliveryFilter,
			IncomeFilter incomeFilter) {
		return StatDAO.getWarehouseItemMovements(ctx, productFilter, itemFilter, invoiceFlatFilter, deliveryFilter,
				incomeFilter);
	}

	@Override
	public StatData<Integer, String, Double> getElaborationMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, ElaborationFilter elaborationFilter) {
		return StatDAO.getElaborationMovements(ctx, productFilter, itemFilter, elaborationFilter);
	}
	
	@Override
	public StatData<String, String, Double> getFinanceStat(AONContext ctx, FinanceFilter financeFilter){
		return StatDAO.getFinanceStat(ctx, financeFilter);
	}

}
