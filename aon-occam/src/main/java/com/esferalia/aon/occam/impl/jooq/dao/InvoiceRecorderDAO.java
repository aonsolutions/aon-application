package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.util.AonCollectionUtils;

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

	private record InvoicePreRecordContext( AONContext ctx, int domain, InvoiceConsole ic ) {};
	static InvoiceConsole fillMessages(AONContext ctx, int domain, InvoiceConsole ic) {
		CHECK_IF_INVESTMENT
			.andThen(CHECK_IF_SURCHARGE)
			.andThen(CHECK_IF_WITHHOLDING)
			.andThen(CHECK_TRANSACTION)
			.andThen(CHECK_PREPAYMENT)
			.andThen(CHECK_EXPENSES)
			.accept(new InvoicePreRecordContext(ctx, domain, ic));
		return ic;
	}

	// ************************************************************************************** 	
	// *************************************************************************** [CHECK] ** 	
	// ************************************************************************************** 	
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_INVESTMENT = c -> {
		if (c.ic.getInvoice().isInvestment()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.INVESTMENT
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_INVESTMENT.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_IF_SURCHARGE = c -> {
		if (c.ic.getInvoice().isSurcharge()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.SURCHARGE
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_SURCHARGE.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};
	
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_WITHHOLDING = c -> {
		if (c.ic.getInvoice().isWithholding()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.WITHHOLDING
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_WITHHOLDING.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_TRANSACTION = c -> {
		if (!c.ic.getInvoice().isNational()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.TRANSACTION
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_TRANSACTION.format(c.ic.getInvoice().getTransaction().getDescription()));
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_PREPAYMENT = c -> {
		if (c.ctx.getDslContext()
			.select( INVOICE_DETAIL.ID )
			.from(INVOICE_DETAIL)
			.where( INVOICE_DETAIL.INVOICE.eq(c.ic.getInvoice().getId()))
			.and(INVOICE_DETAIL.PREPAYMENT.eq((byte) 1))
			.limit(1)
			.fetch()
			.stream()
			.findFirst()
			.isPresent()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.GENERIC
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_PREPAYMENT.getMessage());
			c.ic.getInvoice().addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_EXPENSES = c -> {
		if ( c.ic.getInvoice().isExpenses() || c.ic.getInvoice().isUndeductible() ) {
			Optional.ofNullable( InvoiceDAO.getFullInvoice(c.ctx, c.ic.getInvoice().getId()) )
				.ifPresent( i-> {
					AonCollectionUtils.stream( i.getDetails() )
						.filter( d -> d.getAccount() == null )
						.findAny()
						.ifPresent( d -> {
							InvoiceError error = new InvoiceError(
								InvoiceErrorKey.EXPENSE_ACCOUNT
								,InvoiceErrorLevel.ERR
								,AonError.INVOICE_RECORDER_EXPENSE_ACCOUNT.getMessage());
							c.ic.getInvoice().addMessage(error);
						})
						;
				});
		}
	};
}
