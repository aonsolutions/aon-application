package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TbaiConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceValidation {
	
	private InvoiceValidation() {
		
	}

	public static class AonConfigurationContext {
		
		private AONContext ctx;
		private AonConfiguration config;
		
		public AonConfigurationContext (AONContext ctx,AonConfiguration config) {
			this.ctx = ctx;
			this.config = config;
		}
	
		private AONContext getContext() {
			return ctx;
		}
		
		private AonConfiguration getConfiguration() {
			return config;
		}
	}
	
	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_DOMAIN = (inv,ctx) -> {
		if (inv.getDomain() == 0) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_DATE = (inv,ctx) -> {
		if (inv.getIssueDate() == null)
			throw new AonCoreException(AonError.INVOICE_EMPTY_DATE.getMessage());
	};

	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_TAX_DATE = (inv,ctx) -> {
		if (inv.getTaxDate() == null)
			throw new AonCoreException(AonError.INVOICE_EMPTY_TAX_DATE.getMessage());
	};

	/**
	 * El Tipo de la factura no puede ser null.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_INVOICE_TYPE = (inv,ctx) -> {
		if (inv.getType() == null) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_TYPE.getMessage());
		}
	};
	
	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_INVOICE_REGISTRY = (inv,ctx) -> {
		if (inv.getRegistry() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_REGISTRY.getMessage());
		}
	};
	
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_INVOICE_SCOPE = (inv,ctx) -> {
		if (inv.getScope() == null || inv.getScope().getId() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_SCOPE.getMessage());
		}
	};
	
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_REFERENCE_CODE = (inv,ctx) -> {
		if (!inv.isSales() && AonStringUtils.isBlank( inv.getReferenceCode()) ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_REFERENCE_CODE.getMessage());
		}
	};

	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> EMPTY_TRANSACTION = (inv,ctx) -> {
		if (inv.getTransaction() == null) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_TRANSACTION.getMessage());
		}
	};

	/**
	 * El Domain/Serie/Número/Tipo no puede estar duplicado
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> DUPLICATED_SERIES_NUMBER = (inv,ctx) -> {
		if (ctx.getContext().getDslContext().fetchExists( 
				ctx.getContext().getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(inv.getDomain()))
					.and(AonStringUtils.isBlank(inv.getSeries())
						?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
						:INVOICE.SERIES.eq(inv.getSeries()))
					.and(INVOICE.NUMBER.eq(inv.getNumber()))
					.and(inv.getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(inv.getId()))
					.and(INVOICE.TYPE.eq(inv.getType().value())))) {
			throw new AonCoreException(AonError.INVOICE_DUPLICATED_SERIES_NUMBER.getMessage() 
				+ "["+ (AonStringUtils.isBlank(inv.getSeries())? "" : (inv.getSeries() + "/") + inv.getNumber()) +"]");
		}
	};

	/**
	 * En facturas recibidas, el Domain/Registry/Numero Referencia no puede estar duplicado
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> DUPLICATED_REFERENCE_CODE = (inv,ctx) -> {
		if (!inv.isSales() || (inv.getReferenceCode() != null && !"".equals(inv.getReferenceCode()))) {
			if (ctx.getContext().getDslContext().fetchExists( 
					ctx.getContext().getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(inv.getDomain()))
					.and(INVOICE.REGISTRY.eq(inv.getRegistry()))
					.and(INVOICE.REFERENCE_CODE.eq(inv.getReferenceCode()))
					.and(INVOICE.TYPE.eq(inv.getType().value()))
					.and(inv.getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(inv.getId()))					
					.and(DSL.year(INVOICE.ISSUE_DATE).eq(AonDateUtils.getYear( inv.getIssueDate())))
				)) {
				throw new AonCoreException("[Factura "+ inv.getReferenceCode()+ "] " + AonError.INVOICE_DUPLICATED_REFERENCE_CODE.getMessage());
			}
		}
	};

	/**
	 * Si se ha indicado una fecha de límte de operaciones en los parámetros de la 
	 * empresa, debe ser anterior a la fecha de factura.
	 * 
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> OPERATIONS_DEADLINE = (inv,ctx) -> {
		Date deadline = ctx.getConfiguration().getOperationsDeadline();
		if (deadline != null && deadline.after(inv.getIssueDate())) 
			throw new AonCoreException(AonError.INVOICE_OPERATIONS_DEADLINE.getMessage());
	};
	
	/**
	 * Si el año de la factura no es anterior en diez años al actual.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> CHECK_TEN_YEARS  = (inv,ctx) -> {
		int thisYear = AonDateUtils.getYear(new Date());
		int invoiceYear = AonDateUtils.getYear(inv.getIssueDate());
		if (invoiceYear < (thisYear-10) || invoiceYear > (thisYear+1)) {
			throw new AonCoreException(AonError.INVOICE_TEN_YEARS.getMessage());
		}
	};
	
	/**
	 * Si existen vencimientos, las suma debe coincidir con el total factura.
	 * Sólo se realiza en la creación de la factura.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> CHECK_FINANCES = (inv,ctx) -> {
		if (inv.getFinances() != null && !inv.getFinances().isEmpty()) {
			double financesTotal = 0.0;
			for (Finance finance : inv.getFinances()) {
				if (!finance.isRemoved()) {
					financesTotal = AonMathUtils.round(financesTotal + finance.getAmount());
				}
			}
			if (!AonMathUtils.equals(inv.getTotal(), financesTotal )) {
				throw new AonCoreException(AonError.INVOICE_FINANCES_AMOUNT.getMessage());	
			}
		}
	};

	/**
	 * El origen de la linea de factura es un dato obligatorio.
	 */
	public static final BiConsumer<InvoiceDetail,AonConfigurationContext> EMPTY_SOURCE = (det,ctx) -> {
		if (det.getSource() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_SOURCE.getMessage());
		}
	};

	/**
	 * El centro de trabajo es un dato obligatorio.
	 */
	public static final BiConsumer<InvoiceDetail,AonConfigurationContext> EMPTY_WORKPLACE = (det,ctx) -> {
		if (det.getWorkPlace() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_WORKPLACE.getMessage());
		}
	};

	/**
	 * Las facturas rectificadas no se pueden borrar.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> RECTIFIED_INVOICE = (inv,ctx) -> {
		if (inv.isRectified()) 
			throw new AonCoreException(AonError.INVOICE_CANT_DELETE_RECTIFIED.getMessage());
	};

	/**
	 * Las facturas rectificadas no se pueden borrar.
	 */
	public static final BiConsumer<Invoice,AonConfigurationContext> DUA_LINKED_INVOICE = (inv,ctx) -> {
		if (inv.isDUALinkAllowed() && 
			ctx.getContext().getDslContext().fetchExists( 
				ctx.getContext().getDslContext().selectOne()
					.from(INVOICE_DUA)
					.where(INVOICE_DUA.DOMAIN.eq(inv.getDomain()))
					.and(INVOICE_DUA.INVOICE_IMPORT.eq(inv.getId() )))) {
			throw new AonCoreException(AonError.INVOICE_CANT_DELETE_RECTIFIED.getMessage());
		}
	};
	
	/**
	 * La factura ha sido utilizada para los calculos de los modelos fiscales.
	 */
	public static final BiConsumer<Invoice, AonConfigurationContext> ALCATRAZ = (inv,ctx) -> {
		if (inv.getId() != null) {
			List<FiscalModel> models = AlcatrazDAO.isInvoiceDeclared(ctx.getContext(), inv.getId() );
			if (models != null && !models.isEmpty()) {
				throw new AonCoreException(AonError.INVOICE_CANT_DELETE_MODEL.format(
						models
						.stream()
						.map( fm -> MessageFormat.format("[Mod. {0}] ",fm.getModelFullName()))
						.collect(StringBuilder::new, StringBuilder::append , StringBuilder::append )
						.toString()
						));
			}
		}
	};

	/**
	 * Las facturas enviadas al SII y que no se han dado de baja en el SII no se pueden borrar.
	 */
	public static final BiConsumer<Invoice, AonConfigurationContext> SII = (inv,ctx) -> {
		Invoice a = InvoiceDAO.getSiiInvoiceStream(ctx.getContext(), f -> f.getIdProperty().eq(inv.getId()), false, true, true, false, false, "").findFirst().orElse(new Invoice());
		if(a.getId() != null) {
			throw new AonCoreException(AonError.INVOICE_CANT_DELETE_SII.getMessage());
		}
	};
	
	/**
	 * Las facturas enviadas a Ticket Bai y que no se han dado de baja en Ticket Bai no se pueden borrar.
	 */
	public static final BiConsumer<Invoice, AonConfigurationContext> TBAI = (inv,ctx) -> {
		TbaiConfiguration tbai = TbaiConfigurationDAO.get(ctx.getContext());
		if(tbai.isActive()) {
			boolean accepted = true;
			if(tbai.isBizkaia()) {
				InvoiceInfo info = InvoiceInfoDAO.get(ctx.getContext(), f -> f.getInvoiceProperty().eq(inv.getId())
						.and(f.getTypeProperty().eq(InvoiceCommunicationType.LROE_1_1.value())));
				accepted = info.isAccepted() || info.isAcceptedWithErrors();
			}
			
			DataResponse dr = DataResponseDAO.get(ctx.getContext(), f -> f.getSourceProperty().eq(DataResponseSource.TBAI.value())
					.and(f.getSourceIdProperty().eq(inv.getId())), new Options().setFull(true));
			String type = dr.getDetails().stream().filter(f -> f.getDataVariable().equals("type")).map(DataResponseDetail::getDataValue).findFirst().orElse("alta");
			if(dr.getId() != null && "alta".equalsIgnoreCase(type) && accepted) {
				throw new AonCoreException(AonError.INVOICE_CANT_DELETE_TBAI.getMessage());
			}
		}
	};

	public static void validateInvoice(AONContext ctx,AonConfiguration config,Invoice inv) throws AonCoreException {
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
			.andThen(ALCATRAZ)
			.accept(inv, new AonConfigurationContext(ctx,config));

	}

	public static void validateUpdateSpecialInvoice(AONContext ctx,AonConfiguration config,Invoice inv) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_TAX_DATE)
			.andThen(OPERATIONS_DEADLINE)
			.andThen(CHECK_TEN_YEARS)
			.accept(inv, new AonConfigurationContext(ctx,config));

	}

	public static void validateDetail(AONContext ctx, AonConfiguration config, InvoiceDetail detail) {
		EMPTY_SOURCE
		.andThen(EMPTY_WORKPLACE)
			.accept(detail, new AonConfigurationContext(ctx,config));
	}

	public static void validateInvoiceDeletion(AONContext ctx, AonConfiguration config, Invoice inv) {
		if (config == null) config = ConfigurationDAO.getConfiguration(ctx, inv.getIssueDate());
		RECTIFIED_INVOICE
		.andThen(DUA_LINKED_INVOICE)
		.andThen(OPERATIONS_DEADLINE)
		.andThen(SII)
		.andThen(TBAI)
		.andThen(ALCATRAZ)
		.accept(inv, new AonConfigurationContext(ctx,config));
	}

}
