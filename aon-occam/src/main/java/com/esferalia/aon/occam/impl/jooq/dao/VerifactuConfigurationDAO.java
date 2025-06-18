package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;

public class VerifactuConfigurationDAO {
	
	private VerifactuConfigurationDAO() {

	}
	
	public static VerifactuConfiguration get(AONContext ctx) {
		ctx.checkRead();
		VerifactuConfiguration verifactu = new VerifactuConfiguration();
				
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getNameProperty().like("VERIFACTU_%")))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.VERIFACTU_ACTIVE.toString())) {
				verifactu.setActive(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.VERIFACTU_TEST.toString())) {
				verifactu.setTest(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.VERIFACTU_INCLUDE_DATE.toString())) {
				verifactu.setIncludeDate(AonDateUtils.parse(r.getValue(), "yyyy-MM-dd"));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.VERIFACTU_REGISTRY_DATE.toString())) {
				verifactu.setRegistryDate(r.getValue());
			}
			
		});

		return verifactu;
	}

	public static VerifactuConfiguration save(AONContext ctx, VerifactuConfiguration vc) {
		ctx.checkWrite();	
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.VERIFACTU_ACTIVE.toString(),
				Boolean.toString(vc.isActive()));
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.VERIFACTU_TEST.toString(),
				Boolean.toString(vc.isTest()));
		
		if(vc.getIncludeDate() != null) {
			AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.VERIFACTU_INCLUDE_DATE.toString(),
				AonDateUtils.format(vc.getIncludeDate(), "yyyy-MM-dd"));
		}
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.VERIFACTU_REGISTRY_DATE.toString(),
				vc.getRegistryDate());
		
		if(vc.isActive()) {
			AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM.toString(),
				"default");
		}
		
		return vc;
	}
	
}




