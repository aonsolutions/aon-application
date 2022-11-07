package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TbaiConfigurationDAO {
	
	private TbaiConfigurationDAO() {

	}
	
	public static TbaiConfiguration get(AONContext ctx) {
		ctx.checkRead();
		TbaiConfiguration tbai = new TbaiConfiguration();

		ApplicationParameter administration = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.FS_DEFAULT_ADMINISTRATION.toString())))
			.findFirst().orElse(new ApplicationParameter());
				
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getNameProperty().like("TBAI_%")))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.TBAI_ACTIVE.toString())) {
				tbai.setActive(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.TBAI_TEST.toString())) {
				tbai.setTest(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.TBAI_INCLUDE_DATE.toString())) {
				tbai.setIncludeDate(AonDateUtils.parse(r.getValue(), "yyyy-MM-dd"));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.TBAI_REGISTRY_DATE.toString())) {
				tbai.setRegistryDate(r.getValue());
			}
			
		});

		return tbai.setAdministration(administration.getValue() != null
					? Administration.safeValueOf(Integer.parseInt(administration.getValue()))
					: Administration.UNKNOWN);
	}

	public static TbaiConfiguration save(AONContext ctx, TbaiConfiguration tc) {
		ctx.checkWrite();	
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.TBAI_ACTIVE.toString(),
				Boolean.toString(tc.isActive()));
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.TBAI_TEST.toString(),
				Boolean.toString(tc.isTest()));
		
		if(tc.getIncludeDate() != null) {
			AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.TBAI_INCLUDE_DATE.toString(),
				AonDateUtils.format(tc.getIncludeDate(), "yyyy-MM-dd"));
		}
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.TBAI_REGISTRY_DATE.toString(),
				tc.getRegistryDate());
		
		if(tc.isActive()) {
			AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM.toString(),
				"default");
		}
		
		return tc;
	}
	
}




