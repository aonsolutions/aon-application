package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;

import java.util.function.Consumer;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoicePreRecorderDAO {
	
	private InvoicePreRecorderDAO() {
		
	}

	private record InvoicePreRecordContext( AONContext ctx, int domain, Invoice inv) {}
	public static Invoice fillMessages(AONContext ctx, int domain, Invoice inv) {
		InvoicePreRecordContext iprc = new InvoicePreRecordContext(ctx, domain, inv);
		if (inv.getId() == null) {
			EMPTY_INVOICE_DOMAIN
				.andThen(EMPTY_INVOICE_SCOPE)
				.andThen(OVERFLOW_INVOICE_SERIES)
				.andThen(EMPTY_INVOICE_REFERENCE_CODE)
				.andThen(OVERFLOW_INVOICE_REFERENCE_CODE)
				.andThen(EMPTY_INVOICE_TRANSACTION)
				.andThen(EMPTY_INVOICE_DATE)
				.andThen(DUPLICATED_SERIES_NUMBER)
				.andThen(DUPLICATED_REFERENCE_CODE) 
				.andThen(EMPTY_INVOICE_TAX_DATE)
				.andThen(EMPTY_INVOICE_TYPE)
				.andThen(EMPTY_INVOICE_REGISTRY)
				.andThen(EMPTY_REGISTRY_DOCUMENT)
				.andThen(OVERFLOW_REGISTRY_DOCUMENT)
				.andThen(INVALID_REGISTRY_DOCUMENT)
				.andThen(OVERFLOW_REGISTRY_NAME)
				.accept(iprc);
		}
		
		if ( !inv.isRecorded() ) {
			CHECK_IF_INVESTMENT
				.andThen(CHECK_IF_SURCHARGE)
				.andThen(CHECK_IF_WITHHOLDING)
				.andThen(CHECK_TRANSACTION)
				.andThen(CHECK_PREPAYMENT)
				.andThen(CHECK_EXPENSES)
				.accept(iprc);
		}
		
		return inv;
	}

	// ************************************************************************************** 	
	// *************************************************************************** [CHECK] ** 	
	// ************************************************************************************** 	
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_INVESTMENT = c -> {
		if (c.inv.isInvestment()) {
			c.inv.addMessage( new InvoiceError(
				 InvoiceErrorKey.INVESTMENT
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_INVESTMENT.getMessage()));
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_IF_SURCHARGE = c -> {
		if (c.inv.isSurcharge()) {
			c.inv.addMessage( new InvoiceError(
				 InvoiceErrorKey.SURCHARGE
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_SURCHARGE.getMessage()));
		}
	};
	
	private static final Consumer<InvoicePreRecordContext> CHECK_IF_WITHHOLDING = c -> {
		if (c.inv.isWithholding()) {
			c.inv.addMessage( new InvoiceError(
				 InvoiceErrorKey.WITHHOLDING
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_WITHHOLDING.getMessage()));
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_TRANSACTION = c -> {
		if (c.inv.getTransaction() != null && !c.inv.isNational()) {
			c.inv.addMessage(new InvoiceError(
				 InvoiceErrorKey.TRANSACTION
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_TRANSACTION.format(c.inv.getTransaction().getDescription())));
		}
	};

	private static final Consumer<InvoicePreRecordContext> CHECK_PREPAYMENT = c -> 
		c.inv.detailStream()
			.filter( d -> d.isPrepayment() )
			.findFirst()
			.ifPresent(d -> c.inv.addMessage( new InvoiceError(
				 InvoiceErrorKey.GENERIC
				,InvoiceErrorLevel.INF
				,AonError.INVOICE_RECORDER_PREPAYMENT.getMessage())));
	

	private static final Consumer<InvoicePreRecordContext> CHECK_EXPENSES = c -> {
		if ( c.inv.isExpenses() || c.inv.isUndeductible() ) {
			c.inv.detailStream()
				.filter( d -> d.getAccountId() == null )
				.findAny()
				.ifPresent( d -> c.inv.addMessage( new InvoiceError(
					 InvoiceErrorKey.EXPENSE_ACCOUNT
					,InvoiceErrorLevel.ERR
					,AonError.INVOICE_RECORDER_EXPENSE_ACCOUNT.getMessage())));
		}
	};
	
	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_DOMAIN = c -> {
		if (c.inv.getDomain() == null || c.inv.getDomain() == 0) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.DOMAIN
				,InvoiceErrorLevel.ERR
				,AonError.INVOICE_RECORDER_EMPTY_DOMAIN.getMessage()));
		}
	};
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_SCOPE = c -> {
		if (c.inv.getRegistry() != null &&
			(c.inv.getScope() == null || c.inv.getScope().getId() == null)) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.SCOPE
				,InvoiceErrorLevel.ERR
				,AonError.INVOICE_RECORDER_EMPTY_SCOPE.getMessage()));
		}
	};
	/**
	 * La serie no debe superar caracters definido en BD.
	 */
	private static final Consumer<InvoicePreRecordContext> OVERFLOW_INVOICE_SERIES = c -> {
		if (AonStringUtils.isNotBlank(c.inv.getSeries()) && willOverflow(INVOICE.SERIES, c.inv.getSeries())) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.SERIES
				,InvoiceErrorLevel.ERR
				,AonError.INVALID_LENGTH.format(InvoiceErrorKey.SERIES.getDescription(),INVOICE.SERIES.getDataType().length()) ));
		}
	};
	
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_REFERENCE_CODE = c -> {
		// SI Venta y no número ni reference code 
		//				o 
		// NO Venta y no ref. code 
		if ( ( (c.inv.isSales() && c.inv.getNumber() == 0 ) || !c.inv.isSales())
			&& AonStringUtils.isBlank(c.inv.getReferenceCode())) {
			
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.REFERENCE_CODE
				,InvoiceErrorLevel.ERR
				,AonError.INVOICE_EMPTY_REFERENCE_CODE.getMessage()));
		}
	};
	
	/**
	 * El codigo de referencia debe superar caracters definido en BD.
	 */
	private static final Consumer<InvoicePreRecordContext> OVERFLOW_INVOICE_REFERENCE_CODE = c -> {
		if (AonStringUtils.isNotBlank(c.inv.getReferenceCode()) 
		 && willOverflow(INVOICE.REFERENCE_CODE, c.inv.getReferenceCode())) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.REFERENCE_CODE
				,InvoiceErrorLevel.ERR
				,AonError.INVALID_LENGTH.format(InvoiceErrorKey.REFERENCE_CODE.getDescription(),INVOICE.REFERENCE_CODE.getDataType().length()) ));
		}
	};
	
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_TRANSACTION = c -> {
		if (c.inv.getRegistry() != null && (c.inv.getTransaction() == null)) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.TRANSACTION
				,InvoiceErrorLevel.ERR
				,AonError.INVOICE_EMPTY_TRANSACTION.getMessage()) );
		}
	};
	
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_DATE = c -> { 
		if (c.inv.getIssueDate() == null) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.ISSUE_DATE
				,InvoiceErrorLevel.ERR
				,AonError.INVOICE_EMPTY_DATE.getMessage()) );
		}
	};

	/**
	 * En facturas emitidas, el Domain/Serie/Número/Tipo no puede estar duplicado
	 */
	private static final Consumer<InvoicePreRecordContext> DUPLICATED_SERIES_NUMBER = c -> {
		if ( c.inv.isSales() && c.inv.getNumber() != 0 
			&& c.ctx != null && c.ctx.getDslContext().fetchExists( 
				c.ctx.getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(c.inv.getDomain()))
					.and(AonStringUtils.isBlank(c.inv.getSeries())
						?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
						:INVOICE.SERIES.eq(c.inv.getSeries()))
					.and(INVOICE.NUMBER.eq(c.inv.getNumber()))
					.and(c.inv.getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(c.inv.getId()))
					.and(INVOICE.TYPE.eq(c.inv.getType().value())))) {
				c.inv.addMessage(new InvoiceError(
					InvoiceErrorKey.DUPLICATED_SERIES_NUMBER
					,InvoiceErrorLevel.ERR
					,AonError.INVOICE_DUPLICATED_SERIES_NUMBER.getMessage()) );
			}
	};
	
	/**
	 * En facturas recibidas, el Domain/Registry/Numero Referencia no puede estar duplicado en el mismo año.
	 */
	private static final Consumer<InvoicePreRecordContext> DUPLICATED_REFERENCE_CODE = c -> {
		if (AonStringUtils.isNotBlank(c.inv.getReferenceCode())
			&& c.inv.getType() != null
			&& !c.inv.isSales() 
			&& !c.inv.isUndeductible() 
			&& c.inv.getIssueDate() != null 
			&& c.ctx != null 
			&& c.ctx.getDslContext().fetchExists( 
				c.ctx.getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(c.inv.getDomain()))
					.and(INVOICE.REGISTRY.eq(c.inv.getRegistry()))
					.and(INVOICE.REFERENCE_CODE.eq(c.inv.getReferenceCode()))
					.and(INVOICE.TYPE.eq(c.inv.getType().value()))
					.and(c.inv.getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(c.inv.getId()))					
					.and(DSL.year(INVOICE.ISSUE_DATE).eq(AonDateUtils.getYear( c.inv.getIssueDate())))
				)) {
				c.inv.addMessage(new InvoiceError(
					InvoiceErrorKey.DUPLICATED_REFERENCE_CODE
					,InvoiceErrorLevel.ERR
					,AonError.INVOICE_DUPLICATED_REFERENCE_CODE.getMessage()) );
		}
	};
	
	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_TAX_DATE = c -> {
		if (c.inv.getIssueDate() != null && c.inv.getTaxDate() == null) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.TAX_DATE
				,InvoiceErrorLevel.WRN
				,AonError.INVOICE_EMPTY_TAX_DATE.getMessage()) );
		}
	};
	
	/**
	 * El Tipo de la factura no puede ser null.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_TYPE = c -> {
		if (c.inv.getType() == null) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.TYPE
				,InvoiceErrorLevel.ERR
				,AonError.INVOICE_EMPTY_TYPE.getMessage()) );
		}
	};

	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_INVOICE_REGISTRY = c -> {
		if (c.inv.getRegistry() == null) {
			if (AonStringUtils.isEmpty(c.inv.getRegistryDocument())) {
				c.inv.addMessage(new InvoiceError(
					InvoiceErrorKey.REGISTRY
					,InvoiceErrorLevel.ERR
					,AonError.INVOICE_EMPTY_REGISTRY.getMessage()) );
			} else {
				c.inv.addMessage(new InvoiceError(
					InvoiceErrorKey.REGISTRY
					,InvoiceErrorLevel.ERR
					,AonError.INVOICE_RECORDER_REGISTRY_NOT_FOUND.format(
						(c.inv.isSales()?"cliente":"acreedor/proveedor")
						,(c.inv.getRegistryDocument() + " " + c.inv.getRegistryName()))));
			}
		}
	};

	/**
	 * El document del titular de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoicePreRecordContext> EMPTY_REGISTRY_DOCUMENT = c -> {
		if (c.inv.getRegistry() != null && AonStringUtils.isBlank(c.inv.getRegistryDocument())) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.RDOCUMENT
				,InvoiceErrorLevel.WRN
				,AonError.REGISTRY_EMPTY_DOCUMENT.getMessage()));
		}
	};
	
	/**
	 * El documento del titular no debe superar caracters definido en BD.
	 */
	private static final Consumer<InvoicePreRecordContext> OVERFLOW_REGISTRY_DOCUMENT = c -> {
		if (AonStringUtils.isNotBlank(c.inv.getRegistryDocument()) 
		 && willOverflow(INVOICE.RDOCUMENT, c.inv.getRegistryDocument())) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.RDOCUMENT
				,InvoiceErrorLevel.WRN
				,AonError.INVALID_LENGTH.format(InvoiceErrorKey.REGISTRY.getDescription(),INVOICE.RDOCUMENT.getDataType().length()) ));
		}
	};

	/**
	 * El documento del titular debería validarse correctamente.
	 */
	private static final Consumer<InvoicePreRecordContext> INVALID_REGISTRY_DOCUMENT = c -> {
		if (AonStringUtils.isNotBlank(c.inv.getRegistryDocument())) {
			String country = c.inv.getRegistryDocumentCountry() == null ? null : 
				c.inv.getRegistryDocumentCountry().getIso2();
			if ("ES".equals( country )) {
				if (!AonDocumentUtil.isValid(c.inv.getRegistryDocument())) {
					c.inv.addMessage(new InvoiceError(
						InvoiceErrorKey.RDOCUMENT
						,InvoiceErrorLevel.WRN
						,AonError.REGISTRY_INVALID_DOCUMENT.getMessage()) );
				}
			} else if (!AonDocumentUtil.isValidComunitaryCode(country,c.inv.getRegistryDocument())) {
				c.inv.addMessage(new InvoiceError(
					InvoiceErrorKey.RDOCUMENT
					,InvoiceErrorLevel.WRN
					,AonError.REGISTRY_INVALID_DOCUMENT.getMessage()) );
			}
		}
	};
	
	/**
	 * La razon social del titular de la factura es un dato obligatorio.
	 */
//	private static final Consumer<InvoicePreRecordContext> EMPTY_REGISTRY_NAME = c -> {
//		if (c.inv.getRegistry() != null &&  AonStringUtils.isBlank(c.inv.getRegistryName())) {
//			c.inv.addMessage(new InvoiceError(
//				InvoiceErrorKey.RNAME
//				,InvoiceErrorLevel.WRN
//				,AonError.REGISTRY_EMPTY_NAME.getMessage()));
//		}
//	};
	
	/**
	 * La razon social del titular no debe superar caracters definido en BD.
	 */
	private static final Consumer<InvoicePreRecordContext> OVERFLOW_REGISTRY_NAME = c -> {
		if (AonStringUtils.isNotBlank(c.inv.getRegistryName()) 
 		 && willOverflow(INVOICE.RNAME, c.inv.getRegistryName())) {
			c.inv.addMessage(new InvoiceError(
				InvoiceErrorKey.REGISTRY
				,InvoiceErrorLevel.WRN
				,AonError.INVALID_LENGTH.format(InvoiceErrorKey.RNAME.getDescription(),INVOICE.RNAME.getDataType().length()) ));
		}
	};
//
//	/**
//	 * La dirección de la factura no debe superar caracters definido en BD.
//	 */
//	private static final Consumer<InvoicePreRecordContext> OVERFLOW_ADDRESS = c -> {
//		if (AonStringUtils.isNotBlank(c.inv.getAddress().getAddress())) {
//			if (willOverflow(RADDRESS.ADDRESS, c.inv.getAddress().getAddress())) {
//				ctx.add( InvoiceErrorMessages.C002.err(InvoiceErrorKey.ADDRESS, InvoiceErrorKey.ADDRESS.getDescription(), RADDRESS.ADDRESS.getDataType().length()));
//			}
//		}
//	};
//
//	/**
//	 * La descripcion del detalle no debe superar caracters definido en BD.
//	 */
//	public static BiConsumer<ValidationContext,InvoiceDetail> OVERFLOW_DETAIL_DESCRIPTION = (ctx,detail) -> {
//		if (AonStringUtils.isNotBlank(detail.getDescription())) {
//			if (willOverflow(INVOICE_DETAIL.DESCRIPTION, detail.getDescription())) {
//				InvoiceErrorContext context = new InvoiceErrorContext(InvoiceErrorKey.DETAIL_DESCRIPTION, (int) detail.getLine());  
//				ctx.add( InvoiceErrorMessages.C002.err(context, InvoiceErrorKey.DETAIL_DESCRIPTION.getDescription(), INVOICE_DETAIL.DESCRIPTION.getDataType().length()));
//			}
//		}
//	};
//
//	private static final Consumer<InvoicePreRecordContext> DETAILS_VALIDATION = c -> {
//		if (c.inv.getDetails() != null) {
//			for (InvoiceDetail detail : c.inv.getDetails()) {
//				OVERFLOW_DETAIL_DESCRIPTION
//				 .accept(ctx,detail);
//			}
//		}
//	};
//
//	public static BiConsumer<Finance,ValidationContext> CHECK_FINANCE_AMOUNT_ZERO = (finance,ctx) -> {
//		if (AonMathUtils.isZero(finance.getAmount())) {
//			InvoiceErrorContext context = new InvoiceErrorContext(InvoiceErrorKey.FINANCE_AMOUNT_ZERO);
//			ctx.add( InvoiceErrorMessages.C014.wrn(context, InvoiceErrorKey.FINANCE_AMOUNT_ZERO.getDescription()));
//		}
//	};
//
//	public static BiConsumer<Finance,ValidationContext> CHECK_BANK_ACCOUNT = (finance,ctx) -> {
//		if (finance.getBankAccount() == null || AonStringUtils.isEmpty(finance.getBankAccount().getBban())) {
//			finance.setBankAccount(null);
//			finance.setBankAlias(null);
//			finance.setBic(null);
//		}
//		if (finance.getBankAccount() != null && !finance.getBankAccount().isValidBankAccount()) {
//			InvoiceErrorContext context = new InvoiceErrorContext(InvoiceErrorKey.FINANCE_WRONG_ACCOUNT_BANK);
//			ctx.add( InvoiceErrorMessages.C014.err(context, InvoiceErrorKey.FINANCE_WRONG_ACCOUNT_BANK.getDescription()));
//		}
//	};
//	
//	private static final Consumer<InvoicePreRecordContext> FINANCES_VALIDATION = c -> {
//		if (ctx.getResult().getAccountingInvoice() != null && ctx.getResult().getAccountingInvoice().getInvoice().getFinances() != null) {
//			for (Finance finance : ctx.getResult().getAccountingInvoice().getInvoice().getFinances()) {
//				CHECK_FINANCE_AMOUNT_ZERO
//				.andThen(CHECK_BANK_ACCOUNT)			
//			 	.accept(finance, ctx);
//			}
//		}
//	};
//
//	
//	/**
//	 * Si el año de la factura no es anterior en cinco años al actual.
//	 */
//	private static final Consumer<InvoicePreRecordContext> CHECK_FIVE_YEARS = c -> {
//		if (c.inv.getIssueDate() != null) {
//			int thisYear = AonDateUtils.getYear(new Date());
//			int invoiceYear = AonDateUtils.getYear(c.inv.getIssueDate());
//			if (invoiceYear < (thisYear - 5) || invoiceYear > (thisYear + 1)) {
//				ctx.add( InvoiceErrorMessages.C008.err(InvoiceErrorKey.ISSUE_DATE) );
//			}
//		}
//	};
//
//	private static final Consumer<InvoicePreRecordContext> CHECK_LINES = c -> {
//		if (c.inv.getDetails() == null || c.inv.getDetails().size() == 0) {
//			ctx.add( InvoiceErrorMessages.C010.err(InvoiceErrorKey.DETAILS) );
//		}
//	};
	
	private static boolean willOverflow(Field<String> field, String series) {
		return (AonStringUtils.length(series) > field.getDataType().length());
	}
	
}
