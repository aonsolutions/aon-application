package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.EnterpriseActivity;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.ActivityIntegrityItem;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceInvoiceIntegrityItem;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.finance.utilities.MissingFinanceInvoiceItem;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class FinanceUtilitiesDAO {

	private static byte TRUE = 1;
	private static byte FALSE = 0;

	public static FinanceUtilitiesResult missingFinanceInvoices(AONContext ctx, FinanceUtilitiesParams params) {
		FinanceUtilitiesResult result = new FinanceUtilitiesResult();
		missingFinanceInvoices(ctx,params,result);
		return result;
	}	
				
	private static void missingFinanceInvoices(AONContext ctx, FinanceUtilitiesParams params, FinanceUtilitiesResult result) {
		Condition where = INVOICE.DOMAIN.equal(ctx.getDomainId())
				.and(INVOICE.TOTAL.ne(0.0))
				.and(FINANCE.ID.isNull());
		if (params != null && params.getInvoiceType() != null) {
			where = where.and(INVOICE.TYPE.eq( params.getInvoiceType().value()));
		}
		if (params != null && params.getFromDate() != null) {
			where = where.and(INVOICE.ISSUE_DATE.ge( AonDateUtils.toSql( params.getFromDate() )));
		}
		if (params != null && params.getToDate() != null) {
			where = where.and(INVOICE.ISSUE_DATE.le( AonDateUtils.toSql( params.getToDate() )));
		}
		ctx.getDslContext().select()
			.from(INVOICE)
			.leftOuterJoin(FINANCE).on(INVOICE.ID.eq(FINANCE.INVOICE))
			.where( where )
			.orderBy(INVOICE.DOMAIN,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.TYPE)
			.fetch()
			.stream()
			.map(new MinimalInvoiceFiller())
			.forEach(invoice ->  
				result.add(new MissingFinanceInvoiceItem()
						.setInvoice(invoice)
						.setDomain(ctx.getDomainId())
						.setDomainName(ctx.getDomainName())
						.setMessage("Factura sin vencimientos: " 
								+ invoice.getType().getDescription()
								+ " " + invoice.getReferenceCode()
								+ " " + invoice.getIssueDate() 
								+ " " + invoice.getRegistryName()
								)
						)
			);
	}
	
	public static Invoice missingFinanceInvoicesFix(AONContext ctx, Integer invoiceId) {
		FinanceDAO.insertFinancesForInvoice(ctx, invoiceId);
		return InvoiceDAO.getFullInvoice(ctx, invoiceId); 
	}

	private static class MinimalInvoiceFiller  implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record record) {
			return new Invoice()
					.setId(record.getValue(INVOICE.ID))
					.setDomain(record.getValue(INVOICE.DOMAIN))
					.setType(AonEnumUtils.enumValue(InvoiceType.class,record.getValue(INVOICE.TYPE)))
					.setSeries(record.getValue(INVOICE.SERIES))
					.setNumber(record.getValue(INVOICE.NUMBER))
					.setReferenceCode(record.getValue(INVOICE.REFERENCE_CODE))
					.setIssueDate(record.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(record.getValue(INVOICE.TAX_DATE))
					.setTotal(record.getValue(INVOICE.TOTAL))
					.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,record.getValue(INVOICE.SECURITY_LEVEL)))
					.setRegistry(record.getValue(INVOICE.REGISTRY))
					.setRegistryDocument(record.getValue(INVOICE.RDOCUMENT))
					.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,record.getValue(INVOICE.RDOCUMENT_TYPE)))
					.setRegistryDocumentCountry(Country.safeValueOf(record.getValue(INVOICE.RDOCUMENT_COUNTRY)))
					.setRegistryName(record.getValue(INVOICE.RNAME))
				;
		}
		
	}
	private static class FinanceFiller  implements Function<Record,Finance> {
		@Override
		public Finance apply(Record record) {
			return new Finance()
				.setId(record.getValue(FINANCE.ID))
				.setDomain(record.getValue(FINANCE.DOMAIN))
				.setPayment(AonEnumUtils.getBoolean( record.getValue(FINANCE.PAYMENT)) )
				.setRegistry(record.getValue(INVOICE.REGISTRY)==null?null:new Registry().setId(record.getValue(INVOICE.REGISTRY)))
				.setRegistryDocument(record.getValue(FINANCE.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.safeValueOf(record.getValue(FINANCE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(record.getValue(FINANCE.RDOCUMENT_COUNTRY)))
				.setRegistryName(record.getValue(FINANCE.RNAME))
				.setAmount(record.getValue(FINANCE.AMOUNT))
				.setExpenses(record.getValue(FINANCE.EXPENSES))
				.setConcept(record.getValue(FINANCE.CONCEPT))
				.setInvoice(record.getValue(FINANCE.INVOICE)==null?null : new MinimalInvoiceFiller().apply(record)) 
				.setDueDate(record.getValue(FINANCE.DUE_DATE))
				.setPayMethod(record.getValue(FINANCE.PAY_METHOD))
				.setPayMethodName(record.getValue(PAY_METHOD.NAME))
				.setPayMethodType(PayMethodType.safeValueOf( record.getValue(PAY_METHOD.TYPE)))
				.setBankAccount( new BankAccount(record.getValue(FINANCE.BANK_ACCOUNT)) )
				.setBankAlias(record.getValue(FINANCE.BANK_ALIAS))
				.setBic(record.getValue(FINANCE.BIC))
				.setChequeNumber(record.getValue(FINANCE.CHEQUE_NUMBER))
				.setFinanceStatus(FinanceStatus.safeValueOf( record.getValue(FINANCE.STATUS)))
				.setSecurityLevel( SecurityLevel.safeValueOf( record.getValue(FINANCE.SECURITY_LEVEL)))
				.setRemarks(record.getValue(FINANCE.REMARKS))
				.setScope(new Scope().setId(record.getValue(FINANCE.SCOPE)))
				.setManual(AonEnumUtils.getBoolean( record.getValue(FINANCE.MANUAL)))
				.setAdvance(AonEnumUtils.getBoolean( record.getValue(FINANCE.ADVANCE)))
				.setPayroll(AonEnumUtils.getBoolean( record.getValue(FINANCE.PAYROLL)))
				.setPrepayment(AonEnumUtils.getBoolean( record.getValue(FINANCE.PREPAYMENT)))
				.setSourceId(record.getValue(FINANCE.SOURCE_ID))
				.setFinanceGroup(record.getValue(FINANCE.FINANCE_GROUP))
				.setCreationUser(record.getValue(FINANCE.CREATION_USER))
				.setCreationDate(record.getValue(FINANCE.CREATION_DATE))
				.setModificationUser(record.getValue(FINANCE.MODIFICATION_USER))
				.setModificationDate(record.getValue(FINANCE.MODIFICATION_DATE))
				.setPayMethodName(record.getValue(PAY_METHOD.NAME))
				.setPayMethodType( PayMethodType.safeValueOf(  record.getValue(PAY_METHOD.TYPE)))
				.setDirty(false)
				;
		}
	}
	public static FinanceUtilitiesResult financeInvoiceIntegrity(AONContext ctx) {
		FinanceUtilitiesResult result = new FinanceUtilitiesResult();
		ctx.getDslContext().select()
			.from(FINANCE)
			.innerJoin(INVOICE).on(INVOICE.ID.eq(FINANCE.INVOICE))
			.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
			.where( FINANCE.DOMAIN.eq(ctx.getDomainId()) )
			.and((INVOICE.TYPE.eq( InvoiceType.SALES.value() ).and(FINANCE.PAYMENT.eq( TRUE )))					
			  .or(INVOICE.TYPE.ne( InvoiceType.SALES.value() ).and(FINANCE.PAYMENT.eq( FALSE ))))
			.orderBy(FINANCE.DUE_DATE)
			.fetch()
			.stream()
			.map(new FinanceFiller())
			.map(finance -> new FinanceInvoiceIntegrityItem()
				.setFinance( finance )
				.setDomain(ctx.getDomainId())
				.setDomainName(ctx.getDomainName())
				.setMessage((finance.isPayment()?"COBRO marcado como PAGO":"PAGO marcado como COBRO")))
			.peek(item -> item.setTracking( FinanceTrackingDAO.getLastTracking(ctx, item.getFinance().getId() ) ) )
			.forEach(item -> result.add( item ) )
		;
		return result;
	}

	public static Finance financeInvoiceIntegrityFix(AONContext ctx, Finance finance) {
		ctx.checkWrite();
		if ( finance.getFinanceStatus() == null ) {
			finance.setFinanceStatus( FinanceStatus.PENDING );
		}
		finance.getFinanceStatus().visit( new IFinanceStatusVisitor() {
			private void updatePayment() {
				int i = ctx.getDslContext()
						.update(FINANCE)
						.set(FINANCE.PAYMENT, (byte) (!finance.getInvoice().isSales()?1:0) )
						.set(FINANCE.MODIFICATION_USER,ctx.getUser())
						.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.where(FINANCE.ID.equal( finance.getId()))
						.execute();
					ctx.log().info("[FIX] UPDATE FINANCE  ("+i+") id: " + finance.getId());
			}
			
			@Override
			public void visitSettled() {
				updatePayment(); 
			}
			

			@Override
			public void visitReturned() {
				updatePayment(); 
			}
			
			@Override
			public void visitPending() {
				updatePayment(); 
			}
			
			@Override
			public void visitPaid() {
				visitRecorded();
			}
			@Override
			public void visitBatched() {
				visitRecorded();
			}
			
			private void visitRecorded() {
				FinanceTracking tracking = FinanceTrackingDAO.getLastTracking(ctx, finance.getId() );
				if (tracking.getAccountEntry() != null) {
					throw new AonCoreException( "El vencimiento está contabilizado." ); 
				} else {
					updatePayment();	
				}
			}

		});
		return FinanceDAO.getFinance(ctx, finance.getId());
	}

	public static FinanceUtilitiesResult activityIntegrity(AONContext ctx, Integer domain) {
		EnterpriseActivity INVOICE_ACTIVITY = ENTERPRISE_ACTIVITY.as("invoice_activity");
		EnterpriseActivity ACCOUNT_ENTRY_ACTIVITY = ENTERPRISE_ACTIVITY.as("account_entry_activity");
		
		FinanceUtilitiesResult result = new FinanceUtilitiesResult();
		ctx.getDslContext().select(
					INVOICE.ID, INVOICE.ACTIVITY,
					INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.ISSUE_DATE, INVOICE.REFERENCE_CODE, INVOICE.RNAME,
					INVOICE_ACTIVITY.DESCRIPTION,
					ACCOUNT_ENTRY.ID, ACCOUNT_ENTRY.ACTIVITY, 
					ACCOUNT_ENTRY_ACTIVITY.DESCRIPTION)
			.from(INVOICE)
			.leftOuterJoin(INVOICE_ACTIVITY).on(INVOICE_ACTIVITY.ID.eq(INVOICE.ACTIVITY)) 
			.innerJoin( ACCOUNT_ENTRY_INVOICE ).on( ACCOUNT_ENTRY_INVOICE.INVOICE.eq(INVOICE.ID))
			.innerJoin( ACCOUNT_ENTRY ).on( ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
			.leftOuterJoin(ACCOUNT_ENTRY_ACTIVITY).on(ACCOUNT_ENTRY_ACTIVITY.ID.eq(ACCOUNT_ENTRY.ACTIVITY))
			.where( INVOICE.DOMAIN.eq( domain))
			.and( INVOICE.ACTIVITY.ne(ACCOUNT_ENTRY.ACTIVITY))
			.limit( 500 )
			.fetch()
			.stream()
			.map(r -> new ActivityIntegrityItem()
					.setInvoiceId( r.getValue(INVOICE.ID) )
					.setInvoiceRef( 
						FinanceUtil.getDocumentNumber(
							InvoiceType.safeValueOf( r.getValue(INVOICE.TYPE) ) 
							, r.getValue(INVOICE.SERIES)
							, r.getValue(INVOICE.NUMBER))
						+ " ["
						+ r.getValue(INVOICE.RNAME)
						+ "] [" + new SimpleDateFormat("dd/MM/yyyy").format(r.getValue(INVOICE.ISSUE_DATE))
						+ "] [" + r.getValue(INVOICE.REFERENCE_CODE)
						+ "]" 
					)
					.setInvoiceActivityId( r.getValue(INVOICE.ACTIVITY) )
					.setInvoiceActivityRef( r.getValue(INVOICE_ACTIVITY.DESCRIPTION) )
					.setAccountEntryId( r.getValue(ACCOUNT_ENTRY.ID) )
					.setAccountEntryActivityId( r.getValue(ACCOUNT_ENTRY.ACTIVITY) )
					.setAccountEntryActivityRef( r.getValue(ACCOUNT_ENTRY_ACTIVITY.DESCRIPTION) )
				)
			.forEach( result::add )
		;
		
		return result;
	}

	public static void activityIntegrityFix(AONContext ctx, Integer invoiceId, boolean useInvoiceActivity) {
		if (invoiceId == null)  throw new AonCoreException("El Identificador de factura no puede estar vacio");
		if (useInvoiceActivity) {
			ctx.getDslContext().select( INVOICE.ACTIVITY )
				.from( INVOICE )
				.where(INVOICE.ID.eq(invoiceId))
				.fetch()
				.stream()
				.map( r -> r.getValue(INVOICE.ACTIVITY))
				.findFirst()
				.ifPresent( act -> {
					ctx.getDslContext().select( ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY )
						.from( ACCOUNT_ENTRY_INVOICE )
						.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(invoiceId))
						.fetch()
						.stream()
						.map( r -> r.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
						.findFirst()
						.ifPresent( accountEntryId -> {
							int c = ctx.getDslContext().update(ACCOUNT_ENTRY)
								.set(ACCOUNT_ENTRY.ACTIVITY, act)
								.where(ACCOUNT_ENTRY.ID.eq(accountEntryId))
							.execute();
							ctx.log().info("UPDATE ACTIVITY: AccountEntry {0}: Activity {1}. {2} filas.",accountEntryId, act, c);
						});
				});
		} else {
			ctx.getDslContext().select( ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY )
			.from( ACCOUNT_ENTRY_INVOICE )
			.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(invoiceId))
			.fetch()
			.stream()
			.map( r -> r.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
			.findFirst()
			.ifPresent( accountEntryId -> {
				ctx.getDslContext().select( ACCOUNT_ENTRY.ACTIVITY )
					.from( ACCOUNT_ENTRY )
					.where(ACCOUNT_ENTRY.ID.eq(accountEntryId))
					.fetch()
					.stream()
					.map( r -> r.getValue(ACCOUNT_ENTRY.ACTIVITY))
					.findFirst()
					.ifPresent( act -> {
						int c = ctx.getDslContext().update(INVOICE)
								.set(INVOICE.ACTIVITY, act)
								.where(INVOICE.ID.eq(invoiceId))
								.execute();
						ctx.log().info("UPDATE ACTIVITY: AccountEntry {0}: Activity {1}. {2} filas.",accountEntryId, act, c);
					});
			});
		}
	}
	
}
