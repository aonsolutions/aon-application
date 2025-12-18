package net.aonsolutions.aon.invoice.communication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.exception.DataAccessException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.JsonUtils.JSONArrayCollector;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleLogger;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.fee.FeeBillingDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.TBAIInformation;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.verifactu.NOVERIFACTU;
import net.aonsolutions.aon.verifactu.VERIFACTU;

public class InvoiceCommunicator {
	
	private static final String IC = "IC";
	private static final Logger LOGGER = Logger.getLogger(InvoiceCommunicator.class.getName());
	
	private InvoiceCommunicator() {
	}
	
	private static void log( String message ) {
		LOGGER.info(message);
	}
	
	private abstract static class AonAbstractPhaseListener extends InvoiceCommunicationPhaseListener {
		@Override
		public void beforeAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {
			// Nothing
		}
		@Override
		public void afterAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {
			if (icc.isFailOnWrongValidation() 
			 && icc.invoiceStream().filter( Invoice::hasMessages ).anyMatch( Invoice::hasERRMessages )) {
				// TRACE _-- borrar
				icc.invoiceStream()
					.filter( Invoice::hasMessages )
					.flatMap( Invoice::messageStream )
					.forEach( m -> System.out.println( m.getLevel() + " " + m.getCode() + " - " + m.getMessage() ));
				// ----------------
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0024);
			}
		}
	}
	
	// *************************************************************
	// ******************************************* [HISTORY] *******
	// *************************************************************
	public static HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(Occam occam, Integer invoiceId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return history(ctx, occam.getDomain(), invoiceId);
		}
	}
	
	public static HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(AONContext ctx, final Integer domainId, final Integer invoiceId ) {
		List<InvoiceCommunicationHistory> history = InvoiceCommunicationDAO.getHistory(ctx, invoiceId, t -> {
			try {
				t.getType().visit( new InvoiceCommunicationTypeVisitor() {
					@Override public void visitSERES() throws InvoiceCommunicationException 	{ /*Nothing*/ }
					@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ /*Nothing*/ }
					@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ /*Nothing*/ }
					@Override public void visitSII() throws InvoiceCommunicationException 		{ /*Nothing*/ }
					@Override public void visitLROE() throws InvoiceCommunicationException 		{ /*Nothing*/ }
					@Override public void visitTBAI() throws InvoiceCommunicationException 		{ /*Nothing*/ }
					
					@Override
					public void visitVERIFACTU() throws InvoiceCommunicationException  {
						if (t.getResponseData() != null && t.getResponseData().length > 0) {
							t.setResponseMessages( VERIFACTU.history(t.getResponseData(), invoiceId) );
						}
					}
					
					@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0028); }
					@Override public void visitSIF() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0029); }
					@Override public void visitFACTURAE() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0030);}
				});
			} catch (Exception e) {
				e.printStackTrace();
				LinkedList<String> errorMessages = new LinkedList<>();
				errorMessages.add("Error al interpretar la respuesta: " + e.getMessage() );
				return errorMessages;						
			}
			return t.getResponseMessages();
		});
		
		HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> map = new HashMap<>();
		AonCollectionUtils.stream( InvoiceInfoDAO.getMap(ctx, domainId, invoiceId).orElse(null) )
			.forEach( e -> map.put(e.getKey(), new InvoiceCommunicationHistoryMapValue().setInfo(e.getValue())));
		AonCollectionUtils.stream(history)
			.forEach(t -> {
				InvoiceCommunicationHistoryMapValue v = map.computeIfAbsent(t.getType(), 
					k -> {
						InvoiceInfo info = new InvoiceInfo()
							.setDomain(domainId) 
							.setInvoice(invoiceId)
							.setType(k)
							.setStatus(t.getStatus())
							.setCreationUser(t.getCreationUser())
							.setCreationDate(t.getDate());
						InvoiceInfoDAO.InvoiceInfoURLFiller.build(ctx, info);
						return new InvoiceCommunicationHistoryMapValue().setInfo( info);
					}
				);
				v.add(t);
			})
		;
		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, domainId);
		if (config.isTbai() && !config.isBizkaia()) {	
			TbaiData.getInstance(config).get(ctx, domainId, invoiceId)
				.ifPresent(tbaiInfo -> addTBAI(ctx, map, tbaiInfo, domainId, invoiceId));
		}  
		return map; 
	}
	
	public static Optional<JSONObject> historyToJSON(Map<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history) {
		if (AonCollectionUtils.isEmpty(history)) return Optional.empty();
		JSONObject json = new JSONObject();
		AonCollectionUtils.stream(history)
			.forEach(e -> {
				InvoiceCommunicationHistoryMapValue v = e.getValue();
				InvoiceInfo info = v.getInfo();
				JSONObject mapJSON = new JSONObject();
				JSONObject infoJSON = new JSONObject()
					.put(IJsonNames.COMMUNICATION_TYPE, InvoiceCommunicationType.name(info.getType()))
					.put(IJsonNames.COMMUNICATION_STATUS, InvoiceCommunicationStatus.name(info.getStatus()))
					.put(IJsonNames.CREATION_USER, info.getCreationUser())
					.put(IJsonNames.CREATION_DATE, JsonUtils.getDateTimeJSON(info.getCreationDate()))
					.put(IJsonNames.MODIFICATION_USER, info.getModificationUser())
					.put(IJsonNames.MODIFICATION_DATE, JsonUtils.getDateTimeJSON(info.getModificationDate()))
					.put(IJsonNames.CHECK_URL, info.getCheckUrl())
				;
				mapJSON.put( IJsonNames.COMMUNICATION_INFO, infoJSON);
				JSONArray historyArray = AonCollectionUtils.stream(v.getHistory())		
					.map( h -> new JSONObject() 
						.put(IJsonNames.DATE, JsonUtils.getDateTimeJSON(h.getDate()))
						.put(IJsonNames.CREATION_USER, h.getCreationUser())
						.put(IJsonNames.OPERATION, h.getOperation().getDescription())
						.put(IJsonNames.REQUEST_URL, h.getRequestUrl())
						.put(IJsonNames.RESPONSE_URL, h.getResponseUrl())
						.put(IJsonNames.RESPONSE_MESSAGES, 
							AonCollectionUtils.stream(h.getResponseMessages())
							.filter(AonStringUtils::isNotBlank)
							.collect(JSONArrayCollector.toJSONArray())
							)
						.put(IJsonNames.STATUS, InvoiceCommunicationStatus.name(h.getStatus()))
						.put(IJsonNames.TYPE, InvoiceCommunicationType.name(h.getType()))
				)
				.collect( JSONArray::new, JSONArray::put, JSONArray::putAll );
				mapJSON.put( IJsonNames.COMMUNICATION_HISTORY, historyArray );
				json.put( e.getKey().name(), mapJSON );
			})
		;
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.ofNullable(json);
	}
	
	// *************************************************************
	// ******************************************* [ISSUE] ********
	// *************************************************************
	public static InvoiceCommunicatorContext issueInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(trx -> 
				issue(ctx, communicator, new AonIssuePhaseListener()));
		}
	}
	
	private static class AonIssuePhaseListener extends AonAbstractPhaseListener {

		@Override
		public void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			setOldNumber( invoice.getNumber() );
			setProforma( invoice.isProforma() );
			if (invoice.isProforma()) {
				InvoiceDAO.preIssue(ctx, invoice);
			}
		}
		
		@Override
		public void afterRightInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			if (isProforma()) {
				InvoiceDAO.postIssue(ctx, invoice);
			}
		}
		
		@Override
		public void afterWrongInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) throws InvoiceCommunicationException{
			if (isProforma()) {
				// Revertir el número si es proforma y la validación falló, puesto que no se va a comunicar.
				invoice.setNumber( getOldNumber() );
				invoice.setReferenceCode( FinanceUtil.getSalesReferenceCode(invoice) );
			}
		}
		
	}

	private static InvoiceCommunicatorContext issue(AONContext ctx, final InvoiceCommunicatorContext cc, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		try {
			check(ctx, cc);
			checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));
			if(invoice.isSales()) {
				if (cc.getConfig().hasCommunication() ) {
					for ( InvoiceCommunicationType type : cc.getConfig().getTypes()) {
						type.visit( new InvoiceCommunicationTypeVisitor() {
							
							@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
							@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
							@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
							@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
							@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
							@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }
							
							@Override
							public void visitVERIFACTU() throws InvoiceCommunicationException  {
								VERIFACTU.accept(ctx,cc, phase);
							}
							
							@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0031); }
							@Override public void visitSIF() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0032); }
							@Override public void visitFACTURAE() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0033);}
						});
					}
				}
			}
			return cc;
		} catch (Exception e) {
			if (e instanceof InvoiceCommunicationException ice) {
				throw ice;
			}
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9000, e);
		}
	}
	
	// *************************************************************
	// ******************************************* [ACCEPT] ********
	// *************************************************************
	public static InvoiceCommunicatorContext acceptInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				accept(ctx, communicator, new AonAcceptPhaseListener()));
		}
	}
	
	private static class AonAcceptPhaseListener extends AonAbstractPhaseListener {

		@Override
		public void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			InvoiceDAO.accept(ctx, invoice, invoice.getRawdocId().orElse(null));
			// TODO Cambiarlo cuando accept2 sea viable para la pantalla del portal.
			// invoice = InvoiceDAO.accept2(ctx, invoice, cc.getRawdocId());
		}
		@Override
		public void afterRightInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			// Nothing
		}
		
		@Override
		public void afterWrongInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) throws InvoiceCommunicationException{
			invoice.setId( 0 );
			invoice.setNumber( 0 );
			invoice.setReferenceCode( FinanceUtil.getSalesReferenceCode(invoice) );
		}
	}
	
	private static InvoiceCommunicatorContext accept(AONContext ctx, final InvoiceCommunicatorContext cc, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		try {
			check(ctx, cc);
			checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));

			if(invoice.isSales() && cc.getConfig().hasCommunication() ) {
				for ( InvoiceCommunicationType type : cc.getConfig().getTypes()) {
					type.visit( new InvoiceCommunicationTypeVisitor() {
						
						@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
						@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
						@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
						@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
						@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
						@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }
						@Override public void visitFACTURAE() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0033);}
						
						@Override
						public void visitVERIFACTU() throws InvoiceCommunicationException  {
							VERIFACTU.accept(ctx,cc, phase);
						}
						
						@Override 
						public void visitNO_VERIFACTU() throws InvoiceCommunicationException { 
							NOVERIFACTU.accept(ctx,cc);
						}
						@Override 
						public void visitSIF() throws InvoiceCommunicationException { 
							NOVERIFACTU.accept(ctx,cc);
						}
						
					});
				}
			}
			return cc;
		} catch (Exception e) {
			if (e instanceof InvoiceCommunicationException ice) {
				throw ice;
			}
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9000, e);
		}
	}

	// *************************************************************
	// ************************************* [FEE INVOICING] *******
	// *************************************************************
	public static InvoiceProcessOutput feeInvoicing(Occam occam, FeeBillingParams params, ConsoleLogger logger) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, params.getDomainId() );
			logger.message( IC, "\u00BFLas facturas se deben comunicar? " + (icc.hasCommunication() && params.isCommunicable()));
			logger.message( IC, "\u00BFLa operaci\u00F3n es una simulaci\u00F3n? " + params.isDryRun());
			logger.message( IC, "\u00BFLas facturas se deben guardar como proforma? " + params.mustSaveAsProforma());
			if (icc.hasCommunication() && params.isNotDryRun() && params.isCommunicable()) {
				InvoiceProcessOutput output = new InvoiceProcessOutput();
				try {
					feeInvoicing(ctx, icc, params, logger)
						.invoiceStream()
						.forEach(output::addInvoice);
					logger.ok(IC, "Fin del proceso" );
				} catch (Throwable e) {
					e.printStackTrace();
					output.setProcessErrorLevel(InvoiceErrorLevel.ERR);
					output.setProcessMessage(e.getMessage());
					logger.error(IC, "Error en la comunicaci\u00F3n de las facturas: " + e.getMessage());
					if (e instanceof InvoiceCommunicatorContextError icce) {
						icce.getInvoiceCommunicatorContext()
							.invoiceStream()
							.forEach(output::addInvoice);
					}
				}
				return output;
			} else {
				log( "---> INVOICES MUST NOT BE COMMUNICATED!");
				return ctx.getDslContext().transactionResult(configuration ->
					FeeBillingDAO.invoice(ctx, params, logger)
				);
			}
		} catch (Exception e) {
			InvoiceProcessOutput output = new InvoiceProcessOutput();
			String prev = AonStringUtils.defaultIfBlank(output.getProcessMessage());
			output.setProcessMessage( prev +" <"+ e.getMessage()+">");
			return output;
		}
	}
	
	private static class InvoiceCommunicatorContextError extends RuntimeException {

		private static final long serialVersionUID = -3730190195705367708L;

		private transient final InvoiceCommunicatorContext icc;
		
		InvoiceCommunicatorContextError(Throwable t, InvoiceCommunicatorContext icc) {
			super(t);
			this.icc = icc;
		}
		
		InvoiceCommunicatorContext getInvoiceCommunicatorContext() {
			return icc;
		}
	}
	
	private static InvoiceCommunicatorContext feeInvoicing(AONContext ctx, InvoiceCommunicationConfiguration icc, FeeBillingParams params, ConsoleLogger logger) throws InvoiceCommunicationException {
		if (!icc.hasCommunication()) throw new AonCoreException("Si no hay comunicación, no se debe llamar a este método.");
		if (params.isDryRun()) throw new AonCoreException("Si es una simulación, no se debe llamar a este método.");
		if (!params.isCommunicable()) throw new AonCoreException("Si no se debe comunicar, no se debe llamar a este método.");
		
		// *****************************************************
		if (!icc.isVerifactuTest())
			throw new AonCoreException("La facturación de cuotas no está permitida aún en modo producción de VERIFACTU.");
		// *****************************************************
		
		Company company = CompanyDAO.getByDomain(ctx, params.getDomainId());
		Domain domain = DomainDAO.getDomain(ctx, params.getDomainId());
		User user = SecurityDAO.getUser(ctx, ctx.getUser());
		Integer certId = params.getCertId();
		
		checkCompany(company);
		checkConfig(icc);
		checkCertificate(ctx, icc, certId);
			
		return ctx.getDslContext().transactionResult(conf -> {
			
			InvoiceProcessOutput output = FeeBillingDAO.invoice(ctx, params, logger);
			List<Invoice> invoices = output.invoiceStream().collect(Collectors.toCollection(LinkedList::new));
			
			InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, certId, invoices)
				.setConfig( icc )
				.setCompany( company )
				.setLogger( logger )
				.setFailOnWrongValidation( AonCollectionUtils.size(invoices) == 1 );
			
			try {
				for ( InvoiceCommunicationType type : cc.getConfig().getTypes()) {
					type.visit( new InvoiceCommunicationTypeVisitor() {
						
						@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
						@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
						@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
						@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
						@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
						@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }
						
						@Override
						public void visitVERIFACTU() throws InvoiceCommunicationException  {
							VERIFACTU.accept(ctx,cc, new AonIssuePhaseListener() );
							cc.invoiceStream()
								.filter(invoice -> !invoice.hasInvoiceInfo(InvoiceCommunicationType.VERIFACTU))
								.forEach( invoice -> invoice.reloadCommunicationInfo(InvoiceInfoDAO.getMap(ctx, invoice.getDomain(), invoice.getId()).orElse(null)))
							;
							logger.message(IC, "Fin del proceso comunicación VERIFACTU" );
						}
					});
				}
			} catch (Throwable e) {
				e.printStackTrace();
				throw new InvoiceCommunicatorContextError( e, cc );
			}
			
			return cc;
		});
	}

	// *************************************************************
	// ******************************************* [CANCEL] ********
	// *************************************************************
	public static boolean mustBeAnnulled(Occam occam, Invoice invoice, InvoiceCommunicationType type) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return mustBeAnnulled( ctx, invoice, type );
		}
	}
	public static boolean mustBeAnnulled(AONContext ctx, Invoice invoice, InvoiceCommunicationType type) {
		return invoice.getInvoiceInfo( type )
			.or(() -> InvoiceInfoDAO.get(ctx, invoice.getId(), type))
			.map(InvoiceInfo::isPartialAccepted)
			.orElse(false);
	}
	
	public static InvoiceCommunicatorContext cancelInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				cancel(ctx, communicator));
		}
	}
	
	public static InvoiceCommunicatorContext cancel(AONContext ctx, final InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		try {
			check(ctx,cc);
			checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));
			if (invoice.isSales() && cc.getConfig().hasCommunication() ) {
				for ( InvoiceCommunicationType type : cc.getConfig().getTypes()) {
					type.visit( new InvoiceCommunicationTypeVisitor() {
						
						@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
						@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
						@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
						@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
						@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
						@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }
						@Override public void visitFACTURAE() throws InvoiceCommunicationException { throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0033);}
						
						@Override
						public void visitVERIFACTU() throws InvoiceCommunicationException  {
							if (mustBeAnnulled(ctx, invoice, InvoiceCommunicationType.VERIFACTU)) {
								VERIFACTU.cancel(ctx,cc);
							} 
						}
						
						@Override 
						public void visitNO_VERIFACTU() throws InvoiceCommunicationException { 
							if (mustBeAnnulled(ctx, invoice, InvoiceCommunicationType.VERIFACTU)) {
								VERIFACTU.cancel(ctx,cc);
							} 
						}
						
						@Override 
						public void visitSIF() throws InvoiceCommunicationException { 
							if (mustBeAnnulled(ctx, invoice, InvoiceCommunicationType.VERIFACTU)) {
								VERIFACTU.cancel(ctx,cc);
							} 
						}
						
					});
				}
			}
			return cc;
		} catch (Exception e) {
			if (e instanceof InvoiceCommunicationException ice) {
				throw ice;
			}
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9000, e);
		}
	}
	

	// *************************************************************
	// ******************************************* [PRIVATE] *******
	// *************************************************************
	private static void checkAONContextValues(InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		if (communicator == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0001);
		}
		if (communicator.getDomain() == null || communicator.getDomain().getId() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0003);
		}
		if (communicator.getUser() == null|| communicator.getUser().getId() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0004);
		}
	}
	
	private static void check(AONContext ctx, InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		checkCompany(cc.getCompany());
		checkConfig(cc.getConfig());
		if (cc.invoiceCount() == 0) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005);
		}
		checkCertificate(ctx, cc.getConfig(), cc.getCertificateId());
	}
	
	private static void checkConfig(InvoiceCommunicationConfiguration config) throws InvoiceCommunicationException {
		if (config == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0006);
		}
	}

	private static void checkCompany(Company company) throws InvoiceCommunicationException {
		if (company == null
			|| company.getId() == null
			|| AonStringUtils.isBlank(company.getDocument())
			|| AonStringUtils.isBlank(company.getName())) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0002);
		}
	}

	private static void checkCertificate(AONContext ctx, final InvoiceCommunicationConfiguration icc, Integer certId) throws InvoiceCommunicationException {
		Certificate certificate = icc.getCertificate();
		if (certificate == null) {
			try {
				if (certId != null) {
					certificate = CertificateDAO.getStream(ctx, f -> f.getIdProperty().eq(certId))
						.findFirst()
						.orElseThrow( () -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0021));
					icc.setCertificate( certificate );			 
				}
			} catch (Exception e) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0021,e);
			}
			if (certificate != null && !checkCert(certificate)) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0022);
			}
			if(certificate == null || certificate.isEmpty()) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0023);
			}
		}
		if (certificate.getData() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0023);
		}
		checkCertificateData(certificate);
	}

	private static void checkCertificateData(Certificate certificate) throws InvoiceCommunicationException {
		try {
			ByteArrayInputStream key = new ByteArrayInputStream(certificate.getData());
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(key, certificate.getPassword().toCharArray());
			String alias = keyStore.aliases().nextElement();
			X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
			Date now = new Date();
			if (x509Certificate.getNotBefore() != null && now.before(x509Certificate.getNotBefore())) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0026);
			} 
			if (x509Certificate.getNotAfter() != null && now.after(x509Certificate.getNotAfter())) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0027);
			} 			
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0025);
		}
	}

	private static boolean checkCert(Certificate certificate) {
		if (certificate == null) return false;
		byte[] cert = certificate.getData();
		String password = certificate.getPassword();
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(cert);
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(is, password.toCharArray());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private static void checkUniqueInvoice(InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		if (cc.invoiceCount() > 1) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9007);
		}
	}


	public static void throwRightException(Exception e, Invoice invoice) throws InvoiceErrorException {
		if (invoice != null) {
			JSONObject json = InvoiceJSON.to(invoice).orElse(null);
			if (json != null && invoice.hasMessages()) {
				List<InvoiceError> errors = invoice.messageStream()
						.collect(Collectors.toCollection(LinkedList::new));
				e.printStackTrace();
				throw new InvoiceErrorException(errors);
			}
		}
		throwRightException(e);
	}
	public static void throwRightException(Exception e, Stream<Invoice> invoiceStream) throws InvoiceErrorException {
		List<InvoiceError> errors = invoiceStream
			.filter(Objects::nonNull)
			.filter(invoice -> invoice.hasMessages())
			.flatMap(invoice -> invoice.messageStream())
			.collect(Collectors.toCollection(LinkedList::new));
		if (AonCollectionUtils.isNotEmpty(errors)) {
			throw new InvoiceErrorException(errors);
		};
	}
	
	private static void throwRightException(Exception e) throws InvoiceErrorException {
		// Para evitar recursividad --- 
		Set<Throwable> visited = new HashSet<>();
		Throwable current = e;
		while (current != null && !visited.contains(current)) {
			visited.add(current);
			current = current.getCause();
		}
		// ------------------------------
		System.out.println("******* [ InvoiceCommunicator.throwRightException ] ********");
		e.printStackTrace();
		InvoiceErrorException ti = AonCollectionUtils.stream(visited)
			.filter( InvoiceCommunicationException.class::isInstance )
			.map(ex -> (InvoiceCommunicationException) ex)
			.map(ice -> 
				AonCollectionUtils.stream(ice.getMessages())
					.map(icm -> {
						String message = icm.getMessage(); 
						if ( icm == InvoiceCommunicationError.AON_9000
							&& ice.getCause() != null
							&& AonStringUtils.isNotEmpty(ice.getCause().getMessage())) {
							message = message + " ["+ ice.getCause().getMessage() +"]";
						}
						return InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,icm.getCode(),message);
					})
					.collect(Collectors.toCollection(LinkedList::new))
				)
			.map( InvoiceErrorException::new )
			.findFirst()
			.orElseGet( () -> {
				InvoiceError invoiceError = InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,"",e.getMessage());
				if (e instanceof DataAccessException && e.getCause() != null) {
					invoiceError = InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,"",e.getCause().getMessage());
				}
				return new InvoiceErrorException(invoiceError);	
			})
		;
		System.out.println("***************");
		ti.printStackTrace();
		throw ti;
	}

	private static void addTBAI(AONContext ctx, Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> h, TBAIInformation tbaiInfo, Integer domainId, Integer invoiceId) {
		if ( tbaiInfo == null ) return;
		AonCollectionUtils.stream(tbaiInfo.getRequests())
			.forEach(r -> {
				InvoiceCommunicationStatus status = r.getResponse().isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG;
				InvoiceCommunicationHistoryMapValue map = h.computeIfAbsent(InvoiceCommunicationType.TBAI, 
					k -> {
						InvoiceInfo info = new InvoiceInfo()
							.setDomain(domainId)	
							.setInvoice(invoiceId)
							.setType(k)
							.setStatus(status)
							.setCreationUser(r.getDataResponse().getCreationUser())
							.setCreationDate(r.getDataResponse().getResponseDate());
						InvoiceInfoDAO.InvoiceInfoURLFiller.build(ctx, info);
						return new InvoiceCommunicationHistoryMapValue().setInfo( info);
					}
				);
				map.add(new InvoiceCommunicationHistory()
					.setInvoiceId(invoiceId)
					.setDate(r.getDataResponse().getResponseDate())
					.setCreationUser(r.getDataResponse().getCreationUser())
					.setType( InvoiceCommunicationType.TBAI )
					.setOperation( InvoiceCommunicationOperation.REGISTER)
					.setStatus(status)
					.setRequestUrl(r.getRequestUrl())
					.setResponseUrl(r.getResponseUrl())
				);
		});		
	}
	
}

