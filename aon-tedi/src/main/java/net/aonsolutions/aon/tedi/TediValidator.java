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
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.tedi.TediContext;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TediValidator {
	private TediValidator() {
	}

	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	public static Consumer<ValidationContext> EMPTY_DOMAIN = (ctx) -> {
		if (ctx.getInvoice().getDomain() == 0) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.DOMAIN) );
		}
	};
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	public static Consumer<ValidationContext> EMPTY_INVOICE_SCOPE = (ctx) -> {
		if (ctx.getInvoice().getRegistry() != null &&
			(ctx.getInvoice().getScope() == null || ctx.getInvoice().getScope().getId() == null)) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.SCOPE));
		}
	};
	/**
	 * La serie no debe superar caracters definido en BD.
	 */
	public static Consumer<ValidationContext> OVERFLOW_SERIES = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getSeries())) {
			if (willOverflow(INVOICE.SERIES, ctx.getInvoice().getSeries())) {
				ctx.add( TediErrorMessages.C002.wrn(TediContextKey.SERIES, TediContextKey.SERIES.getDescription(),INVOICE.SERIES.getDataType().length()));
			}
		}
	};
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	public static Consumer<ValidationContext> EMPTY_REFERENCE_CODE = (ctx) -> {
		if (!ctx.getInvoice().isSales() && AonStringUtils.isBlank(ctx.getInvoice().getReferenceCode())) {
			ctx.add( TediErrorMessages.C001.wrn(TediContextKey.REFERENCE_CODE));
		}
	};
	
	/**
	 * Si la factura es de ventas, debe tener número de factura.
	 */
	public static Consumer<ValidationContext> EMPTY_SALES_NUMBER = (ctx) -> {
		if (ctx.getInvoice().isSales() && ctx.getInvoice().getNumber() == 0 ) {
			ctx.add( TediErrorMessages.C001.wrn(TediContextKey.NUMBER));
		}
	};
	

	/**
	 * El codigo de referencia debe superar caracters definido en BD.
	 */
	public static Consumer<ValidationContext> OVERFLOW_REFERENCE_CODE = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getReferenceCode())) {
			if (willOverflow(INVOICE.REFERENCE_CODE, ctx.getInvoice().getReferenceCode())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.REFERENCE_CODE, TediContextKey.REFERENCE_CODE.getDescription(), INVOICE.REFERENCE_CODE.getDataType().length()));
			}
		}
	};
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	public static Consumer<ValidationContext> EMPTY_TRANSACTION = (ctx) -> {
		if (ctx.getInvoice().getRegistry() != null && (ctx.getInvoice().getTransaction() == null)) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.TRANSACTION) );
		}
	};
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	public static Consumer<ValidationContext> EMPTY_DATE = (ctx) -> {
		if (ctx.getInvoice().getIssueDate() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.ISSUE_DATE) );
		}
	};

	/**
	 * El Domain/Serie/Número/Tipo no puede estar duplicado
	 */
	public static Consumer<ValidationContext> DUPLICATED_SERIES_NUMBER = (ctx) -> {
		if ( ctx.getInvoice().isSales() && ctx.getInvoice().getNumber() != 0) {
			if (ctx.getCtx() != null && ctx.getCtx().getDslContext().fetchExists( 
					ctx.getCtx().getDslContext().selectOne()
						.from(INVOICE)
						.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
						.and(AonStringUtils.isBlank(ctx.getInvoice().getSeries())
							?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
							:INVOICE.SERIES.eq(ctx.getInvoice().getSeries()))
						.and(INVOICE.NUMBER.eq(ctx.getInvoice().getNumber()))
						.and(ctx.getInvoice().getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(ctx.getInvoice().getId()))
						.and(INVOICE.TYPE.eq(ctx.getInvoice().getType().value())))) {
				ctx.add( TediErrorMessages.C005.wrn(TediContextKey.DUPLICATED_SERIES_NUMBER) );
			}
		}
	};
	
	/**
	 * En facturas recibidas, el Domain/Registry/Numero Referencia no puede estar duplicado
	 */
	public static Consumer<ValidationContext> DUPLICATED_REFERENCE_CODE = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getReferenceCode())
			&& ctx.getInvoice().getType() != null
			&& !ctx.getInvoice().isSales() 
			&& !ctx.getInvoice().isUndeductible() 
			&& ctx.getInvoice().getIssueDate() != null) {
			if (ctx.getCtx() != null && ctx.getCtx().getDslContext().fetchExists( 
					ctx.getCtx().getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
					.and(INVOICE.REGISTRY.eq(ctx.getInvoice().getRegistry()))
					.and(INVOICE.REFERENCE_CODE.eq(ctx.getInvoice().getReferenceCode()))
					.and(INVOICE.TYPE.eq(ctx.getInvoice().getType().value()))
					.and(ctx.getInvoice().getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(ctx.getInvoice().getId()))					
					.and(DSL.year(INVOICE.ISSUE_DATE).eq(AonDateUtils.getYear( ctx.getInvoice().getIssueDate())))
				)) {
				ctx.add( TediErrorMessages.C006.wrn(TediContextKey.DUPLICATED_REFERENCE_CODE) );
			}
		}
	};

	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	public static Consumer<ValidationContext> EMPTY_TAX_DATE = (ctx) -> {
		if (ctx.getInvoice().getIssueDate() != null && ctx.getInvoice().getTaxDate() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.TAX_DATE) );
		}
	};
	/**
	 * El Tipo de la factura no puede ser null.
	 */
	public static Consumer<ValidationContext> EMPTY_INVOICE_TYPE = (ctx) -> {
		if (ctx.getInvoice().getType() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.TYPE) );
		}
	};

	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	public static Consumer<ValidationContext> EMPTY_REGISTRY = (ctx) -> {
		if (ctx.getInvoice().getRegistry() == null) {
			if (AonStringUtils.isEmpty(ctx.getInvoice().getRegistryDocument())) {
				ctx.add( TediErrorMessages.C001.err(TediContextKey.REGISTRY) );
			} else {
				ctx.add( TediErrorMessages.C009.err(TediContextKey.REGISTRY,
						(ctx.getInvoice().isSales()?"cliente":"acreedor/proveedor")
						,(ctx.getInvoice().getRegistryDocument() + " " + ctx.getInvoice().getRegistryName())) );
			}
		}
	};

	/**
	 * El document del titular de la factura es un dato obligatorio.
	 */
	public static Consumer<ValidationContext> EMPTY_REGISTRY_DOCUMENT = (ctx) -> {
		if (ctx.getInvoice().getRegistry() != null && AonStringUtils.isBlank(ctx.getInvoice().getRegistryDocument())) {
			ctx.add( TediErrorMessages.C001.wrn(TediContextKey.RDOCUMENT) );
		}
	};
	/**
	 * El documento del titular no debe superar caracters definido en BD.
	 */
	public static Consumer<ValidationContext> OVERFLOW_REGISTRY_DOCUMENT = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryDocument())) {
			if (willOverflow(INVOICE.RDOCUMENT, ctx.getInvoice().getRegistryDocument())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.RDOCUMENT, TediContextKey.RDOCUMENT.getDescription(), INVOICE.RDOCUMENT.getDataType().length()));
			}
		}
	};

	/**
	 * El documento del titular debería validarse correctamente.
	 */
	public static Consumer<ValidationContext> INVALID_REGISTRY_DOCUMENT = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryDocument())) {
			String country = ctx.getInvoice().getRegistryDocumentCountry() == null ? null : 
				ctx.getInvoice().getRegistryDocumentCountry().getIso2();
			if ("ES".equals( country )) {
				if (!AonDocumentUtil.isValid(ctx.getInvoice().getRegistryDocument())) {
					ctx.add( TediErrorMessages.C004.wrn(TediContextKey.RDOCUMENT) );
				}
			} else if (!AonDocumentUtil.isValidComunitaryCode(country,ctx.getInvoice().getRegistryDocument())) {
				ctx.add( TediErrorMessages.C004.wrn(TediContextKey.RDOCUMENT) );
			}
		}
	};
	/**
	 * La razon social del titular de la factura es un dato obligatorio.
	 */
	public static Consumer<ValidationContext> EMPTY_REGISTRY_NAME = (ctx) -> {
		if (ctx.getInvoice().getRegistry() != null &&  AonStringUtils.isBlank(ctx.getInvoice().getRegistryName())) {
			ctx.add( TediErrorMessages.C001.wrn(TediContextKey.RNAME) );
		}
	};
	/**
	 * La razon social del titular no debe superar caracters definido en BD.
	 */
	public static Consumer<ValidationContext> OVERFLOW_REGISTRY_NAME = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryName())) {
			if (willOverflow(INVOICE.RNAME, ctx.getInvoice().getRegistryName())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.RNAME, TediContextKey.RNAME.getDescription(), INVOICE.RNAME.getDataType().length()));
			}
		}
	};

	/**
	 * La dirección de la factura no debe superar caracters definido en BD.
	 */
	public static Consumer<ValidationContext> OVERFLOW_ADDRESS = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getAddress())) {
			if (willOverflow(RADDRESS.ADDRESS, ctx.getInvoice().getAddress())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.ADDRESS, TediContextKey.ADDRESS.getDescription(), RADDRESS.ADDRESS.getDataType().length()));
			}
		}
	};

	/**
	 * La descripcion del detalle no debe superar caracters definido en BD.
	 */
	public static BiConsumer<ValidationContext,InvoiceDetail> OVERFLOW_DETAIL_DESCRIPTION = (ctx,detail) -> {
		if (AonStringUtils.isNotBlank(detail.getDescription())) {
			if (willOverflow(INVOICE_DETAIL.DESCRIPTION, detail.getDescription())) {
				TediContext context = new TediContext(TediContextKey.DETAIL_DESCRIPTION, (int) detail.getLine());  
				ctx.add( TediErrorMessages.C002.err(context, TediContextKey.DETAIL_DESCRIPTION.getDescription(), INVOICE_DETAIL.DESCRIPTION.getDataType().length()));
			}
		}
	};

	public static Consumer<ValidationContext> DETAILS_VALIDATION = (ctx) -> {
		if (ctx.getInvoice().getDetails() != null) {
			for (InvoiceDetail detail : ctx.getInvoice().getDetails()) {
				OVERFLOW_DETAIL_DESCRIPTION
				 .accept(ctx,detail);
			}
		}
	};

	public static BiConsumer<Finance,ValidationContext> CHECK_FINANCE_AMOUNT_ZERO = (finance,ctx) -> {
		if (AonMathUtils.isZero(finance.getAmount())) {
			TediContext context = new TediContext(TediContextKey.FINANCE_AMOUNT_ZERO);
			ctx.add( TediErrorMessages.C014.wrn(context, TediContextKey.FINANCE_AMOUNT_ZERO.getDescription()));
		}
	};

	public static BiConsumer<Finance,ValidationContext> CHECK_BANK_ACCOUNT = (finance,ctx) -> {
		if (finance.getBankAccount() == null || AonStringUtils.isEmpty(finance.getBankAccount().getBban())) {
			finance.setBankAccount(null);
			finance.setBankAlias(null);
			finance.setBic(null);
		}
		if (finance.getBankAccount() != null && !finance.getBankAccount().isValidBankAccount()) {
			TediContext context = new TediContext(TediContextKey.FINANCE_WRONG_ACCOUNT_BANK);
			ctx.add( TediErrorMessages.C014.err(context, TediContextKey.FINANCE_WRONG_ACCOUNT_BANK.getDescription()));
		}
	};
	
	public static Consumer<ValidationContext> FINANCES_VALIDATION = (ctx) -> {
		if (ctx.getResult().getAccountingInvoice() != null && ctx.getResult().getAccountingInvoice().getInvoice().getFinances() != null) {
			for (Finance finance : ctx.getResult().getAccountingInvoice().getInvoice().getFinances()) {
				CHECK_FINANCE_AMOUNT_ZERO
				.andThen(CHECK_BANK_ACCOUNT)			
			 	.accept(finance, ctx);
			}
		}
	};

	
	/**
	 * Si el año de la factura no es anterior en cinco años al actual.
	 */
	public static Consumer<ValidationContext> CHECK_FIVE_YEARS = (ctx) -> {
		if (ctx.getInvoice().getIssueDate() != null) {
			int thisYear = AonDateUtils.getYear(new Date());
			int invoiceYear = AonDateUtils.getYear(ctx.getInvoice().getIssueDate());
			if (invoiceYear < (thisYear - 5) || invoiceYear > (thisYear + 1)) {
				ctx.add( TediErrorMessages.C008.err(TediContextKey.ISSUE_DATE) );
			}
		}
	};

	public static Consumer<ValidationContext> CHECK_LINES = (ctx) -> {
		if (ctx.getInvoice().getDetails() == null || ctx.getInvoice().getDetails().size() == 0) {
			ctx.add( TediErrorMessages.C010.err(TediContextKey.DETAILS) );
		}
	};

//	public static Consumer<ValidationContext> ENTRY_SETTLED = (ctx) -> {
//		if (ctx.getResult().getAccountingInvoice() != null && ctx.getResult().getAccountingInvoice().getAccountEntry() != null) {
//			AccountEntry ae = ctx.getResult().getAccountingInvoice().getAccountEntry();
//			double sumD = 0.0;
//			double sumC = 0.0;
//			boolean empty = true;
//			for (AccountEntryDetail aed : ae.getDetails()) {
//				if (!aed.isDeleted()) {
//					sumD = AonMathUtils.sum(sumD, aed.getDebit());	
//					sumC = AonMathUtils.sum(sumC, aed.getCredit());
//					empty = false;
//				}
//			}
//			if (empty) {
//				ctx.add( TediErrorMessages.C013.wrn(TediContextKey.ACCOUNT_ENTRY) );
//			}
//			if (!AonMathUtils.isZero( AonMathUtils.round(sumD - sumC))) {
//				ctx.add( TediErrorMessages.C012.wrn(TediContextKey.ACCOUNT_ENTRY) );
//			}
//		}
//	};

	private static boolean willOverflow(Field<String> field, String series) {
		return (AonStringUtils.length(series) > field.getDataType().length());
	}

	private static class ValidationContext {
		private AONContext ctx;
		private TediResult result; 
		
		private ValidationContext(AONContext ctx,TediResult result) {
			this.ctx = ctx;
			this.result = result;
		}
		private AONContext getCtx() {
			return ctx;
		}
		private TediResult getResult() {
			return result;
		}
		public void add(TediError err) {
			this.getResult().add(err);
		}
		public Invoice getInvoice() {
			return this.getResult().getInvoice();
		}
	}
	
	public static void validateInvoice(AONContext ctx,TediResult result) throws AonCoreException {
		
		EMPTY_DOMAIN
			.andThen(EMPTY_INVOICE_SCOPE)
			.andThen(OVERFLOW_SERIES)
			.andThen(EMPTY_REFERENCE_CODE)
			.andThen(EMPTY_SALES_NUMBER)
			.andThen(OVERFLOW_REFERENCE_CODE)
			.andThen(EMPTY_DATE)
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
			
			.andThen(FINANCES_VALIDATION)
			
//			.andThen(ENTRY_SETTLED)
			
		.accept(new ValidationContext(ctx,result));
		
//		.andThen(EMPTY_TRANSACTION)
//		.andThen(VALIDATE_INVOICE_DETAILS)
//		.andThen(OPERATIONS_DEADLINE)
	}

}
