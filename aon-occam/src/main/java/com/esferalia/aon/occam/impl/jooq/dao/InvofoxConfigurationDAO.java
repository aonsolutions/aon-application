package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Arrays;
import java.util.Objects;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class InvofoxConfigurationDAO {
	
	private InvofoxConfigurationDAO() {

	}
	
	public static InvofoxConfiguration get(AONContext ctx) {
		ctx.checkRead();
		Integer adminDomain = 0;
		Integer parentDomain = DomainDAO.getParentDomain(ctx);		
		Integer contextDomain = ctx.getDomainId();		
		
		InvofoxConfiguration invofoxConfiguration = new InvofoxConfiguration();
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().in(new Integer[] {adminDomain,parentDomain, contextDomain})
			.and(f.getNameProperty().like("INVOFOX_%")))
		.sorted((r1,r2) -> AonNumberUtils.compare(r1.getDomain(), r2.getDomain()))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_TEST.toString())) {
				invofoxConfiguration.setTest(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_API_KEY.toString())) {
				invofoxConfiguration.setApiKey(r.getValue());
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_API_URL.toString())) {
				invofoxConfiguration.setApiUrl(r.getValue());
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_AUTO_ACCEPT.toString())) {
				invofoxConfiguration.setAutoAccept(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_AUTO_RECORD.toString())) {
				invofoxConfiguration.setAutoRecord(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
		});

		return invofoxConfiguration;
	}

	public static InvofoxConfiguration save(AONContext ctx, InvofoxConfiguration invofoxConfiguration) {
		ctx.checkWrite();	
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_TEST.toString(),
				Boolean.toString(invofoxConfiguration.isTest()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_AUTO_ACCEPT.toString(),
				Boolean.toString(invofoxConfiguration.isAutoAccept()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_AUTO_RECORD.toString(),
				Boolean.toString(invofoxConfiguration.isAutoRecord()));
		
		return invofoxConfiguration;
	}
	
}




