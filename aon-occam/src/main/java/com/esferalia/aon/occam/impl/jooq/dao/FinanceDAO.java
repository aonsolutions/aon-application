package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntryFbatch.ACCOUNT_ENTRY_FBATCH;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Timestamp;
import java.util.Calendar;
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
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceProperties;
import com.esferalia.aon.occam.api.model.finance.FinanceRecorder;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.validation.FinanceAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.FinanceValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
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
	
	public static LinkedList<PayMethod>  getPayMethodsById(AONContext ctx) {
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAY_METHOD.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(PAY_METHOD.ID)
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
	
	public static Finance insertFinance(AONContext ctx, Finance finance) {
		Integer id = insert(ctx, finance);
		return finance.setId(id);
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
		FinanceAutoComplete.completeFinance(ctx, finance);
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
		FinanceAutoComplete.completeFinance(ctx, finance);
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
				.setDirty(false)
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
	
	public static Integer insertTracking(AONContext ctx, FinanceTracking ft) {
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
						pay(ctx,ft.getFinance(),entry);	
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
					pay(ctx,finance.getFinance(),entry);
				}
			}
		}
		return financeEntry;
	}
	
	private static void pay(AONContext ctx,Finance finance,AccountEntry entry) {
		finance.setFinanceStatus(FinanceStatus.PAID);
		FinanceTracking ft = new FinanceTracking()
				.setFinance(finance)
				.setDomain(ctx.getDomainId())
				.setTrackingDate(entry.getEntryDate())
				.setType(FinanceTrackingType.PAID)
				.setDescription("CONTABILIZADO")
				.setRegistryBank(null)
				.setPayMethodTypeDetail(null)
				.setBankStatementLink(null)
				.setAmount(finance.getAmount())
				.setRecorded(true);
		updateFinanceStatus(ctx,finance.getId(),FinanceStatus.PAID);
		Integer trackingId = insertTracking(ctx, ft);
		ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY,entry.getId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING,trackingId)
			.execute();
		ctx.log().info("ACCOUNT_ENTRY_FINANCE_TRACKING account_entry: " + entry.getId() + " tracking: " + trackingId);
	}
	
	public static void deleteAccountEntryFinanceTrackings(AONContext ctx, Integer accountEntryId) {
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
	
	public static class FinanceTrackingFiller  implements Function<Record,FinanceTracking> {
		@Override
		public FinanceTracking apply(Record record) {
			return new FinanceTracking()
				.setId(record.getValue(FINANCE_TRACKING.ID))
				.setDomain(record.getValue(FINANCE_TRACKING.DOMAIN))
				.setRegistryBank(record.getValue(FINANCE_TRACKING.RBANK))
				.setBankStatementLink(record.getValue(FINANCE_TRACKING.BANK_STATEMENT_LINK))
				.setFinance(new Finance().setId(record.getValue(FINANCE_TRACKING.FINANCE)))
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
	
//	private static boolean wasFinanceReturned(AONContext ctx,Integer financeId)  {
//		return (getReturnedTimes(ctx,financeId) > 0);
//	}
//	private static int getReturnedTimes(AONContext ctx, Integer financeId)  {
//		return ctx.getDslContext().fetchCount(FINANCE_TRACKING
//			,FINANCE_TRACKING.FINANCE.eq(financeId)
//			.and(FINANCE_TRACKING.TYPE.eq(FinanceTrackingType.RETURNED.value())));
//	}
	
	public static Invoice insertFinancesForInvoice(AONContext ctx, Integer invoiceId) {
		Invoice invoice = InvoiceDAO.getInvoice(ctx, invoiceId);
		if (invoice == null) {
			throw new AonCoreException(AonError.INVOICE_NOT_FOUND.getMessage());
		}
		return insertFinancesForInvoice(ctx,invoice);
	}
	
	public static Invoice insertFinancesForInvoice(AONContext ctx, Invoice invoice) {
		LinkedList<Finance> finances = getFinancesForInvoice(ctx, invoice);
		for (Finance finance : finances) {
			insertFinance(ctx, finance);
		}
		invoice.setFinances(finances);
		return invoice;
	}
	public static LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice) {
		LinkedList<Finance> finances = new LinkedList<Finance>();
		RegistryPayMethod rPayMethod = RegistryDAO.getRPayMethodStream(ctx, prop -> prop.getDomainProperty()
				.eq(ctx.getDomainId()).and(prop.getRegistryProperty().eq(invoice.getRegistry()))).findFirst()
				.orElse(null);
		;
		RegistryBank rBank = null;
		if (rPayMethod != null && rPayMethod.getRbank() != null) {
			rBank = RegistryDAO.getRBankStream(ctx, prop -> prop.getDomainProperty().eq(ctx.getDomainId())
					.and(prop.getIdProperty().eq(rPayMethod.getRbank()))).findFirst().orElse(null);
		}
		Date date = invoice.getIssueDate();
		int numberOfPymnts =  ((rPayMethod == null) || (rPayMethod.getNumberOfPymnts() == 0)) ? 1 : rPayMethod.getNumberOfPymnts();
		int daysToFirstPymnt = ((rPayMethod == null) || (rPayMethod.getDaysToFirstPymnt() == 0)) ? 0 : rPayMethod.getDaysToFirstPymnt();
		int daysBetwenPymnts = ((rPayMethod == null) || (rPayMethod.getDaysBetwenPymnts() == 0)) ? 0 : rPayMethod.getDaysBetwenPymnts();
		Integer pm =  (rPayMethod==null) ? null : rPayMethod.getPayMethod();
		double paymentPrice = AonMathUtils.round(invoice.getTotal() / numberOfPymnts);
		for (int i = 0; i < numberOfPymnts; i++) {
			int days = (i==0?daysToFirstPymnt:daysBetwenPymnts);
			date = (rPayMethod==null? date : calculatePaymentDate(days, rPayMethod.getPymnt_days(), date));
			Finance finance = createFinance(invoice, date, pm , rBank, paymentPrice );  
			finances.add(finance);
		}
		double lastPaymentPrice = AonMathUtils.round(invoice.getTotal() - (paymentPrice * (numberOfPymnts - 1)));
		if ( !AonMathUtils.equals(paymentPrice, lastPaymentPrice)) {
			finances.getLast().setAmount(lastPaymentPrice);
		}
		
//		if ((rPayMethod == null) || (rPayMethod.getNumberOfPymnts() == 1)) {
//			date = (rPayMethod==null) ? date : calculatePaymentDate(rPayMethod.getDaysToFirstPymnt(), rPayMethod.getPymnt_days(), date);
//			finances.add(createFinance(invoice, date, (rPayMethod==null) ? null : rPayMethod.getPayMethod(), rBank, invoice.getTotal()));
//		} else {
//			double paymentPrice = AonMathUtils.round((invoice.getTotal() / rPayMethod.getNumberOfPymnts()));
//			date = calculatePaymentDate(rPayMethod.getDaysToFirstPymnt(), rPayMethod.getPymnt_days(), date);
//			finances.add(createFinance(invoice, date, rPayMethod.getPayMethod(), rBank, paymentPrice));
//			for(int i=2; i<=rPayMethod.getNumberOfPymnts()-1; i++) {
//				date = calculatePaymentDate(rPayMethod.getDaysBetwenPymnts(), rPayMethod.getPymnt_days(), date);
//				finances.add(createFinance(invoice, date, rPayMethod.getPayMethod(), rBank, paymentPrice));
//			}
//			paymentPrice = AonMathUtils.round(invoice.getTotal() - (paymentPrice * (rPayMethod.getNumberOfPymnts() - 1)));
//			date = calculatePaymentDate(rPayMethod.getDaysBetwenPymnts(), rPayMethod.getPymnt_days(), date);
//			finances.add(createFinance(invoice, date, rPayMethod.getPayMethod(), rBank, paymentPrice));
//		}
		return finances;
	}
	
	private static Date calculatePaymentDate(int daysNumber, String paymentDays, Date date) {
		Date paymentDate = AonDateUtils.addDays(date, daysNumber);
		String[] paymentDaysArray = AonStringUtils.split(paymentDays, ' ');
		if (paymentDaysArray.length > 0) {
			for (int i=0; i<paymentDaysArray.length; i++) {
				int daysInMonth = AonDateUtils.daysInMonth(paymentDate);
				int day = AonNumberUtils.toint(paymentDaysArray[i]);
				day = day>daysInMonth ? daysInMonth : day;
				if (AonDateUtils.getFragmentInDays(paymentDate, Calendar.MONTH) <= day) {
					return AonDateUtils.setDays(paymentDate, day);
				}
			}
			int day = AonNumberUtils.toint(paymentDaysArray[0]);
			if (day != 0) {
				paymentDate = AonDateUtils.addMonths(paymentDate, 1);
				int daysInMonth = AonDateUtils.daysInMonth(paymentDate);
				day = (day>daysInMonth) ? daysInMonth : day;
				return AonDateUtils.setDays(paymentDate, day);
			}
		}
		return paymentDate;
	}

	private static Finance createFinance(Invoice invoice, Date date, Integer payMethod, RegistryBank rBank, double totalPrice) {
		Finance finance = new Finance();
		finance.setDomain(invoice.getDomain());
		finance.setPayment(!invoice.getType().equals(InvoiceType.SALES));
		finance.setRegistry( new Registry().setId(invoice.getRegistry()));
		finance.setRegistryName(invoice.getRegistryName());
		finance.setRegistryDocument(invoice.getRegistryDocument());
		finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
		finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		finance.setAmount(totalPrice);
		finance.setConcept(invoice.getDocumentNumber());
		finance.setInvoice(invoice);
		finance.setDueDate(date);
		finance.setPayMethod(payMethod);
		finance.setBankAccount((rBank==null) ? null : new BankAccount(rBank.getBankAccount()) );
		finance.setBankAlias((rBank==null) ? null : rBank.getAlias());
		finance.setBic((rBank==null) ? null : rBank.getBic());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(invoice.getSecurityLevel());
		finance.setScope(invoice.getScope());
		return finance;
	}

	public static Integer settle(AONContext ctx, Integer financeId) {
		ctx.checkWrite();
		Finance finance = FinanceValidation.validateSettleTracking(ctx, financeId);
		FinanceTracking ft = new FinanceTracking()
				.setFinance(finance)
				.setDomain(ctx.getDomainId())
				.setTrackingDate(new Date())
				.setType(FinanceTrackingType.SETTLED)
				.setDescription(FinanceTrackingType.SETTLED.getDescription())
				.setRegistryBank(null)
				.setPayMethodTypeDetail(null)
				.setBankStatementLink(null)
				.setAmount(finance.getAmount())
				.setRecorded(false);
		updateFinanceStatus(ctx,financeId,FinanceStatus.SETTLED);
		return insertTracking(ctx, ft);
	}
	

	private static void updateFinanceStatus(AONContext ctx,Integer financeId,FinanceStatus financeStatus) {
		int i = ctx.getDslContext().update(FINANCE)
				.set(FINANCE.STATUS,financeStatus.value())
				.set(FINANCE.MODIFICATION_USER,ctx.getUser())
				.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.where(FINANCE.ID.equal( financeId))
				.execute();
		ctx.log().info("UPDATE FINANCE  ("+i+") id: " + financeId + " status: " + financeStatus.getDescription());
	}
	
	public static LinkedList<FinanceTracking> getFinanceTracking(AONContext ctx, Integer finance) {
		return ctx.getDslContext()
			.select(FINANCE_TRACKING.fields())
			.select(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY)
			.from(FINANCE_TRACKING)
			.leftOuterJoin(ACCOUNT_ENTRY_FINANCE_TRACKING).on(FINANCE_TRACKING.ID.equal(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING))
			.where(FINANCE_TRACKING.FINANCE.eq(finance))
			.orderBy(FINANCE_TRACKING.ID)
			.fetch()
			.stream()
			.map( new FinanceTrackingFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static FinanceTracking getLastTracking(AONContext ctx, Integer financeId) {
		LinkedList<FinanceTracking> trackings = getFinanceTracking(ctx, financeId);
		if ( trackings != null && !trackings.isEmpty()) {
			return trackings.getLast();	
		}
		return null;
	}

	public static void undo(AONContext ctx, Integer financeId) {
		ctx.checkWrite();
		FinanceTracking financeTracking = FinanceValidation.validateUndoTracking(ctx, financeId);
		Integer accountEntryId = ctx.getDslContext()
			.select(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY)
				.from(ACCOUNT_ENTRY_FINANCE_TRACKING)
				.where(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING.eq(financeTracking.getId()))
			.fetch()
			.stream()
			.map( rec -> rec.getValue(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY))
			.findFirst()
			.orElse(null);
		if (accountEntryId != null) {
			FinanceEntry fe = getFinanceEntry(ctx, accountEntryId);
			if (fe.getTrackings().size() == 1) {
				AccountEntryDAO.delete(ctx, accountEntryId);
			} else {
				fe.getTrackings().get(financeId).setDeleted(true);
				update(ctx, fe);
			}
		} else {
			deleteFinanceTracking(ctx, financeTracking);
		}
	}

	/*  FRACTION
		if (!AonNumberUtils.equals( original.getAmount(), finance.getAmount())) {
			ctx.log().info(" ----- START FINANCE FRACTION----- ");
			double newAmount = AonMathUtils.round(original.getAmount() - finance.getAmount() );
			original.setId( null )
				.setAmount(newAmount)
				.setFinanceStatus( FinanceStatus.PENDING );
			Integer originalId = save(ctx, original);
			FinanceTracking ft = new FinanceTracking()
				.setFinance(original)
				.setDomain(original.getDomain())
				.setTrackingDate(new Date())
				.setType(FinanceTrackingType.FRACTIONED)
				.setDescription("Fracci\u00F3n 1/2")
				.setRegistryBank(null)
				.setPayMethodTypeDetail(null)
				.setBankStatementLink(null)
				.setAmount(newAmount)
				.setRecorded(true);
			insertTracking(ctx, ft)
			
			
			ctx.log().info("INSERT FINANCE ( AMOUNT DIFERENCE " + newAmount +") id: " + originalId);
			ctx.log().info(" ----- END FINANCE FRACTION----- ");
		}
 
	 */
	
	public static AccountEntry[] getFinanceEntry(AONContext ctx, Finance finance) {
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, finance.getDueDate());
		if (period == null) {
			throw new AonCoreException( AonError.WRONG_PERIOD.format( finance.getDueDate() ) );
		}
		AccountEntry ae = new AccountEntry()
			.setDomain(finance.getDomain())
			.setPeriod(period.getId())
			.setEntryDate(finance.getDueDate())
			.setEntryType(AccountEntryType.FINANCE)
			.setActivity(finance.getInvoice()==null?null:finance.getInvoice().getActivity())
			.setSecurityLevel(finance.getSecurityLevel());
		FinanceEntry financeEntry = new FinanceEntry();
		financeEntry.setAccountEntry(ae);
		financeEntry.setBankAccount( AccountDAO.get( ctx, finance.getPayAccountId()) );
		if (finance.getRegistryAccountId() == null) {
			Invoice invoice = finance.getInvoice();
			if (invoice == null || finance.getRegistry() == null || finance.getRegistry().getId() == null) {
				throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
			}
			invoice.getType().visit(invoice, new IInvoiceTypeVisitor() {
				private void fill( Account acc) {
					if (acc == null) {
						throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
					}
					finance.setRegistryAccountId(acc.getId());
					finance.setRegistryAccountCode(acc.getCode());
					finance.setRegistryAccountDescription(acc.getDescription());
				}
				
				@Override
				public void visitSales(Invoice invoice) {
					fill( RegistryDAO.getCustomerAccount(ctx, finance.getRegistry().getId()));
				}
				
				@Override
				public void visitPurchase(Invoice invoice) {
					fill( RegistryDAO.getSupplierAccount(ctx, finance.getRegistry().getId()));
				}
				
				@Override
				public void visitExpenses(Invoice invoice) {
					fill( RegistryDAO.getCreditorAccount(ctx, finance.getRegistry().getId()));
				}
				@Override
				public void visitUndeductible(Invoice invoice) {
					visitExpenses(invoice);
				}
				
			});
		}
		financeEntry.add(finance);
		return FinanceRecorder.recordFinanceEntry(financeEntry);
	}

	public static void pay(AONContext ctx, Finance finance) {
		ctx.log().info(" ----- START FINANCE PAY ----- ");
		try {
			ctx.checkWrite();
			Finance original = FinanceValidation.validatePay(ctx, finance);
			AccountEntry entry = null;
			if (finance.getPayAccountId() != null) {
				AccountEntry[] entries = getFinanceEntry(ctx, original);
				if (entries != null && entries.length == 1) {
					entry = entries[0];	
					Integer entryId = AccountEntryDAO.save(ctx, entry);
					entry.setId(entryId);
					pay(ctx,finance,entry);
				}
			} else {
				// TODO ¿Si no se quiere contabilizar el pago?
				throw new AonCoreException(AonError.FINANCE_TRACKING_NO_BANK_ACCOUNT.getMessage());
			}
		} catch (Throwable t) {
			ctx.log().info(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().info(" ----- END FINANCE PAY ----- ");
		}
	}

	private static void deleteFinanceTracking(AONContext ctx, FinanceTracking ft) {
		if (!isLastTracking(ctx,ft)) {
			throw new AonCoreException(AonError.FINANCE_TRACKING_LATER_TRACKINGS.getMessage());
		}
		
		if (ft.getBankStatementLink() == null ) {
			if (ft.isRecorded()) {
				if (SecurityDAO.getUser(ctx).hasAccountingRole()) {
					deleteAccountEntryFinanceTracking(ctx,ft);
				} else {
					throw new AonCoreException(AonError.FINANCE_TRACKING_RECORDED.getMessage());		
				}
			}
			int i = ctx.getDslContext()
				.delete(FINANCE_TRACKING)
				.where(FINANCE_TRACKING.ID.equal(ft.getId()))
				.execute();
			ctx.log().info("DELETE FINANCE_TRACKING  ("+i+") id: " + ft.getId());
			FinanceTracking previuosTracking = getLastTracking(ctx, ft.getFinance().getId());
			FinanceStatus newStatus = previuosTracking == null ? FinanceStatus.PENDING : previuosTracking.getType().getFinanceStatus(); 
			updateFinanceStatus(ctx, ft.getFinance().getId(), newStatus );
			
		} else {
			// TODO El movimiento viene de extracto bancario.
		}
	}

	private static void deleteAccountEntryFinanceTracking(AONContext ctx, FinanceTracking ft) {
		int i = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.where(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING.equal(ft.getId()))
			.execute();
		ctx.log().info("DELETE ACCOUNT_ENTRY_FINANCE_TRACKING ("+i+") Tracking: " + ft.getId());
	}
}
