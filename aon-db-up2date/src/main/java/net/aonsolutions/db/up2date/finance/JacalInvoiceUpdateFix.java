package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.AccountEntryDetail;
import com.esferalia.aon.jooq.tables.AccountEntryInvoice;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.Invoice;

import net.aonsolutions.db.up2date.Update;

public class JacalInvoiceUpdateFix implements Update {

	public static final JacalInvoiceUpdateFix JACA_INVOICE_UPDATE = new JacalInvoiceUpdateFix();

	private JacalInvoiceUpdateFix() {
		
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	
		
		if(isJacal(dslContext)) {
			Integer[] oldNumbers = {
				51, 50, 49, 48, 47, 46, 45, 44, 43, 42,
				41, 40, 39, 38, 37, 36, 35, 34, 33, 32,
				31, 30, 29, 28, 27, 26, 25, 24, 23, 3258,
				3257, 3256, 3255, 3254, 3253, 3252, 3251, 3259, 3260, 3261,
				3262, 3263, 3264, 3265, 3266, 3267, 3268, 3269, 3270, 3271,
				3272, 3273, 3274, 3275, 3276, 3277, 3278, 3279, 3280, 3281,
				3282, 3283, 3284, 3285, 3286, 3287, 3288, 3289, 3290, 3291,
				3292, 3293, 3294, 3295, 3296, 3297, 3298, 3299, 3300, 3301,
				3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311,
				3312, 3313, 3314, 3315, 3316, 3317, 3318, 3319, 3320, 3321,
				3322, 3323, 3324, 3325, 3326, 3327, 3328, 3329, 3330, 3331,
				3332, 3333, 3334, 3335, 3336, 3337, 3338, 3339, 3340, 3341,
				3342, 3343, 3344, 3345, 3346, 3347, 3348, 3349, 3350, 3351,
				3352, 3353, 3354, 3355, 3356				
			};
			
			Integer[] newNumbers = {
				59, 58, 57, 56, 55, 54, 53, 52, 51, 50,
				49, 48, 47, 46, 45, 44, 43, 42, 41, 40,
				39, 38, 37, 36, 35, 34, 33, 32, 31, 30,
				29, 28, 27, 26, 25, 24, 23, 60, 61, 62,
				63, 64, 65, 66, 67, 68, 69, 70, 71, 72,
				73, 74, 75, 76, 77, 78, 79, 80, 81, 82,
				83, 84, 85, 86, 87, 88, 89, 90, 91, 92,
				93, 94, 95, 96, 97, 98, 99, 100, 101, 102,
				103, 104, 105, 106, 107, 108, 109, 110, 111, 112,
				113, 114, 115, 116, 117, 118, 119, 120, 121, 122,
				123, 124, 125, 126, 127, 128, 129, 130, 131, 132,
				133, 134, 135, 136, 137, 138, 139, 140, 141, 142,
				143, 144, 145, 146, 147, 148, 149, 150, 151, 152,
				153, 154, 155, 156, 157
			};
			Integer invoice = getInvoice(dslContext, 9222, 3335);

			if(oldNumbers.length == newNumbers.length && invoice != null) {
				for(Integer i = 0; i < oldNumbers.length; i++) {
					fix(dslContext, oldNumbers[i], newNumbers[i]);
				}
			}
		}
	}
	
	private boolean isJacal(DSLContext dslContext) {
		Integer domain = 9222;
		String domainName = dslContext.select(Domain.DOMAIN.NAME)
			.from(Domain.DOMAIN)
			.where(Domain.DOMAIN.ID.eq(domain))
			.fetch().stream().map(r -> r.getValue(Domain.DOMAIN.NAME))
			.findFirst().orElse(null);
		
		return domainName != null && "despacho-jacal.aonsolutions.net".equalsIgnoreCase(domainName);
	}
	
	private void fix(DSLContext dslContext, Integer oldNumber, Integer newNumber) {
		Integer domain = 9222;
		Integer invoice = getInvoice(dslContext, domain, oldNumber);
		if(invoice != null) {
			updateInvoice(dslContext, domain, invoice, newNumber);
			Integer accountEntry = getAccountEntry(dslContext, domain, invoice);
			if(accountEntry != null)
				updateAccountEntryDetail(dslContext, domain, accountEntry, newNumber);
		}
	}
	
	private Integer getInvoice(DSLContext dslContext, Integer domain, Integer number) {
		return dslContext.select(Invoice.INVOICE.ID)
			.from(Invoice.INVOICE)
			.where(Invoice.INVOICE.DOMAIN.eq(domain))
			.and(Invoice.INVOICE.SERIES.eq("REN22"))
			.and(Invoice.INVOICE.NUMBER.eq(number))
			.fetch().stream().map(r -> r.getValue(Invoice.INVOICE.ID))
			.findFirst().orElse(null);
	}
	
	private void updateInvoice(DSLContext dslContext, Integer domain, Integer invoice, Integer number) {
		String reference = "REN22/" + (number.toString().length() == 2 ? "0000" + number: "000" + number) ;
		dslContext.update(Invoice.INVOICE)
			.set(Invoice.INVOICE.NUMBER, number)
			.set(Invoice.INVOICE.REFERENCE_CODE, reference)
			.where(Invoice.INVOICE.DOMAIN.eq(domain))
			.and(Invoice.INVOICE.ID.eq(invoice))
			.execute();
	}
	
	private Integer getAccountEntry(DSLContext dslContext, Integer domain, Integer invoice) {
		return dslContext.select(AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY)
			.from(AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE)
			.where(AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE.DOMAIN.eq(domain))
			.and(AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE.INVOICE.eq(invoice))
			.fetch().stream().map(r -> r.getValue(AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
			.findFirst().orElse(null);
	}
	
	private void updateAccountEntryDetail(DSLContext dslContext, Integer domain, Integer accountEntry, Integer number) {
		String reference = "REN22/" + (number.toString().length() == 2 ? "0000" + number: "000" + number) ;
		String concept = "N/Fra: " + reference;
		String documentNumber = "E-" + reference;
		dslContext.update(AccountEntryDetail.ACCOUNT_ENTRY_DETAIL)
			.set(AccountEntryDetail.ACCOUNT_ENTRY_DETAIL.CONCEPT, concept)
			.set(AccountEntryDetail.ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER, documentNumber)
			.where(AccountEntryDetail.ACCOUNT_ENTRY_DETAIL.DOMAIN.eq(domain))
			.and(AccountEntryDetail.ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(accountEntry))
			.execute();
	}
}
