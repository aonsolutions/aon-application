package net.aonsolutions.aon.verifactu;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

final class VerifactuEnvironment extends VerifactuEnvironmentAbs {
	protected String domainName = "verifactutest.aonsolutions.test";	
	protected String user 		= "admin";
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	@Override
	public String getUser() {
		return user;
	}
	
	public void initializeDomain(AONContext ctx) {
		ApplicationParameter verifactuActiveParam = AppParamDAO.fetchOne(ctx, AppParam.VERIFACTU_ACTIVE.toString());
		if (verifactuActiveParam == null || verifactuActiveParam.getId() == null) {
			ctx.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, ctx.getDomainId())
			.set(APP_PARAM.NAME, AppParam.VERIFACTU_ACTIVE.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
			ctx.log().info("App Param VERIFACTU_ACTIVE set to TRUE");
		}
		
		ApplicationParameter verifactuTestParam = AppParamDAO.fetchOne(ctx, AppParam.VERIFACTU_TEST.toString());
		if (verifactuTestParam == null || verifactuTestParam.getId() == null) {
			ctx.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, ctx.getDomainId())
			.set(APP_PARAM.NAME, AppParam.VERIFACTU_TEST.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
			ctx.log().info("App Param VERIFACTU_TEST set to TRUE");
		}
		
		ApplicationParameter verifactuIncludeDateParam = AppParamDAO.fetchOne(ctx, AppParam.VERIFACTU_INCLUDE_DATE.toString());
		if (verifactuIncludeDateParam == null || verifactuIncludeDateParam.getId() == null) {
			String date = AonDateUtils.format(new Date(), "yyyy-MM-dd");
			ctx.getDslContext().insertInto(APP_PARAM)
				.set(APP_PARAM.DOMAIN, ctx.getDomainId())
				.set(APP_PARAM.NAME, AppParam.VERIFACTU_INCLUDE_DATE.toString())
				.set(APP_PARAM.VALUE, date)
			.execute();
			ctx.log().info("App Param VERIFACTU_INCLUDE_DATE set to " + date);
		}
	}
}
