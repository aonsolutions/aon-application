package com.esferalia.aon.occam.impl.jooq.dao.invoice.fee;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Prepayment.PREPAYMENT;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rsegment.RSEGMENT;
import static com.esferalia.aon.jooq.tables.Rtax.RTAX;
import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.TaxDetail.TAX_DETAIL;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.console.ConsoleLogger;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.RegistryTax;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PrepaymentCollect;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCalculatorDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonChronometer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FeeBillingDAO {
	
	private static final String LOG_ID = "FEE_BILLING";
	private static final String IS = "INVOICE_SAVE";
	
	private FeeBillingDAO() {
	}
	
	// Cliente y registry vinculado a la cuota.
	protected static final com.esferalia.aon.jooq.tables.Customer FEECUST = com.esferalia.aon.jooq.tables.Customer.CUSTOMER.as("FEECUST") ;
	protected static final com.esferalia.aon.jooq.tables.Registry FEEREGI = com.esferalia.aon.jooq.tables.Registry.REGISTRY.as("FEEREGI") ;

	// Cliente y registry vinculado al grupo de facturacion vinculado a la cuota.
	protected static final com.esferalia.aon.jooq.tables.Customer IGCUST = com.esferalia.aon.jooq.tables.Customer.CUSTOMER.as("IGCUST") ;
	protected static final com.esferalia.aon.jooq.tables.Registry IGREGI = com.esferalia.aon.jooq.tables.Registry.REGISTRY.as("IGREGI") ;

	// Impuesto IVA vinculado al producto de la cuota.
	public static final com.esferalia.aon.jooq.tables.Tax VAT = TAX.as("vat");
	
	// Impuesto IRPF vinculado al producto de la cuota.
	public static final com.esferalia.aon.jooq.tables.Tax IRPF = TAX.as("retention");

	private static SelectOnConditionStep<Record> select(AONContext ctx) {
		return ctx.getDslContext()
			.select( )
			.from( CUSTOMER_FEE )
			
			.innerJoin( FEECUST ).on( FEECUST.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
			.innerJoin( FEEREGI ).on( FEEREGI.ID.eq(FEECUST.REGISTRY))
			
			.leftOuterJoin( INVOICING_GROUP ).on( INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP))
			.leftOuterJoin( IGCUST ).on( IGCUST.REGISTRY.eq(INVOICING_GROUP.CUSTOMER))
			.leftOuterJoin( IGREGI ).on( IGREGI.ID.eq(IGCUST.REGISTRY))
			
			.innerJoin( ITEM ).on( ITEM.ID.eq( CUSTOMER_FEE.ITEM) )
			.innerJoin( PRODUCT ).on( PRODUCT.ID.eq( ITEM.PRODUCT ))
			.leftOuterJoin(VAT).on(VAT.ID.eq(PRODUCT.VAT))
			.leftOuterJoin(IRPF).on(IRPF.ID.eq(PRODUCT.RETENTION))
			
			.leftOuterJoin( PCATEGORY ).on( PCATEGORY.ID.eq( PRODUCT.CATEGORY ))
		;
	}
	
	public static Optional<FeeBilling> getFeeBilling( AONContext ctx, Integer customerFeeId) {
		return select(ctx)
			.where( CUSTOMER_FEE.ID.eq( customerFeeId ) )
			.fetch()
			.stream()
			.map( new FeeBillingFiller() )
			.findFirst();
	}
	
	public static InvoiceProcessOutput invoice(AONContext ctx, FeeBillingParams params, ConsoleLogger logger) {
		logger.subtitle(LOG_ID, "Proceso de facturación de cuotas");
		checkParams( params );
		checkSegments( ctx, params );
		params.setCompany( CompanyDAO.getByDomain( ctx, params.getDomainId() ) );
		Condition condition = getCondition(params);
		AonConfiguration tmpConfig = null;
		if (params.isNotDryRun()) {
			tmpConfig = ConfigurationDAO.getConfiguration( ctx, params.getInvoiceDate() );
		}
		AonConfiguration config = tmpConfig; 
		SelectConditionStep<Record> sentence = select(ctx)
			.where( condition );
		
        Map<String, Object> mvelContext = new HashMap<>();
        mvelContext.put("MONTH", params.getMonth().getName());
        mvelContext.put("YEAR", params.getYear());
        logger.message(LOG_ID, "Mes seleccionado: " + params.getMonth().getName() + " " + params.getYear());
		
        AtomicInteger invoiceNumber = new AtomicInteger( );
        if (params.isDryRun() || params.mustSaveAsProforma() || params.isCommunicable() ) {
        	int minNumber = InvoiceDAO.getMinNumber(ctx, InvoiceType.SALES, params.getInvoiceSeries());
        	if (minNumber >= 0) minNumber = -1;
        	invoiceNumber.set(minNumber );
        } else {
        	int nextNumber = InvoiceDAO.getNextNumber(ctx, InvoiceType.SALES, params.getInvoiceSeries());
        	invoiceNumber.set( nextNumber );
        }
        
        MutableObject<FeeBilling> lastFee = new MutableObject<>( null );
        // Collector que acumula bloques de FeeBilling según breakInvoice
        logger.message(LOG_ID, "Buscando y organizando cuotas para la generaciónde facturas...");
        List<Invoice> fullInvoices = sentence
        	.fetch()
        	.stream()
			.map( new FeeBillingFiller() )			
			.sorted(FEE_COMPARATOR)
			.collect(Collector.of(
				LinkedList<Invoice>::new, // acumulador de facturas
				(invoices, fee) -> {
					Invoice lastInvoice = AonCollectionUtils.stream(invoices).reduce((f, s) -> s).orElse(null);
					if (breakInvoice(fee, lastFee.getValue())) {
						// Nuevo bloque --> nueva factura
						int invNumber = (params.isDryRun() || params.mustSaveAsProforma() || params.isCommunicable() )
							?invoiceNumber.getAndDecrement()
							:invoiceNumber.getAndIncrement();
						lastInvoice = createInvoice(ctx, fee, invNumber, params);
						invoices.add(lastInvoice);
					}
					InvoiceDetail detail = createInvoiceDetail(ctx, lastInvoice, fee, mvelContext, params);
                    lastInvoice.addDetail(detail);
                    lastFee.setValue( fee );
				}
				,(left, right) -> { left.addAll(right); return left; }
			))
		;
        AtomicInteger progress = new AtomicInteger(0);
        int count = fullInvoices.size();
        logger.progress(IS, count, 0);
        AonChronometer cr = new AonChronometer();
        cr.start();
        AonCollectionUtils.stream(fullInvoices)
	    	.forEach(i -> {
	        	// Se calculan los datos de la factura
	    		InvoiceCalculatorDAO.calculate( ctx, i);
	    		FinanceDAO.getFinancesForInvoiceStream( ctx, i).forEach(i::addFinance);  
	    		// En su caso, Se guardan las facturas y sus vencimientos
	            if (params.isNotDryRun()) {
	            	InvoiceDAO.saveInvoiceAndFinances(ctx, config, i, false);
	            	logger.progress(IS, count, progress.incrementAndGet()
            			, AonMathUtils.round(cr.getCurrentSeconds(),4)  
            			+ " Grabando factura: " + FinanceUtil.getDocumentNumber(i) + " para " + i.getRegistryName() );
            	} else {
            		logger.progress(IS, count, progress.incrementAndGet()
        				, AonMathUtils.round(cr.getCurrentSeconds(),4)  
            			+  "Calculando factura: " + FinanceUtil.getDocumentNumber(i) + " para " + i.getRegistryName() );
            	}
	    	});
        cr.stop();
        logger.message(LOG_ID, "Recopilando información grabada" );
        InvoiceProcessOutput ipo = AonCollectionUtils.stream(fullInvoices)
    		.collect(InvoiceProcessOutput.collector());
    	logger.message(LOG_ID, "Fin del proceso de facturación" );
    	return ipo;
	}

	public static void updateSource(AONContext ctx, FeeBilling fee, InvoiceDetail detail) {
		if (fee.getPeriod() == BillingPeriod.NO_PERIOD) {
			if (fee.isPrepayment()) {
				ctx.getDslContext()
					.select()
					.from(PREPAYMENT)
					.where(PREPAYMENT.COLLECT.eq(PrepaymentCollect.FEE.value()))
					.and(PREPAYMENT.COLLECT_ID.eq(fee.getId()))
					.fetch()
					.stream()
					.map( r -> r.getValue( PREPAYMENT.ID ))
					.forEach( id ->
						ctx.getDslContext()
							.update( PREPAYMENT )
							.set(PREPAYMENT.COLLECT, PrepaymentCollect.INVOICE_DETAIL.value())
							.set(PREPAYMENT.COLLECT_ID, detail.getId())
							.where( PREPAYMENT.ID.eq(id) )
					)
				;
			}
			deleteCustomerFee(ctx, fee);
		} else {
			int months = fee.getPeriod().getValue();
			Date billingDate = fee.getBillingDate();
			LocalDate localDate = (billingDate instanceof java.sql.Date sqlDate)
				?sqlDate.toLocalDate().plusMonths(months)
				:billingDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(months)
			;
			Date newBillingDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			fee.setBillingDate(newBillingDate);
			if (fee.getFinalDate() != null && fee.getBillingDate().after(fee.getFinalDate())) {
				deleteCustomerFee(ctx, fee);
			} else {
				updateCustomerFee(ctx, fee);
			}
		}
	}

	private static void updateCustomerFee(AONContext ctx, FeeBilling fee) {
		int count = ctx.getDslContext()
			.update( CUSTOMER_FEE )
			.set(CUSTOMER_FEE.BILLING_DATE, AonDateUtils.toSql(fee.getBillingDate()) )
			.where( CUSTOMER_FEE.ID.eq(fee.getId()) )
			.execute();
		if (count < 1) {
			throw new AonCoreException("No se ha podido actualizar la fecha de facturación de la cuota " + fee.getId());
		}
	}

	private static void deleteCustomerFee(AONContext ctx, FeeBilling fee) {
		int count = ctx.getDslContext()
			.delete( CUSTOMER_FEE )
			.where( CUSTOMER_FEE.ID.eq(fee.getId()) )
			.execute();
		if (count < 1) {
			throw new AonCoreException("No se ha podido eliminar la cuota " + fee.getId());
		}
	}

	// **********************************************************************************
	// ************************************************************* [INVOICING] ********
	// **********************************************************************************
	
	private static Invoice createInvoice(AONContext ctx, FeeBilling fee, int number, FeeBillingParams params) {
		CustomerFull cus = CustomerDAO.getFull( ctx, fee.getInvoicingCustomer().getId() );
		Integer mainAddressId = (cus.getMainAddress() != null)? cus.getMainAddress().getId() : null;
		EnterpriseActivity activity = ( params.getInvoiceActivity()!= null )
			?new EnterpriseActivity().setId(params.getInvoiceActivity()) 
			: null;
		return new Invoice()
			.setDomain(params.getDomainId())
			.setType(InvoiceType.SALES)
			.setActivity(activity)
			.setIssueDate(params.getInvoiceDate())
			.setTaxDate(params.getInvoiceDate())
			.setScope(cus.getRegistry().getScope())
			.setSecurityLevel(params.getSecurityLevel())
			.setProject(fee.getProject().orElse(null))
			.setSeries(params.getInvoiceSeries())
			.setNumber(number)
			.setRegistry(cus.getId())
			.setRegistryDocument(cus.getRegistry().getDocument())
			.setRegistryDocumentType(cus.getRegistry().getDocumentType())
			.setRegistryDocumentCountry(cus.getRegistry().getDocumentCountry())
			.setRegistryName(cus.getRegistry().getName())
			.setRegistryAddress(mainAddressId)
			.setRecorded( false )
			.setComments(params.getInvoiceComments())
			.setSeller(fee.getSeller())
			.setTransaction(cus.getRegistry().getTransaction())
			.setSurcharge(cus.getRegistry().isSurcharge())
			.setWithholding(params.getCompany().isWithholding() && cus.getRegistry().isWithholding())
		;
	}
	
	private static InvoiceDetail createInvoiceDetail(AONContext ctx, Invoice invoice, FeeBilling fee, Map<String, Object> mvelContext, FeeBillingParams params) {
        short line = (short) (invoice.detailsSize() + 1);
        InvoiceDetail detail = new InvoiceDetail()
    		.setDomain(params.getDomainId())
    		.setLine(line)
			.setProject(fee.getProject().orElse(null))
			.setItem(fee.getItem())	
			.setDescription( composeDescription(fee, mvelContext) )
			.setSource( InvoiceSource.FEE )
			.setSourceId( fee.getId() )
			.setQuantity(fee.getQuantity())
			.setPrice(AonMathUtils.round(fee.getPrice() * calculateCorrectionFactor(fee, params), 4))
			.setDiscountExpression(fee.getDiscountExpression())
			.setSeller(new Seller().setId(fee.getSeller()))
			.setWorkplace(new Workplace().setId(fee.getWorkplace()))
			.setPrepayment(fee.isPrepayment())
		;
        InvoiceCalculatorDAO.calculate(ctx, detail);
        return addDetail(ctx, invoice, detail);
	}

	private static String composeDescription(FeeBilling fee, Map<String, Object> mvelContext) {
		String description = fee.getDescription();
		if (isMVELTemplate(description)) {
		    description = (String) TemplateRuntime.eval(description, mvelContext);
		}
        if ( AonNumberUtils.notEquals(fee.getInvoicingCustomer().getId(), fee.getCustomer().getId())) {
        	description += " - " + fee.getInvoicingCustomer().getName();
		}
		return AonStringUtils.abbreviate(description, INVOICE_DETAIL.DESCRIPTION.getDataType().length());	// prevent DB overflow 
	}

	private static boolean isMVELTemplate(String description) {
		return description != null && 
			(description.contains("${") || description.contains("@{"));
	}

	private static double calculateCorrectionFactor(FeeBilling fee, FeeBillingParams params) {
		if (fee.getPeriod() == BillingPeriod.NO_PERIOD) return 1;
		Date fromDate = AonDateUtils.truncate(new Date(), Calendar.YEAR);
		fromDate = AonDateUtils.setYears(fromDate, params.getYear());
		fromDate = AonDateUtils.setMonths(fromDate, params.getMonth().ordinal());
		Date toDate = AonDateUtils.addDays(AonDateUtils.addMonths(fromDate, fee.getPeriod().getValue()), -1);
		Date iniFee = (fee.getInitialDate().before(fromDate)) ? fromDate : fee.getInitialDate();
		Date endFee = (fee.getFinalDate() == null || fee.getFinalDate().after(toDate)) ? toDate : fee.getFinalDate();
		return (double) AonDateUtils.getDaysBetweenDates(iniFee, endFee) / (double) AonDateUtils.getDaysBetweenDates(fromDate, toDate);
	}

	private static boolean breakInvoice(FeeBilling fee, FeeBilling previousFee) {
		return previousFee == null
	        || isDifferentInvoicingCustomer(fee, previousFee)
	        || isDifferentCustomerInNonGroupedInvoicingGroup(fee, previousFee)
	        || isDifferentProjectInNonGroupedCustomer(fee, previousFee);
	}
	
	private static boolean isDifferentInvoicingCustomer(FeeBilling fee, FeeBilling previousFee) {
	    return AonNumberUtils.notEquals(fee.getInvoicingCustomer().getId(), previousFee.getInvoicingCustomer().getId());
	}

	private static boolean isDifferentCustomerInNonGroupedInvoicingGroup(FeeBilling fee, FeeBilling previousFee) {
	    return fee.getInvoicingGroup()
              .map(ig -> !ig.isCustomerGrouped())
              .orElse(false)
           && AonNumberUtils.notEquals(fee.getCustomer().getId(), previousFee.getCustomer().getId());
	}

	private static boolean isDifferentProjectInNonGroupedCustomer(FeeBilling fee, FeeBilling previousFee) {
	    return !fee.getInvoicingCustomer().isProjectGrouped()
	        && AonNumberUtils.notEquals(fee.getProject().orElse(null), previousFee.getProject().orElse(null));
	}	
	
	// **********************************************************************************
	// *************************************************************** [PRIVATE] ********
	// **********************************************************************************
	private static void checkParams(FeeBillingParams params) {
		if (params == null) throw new AonCoreException( "No se han indicado condiciones de facturación");
		if (params.getDomainId() == null) throw new AonCoreException( "No se han indicado el dominio para la facturación");
		if (params.getYear() == null) throw new AonCoreException( "No se ha indicado el año a facturar");
		if (params.getMonth() == null) throw new AonCoreException( "No se ha indicado el mes a facturar");
		if (params.isNotDryRun()) {
			if (params.getInvoiceDate() == null) throw new AonCoreException( "No se ha indicado la fecha de las facturas");
			if (AonStringUtils.isBlank(params.getInvoiceSeries())) throw new AonCoreException( "No se ha indicado la serie de las facturas");
		} else {
			if (params.getInvoiceDate() == null) params.setInvoiceDate( new Date() );
		}
	}
	
	private static void checkSegments(AONContext ctx, FeeBillingParams params) {
		if (AonCollectionUtils.isNotEmpty( params.getSegments() ) ) {
			String msg = "[Cliente: {0} - Segmento: {1} - {2} veces]";
			AggregateFunction<Integer> count = DSL.count();
			String message = ctx.getDslContext()
				.select( REGISTRY.NAME, SEGMENT.NAME, count )
				.from( RSEGMENT )
				.innerJoin( REGISTRY ).on( REGISTRY.ID.eq(RSEGMENT.REGISTRY) )
				.innerJoin( CUSTOMER ).on( CUSTOMER.REGISTRY.eq(REGISTRY.ID ) )
				.innerJoin( SEGMENT ).on( SEGMENT.ID.eq(RSEGMENT.SEGMENT ) )
				.where( RSEGMENT.DOMAIN.eq(params.getDomainId()) )
				.groupBy( REGISTRY.NAME,SEGMENT.NAME )
				.having(count.gt(1))
				.fetch()
				.stream()
				.map( r -> MessageFormat.format(msg,r.getValue(REGISTRY.NAME),r.getValue(SEGMENT.NAME),r.getValue(count)))
				.collect(Collectors.joining(" "))
			;
			if (AonStringUtils.isNotEmpty( message)) {
				StringBuilder buf = new StringBuilder();
				buf.append("Existen clientes con segmentos definidos dos veces. Corrija la situación para poder continuar.");
				buf.append(message);
				throw new AonCoreException(buf.toString());
			}
		}
	}

	private static Condition getCondition(FeeBillingParams params) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(params.getYear(), params.getMonth().value(), 1, 0, 0, 0);
		java.sql.Date from = AonDateUtils.toSql(calendar.getTime());
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.SECOND, -1);
		java.sql.Date to = AonDateUtils.toSql(calendar.getTime());
		
		Condition c = CUSTOMER_FEE.DOMAIN.eq(params.getDomainId())
			.and(FEECUST.STATUS.eq( RegistryStatus.ACTIVE.value() ))
			.and(CUSTOMER_FEE.SECURITY_LEVEL.eq( params.getSecurityLevel().value() ))
			.and(CUSTOMER_FEE.BILLING_DATE.between(from, to) )
			.and(CUSTOMER_FEE.INITIAL_DATE.le(to))
			.and(CUSTOMER_FEE.FINAL_DATE.ge(from).or(CUSTOMER_FEE.FINAL_DATE.isNull()))
		;
		
		if (params.getInvoicingGroup() != null) {
			c = c.and(CUSTOMER_FEE.INVOICING_GROUP.eq(params.getInvoicingGroup()));
		}
		if (params.getCustomer() != null ) {
			c = c.and(CUSTOMER_FEE.CUSTOMER.eq(params.getCustomer()));
		} else if (AonCollectionUtils.isNotEmpty(params.getScopes()) ) {
			c = c.and(FEECUST.SCOPE.in(params.getScopes()));
		}
		if (params.getItem() != null) {
			c = c.and(ITEM.ID.eq( params.getItem() ));
		}
		if (params.getProductCategory() != null ) {
			c = c.and(PCATEGORY.ID.eq( params.getProductCategory() ));
		}
		if (params.getWorkplace() != null) {
			c = c.and(CUSTOMER_FEE.WORKPLACE.eq( params.getWorkplace() ));
		}
		if (params.getPeriod() != null ) {
			c = c.and(CUSTOMER_FEE.PERIOD.eq(params.getPeriod().value()));
		}
		if (AonCollectionUtils.isNotEmpty(params.getSegments())) {
			c = c.and(RSEGMENT.SEGMENT.in(params.getSegments()));
		}
		return c;
	}

	private static final Comparator<FeeBilling> FEE_COMPARATOR =
	    Comparator
	        // 1 Cliente facturante (nombre y luego ID)
	        .comparing( (FeeBilling f) -> f.getInvoicingCustomer().getName(), Comparator.nullsLast(String::compareTo))
	        .thenComparing(f -> f.getInvoicingCustomer().getId(), Comparator.nullsLast(Comparator.naturalOrder()))
	        // 2 Grupo de facturación (ID)
	        .thenComparing(f -> f.getInvoicingGroup().map( ig -> ig.getId()).orElse(null),Comparator.nullsLast(Comparator.naturalOrder()))
	        // 3 Cliente principal primero
	        .thenComparing(f -> isParent(f) ? 0 : 1)
	        // 4 Cliente final (nombre y luego ID)
	        .thenComparing(f -> f.getCustomer().getName(), Comparator.nullsLast(String::compareTo))
	        .thenComparing(f -> f.getCustomer().getId(), Comparator.nullsLast(Comparator.naturalOrder()))
	        // 5 Proyecto (solo si no está agrupado)
	        .thenComparing(f -> f.getProject().filter( p -> !f.getInvoicingCustomer().isProjectGrouped()).orElse( null ), Comparator.nullsLast(Comparator.naturalOrder()))
	        // 6 Línea de factura (último criterio)
	        .thenComparing(FeeBilling::getLine, Comparator.nullsLast(Comparator.naturalOrder()))
        ;

	private static boolean isParent(FeeBilling f) {
		Customer c1 = f.getInvoicingCustomer();
		Customer c2 = f.getCustomer();
	    return c1.getId().equals(c2.getId());
	}	

	// **********************************************************************************
	// *************************************************************** [FILLERS] ********
	// **********************************************************************************
	private static class FeeBillingCustomerFiller extends Filler  implements Function<Record, Customer> {

		@Override
		public Customer apply(Record r) {
			return build(r, CUSTOMER,REGISTRY);
		}
		
		private static Customer build(Record r
			, com.esferalia.aon.jooq.tables.Customer customer
			, com.esferalia.aon.jooq.tables.Registry registry) {
			return new Customer()
				.copy(RegistryFiller.build(r, registry))
				.setAccount(getValue(r, customer.ACCOUNT))
				.setCreationDate(getValue(r, customer.CREATION_DATE))
				.setCreationUser(getValue(r, customer.CREATION_USER))
				.setDeliveryGrouped(getBoolean(r, customer.DELIVERY_GROUPED))
				.setDeliveryValuated(getBoolean(r, customer.DELIVERY_VALUATED))
				.setEInvoice(getBoolean(r, customer.E_INVOICE))
				.setInvoicingGroup(getValue(r, customer.INVOICING_GROUP))
				.setModificationDate(getValue(r, customer.MODIFICATION_DATE))
				.setModificationUser(getValue(r, customer.MODIFICATION_USER))
				.setProjectGrouped(getBoolean(r, customer.PROJECT_GROUPED))
				.setScope(new Scope().setId(getValue(r, customer.SCOPE)))
				.setSurcharge(getBoolean(r, customer.SURCHARGE))
				.setTariff(getValue(r, customer.TARIFF))
				.setTransaction(InvoiceTransactionType.safeValueOf(getValue(r, customer.TRANSACTION)))
				.setWithholding(getBoolean(r, customer.WITHHOLDING))
				.setStatus(RegistryStatus.safeValueOf(getValue(r, customer.STATUS)))
			;
		}
	}
	
	private static class FeeBillingInvoicingGroupFiller extends Filler implements Function<Record, FeeBillingInvoicingGroup> {
		@Override
		public FeeBillingInvoicingGroup apply(Record r) {
			return build(r);
		}
		
		private static FeeBillingInvoicingGroup build(Record r) {
			if (isNull( r, INVOICING_GROUP.ID)) return null;
			return new FeeBillingInvoicingGroup()
				.setId( getValue(r, INVOICING_GROUP.ID))
				.setDomain( getValue(r, INVOICING_GROUP.DOMAIN))
				.setDescription( getValue(r, INVOICING_GROUP.DESCRIPTION))
				.setCustomer( FeeBillingCustomerFiller.build(r, IGCUST, IGREGI) )
				.setCustomerGrouped(getBoolean(r, INVOICING_GROUP.CUSTOMER_GROUPED))
			;
		}
	}
	
	private static class FeeBillingItemFiller extends Filler implements Function<Record, Item> {
		
		@Override
		public Item apply(Record r) {
			return build(r);
		}
		
		public static Item build(Record r) {
			return new Item()
				.setId(getValue(r, ITEM.ID))
				.setDomain(new Domain().setId(getValue(r, ITEM.DOMAIN)))
				.setProduct( FeeBillingProductFiller.build(r) )
				.setDetail(getValue(r, ITEM.DETAIL))
				.setDetail2(getValue(r, ITEM.DETAIL2))
				.setDetail3(getValue(r, ITEM.DETAIL3))
				.setDescription(getValue(r, ITEM.DESCRIPTION))
				.setSerialNumber(getValue(r, ITEM.SERIAL_NUMBER))
				.setSerialDate(getValue(r, ITEM.SERIAL_DATE))
				.setExpireDate(getValue(r, ITEM.EXPIRE_DATE))
				.setPrice(getDouble(r, ITEM.PRICE))
				.setStatus(ProductStatus.safeValueOf(getValue(r, ITEM.STATUS)))
				.setExpensesPercent(getDouble(r, ITEM.EXPENSES_PERCENT))
				.setExpensesFixed(getDouble(r, ITEM.EXPENSES_FIXED))
				.setProfitPercent(getDouble(r, ITEM.PROFIT_PERCENT))
				.setPurchasePrice(getDouble(r, ITEM.PURCHASE_PRICE))				
				.setInternet(getBoolean(r, ITEM.INTERNET))
				.setBarcode(getValue(r, ITEM.BARCODE))
				.setCreationUser(getValue(r, ITEM.CREATION_USER))
				.setCreationDate(getValue(r, ITEM.CREATION_DATE))
				.setModificationUser(getValue(r, ITEM.MODIFICATION_USER))
				.setModificationDate(getValue(r, ITEM.MODIFICATION_DATE));
		}
	}
	
	private static class FeeBillingProductFiller extends Filler implements Function<Record, Product> {
		@Override
		public Product apply(Record r) {
			return build(r);			
		}
		
		public static Product build(Record r) {
			return new Product()
				.setId(getValue(r, PRODUCT.ID))
				.setName(getValue(r, PRODUCT.NAME))
				.setDomain(new Domain().setId(getValue(r, PRODUCT.DOMAIN)))
				.setBrand(new Brand().setId(getValue(r, PRODUCT.BRAND)))
				.setCategory(new ProductCategory().setId(getValue(r, PRODUCT.CATEGORY)))
				.setCode(getValue(r, PRODUCT.CODE))
				.setComposition(getBoolean(r, PRODUCT.COMPOSITION))
				.setCompositionPrice(getBoolean(r, PRODUCT.COMPOSITION_PRICE))
				.setInventoriable(getBoolean(r, PRODUCT.INVENTORIABLE))
				.setKind(ProductKind.safeValueOf(getValue(r, PRODUCT.KIND)))
				.setLotable(getBoolean(r, PRODUCT.LOTABLE))
				.setManufactured(getBoolean(r, PRODUCT.MANUFACTURED))
				.setPackaged(getBoolean(r, PRODUCT.PACKAGED))
				.setPurchaseAccount(new Account().setId(getValue(r, PRODUCT.PURCHASE_ACCOUNT)))
				.setRetention(FeeBillingTaxFiller.build(r, IRPF))
				.setSalesAccount(new Account().setId(getValue(r, PRODUCT.SALES_ACCOUNT)))
				.setSerializable(getBoolean(r, PRODUCT.SERIALIZABLE))
				.setStatus(ProductStatus.safeValueOf(getValue(r, PRODUCT.STATUS)))
				.setType(ProductType.safeValueOf(getValue(r, PRODUCT.TYPE)))
				.setVat(FeeBillingTaxFiller.build(r, VAT))
				.setPerishable(getBoolean(r, PRODUCT.PERISHABLE))
				.setDaysToExpire(getValue(r, PRODUCT.DAYS_TO_EXPIRE))
				.setCreationDate(getValue(r, PRODUCT.CREATION_DATE))
				.setCreationUser(getValue(r, PRODUCT.CREATION_USER))
				.setModificationDate(getValue(r, PRODUCT.MODIFICATION_DATE))
				.setModificationUser(getValue(r, PRODUCT.MODIFICATION_USER))
			;
		}
	}
	
	private static class FeeBillingTaxFiller extends Filler implements Function<Record, Tax> {

		@Override
		public Tax apply(Record r) {
			return build(r, TAX);
		}
		
		public static Tax build(Record r, com.esferalia.aon.jooq.tables.Tax tax) {
			if ( isNull(r, tax.ID) ) return null;
			return new Tax()
				.setId(getValue(r, tax.ID))
				.setDomain(getValue(r,tax.DOMAIN))
				.setName(getValue(r,tax.NAME))
				.setType(TaxType.safeValueOf( getValue(r,tax.TAX_TYPE)))
				.setPercentage(getValue(r,tax.PERCENTAGE))
				.setSurcharge(getValue(r,tax.SURCHARGE))
				.setStartDate(getValue(r,tax.START_DATE))
				.setVatDeductionType( VatDeductionType.safeValueOf( getValue(r,tax.VAT_DEDUCTION_TYPE)))
				.setWithholdingType(WithholdingType.safeValueOf( getValue(r,tax.WITHHOLDING_TYPE)))
				.setPurchaseAccount(new Account().setId(getValue(r,tax.PURCHASE_ACCOUNT)))
				.setSalesAccount(new Account().setId(getValue(r,tax.SALES_ACCOUNT)))
				.setCreationUser(getValue(r,tax.CREATION_USER))
				.setCreationDate(getValue(r,tax.CREATION_DATE))
				.setModificationUser(getValue(r,tax.MODIFICATION_USER))
				.setModificationDate(getValue(r,tax.MODIFICATION_DATE));
		}
	}
	
	
	private static class FeeBillingFiller extends Filler implements Function<Record, FeeBilling> {
		@Override
		public FeeBilling apply(Record r) {
			return new FeeBilling()
				.setId( getValue(r, CUSTOMER_FEE.ID))
				.setLine( getValue(r, CUSTOMER_FEE.LINE))
				.setInvoicingGroup( FeeBillingInvoicingGroupFiller.build(r) )
				.setCustomer( FeeBillingCustomerFiller.build(r, FEECUST, FEEREGI) )
				.setProject( getValue(r, CUSTOMER_FEE.PROJECT ))
				.setSeller( getValue(r, CUSTOMER_FEE.SELLER))
				.setItem( FeeBillingItemFiller.build(r) )
				.setDescription( getValue(r, CUSTOMER_FEE.DESCRIPTION))
				.setQuantity( getValue(r, CUSTOMER_FEE.QUANTITY))
				.setPrice( getValue(r, CUSTOMER_FEE.PRICE))
				.setDiscountExpression( new DiscountExpression(getValue(r, CUSTOMER_FEE.DISCOUNT_EXPR)))
				.setPeriod( BillingPeriod.safeValueOf( getValue(r, CUSTOMER_FEE.PERIOD)).orElse( null ) )
				.setInitialDate(  getValue(r, CUSTOMER_FEE.INITIAL_DATE))
				.setFinalDate( getValue(r, CUSTOMER_FEE.FINAL_DATE))
				.setBillingDate( getValue(r, CUSTOMER_FEE.BILLING_DATE))
				.setWorkplace( getValue(r, CUSTOMER_FEE.WORKPLACE))
				.setSecurityLevel( SecurityLevel.safeValueOf( getValue(r, CUSTOMER_FEE.SECURITY_LEVEL)) )
			;
		}
	}

	// *********************************************************************
	// ***************************************************** [TAXES] *******
	// *********************************************************************
	public static InvoiceTax getTax(AONContext ctx, Invoice invoice, InvoiceDetail detail, Tax tax) {
		return new InvoiceTax()
			.setTaxType(tax.getType())
			.setBase(detail.getTaxableBase())
			.setPercentage(
				getTaxPercentage(ctx, invoice.getRegistry(), tax, invoice.getIssueDate(), false))
			.setSurcharge( 
				invoice.isSurcharge() 
					? getTaxPercentage(ctx, invoice.getRegistry(), tax, invoice.getIssueDate(), true) 
					: 0.0)
			.setDeductiblePercent(100.0)
		;
	}
	
	private static double getTaxPercentage(AONContext ctx, Integer registryId, Tax tax, Date date, boolean surcharge) {
		// Si el cliente tiene redefinido el impuesto, se devuelve.
		Optional<RegistryTax> rtax = getRegistryTax(ctx, registryId, tax, date);
		if (rtax.isPresent() ) return rtax.get().getPercentage(surcharge);
		
		// Si se modificó el TAX y se generó tax_detail, se busca el tramo.
		if (AonDateUtils.isBefore(date, tax.getStartDate())) {
			Double ret = ctx.getDslContext()
				.select()
				.from( TAX_DETAIL )
				.where( TAX_DETAIL.TAX.eq( tax.getId() ) )
				.and( TAX_DETAIL.START_DATE.le( AonDateUtils.toSql(date)))
				.and( TAX_DETAIL.END_DATE.ge( AonDateUtils.toSql(date)))
				.fetch()
				.stream()
				.map( r -> (!surcharge) ? r.getValue(TAX_DETAIL.VALUE) : r.getValue(TAX_DETAIL.SURCHARGE))
				.findFirst()
				.orElse(null)
			;
			if ( ret != null) return ret;
		}
		// Se devuelve el porcentage de Tax.
		return tax.getPercentage(surcharge);
	}

	private static Optional<RegistryTax> getRegistryTax(AONContext ctx, Integer registryId, Tax tax, Date date) {
		java.sql.Date sqlDate = AonDateUtils.toSql(date);
		return ctx.getDslContext()
			.select() 
			.from( RTAX )
			.innerJoin( TAX ).on( TAX.ID.eq(RTAX.TAX))
			.where( RTAX.REGISTRY.eq(registryId) )
			.and( RTAX.TAX.eq(tax.getId()) )
			.and( RTAX.START_DATE.le(sqlDate) )
			.and( RTAX.END_DATE.isNull().or( RTAX.END_DATE.ge( sqlDate )))
			.fetch()
			.stream()
			.map( new RegistryTaxFiller() )
			.findFirst()
		;
	}
	
	private static class RegistryTaxFiller extends Filler implements Function<Record, RegistryTax> {
		@Override
		public RegistryTax apply(Record r) {
			return new RegistryTax()
				.setId( getValue(r, RTAX.ID))
				.setDomain( getValue(r, RTAX.DOMAIN))
				.setRegistry( getValue(r, RTAX.REGISTRY))
				.setTax(FeeBillingTaxFiller.build(r, TAX))
				.setPercentage( getValue(r, RTAX.PERCENTAGE))
				.setSurcharge( getValue(r, RTAX.SURCHARGE))
				.setStartDate( getValue(r, RTAX.START_DATE))
				.setEndDate( getValue(r, RTAX.END_DATE))
			;
		}
	}

	public static InvoiceDetail addDetail(AONContext ctx, Invoice invoice, InvoiceDetail detail) {
		if ( invoice == null) throw new AonCoreException("La factura es un dato obligatorio");
		if ( detail == null) throw new AonCoreException("El detalle de factura es un dato obligatorio");
		detail.optItem()
			.map( Item::getProduct )
			.filter( p -> p != null )
			.ifPresent(
				p -> {
					if (p.getVat() != null && (invoice.isOutputVatEnabled() || invoice.isCanCeuMel())) {
						detail.addTax( getTax(ctx, invoice, detail, p.getVat()));
					}
					if (p.getRetention() != null && canApplyRetention(invoice)) {
			        	detail.addTax( getTax(ctx, invoice, detail, p.getRetention()));
			        }
				}
			);
        return detail;
	}

	private static boolean canApplyRetention(Invoice invoice) {
		return  (invoice.isWithholding()		// Marcada como retencion 
			  || invoice.isWithholdingFarmer())	// o como retencion agricola
			&& !invoice.isExtracommunity() 		// y no extracomunitaria
			&& !invoice.isIntracommunity()		// y no intracomunitaria
			&& !invoice.isCanCeuMel()			// y no canCeuMel
		;
	}
	
	public static FeeBilling invoiceDetailRemoved(AONContext ctx, Invoice invoice, InvoiceDetail invoiceDetail) {
		Date feeDate = obtainFeeDate(invoiceDetail);
		short line = obtainMaxLine(ctx, invoice.getRegistry());
		FeeBilling savedFee = insert(ctx, 
			new FeeBilling()
				.setDomain(invoiceDetail.getDomain() )
				.setCustomer(new Customer().setId(invoice.getRegistry()))
				.setLine(++line)
				.setItem(new Item().setId(invoiceDetail.getItem().getId()))
				.setDescription(invoiceDetail.getDescription())
				.setQuantity(invoiceDetail.getQuantity())
				.setPrice(invoiceDetail.getPrice())
				.setDiscountExpression(invoiceDetail.getDiscountExpression())
				.setInitialDate(feeDate)
				.setFinalDate(feeDate)
				.setBillingDate(feeDate)
				.setPeriod(BillingPeriod.NO_PERIOD)
				.setConfidential(invoiceDetail.getInvoice().isConfidential())
				.setSeller(invoiceDetail.getSeller() != null ? invoiceDetail.getSeller().getId() : null)
				.setWorkplace( invoiceDetail.getWorkplace() != null ? invoiceDetail.getWorkplace().getId() : null)
		);
		if (invoiceDetail.isPrepayment()) {
			ctx.getDslContext()
				.select()
				.from(PREPAYMENT)
				.where(PREPAYMENT.COLLECT.eq(PrepaymentCollect.INVOICE_DETAIL.value()))
				.and(PREPAYMENT.COLLECT_ID.eq(invoiceDetail.getId()))
				.fetch()
				.stream()
				.map( r -> r.getValue( PREPAYMENT.ID ))
				.forEach( id ->
					ctx.getDslContext()
						.update( PREPAYMENT )
						.set(PREPAYMENT.CUSTOMER, invoice.getRegistry())
						.set(PREPAYMENT.COLLECT, PrepaymentCollect.FEE.value())
						.set(PREPAYMENT.COLLECT_ID, savedFee.getId())
						.where( PREPAYMENT.ID.eq(id) )
				);
		}
		return savedFee;
	}

	private static FeeBilling insert(AONContext ctx, FeeBilling fee) {
		Integer id = ctx.getDslContext()
			.insertInto(CUSTOMER_FEE)
			.set(CUSTOMER_FEE.DOMAIN, fee.getDomain())  
			.set(CUSTOMER_FEE.PROJECT, fee.getProject().orElse(null)) 
			.set(CUSTOMER_FEE.CUSTOMER, fee.getCustomer().getId())
			.set(CUSTOMER_FEE.LINE, fee.getLine())
			.set(CUSTOMER_FEE.ITEM, fee.getItem().getId())
			.set(CUSTOMER_FEE.DESCRIPTION, fee.getDescription()) 
			.set(CUSTOMER_FEE.QUANTITY, fee.getQuantity()) 
			.set(CUSTOMER_FEE.PRICE, fee.getPrice())
			.set(CUSTOMER_FEE.DISCOUNT_EXPR, fee.getDiscountExpression().getDiscountExpr()) 
			.set(CUSTOMER_FEE.INITIAL_DATE,  AonDateUtils.toSql(fee.getInitialDate()))
			.set(CUSTOMER_FEE.FINAL_DATE,  AonDateUtils.toSql(fee.getFinalDate()))
			.set(CUSTOMER_FEE.BILLING_DATE, AonDateUtils.toSql(fee.getBillingDate()))
			.set(CUSTOMER_FEE.PERIOD, fee.getPeriod().value())
			.set(CUSTOMER_FEE.SECURITY_LEVEL, fee.getSecurityLevel().value())  
			.set(CUSTOMER_FEE.INVOICING_GROUP,  fee.getInvoicingGroup().map(ig -> ig.getId()).orElse(null))
			.set(CUSTOMER_FEE.SELLER, fee.getSeller()) 
			.set(CUSTOMER_FEE.WORKPLACE, fee.getWorkplace())
			.returning(CUSTOMER_FEE.ID)
			.fetchOne()
			.getValue(CUSTOMER_FEE.ID);
		return getFeeBilling(ctx, id)
			.orElseThrow(() -> new AonCoreException("No se ha podido recuperar la cuota recién creada"));
	}

	private static Date obtainFeeDate(InvoiceDetail invoiceDetail) {
		if (invoiceDetail.getSourceId() != null) {
			int year = invoiceDetail.getSourceId() / 100;
			int month = invoiceDetail.getSourceId() - (year * 100);
			AonDateUtils.getDate(year, (month-1), 1);
		}
		return invoiceDetail.getInvoice().getIssueDate();
	}

	private static	short obtainMaxLine(AONContext ctx, Integer custoner ) {
		return ctx.getDslContext()
			.select(DSL.max(CUSTOMER_FEE.LINE))
			.from(CUSTOMER_FEE)
			.where(CUSTOMER_FEE.CUSTOMER.eq(custoner))
			.fetch()
			.stream()
			.map( r -> r.getValue( DSL.max(CUSTOMER_FEE.LINE) ))
			.findFirst()
			.orElse((short) 0);
	}
	
}