package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;

import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceSIIDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceValidation {
	
	private InvoiceValidation() {
		
	}

	private static record InvoiceValidationContext(AONContext ctx,AonConfiguration config,Invoice inv) {}
	private static record InvoiceDetailValidationContext(AONContext ctx,AonConfiguration config,InvoiceDetail det) {}
	
	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_DOMAIN = ivc -> {
		if (ivc.inv.getDomain() == 0) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	/**
	 * La fecha de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_DATE = ivc -> {
		if (ivc.inv.getIssueDate() == null)
			throw new AonCoreException(AonError.INVOICE_EMPTY_DATE.getMessage());
	};

	/**
	 * La fecha IVA de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_TAX_DATE = ivc -> {
		if (ivc.inv.getTaxDate() == null)
			throw new AonCoreException(AonError.INVOICE_EMPTY_TAX_DATE.getMessage());
	};

	/**
	 * El Tipo de la factura no puede ser null.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_INVOICE_TYPE = ivc -> {
		if (ivc.inv.getType() == null) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_TYPE.getMessage());
		}
	};
	
	/**
	 * El titular de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_INVOICE_REGISTRY = ivc -> {
		if (ivc.inv.getRegistry() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_REGISTRY.getMessage());
		}
	};
	
	/**
	 * El ámbito de la factura es un dato obligatorio.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_INVOICE_SCOPE = ivc -> {
		if (ivc.inv.getScope() == null || ivc.inv.getScope().getId() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_SCOPE.getMessage());
		}
	};
	
	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_REFERENCE_CODE = ivc -> {
		if (!ivc.inv.isSales() && AonStringUtils.isBlank( ivc.inv.getReferenceCode()) ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_REFERENCE_CODE.getMessage());
		}
	};

	/**
	 * Si la factura no es de ventas, el codigo de referencia debe tener valor.
	 */
	private static final Consumer<InvoiceValidationContext> EMPTY_TRANSACTION = ivc -> {
		if (ivc.inv.getTransaction() == null) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_TRANSACTION.getMessage());
		}
	};

	/**
	 * El Domain/Serie/Número/Tipo no puede estar duplicado
	 */
	private static final Consumer<InvoiceValidationContext> DUPLICATED_SERIES_NUMBER = ivc -> {
		if (ivc.ctx.getDslContext().fetchExists( 
				ivc.ctx.getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(ivc.inv.getDomain()))
					.and(AonStringUtils.isBlank(ivc.inv.getSeries())
						?INVOICE.SERIES.isNull().or(DSL.trim(INVOICE.SERIES).eq(""))
						:INVOICE.SERIES.eq(ivc.inv.getSeries()))
					.and(INVOICE.NUMBER.eq(ivc.inv.getNumber()))
					.and(ivc.inv.getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(ivc.inv.getId()))
					.and(INVOICE.TYPE.eq(ivc.inv.getType().value())))) {
			throw new AonCoreException(AonError.INVOICE_DUPLICATED_SERIES_NUMBER
					.format(ivc.inv.getDocumentNumber()));
		}
		
	};

	/**
	 * En facturas recibidas, el Domain/Registry/Numero Referencia no puede estar duplicado
	 */
	
	private static final Consumer<InvoiceValidationContext> DUPLICATED_REFERENCE_CODE = ivc -> {
		if (!ivc.inv.isSales() || AonStringUtils.isNotEmpty(ivc.inv.getReferenceCode())) {
			Condition invoiceTypeCondition = ivc.inv.isSales()
				?INVOICE.TYPE.eq(InvoiceType.SALES.value())
				:INVOICE.TYPE.ne(InvoiceType.SALES.value());
			Condition registryCondition = INVOICE.REGISTRY.eq(ivc.inv.getRegistry());
			if (!ivc.inv.isSales() && AonStringUtils.isNotEmpty(ivc.inv.getRegistryDocument())) {
				registryCondition = registryCondition.or(INVOICE.RDOCUMENT.eq(ivc.inv.getRegistryDocument()));
			}
			if (ivc.ctx.getDslContext().fetchExists( 
				ivc.ctx.getDslContext().selectOne()
					.from(INVOICE)
					.where(INVOICE.DOMAIN.eq(ivc.inv.getDomain()))
					.and(registryCondition)
					.and(INVOICE.REFERENCE_CODE.eq(ivc.inv.getReferenceCode()))
					.and(invoiceTypeCondition)
					.and(ivc.inv.getId() == null ? DSL.trueCondition() : INVOICE.ID.ne(ivc.inv.getId()))					
					.and(DSL.year(INVOICE.ISSUE_DATE).eq(AonDateUtils.getYear( ivc.inv.getIssueDate())))
				)) {
				throw new AonCoreException(AonError.INVOICE_DUPLICATED_REFERENCE_CODE.format(ivc.inv.getRegistry(),ivc.inv.getRegistryDocument(),ivc.inv.getReferenceCode()));
			}
		}
	};

	/**
	 * Si se ha indicado una fecha de límte de operaciones en los parámetros de la 
	 * empresa, debe ser anterior a la fecha de factura.
	 * 
	 */
	private static final Consumer<InvoiceValidationContext> OPERATIONS_DEADLINE = ivc -> {
		Date deadline = ivc.config.getOperationsDeadline();
		if (deadline != null && deadline.after(ivc.inv.getIssueDate())) 
			throw new AonCoreException(AonError.INVOICE_OPERATIONS_DEADLINE.getMessage());
	};
	
	/**
	 * Si el año de la factura no es anterior en diez años al actual. o posterior 2 años al actual.
	 */
	private static final Consumer<InvoiceValidationContext> CHECK_TEN_YEARS  = ivc -> {
		int thisYear = AonDateUtils.getYear(new Date());
		int invoiceYear = AonDateUtils.getYear(ivc.inv.getIssueDate());
		if (invoiceYear < (thisYear-10)) {
			throw new AonCoreException(AonError.INVOICE_TEN_YEARS.getMessage());
		}
		if(invoiceYear > (thisYear+1)) {
			throw new AonCoreException(AonError.INVOICE_TWO_YEARS.getMessage());
		}
	};
	
	/**
	 * Si existen vencimientos, las suma debe coincidir con el total factura.
	 * Sólo se realiza en la creación de la factura.
	 */
	private static final Consumer<InvoiceValidationContext> CHECK_FINANCES = ivc -> {
		if (ivc.inv.getFinances() != null && !ivc.inv.getFinances().isEmpty()) {
			double financesTotal = 0.0;
			for (Finance finance : ivc.inv.getFinances()) {
				if (!finance.isRemoved()) {
					financesTotal = AonMathUtils.round(financesTotal + finance.getAmount());
				}
			}
			if (!AonMathUtils.equals(ivc.inv.getTotal(), financesTotal )) {
				throw new AonCoreException(AonError.INVOICE_FINANCES_AMOUNT.getMessage());	
			}
		}
	};

	/**
	 * El origen de la linea de factura es un dato obligatorio.
	 */
	private static final Consumer<InvoiceDetailValidationContext> EMPTY_SOURCE = idvc -> {
		if (idvc.det.getSource() == null ) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_SOURCE.getMessage());
		}
	};

	/**
	 * El centro de trabajo es un dato obligatorio.
	 */
	private static final Consumer<InvoiceDetailValidationContext> EMPTY_WORKPLACE = idvc -> {
		if(idvc.det.getWorkplace() == null || idvc.det.getWorkplace().getId() == null) {
			throw new AonCoreException(AonError.INVOICE_EMPTY_WORKPLACE.getMessage());
		}
	};

	/**
	 * Las facturas rectificadas no se pueden borrar.
	 */
	private static final Consumer<InvoiceValidationContext> RECTIFIED_INVOICE = ivc -> {
		if (ivc.inv.isRectified() && ivc.inv.getRectificationInvoice() != null) {
			throw new AonCoreException(AonError.INVOICE_CANT_DELETE_RECTIFIED.getMessage());
		} else if(ivc.inv.isRectified()) {
			InvoiceDAO.getInvoiceStream(ivc.ctx, f -> f.getDomainProperty().eq(ivc.inv.getDomain())
				.and(f.getRectificationInvoiceProperty().eq(ivc.inv.getId()))).findFirst().ifPresent( i -> {
					throw new AonCoreException(AonError.INVOICE_CANT_DELETE_RECTIFIED.getMessage());
				});
		}
	};
	
	/**
	 * Las facturas rectificadas no se pueden anular.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_RECTIFIED_INVOICE = ivc -> {
		if (ivc.inv.isRectified() && ivc.inv.getRectificationInvoice() != null) {
			throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_RECTIFIED.getMessage());
		} else if(ivc.inv.isRectified()) {
			InvoiceDAO.getInvoiceStream(ivc.ctx, f -> f.getDomainProperty().eq(ivc.inv.getDomain())
				.and(f.getRectificationInvoiceProperty().eq(ivc.inv.getId()))).findFirst().ifPresent( i -> {
					throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_RECTIFIED.getMessage());
				});
		}
	};
	
	/**
	 * Las facturas contabilizadas no se pueden anular.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_RECORDED_INVOICE = ivc -> {
		if (ivc.inv.isRecorded()) {
			throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_RECORDED.getMessage());
		} 
	};
	
	
	/**
	 * Las facturas rectificadas no se pueden borrar.
	 */
	private static final Consumer<InvoiceValidationContext> DUA_LINKED_INVOICE = ivc -> {
		if (ivc.inv.isDUALinkAllowed() && 
			ivc.ctx.getDslContext().fetchExists( 
				ivc.ctx.getDslContext().selectOne()
					.from(INVOICE_DUA)
					.where(INVOICE_DUA.DOMAIN.eq(ivc.inv.getDomain()))
					.and(INVOICE_DUA.INVOICE_IMPORT.eq(ivc.inv.getId() )))) {
			throw new AonCoreException(AonError.INVOICE_CANT_DELETE_RECTIFIED.getMessage());
		}
	};
	
	/**
	 * Las facturas rectificadas no se pueden anular.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_DUA_LINKED_INVOICE = ivc -> {
		if (ivc.inv.isDUALinkAllowed() && 
			ivc.ctx.getDslContext().fetchExists( 
				ivc.ctx.getDslContext().selectOne()
					.from(INVOICE_DUA)
					.where(INVOICE_DUA.DOMAIN.eq(ivc.inv.getDomain()))
					.and(INVOICE_DUA.INVOICE_IMPORT.eq(ivc.inv.getId() )))) {
			throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_RECTIFIED.getMessage());
		}
	};
	
	/**
	 * Las facturas con vencimientos no pendientes no se pueden anular.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_FINANCE_NOT_PENDING = ivc -> {
		if (ivc.inv.getFinances() != null && !ivc.inv.getFinances().isEmpty()) {
			ivc.inv.getFinances().stream()
				.filter( f -> !f.isRemoved() && !f.isPending() )
				.findFirst()
				.ifPresent( f -> {
					throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_FINANCE.format(f.getDueDate(),f.getAmount()));
				});
		}		
	};
	
	/**
	 * La factura ha sido utilizada para los calculos de los modelos fiscales.
	 */
	private static final Consumer<InvoiceValidationContext> ALCATRAZ = ivc -> {
		if (ivc.inv.getId() != null) {
			List<FiscalModel> models = AlcatrazDAO.isInvoiceDeclared(ivc.ctx, ivc.inv.getId() );
			if (models != null && !models.isEmpty()) {
				if (ivc.inv.isSkipAlcatrazValidation()) {
					ApplicationParameter ap = new ApplicationParameter()
							.setDomain( ivc.inv.getDomain() )
							.setName( AppParam.FS_FORCE_DIFF_CALC )
							.setValue(  AonNumberUtils.toString(AonDateUtils.getYear( ivc.inv.getIssueDate())) );
					AppParamDAO.insertApplicationParameter( ivc.ctx, ap );
				} else {
					throw new AonCoreException(AonError.INVOICE_CANT_DELETE_MODEL.format(
						models.stream()
							.map( fm -> MessageFormat.format("[Mod. {0}] ",fm.getModelFullName()))
							.collect(StringBuilder::new, StringBuilder::append , StringBuilder::append )
							.toString()
							));
					
				}
			}
		}
	};
	
	/**
	 * La factura ha sido utilizada para los calculos de los modelos fiscales.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_ALCATRAZ = ivc -> {
		if (ivc.inv.getId() != null) {
			List<FiscalModel> models = AlcatrazDAO.isInvoiceDeclared(ivc.ctx, ivc.inv.getId() );
			if (models != null && !models.isEmpty()) {
				if (ivc.inv.isSkipAlcatrazValidation()) {
					ApplicationParameter ap = new ApplicationParameter()
							.setDomain( ivc.inv.getDomain() )
							.setName( AppParam.FS_FORCE_DIFF_CALC )
							.setValue(  AonNumberUtils.toString(AonDateUtils.getYear( ivc.inv.getIssueDate())) );
					AppParamDAO.insertApplicationParameter( ivc.ctx, ap );
				} else {
					throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_MODEL.format(
						models.stream()
							.map( fm -> MessageFormat.format("[Mod. {0}] ",fm.getModelFullName()))
							.collect(StringBuilder::new, StringBuilder::append , StringBuilder::append )
							.toString()
							));
					
				}
			}
		}
	};
	
	private static final Consumer<InvoiceValidationContext> RECTIFICATION_INVOICE_DATE = ivc -> {
		if (ivc.inv.getId() == null
			&& ivc.inv.getIssueDate() != null
			&& ivc.inv.isRectifier() 
			&& ivc.inv.getRectificationInvoice() != null) {
			ivc.ctx.getDslContext().select( INVOICE.ISSUE_DATE )
				.from(INVOICE)
				.where(INVOICE.ID.eq(ivc.inv.getRectificationInvoice()))
				.fetch()
				.stream()
				.map( r -> r.getValue(INVOICE.ISSUE_DATE) )
				.peek( sourceDate -> System.out.println( sourceDate 
						+ " ----- "
						+ ivc.inv.getIssueDate()
						+ " ----- "
						+  AonDateUtils.isBefore( ivc.inv.getIssueDate(), sourceDate )))
				.filter( sourceDate -> AonDateUtils.isBefore( ivc.inv.getIssueDate(), sourceDate ) )
				.findFirst()
				.ifPresent( r -> {
					throw new AonCoreException(AonError.INVOICE_INVALID_RECTIFICATION_DATE.getMessage() );
				});
		}
		
	};
	
	private static final Consumer<InvoiceValidationContext> RECTIFICATION_DATA = ivc -> {
		if (ivc.inv.getId() == null 
			&& ivc.inv.isRectifier() 
			&& ivc.inv.getRectificationInvoice() != null) {
			com.esferalia.aon.jooq.tables.Invoice invRect = INVOICE.as("INV_RECT");
			ivc.ctx.getDslContext().select( invRect.REFERENCE_CODE, invRect.ISSUE_DATE )
				.from(INVOICE)
				.join(invRect).on(invRect.ID.eq(INVOICE.RECTIFICATION_INVOICE))
				.where(INVOICE.ID.eq(ivc.inv.getRectificationInvoice()))
				.and(INVOICE.RECTIFICATION_TYPE.eq(RectificationType.RECTIFIED.value()))
				.fetch()
				.stream()
				.findFirst()
				.ifPresent( r -> {
					throw new AonCoreException(AonError.INVOICE_RECTIFIED_ALREADY_RECTIFIED.format(
						r.getValue(invRect.REFERENCE_CODE),r.getValue(invRect.ISSUE_DATE) ));
				});
		}
	};
	

	/**
	 * Las facturas enviadas al SII y que no se han dado de baja en el SII no se pueden borrar.
	 */
	private static final Consumer<InvoiceValidationContext> SII = ivc -> {
		Invoice a = InvoiceSIIDAO.getSiiInvoiceStream(ivc.ctx, f -> f.getIdProperty().eq(ivc.inv.getId()), false, true, true, false, false, "").findFirst().orElse(new Invoice());
		if(a.getId() != null) {
			throw new AonCoreException(AonError.INVOICE_CANT_DELETE_SII.getMessage());
		}
	};
	
	/**
	 * Las facturas enviadas al SII y que no se han dado de baja en el SII no se pueden ANULAR.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_SII = ivc -> {
		InvoiceInfoDAO.get(ivc.ctx, ivc.inv.getId(), InvoiceCommunicationType.SII).ifPresent( info -> {
			if (info.isAccepted() || info.isAcceptedWithErrors()) {
				throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_SII.getMessage());
			}
		});
	};
	
	/**
	 * Las facturas enviadas a Ticket Bai y que no se han dado de baja en Ticket Bai no se pueden borrar.
	 */
	private static final Consumer<InvoiceValidationContext> TBAI = ivc -> {
		InvoiceCommunicationConfiguration icc = ivc.config.getCommunicationConfig();
		if(icc.isLroe() && icc.isBizkaia()) {
			 DataResponse dr = DataResponseDAO.get(ivc.ctx, f -> f.getSourceProperty().eq(DataResponseSource.LROE.value())
					.and(f.getSourceIdProperty().eq(ivc.inv.getId())), new Options().setFull(true));
			 dr.getDetails().stream().filter(f -> f.getDataVariable().equals("info")).findFirst().ifPresent( d -> {
				 if(AonStringUtils.isBlank(d.getDataValue()) || !d.getDataValue().contains("\"operacion\":\"AN_0\"")) {
					 throw new AonCoreException(AonError.INVOICE_CANT_DELETE_TBAI.getMessage());
				 }
			 });	
		} 
		if (icc.isTbai() && (icc.isAraba() || icc.isGipuzkoa())) {
			DataResponse dr = DataResponseDAO.get(ivc.ctx, f -> f.getSourceProperty().eq(DataResponseSource.TBAI.value())
					.and(f.getSourceIdProperty().eq(ivc.inv.getId())), new Options().setFull(true));
			
			if(dr.getId() != null && !"baja".equalsIgnoreCase(dr.getCode())) {
				throw new AonCoreException(AonError.INVOICE_CANT_DELETE_TBAI.getMessage());	
			}
		}
	};
	
	/**
	 * Las facturas enviadas al TicketBAI y que no se han dado de baja en TicketBAI no se pueden ANULAR.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_TBAI = ivc -> {
		InvoiceInfoDAO.get(ivc.ctx, ivc.inv.getId(), InvoiceCommunicationType.TBAI).ifPresent( info -> {
			if (info.isAccepted() || info.isAcceptedWithErrors()) {
				throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_TBAI.getMessage());
			}
		});
		
		InvoiceInfoDAO.get(ivc.ctx, ivc.inv.getId(), InvoiceCommunicationType.LROE).ifPresent( info -> {
			if (info.isAccepted() || info.isAcceptedWithErrors()) {
				throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_TBAI.getMessage());
			}
		});
	};
	
	
	/**
	 * Las facturas enviadas a Verifactu y que no se han dado de baja en Verifactu no se pueden borrar.
	 */
	private static final Consumer<InvoiceValidationContext> VERIFACTU = ivc -> {
		InvoiceCommunicationConfiguration icc = ivc.config.getCommunicationConfig();
		if(icc.isVerifactu()) {
			boolean accepted = InvoiceInfoDAO.getMap(
					ivc.ctx
					, icc 
					, ivc.inv.getDomain()
					, ivc.inv.getId()
					, ivc.inv.getType()
					, ivc.inv.getExpDate()
				)
				.map( ic -> ic.get(InvoiceCommunicationType.VERIFACTU) )
				.filter( Objects::nonNull )
				.map(info -> info.isAccepted() || info.isAcceptedWithErrors())
				.orElse( true )
			;
			DataResponse dr = DataResponseDAO.get(ivc.ctx, f -> f.getSourceProperty().eq(DataResponseSource.VERIFACTU.value())
					.and(f.getSourceIdProperty().eq(ivc.inv.getId())), new Options().setFull(true));
			String type = dr.getDetails().stream().filter(f -> f.getDataVariable().equals("type")).map(DataResponseDetail::getDataValue).findFirst().orElse("alta");
			if(dr.getId() != null && "alta".equalsIgnoreCase(type) && accepted) {
				throw new AonCoreException(AonError.INVOICE_CANT_DELETE_VERIFACTU.getMessage());
			}
		}
	};

	/**
	 * Las facturas enviadas a Verifactu y que no se han dado de baja en Verifactu no se pueden ANULAR.
	 */
	private static final Consumer<InvoiceValidationContext> CANCEL_VERIFACTU = ivc -> {
		InvoiceInfoDAO.get(ivc.ctx, ivc.inv.getId(), InvoiceCommunicationType.VERIFACTU).ifPresent( info -> {
			if (info.isAccepted() || info.isAcceptedWithErrors()) {
				throw new AonCoreException(AonError.INVOICE_CANT_CANCEL_VERIFACTU.getMessage());
			}
		});
	};
	
	/**
	 * El dominio de la factura no puede estar vacio.
	 */
	private static final Consumer<InvoiceValidationContext> ISSUE_OPE_DATE_FUTURE = ivc -> {
		if ( AonDateUtils.isFuture( ivc.inv.getIssueDate() )   ) 
			throw new AonCoreException(AonError.INVOICE_EXP_DATE_BEFORE_DATE.getMessage());
	};
	
	private static final Consumer<InvoiceValidationContext> ISSUE_EXP_DATE_LIMIT = ivc -> {
		Date today = AonDateUtils.today();
		int day = AonDateUtils.getDay( today );
		if (day > 16) {
			Date currentMonthFirDay = AonDateUtils.getMonthFirstDay( today );
			if ( AonDateUtils.isBefore( ivc.inv.getIssueDate(), currentMonthFirDay ) ) {
				throw new AonCoreException(AonError.INVOICE_EXP_DATE_CURRENT_MONTH_LIMIT.getMessage());
			}
		} else {
			Date lastMonthFirstDay = Date.from( 
				today.toInstant()
                	.atZone(ZoneId.systemDefault())
                	.with(TemporalAdjusters.firstDayOfMonth())
                	.minusMonths(1)
                	.toLocalDate()
                	.atStartOfDay(ZoneId.systemDefault())
                	.toInstant()
		        );
			if ( AonDateUtils.isBefore( ivc.inv.getIssueDate(), lastMonthFirstDay)) {
				throw new AonCoreException(AonError.INVOICE_EXP_DATE_PAST_MONTH_LIMIT.getMessage());
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
			.andThen(RECTIFICATION_INVOICE_DATE)
			.andThen(RECTIFICATION_DATA)
			.accept(new InvoiceValidationContext(ctx,config,inv));

	}

	public static void validateUpdateSpecialInvoice(AONContext ctx,AonConfiguration config,Invoice inv) throws AonCoreException {
		EMPTY_DOMAIN
			.andThen(EMPTY_TAX_DATE)
			.andThen(OPERATIONS_DEADLINE)
			.andThen(CHECK_TEN_YEARS)
			.accept(new InvoiceValidationContext(ctx,config,inv));

	}

	public static void validateDetail(AONContext ctx, AonConfiguration config, InvoiceDetail detail) {
		EMPTY_SOURCE
		.andThen(EMPTY_WORKPLACE)
			.accept(new InvoiceDetailValidationContext(ctx,config,detail));
	}

	public static void validateInvoiceDeletion(AONContext ctx, AonConfiguration config, Invoice inv) {
		if (config == null) config = ConfigurationDAO.getConfiguration(ctx, inv.getIssueDate());
		RECTIFIED_INVOICE
		.andThen(DUA_LINKED_INVOICE)
		.andThen(OPERATIONS_DEADLINE)
		.andThen(SII)
		.andThen(TBAI)
		.andThen(VERIFACTU)
		.andThen(ALCATRAZ)
		.accept(new InvoiceValidationContext(ctx,config,inv));
	}

	public static void validateInvoiceCancellation(AONContext ctx, AonConfiguration config, Invoice inv) {
		if (config == null) config = ConfigurationDAO.getConfiguration(ctx, inv.getIssueDate());
		CANCEL_RECTIFIED_INVOICE
		.andThen(CANCEL_RECORDED_INVOICE)
		.andThen(CANCEL_FINANCE_NOT_PENDING)
		.andThen(CANCEL_DUA_LINKED_INVOICE)
		.andThen(OPERATIONS_DEADLINE)
		.andThen(CANCEL_SII)
		.andThen(CANCEL_TBAI)
		.andThen(CANCEL_VERIFACTU)
		.andThen(CANCEL_ALCATRAZ)
		.accept(new InvoiceValidationContext(ctx,config,inv));
	}
	
	public static void validatePreCommunicationInvoiceCancellation(AONContext ctx, AonConfiguration config, Invoice inv) {
		if (config == null) config = ConfigurationDAO.getConfiguration(ctx, inv.getIssueDate());
		CANCEL_RECTIFIED_INVOICE
		.andThen(CANCEL_RECORDED_INVOICE)
		.andThen(CANCEL_FINANCE_NOT_PENDING)
		.andThen(CANCEL_DUA_LINKED_INVOICE)
		.andThen(OPERATIONS_DEADLINE)
		.andThen(CANCEL_ALCATRAZ)
		.accept(new InvoiceValidationContext(ctx,config,inv));
	}
	
	public static void validateIssue(AONContext ctx, Invoice invoice) {
		if (invoice != null && invoice.isSales()) {
			ISSUE_OPE_DATE_FUTURE
				.andThen(ISSUE_EXP_DATE_LIMIT)
				.accept(new InvoiceValidationContext(ctx,null,invoice));
		}
	}

}
