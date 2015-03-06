package com.esferalia.aon.occam.impl.jooq;

import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICommon;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;

public class CommonImpl implements ICommon {

	// ------------------ APPLICATION PARAMETERS
	@Override
	public ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		return AppParamDAO.fetchOne(ctx, param);
	}
	// ------------------ FISCAL PARAMETERS

	@Override
	public FiscalParameters getFiscalParameters(AONContext ctx) {
		return AppParamDAO.getFiscalParameters(ctx);
	}

	// ------------------ PRODUCT
	@Override
	public List<String> getProductTags(AONContext ctx) {
		return ProductDAO.getProductTags(ctx);
	}

	@Override
	public Map<Integer, String[]> getProductTagMap(AONContext ctx) {
		return ProductDAO.getProductTagMap(ctx);
	}
	

}
