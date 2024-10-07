package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.LogDataFilter;
import com.esferalia.aon.occam.api.model.LogData;

public interface ILogData {

	void insertLogData(AONContext ctx, LogData data);
	LogData getLogData(AONContext ctx, LogDataFilter filter);
	void deleteLogData(AONContext ctx, LogDataFilter filter);
	Stream<LogData>getLogsTream(AONContext ctx);
}
