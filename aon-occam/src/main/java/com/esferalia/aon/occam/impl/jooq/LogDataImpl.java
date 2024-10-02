package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ILogData;
import com.esferalia.aon.occam.api.model.Filter.LogDataFilter;
import com.esferalia.aon.occam.impl.jooq.dao.LogDAO;
import com.esferalia.aon.occam.api.model.LogData;

public class LogDataImpl implements ILogData{

	@Override
	public void insertLogData(AONContext ctx, LogData data) {
		 ctx.getDslContext().transaction(
					configuration -> LogDAO.insert(ctx, data));
	}

	@Override
	public LogData getLogData(AONContext ctx, LogDataFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> LogDAO.select(ctx, filter));
	}

	@Override
	public void deleteLogData(AONContext ctx, LogDataFilter filter) {
		ctx.getDslContext().transaction(
				configuration -> LogDAO.delete(ctx, filter));
	}

}
