package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public interface IStats {

	StatParams createStatParams(AONContext ctx);

	StatData<Integer, String, Double> getYearInvoiceTypeData(
			AONContext ctx, StatParams params);

	StatData<Integer, String, Double> getMonthInvoiceTypeData(
			AONContext ctx, StatParams params);

	StatData<Integer, String, Double> getDayInvoiceTypeData(
			AONContext ctx, StatParams params);


}
