package com.code.aon.ui.finance;

import java.util.Date;
import java.util.List;

import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.registry.Registry;

public class InvoiceCalculableContainer implements ICalculableContainer {
	
	private Invoice invoice;
	private List<InvoiceDetail> details;
	
	public InvoiceCalculableContainer(Invoice invoice, List<InvoiceDetail> details) {
		this.invoice = invoice;
		this.details = details;
	}

	@Override
	public Registry getRegistry() {
		return invoice.getRegistry();
	}

	@Override
	public Date getDate() {
		return invoice.getDate();
	}

	@Override
	public DiscountExpression getDiscountExpression() {
		return invoice.getDiscountExpression();
	}

	@Override
	public List<?> getDetailList() {
		return this.details;
	}

}
