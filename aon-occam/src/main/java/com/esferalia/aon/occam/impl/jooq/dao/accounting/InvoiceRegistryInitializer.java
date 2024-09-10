package com.esferalia.aon.occam.impl.jooq.dao.accounting;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType.AccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceOLDDAO;

public class InvoiceRegistryInitializer implements AccountingRegistryTypeVisitor {
	private AONContext ctx;
	private Invoice invoice;
	private AonConfiguration config;
	private AccountingRegistry reg;
	
	public InvoiceRegistryInitializer(AONContext ctx,Invoice invoice,AonConfiguration config, AccountingRegistry reg) {
		this.ctx = ctx;
		this.invoice = invoice;
		this.config = config;
		this.reg = reg;
	}
	
	private void visitCommon() {
		invoice.setScope(new Scope().setId( reg.getScope() ));
		invoice.setRegistryDocumentType(reg.getDocumentType());
		invoice.setRegistryDocumentCountry(reg.getDocumentCountry());
		invoice.setRegistryDocument(reg.getDocument());
		invoice.setRegistryName(reg.getName());
		invoice.setVatAccrualPayment(invoice.isNational()
			&& invoice.getIssueDate() != null
			&& !invoice.getIssueDate().before(InvoiceOLDDAO.VAT_ACCRUAL_START_DATE)
			&& ( config.getCompany().isVatAccrualPayment() || reg.isVatAccrualPayment()));
	}

	@Override
	public void visitCustomer() {
		visitCommon();
		invoice.setSurcharge(reg.isSurcharge());
		invoice.setWithholding(reg.isWithholding() && config.getCompany().isWithholding());
		invoice.setWithholdingFarmer(false);
		invoice.setSeries(config.getDefaultInvoiceSeries());
		invoice.setNumber( InvoiceOLDDAO.getNextNumber(ctx, new Byte[]{invoice.getType().value()}, invoice.getSeries()));
	}

	@Override
	public void visitSupplier() {
		invoice.setSurcharge(config.getCompany().isSurcharge());
		invoice.setWithholding(reg.isWithholding());
		invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
		visitCommon();
	}

	@Override
	public void visitCreditor() {
		invoice.setSurcharge(false);
		invoice.setWithholding(reg.isWithholding());
		invoice.setWithholdingFarmer(reg.isWithholdingFarmer());
		visitCommon();
	}
	@Override
	public void visitUndedCreditor() {
		visitCommon();
		invoice.setSurcharge(false);
		invoice.setWithholding(false);
		invoice.setWithholdingFarmer(false);
		invoice.setVatAccrualPayment(false);
	}
}
