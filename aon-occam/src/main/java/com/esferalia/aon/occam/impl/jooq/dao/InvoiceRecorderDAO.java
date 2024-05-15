package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;

public class InvoiceRecorderDAO {
	
	private InvoiceRecorderDAO() {
		
	}
	
	public static AccountEntry getEntryBase(AONContext ctx, AonConfiguration aonCtx, Invoice invoice) {
		EnterpriseActivity ea = !invoice.getActivity().isEmpty() ? invoice.getActivity() : aonCtx.getMainActivity();
		Integer activity = (ea==null?null:ea.getId());
		Integer periodId = null;
		if (invoice.getIssueDate() != null) {
			AccountPeriod period = ACCOUNTING.ensurePeriod(ctx, ctx.getDomainId(), invoice.getIssueDate());
			periodId = (period == null? null : period.getId());
		}
		AccountEntry accountEntry = new AccountEntry()
				.setPeriod(periodId)
				.setDomain(invoice.getDomain())
				.setConfidential(false)
				.setEntryDate(invoice.getIssueDate())
				.setActivity(activity)
				.setComments(invoice.getComments())
				.setDirty(false);
		invoice.getType().visit(invoice,  new IInvoiceTypeVisitor<Void>() {
			@Override 
			public Void visitUndeductible(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(true);
				return null;
			}
			@Override 
			public Void visitSales(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);
				return null;
			}
			@Override 
			public Void visitPurchase(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);
				return null;
			}
			@Override 
			public Void visitExpenses(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(false);
				return null;
			}
		});
		return accountEntry;
	}
	
}
