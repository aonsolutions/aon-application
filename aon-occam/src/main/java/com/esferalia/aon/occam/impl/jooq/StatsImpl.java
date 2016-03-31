package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IStats;
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


}
