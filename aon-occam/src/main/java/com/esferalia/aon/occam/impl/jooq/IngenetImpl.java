package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IIngenet;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.impl.jooq.dao.IngenetDAO;

public class IngenetImpl implements IIngenet {

	@Override
	public Stream<Sales> getSalesStream(AONContext ctx, SalesFilter filter, Options... options) {
		return ctx.getDslContext().transactionResult(
				configuration -> IngenetDAO.getSalesStream(ctx, filter, options));
	}
	
}
