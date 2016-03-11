package com.code.aon.accounting.util;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountingFinanceChecker implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final byte PENDING = (byte) FinanceStatus.PENDING.ordinal();
	private static final byte BATCHED = (byte) FinanceTrackingType.BATCHED.ordinal();
	private static final byte PAID = (byte) FinanceTrackingType.PAID.ordinal();
	private static final byte RETURNED = (byte) FinanceTrackingType.RETURNED.ordinal();
	private static final byte FRACTIONED = (byte) FinanceTrackingType.FRACTIONED.ordinal();
	private static final byte SETTLED = (byte) FinanceTrackingType.SETTLED.ordinal();
	private static final Field<BigDecimal> SUM_AMOUNT = DSL.sum(FINANCE.AMOUNT);
	private static final Field<Byte> PENDING_FIELD = DSL.val(PENDING);
	private static final Field<Byte> FINANCE_TRACKING_STATUS_DECODE = DSL.decode()
			.when(FINANCE_TRACKING.TYPE.eq(BATCHED), PENDING)
			.when(FINANCE_TRACKING.TYPE.eq(PAID), PAID)
			.when(FINANCE_TRACKING.TYPE.eq(RETURNED), PENDING)
			.when(FINANCE_TRACKING.TYPE.eq(FRACTIONED), PENDING)
			.when(FINANCE_TRACKING.TYPE.eq(SETTLED), PAID)
	;

	private static Condition getPendingFinanceTrackingCondition(AONContext ctx, java.sql.Date date) {
		return PENDING_FIELD.eq(
				DSL.isnull(
					DSL.field(ctx.getDslContext().select(FINANCE_TRACKING_STATUS_DECODE)
						.from(FINANCE_TRACKING)
						.where(FINANCE_TRACKING.FINANCE.eq(FINANCE.ID))
						.and(FINANCE_TRACKING.TRACKING_DATE.le(date))
						.orderBy(FINANCE_TRACKING.ID.desc())
						.limit(1)
				),PENDING_FIELD));
	}

	public static Collection<AccountingFinanceCheck> getChecks(String domainName,int domainId
			,String user,AccountingFinanceCheckerParams params) throws AonException {
		AONContext ctx = null;
		try {
			java.sql.Date date = AonDateUtils.toSql( params.getDeadline() );
			ctx = AONContext.getAONContext(domainName, domainId, user);
			final Map<Integer,AccountingFinanceCheck> financeMap = ctx.getDslContext()
				.select(FINANCE.REGISTRY,FINANCE.RNAME,SUM_AMOUNT)
				.from(FINANCE)
				.join(INVOICE).on(FINANCE.INVOICE.eq(INVOICE.ID)).and(INVOICE.ISSUE_DATE.le(date))
				.where(FINANCE.DOMAIN.eq(ctx.getDomainId()))
				.and( getPendingFinanceTrackingCondition(ctx, date) )
				.groupBy(FINANCE.REGISTRY)
				.fetch()
				.stream()
				.map(record -> new AccountingFinanceCheck()
								.setRegistryId( record.getValue(FINANCE.REGISTRY) )
								.setRegistryName(record.getValue(FINANCE.RNAME) )
								.setFinBalance( record.getValue(SUM_AMOUNT) ) )
				.collect(Collectors.toMap(AccountingFinanceCheck::getRegistryId
						,check -> check
						))
			;
			Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
			Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
			
			
			Select<Record2<Integer,Integer>> customerAccount = ctx.getDslContext().select(CUSTOMER.REGISTRY,CUSTOMER.ACCOUNT).from(CUSTOMER).where(CUSTOMER.DOMAIN.eq(ctx.getDomainId())); 
			Select<Record2<Integer,Integer>> creditorAccount = ctx.getDslContext().select(CREDITOR.REGISTRY,CREDITOR.ACCOUNT).from(CREDITOR).where(CREDITOR.DOMAIN.eq(ctx.getDomainId())); 
			Select<Record2<Integer,Integer>> supplierAccount = ctx.getDslContext().select(SUPPLIER.REGISTRY,SUPPLIER.ACCOUNT).from(SUPPLIER).where(SUPPLIER.DOMAIN.eq(ctx.getDomainId()));
			
			Select<Record2<Integer,Integer>> from = null;
			if (params.isCustomersEnabled()) {
				from = customerAccount;	
			}
			if (params.isCreditorsEnabled()) {
				from = from==null?creditorAccount:from.union(creditorAccount);
			}
			if (params.isSuppliersEnabled()) {
				from = from==null?supplierAccount:from.union(supplierAccount);
			}
			
			Table<Record> REGISTRY_ACCOUNT = 
					ctx.getDslContext().select()
					.from( from )
					.asTable("REGISTRY_ACCOUNT"); 
			
			@SuppressWarnings("unchecked")
			Field<Integer> REGISTRY_ID = (Field<Integer>) REGISTRY_ACCOUNT.field(0);
			@SuppressWarnings("unchecked")
			Field<Integer> ACCOUNT_ID = (Field<Integer>) REGISTRY_ACCOUNT.field(1);
			
			final Map<Integer,AccountingFinanceCheck> accountingMap = ctx.getDslContext().select(
					REGISTRY_ID
					,REGISTRY.NAME
					,ACCOUNT_ID
					,ACCOUNT.CODE
					,ACCOUNT.DESCRIPTION
					,sumDebit
					,sumCredit)
			.from(  ACCOUNT_ENTRY_DETAIL )
			.join( ACCOUNT_ENTRY).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID)).and(ACCOUNT_ENTRY.ENTRY_DATE.le(date))
			.join(REGISTRY_ACCOUNT).on(ACCOUNT_ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
			.join(ACCOUNT).on(ACCOUNT_ID.eq(ACCOUNT.ID))
			.join(REGISTRY).on(REGISTRY_ID.eq(REGISTRY.ID))
			.groupBy(REGISTRY_ID)
			.having(DSL.round(sumDebit).ne(DSL.round(sumCredit)))
			.fetch()
			.stream()
			.map(record -> new AccountingFinanceCheck()
							.setRegistryId( record.getValue(REGISTRY_ID) )
							.setRegistryName( record.getValue(REGISTRY.NAME) )
							.setAccountId( record.getValue(ACCOUNT_ID) )
							.setAccountCode( record.getValue(ACCOUNT.CODE) )
							.setAccountDescription( record.getValue(ACCOUNT.DESCRIPTION) )
							.setDebit( AonMathUtils.round( record.getValue(sumDebit).doubleValue()))
							.setCredit( AonMathUtils.round( record.getValue(sumCredit).doubleValue())))
			.collect( Collectors.toMap(
					AccountingFinanceCheck::getRegistryId
					, check -> check
					))
			;
			
			
			//TODO.  Merge maps. do it more ..... beauty.
			final AONContext ctx2 = ctx;
			financeMap.entrySet()
			.stream()
			.forEach( entry -> {
				if (accountingMap.containsKey(entry.getKey())){
					accountingMap.get(entry.getKey()).setFinBalance(entry.getValue().getFinBalance());
				} else {
					boolean toAdd = false;
					if (params.isCreditorsEnabled() && params.isCustomersEnabled() && params.isCreditorsEnabled()) {
						toAdd = true;
					} else {
						Field<Integer> f = DSL.count();
						if (params.isCustomersEnabled()) {
							toAdd = ctx2.getDslContext().select(f).from(CUSTOMER).where(CUSTOMER.REGISTRY.equal(entry.getKey())).fetchOne(f) > 0;
						}
						if (!toAdd && params.isCreditorsEnabled()) {
							toAdd = ctx2.getDslContext().select(f).from(CREDITOR).where(CREDITOR.REGISTRY.equal(entry.getKey())).fetchOne(f) > 0;							
						}
						if (!toAdd && params.isSuppliersEnabled()) {
							toAdd = ctx2.getDslContext().select(f).from(SUPPLIER).where(SUPPLIER.REGISTRY.equal(entry.getKey())).fetchOne(f) > 0;
						}
					}
					if (toAdd) {
						accountingMap.put(entry.getKey(),entry.getValue());
					}
				}
				})
			;
			LinkedHashMap<String, AccountingFinanceCheck> map = new LinkedHashMap<String, AccountingFinanceCheck>(); 
			accountingMap.entrySet().stream().forEach(entry -> {
				AccountingFinanceCheck check = accountingMap.get(entry.getKey());
				if (check.getDifference() != 0 ) {
					map.put(check.getAccountCode(), check);
				}
			} );
			return map.values();
		} finally {
			if (ctx != null) {
				ctx.close();
			}
		}
	}

	public static List<StrippedStatement> getStrippedStatement(String domainName, int domainId
			,String user, AccountingFinanceCheckerParams params ) throws AonException {
		AONContext ctx = null;
		try {
			java.sql.Date date = AonDateUtils.toSql( params.getDeadline() );
			ctx = AONContext.getAONContext(domainName, domainId,user);
			final String DEFAULT_DOCUMENT = "APUNTES SIN N\u00DAMERO DE DOCUMENTO";
			Field<String> doc = DSL.nvl(DSL.trim(ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER),DEFAULT_DOCUMENT);
			Field<BigDecimal> sumDebit = DSL.sum(ACCOUNT_ENTRY_DETAIL.DEBIT); 
			Field<BigDecimal> sumCredit = DSL.sum(ACCOUNT_ENTRY_DETAIL.CREDIT);
			return ctx.getDslContext()
				.select(doc,sumDebit,sumCredit)
				.from(ACCOUNT)
				.join(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(ACCOUNT.ID))
				.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
				.where(ACCOUNT.DOMAIN.eq(domainId))
				.and(ACCOUNT.CODE.eq(params.getAccountCode()))
				.and(ACCOUNT_ENTRY.ENTRY_DATE.le(date))
				.groupBy(doc)
				.having(DSL.round(sumDebit).ne(DSL.round(sumCredit)))
				.orderBy(doc.desc())
				.fetch()
				.stream()
				.map(record -> new StrippedStatement()
					.setDocumentNumber(record.getValue(doc))
					.setDebit(record.getValue(sumDebit))
					.setCredit(record.getValue(sumCredit))
					.setEmptyDocument( DEFAULT_DOCUMENT.equals(record.getValue(doc)) ))
				.collect(Collectors.toCollection(LinkedList::new));
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static List<AccountingFinanceCheck> getFinances(String domainName, int domainId
			,String user, AccountingFinanceCheckerParams params ) throws AonException {
		AONContext ctx = null;
		try {
			java.sql.Date date = AonDateUtils.toSql( params.getDeadline() );
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return ctx.getDslContext()
				.select(FINANCE.REGISTRY
						,FINANCE.RNAME
						,FINANCE.CONCEPT
						,FINANCE.STATUS
						,FINANCE.AMOUNT)
				.from(FINANCE)
				.join(INVOICE).on(FINANCE.INVOICE.eq(INVOICE.ID)).and(INVOICE.ISSUE_DATE.le(date))
				.where(FINANCE.DOMAIN.eq(ctx.getDomainId()))
				.and( FINANCE.REGISTRY.eq(params.getRegistryId()))
				.and( getPendingFinanceTrackingCondition(ctx, date) )
				.orderBy(FINANCE.DUE_DATE)
				.fetch()
				.stream()
				.map(record -> new AccountingFinanceCheck()
								.setRegistryId( record.getValue(FINANCE.REGISTRY) )
								.setRegistryName(record.getValue(FINANCE.RNAME) )
								.setDocumentNumber(record.getValue(FINANCE.CONCEPT) )
								.setFinanceStatus(record.getValue(FINANCE.STATUS) )
								.setFinBalance( record.getValue(FINANCE.AMOUNT) ) )
				.collect(Collectors.toCollection(LinkedList::new ))
			;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
}
