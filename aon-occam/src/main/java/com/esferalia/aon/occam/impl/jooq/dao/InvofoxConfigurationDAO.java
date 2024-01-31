package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;

public class InvofoxConfigurationDAO {
	
	private InvofoxConfigurationDAO() {

	}
	
	public static InvofoxConfiguration get(AONContext ctx) {
		ctx.checkRead();
		InvofoxConfiguration invofoxConfiguration = new InvofoxConfiguration();
				
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getNameProperty().like("INVOFOX_%")))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_TEST.toString())) {
				invofoxConfiguration.setTest(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
		});

		return invofoxConfiguration;
	}

	public static InvofoxConfiguration save(AONContext ctx, InvofoxConfiguration invofoxConfiguration) {
		ctx.checkWrite();	
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.INVOFOX_TEST.toString(),
				Boolean.toString(invofoxConfiguration.isTest()));
		
		return invofoxConfiguration;
	}
	
}




