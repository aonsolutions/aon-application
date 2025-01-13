package net.aonsolutions.occam.impl.handler;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountEntryParams;
import net.aonsolutions.occam.api.model.AccountingInvoice;
import net.aonsolutions.occam.api.model.FlatAccountEntryDetail;
import net.aonsolutions.occam.api.model.InvoicePreRecord;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.IHandlerCallback;

public class AccountingBridge {
	private AccountingBridge() {
		
	}
	
	// ----------------------------------------- 
	// ------------------- [ACCOUNT ENTRY] -----
	// -----------------------------------------
	public static LinkedList<Account> getAccounts(AONContext ctx, int domain, String query) {
		final String q = parse(query);
		return AccountHandler.stream(ctx, domain, 
				p ->  p.getActiveProperty().eq((byte) 1)
					.and(p.getEntryEnabledProperty().eq((byte) 1))
					.and(p.getCodeProperty().like(q)
					 .or(p.getDescriptionProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				,0,50)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Optional<Account> getAccount(AONContext ctx, int domain, String code) {
		return AccountHandler.get(ctx, domain, code);
	}
	public static Stream<FlatAccountEntryDetail> getFlatAccountEntries(AONContext ctx
			, int domain
			, AccountEntryParams params
			, int offset
			, int limit
			, IHandlerCallback callback) {
		return AccountEntryHandler.flatStream(ctx, domain, params, offset, limit, callback);
	}
	
	public static Account delete(AONContext ctx, int domain, Account account) {
		return AccountHandler.delete(ctx, domain, account );
	}
	
	// ----------------------------------------- 
	// ------------------- [ACCOUNT ENTRY] -----
	// -----------------------------------------
	
	public static Optional<AccountEntry> getAccountEntry(AONContext ctx, int domain, int accountEntryId) {
		return AccountEntryHandler.get(ctx, domain, accountEntryId);
	}

	public static AccountEntry resetAccountEntry(AONContext ctx, int domain, AccountEntry last) {
		return AccountEntryHandler.reset(ctx, domain, Optional.ofNullable(last));
	}
	
	public static Integer save(AONContext ctx, int domain, AccountEntry accountEntry) {
		return AccountEntryHandler.save(ctx, domain, accountEntry);
	}
	public static AccountEntry saveAndGet(AONContext ctx, int domain, AccountEntry accountEntry) {
		Integer id = AccountEntryHandler.save(ctx, domain, accountEntry);
		return AccountEntryHandler.get(ctx, domain, id)
			.orElseThrow( () -> new AonCoreException( AonError.NOT_SAVED.getMessage()) );
	}
	
	public static AccountEntry delete(AONContext ctx, int domain, AccountEntry accountEntry) {
		return AccountEntryHandler.delete(ctx, domain, accountEntry.getId() );
	}
	
	// ---------------------------------------------- 
	// ------------------- [ACCOUNTING INVOICE] -----
	// ----------------------------------------------
	public static AccountingInvoice getAccountingInvoice(AONContext ctx, int domain, Integer accountEntryId) {
		return AccountingInvoiceHandler.getFromAccountEntry(ctx, domain, accountEntryId ).orElse(null);
	}
	
	// ----------------------------------------- 
	// ------------------------- [PRIVATE] -----
	// -----------------------------------------
	
	private static String parse( String query) {
		if (AonStringUtils.contains(query, AonStringUtils.PERCENT)) return query;
		StringBuilder buff = new StringBuilder();
		if (!AonStringUtils.isNumeric(query)) {
			buff.append(AonStringUtils.PERCENT);
		}
		buff.append(query);
		buff.append(AonStringUtils.PERCENT);
		return buff.toString();
	}

	// ---------------------------------------------- 
	// ------------------- [INVOICE RECORDER] -------
	// ----------------------------------------------
	public static LinkedList<InvoicePreRecord> getPendingInvoices(AONContext ctx, int domain) {
		return InvoiceRecorderHandler.getPendingInvoices(ctx, domain,
			f -> f.getStatusProperty().ne( (byte) 1 )
			, 0, Integer.MAX_VALUE )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	public static Integer getPendingInvoicesCount(AONContext ctx, int domain) {
		return AonCollectionUtils.size(getPendingInvoices(ctx, domain));
	}

	public static AccountingInvoice preRecordInvoice(AONContext ctx, int domain, int invoiceId) {
		return InvoiceRecorderHandler.preRecordInvoice(ctx, domain, invoiceId);
	}

	public static AccountingInvoice recordInvoice(AONContext ctx, int domain, int invoiceId) {
		return InvoiceRecorderHandler.recordInvoice(ctx, domain, invoiceId);
	}
}
