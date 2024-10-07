package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

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

	@Override
	public Stream<LogData> getLogsTream(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> LogDAO.selectAll(ctx));
	}

}
