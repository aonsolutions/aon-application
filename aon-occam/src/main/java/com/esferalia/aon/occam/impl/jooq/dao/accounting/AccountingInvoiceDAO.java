package com.esferalia.aon.occam.impl.jooq.dao.accounting;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;

import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingRegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingInvoiceDAO {
	private AccountingInvoiceDAO() {
		
	}
	public static Optional<AccountingInvoice> getFromAccountEntry(final AONContext ctx, final Integer accountEntryId) {
		return ctx.getDslContext()
			.select( ACCOUNT_ENTRY_INVOICE.INVOICE )
			.from( ACCOUNT_ENTRY_INVOICE )
			.where(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(accountEntryId))
			.fetch()
			.stream()
			.map(rec -> rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE))
			.findFirst()
			.flatMap( invoiceIdOpt -> InvoiceDAO.getFull(ctx, invoiceIdOpt))
			.map( invoice -> new AccountingInvoice().setInvoice(invoice))
			.map( ai -> ai.fillAccountEntry(AccountEntryDAO.getAccountEntry(ctx, accountEntryId)))
			.map( ai -> fillAccountingInvoice(ctx, ai) )
		;
	}
	
	public static Optional<AccountingInvoice> getFromInvoice(final AONContext ctx, final Integer invoiceId) {
		return InvoiceDAO.getFull(ctx, invoiceId)
			.flatMap(invoice -> getFromInvoice(ctx, invoice));
	}
	
	public static Optional<AccountingInvoice> getFromInvoice(final AONContext ctx, final Invoice invoice) {
		return Optional.empty();
	}
	

	public static Optional<Account> getInvoiceDetailAccount(AONContext ctx, Integer invoiceDetailId) {
		return ctx.getDslContext()
				.select()
				.from(ACCOUNT)
				.join(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.ACCOUNT.eq(ACCOUNT.ID))
				.where(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(invoiceDetailId)).limit(1)
				.fetch()
				.stream()
				.map(new FullAccountFiller())
				.findFirst();
	}
	
	private static AccountingInvoice fillAccountingInvoice(AONContext ctx, AccountingInvoice ai) {
		Invoice invoice = ai.getInvoice();
		
		if (ai.getAccountEntry().getDetails() != null && ai.getAccountEntry().getDetails().size() > 0) {
			String concept = ai.getAccountEntry().getDetails().get(0).getConcept();
			ai.setManualConcept(AonStringUtils.substringBetween(concept,"[","]"));
		}
		
		AccountingRegistry reg = AccountingRegistryDAO.getAccountingRegistries(ctx
				, filter -> filter.getIdProperty().eq(invoice.getRegistry()))
				.filter(f -> AccountingRegistryType.getFor(invoice.getType()).equals(f.getType()))
				.findFirst()
				.orElse(null);
		ai.setRegistry(reg);
		
		
//		if (ai.getInvoice().isDUAAllowed()) {
//			fillDUAInfo(ctx, ai);
//		}
//		if (ai.getInvoice().isDUALinkAllowed()) {
//			Integer duaNationalInvoice = ctx.getDslContext()
//					.select(INVOICE_DUA.INVOICE_NATIONAL)
//					.from(INVOICE_DUA)
//					.where(INVOICE_DUA.INVOICE_IMPORT.equal(invoiceId))
//					.and(INVOICE_DUA.DOMAIN.eq(invoice.getDomain()))
//					.fetch()
//					.stream()
//					.map( rec -> rec.getValue(INVOICE_DUA.INVOICE_NATIONAL))				
//					.findFirst()
//					.orElse(null);
//			ai.setDuaNationalInvoice(duaNationalInvoice);
//		}
//		
//		if ( !ai.isAccountSource() ) {
//			InvoiceDAO.fillBreakdown(ctx, ai.getInvoice());
//		}
//		ai.getInvoice().setFinances(FinanceDAO.getInvoiceFinances(ctx, invoiceId));
//		
//		ai.setAttach(
//			ctx.getDslContext()
//				.select(INVOICE_ATTACH.ID,INVOICE_ATTACH.INVOICE,INVOICE_ATTACH.DRIVEID,INVOICE_ATTACH.MIMETYPE)
//					.from(INVOICE_ATTACH)
//					.where(INVOICE_ATTACH.INVOICE.eq(invoice.getId()))
//					.fetch()
//					.stream()
//					.map(rec -> new Attach()
//							.setId(rec.getValue(INVOICE_ATTACH.ID))
//							.setAttachModule(rec.getValue(INVOICE_ATTACH.INVOICE))
//							.setAttachType(AttachType.INVOICE)
//							.setDriveId(rec.getValue(INVOICE_ATTACH.DRIVEID))
//							.setMimeType(MimeType.safeValueOf(rec.getValue(INVOICE_ATTACH.MIMETYPE)))
//						)
//					.findFirst()
//					.orElse(null)
//			);
		return ai;
	}
	private static InvoiceVAT getInvoiceVAT(AccountingInvoice ai, InvoiceDetail det) {
		return null;
	}
	
	
	
}

