package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
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
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;

import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.security.Scope;
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
		@Override public Property<Date> getInvoiceDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<String> getInvoiceReferenceCodeProperty() {return new FilterDAO.PropertyDAO<String>(INVOICE.REFERENCE_CODE);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<Integer>(FINANCE.PAY_METHOD);}
	}
	
	// ---------------------------------------------------------- ORDER
	public static enum FinanceOrder {
		 DUE_DATE ( FINANCE.DUE_DATE.asc(),FINANCE.ID.asc())
		,DUE_DATE_DESC ( FINANCE.DUE_DATE.desc(),FINANCE.ID.asc())
		,REGISTRY_NAME( REGISTRY.NAME.asc() )
		,AMOUNT( FINANCE.AMOUNT.asc() )
		,PAYMETHOD( PAY_METHOD.NAME.asc() )
		,CREATION_DATE ( FINANCE.CREATION_DATE.asc(),FINANCE.ID.asc())
		,CREATION_DATE_DESC ( FINANCE.CREATION_DATE.desc(),FINANCE.ID.desc())
		,ORDER_MODIFICATION_DATE_DESC ( FINANCE.MODIFICATION_DATE.desc(),FINANCE.CREATION_DATE.desc())
		;
		private SortField<?>[] fields;
		
		private FinanceOrder( SortField<?> ...fields) {
			this.fields = fields;
		}
		public SortField<?>[] getFields() {
			return fields;
		}
		
		public static FinanceOrder safeEnum(int order) {
			if (order < 0 || order > FinanceOrder.values().length) {
				return DUE_DATE;
			}
			return FinanceOrder.values()[order];
		}
	}
	// -------------------------------------------------------------
	// --------------------- FINANCE --- LECTURA -------------------
	// -------------------------------------------------------------
	public static Finance getFinance(AONContext ctx,Integer id) {
		return fetch(ctx,p -> p.getDomainProperty().eq(ctx.getDomainId())
				   	.and(p.getIdProperty().eq(id)), 0, 1)
		.findFirst().orElse(null);
	}
	public static Stream<Finance> getFinanceStream(AONContext ctx,FinanceFilter filter) {
		return getFinanceStream(ctx,filter, FinanceOrder.CREATION_DATE);
	}
	
	public static Stream<Finance> getFinanceStream(AONContext ctx,FinanceFilter filter, FinanceOrder orderBy) {
		return fetch(ctx, filter)
				.orderBy(orderBy.getFields())
				.fetch()
				.stream()
				.map(new FullFinanceFiller());
	}
	
	public static Stream<Finance> getSiiFinanceStream(AONContext ctx, FinanceFilter filter){
		ctx.checkRead();
		return fetch(ctx, filter)
			.and(FINANCE.ID.notIn(ctx.getDslContext()
					.select(DATA_RESPONSE.SOURCE_ID)
						.from(DATA_RESPONSE)
						.where(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_FINANCE.value()))
						.and(DATA_RESPONSE.SOURCE_ID.eq(FINANCE.ID))
					))
		.fetch()
		.stream()
		.map(new FullFinanceFiller());
	}

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
	
	public static Stream<Finance> fetch(AONContext ctx, FinanceFilter filter, int offset, int numberOfRows) {
		return fetch(ctx, filter, offset, numberOfRows, FinanceOrder.CREATION_DATE_DESC );	
	}
			
	public static Stream<Finance> fetch(AONContext ctx, FinanceFilter filter, int offset, int numberOfRows, FinanceOrder orderBy) {
		return fetch(ctx, filter)
				.orderBy(orderBy.getFields())
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( new FullFinanceFiller() );
	}
	
	// -------------------------------------------------------------
	// ------------------- FINANCE --- ESCRITURA -------------------
	// -------------------------------------------------------------
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
		ctx.log().debug("INSERT FINANCE id: " + record.getValue(FINANCE.ID));		
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
		ctx.log().debug("UPDATE FINANCE  ("+i+") id: " + finance.getId());
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
		ctx.log().debug("DELETE FINANCE ("+i+") id: " + finance.getId());
	}
	
	private static void removeFinanceTrackingFractions(AONContext ctx, Finance finance) {
		if (finance.isPending()) {
			int i = ctx.getDslContext()
				.delete(FINANCE_TRACKING)
				.where(FINANCE_TRACKING.FINANCE.eq(finance.getId()))
				.and(FINANCE_TRACKING.TYPE.eq(FinanceTrackingType.FRACTIONED.value()))
				.execute();
			ctx.log().debug("DELETE FINANCE_TRACKING Fractions ("+i+") Finance id: " + finance.getId());
		}
	}
	
	public static void deleteInvoiceFinances(AONContext ctx, Integer invoiceId) {
		ctx.checkWrite();
		LinkedList<Finance>  finances = getInvoiceFinances(ctx, invoiceId);
		for (Finance finance : finances ) {
			delete(ctx, finance.getId());
		}
	}
	
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
			insert(ctx, finance);
		}
		invoice.setFinances(finances);
		return invoice;
	}
	
	public static void insertFinances(AONContext ctx, List<Finance> finances) {
		for (Finance finance : finances) {
			insert(ctx, finance);
		}
	}
	// --------------------------------------------
	// ------ FINANCE FRACCIONAMIENTO -------------
	// --------------------------------------------
	public static LinkedList<Finance> fraction(AONContext ctx, Finance origin, LinkedList<Finance> fractions) {
		LinkedList<Finance> result = new LinkedList<Finance>();
		Finance original = FinanceValidation.validateFraction(ctx, origin,fractions);
		double originalAmount = original.getAmount();
		int count = 1;
		for (Finance fin : fractions) {
			Finance fraction = null;
			if (count == 1) {
				fraction = original;
			} else {
				fraction = new Finance()	
					.setInvoice(original.getInvoice())
					.setPayMethod(original.getPayMethod())
					.setPayMethodType(original.getPayMethodType())
					.setPayMethodName(original.getPayMethodName())
					.setRegistry(original.getRegistry())
					.setScope(original.getScope())
					.setDomain(original.getDomain())
					.setPayment(original.isPayment())
					.setRegistryDocument(original.getRegistryDocument())
					.setRegistryDocumentType(original.getRegistryDocumentType())
					.setRegistryDocumentCountry(original.getRegistryDocumentCountry())
					.setRegistryName(original.getRegistryName())
					.setRegistryAccountId(original.getRegistryAccountId())
					.setRegistryAccountCode(original.getRegistryAccountCode())
					.setRegistryAccountDescription(original.getRegistryAccountDescription())
					.setConcept(original.getConcept())
					.setDueDate(original.getDueDate())
					.setBankAccount(original.getBankAccount())
					.setBankAlias(original.getBankAlias())
					.setBic(original.getBic())
					.setChequeNumber(original.getChequeNumber())
					.setFinanceStatus(original.getFinanceStatus())
					.setSecurityLevel(original.getSecurityLevel())
					.setRemarks(original.getRemarks())
					.setManual(original.isManual())
					.setAdvance(original.isAdvance())
					.setPayroll(original.isPayroll())
					.setPrepayment(original.isPrepayment())
					.setSourceId(original.getSourceId())
					;
			}
			fraction.setAmount(fin.getAmount());
			fraction.setExpenses(fin.getExpenses());
			Integer fractionId = save(ctx, fraction);
			fraction = getFinance(ctx, fractionId);
			String description = "Fracci\u00F3n " + count + " de " + fractions.size(); 
			FinanceTrackingDAO.fraction(ctx, fraction, description,originalAmount);
			result.add(fraction);
			count++;
		}
		return result;
	}
	
	
	// -------------------------------------------------------------
	// ------ FINANCE --- CALCULO EN FUNCION DE RPAYMETHOD ---------
	// -------------------------------------------------------------
	public static LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice) {
		LinkedList<Finance> finances = new LinkedList<>();
		RegistryPayMethod rPayMethod = RegistryPayMethodDAO.getStream(ctx, prop -> prop.getDomainProperty()
				.eq(ctx.getDomainId()).and(prop.getRegistryProperty().eq(invoice.getRegistry()))).findFirst()
				.orElse(null);
		;
		RegistryBank rBank = null;
		if (rPayMethod != null && rPayMethod.getRbank() != null) {
			rBank = RegistryOldDAO.getRBankStream(ctx, prop -> prop.getDomainProperty().eq(ctx.getDomainId())
					.and(prop.getIdProperty().eq(rPayMethod.getRbank()))).findFirst().orElse(null);
		}
		Date date = invoice.getIssueDate();
		int numberOfPymnts =  ((rPayMethod == null) || (rPayMethod.getNumberOfPymnts() == 0)) ? 1 : rPayMethod.getNumberOfPymnts();
		int daysToFirstPymnt = ((rPayMethod == null) || (rPayMethod.getDaysToFirstPymnt() == 0)) ? 0 : rPayMethod.getDaysToFirstPymnt();
		int daysBetwenPymnts = ((rPayMethod == null) || (rPayMethod.getDaysBetwenPymnts() == 0)) ? 0 : rPayMethod.getDaysBetwenPymnts();
		PayMethod payMethod = null;
		if (rPayMethod != null && rPayMethod.getPayMethod() != null) {
			payMethod = PayMethodDAO.get(ctx, rPayMethod.getPayMethod());
		}
		double paymentPrice = AonMathUtils.round(invoice.getTotal() / numberOfPymnts);
		for (int i = 0; i < numberOfPymnts; i++) {
			int days = (i==0?daysToFirstPymnt:daysBetwenPymnts);
			date = (rPayMethod==null? date : calculatePaymentDate(days, rPayMethod.getPymntDays(), date));
			Finance finance = buildFinance(invoice, date, payMethod , rBank, paymentPrice );  
			finances.add(finance);
		}
		double lastPaymentPrice = AonMathUtils.round(invoice.getTotal() - (paymentPrice * (numberOfPymnts - 1)));
		if ( !AonMathUtils.equals(paymentPrice, lastPaymentPrice)) {
			finances.getLast().setAmount(lastPaymentPrice);
		}
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

	private static Finance buildFinance(Invoice invoice, Date date, PayMethod payMethod, RegistryBank rBank, double totalPrice) {
		return  new Finance()
			.setDomain(invoice.getDomain())
			.setPayment(invoice.getType() != InvoiceType.SALES)
			.setRegistry( new Registry().setId(invoice.getRegistry()))
			.setRegistryName(invoice.getRegistryName())
			.setRegistryDocument(invoice.getRegistryDocument())
			.setRegistryDocumentType(invoice.getRegistryDocumentType())
			.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
			.setAmount(totalPrice)
			.setConcept(invoice.getDocumentNumber())
			.setInvoice(invoice)
			.setDueDate(date)
			.setPayMethod(payMethod==null?null:payMethod.getId())
			.setPayMethodName(payMethod==null?null:payMethod.getName())
			.setPayMethodType(payMethod==null?null:payMethod.getType())
			.setBankAccount((rBank==null) ? null : rBank.getBankAccount() )
			.setBankAlias((rBank==null) ? null : rBank.getAlias())
			.setBic((rBank==null) ? null : rBank.getBic())
			.setFinanceStatus(FinanceStatus.PENDING)
			.setSecurityLevel(invoice.getSecurityLevel())
			.setScope(invoice.getScope());
	}

	// -------------------------------------------------------------
	// ---------------------------- MAP ----------------------------
	// -------------------------------------------------------------
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

}
