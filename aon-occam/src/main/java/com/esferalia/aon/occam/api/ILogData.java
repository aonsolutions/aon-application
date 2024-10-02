package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.Filter.LogDataFilter;
import com.esferalia.aon.occam.api.model.LogData;

public interface ILogData {

	void insertLogData(AONContext ctx, LogData data);
	LogData getLogData(AONContext ctx, LogDataFilter filter);
	void deleteLogData(AONContext ctx, LogDataFilter filter);
}
