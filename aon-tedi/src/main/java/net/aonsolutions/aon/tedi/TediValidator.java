package net.aonsolutions.aon.tedi;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TediValidator {
	private TediValidator() {
	}

	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	private static final Consumer<ValidationContext> EMPTY_DOMAIN = ctx -> {
		if (ctx.getInvoice().getDomain() == null || ctx.getInvoice().getDomain() == 0) {
			ctx.add( InvoiceErrorMessages.C001.err(InvoiceErrorKey.DOMAIN) );
		}
	};
	
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	private static final Consumer<ValidationContext> EMPTY_INVOICE_SCOPE = ctx -> {
		if (ctx.getInvoice().getRegistry() != null &&
			(ctx.getInvoice().getScope() == null || ctx.getInvoice().getScope().getId() == null)) {
			ctx.add( InvoiceErrorMessages.C001.err(InvoiceErrorKey.SCOPE));
		}
	};
	
	/**
	 * La serie no debe superar caracters definido en BD.
	 */
	private static final Consumer<ValidationContext> OVERFLOW_SERIES = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getSeries()) 
		 && willOverflow(INVOICE.SERIES, ctx.getInvoice().getSeries())) {
			ctx.add( InvoiceErrorMessages.C002.wrn(InvoiceErrorKey.SERIES, InvoiceErrorKey.SERIES.getDescription(),INVOICE.SERIES.getDataType().length()));
		}
	};
	
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	private static final Consumer<ValidationContext> EMPTY_REFERENCE_CODE = ctx -> {
		if (!ctx.getInvoice().isSales() && AonStringUtils.isBlank(ctx.getInvoice().getReferenceCode())) {
			ctx.add( InvoiceErrorMessages.C001.wrn(InvoiceErrorKey.REFERENCE_CODE));
		}
	};
	
	/**
	 * Si la factura es de ventas, debe tener número de factura.
	 */
	private static final Consumer<ValidationContext> EMPTY_SALES_NUMBER = ctx -> {
		if (ctx.getInvoice().isSales() 
			&& ctx.getInvoice().getNumber() == 0 
			&& AonStringUtils.isBlank(ctx.getInvoice().getReferenceCode())) {
			ctx.add( InvoiceErrorMessages.C001.wrn(InvoiceErrorKey.NUMBER));
		}
	};
	

	/**
	 * El codigo de referencia debe superar caracters definido en BD.
	 */
	private static final Consumer<ValidationContext> OVERFLOW_REFERENCE_CODE = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getReferenceCode()) 
		 && willOverflow(INVOICE.REFERENCE_CODE, ctx.getInvoice().getReferenceCode())) {
			ctx.add( InvoiceErrorMessages.C002.err(InvoiceErrorKey.REFERENCE_CODE, InvoiceErrorKey.REFERENCE_CODE.getDescription(), INVOICE.REFERENCE_CODE.getDataType().length()));
		}
	};
	
//	/**
//	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
//	 */
//	private static final Consumer<ValidationContext> EMPTY_TRANSACTION = (ctx) -> {
//		if (ctx.getInvoice().getRegistry() != null && (ctx.getInvoice().getTransaction() == null)) {
//			ctx.add( InvoiceErrorMessages.C001.err(InvoiceErrorKey.TRANSACTION) );
//		}
//	};
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	private static final Consumer<ValidationContext> EMPTY_DATE = ctx -> {
		if (ctx.getInvoice().getIssueDate() == null) {
			ctx.add( InvoiceErrorMessages.C001.err(InvoiceErrorKey.ISSUE_DATE) );
		}
	};

	/**
	 * El Domain/Serie/Número/Tipo no puede estar duplicado
	 */
	private static final Consumer<ValidationContext> DUPLICATED_SERIES_NUMBER = ctx -> {
		if ( ctx.getInvoice().isSales() 
		  && ctx.getInvoice().getNumber() != 0 && ctx.getCtx() != null && ctx.getCtx().getDslContext().fetchExists( 
			ctx.getCtx().getDslContext().selectOne()
				.from(INVOICE)
				.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
				.and(AonStringUtils.isBlank(ctx.getInvoice().getSeries())
					?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
					:INVOICE.SERIES.eq(ctx.getInvoice().getSeries()))
				.and(INVOICE.NUMBER.eq(ctx.getInvoice().getNumber()))
				.and(ctx.getInvoice().getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(ctx.getInvoice().getId()))
				.and(INVOICE.TYPE.eq(ctx.getInvoice().getType().value())))) {
			ctx.add( InvoiceErrorMessages.C005.wrn(InvoiceErrorKey.DUPLICATED_SERIES_NUMBER) );
		}
	};
	
	/**
	 * En facturas recibidas, el Domain/Registry/Numero Referencia no puede estar duplicado
	 */
	private static final Consumer<ValidationContext> DUPLICATED_REFERENCE_CODE = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getReferenceCode())
			&& ctx.getInvoice().getType() != null
			&& !ctx.getInvoice().isSales() 
			&& !ctx.getInvoice().isUndeductible() 
			&& ctx.getInvoice().getIssueDate() != null
			&& ctx.getCtx() != null && ctx.getCtx().getDslContext().fetchExists( 
				ctx.getCtx().getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
					.and(INVOICE.REGISTRY.eq(ctx.getInvoice().getRegistry()))
					.and(INVOICE.REFERENCE_CODE.eq(ctx.getInvoice().getReferenceCode()))
					.and(INVOICE.TYPE.eq(ctx.getInvoice().getType().value()))
					.and(ctx.getInvoice().getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(ctx.getInvoice().getId()))					
					.and(DSL.year(INVOICE.ISSUE_DATE).eq(AonDateUtils.getYear( ctx.getInvoice().getIssueDate())))
				)) {
				ctx.add( InvoiceErrorMessages.C006.wrn(InvoiceErrorKey.DUPLICATED_REFERENCE_CODE) );
		}
	};

	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	private static final Consumer<ValidationContext> EMPTY_TAX_DATE = ctx -> {
		if (ctx.getInvoice().getIssueDate() != null && ctx.getInvoice().getTaxDate() == null) {
			ctx.add( InvoiceErrorMessages.C001.err(InvoiceErrorKey.TAX_DATE) );
		}
	};
	
	/**
	 * Si se ha indicado una fecha de límte de operaciones en los parámetros de la 
	 * empresa, debe ser anterior a la fecha de factura.
	 * 
	 */
	private static final Consumer<ValidationContext> OPERATIONS_DEADLINE = ctx -> {
		if (ctx.getConfig() != null) {
			Date deadline = ctx.getConfig().getOperationsDeadline();
			if (deadline != null && ctx.getInvoice().getIssueDate() != null && deadline.after(ctx.getInvoice().getIssueDate()))
				ctx.add( InvoiceErrorMessages.C007.wrn(InvoiceErrorKey.ISSUE_DATE) );
		}
	};

	
	/**
	 * El Tipo de la factura no puede ser null.
	 */
	private static final Consumer<ValidationContext> EMPTY_INVOICE_TYPE = ctx -> {
		if (ctx.getInvoice().getType() == null) {
			ctx.add( InvoiceErrorMessages.C001.err(InvoiceErrorKey.TYPE) );
		}
	};

	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	private static final Consumer<ValidationContext> EMPTY_REGISTRY = ctx -> {
		if (ctx.getInvoice().getRegistry() == null) {
			if (AonStringUtils.isEmpty(ctx.getInvoice().getRegistryDocument())) {
				ctx.add( InvoiceErrorMessages.C001.err(InvoiceErrorKey.REGISTRY) );
			} else {
				ctx.add( InvoiceErrorMessages.C009.err(InvoiceErrorKey.REGISTRY,
						(ctx.getInvoice().isSales()?"cliente":"acreedor/proveedor")
						,(ctx.getInvoice().getRegistryDocument() + " " + ctx.getInvoice().getRegistryName())) );
			}
		}
	};

	/**
	 * El document del titular de la factura es un dato obligatorio.
	 */
	private static final Consumer<ValidationContext> EMPTY_REGISTRY_DOCUMENT = ctx -> {
		if (ctx.getInvoice().getRegistry() != null && AonStringUtils.isBlank(ctx.getInvoice().getRegistryDocument())) {
			ctx.add( InvoiceErrorMessages.C001.wrn(InvoiceErrorKey.RDOCUMENT) );
		}
	};
	/**
	 * El documento del titular no debe superar caracters definido en BD.
	 */
	private static final Consumer<ValidationContext> OVERFLOW_REGISTRY_DOCUMENT = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryDocument()) 
		 && willOverflow(INVOICE.RDOCUMENT, ctx.getInvoice().getRegistryDocument())) {
			ctx.add( InvoiceErrorMessages.C002.err(InvoiceErrorKey.RDOCUMENT, InvoiceErrorKey.RDOCUMENT.getDescription(), INVOICE.RDOCUMENT.getDataType().length()));
		}
	};

	/**
	 * El documento del titular debería validarse correctamente.
	 */
	private static final Consumer<ValidationContext> INVALID_REGISTRY_DOCUMENT = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryDocument())) {
			String country = ctx.getInvoice().getRegistryDocumentCountry() == null ? null : 
				ctx.getInvoice().getRegistryDocumentCountry().getIso2();
			if ("ES".equals( country )) {
				if (!AonDocumentUtil.isValid(ctx.getInvoice().getRegistryDocument())) {
					ctx.add( InvoiceErrorMessages.C004.wrn(InvoiceErrorKey.RDOCUMENT) );
				}
			} else if (!AonDocumentUtil.isValidComunitaryCode(country,ctx.getInvoice().getRegistryDocument())) {
				ctx.add( InvoiceErrorMessages.C004.wrn(InvoiceErrorKey.RDOCUMENT) );
			}
		}
	};
	/**
	 * La razon social del titular de la factura es un dato obligatorio.
	 */
	private static final Consumer<ValidationContext> EMPTY_REGISTRY_NAME = ctx -> {
		if (ctx.getInvoice().getRegistry() != null &&  AonStringUtils.isBlank(ctx.getInvoice().getRegistryName())) {
			ctx.add( InvoiceErrorMessages.C001.wrn(InvoiceErrorKey.RNAME) );
		}
	};
	/**
	 * La razon social del titular no debe superar caracters definido en BD.
	 */
	private static final Consumer<ValidationContext> OVERFLOW_REGISTRY_NAME = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryName()) 
		 && willOverflow(INVOICE.RNAME, ctx.getInvoice().getRegistryName())) {
			ctx.add( InvoiceErrorMessages.C002.err(InvoiceErrorKey.RNAME, InvoiceErrorKey.RNAME.getDescription(), INVOICE.RNAME.getDataType().length()));
		}
	};

	/**
	 * La dirección de la factura no debe superar caracters definido en BD.
	 */
	private static final Consumer<ValidationContext> OVERFLOW_ADDRESS = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getAddress().getAddress())
		 && willOverflow(RADDRESS.ADDRESS, ctx.getInvoice().getAddress().getAddress())) {
			ctx.add( InvoiceErrorMessages.C002.err(InvoiceErrorKey.ADDRESS, InvoiceErrorKey.ADDRESS.getDescription(), RADDRESS.ADDRESS.getDataType().length()));
		}
	};

	/**
	 * La descripcion del detalle no debe superar caracters definido en BD.
	 */
	private static final BiConsumer<ValidationContext,InvoiceDetail> OVERFLOW_DETAIL_DESCRIPTION = (ctx,detail) -> {
		if (AonStringUtils.isNotBlank(detail.getDescription())
		 && willOverflow(INVOICE_DETAIL.DESCRIPTION, detail.getDescription())) {
			InvoiceErrorContext context = new InvoiceErrorContext(InvoiceErrorKey.DETAIL_DESCRIPTION, (int) detail.getLine());  
			ctx.add( InvoiceErrorMessages.C002.err(context, InvoiceErrorKey.DETAIL_DESCRIPTION.getDescription(), INVOICE_DETAIL.DESCRIPTION.getDataType().length()));
		}
	};

	private static final Consumer<ValidationContext> DETAILS_VALIDATION = ctx -> 
		ctx.getInvoice().detailStream().forEach(d -> OVERFLOW_DETAIL_DESCRIPTION.accept(ctx,d));
	
	private static final Consumer<ValidationContext> INVEST_ASSET_VALIDATION = ctx -> {
		if ( !ctx.getInvoice().isSales() && ctx.getInvoice().getRegistry() != null ) {
			Date invoiceDate =  AonObjectUtils.defaultIfNull(ctx.getInvoice().getIssueDate(), new Date() );
			ctx.ctx.getDslContext().select(INVOICE.ID)
				.from(INVOICE)
				.innerJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
				.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
				.and(INVOICE.REGISTRY.eq(ctx.getInvoice().getRegistry()))
				.and(INVOICE.ISSUE_DATE.le( AonDateUtils.toSql(invoiceDate)))
				.and(INVOICE_DETAIL.INVEST_ASSET.isNotNull())
				.groupBy(INVOICE.ID)
				.orderBy( INVOICE.ISSUE_DATE.desc(), INVOICE.ID.desc() )
				.limit(3)
				.fetch()
				.stream()
				.findFirst()
				.ifPresent( i -> ctx.getInvoice().addMessage( InvoiceErrorMessages.C202.wrn(InvoiceErrorKey.DETAILS, ctx.getInvoice().getRegistryName()) ));
			
			
//			Field<BigDecimal> nullField = DSL.sum(DSL.decode().when(INVOICE_DETAIL.INVEST_ASSET.isNull(), 1).otherwise(0));
//			Field<BigDecimal> notNullField = DSL.sum(DSL.decode().when(INVOICE_DETAIL.INVEST_ASSET.isNotNull(), 1).otherwise(0));
//			if (ctx.getCtx().getDslContext().select(notNullField,nullField)
//				.from(INVOICE)
//				.innerJoin(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
//				.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
//				.and(INVOICE.REGISTRY.eq(ctx.getInvoice().getRegistry()))
//				.orderBy( INVOICE.ID.desc() )
//				.limit(20)
//				.fetch()
//				.stream()
//				.filter( r -> r != null)
////				.map( r -> {
////					System.out.println(
////						" notNullField ..: " + r.getValue(notNullField) +
////						" nullField ..: " + r.getValue(nullField) +
////						" ---> " + (AonNumberUtils.compare(r.getValue(nullField),r.getValue(notNullField)) > 0)
////					);
////					return r;
////				})
//				.map( r -> AonNumberUtils.compare(r.getValue(notNullField),r.getValue(nullField)) > 0)
//				.findFirst()
//				.orElse( false )) {
//				
//				ctx.add( InvoiceErrorMessages.C202.wrn(InvoiceErrorKey.DETAILS) );
				
		};			
	};

	private static final BiConsumer<Finance,ValidationContext> CHECK_FINANCE_AMOUNT_ZERO = (finance,ctx) -> {
		if (AonMathUtils.isZero(finance.getAmount())) {
			InvoiceErrorContext context = new InvoiceErrorContext(InvoiceErrorKey.FINANCE_AMOUNT_ZERO);
			ctx.add( InvoiceErrorMessages.C014.wrn(context, InvoiceErrorKey.FINANCE_AMOUNT_ZERO.getDescription()));
		}
	};

	private static final BiConsumer<Finance,ValidationContext> CHECK_BANK_ACCOUNT = (finance,ctx) -> {
		if (finance.getBankAccount() == null || AonStringUtils.isEmpty(finance.getBankAccount().getBban())) {
			finance.setBankAccount(null);
			finance.setBankAlias(null);
			finance.setBic(null);
		}
		if (finance.getBankAccount() != null && !finance.getBankAccount().isValidBankAccount()) {
			InvoiceErrorContext context = new InvoiceErrorContext(InvoiceErrorKey.FINANCE_WRONG_ACCOUNT_BANK);
			ctx.add( InvoiceErrorMessages.C014.err(context, InvoiceErrorKey.FINANCE_WRONG_ACCOUNT_BANK.getDescription()));
		}
	};
	
	private static final Consumer<ValidationContext> FINANCES_VALIDATION = ctx -> 
		ctx.getInvoice().financeStream()
			.forEach(f -> 
				CHECK_FINANCE_AMOUNT_ZERO
					.andThen(CHECK_BANK_ACCOUNT)			
				 	.accept(f, ctx)
			);
	
	/**
	 * Si el año de la factura no es anterior en cinco años al actual.
	 */
	private static final Consumer<ValidationContext> CHECK_FIVE_YEARS = ctx -> {
		if (ctx.getInvoice().getIssueDate() != null) {
			int thisYear = AonDateUtils.getYear(new Date());
			int invoiceYear = AonDateUtils.getYear(ctx.getInvoice().getIssueDate());
			if (invoiceYear < (thisYear - 5) || invoiceYear > (thisYear + 1)) {
				ctx.add( InvoiceErrorMessages.C008.err(InvoiceErrorKey.ISSUE_DATE) );
			}
		}
	};

	private static final Consumer<ValidationContext> CHECK_LINES = ctx -> {
		if (!ctx.getInvoice().hasDetails()) {
			ctx.add( InvoiceErrorMessages.C010.err(InvoiceErrorKey.DETAILS) );
		}
	};

	private static final Consumer<ValidationContext> ENTRY_SETTLED = ctx -> {
		if (ctx.getResult().getAccountingInvoice() != null && ctx.getResult().getAccountingInvoice().getAccountEntry() != null) {
			AccountEntry ae = ctx.getResult().getAccountingInvoice().getAccountEntry();
			double sumD = 0.0;
			double sumC = 0.0;
			boolean empty = true;
			for (AccountEntryDetail aed : ae.getDetails()) {
				if (!aed.isDeleted()) {
					sumD = AonMathUtils.sum(sumD, aed.getDebit());	
					sumC = AonMathUtils.sum(sumC, aed.getCredit());
					empty = false;
				}
			}
//			if (empty) {
//				ctx.add( InvoiceErrorMessages.C013.wrn(InvoiceErrorKey.ACCOUNT_ENTRY) );
//			}
			if (!AonMathUtils.isZero( AonMathUtils.round(sumD - sumC))) {
				ctx.add( InvoiceErrorMessages.C012.wrn(InvoiceErrorKey.ACCOUNT_ENTRY) );
			}
		}
	};

	private static final Consumer<ValidationContext> ACCOUNT_PERIOD_VALIDATION = ctx -> {
		if (ctx.getResult().getAccountingInvoice() != null 
		 && ctx.getResult().getAccountingInvoice().getAccountEntry() != null
		 && ctx.getConfig() != null
		 && ctx.getConfig().accounting() != null
		 ) {
			AccountEntry ae = ctx.getResult().getAccountingInvoice().getAccountEntry();
			AonCollectionUtils.stream(ctx.getConfig().accounting().getPeriods())
				.filter( p -> AonNumberUtils.equals(p.getId(),ae.getPeriod()) && p.getStatus().isActive() )
				.findFirst()
				.ifPresentOrElse( 
					p -> {}
					, () -> {
						ctx.add( InvoiceErrorMessages.C201.wrn(InvoiceErrorKey.ACCOUNT_ENTRY) );		
					}
				);
		}
	};
	
	private static boolean willOverflow(Field<String> field, String series) {
		return (AonStringUtils.length(series) > field.getDataType().length());
	}

	private static class ValidationContext {
		private AONContext ctx;
		private AonConfiguration config;
		private TediResult result; 
		
		private ValidationContext(AONContext ctx,AonConfiguration config,TediResult result) {
			this.ctx = ctx;
			this.config = config;
			this.result = result;
		}
		private AONContext getCtx() {
			return ctx;
		}
		public AonConfiguration getConfig() {
			return config;
		}
		private TediResult getResult() {
			return result;
		}
		public void add(InvoiceError err) {
			this.getResult().getAccountingInvoice().add(err);
		}
		public Invoice getInvoice() {
			return this.getResult().getInvoice();
		}
	}
	
	public static void validateInvoice(AONContext ctx,AonConfiguration config, TediResult result) throws AonCoreException {
		
		EMPTY_DOMAIN
			.andThen(EMPTY_INVOICE_SCOPE)
			.andThen(OVERFLOW_SERIES)
			.andThen(EMPTY_REFERENCE_CODE)
			.andThen(EMPTY_SALES_NUMBER)
			.andThen(OVERFLOW_REFERENCE_CODE)
			.andThen(EMPTY_DATE)
			.andThen(OPERATIONS_DEADLINE)
			.andThen(DUPLICATED_SERIES_NUMBER)
			.andThen(DUPLICATED_REFERENCE_CODE)
			.andThen(EMPTY_TAX_DATE)
			.andThen(EMPTY_INVOICE_TYPE)
			.andThen(EMPTY_REGISTRY)
			.andThen(EMPTY_REGISTRY_DOCUMENT)
			.andThen(OVERFLOW_REGISTRY_DOCUMENT)
			.andThen(INVALID_REGISTRY_DOCUMENT)
			.andThen(EMPTY_REGISTRY_NAME)
			.andThen(OVERFLOW_REGISTRY_NAME)
			.andThen(OVERFLOW_ADDRESS)
			.andThen(CHECK_FIVE_YEARS)
			.andThen(CHECK_LINES)
			
			.andThen(DETAILS_VALIDATION)
			.andThen(INVEST_ASSET_VALIDATION)
			
			.andThen(FINANCES_VALIDATION)
			
			.andThen(ENTRY_SETTLED)
			.andThen(ACCOUNT_PERIOD_VALIDATION)
			
		.accept(new ValidationContext(ctx,config,result));

//		.andThen(EMPTY_TRANSACTION)
//		.andThen(VALIDATE_INVOICE_DETAILS)
//		.andThen(OPERATIONS_DEADLINE)
	}

}
