package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IEnterprise;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseCCCFilter;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseCCCDAO;

public class EnterpriseImpl implements IEnterprise {
	
	
	@Override
	public EnterpriseCCC saveEnterpriseCCC(AONContext ctx, EnterpriseCCC ec) {
		return  ctx.getDslContext().transactionResult(
				configuration -> EnterpriseCCCDAO.save(ctx, ec));
	}
	
	
	@Override
	public EnterpriseCCC getEnterpriseCCC(AONContext ctx, EnterpriseCCCFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> EnterpriseCCCDAO.get(ctx, filter));
	}

	@Override
	public Stream<EnterpriseCCC> getEnterpriseCCCStream(AONContext ctx, EnterpriseCCCFilter filter) {
		return  ctx.getDslContext().transactionResult(
				configuration -> EnterpriseCCCDAO.getStream(ctx, filter));
	}

	@Override
	public void deleteEnterpriseCCC(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> EnterpriseCCCDAO.delete(ctx, id)
		);
	}
	
}
