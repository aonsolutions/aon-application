package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceProperties;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.validation.FinanceValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

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
	// ------------------------------------------------------------- FINANCE
	public static Finance getFinance(AONContext ctx,Integer id) {
		return fetch(ctx,p -> p.getDomainProperty().eq(ctx.getDomainId())
				   	.and(p.getIdProperty().eq(id)), 0, 1)
		.findFirst().orElse(null);
	}
	
	public static Stream<Finance> fetch(AONContext ctx
			, FinanceFilter filter
			, int offset
			, int numberOfRows) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FINANCE.fields())
			.select(REGISTRY.fields())
			.select(PAY_METHOD.fields())
			.select(INVOICE.fields())
				.from(FINANCE)
				.join(REGISTRY).on(FINANCE.REGISTRY.equal(REGISTRY.ID))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
				.where(FINANCE_PROPERTIES.getConditions(filter))
				.limit(offset,numberOfRows)
				.fetch()
				.stream()
				.map( new FullFinanceFiller() )
			;
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
				.set(FINANCE.SECURITY_LEVEL,  (byte) (finance.isConfidential()?1:0) )
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
		return record.getValue(FINANCE.ID); 
	}
	
	private static void update(AONContext ctx, Finance finance) {
		ctx.checkWrite();
		FinanceValidation.validateSave(ctx, finance);
		ctx.getDslContext().update(FINANCE)
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
			.set(FINANCE.SECURITY_LEVEL,  (byte) (finance.isConfidential()?1:0) )
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
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		Finance finance = getFinance(ctx,id);
		FinanceValidation.validateDelete(ctx, finance);
		removeFractionTracking(ctx,finance);
		ctx.getDslContext()
			.delete(FINANCE)
			.where(FINANCE.ID.equal(id))
			.execute();
	}
	
	private static void removeFractionTracking(AONContext ctx, Finance finance) {
		if (finance.isPending()) {
			ctx.getDslContext()
			.delete(FINANCE_TRACKING)
				.where(FINANCE_TRACKING.FINANCE.eq(finance.getId()))
				.execute();
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

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(FINANCE.ID);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(FINANCE.DOMAIN);
		}

		@Override
		public Property<Byte> getConfidentialProperty() {
			return new FilterDAO.PropertyDAO<Byte>(ACCOUNT_ENTRY.SECURITY_LEVEL);
		}

		@Override
		public Property<Integer> getRegistryProperty() {
			return new FilterDAO.PropertyDAO<Integer>(FINANCE.REGISTRY);
		}

		@Override
		public Property<Date> getDueDateProperty() {
			return new FilterDAO.DatePropertyDAO(FINANCE.DUE_DATE);
		}

		@Override
		public Property<Integer> getInvoiceProperty() {
			return new FilterDAO.PropertyDAO<Integer>(FINANCE.INVOICE);
		}

		@Override
		public Property<Byte> getStatusProperty() {
			return new FilterDAO.PropertyDAO<Byte>(FINANCE.STATUS);
		}
		
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
			;
		}

			
	}
}
