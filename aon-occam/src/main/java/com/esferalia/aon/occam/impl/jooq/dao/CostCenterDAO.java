package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;

import java.util.List;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CostCenterDAO {
	
	private CostCenterDAO() {}
	
	public static List<ApplicationParameter> getList(CloseableAONContext ctx) {
		ctx.checkRead();
		Condition condition = APP_PARAM.NAME.like("%ACC_COST_CENTER_%").and(APP_PARAM.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
		List<ApplicationParameter> result = AppParamDAO.getApplicationParameters(ctx, condition);
		return result;
	}

	public static void save(CloseableAONContext ctx, ApplicationParameter costCenter) {
		if(null == costCenter.getId()) insert(ctx, costCenter);
		else update(ctx, costCenter);
	}

	private static void insert(CloseableAONContext ctx, ApplicationParameter costCenter) {
		ctx.checkWrite();
		ApplicationParameter insertedCostCenter = AppParamDAO.insertApplicationParameter(ctx, costCenter);
		ctx.log().debug("INSERT COST CENTER id: {0}", insertedCostCenter.getId());
	}


	private static void update(CloseableAONContext ctx, ApplicationParameter costCenter) {
		ctx.checkWrite();
		ApplicationParameter oldCostCenter = AppParamDAO.fetchOne(ctx, costCenter.getName());
		AppParamDAO.updateApplicationParameter(ctx, costCenter, f -> f.getIdProperty().eq(costCenter.getId()));
		
		// Update account.cost_center
		if(!AonStringUtils.equalsIgnoreCase(oldCostCenter.getValue(), costCenter.getValue())) {
			ctx.getDslContext().update(ACCOUNT)
				.set(ACCOUNT.COST_CENTER, costCenter.getValue())
				.where(ACCOUNT.COST_CENTER.eq(oldCostCenter.getValue()))
				.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.execute();
		}
		
		ctx.log().debug("UPDATE COST CENTER id: {0}", costCenter.getId());
	}

	public static void delete(CloseableAONContext ctx, Integer id) {
		ctx.checkWrite();
		ApplicationParameter oldCostCenter = AppParamDAO.fetchOne(ctx, id);
		AppParamDAO.deleteApplicationParameter(ctx, f -> f.getIdProperty().eq(id));
		
		// Update account.cost_center
		if(AonStringUtils.isNotBlank(oldCostCenter.getValue())) {
			ctx.getDslContext().update(ACCOUNT)
				.set(ACCOUNT.COST_CENTER, DSL.castNull(ACCOUNT.COST_CENTER))
				.where(ACCOUNT.COST_CENTER.eq(oldCostCenter.getValue()))
				.and(ACCOUNT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.execute();
		}
		
		ctx.log().debug("DELETE COST CENTER id: {0}", id);
	}
	
}




