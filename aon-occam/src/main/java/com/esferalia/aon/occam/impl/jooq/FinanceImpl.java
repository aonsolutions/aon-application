package com.esferalia.aon.occam.impl.jooq;

import java.util.function.Consumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFinance;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;

public class FinanceImpl implements IFinance {

	@Override
	public void getInvoiceDetails(AONContext ctx,
			Consumer<InvoiceDetail> action, InvoiceFilter filter) {
		InvoiceDAO.getInvoiceDetails(ctx, action, filter);
	}


}
