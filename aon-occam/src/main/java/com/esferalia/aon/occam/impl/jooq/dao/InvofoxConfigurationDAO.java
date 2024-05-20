package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
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
		
		// ----- BORRAR ----- es para tener en cuenta la anterior configuración de entorno de pruebas.
		ApplicationParameter appParam = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().in(new Integer[] {adminDomain,parentDomain, contextDomain})
			.and(f.getNameProperty().eq("INVOFOX_TEST"))).findFirst().orElse(null);
		if(appParam != null) {
			boolean test = appParam.getValue() != null && ("true".equalsIgnoreCase(appParam.getValue()) || "1".equals(appParam.getValue()));
			if(test) {
				invofoxConfiguration.setTest(test);
			}
			AppParamDAO.deleteApplicationParameter(ctx, f -> f.getIdProperty().eq(appParam.getId()));
		}
		//-------------------
		
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().in(new Integer[] {adminDomain,parentDomain, contextDomain})
			.and(f.getNameProperty().like("INVOFOX_%")))
		.sorted((r1,r2) -> AonNumberUtils.compare(r1.getDomain(), r2.getDomain()))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_USER.toString())) {
				invofoxConfiguration.setUser(r.getValue());
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_PASS.toString())) {
				invofoxConfiguration.setPass(r.getValue());
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_API_KEY.toString())) {
				invofoxConfiguration.setApiKey(r.getValue());
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_API_URL.toString())) {
				invofoxConfiguration.setApiUrl(r.getValue());
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_ENVIRONMENT.toString())) {
				invofoxConfiguration.setEnvironment(r.getValue());
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_AUTO_ACCEPT.toString())) {
				invofoxConfiguration.setAutoAccept(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_AUTO_RECORD.toString())) {
				invofoxConfiguration.setAutoRecord(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			} else if(r.getName().equalsIgnoreCase(AppParam.INVOFOX_PERSONALIZED.toString())) {
				invofoxConfiguration.setPersonalized(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
		});

		return invofoxConfiguration;
	}

	public static InvofoxConfiguration save(AONContext ctx, InvofoxConfiguration invofoxConfiguration) {
		ctx.checkWrite();	
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_USER.toString(),
				invofoxConfiguration.getUser());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_PASS.toString(),
				invofoxConfiguration.getPass());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_API_URL.toString(),
				invofoxConfiguration.getApiUrl());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_API_KEY.toString(),
				invofoxConfiguration.getApiKey());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_ENVIRONMENT.toString(),
				invofoxConfiguration.getEnvironment());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_AUTO_ACCEPT.toString(),
				Boolean.toString(invofoxConfiguration.isAutoAccept()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_AUTO_RECORD.toString(),
				Boolean.toString(invofoxConfiguration.isAutoRecord()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOFOX_PERSONALIZED.toString(),
				Boolean.toString(invofoxConfiguration.isPersonalized()));
		
		return invofoxConfiguration;
	}
	
}




