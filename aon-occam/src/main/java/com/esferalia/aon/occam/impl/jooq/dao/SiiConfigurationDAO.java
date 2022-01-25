package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SiiConfigurationDAO {
	
	private SiiConfigurationDAO() {

	}
	
	public static SiiConfiguration get(AONContext ctx) {
		ctx.checkRead();
		
		ApplicationParameter administration = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.FS_DEFAULT_ADMINISTRATION.toString())))
			.findFirst().orElse(new ApplicationParameter());

		SiiConfiguration sii = new SiiConfiguration();
		sii.setAdministration(administration.getValue() != null
				? Administration.safeValueOf(Integer.parseInt(administration.getValue()))
				: Administration.UNKNOWN);
		
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
				f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getNameProperty().like("SII_%")))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.SII_ACTIVE.toString())) {
				sii.setActive(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_TEST.toString())) {
				sii.setTest(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_AUTOSEND.toString())) {
				sii.setAutosend(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_INCLUDE_DATE.toString())) {
				sii.setIncludeDate(AonDateUtils.parse(r.getValue(), "yyyy-MM-dd"));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_REGISTRY_DATE.toString())) {
				sii.setRegistryDate(r.getValue());
			}
			
		});
		
		ApplicationParameter cfgSii = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.FS_MODEL_CFG_SII.toString())))
				.findFirst().orElse(new ApplicationParameter());
		if(cfgSii != null && !AonStringUtils.isBlank(cfgSii.getValue()) )
			sii.setRegistryDate("R".equalsIgnoreCase(cfgSii.getValue()) ? "audit" : "tax");
	
		if(sii.getIncludeDate() == null) {
    		String defaultDate = Administration.COMMON_TERRITORY.equals(administration) ? "2017-07-01" : "2018-01-01";
    		sii.setIncludeDate(AonDateUtils.parse(defaultDate, "yyyy-MM-dd"));
		}
		
		return sii;
	}

	public static SiiConfiguration save(AONContext ctx, SiiConfiguration tc) {
		ctx.checkWrite();	

		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_ACTIVE.toString(),
				Boolean.toString(tc.isActive()));
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_TEST.toString(),
				Boolean.toString(tc.isTest()));

		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_AUTOSEND.toString(),
				Boolean.toString(tc.isAutosend()));
		
		if(tc.getIncludeDate() != null) {
			AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_INCLUDE_DATE.toString(),
				AonDateUtils.format(tc.getIncludeDate(), "yyyy-MM-dd"));
		}
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_REGISTRY_DATE.toString(),
				tc.getRegistryDate());
		
		if("audit".equalsIgnoreCase(tc.getRegistryDate()) ) {
			AppParamDAO.insertApplicationParameter(ctx, 
					AppParam.FS_MODEL_CFG_SII.toString(),
					"R");
		}
		return tc;
	}
	
}




