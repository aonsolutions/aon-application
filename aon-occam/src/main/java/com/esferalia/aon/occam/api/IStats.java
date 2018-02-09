package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;

public interface IStats {

	StatParams createStatParams(AONContext ctx);
	StatData<String, String, Double> getStatData(AONContext ctx, StatParams params);
	String getInvoicesReport(AONContext ctx, StatParams params);
	
	
	Stream<Task> getStatTaskStream(AONContext ctx, StatParams params);
	StatData<Integer, String, Double> getProductStat(AONContext ctx, ProductFilter productFilter, InvoiceFilter invoiceFilter,
			DeliveryFilter deliveryFilter, SalesFilter salesFilter, PurchaseFilter purchaseFilter);
	

}
