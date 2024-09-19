package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

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

public interface IStats {

	StatParams createStatParams(AONContext ctx);
	StatData<String, String, Double> getStatData(AONContext ctx, StatParams params);
	String getInvoicesReport(AONContext ctx, StatParams params, IDAOCallback callback);
	
	
	Stream<OldTask> getStatTaskStream(AONContext ctx, StatParams params);

	StatData<Integer, String, Double> getProductStat(AONContext ctx, ProductFilter productFilter, ItemFilter itemFilter,
			InvoiceFlatFilter invoiceFlatFilter, DeliveryFilter deliveryFilter, SalesFilter salesFilter,
			PurchaseFilter purchaseFilter);

	StatData<Integer, String, Double> getProductMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFlatFilter invoiceFlatFilter, DeliveryFilter deliveryFilter,
			IncomeFilter incomeFilter);
	
	StatData<Integer, String, Double> getItemMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, InvoiceFlatFilter invoiceFilter, DeliveryFilter deliveryFilter,
			IncomeFilter incomeFilter);
	
	StatData<Integer, String, Double> getElaborationMovements(AONContext ctx, ProductFilter productFilter,
			ItemFilter itemFilter, ElaborationFilter elaborationFilter);
	
	StatData<String, String, Double> getFinanceStat(AONContext ctx, FinanceFilter financeFilter);
}
