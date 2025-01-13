package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.sql.Timestamp;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;

import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountingInvoice;
import net.aonsolutions.occam.api.model.Filter.InvoiceFilter;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceError;
import net.aonsolutions.occam.api.model.InvoicePreRecord;
import net.aonsolutions.occam.api.model.type.InvoiceErrorKey;
import net.aonsolutions.occam.api.model.type.InvoiceErrorLevel;
import net.aonsolutions.occam.impl.AONContext;

class InvoiceRecorderHandler {
	
	private InvoiceRecorderHandler() {
	}
	
	// -------------------------------------------------------
	// ------------------------------------------- [PROTECTED]
	// -------------------------------------------------------
	static Stream<InvoicePreRecord> getPendingInvoices(AONContext ctx, int domain,InvoiceFilter filter, int offset , int numberOfRows) {
		return InvoiceHeaderHandler.stream(ctx, domain
				, filter
				, offset, numberOfRows)
			.filter(inv -> !inv.isRecorded())
			.map( i -> new InvoicePreRecord().setHeader(i) )
			.map( ipr -> checkInvoice(ctx, domain, ipr) )
		;
	}
	static AccountingInvoice preRecordInvoice(AONContext ctx, int domain, int invoiceId) {
		Invoice inv = InvoiceHandler.get(ctx, domain, invoiceId)
			.orElseThrow(() -> new AonCoreException( AonError.INVOICE_NOT_FOUND.getMessage()));
		return preRecordInvoice(ctx, domain, inv);
	}
	static AccountingInvoice preRecordInvoice(AONContext ctx, int domain, Invoice invoice) {
		return new AccountingInvoice()
			.setInvoice(invoice)
			.setAccountEntry(InvoiceRecorder.getInvoiceEntry(ctx, domain, invoice))
		;
	}

	static AccountingInvoice recordInvoice(AONContext ctx, int domain, int invoiceId) {
		Invoice inv = InvoiceHandler.get(ctx, domain, invoiceId)
			.orElseThrow(() -> new AonCoreException( AonError.INVOICE_NOT_FOUND.getMessage()));
		return recordInvoice(ctx, domain, inv);
	}
	static AccountingInvoice recordInvoice(AONContext ctx, int domain, Invoice invoice) {
		AccountEntry ae = InvoiceRecorder.getInvoiceEntry(ctx, domain, invoice);
		AccountEntryHandler.save(ctx, domain, ae);
		
		ctx.getDslContext().insertInto(ACCOUNT_ENTRY_INVOICE)
			.set(ACCOUNT_ENTRY_INVOICE.DOMAIN,domain)
			.set(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY, ae.getId())
			.set(ACCOUNT_ENTRY_INVOICE.INVOICE, invoice.getId())
			.execute();
		ctx.log().debug("INSERT ACCOUNT_ENTRY_INVOICE");
		
		int i = ctx.getDslContext().update(INVOICE)
			.set(INVOICE.STATUS, AonEnumUtils.getByte( true  ) )
			.set(INVOICE.MODIFICATION_USER,ctx.getUser())
			.set(INVOICE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(INVOICE.ID.equal( invoice.getId() ))
			.execute();
		ctx.log().debug("UPDATE INVOICE invoice: {0} ({1} rows)",invoice.getId(),i);
		
		
		return new AccountingInvoice().setInvoice(invoice).setAccountEntry(ae);
	}

	// -------------------------------------------------------
	// --------------------------------------------- [PRIVATE]
	// -------------------------------------------------------
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_INVESTMENT = c -> {
		if (c.ipr.getHeader().isInvestment()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.INVESTMENT
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_INVESTMENT.getMessage());
			c.ipr.addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_IF_SURCHARGE = c -> {
		if (c.ipr.getHeader().isSurcharge()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.SURCHARGE
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_SURCHARGE.getMessage());
			c.ipr.addMessage(error);
		}
	};
	
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_WITHHOLDING = c -> {
		if (c.ipr.getHeader().isWithholding()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.WITHHOLDING
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_WITHHOLDING.getMessage());
			c.ipr.addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_TRANSACTION = c -> {
		if (!c.ipr.getHeader().isNational()) {
			InvoiceError error = new InvoiceError(
				InvoiceErrorKey.TRANSACTION
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_TRANSACTION.format(c.ipr.getHeader().getTransaction().getDescription()));
			c.ipr.addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_PREPAYMENT = c -> {
		if (c.ctx.getDslContext()
			.select( INVOICE_DETAIL.ID )
			.from(INVOICE_DETAIL)
			.where( INVOICE_DETAIL.INVOICE.eq(c.ipr.getHeader().getId()))
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
			c.ipr.addMessage(error);
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_EXPENSES = c -> {
		if ( c.ipr.getHeader().isExpenses() || c.ipr.getHeader().isUndeductible() ) {
			InvoiceHandler.get(c.ctx, c.domain, c.ipr.getHeader().getId())
				.ifPresent( i-> {
					i.detailStream()
						.filter( d -> d.getItem() == null)
						.filter( d -> d.getExpAccount().isEmpty())
						.findAny()
						.ifPresent( d -> {
							InvoiceError error = new InvoiceError(
								InvoiceErrorKey.EXPENSE_ACCOUNT
								,InvoiceErrorLevel.ERR
								,AonError.INVOICE_RECORDER_EXPENSE_ACCOUNT.getMessage());
							c.ipr.addMessage(error);
						})
						;
				});
		}
	};
	
	private record InvoicePreRecordContext( AONContext ctx, int domain, InvoicePreRecord ipr ) {};
	private static InvoicePreRecord checkInvoice(AONContext ctx, int domain, InvoicePreRecord ipr) {
		CHECK_IF_INVESTMENT
			.andThen(CHECK_IF_SURCHARGE)
			.andThen(CHECK_IF_WITHHOLDING)
			.andThen(CHECK_TRANSACTION)
			.andThen(CHECK_PREPAYMENT)
			.andThen(CHECK_EXPENSES)
			.accept(new InvoicePreRecordContext(ctx, domain, ipr));
		return ipr;
	}

}
