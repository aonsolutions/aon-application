package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntryFbatch.ACCOUNT_ENTRY_FBATCH;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.jooq.tables.records.FinanceTrackingRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceProperties;
import com.esferalia.aon.occam.api.model.finance.FinanceRecorder;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.validation.FinanceValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceDAO {
	
	// ------------------------------------------------------------- PAY_METHOD
	public static LinkedList<PayMethod>  getPayMethods(AONContext ctx) {
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAY_METHOD.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(PAY_METHOD.NAME)
				.fetch()
				.stream()
				.map( new FullPayMethodFiller())
				.collect(Collectors.toCollection(LinkedList::new))
				;
	}
	
	public static PayMethod  getPayMethod(AONContext ctx, String name) {
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAY_METHOD.DOMAIN.eq(ctx.getDomainId()))
				.and(PAY_METHOD.NAME.eq(name))
				.fetch()
				.stream()
				.map( new FullPayMethodFiller())
				.findFirst().orElse(new PayMethod());
	}
	// ------------------------------------------------------------- FINANCE
	public static Finance getFinance(AONContext ctx,Integer id) {
		return fetch(ctx,p -> p.getDomainProperty().eq(ctx.getDomainId())
				   	.and(p.getIdProperty().eq(id)), 0, 1)
		.findFirst().orElse(null);
	}
	
	public static Stream<Finance> getFinanceStream(AONContext ctx,FinanceFilter filter) {
		return fetch(ctx, filter)
				.fetch()
				.stream()
				.map(new FullFinanceFiller());
	}
	public static Stream<Finance> getSiiFinanceStream(AONContext ctx, FinanceFilter filter){
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FINANCE.fields())
			.select(REGISTRY.fields())
			.select(PAY_METHOD.fields())
			.select(SCOPE.fields())
			.select(INVOICE.fields())
				.from(FINANCE)
				.join(REGISTRY).on(FINANCE.REGISTRY.equal(REGISTRY.ID))
				.join(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
				.where(FINANCE_PROPERTIES.getConditions(filter))
				.and(FINANCE.DOMAIN.eq(ctx.getDomainId()))
				.and(FINANCE.ID.notIn(ctx.getDslContext().select(DATA_RESPONSE.SOURCE_ID)
						.from(DATA_RESPONSE)
						.where(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_FINANCE.value()))
						.and(DATA_RESPONSE.SOURCE_ID.eq(FINANCE.ID))
				))
			.fetch()
			.stream()
			.map(new FullFinanceFiller());
	}
	// ------------------------------------------------------------- FINANCE
	public static LinkedList<Finance> getInvoiceFinances(AONContext ctx,Integer invoice) {
		return fetch(ctx,p -> 
				p.getDomainProperty().eq(ctx.getDomainId())
				.and(p.getInvoiceProperty().eq(invoice)), 0, 100)
		.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static SelectConditionStep<Record> fetch(AONContext ctx , FinanceFilter filter) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FINANCE.fields())
			.select(REGISTRY.fields())
			.select(PAY_METHOD.fields())
			.select(SCOPE.fields())
			.select(INVOICE.fields())
				.from(FINANCE)
				.join(REGISTRY).on(FINANCE.REGISTRY.equal(REGISTRY.ID))
				.join(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
				.where(FINANCE_PROPERTIES.getConditions(filter))
				.and(FINANCE.DOMAIN.eq(ctx.getDomainId()));
	}
			
	public static Stream<Finance> fetch(AONContext ctx
			, FinanceFilter filter
			, int offset
			, int numberOfRows) {
		return fetch(ctx, filter)
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( new FullFinanceFiller() );
	}
	
	public static Stream<Finance> accountFetch(final AONContext ctx
			, FinanceFilter filter
			, int offset
			, int numberOfRows) {
		return fetch(ctx, filter, offset, numberOfRows)
				.peek(finance -> fillCustomerAcccount(ctx,finance))
				.peek(finance -> fillSupplierAcccount(ctx,finance))
				.filter(finance -> 
						finance.hasInvoice()						// Si viene de factura debe pasar
																	// o
						|| (!finance.hasInvoice()					// Si no viene de factura, 
						&& finance.isPayment()			 			// y es un pago 
						&& finance.getRegistryAccountId() == null))	// y no hay cuenta (el paso anterior no ha rellenado la cuenta)
				.peek(finance -> fillCreditorAcccount(ctx,finance))
			;
	}
	
	
	private static Finance fillCustomerAcccount(AONContext ctx,Finance finance) {
		if (finance.getRegistry() != null	 
			&& (finance.isFromSalesInvoice()					// Es factura de Venta 
			|| (!finance.hasInvoice() && !finance.isPayment())) // Cobro sin factura
			) {
			Account account = RegistryDAO.getCustomerAccount(ctx,finance.getRegistry().getId());
			fillRegistryAccountData(finance,account);
		}
		return finance;
	}
	private static Finance fillSupplierAcccount(AONContext ctx,Finance finance) {
		if (finance.getRegistry() != null 
			&& (finance.isFromPurchaseInvoice()					// Es factura de Compra 
			|| (!finance.hasInvoice() && finance.isPayment()))  // Pago sin factura
			) {
			Account account = RegistryDAO.getSupplierAccount(ctx,finance.getRegistry().getId());
			fillRegistryAccountData(finance,account);
		}
		return finance;
	}
	private static Finance fillCreditorAcccount(AONContext ctx,Finance finance) {
		if (finance.getRegistry() != null && 
			(finance.isFromExpensesInvoice()					// Es factura de Gastos 
			|| finance.isFromUndeductibleInvoice() 				// Es factura de Gastos No Ded.
			|| (!finance.hasInvoice() && finance.isPayment()))  // Pago sin factura 
			) {
			Account account = RegistryDAO.getCreditorAccount(ctx,finance.getRegistry().getId());
			fillRegistryAccountData(finance,account);
		}
		return finance;
	}
	
	private static void fillRegistryAccountData(Finance finance, Account account) {
		if (account == null) {
			finance.setRegistryAccountId(null);
			finance.setRegistryAccountCode(null);
			finance.setRegistryAccountDescription(null);
		} else {
			finance.setRegistryAccountId(account.getId());
			finance.setRegistryAccountCode(account.getCode());
			finance.setRegistryAccountDescription(account.getDescription());
		}
	}
	// ------------------------------------------------------------- ESCRITURA
	public static Integer save(AONContext ctx, Finance finance) {
		if (finance.getId() == null) {
			return insert(ctx, finance);
		} else {
			update(ctx, finance);
			return finance.getId();
		}
	}

	public static Integer insert(AONContext ctx, Finance finance) {
		ctx.checkWrite();
		FinanceValidation.validateSave(ctx, finance);
		FinanceRecord record = ctx.getDslContext()
			.insertInto(FINANCE)
				.set(FINANCE.DOMAIN,finance.getDomain())
				.set(FINANCE.PAYMENT,AonEnumUtils.getByte(finance.isPayment()))
				.set(FINANCE.REGISTRY,finance.getRegistry().getId())
				.set(FINANCE.RDOCUMENT,finance.getRegistryDocument())
				.set(FINANCE.RDOCUMENT_TYPE,finance.getRegistryDocumentType()==null?null:finance.getRegistryDocumentType().value())
				.set(FINANCE.RDOCUMENT_COUNTRY,finance.getRegistryDocumentCountry()==null?null:finance.getRegistryDocumentCountry().getIso2())
				.set(FINANCE.RNAME,finance.getRegistryName())
				.set(FINANCE.AMOUNT,finance.getAmount())
				.set(FINANCE.EXPENSES,finance.getExpenses())
				.set(FINANCE.CONCEPT,finance.getConcept())
				.set(FINANCE.INVOICE,finance.getInvoice()==null?null:finance.getInvoice().getId())
				.set(FINANCE.DUE_DATE,AonDateUtils.toSql(finance.getDueDate()))
				.set(FINANCE.PAY_METHOD,finance.getPayMethod()==null?null:finance.getPayMethod())
				.set(FINANCE.BANK_ACCOUNT,finance.getBankAccount()==null?null:finance.getBankAccount().getIban())
				.set(FINANCE.BANK_ALIAS,finance.getBankAlias())
				.set(FINANCE.BIC,finance.getBic())
				.set(FINANCE.CHEQUE_NUMBER,finance.getChequeNumber())
				.set(FINANCE.STATUS,finance.getFinanceStatus().value())
				.set(FINANCE.SECURITY_LEVEL, AonEnumUtils.getByte(finance.isConfidential()) )
				.set(FINANCE.REMARKS,finance.getRemarks())
				.set(FINANCE.SCOPE,finance.getScope().getId())
				.set(FINANCE.MANUAL,AonEnumUtils.getByte(finance.isManual()))
				.set(FINANCE.ADVANCE,AonEnumUtils.getByte(finance.isAdvance()))
				.set(FINANCE.PAYROLL,AonEnumUtils.getByte(finance.isPayroll()))
				.set(FINANCE.PREPAYMENT,AonEnumUtils.getByte(finance.isPrepayment()))
				.set(FINANCE.SOURCE_ID,finance.getSourceId())
				.set(FINANCE.FINANCE_GROUP,finance.getFinanceGroup())
				.set(FINANCE.CREATION_USER,ctx.getUser())
				.set(FINANCE.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.returning(FINANCE.ID)
				.fetchOne();
		ctx.log().info("INSERT FINANCE id: " + record.getValue(FINANCE.ID));		
		return record.getValue(FINANCE.ID); 
	}
	
	private static void update(AONContext ctx, Finance finance) {
		ctx.checkWrite();
		FinanceValidation.validateSave(ctx, finance);
		int i = ctx.getDslContext().update(FINANCE)
			.set(FINANCE.DOMAIN,finance.getDomain())
			.set(FINANCE.PAYMENT,AonEnumUtils.getByte(finance.isPayment()))
			.set(FINANCE.REGISTRY,finance.getRegistry().getId())
			.set(FINANCE.RDOCUMENT,finance.getRegistryDocument())
			.set(FINANCE.RDOCUMENT_TYPE,finance.getRegistryDocumentType()==null?null:finance.getRegistryDocumentType().value())
			.set(FINANCE.RDOCUMENT_COUNTRY,finance.getRegistryDocumentCountry()==null?null:finance.getRegistryDocumentCountry().getIso2())
			.set(FINANCE.RNAME,finance.getRegistryName())
			.set(FINANCE.AMOUNT,finance.getAmount())
			.set(FINANCE.EXPENSES,finance.getExpenses())
			.set(FINANCE.CONCEPT,finance.getConcept())
			.set(FINANCE.INVOICE,finance.getInvoice()==null?null:finance.getInvoice().getId())
			.set(FINANCE.DUE_DATE,AonDateUtils.toSql(finance.getDueDate()))
			.set(FINANCE.PAY_METHOD,finance.getPayMethod()==null?null:finance.getPayMethod())
			.set(FINANCE.BANK_ACCOUNT,finance.getBankAccount()==null?null:finance.getBankAccount().getIban())
			.set(FINANCE.BANK_ALIAS,finance.getBankAlias())
			.set(FINANCE.BIC,finance.getBic())
			.set(FINANCE.CHEQUE_NUMBER,finance.getChequeNumber())
			.set(FINANCE.STATUS,finance.getFinanceStatus().value())
			.set(FINANCE.SECURITY_LEVEL,  AonEnumUtils.getByte(finance.isConfidential()) )
			.set(FINANCE.REMARKS,finance.getRemarks())
			.set(FINANCE.SCOPE,finance.getScope().getId())
			.set(FINANCE.MANUAL,AonEnumUtils.getByte(finance.isManual()))
			.set(FINANCE.ADVANCE,AonEnumUtils.getByte(finance.isAdvance()))
			.set(FINANCE.PAYROLL,AonEnumUtils.getByte(finance.isPayroll()))
			.set(FINANCE.PREPAYMENT,AonEnumUtils.getByte(finance.isPrepayment()))
			.set(FINANCE.SOURCE_ID,finance.getSourceId())
			.set(FINANCE.FINANCE_GROUP,finance.getFinanceGroup())
			.set(FINANCE.MODIFICATION_USER,ctx.getUser())
			.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FINANCE.ID.equal( finance.getId()))
			.execute();
		ctx.log().info("UPDATE FINANCE  ("+i+") id: " + finance.getId());
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		Finance finance = getFinance(ctx,id);
		FinanceValidation.validateDelete(ctx, finance);
		removeFinanceTrackingFractions(ctx,finance);
		int i = ctx.getDslContext()
			.delete(FINANCE)
			.where(FINANCE.ID.equal(id))
			.execute();
		ctx.log().info("DELETE FINANCE ("+i+") id: " + finance.getId());
	}
	
	private static void removeFinanceTrackingFractions(AONContext ctx, Finance finance) {
		if (finance.isPending()) {
			int i = ctx.getDslContext()
				.delete(FINANCE_TRACKING)
				.where(FINANCE_TRACKING.FINANCE.eq(finance.getId()))
				.and(FINANCE_TRACKING.TYPE.eq(FinanceTrackingType.FRACTIONED.value()))
				.execute();
			ctx.log().info("DELETE FINANCE_TRACKING Fractions ("+i+") Finance id: " + finance.getId());
		}
	}
	
	// ---------------------------------------------------------- FILTROS
	private static final FinancePropertiesDAO FINANCE_PROPERTIES = new FinancePropertiesDAO();
	private static class FinancePropertiesDAO implements FinanceProperties {

		private Condition[] getConditions(FinanceFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(FINANCE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(FINANCE.DOMAIN);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<Byte>(FINANCE.SECURITY_LEVEL);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(FINANCE.REGISTRY);}
		@Override public Property<Date> getDueDateProperty() {return new FilterDAO.DatePropertyDAO(FINANCE.DUE_DATE);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterDAO.PropertyDAO<Integer>(FINANCE.INVOICE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(FINANCE.STATUS);}
		@Override public Property<Double> getAmountProperty() {return new FilterDAO.PropertyDAO<Double>(FINANCE.AMOUNT);}
		@Override public Property<String> getConceptProperty() {return new FilterDAO.PropertyDAO<String>(FINANCE.CONCEPT);}
		@Override public Property<Byte> getPaymentProperty() {return new FilterDAO.PropertyDAO<Byte>(FINANCE.PAYMENT);}
		@Override public Property<String> getInvoiceReferenceCode() {return new FilterDAO.PropertyDAO<String>(INVOICE.REFERENCE_CODE);}
	}
	
	// ---------------------------------------------------------- MAP
	private static class FullPayMethodFiller  implements Function<Record,PayMethod> {
		@Override
		public PayMethod apply(Record record) {
			return new PayMethod()
				.setId(record.getValue(PAY_METHOD.ID))
				.setDomain(record.getValue(PAY_METHOD.DOMAIN))
				.setName(record.getValue(PAY_METHOD.NAME))
				.setType(PayMethodType.safeValueOf( record.getValue(PAY_METHOD.TYPE)));
		}
		
		
	}
	public static class FullFinanceFiller  implements Function<Record,Finance> {
		@Override
		public Finance apply(Record record) {
			return new Finance()
				.setId(record.getValue(FINANCE.ID))
				.setDomain(record.getValue(FINANCE.DOMAIN))
				.setPayment(AonEnumUtils.getBoolean( record.getValue(FINANCE.PAYMENT)) )
				.setRegistry(new RegistryDAO.RegistryFiller().apply(record))
				.setRegistryDocument(record.getValue(FINANCE.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.safeValueOf(record.getValue(FINANCE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(record.getValue(FINANCE.RDOCUMENT_COUNTRY)))
				.setRegistryName(record.getValue(FINANCE.RNAME))
				.setAmount(record.getValue(FINANCE.AMOUNT))
				.setExpenses(record.getValue(FINANCE.EXPENSES))
				.setConcept(record.getValue(FINANCE.CONCEPT))
				.setInvoice(record.getValue(FINANCE.INVOICE)==null?null : new InvoiceDAO.MinimalInvoiceFiller().apply(record)) 
				.setDueDate(record.getValue(FINANCE.DUE_DATE))
				.setPayMethod(record.getValue(FINANCE.PAY_METHOD))
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
				;
		}

			
	}
	public static void deleteAllPendingFinances(AONContext ctx, Integer invoiceId) {
		ctx.checkWrite();
		LinkedList<Finance>  finances = getInvoiceFinances(ctx, invoiceId);
		for (Finance finance : finances ) {
			delete(ctx, finance.getId());
		}
	}
	public static Integer settle(AONContext ctx, Integer financeId, double amount) {
		return settle(ctx, financeId, new Date(), amount
				,FinanceTrackingType.SETTLED.getDescription(), false);	
	}
	public static Integer settle(AONContext ctx, Integer financeId,Date date, double amount, String description, boolean recorded) {
		return addTracking(ctx,financeId,date,amount,FinanceStatus.SETTLED
				,FinanceTrackingType.SETTLED,description,recorded);
	}
	private static Integer addTracking(AONContext ctx, Integer financeId,Date date, double amount
			,FinanceStatus financeStatus,FinanceTrackingType type, String description, boolean recorded) {
		
		FinanceTracking ft = new FinanceTracking()
			.setFinance(new Finance().setId(financeId))
			.setDomain(ctx.getDomainId())
        	.setTrackingDate(date)
        	.setType(type)
        	.setDescription(description)
        	.setRegistryBank(null)
        	.setPayMethodTypeDetail(null)
        	.setBankStatementLink(null)
        	.setAmount(amount)
        	.setRecorded(recorded);
		int i = ctx.getDslContext().update(FINANCE)
			.set(FINANCE.STATUS,financeStatus.value())
			.set(FINANCE.MODIFICATION_USER,ctx.getUser())
			.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FINANCE.ID.equal( financeId))
			.execute();
		ctx.log().info("UPDATE FINANCE  ("+i+") id: " + financeId + " status: " + financeStatus.getDescription());
		return insert(ctx,ft);
	}
	
	public static Integer insert(AONContext ctx, FinanceTracking ft) {
		ctx.checkWrite();
		FinanceTrackingRecord record = ctx.getDslContext()
			.insertInto(FINANCE_TRACKING)
				.set(FINANCE_TRACKING.DOMAIN,ft.getDomain())
				.set(FINANCE_TRACKING.FINANCE,ft.getFinance().getId())
				.set(FINANCE_TRACKING.TRACKING_DATE,AonDateUtils.toSql(ft.getTrackingDate()))
				.set(FINANCE_TRACKING.TYPE,ft.getType().value())
				.set(FINANCE_TRACKING.DESCRIPTION,ft.getDescription())
				.set(FINANCE_TRACKING.PM_TYPE_DETAIL,ft.getPayMethodTypeDetail())
				.set(FINANCE_TRACKING.RBANK,ft.getRegistryBank())
				.set(FINANCE_TRACKING.BANK_STATEMENT_LINK,ft.getBankStatementLink())
				.set(FINANCE_TRACKING.AMOUNT,ft.getAmount())
				.set(FINANCE_TRACKING.RECORDED, AonEnumUtils.getByte(ft.isRecorded()))
				.set(FINANCE_TRACKING.CREATION_USER,ctx.getUser())
				.set(FINANCE_TRACKING.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.returning(FINANCE_TRACKING.ID)
				.fetchOne();
		ctx.log().info("INSERT FINANCE_TRACKING id: " + record.getValue(FINANCE_TRACKING.ID));
		return record.getValue(FINANCE_TRACKING.ID); 
	}
	
	public static FinanceEntry save(AONContext ctx, FinanceEntry financeEntry) {
		ctx.checkWrite();
		if (financeEntry.getAccountEntry().getId() == null) {
			return insert(ctx,financeEntry);
		} else {
			return update(ctx,financeEntry);
		}
	}
	
	private static FinanceEntry update(AONContext ctx, FinanceEntry financeEntry) {
		if (!financeEntry.isMultipleGeneration()) {
			AccountEntry[] entries = FinanceRecorder.recordFinanceEntry(financeEntry);
			AccountEntry entry = entries[0];
			
			// Se borran (id negativo) las líneas del apunte original, se 
			// suman al nuevo apunte (que tiene el mismo id) De tal forma, 
			// la cabecera se modifica y las línea con id negativo se borran. 
			// Las línea sin id, se añaden.
			for (AccountEntryDetail original : financeEntry.getAccountEntry().getDetails()) {
				original.setId( original.getId() * -1);
				entry.addDetail(original);
			}
			Integer entryId = AccountEntryDAO.save(ctx, entry);
			entry.setId(entryId);
			for (FinanceTracking ft : financeEntry.getTrackings().values()) {
				if (ft.isDeleted()) {
					deleteFinanceTracking(ctx, ft);
				} else {
					if (ft.getAccountEntry() == null) {
						pay(ctx,ft,entry);	
					}
				}
			}
		}
		return financeEntry;
	}
	
	private static FinanceEntry insert(AONContext ctx, FinanceEntry financeEntry) {
		if (!financeEntry.isMultipleGeneration()) {
			AccountEntry[] entries = FinanceRecorder.recordFinanceEntry(financeEntry);
			if (entries != null && entries.length == 1) {
				AccountEntry entry = entries[0];
				Integer entryId = AccountEntryDAO.save(ctx, entry);
				entry.setId(entryId);
				financeEntry.setAccountEntry(entry);
				for (FinanceTracking finance : financeEntry.getTrackings().values()) {
					pay(ctx,finance,entry);
				}
			}
		}
		return financeEntry;
	}
	
	private static void pay(AONContext ctx,FinanceTracking finance,AccountEntry entry) {
		finance.getFinance().setFinanceStatus(FinanceStatus.PAID);
		Integer trackingId = addTracking(ctx
				, finance.getFinance().getId()
				, entry.getEntryDate()
				, finance.getFinance().getAmount()
				, FinanceStatus.PAID
				, FinanceTrackingType.PAID
				, "CONTABILIZADO"
				, true);
		ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY,entry.getId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING,trackingId)
			.execute();
		ctx.log().info("ACCOUNT_ENTRY_FINANCE_TRACKING account_entry: " + entry.getId() + " tracking: " + trackingId);
	}
	
	public static void deleteAccountEntryTrackings(AONContext ctx, Integer accountEntryId) {
		ctx.checkWrite();
		FinanceEntry entry = getFinanceEntry(ctx, accountEntryId);
		FinanceValidation.validateDelete(ctx, entry);
		for (FinanceTracking ft : entry.getTrackings().values()) {
			if (ft.getAccountEntry() != null) {
				deleteFinanceTracking(ctx, ft);
			}
		}
	}
	
	public static FinanceEntry getFinanceEntry(final AONContext ctx, Integer accountEntryId) {
		AccountEntry accountEntry = AccountEntryDAO.getAccountEntry(ctx, accountEntryId);
		if (accountEntry == null) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage());
		}
		final FinanceEntry entry = new FinanceEntry();
		entry.setFinanceBatch(ctx.getDslContext()
				.select(ACCOUNT_ENTRY_FBATCH.FBATCH)
				.from(ACCOUNT_ENTRY_FBATCH)
				.where(ACCOUNT_ENTRY_FBATCH.ACCOUNT_ENTRY.eq(accountEntryId))
				.fetch()
				.stream()
				.findFirst()
				.map( rec -> rec.getValue(ACCOUNT_ENTRY_FBATCH.FBATCH))
				.orElse(null)
				);
		
		entry.setAccountEntry(accountEntry);
		accountEntry.getDetails()
			.stream()
			.forEach(detail -> {
				if (AonStringUtils.startsWith(detail.getAccountCode(), "5")) {
					entry.setBankAccount(AccountDAO.get(ctx, detail.getAccount()));
					entry.setManualConcept(AonStringUtils.substringBetween(detail.getConcept(), AonStringUtils.OPEN_BRACKET, AonStringUtils.CLOSE_BRACKET));
				} else if (AonStringUtils.startsWith(detail.getAccountCode(), "6")) {
						entry.setExpensesAccount(AccountDAO.get(ctx, detail.getAccount()));
						entry.setExpenses( AonMathUtils.round(detail.getDebit() - detail.getCredit() ));
				}
			});
		if (entry.isFromFinanceBatch()) {
			ctx.getDslContext()
			.select(FINANCE.fields())
			.select(REGISTRY.fields())
			.select(PAY_METHOD.fields())
			.select(SCOPE.fields())
			.select(INVOICE.fields())
				.from(FBATCH_DETAIL)
				.join(FINANCE).on(FINANCE.ID.equal(FBATCH_DETAIL.FINANCE))
				.join(REGISTRY).on(FINANCE.REGISTRY.equal(REGISTRY.ID))
				.join(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
				.where(FBATCH_DETAIL.FBATCH.eq(entry.getFinanceBatch()))
				.fetch()
				.stream()
			.map( new FullFinanceFiller() )
			.peek(finance -> fillCustomerAcccount(ctx,finance))
			.peek(finance -> fillSupplierAcccount(ctx,finance))
			.peek(finance -> fillCreditorAcccount(ctx,finance))
			.forEach( finance -> entry.getTrackings().put(finance.getId(), new FinanceTracking().setFinance(finance)));
		} else {
			ctx.getDslContext()
				.select(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY)
				.select(FINANCE_TRACKING.fields())
				.select(FINANCE.fields())
				.select(REGISTRY.fields())
				.select(PAY_METHOD.fields())
				.select(SCOPE.fields())
				.select(INVOICE.fields())
					.from(ACCOUNT_ENTRY_FINANCE_TRACKING)
					.join(FINANCE_TRACKING).on(FINANCE_TRACKING.ID.equal(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING))
					.join(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
					.join(REGISTRY).on(FINANCE.REGISTRY.equal(REGISTRY.ID))
					.join(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
					.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
					.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
					.where(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY.eq(accountEntryId))
					.fetch()
					.stream()
				.map( new FullFinanceTrackingFiller() )
				.peek(ft -> ft.setLastTracking(isLastTracking(ctx, ft)))
				.peek(ft -> fillCustomerAcccount(ctx,ft.getFinance()))
				.peek(ft -> fillSupplierAcccount(ctx,ft.getFinance()))
				.peek(ft -> fillCreditorAcccount(ctx,ft.getFinance()))
				.forEach( ft -> entry.getTrackings().put(ft.getFinance().getId(), ft));
		}
		return entry;
	}
	
	public static class FullFinanceTrackingFiller  implements Function<Record,FinanceTracking> {
		@Override
		public FinanceTracking apply(Record record) {
			return new FinanceTracking()
				.setId(record.getValue(FINANCE_TRACKING.ID))
				.setDomain(record.getValue(FINANCE_TRACKING.DOMAIN))
				.setRegistryBank(record.getValue(FINANCE_TRACKING.RBANK))
				.setBankStatementLink(record.getValue(FINANCE_TRACKING.BANK_STATEMENT_LINK))
				.setFinance(new FullFinanceFiller().apply(record))
				.setPayMethodTypeDetail(record.getValue(FINANCE_TRACKING.PM_TYPE_DETAIL))
				.setTrackingDate(record.getValue(FINANCE_TRACKING.TRACKING_DATE))
				.setType( FinanceTrackingType.safeValueOf(record.getValue(FINANCE_TRACKING.TYPE)))
				.setDescription(record.getValue(FINANCE_TRACKING.DESCRIPTION))
				.setAmount(record.getValue(FINANCE_TRACKING.AMOUNT))
				.setRecorded(AonEnumUtils.getBoolean( record.getValue(FINANCE_TRACKING.RECORDED)))
				.setAccountEntry(record.getValue(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY))
				.setCreationUser(record.getValue(FINANCE_TRACKING.CREATION_USER))
				.setCreationDate(record.getValue(FINANCE_TRACKING.CREATION_DATE))
				.setModificationUser(record.getValue(FINANCE_TRACKING.MODIFICATION_USER))
				.setModificationDate(record.getValue(FINANCE_TRACKING.MODIFICATION_DATE))
				;
		}
	}
	
	private static boolean isLastTracking(AONContext ctx, FinanceTracking ft) {
		return !ctx.getDslContext()
			.select()
			.from(FINANCE_TRACKING)
			.where(FINANCE_TRACKING.FINANCE.eq(ft.getFinance().getId()))
			.and(FINANCE_TRACKING.ID.gt(ft.getId()))
			.fetch()
			.stream()
			.findAny()
			.isPresent();
	}
	
	private static void deleteAccountEntryFinanceTracking(AONContext ctx, FinanceTracking ft) {
		int i = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.where(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING.equal(ft.getId()))
			.execute();
		ctx.log().info("DELETE ACCOUNT_ENTRY_FINANCE_TRACKING ("+i+") Tracking: " + ft.getId());
	}
		

	private static void deleteFinanceTracking(AONContext ctx, FinanceTracking ft) {
		deleteAccountEntryFinanceTracking(ctx,ft);
		if (ft.getBankStatementLink() == null && isLastTracking(ctx,ft)) {
			int i = ctx.getDslContext()
				.delete(FINANCE_TRACKING)
				.where(FINANCE_TRACKING.ID.equal(ft.getId()))
				.execute();
			ctx.log().info("DELETE FINANCE_TRACKING  ("+i+") id: " + ft.getId());
			FinanceStatus newStatus = wasFinanceReturned(ctx,ft.getFinance().getId())
					?FinanceStatus.RETURNED
					:FinanceStatus.PENDING;
			i = ctx.getDslContext().update(FINANCE)
				.set(FINANCE.STATUS,newStatus.value())
				.set(FINANCE.MODIFICATION_USER,ctx.getUser())
				.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.where(FINANCE.ID.equal( ft.getFinance().getId() ))
				.execute();
			ctx.log().info("UPDATE FINANCE  ("+i+") id: " + ft.getFinance().getId() + " status: " + newStatus.getDescription());
		} else {
		}
	}

	private static boolean wasFinanceReturned(AONContext ctx,Integer financeId)  {
		return (getReturnedTimes(ctx,financeId) > 0);
	}

	private static int getReturnedTimes(AONContext ctx, Integer financeId)  {
		return ctx.getDslContext().fetchCount(FINANCE_TRACKING
			,FINANCE_TRACKING.FINANCE.eq(financeId)
			.and(FINANCE_TRACKING.TYPE.eq(FinanceTrackingType.RETURNED.value())));
	}
}
