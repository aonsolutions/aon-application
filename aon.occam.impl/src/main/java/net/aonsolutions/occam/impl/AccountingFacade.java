package net.aonsolutions.occam.impl;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountEntryParams;
import net.aonsolutions.occam.api.model.AccountingInvoice;
import net.aonsolutions.occam.api.model.FlatAccountEntryDetail;
import net.aonsolutions.occam.api.model.InvoicePreRecord;
import net.aonsolutions.occam.api.model.Occam;
import net.aonsolutions.occam.impl.AONContext.CloseableAONContext;
import net.aonsolutions.occam.impl.handler.AccountingBridge;

public class AccountingFacade {
	
	private AccountingFacade() {
	}
	
	// ----------------------------------------- 
	// ------------------------- [ACCOUNT] -----
	// -----------------------------------------
	public static LinkedList<Account> getAccounts(Occam occam, int domain, String query) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.getAccounts(ctx, domain, query);
		}
	}

	public static Optional<Account> getAccount(Occam occam, int domain, String code) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.getAccount(ctx, domain, code);
		}
	}
	
	
	public static Stream<FlatAccountEntryDetail> getFlatAccountEntries(Occam occam, int domain, final AccountEntryParams params, int offset,int limit) throws AonCoreException {
	 	final CloseableAONContext ctx = AONContext.getAONContext(occam);
	 	return AccountingBridge.getFlatAccountEntries(ctx, domain, params, offset, limit
 			, () -> { if (ctx != null) ctx.close();}
	 	);
	}
	
	public static Account delete(Occam occam, int domain, Account account) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.delete(ctx, domain, account);
		}
	}
	
	// ----------------------------------------- 
	// ------------------- [ACCOUNT ENTRY] -----
	// -----------------------------------------
	public static Optional<AccountEntry> getAccountEntry(Occam occam, int domain, int accountEntryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.getAccountEntry(ctx, domain, accountEntryId);
		}
	}
	
	public static AccountEntry resetAccountEntry(Occam occam, int domain, AccountEntry last) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.resetAccountEntry(ctx, domain, last);
		}
	}

	public static AccountEntry saveAndGet(Occam occam, int domain, AccountEntry accountEntry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.saveAndGet(ctx, domain, accountEntry);
		}
	}

	public static AccountEntry delete(Occam occam, int domain, AccountEntry accountEntry) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.delete(ctx, domain, accountEntry);
		}
	}

	// ---------------------------------------------- 
	// ------------------- [ACCOUNTING INVOICE] -----
	// ----------------------------------------------
	public static AccountingInvoice getAccountingInvoice(Occam occam, int domain, Integer accountEntryId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.getAccountingInvoice(ctx, domain, accountEntryId);
		}
	}

	// ---------------------------------------------- 
	// ------------------- [INVOICE RECORDER] -------
	// ----------------------------------------------
	public static LinkedList<InvoicePreRecord> getPendingInvoices(Occam occam, int domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.getPendingInvoices(ctx, domain);
		}
	}
	public static Integer getPendingInvoicesCount(Occam occam, int domain) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.getPendingInvoicesCount(ctx, domain);
		}
	}
	public static AccountingInvoice preRecordInvoice(Occam occam, int domain, int invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.preRecordInvoice(ctx, domain, invoiceId);
		}
	}
	public static AccountingInvoice recordInvoice(Occam occam, int domain, int invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return AccountingBridge.recordInvoice(ctx, domain, invoiceId);
		}
	}
	
	
}
