package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IEnterprise;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseCCCFilter;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseDataFilter;
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
	public EnterpriseData getEnterpriseData(AONContext ctx, EnterpriseDataFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> EnterpriseDataDAO.get(ctx, filter));
	}
	
	@Override
	public LinkedList<EnterpriseData> getEnterpriseDataList(AONContext ctx, EnterpriseDataFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> EnterpriseDataDAO.getList(ctx, filter));
	}
	
	@Override
	public EnterpriseData saveEnterpriseData(AONContext ctx, EnterpriseData enterpriseData) {
		return ctx.getDslContext().transactionResult(
				configuration -> EnterpriseDataDAO.save(ctx, enterpriseData));
	}

	@Override
	public void insertEnterpriseData(AONContext ctx, List<EnterpriseData> enterpriseData) {
		ctx.getDslContext().transaction(
				configuration -> EnterpriseDataDAO.save(ctx, enterpriseData));
	}


	@Override
	public void updateEnterpriseData(AONContext ctx, EnterpriseData enterpriseData) {
		ctx.getDslContext().transactionResult(
				configuration -> EnterpriseDataDAO.update(ctx, enterpriseData));
	}


	@Override
	public void deleteEnterpriseData(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> EnterpriseDataDAO.delete(ctx, id));
	}
	
}
