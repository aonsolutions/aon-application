package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IExpedient;
import com.esferalia.aon.occam.api.model.Expedient;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.impl.jooq.dao.ExpedientDAO;

public class ExpedientImpl implements IExpedient {

	@Override
	public Stream<Expedient> getResumeExpedientStream(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> ExpedientDAO.resumeExpedient(ctx, domain, filter));
	}

	@Override
	public Stream<Expedient> getFullExpedientStream(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> ExpedientDAO.fullExpedient(ctx, domain, filter));
	}
}
