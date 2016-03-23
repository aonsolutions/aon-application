package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IStats;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.impl.jooq.dao.StatsDAO;

public class StatsImpl implements IStats {

	@Override
	public StatParams createStatParams(AONContext ctx) {
		return StatsDAO.createStatParams(ctx);
	}

	@Override
	public StatData<Integer, String, Double> getYearInvoiceTypeData(
			AONContext ctx,StatParams params) {
		return StatsDAO.getYearInvoiceTypeData(ctx, params);
	}

	@Override
	public StatData<Integer, String, Double> getMonthInvoiceTypeData(
			AONContext ctx, StatParams params) {
		return StatsDAO.getMonthInvoiceTypeData(ctx, params);
	}
	
	@Override
	public StatData<Integer, String, Double> getDayInvoiceTypeData(
			AONContext ctx, StatParams params) {
		return StatsDAO.getDayInvoiceTypeData(ctx, params);
	}


}
