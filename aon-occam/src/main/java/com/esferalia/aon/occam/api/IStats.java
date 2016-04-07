package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;

public interface IStats {

	StatParams createStatParams(AONContext ctx);
	StatData<String, String, Double> getStatData(AONContext ctx, StatParams params);
	String getInvoicesReport(AONContext ctx, StatParams params);

}
