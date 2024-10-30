package net.aonsolutions.occam.impl.handler;

import net.aonsolutions.occam.api.model.TbaiConfiguration;
import net.aonsolutions.occam.api.model.type.Administration;
import net.aonsolutions.occam.impl.AONContext;

class TbaiConfigurationHandler {
	
	private TbaiConfigurationHandler() {
	}
	
	static TbaiConfiguration get(AONContext ctx, int domain) {
		ctx.checkRead();
		return new TbaiConfiguration()
			.setAdministration( ctx.getApplicationParameters(domain)
				.getDefaultAdministration()
				.orElse(Administration.UNKNOWN))
			.setActive(ctx.getApplicationParameters(domain).isTbaiActive())
			.setTest(ctx.getApplicationParameters(domain).isTbaiTest())
			.setIncludeDate(ctx.getApplicationParameters(domain).getTbaiIncludeDate().orElse(null))
			.setRegistryDate(ctx.getApplicationParameters(domain).getTbaiRegistryDate().orElse(null))
		;
	}
/*
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
*/
}




