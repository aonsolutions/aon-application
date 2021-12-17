package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;

public class TbaiConfigurationDAO {
	
	private TbaiConfigurationDAO() {

	}
	
	public static TbaiConfiguration get(AONContext ctx) {
		ctx.checkRead();
		
		ApplicationParameter administration = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.FS_DEFAULT_ADMINISTRATION.toString())))
			.findFirst().orElse(new ApplicationParameter());

		ApplicationParameter active = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getNameProperty().eq(AppParam.TBAI_ACTIVE.toString())))
			.findFirst().orElse(new ApplicationParameter());
		
		ApplicationParameter test = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getNameProperty().eq(AppParam.TBAI_TEST.toString())))
			.findFirst().orElse(new ApplicationParameter());

		return new TbaiConfiguration()
				.setAdministration(administration.getValue() != null
					? Administration.safeValueOf(Integer.parseInt(administration.getValue()))
					: Administration.UNKNOWN)
				.setTest(test.getValue() != null && ("true".equalsIgnoreCase(test.getValue()) || "1".equals(test.getValue())))
				.setActive(active.getValue() != null && ("true".equalsIgnoreCase(active.getValue()) || "1".equals(active.getValue())));
	}

	public static TbaiConfiguration save(AONContext ctx, TbaiConfiguration tc) {
		ctx.checkWrite();	
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.TBAI_ACTIVE.toString(),
				Boolean.toString(tc.isActive()));
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.TBAI_TEST.toString(),
				Boolean.toString(tc.isTest()));
		
		if(tc.isActive()) {
			AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM.toString(),
				"saleInvoice");
		}
		
		return tc;
	}
	
}




