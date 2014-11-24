package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.AppParamRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.type.AppParam;

public class AppParamDAO {

	public static ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		ctx.checkRead();
		Condition condition = APP_PARAM.DOMAIN.equal(ctx.getDomainId()).and(APP_PARAM.NAME.equal(param.getValue()));
		return populateRecord(ctx.getDslContext().fetchOne(APP_PARAM,condition));
	}

	private static ApplicationParameter populateRecord(AppParamRecord record) {
		if (record == null) return null;
		
		ApplicationParameter app = new ApplicationParameter();
		app.setId(record.getId());
		app.setDomain(record.getDomain());
		app.setName(record.getName());
		app.setValue(record.getValue());
		return app;
	}
}
