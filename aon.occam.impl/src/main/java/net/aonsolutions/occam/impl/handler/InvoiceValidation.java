package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.impl.DSL;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.FiscalModel;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceErrorMessages;
import net.aonsolutions.occam.api.model.InvoiceException;
import net.aonsolutions.occam.api.model.type.InvoiceErrorKey;
import net.aonsolutions.occam.impl.AONContext;

public class InvoiceValidation {
	
	private InvoiceValidation() {
		
	}

	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_DOMAIN = (ctx,inv) -> {
		if (inv.getDomain() == null || inv.getDomain() == 0) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.DOMAIN) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};
	
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_DATE = (ctx,inv) -> {
		if (inv.getHeader().getIssueDate() == null) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.ISSUE_DATE) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};

	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_TAX_DATE = (ctx,inv) -> {
		if (inv.getHeader().getTaxDate() == null) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.TAX_DATE) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};

	/**
	 * El Tipo de la factura no puede ser null.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_INVOICE_TYPE = (ctx,inv) -> {
		if (inv.getHeader().getType() == null) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.TYPE) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};
	
	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_INVOICE_REGISTRY = (ctx,inv) -> {
		if (inv.getHeader().getRegistry() == null ) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.REGISTRY) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};
	
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_INVOICE_SCOPE = (ctx,inv) -> {
		if (inv.getHeader().getScope() == null) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.SCOPE) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};
	
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_REFERENCE_CODE = (ctx,inv) -> {
		if (!inv.getHeader().isSales() && AonStringUtils.isBlank( inv.getHeader().getReferenceCode()) ) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.REFERENCE_CODE) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};

	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	private static final BiConsumer<AONContext,Invoice> EMPTY_TRANSACTION = (ctx,inv) -> {
		if (inv.getHeader().getTransaction() == null) {
			inv.addMessage( InvoiceErrorMessages.C001.err(InvoiceErrorKey.TRANSACTION) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};

	/**
	 * El Domain/Serie/Número/Tipo no puede estar duplicado
	 */
	private static final BiConsumer<AONContext,Invoice> DUPLICATED_SERIES_NUMBER = (ctx,inv) -> {
		if (ctx.getDslContext().fetchExists( 
				ctx.getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(inv.getHeader().getDomain()))
					.and(AonStringUtils.isBlank(inv.getHeader().getSeries())
						?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
						:INVOICE.SERIES.eq(inv.getHeader().getSeries()))
					.and(INVOICE.NUMBER.eq(inv.getHeader().getNumber()))
					.and(inv.getHeader().getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(inv.getId()))
					.and(INVOICE.TYPE.eq(inv.getHeader().getType().value())))) {
			inv.addMessage( InvoiceErrorMessages.C005.err(InvoiceErrorKey.DUPLICATED_SERIES_NUMBER) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};

	/**
	 * En facturas recibidas, el Domain/Registry/Numero Referencia no puede estar duplicado
	 */
	private static final BiConsumer<AONContext,Invoice> DUPLICATED_REFERENCE_CODE = (ctx,inv) -> {
		if (inv.getHeader().isNotSales() && 
			ctx.getDslContext().fetchExists( 
				ctx.getDslContext().selectOne()
				.from(INVOICE)
				.where(INVOICE.DOMAIN.eq(inv.getHeader().getDomain()))
				.and(INVOICE.REGISTRY.eq(inv.getHeader().getRegistry()))
				.and(INVOICE.REFERENCE_CODE.eq(inv.getHeader().getReferenceCode()))
				.and(INVOICE.TYPE.eq(inv.getHeader().getType().value()))
				.and(inv.getHeader().getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(inv.getHeader().getId()))					
				.and(DSL.year(INVOICE.ISSUE_DATE).eq(AonDateUtils.getYear( inv.getHeader().getIssueDate())))
			)
		) {
			inv.addMessage( InvoiceErrorMessages.C006.err(InvoiceErrorKey.DUPLICATED_REFERENCE_CODE) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};

	/**
	 * Si se ha indicado una fecha de límte de operaciones en los parámetros de la 
	 * empresa, debe ser anterior a la fecha de factura.
	 * 
	 */
	private static final BiConsumer<AONContext,Invoice> OPERATIONS_DEADLINE = (ctx,inv) -> {
		ctx.getApplicationParameters(inv.getHeader().getDomain()).getOperationsDeadline()
			.ifPresent( deadline -> {
				if (AonDateUtils.isAfter(deadline, inv.getHeader().getIssueDate())) {
					inv.addMessage( InvoiceErrorMessages.C007.err(InvoiceErrorKey.ISSUE_DATE) );
					throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
				}
			} 
		);
	};
	
	/**
	 * Si el año de la factura no es anterior en diez años al actual.
	 */
	private static final BiConsumer<AONContext,Invoice> CHECK_TEN_YEARS  = (ctx,inv) -> {
		int thisYear = AonDateUtils.getYear(new Date());
		int invoiceYear = AonDateUtils.getYear(inv.getHeader().getIssueDate());
		if (invoiceYear < (thisYear-10) || invoiceYear > (thisYear+1)) {
			inv.addMessage( InvoiceErrorMessages.C008.err(InvoiceErrorKey.ISSUE_DATE) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
		}
	};
	
	/**
	 * Si existen vencimientos, las suma debe coincidir con el total factura.
	 * Sólo se realiza en la creación de la factura.
	 */
	private static final BiConsumer<AONContext,Invoice> CHECK_FINANCES = (ctx,inv) -> {
		if (inv.getFinancesSize() > 0) {
			double financesTotal = AonMathUtils.round( 
				inv.financeStream()
					.filter( f -> f.isNotDeleted())
					.mapToDouble( f -> f.getAmount() )
					.sum()
			);
			if (!AonMathUtils.equals(inv.getHeader().getTotal(), financesTotal )) {
				inv.addMessage( InvoiceErrorMessages.C020.err(InvoiceErrorKey.FINANCE_TOTAL_AMOUNT) );
				throw new InvoiceException(inv,AonError.INVOICE_SAVE_ERROR.getMessage());
			}
		}
	};

	/**
	 * Las facturas rectificadas no se pueden borrar.
	 */
	private static final BiConsumer<AONContext,Invoice> RECTIFIED_INVOICE = (ctx,inv) -> {
		if (inv.getHeader().isRectified()) {
			inv.addMessage( InvoiceErrorMessages.C050.err( InvoiceErrorKey.GENERIC) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_DELETE_ERROR.getMessage());
		}
	};

	/**
	 * Las facturas rectificadas no se pueden borrar.
	 */
	private static final BiConsumer<AONContext,Invoice> DUA_LINKED_INVOICE = (ctx,inv) -> {
		if (inv.getHeader().isDUALinkAllowed() && 
			ctx.getDslContext().fetchExists( 
				ctx.getDslContext().selectOne()
					.from(INVOICE_DUA)
					.where(INVOICE_DUA.DOMAIN.eq(inv.getDomain()))
					.and(INVOICE_DUA.INVOICE_IMPORT.eq(inv.getId() )))) {
			inv.addMessage( InvoiceErrorMessages.C051.err( InvoiceErrorKey.GENERIC) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_DELETE_ERROR.getMessage());
		}
	};
	
	/**
	 * La factura ha sido utilizada para los calculos de los modelos fiscales.
	 */
	private static final BiConsumer<AONContext,Invoice> ALCATRAZ_MODEL = (ctx,inv) -> {
		if (inv.getId() != null) {
			List<FiscalModel> models = AlcatrazHandler.isInvoiceDeclared(ctx, inv.getId() );
			if (models != null && !models.isEmpty()) {
				String message = models
					.stream()
					.map( fm -> MessageFormat.format("[Mod. {0}] ",fm.getModelFullName()))
					.collect(StringBuilder::new, StringBuilder::append , StringBuilder::append )
					.toString(); 
				inv.addMessage( InvoiceErrorMessages.C052.err( InvoiceErrorKey.GENERIC, message ) );
				throw new InvoiceException(inv,AonError.INVOICE_SAVE_DELETE_ERROR.format( message ));
			}
		}
	};

	/**
	 * La factura ha sido bloqueada de alguna forma en ALCATRAZ
	 */
	private static final BiConsumer<AONContext,Invoice> ALCATRAZ = (ctx,inv) -> {
		if (inv.getId() != null && AlcatrazHandler.isInvoiceAlcatrazed(ctx, inv.getId() )) {
			inv.addMessage( InvoiceErrorMessages.C056.err( InvoiceErrorKey.GENERIC) );
			throw new InvoiceException(inv,AonError.INVOICE_SAVE_DELETE_ERROR.getMessage());
		}
	};

//	/**
//	 * Las facturas enviadas al SII y que no se han dado de baja en el SII no se pueden borrar.
//	 */
//	private static final BiConsumer<AONContext,Invoice> SII = (ctx,inv) -> {
//		if (InvoiceSIIDAO.getSiiInvoiceStream(ctx, f -> f.getIdProperty().eq(inv.getId()), false, true, true, false, false, "")
//			.findFirst()
//			.isPresent()) {
//			
//			inv.addMessage( InvoiceErrorMessages.C052.err( InvoiceErrorKey.GENERIC) );
//			throw new InvoiceException(inv,AonError.INVOICE_SAVE_DELETE_ERROR.getMessage());
//		}
//	};
//	
//	/**
//	 * Las facturas enviadas a Ticket Bai y que no se han dado de baja en Ticket Bai no se pueden borrar.
//	 */
//	private static final BiConsumer<AONContext,Invoice> TBAI = (ctx,inv) -> {
//		TbaiConfiguration tbai = TbaiConfigurationDAO.get(ctx);
//		if(tbai.isActive()) {
//			boolean accepted = true;
//			if(tbai.isBizkaia()) {
//				InvoiceInfo info = InvoiceInfoDAO.get(ctx, f -> f.getInvoiceProperty().eq(inv.getId())
//						.and(f.getTypeProperty().eq(InvoiceCommunicationType.LROE.value())));
//				accepted = info.isAccepted() || info.isAcceptedWithErrors();
//			}
//			
//			DataResponse dr = DataResponseDAO.get(ctx, f -> f.getSourceProperty().eq(DataResponseSource.TBAI.value())
//					.and(f.getSourceIdProperty().eq(inv.getId())), new Options().setFull(true));
//			String type = dr.getDetails().stream().filter(f -> f.getDataVariable().equals("type")).map(DataResponseDetail::getDataValue).findFirst().orElse("alta");
//			if(dr.getId() != null && "alta".equalsIgnoreCase(type) && accepted) {
//				inv.addMessage( InvoiceErrorMessages.C053.err( InvoiceErrorKey.GENERIC) );
//				throw new InvoiceException(inv,AonError.INVOICE_SAVE_DELETE_ERROR.getMessage());
//			}
//		}
//	};

	static void validate(AONContext ctx, Invoice inv) throws InvoiceException {
		EMPTY_DOMAIN
		.andThen(EMPTY_DATE)
		.andThen(EMPTY_TAX_DATE)
		.andThen(EMPTY_INVOICE_TYPE)
		.andThen(EMPTY_INVOICE_REGISTRY)
		.andThen(EMPTY_INVOICE_SCOPE)
		.andThen(EMPTY_REFERENCE_CODE)
		.andThen(EMPTY_TRANSACTION)
		.andThen(DUPLICATED_SERIES_NUMBER)
		
		.andThen(DUPLICATED_REFERENCE_CODE)
		.andThen(OPERATIONS_DEADLINE)
		.andThen(CHECK_TEN_YEARS)
		.andThen(CHECK_FINANCES)
		.andThen(ALCATRAZ_MODEL)
		.andThen(ALCATRAZ)
		.accept(ctx,inv);
	}

	static void validateDeletion(AONContext ctx, Invoice inv) {
		RECTIFIED_INVOICE
		.andThen(DUA_LINKED_INVOICE)
		.andThen(OPERATIONS_DEADLINE)
//		.andThen(SII)
//		.andThen(TBAI)
		.andThen(ALCATRAZ)
		.accept(ctx,inv);
	}

}
