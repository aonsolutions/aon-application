package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICommission;
import com.esferalia.aon.occam.api.model.Filter.CommissionCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionItemFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionTypeCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.OfferDetailCommissionFilter;
import com.esferalia.aon.occam.api.model.commission.Commission;
import com.esferalia.aon.occam.api.model.commission.CommissionCategory;
import com.esferalia.aon.occam.api.model.commission.CommissionItem;
import com.esferalia.aon.occam.api.model.commission.CommissionTypeCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommission;
import com.esferalia.aon.occam.impl.jooq.dao.CommissionDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;

public class CommissionImpl implements ICommission {

	@Override
	public Stream<OfferDetailCommission> getOfferDetailCommissionStream(AONContext ctx, OfferDetailCommissionFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CommissionDAO.getOfferDetailCommissionStream(ctx, filter));
	}

	@Override
	public Stream<Commission> getCommissionStream(AONContext ctx, CommissionFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CommissionDAO.getCommissionStream(ctx, filter));
	}

	@Override
	public Stream<CommissionTypeCommission> getCommissionTypeCommissionStream(AONContext ctx, CommissionTypeCommissionFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CommissionDAO.getCommissionTypeCommissionStream(ctx, filter));
	}

	@Override
	public Stream<CommissionCategory> getCommissionCategoryStream(AONContext ctx, CommissionCategoryFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CommissionDAO.getCommissionCategoryStream(ctx, filter));
	}

	@Override
	public Stream<CommissionItem> getCommissionItemStream(AONContext ctx, CommissionItemFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> CommissionDAO.getCommissionItemStream(ctx, filter));
	}

	@Override
	public OfferDetailCommission insertOfferDetailCommission(AONContext ctx, OfferDetailCommission odc) {
		return ctx.getDslContext().transactionResult(configuration -> CommissionDAO.insertOfferDetailCommission(ctx, odc));
	}

	@Override
	public OfferDetailCommission updateOfferDetailCommission(AONContext ctx, OfferDetailCommission odc) {
		return ctx.getDslContext().transactionResult(configuration -> CommissionDAO.updateOfferDetailCommission(ctx, odc));
	}
}
