package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IEnterprise;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseCCCFilter;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseCCCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDataDAO;

public class EnterpriseImpl implements IEnterprise {
	
	// --------------------------- ENTERPRISE CCC
	
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

	// --------------------------- ENTERPRISE DATA
	@Override
	public Optional<EnterpriseData> getEnterpriseData(AONContext ctx, Integer domainId, EnterpriseDataNames name) {
		return ctx.getDslContext().transactionResult(
				configuration -> EnterpriseDataDAO.get(ctx, domainId, name));
	}
	@Override
	public LinkedList<EnterpriseData> getEnterpriseDataList(AONContext ctx, Integer domainId) {
		return ctx.getDslContext().transactionResult( configuration -> 
		EnterpriseDataDAO.streamByDomain(ctx, domainId)
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	@Override
	public EnterpriseData saveEnterpriseData(AONContext ctx, EnterpriseData enterpriseData) {
		return ctx.getDslContext().transactionResult(
				configuration -> EnterpriseDataDAO.save(ctx, enterpriseData));
	}
}
