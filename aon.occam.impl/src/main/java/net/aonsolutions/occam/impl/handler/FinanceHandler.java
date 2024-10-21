package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import net.aonsolutions.occam.api.model.BankAccount;
import net.aonsolutions.occam.api.model.Filter.FinanceFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Finance;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.PayMethod;
import net.aonsolutions.occam.api.model.Properties.FinanceProperties;
import net.aonsolutions.occam.api.model.RegistryBank;
import net.aonsolutions.occam.api.model.RegistryPayMethod;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.FinanceStatus;
import net.aonsolutions.occam.api.model.type.FinanceTrackingType;
import net.aonsolutions.occam.api.model.type.FinanceType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.SecurityLevel;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.InvoiceHandler.InvoiceFiller;
import net.aonsolutions.occam.impl.handler.PayMethodHandler.PayMethodFiller;

class FinanceHandler {
	
	private FinanceHandler() {
	}

	// ---------------------------------------------------------- FILTRO
	private static final FinancePropertiesDAO FINANCE_PROPERTIES = new FinancePropertiesDAO();
	protected static class FinancePropertiesDAO implements FinanceProperties {

		Condition getConditions(FinanceFilter filter) {
			FilterHandler filterDAO = (FilterHandler) filter.filter(this);
			if (filterDAO == null) return DSL.trueCondition();
			return filterDAO.getCondition();
		}

		@Override public Property<Integer> getIdProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.DOMAIN);}
		@Override public Property<Integer> getScopeProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.SCOPE);}
		@Override public Property<Byte> getFinanceTypeProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.PAYMENT);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.REGISTRY);}
		@Override public Property<String> getRegistryDocumentProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.RDOCUMENT);}
		@Override public Property<Byte> getRegistryDocumentTypeProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.RDOCUMENT_TYPE);}
		@Override public Property<String> getRegistryDocumentCountryProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.RDOCUMENT_COUNTRY);}
		@Override public Property<String> getRegistryNameProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.RNAME);}
		@Override public Property<Double> getAmountProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.AMOUNT);}
		@Override public Property<Double> getExpensesProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.EXPENSES);}
		@Override public Property<String> getConceptProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.CONCEPT);}
		@Override public Property<Integer> getInvoiceProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.INVOICE);}
		@Override public Property<Date> getDueDateProperty() {return new FilterHandler.DatePropertyDAO(FINANCE.DUE_DATE);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.PAY_METHOD);}
		@Override public Property<String> getBankAccountProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.BIC);}
		@Override public Property<String> getChequeNumberProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.CHEQUE_NUMBER);}
		@Override public Property<Byte> getStatusProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.STATUS);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.SECURITY_LEVEL);}
		@Override public Property<String> getRemarksProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.REMARKS);}
		@Override public Property<String> getInvoiceReferenceCodeProperty() {return new FilterHandler.PropertyDAO<>(INVOICE.REFERENCE_CODE);}
		@Override public Property<Date> getInvoiceDateProperty() {return new FilterHandler.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Byte> getPayMethodTypeProperty() {return new FilterHandler.PropertyDAO<>(PAY_METHOD.TYPE);}
		@Override public Property<Byte> getPayrollProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.PAYROLL);}
		@Override public Property<String> getCreationUserProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.CREATION_DATE );}
		@Override public Property<String> getModificationUserProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterHandler.PropertyDAO<>(FINANCE.MODIFICATION_DATE);}
	}
	// ---------------------------------------------------------- ORDER
	enum FinanceOrder {
		 DUE_DATE ( FINANCE.DUE_DATE.asc(),FINANCE.ID.asc())
		,DUE_DATE_DESC ( FINANCE.DUE_DATE.desc(),FINANCE.ID.asc())
		,REGISTRY_NAME( REGISTRY.NAME.asc() )
		,AMOUNT( FINANCE.AMOUNT.asc() )
		,PAYMETHOD( PAY_METHOD.NAME.asc() )
		,CREATION_DATE ( FINANCE.CREATION_DATE.asc(),FINANCE.ID.asc())
		,CREATION_DATE_DESC ( FINANCE.CREATION_DATE.desc(), REGISTRY.NAME.asc(), FINANCE.ID.desc())
		,ORDER_MODIFICATION_DATE_DESC ( FINANCE.MODIFICATION_DATE.desc(),FINANCE.CREATION_DATE.desc())
		;
		private SortField<?>[] fields;
		
		private FinanceOrder( SortField<?> ...fields) {
			this.fields = fields;
		}
		SortField<?>[] getFields() {
			return fields;
		}
		
		static FinanceOrder safeEnum(int order) {
			if (order < 0 || order > FinanceOrder.values().length) {
				return DUE_DATE;
			}
			return FinanceOrder.values()[order];
		}
	}
	
	// ---------------------------------------------------------- FILKER
	static class FinanceFiller extends Filler<Finance> {
		
		@Override
		public Finance apply(Record r) {
			return build(r);
		}
		
		static Finance build(Record r) {
			return new Finance()
				.setId(getValue(r,FINANCE.ID))
				.setDomain(getValue(r,FINANCE.DOMAIN))
				.setFinanceType(FinanceType.value(getValue(r,FINANCE.PAYMENT)).orElse(null))
				.setRegistry(getValue(r,FINANCE.REGISTRY))
				.setRegistryDocument(getValue(r,FINANCE.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.value(getValue(r,FINANCE.RDOCUMENT_TYPE)).orElse(null))
				.setRegistryDocumentCountry(Country.value(getValue(r,FINANCE.RDOCUMENT_COUNTRY)).orElse(null))
				.setRegistryName(getValue(r,FINANCE.RNAME))
				.setAmount(getValue(r,FINANCE.AMOUNT))
				.setExpenses(getValue(r,FINANCE.EXPENSES))
				.setConcept(getValue(r,FINANCE.CONCEPT))
				.setInvoice(InvoiceFiller.build(r)) 
				.setDueDate(getValue(r,FINANCE.DUE_DATE))
				.setPayMethod(PayMethodFiller.build(r))
				.setBankAccount( new BankAccount(getValue(r,FINANCE.BANK_ACCOUNT)) )
				.setBankAlias(getValue(r,FINANCE.BANK_ALIAS))
				.setBic(getValue(r,FINANCE.BIC))
				.setChequeNumber(getValue(r,FINANCE.CHEQUE_NUMBER))
				.setFinanceStatus(FinanceStatus.value( getValue(r,FINANCE.STATUS)).orElse(null))
				.setConfidential( SecurityLevel.confidential( getValue(r,FINANCE.SECURITY_LEVEL)))
				.setRemarks(getValue(r,FINANCE.REMARKS))
				.setScope(getValue(r,FINANCE.SCOPE))
				.setManual(AonEnumUtils.getBoolean( getValue(r,FINANCE.MANUAL)))
				.setAdvance(AonEnumUtils.getBoolean( getValue(r,FINANCE.ADVANCE)))
				.setPayroll(AonEnumUtils.getBoolean( getValue(r,FINANCE.PAYROLL)))
				.setPrepayment(AonEnumUtils.getBoolean( getValue(r,FINANCE.PREPAYMENT)))
				.setSourceId(getValue(r,FINANCE.SOURCE_ID))
				.setFinanceGroup(getValue(r,FINANCE.FINANCE_GROUP))
				.setCreationUser(getValue(r,FINANCE.CREATION_USER))
				.setCreationDate(getValue(r,FINANCE.CREATION_DATE))
				.setModificationUser(getValue(r,FINANCE.MODIFICATION_USER))
				.setModificationDate(getValue(r,FINANCE.MODIFICATION_DATE))
				;
		}
	}
	// -------------------------------------------------------------
	// --------------------- FINANCE --- LECTURA -------------------
	// -------------------------------------------------------------
	private static SelectConditionStep<Record> select(AONContext ctx , int domain) {
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
			.where(FINANCE.DOMAIN.eq( domain ));
	}
	
	static Optional<Finance> get(AONContext ctx,int domain, Integer id) {
		return select(ctx, domain)
			.and(FINANCE.ID.eq( id ))
			.fetch()
			.stream()
			.map( new FinanceFiller() )
			.findFirst();
	}
	
	static Stream<Finance> stream(AONContext ctx, int domain, FinanceFilter filter) {
		return stream(ctx, domain, filter, 0, Integer.MAX_VALUE, FinanceOrder.CREATION_DATE_DESC );
	}
	
	static Stream<Finance> stream(AONContext ctx, int domain, FinanceFilter filter, int offset, int numberOfRows) {
		return stream(ctx, domain, filter, offset, numberOfRows, FinanceOrder.CREATION_DATE_DESC );	
	}
			
	static Stream<Finance> stream(AONContext ctx,int domain, FinanceFilter filter, int offset, int numberOfRows, FinanceOrder orderBy) {
		return select(ctx, domain)
			.and(FINANCE_PROPERTIES.getConditions(filter))
			.orderBy(orderBy.getFields())
			.limit(offset,numberOfRows)
			.fetch()
			.stream()
			.map( new FinanceFiller() )
		;
	}
	
	
	// -------------------------------------------------------------
	// ------------------- FINANCE --- ESCRITURA -------------------
	// -------------------------------------------------------------
	static void save(AONContext ctx, int domain, Invoice invoice) {
		AonCollectionUtils.stream( invoice.getFinances() )
 			.forEach( finance -> {
				if ( !finance.isFullPending()) {
					ctx.log().debug("** FINANCE NOT SAVED [NOT PENDING]");
					return;
				}
				if ( finance.getId() == null && finance.isDeleted()) {
					ctx.log().debug("** FINANCE NOT SAVED [MARKED TO DELETE BUT NOT SAVED]");
					return;
				}
				if ( finance.getId() != null && finance.isDeleted()) {
					ctx.log().debug("** FINANCE MARKED TO DELETE");
					delete(ctx, domain, finance.getId());
					return;
				} 
				
				if ( AonMathUtils.isNotZero(finance.getAmount()) ) {
					ctx.log().debug("** FINANCE READY TO SAVE");
					FinanceAutoComplete.completeFinanceFromInvoice(ctx, new Pair<>(invoice, finance));
					Integer financeId = save(ctx, finance);
					finance.setId(financeId);
				} else {
					ctx.log().debug("** FINANCE NOT SAVED [AMOUNT 0]");
				}
			});
	}

	static Integer save(AONContext ctx, Finance finance) {
		if (finance.getId() == null) {
			return insert(ctx, finance);
		} else {
			update(ctx, finance);
			return finance.getId();
		}
	}

	private static Integer insert(AONContext ctx, Finance finance) {
		ctx.checkWrite();
		FinanceAutoComplete.completeFinance(ctx, finance);
		FinanceValidation.validateSave(ctx, finance);
		FinanceRecord record = ctx.getDslContext()
			.insertInto(FINANCE)
				.set(FINANCE.DOMAIN,finance.getDomain())
				.set(FINANCE.PAYMENT, FinanceType.value(finance.getFinanceType()))
				.set(FINANCE.REGISTRY,finance.getRegistry())
				.set(FINANCE.RDOCUMENT,finance.getRegistryDocument())
				.set(FINANCE.RDOCUMENT_TYPE,finance.getRegistryDocumentType()==null?null:finance.getRegistryDocumentType().value())
				.set(FINANCE.RDOCUMENT_COUNTRY,finance.getRegistryDocumentCountry()==null?null:finance.getRegistryDocumentCountry().getIso2())
				.set(FINANCE.RNAME,finance.getRegistryName())
				.set(FINANCE.AMOUNT,finance.getAmount())
				.set(FINANCE.EXPENSES,finance.getExpenses())
				.set(FINANCE.CONCEPT,finance.getConcept())
				.set(FINANCE.INVOICE,finance.getInvoice().map( i -> i.getId()).orElse(null))
				.set(FINANCE.DUE_DATE,AonDateUtils.toSql(finance.getDueDate()))
				.set(FINANCE.PAY_METHOD,finance.getPayMethod().map( p -> p.getId()).orElse(null))
				.set(FINANCE.BANK_ACCOUNT,finance.getBankAccount().map( b -> b.getIban()).orElse(null))
				.set(FINANCE.BANK_ALIAS,finance.getBankAlias())
				.set(FINANCE.BIC,finance.getBic())
				.set(FINANCE.CHEQUE_NUMBER,finance.getChequeNumber())
				.set(FINANCE.STATUS,finance.getFinanceStatus().value())
				.set(FINANCE.SECURITY_LEVEL, AonEnumUtils.getByte(finance.isConfidential()) )
				.set(FINANCE.REMARKS,finance.getRemarks())
				.set(FINANCE.SCOPE,finance.getScope())
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
			.set(FINANCE.PAYMENT, FinanceType.value(finance.getFinanceType()))
			.set(FINANCE.REGISTRY,finance.getRegistry())
			.set(FINANCE.RDOCUMENT,finance.getRegistryDocument())
			.set(FINANCE.RDOCUMENT_TYPE,finance.getRegistryDocumentType()==null?null:finance.getRegistryDocumentType().value())
			.set(FINANCE.RDOCUMENT_COUNTRY,finance.getRegistryDocumentCountry()==null?null:finance.getRegistryDocumentCountry().getIso2())
			.set(FINANCE.RNAME,finance.getRegistryName())
			.set(FINANCE.AMOUNT,finance.getAmount())
			.set(FINANCE.EXPENSES,finance.getExpenses())
			.set(FINANCE.CONCEPT,finance.getConcept())
			.set(FINANCE.INVOICE,finance.getInvoice().map( inv -> inv.getId()).orElse(null))
			.set(FINANCE.DUE_DATE,AonDateUtils.toSql(finance.getDueDate()))
			.set(FINANCE.PAY_METHOD,finance.getPayMethod().map( p -> p.getId()).orElse(null))
			.set(FINANCE.BANK_ACCOUNT,finance.getBankAccount().map( b -> b.getIban()).orElse(null))
			.set(FINANCE.BANK_ALIAS,finance.getBankAlias())
			.set(FINANCE.BIC,finance.getBic())
			.set(FINANCE.CHEQUE_NUMBER,finance.getChequeNumber())
			.set(FINANCE.STATUS,finance.getFinanceStatus().value())
			.set(FINANCE.SECURITY_LEVEL, AonEnumUtils.getByte(finance.isConfidential()) )
			.set(FINANCE.REMARKS,finance.getRemarks())
			.set(FINANCE.SCOPE,finance.getScope())
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

	static void delete(AONContext ctx, int domain, Integer id) {
		ctx.checkWrite();
		get(ctx,domain,id)
			.ifPresent( finance -> {
				FinanceValidation.validateDelete(ctx, finance);
				removeFinanceTrackingFractions(ctx,finance);
				int i = ctx.getDslContext()
					.delete(FINANCE)
					.where(FINANCE.ID.equal(id))
					.execute();
				ctx.log().debug("DELETE FINANCE ("+i+") id: " + finance.getId());
			});
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
	
	private class FinanceAutoComplete {
		private FinanceAutoComplete() {
		}
		
		/**
		 * Si no hay registry, se rellena con el de invoice (si hay). 
		 */
		private static final BiConsumer<Finance,AONContext> COMPLETE_REGISTRY_IF_EMPTY = (finance,ctx) -> {
			if (finance.getRegistry() == null) {
				finance.getInvoice().ifPresent( i -> finance.setRegistry( i.getRegistry()));
			}
		};
		
		/**
		 * Se rellenan los datos de registry, bien de la factura o del registry. 
		 */
		private static final BiConsumer<Finance,AONContext> COMPLETE_REGISTRY_DOCUMENT_IF_EMPTY = (finance,ctx) -> {
			if (AonStringUtils.isEmpty(finance.getRegistryDocument())) {
				finance.getInvoice().ifPresent( i -> finance 
					.setRegistryDocument( i.getRegistryDocument() )
					.setRegistryDocumentType( i.getRegistryDocumentType() )
					.setRegistryDocumentCountry( i.getRegistryDocumentCountry() )
				);
			}
		};

		/**
		 * Se rellena el concepto si no existe. 
		 */
		private static final BiConsumer<Finance,AONContext> COMPLETE_CONCEPT_IF_EMPTY = (finance,ctx) -> {
			if (AonStringUtils.isBlank( finance.getConcept() )) {
				finance.getInvoice().ifPresent( i -> finance.setConcept( i.getDocumentNumber()));
			}
		};
		
		/**
		 * Se rellena el nivel de seguridad. 
		 */
		private static final BiConsumer<Finance,AONContext> COMPLETE_SECURITY_LEVEL_IF_EMPTY = (finance,ctx) -> {
			finance.getInvoice().ifPresent( i -> finance.setConfidential( i.isConfidential()) );
		};
		
		/**
		 * Se rellena el nivel de seguridad. 
		 */
		private static final BiConsumer<Finance,AONContext> COMPLETE_SCOPE_IF_EMPTY = (finance,ctx) -> {
			if (finance.getScope() == null) {
				finance.getInvoice().ifPresentOrElse(
					i    -> finance.setScope( i.getScope())
					, () -> finance.setScope( 
						finance.isPayroll()
							?SecurityHandler.getScopeFromContract(ctx, finance.getRegistry()).orElse(null)
							:SecurityHandler.getScopeFromRegistry(ctx
									, finance.getFinanceType() == FinanceType.PAYMENT
									, finance.getRegistry()).orElse(null)
						)
					);
			}
		};
		
		/**
		 * Se rellena payment. 
		 */
		private static final BiConsumer<Finance,AONContext> COMPLETE_PAYMENT = (finance,ctx) -> 
			finance.getInvoice().ifPresent( i -> { 
				if(i.getType() != null) {
					finance.setFinanceType( i.isSales() ? FinanceType.COLLECTION : FinanceType.PAYMENT );
				} else {
					InvoiceType invoiceType = ctx.getDslContext()
						.select(INVOICE.TYPE)
						.from(INVOICE)
						.where(INVOICE.ID.eq(i.getId()))
						.fetch()
						.stream()
						.map(r -> InvoiceType.value(r.getValue(INVOICE.TYPE))
								.orElseThrow( () -> new AonCoreException(AonError.INVOICE_NOT_FOUND.getMessage())) 
							)
						.findFirst()
						.orElseThrow( () -> new AonCoreException(AonError.FINANCE_UNKNOWN_PAYMENT.getMessage()))
					;						
					finance.setFinanceType( invoiceType == InvoiceType.SALES ? FinanceType.COLLECTION : FinanceType.PAYMENT ); 
				}	
			}
		);
		
		private static void completeFinance(AONContext ctx, Finance finance) throws AonCoreException {
				COMPLETE_REGISTRY_IF_EMPTY
				.andThen(COMPLETE_REGISTRY_DOCUMENT_IF_EMPTY)
				.andThen(COMPLETE_CONCEPT_IF_EMPTY)
				.andThen(COMPLETE_SECURITY_LEVEL_IF_EMPTY)
				.andThen(COMPLETE_SCOPE_IF_EMPTY)
				.andThen(COMPLETE_PAYMENT)
				.accept(finance, ctx);
		}
		
		private static final BiConsumer<AONContext,Pair<Invoice,Finance>> COMPLETE_NEW_FINANCES = (ctx,pair) -> {
			Finance finance = pair.getRight();
			if (finance.getId() == null ) {
				Invoice inv = pair.getLeft();
				finance
					.setInvoice(inv)
					.setDomain(inv.getDomain())
					.setRegistry(inv.getRegistry())
					.setRegistryDocument(inv.getRegistryDocument())
					.setRegistryDocumentType(inv.getRegistryDocumentType())
					.setRegistryDocumentCountry(inv.getRegistryDocumentCountry())
					.setRegistryName(inv.getRegistryName())
					.setScope(inv.getScope())
					.setConfidential(inv.isConfidential())
					.setConcept(inv.getDocumentNumber())
					.setFinanceStatus(FinanceStatus.PENDING);
			}
		};
		
		private static final BiConsumer<AONContext,Pair<Invoice,Finance>> COMPLETE_SAVED_FINANCES = (ctx,pair) -> {
			Finance finance = pair.getRight();
			if (finance.getId() != null ) {
				Invoice inv = pair.getLeft();
				finance
					.setConfidential(inv.isConfidential())
					.setConcept(inv.getDocumentNumber());
			}
		};
		
		static void completeFinanceFromInvoice(AONContext ctx, Pair<Invoice,Finance> pair) throws AonCoreException {
			COMPLETE_NEW_FINANCES
				.andThen(COMPLETE_SAVED_FINANCES)
				.accept(ctx,pair);
		}
		
	}

	// -------------------------------------------------------------
	// ------ FINANCE --- CALCULO EN FUNCION DE RPAYMETHOD ---------
	// -------------------------------------------------------------
	static LinkedList<Finance> getFinancesForInvoice(AONContext ctx, Invoice invoice) {
		LinkedList<Finance> finances = new LinkedList<>();
		Optional<RegistryPayMethod> rpm = RegistryPayMethodHandler.getByRegistry(ctx, invoice.getRegistry());
		Optional<RegistryBank> rBank = rpm.flatMap(r -> r.getRegistryBank());
		Date date = invoice.getIssueDate();
		short numberOfPymnts = rpm.map(r -> r.getNumberOfPymnts()).orElse( (short) 1 );
		int daysToFirstPymnt = rpm.map(r -> r.getDaysToFirstPymnt()).orElse( (short) 0 ); 
		int daysBetwenPymnts = rpm.map(r -> r.getDaysBetwenPymnts()).orElse( (short) 0 );
		Optional<PayMethod> payMethod = rpm.flatMap(r -> r.getPayMethod());
		double paymentPrice = AonMathUtils.round(invoice.getTotal() / numberOfPymnts);
		for (int i = 0; i < numberOfPymnts; i++) {
			int days = (i==0?daysToFirstPymnt:daysBetwenPymnts);
			date = (rpm.isEmpty()
				? date 
				: calculatePaymentDate(days, rpm.get().getPymntDays(), date));
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
	
	private static Finance buildFinance(Invoice invoice
			, Date date
			, Optional<PayMethod> payMethod
			, Optional<RegistryBank> rBank
			, double amount) {
		return  new Finance()
			.setDomain(invoice.getDomain())
			.setFinanceType(invoice.isSales() ? FinanceType.COLLECTION : FinanceType.PAYMENT )
			.setRegistry( invoice.getRegistry() )
			.setRegistryName(invoice.getRegistryName())
			.setRegistryDocument(invoice.getRegistryDocument())
			.setRegistryDocumentType(invoice.getRegistryDocumentType())
			.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry())
			.setRegistryAccount( invoice.getRegistryAccount().orElse(null) )
			.setAmount(amount)
			.setConcept(invoice.getDocumentNumber())
			.setInvoice(invoice)
			.setDueDate(date)
			.setPayMethod(payMethod.orElse(null))
			.setBankAccount(rBank.flatMap( rb -> rb.getBankAccount()).orElse(null))
			.setBankAlias(rBank.map( rb -> rb.getAlias()).orElse(null))
			.setBic(rBank.map( rb -> rb.getBic()).orElse(null))
			.setFinanceStatus(FinanceStatus.PENDING)
			.setConfidential(invoice.isConfidential())
			.setScope(invoice.getScope())
		;
	}

}
