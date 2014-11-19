package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICommon;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;

public class CommonImpl implements ICommon {

	// ------------------ APPLICATION PARAMETERS

	@Override
	public ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		return AppParamDAO.fetchOne(ctx, param);
	}

}
