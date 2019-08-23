package net.aonsolutions.aon.tedi;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jooq.Field;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.tedi.TediContext;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TediValidator {
	private TediValidator() {
	}

	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	public static Consumer<TediResult> EMPTY_DOMAIN = (ctx) -> {
		if (ctx.getInvoice().getDomain() == 0) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.DOMAIN) );
		}
	};
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	public static Consumer<TediResult> EMPTY_INVOICE_SCOPE = (ctx) -> {
		if (ctx.getInvoice().getScope() == null || ctx.getInvoice().getScope().getId() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.SCOPE));
		}
	};
	/**
	 * La serie no debe superar caracters definido en BD.
	 */
	public static Consumer<TediResult> OVERFLOW_SERIES = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getSeries())) {
			if (willOverflow(INVOICE.SERIES, ctx.getInvoice().getSeries())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.SERIES, TediContextKey.SERIES.getDescription(),INVOICE.SERIES.getDataType().length()));
			}
		}
	};
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	public static Consumer<TediResult> EMPTY_REFERENCE_CODE = (ctx) -> {
		if (!ctx.getInvoice().isSales() && AonStringUtils.isBlank(ctx.getInvoice().getReferenceCode())) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.REFERENCE_CODE));
		}
	};
	
	/**
	 * Si la factura es de ventas, debe tener número de factura.
	 */
	public static Consumer<TediResult> EMPTY_SALES_NUMBER = (ctx) -> {
		if (ctx.getInvoice().isSales() && ctx.getInvoice().getNumber() == 0 ) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.NUMBER));
		}
	};
	

	/**
	 * El codigo de referencia debe superar caracters definido en BD.
	 */
	public static Consumer<TediResult> OVERFLOW_REFERENCE_CODE = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getReferenceCode())) {
			if (willOverflow(INVOICE.REFERENCE_CODE, ctx.getInvoice().getReferenceCode())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.REFERENCE_CODE, TediContextKey.REFERENCE_CODE.getDescription(), INVOICE.REFERENCE_CODE.getDataType().length()));
			}
		}
	};
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	public static Consumer<TediResult> EMPTY_TRANSACTION = (ctx) -> {
		if (ctx.getInvoice().getTransaction() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.TRANSACTION) );
		}
	};
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	public static Consumer<TediResult> EMPTY_DATE = (ctx) -> {
		if (ctx.getInvoice().getIssueDate() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.ISSUE_DATE) );
		}
	};

	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	public static Consumer<TediResult> EMPTY_TAX_DATE = (ctx) -> {
		if (ctx.getInvoice().getTaxDate() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.TAX_DATE) );
		}
	};
	/**
	 * El Tipo de la factura no puede ser null.
	 */
	public static Consumer<TediResult> EMPTY_INVOICE_TYPE = (ctx) -> {
		if (ctx.getInvoice().getType() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.TYPE) );
		}
	};

	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	public static Consumer<TediResult> EMPTY_REGISTRY = (ctx) -> {
		if (ctx.getInvoice().getRegistry() == null) {
			ctx.add( TediErrorMessages.C001.err(TediContextKey.REGISTRY) );
		}
	};

	/**
	 * El document del titular de la factura es un dato obligatorio.
	 */
	public static Consumer<TediResult> EMPTY_REGISTRY_DOCUMENT = (ctx) -> {
		if (AonStringUtils.isBlank(ctx.getInvoice().getRegistryDocument())) {
			ctx.add( TediErrorMessages.C001.wrn(TediContextKey.RDOCUMENT) );
		}
	};
	/**
	 * El documento del titular no debe superar caracters definido en BD.
	 */
	public static Consumer<TediResult> OVERFLOW_REGISTRY_DOCUMENT = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryDocument())) {
			if (willOverflow(INVOICE.RDOCUMENT, ctx.getInvoice().getRegistryDocument())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.RDOCUMENT, TediContextKey.RDOCUMENT.getDescription(), INVOICE.RDOCUMENT.getDataType().length()));
			}
		}
	};

	/**
	 * El documento del titular debería validarse correctamente.
	 */
	public static Consumer<TediResult> INVALID_REGISTRY_DOCUMENT = (ctx) -> {
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
	public static Consumer<TediResult> EMPTY_REGISTRY_NAME = (ctx) -> {
		if (AonStringUtils.isBlank(ctx.getInvoice().getRegistryName())) {
			ctx.add( TediErrorMessages.C001.wrn(TediContextKey.RNAME) );
		}
	};
	/**
	 * La razon social del titular no debe superar caracters definido en BD.
	 */
	public static Consumer<TediResult> OVERFLOW_REGISTRY_NAME = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getRegistryName())) {
			if (willOverflow(INVOICE.RNAME, ctx.getInvoice().getRegistryName())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.RNAME, TediContextKey.RNAME.getDescription(), INVOICE.RNAME.getDataType().length()));
			}
		}
	};

	/**
	 * La dirección de la factura no debe superar caracters definido en BD.
	 */
	public static Consumer<TediResult> OVERFLOW_ADDRESS = (ctx) -> {
		if (AonStringUtils.isNotBlank(ctx.getInvoice().getAddress())) {
			if (willOverflow(RADDRESS.ADDRESS, ctx.getInvoice().getAddress())) {
				ctx.add( TediErrorMessages.C002.err(TediContextKey.ADDRESS, TediContextKey.ADDRESS.getDescription(), RADDRESS.ADDRESS.getDataType().length()));
			}
		}
	};

	/**
	 * La descripcion del detalle no debe superar caracters definido en BD.
	 */
	public static BiConsumer<TediResult,InvoiceDetail> OVERFLOW_DETAIL_DESCRIPTION = (ctx,detail) -> {
		if (AonStringUtils.isNotBlank(detail.getDescription())) {
			if (willOverflow(INVOICE_DETAIL.DESCRIPTION, detail.getDescription())) {
				TediContext context = new TediContext(TediContextKey.DETAIL_DESCRIPTION, (int) detail.getLine());  
				ctx.add( TediErrorMessages.C002.err(context, TediContextKey.DETAIL_DESCRIPTION.getDescription(), INVOICE_DETAIL.DESCRIPTION.getDataType().length()));
			}
		}
	};

	/**
	 * La dirección de la factura no debe superar caracters definido en BD.
	 */
	public static Consumer<TediResult> DETAILS_VALIDATION = (ctx) -> {
		if (ctx.getInvoice().getDetails() != null) {
			for (InvoiceDetail detail : ctx.getInvoice().getDetails()) {
				OVERFLOW_DETAIL_DESCRIPTION
				 .accept(ctx,detail);
			}
		}
	};

	/*
	 * *********************************************************
	 * *********************************************************
	 * *********************************************************
	 * *********************************************************
	 * *********************************************************
	 */

//	/**
//	 * El Domain/Serie/N?mero/Tipo no puede estar duplicado
//	 */
//	public static Consumer<TediResult> DUPLICATED_SERIES_NUMBER = (ctx) -> {
//		if (ctx.getContext().getDslContext()
//				.fetchExists(ctx.getContext().getDslContext().selectOne().from(INVOICE)
//						.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
//						.and(AonStringUtils.isBlank(ctx.getInvoice().getSeries())
//								? INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
//								: INVOICE.SERIES.eq(ctx.getInvoice().getSeries()))
//						.and(INVOICE.NUMBER.eq(ctx.getInvoice().getNumber()))
//						.and(ctx.getInvoice().getId() == null ? DSL.trueCondition()
//								: INVOICE.ID.ne(ctx.getInvoice().getId()))
//						.and(INVOICE.TYPE.eq(ctx.getInvoice().getType().value())))) {
//			ctx.err(TediContextKey.NUMBER, TediError.C005);
//		}
//	};
//
//	/**
//	 * En facturas recibidas, el Domain/Registry/Numero Referencia no puede estar
//	 * duplicado
//	 */
//	public static Consumer<TediResult> DUPLICATED_REFERENCE_CODE = (ctx) -> {
//		if (!ctx.getInvoice().isSales()) {
//			if (ctx.getContext().getDslContext().fetchExists(ctx.getContext().getDslContext().selectOne().from(INVOICE)
//					.where(INVOICE.DOMAIN.eq(ctx.getInvoice().getDomain()))
//					.and(INVOICE.REGISTRY.eq(ctx.getInvoice().getRegistry()))
//					.and(INVOICE.REFERENCE_CODE.eq(ctx.getInvoice().getReferenceCode()))
//					.and(INVOICE.TYPE.eq(ctx.getInvoice().getType().value()))
//					.and(ctx.getInvoice().getId() == null ? DSL.trueCondition()
//							: INVOICE.ID.ne(ctx.getInvoice().getId()))
//					.and(DSL.year(INVOICE.ISSUE_DATE).eq(AonDateUtils.getYear(ctx.getInvoice().getIssueDate()))))) {
//				ctx.err(TediContextKey.NUMBER, TediError.C006);
//			}
//		}
//	};

	/**
	 * Si el año de la factura no es anterior en cinco años al actual.
	 */
	public static Consumer<TediResult> CHECK_FIVE_YEARS = (ctx) -> {
		if (ctx.getInvoice().getIssueDate() != null) {
			int thisYear = AonDateUtils.getYear(new Date());
			int invoiceYear = AonDateUtils.getYear(ctx.getInvoice().getIssueDate());
			if (invoiceYear < (thisYear - 5) || invoiceYear > (thisYear + 1)) {
				ctx.add( TediErrorMessages.C008.err(TediContextKey.ISSUE_DATE) );
			}
		}
	};

	/**
	 * Si se ha indicado una fecha de l?mte de operaciones en los par?metros de la
	 * empresa, debe ser anterior a la fecha de factura.
	 * 
	 */
//	public static Consumer<TediResult> OPERATIONS_DEADLINE = (ctx) -> {
//		Date deadline = ctx.getConfiguration().getOperationsDeadline();
//		if (deadline != null && deadline.after(ctx.getInvoice().getIssueDate())) {
//			ctx.err(TediContextKey.NUMBER, TediError.C007);
//		}
//	};

	private static boolean willOverflow(Field<String> field, String series) {
		return (AonStringUtils.length(series) > field.getDataType().length());
	}

//	public static TediResult validateInvoice(AONContext ctx, AonConfiguration config, AccountingInvoice ai)
//			throws AonCoreException {
//		TediResult context = new TediResult(null, ai);
//		validateInvoice(context);
//		return context;
//	}

	public static void validateInvoice(TediResult ctx) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_INVOICE_SCOPE)
			.andThen(OVERFLOW_SERIES)
			.andThen(EMPTY_REFERENCE_CODE)
			.andThen(EMPTY_SALES_NUMBER)
			.andThen(OVERFLOW_REFERENCE_CODE)
			.andThen(EMPTY_DATE)
			.andThen(EMPTY_TAX_DATE)
			.andThen(EMPTY_INVOICE_TYPE)
			.andThen(EMPTY_REGISTRY)
			.andThen(EMPTY_REGISTRY_DOCUMENT)
			.andThen(OVERFLOW_REGISTRY_DOCUMENT)
			.andThen(INVALID_REGISTRY_DOCUMENT)
			.andThen(EMPTY_REGISTRY_NAME)
			.andThen(OVERFLOW_REGISTRY_NAME)
			.andThen(OVERFLOW_ADDRESS)
			.andThen(DETAILS_VALIDATION)
			.andThen(CHECK_FIVE_YEARS)
		.accept(ctx);
		
//		.andThen(EMPTY_TRANSACTION)
//		.andThen(VALIDATE_INVOICE_DETAILS)
//		.andThen(DUPLICATED_SERIES_NUMBER)
//		.andThen(DUPLICATED_REFERENCE_CODE)
//		.andThen(OPERATIONS_DEADLINE)
	}

}
