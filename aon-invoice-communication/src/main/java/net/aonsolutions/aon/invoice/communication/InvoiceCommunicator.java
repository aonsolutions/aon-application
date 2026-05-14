package net.aonsolutions.aon.invoice.communication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils.PrintStreamConsoleLogger;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceConsoleDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.fee.FeeBillingDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.util.AonChronometer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.TBAIInformation;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.verifactu.NOVERIFACTU;
import net.aonsolutions.aon.verifactu.SIF;
import net.aonsolutions.aon.verifactu.VERIFACTU;

public class InvoiceCommunicator {
	
	private static final int MSG_INVOICES = 500;
	private static final String PC = "PC";
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
			Invoice invoice = InvoiceDAO.getInvoice(ctx, invoiceId);
			if (invoice == null) return new HashMap<>();
			InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, occam.getDomain() );
			return history(ctx, icc, invoice);
		}
		
	}

	public static HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(AONContext ctx, Integer domain, Integer invoiceId) {
		Invoice invoice = InvoiceDAO.getInvoice(ctx, invoiceId);
		if (invoice == null) return new HashMap<>();
		InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, domain );
		return history(ctx, icc, invoice);
	}

	public static HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(
		Occam occam
		, Integer invoiceId
		, InvoiceType invoiceType
		, Date expDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, occam.getDomain() );
			return history(ctx, icc, occam.getDomain(), invoiceId, invoiceType, expDate);
		}
	}
	
	public static HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(
			AONContext ctx
			, final InvoiceCommunicationConfiguration icc
			, final Invoice invoice) {
		return history(ctx, icc, invoice.getDomain(), invoice.getId(), invoice.getType(), invoice.getExpDate());
	}
			
	private static HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(
			AONContext ctx
			, final InvoiceCommunicationConfiguration icc
			, final Integer domainId
			, final Integer invoiceId
			, InvoiceType invoiceType
			, Date expDate) {
		List<InvoiceCommunicationHistory> history = InvoiceCommunicationDAO.getHistory(ctx, invoiceId, t -> {
			try {
				t.getType().visit( new InvoiceCommunicationTypeVisitor() {
					@Override public void visitSERES() throws InvoiceCommunicationException 		{ /*Nothing*/ }
					@Override public void visitEMAIL() throws InvoiceCommunicationException 		{ /*Nothing*/ }
					@Override public void visitCLOSING() throws InvoiceCommunicationException 		{ /*Nothing*/ }
					@Override public void visitSII() throws InvoiceCommunicationException 			{ /*Nothing*/ }
					@Override public void visitLROE() throws InvoiceCommunicationException 			{ /*Nothing*/ }
					@Override public void visitTBAI() throws InvoiceCommunicationException 			{ /*Nothing*/ }
					@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException 	{ /*Nothing*/ }
					@Override public void visitSIF() throws InvoiceCommunicationException 			{ /*Nothing*/ }
					@Override public void visitFACTURAE() throws InvoiceCommunicationException 		{ /*Nothing*/ }
					
					@Override
					public void visitVERIFACTU() throws InvoiceCommunicationException  {
						if (t.getResponseData() != null && t.getResponseData().length > 0) {
							t.setResponseMessages( VERIFACTU.history(t.getResponseData(), invoiceId) );
						}
					}
					
				});
			} catch (Exception e) {
				return AonCollectionUtils.toList("Error al interpretar la respuesta: " + e.getMessage() );						
			}
			return t.getResponseMessages();
		});
		
		HashMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> map = new HashMap<>();
		AonCollectionUtils.stream( InvoiceInfoDAO.getMap(ctx, icc, domainId, invoiceId, invoiceType, expDate).orElse(null) )
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
		
		if (icc.isTbai(invoiceType, expDate)) {	
			TbaiData.getInstance(ctx, icc).get(ctx, domainId, invoiceId)
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
	private static class AonIssuePhaseListener extends AonAbstractPhaseListener {

		@Override
		public void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			setOldNumber( invoice.getNumber() );
			setProforma( invoice.isProforma() );
			if (invoice.isProforma()) {
				InvoiceDAO.preIssue(ctx, icc.getConfig(), invoice);
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
				// Revertir el n\u00FAmero si es proforma y la validaci\u00F3n fall\u00F3, puesto que no se va a comunicar.
				invoice.setNumber( getOldNumber() );
				invoice.setReferenceCode( FinanceUtil.getSalesReferenceCode(invoice) );
			}
		}
		
	}
	
	public static InvoiceProcessOutput issue(Occam occam, InvoiceConsoleParams params, PrintStreamConsoleLogger logger) {
		if (params == null) throw new AonCoreException("Par\u00E1metros nulos.");
		params.setAnnulled( false )	// No se comunican facturas anuladas
			.setOutput( true )		// Facturas emitidas
		;
		AonChronometer chronometer = new AonChronometer();
		chronometer.start();
		Date expDate = new Date();;
		final InvoiceProcessOutput output = new InvoiceProcessOutput();
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, params.getDomain() );
			if (!icc.hasCommunication(expDate)) throw new AonCoreException("Si no hay comunicaci\u00F3n, no se debe llamar a este m\u00C9todo.");
			Company company = CompanyDAO.getByDomain(ctx, params.getDomain());
			Domain domain = DomainDAO.getDomain(ctx, params.getDomain());
			User user = SecurityDAO.getUser(ctx, ctx.getUser());
			Integer certId = params.getCertId();
			checkCompany(company);
			checkConfig(icc);
			if ( icc.isCertificateNeeded() ) {
				checkCertificate(ctx, icc, certId);
			}
			
			params.setAttachExcluded( true );				// Se rellena en getFullInvoice()
			params.setCommunicationExcluded( true ); 		// Se rellena en getFullInvoice()
			InvoiceConsoleDAO.getInvoiceHeadersStream(ctx, params)
				.map(ic -> InvoiceDAO.getFullInvoice(ctx, icc, ic.getId()))
				.forEach( output::addInvoice );
			
			ctx.getDslContext().transaction(conf -> {
				
				logger.message(IC, "Inicio del proceso comunicaci\u00F3n.");

				List<Invoice> invoices = output.invoiceStream().collect(Collectors.toCollection(LinkedList::new));
				InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, certId, invoices)
					.setConfig( icc )
					.setCompany( company )
					.setLogger( logger )
					.setFailOnWrongValidation( AonCollectionUtils.size(invoices) == 1 );
				
				if (AonCollectionUtils.size(invoices) > MSG_INVOICES) {
					List<List<Invoice>> splittedInvoices = AonCollectionUtils.split( invoices, MSG_INVOICES );
					int prg = 0;
					int count = splittedInvoices.size();
					cc.getLogger().message( PC, MessageFormat.format("Procesando {0} lotes", count));
					cc.getLogger().mainProgress(PC, count, 0);
					for (List<Invoice> invoiceBatch : splittedInvoices) {
						InvoiceCommunicatorContext batchContext = new InvoiceCommunicatorContext(domain, user, certId, invoiceBatch)
							.setConfig( icc )
							.setCompany( company )
							.setLogger( logger )
							.setFailOnWrongValidation( false );
						communicateGeneratedInvoices( ctx, batchContext, expDate );
						prg = prg + 1;
						cc.getLogger().mainProgress( PC, count, prg );
					}
				} else {
					communicateGeneratedInvoices( ctx, cc, expDate);
				}
			});
			chronometer.stop(); 
			logger.message(IC, "Tiempo total del proceso:" + chronometer.format());
			logger.ok(IC, "Fin del proceso" );
		} catch (Throwable e) {
			e.printStackTrace();
			output.setProcessErrorLevel(InvoiceErrorLevel.ERR);
			output.setProcessMessage(e.getMessage());
			chronometer.stop();
			logger.message(IC, "Tiempo total del proceso:" + chronometer.format());
			logger.error(IC, "Error en la comunicaci\u00F3n de las facturas: " + e.getMessage());
		}
		return output;
	}
	
	public static InvoiceCommunicatorContext issueInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(trx -> 
				issue(ctx, communicator, new AonIssuePhaseListener()));
		}
	}
	
	private static InvoiceCommunicatorContext issue(AONContext ctx, final InvoiceCommunicatorContext cc, InvoiceCommunicationPhaseListener phase) throws InvoiceCommunicationException {
		try {
			check(ctx, cc);
			// checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));
			if(invoice.isSales()) {
				for ( CommunicationData data : cc.getConfig().getTypes()) {
					InvoiceCommunicationType type = data.getCommunicationType().orElse(null);
					if (type != null) {
						type.visit( new InvoiceCommunicationTypeVisitor() {
							@Override public void visitSERES() throws InvoiceCommunicationException 	{throwSERES();}
							@Override public void visitEMAIL() throws InvoiceCommunicationException		{throwEMAIL();}
							@Override public void visitCLOSING() throws InvoiceCommunicationException	{throwCLOSING();}
							@Override public void visitTBAI() throws InvoiceCommunicationException		{throwTBAI();}
							@Override public void visitLROE() throws InvoiceCommunicationException		{throwLROE();}
							@Override public void visitFACTURAE() throws InvoiceCommunicationException	{throwFACTURAE();}
							
							@Override public void visitVERIFACTU() throws InvoiceCommunicationException  	{ VERIFACTU.accept(ctx,cc, data, phase); }
							@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException 	{ NOVERIFACTU.accept(ctx,cc, data, phase);}
							@Override public void visitSIF() throws InvoiceCommunicationException			{ SIF.accept(ctx,cc, data, phase);}
							@Override public void visitSII() throws InvoiceCommunicationException			{ /* Se emite la factura. La comunicación se delega en la pantalla del SII.*/ }
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
	private static class AonAcceptPhaseListener extends AonAbstractPhaseListener {

		@Override
		public void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			setOldNumber( invoice.getNumber() );
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
			invoice.setId( null );
			invoice.setNumber( getOldNumber() );
			invoice.setReferenceCode( FinanceUtil.getSalesReferenceCode(invoice) );
		}
	}
	
	public static InvoiceCommunicatorContext acceptInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				accept(ctx, communicator, new AonAcceptPhaseListener()));
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
				for ( CommunicationData data : cc.getConfig().getTypes()) {
					InvoiceCommunicationType type = data.getCommunicationType().orElse(null);
					if (type != null) {
						type.visit( new InvoiceCommunicationTypeVisitor() {
							
							@Override public void visitSERES() throws InvoiceCommunicationException		{ throwSERES(); }
							@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throwEMAIL(); }
							@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throwCLOSING(); } 
							@Override public void visitSII() throws InvoiceCommunicationException 		{ throwSII(); }
							@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throwTBAI(); }
							@Override public void visitLROE() throws InvoiceCommunicationException 		{ throwLROE(); }
							@Override public void visitFACTURAE() throws InvoiceCommunicationException	{ throwFACTURAE(); }	
							
							@Override
							public void visitVERIFACTU() throws InvoiceCommunicationException  {
								VERIFACTU.accept(ctx,cc, data, phase);
							}
							
							@Override 
							public void visitNO_VERIFACTU() throws InvoiceCommunicationException { 
								NOVERIFACTU.accept(ctx,cc, data, phase);
							}
							
							@Override 
							public void visitSIF() throws InvoiceCommunicationException {
								SIF.accept(ctx,cc, data, phase);
							}
							
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
	// ************************************* [FEE INVOICING] *******
	// *************************************************************
	public static InvoiceProcessOutput feeInvoicing(Occam occam, FeeBillingParams params, ConsoleLogger logger) {
		AonChronometer chronometer = new AonChronometer();
		chronometer.start();

		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, params.getDomainId() );
			logger.message( IC, "\u00BFLa operaci\u00F3n es una simulaci\u00F3n? " + (params.isDryRun()?"SI":"NO"));
			logger.message( IC, "\u00BFLas facturas se deben guardar como proforma? " + (params.mustSaveAsProforma()?"SI":"NO"));
			logger.message( IC, "\u00BFLas facturas se deben comunicar? " + ((icc.hasCommunication() && params.isCommunicable())?"SI":"NO"));
			if (icc.hasCommunication(params.getInvoiceDate()) && params.isNotDryRun() && params.isCommunicable()) {
				InvoiceProcessOutput output = new InvoiceProcessOutput();
				try {
					feeInvoicing(ctx, icc, params, logger)
						.invoiceStream()
						.forEach(output::addInvoice);
					chronometer.stop(); 
					logger.message(IC, "Tiempo total del proceso:" + chronometer.format());
					logger.ok(IC, "Fin del proceso" );
				} catch (Throwable e) {
					e.printStackTrace();
					output.setProcessErrorLevel(InvoiceErrorLevel.ERR);
					output.setProcessMessage(e.getMessage());
					chronometer.stop();
					logger.message(IC, "Tiempo total del proceso:" + chronometer.format());
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
		if (!icc.hasCommunication(params.getInvoiceDate())) throw new AonCoreException("Si no hay comunicaci\u00F3n, no se debe llamar a este m\u00C9todo.");
		if (params.isDryRun()) throw new AonCoreException("Si es una simulaci\u00F3n, no se debe llamar a este m\u00C9todo.");
		if (!params.isCommunicable()) throw new AonCoreException("Si no se debe comunicar, no se debe llamar a este m\u00C9todo.");
		
		// ************************************** [BORRAR AL SUBIR A PRODUCCION] ****
		if (icc.isVerifactu()) {
			icc.getVerifactuData()
				.filter( d -> d.isNotTest() )
				.orElseThrow(() -> new AonCoreException("La facturaci\u00F3n de cuotas no est\u00E1 permitida a\u00FAn en modo producci\u00F3n de VERIFACTU."));
		}
		// **************************************************************************
		
		Company company = CompanyDAO.getByDomain(ctx, params.getDomainId());
		Domain domain = DomainDAO.getDomain(ctx, params.getDomainId());
		User user = SecurityDAO.getUser(ctx, ctx.getUser());
		Integer certId = params.getCertId();
		
		checkCompany(company);
		checkConfig(icc);
		if ( icc.isCertificateNeeded() ) {
			checkCertificate(ctx, icc, certId);
		}
			
		return ctx.getDslContext().transactionResult(conf -> {
			InvoiceProcessOutput output = FeeBillingDAO.invoice(ctx, params, logger);
			logger.message(IC, "Inicio del proceso comunicaci\u00F3n.");

			List<Invoice> invoices = output.invoiceStream().collect(Collectors.toCollection(LinkedList::new));
			InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, certId, invoices)
				.setConfig( icc )
				.setCompany( company )
				.setLogger( logger )
				.setFailOnWrongValidation( AonCollectionUtils.size(invoices) == 1 );
			
			if (AonCollectionUtils.size(invoices) > MSG_INVOICES) {
				List<List<Invoice>> splittedInvoices = AonCollectionUtils.split( invoices, MSG_INVOICES );
				int prg = 0;
				int count = splittedInvoices.size();
				cc.getLogger().message( PC, MessageFormat.format("Procesando {0} lotes", count));
				cc.getLogger().mainProgress(PC, count, 0);
				for (List<Invoice> invoiceBatch : splittedInvoices) {
					InvoiceCommunicatorContext batchContext = new InvoiceCommunicatorContext(domain, user, certId, invoiceBatch)
						.setConfig( icc )
						.setCompany( company )
						.setLogger( logger )
						.setFailOnWrongValidation( false );
					communicateGeneratedInvoices( ctx, batchContext, params.getInvoiceDate() );
					prg = prg + 1;
					cc.getLogger().mainProgress( PC, count, prg );
				}
			} else {
				communicateGeneratedInvoices( ctx, cc, params.getInvoiceDate());
			}
			return cc;
		});
	}

	private static void communicateGeneratedInvoices(AONContext ctx, InvoiceCommunicatorContext cc, Date expDate) throws InvoiceCommunicationException {
			try {
				for ( CommunicationData data : cc.getConfig().getTypes( expDate )) {
					InvoiceCommunicationType type = data.getCommunicationType().orElse(null);
					if (type != null) {
						type.visit( new InvoiceCommunicationTypeVisitor() {
							
							@Override public void visitSERES() throws InvoiceCommunicationException 		{ throwSERES();}
							@Override public void visitEMAIL() throws InvoiceCommunicationException 		{ throwEMAIL(); }
							@Override public void visitCLOSING() throws InvoiceCommunicationException 		{ throwCLOSING(); }
							@Override public void visitSII() throws InvoiceCommunicationException 			{ throwSII(); }
							@Override public void visitTBAI() throws InvoiceCommunicationException 			{ throwTBAI(); }
							@Override public void visitLROE() throws InvoiceCommunicationException 			{ throwLROE(); }
							@Override public void visitFACTURAE() throws InvoiceCommunicationException 		{ throwFACTURAE(); }
							
							@Override 
							public void visitSIF() throws InvoiceCommunicationException {
								SIF.accept(ctx, cc, data, new AonIssuePhaseListener() );
							}
							
							@Override 
							public void visitNO_VERIFACTU() throws InvoiceCommunicationException { 
								NOVERIFACTU.accept(ctx, cc, data, new AonIssuePhaseListener() );
							}
							 
							@Override
							public void visitVERIFACTU() throws InvoiceCommunicationException  {
								VERIFACTU.accept(ctx, cc, data, new AonIssuePhaseListener() );
							}
						});
					}
				}
			} catch (Throwable e) {
				if (e instanceof InvoiceCommunicationException ice) {
					throw ice;
				}
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9000, e);
			}
	}

	// *************************************************************
	// ******************************************* [CANCEL] ********
	// *************************************************************
	public static boolean mustBeAnnulled(Occam occam, Invoice invoice, InvoiceCommunicationType type) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return mustBeAnnulled( ctx, invoice, type );
		}
	}
	private static boolean mustBeAnnulled(AONContext ctx, Invoice invoice, InvoiceCommunicationType type) {
		try {
			MutableBoolean result = new MutableBoolean(false);
			type.visit( new InvoiceCommunicationTypeVisitor() {
				@Override public void visitSII() throws InvoiceCommunicationException 		{ /* Nothing */ }
				@Override public void visitTBAI() throws InvoiceCommunicationException 		{ /* Nothing */ }
				@Override public void visitLROE() throws InvoiceCommunicationException 		{ /* Nothing */ }
				@Override public void visitSERES() throws InvoiceCommunicationException 	{ /* Nothing */ }
				@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ /* Nothing */ }
				@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ /* Nothing */ }
				@Override public void visitFACTURAE() throws InvoiceCommunicationException 	{ /* Nothing */ }

				@Override 
				public void visitSIF() throws InvoiceCommunicationException {
					result.setValue( 
						invoice.getInvoiceInfo( type )
							.or(() -> InvoiceInfoDAO.get(ctx, invoice.getId(), type))
							.map(InvoiceInfo::isAccepted)
							.orElse(false)
					);
				}
				
				@Override 
				public void visitNO_VERIFACTU() throws InvoiceCommunicationException {
					result.setValue( 
						invoice.getInvoiceInfo( type )
							.or(() -> InvoiceInfoDAO.get(ctx, invoice.getId(), type))
							.map(InvoiceInfo::isPending)
							.orElse(false)
					);
				}
				
				@Override 
				public void visitVERIFACTU() throws InvoiceCommunicationException {
					result.setValue( 
						invoice.getInvoiceInfo( type )
							.or(() -> InvoiceInfoDAO.get(ctx, invoice.getId(), type))
							.map(InvoiceInfo::isPartialAccepted)
							.orElse(false)
					);
				}
				
			});
			return result.getValue();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
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
			for ( CommunicationData data : cc.getConfig().getTypes(invoice.getType())) {
				InvoiceCommunicationType type = data.getCommunicationType().orElse(null);
				if (type != null) {
					type.visit( new InvoiceCommunicationTypeVisitor() {
						
						@Override public void visitSERES() throws InvoiceCommunicationException 	{ throwSERES(); }
						@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throwEMAIL(); }
						@Override public void visitCLOSING() throws InvoiceCommunicationException	{ throwCLOSING(); }
						@Override public void visitSII() throws InvoiceCommunicationException 		{ throwSII(); }
						@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throwTBAI(); }
						@Override public void visitLROE() throws InvoiceCommunicationException 		{ throwLROE(); }
						@Override public void visitFACTURAE() throws InvoiceCommunicationException	{ throwFACTURAE(); }
						
						@Override
						public void visitVERIFACTU() throws InvoiceCommunicationException  {
							if (mustBeAnnulled(ctx, invoice, InvoiceCommunicationType.VERIFACTU)) {
								VERIFACTU.cancel(ctx,cc, data);
							} 
						}
						
						@Override 
						public void visitNO_VERIFACTU() throws InvoiceCommunicationException { 
							if (mustBeAnnulled(ctx, invoice, InvoiceCommunicationType.NO_VERIFACTU)) {
								NOVERIFACTU.cancel(ctx,cc, data);
							} 
						}
						
						@Override 
						public void visitSIF() throws InvoiceCommunicationException {
							if (mustBeAnnulled(ctx, invoice, InvoiceCommunicationType.SIF)) {
								SIF.cancel(ctx,cc, data); 
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
	public static InvoiceConsoleAnalysis analyze(Occam occam, InvoiceConsoleParams params, PrintStreamConsoleLogger logger) {
		if (params == null) throw new AonCoreException("Par\u00E1metros nulos.");
		params.setAnnulled( false )	// No se comunican facturas anuladas
			.setOutput( true )		// Facturas emitidas
		;
		AonChronometer chronometer = new AonChronometer();
		chronometer.start();
		
		Date expDate = new Date();;
		final InvoiceProcessOutput output = new InvoiceProcessOutput();
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			InvoiceCommunicationConfiguration icc = InvoiceCommunicationDAO.get(ctx, params.getDomain() );
			if (!icc.hasCommunication(expDate)) {
				return InvoiceConsoleAnalysis.fromError("No hay comunicaci\u00F3n activa a fecha de hoy);");
			}
				
			Company company = CompanyDAO.getByDomain(ctx, params.getDomain());
			Domain domain = DomainDAO.getDomain(ctx, params.getDomain());
			User user = SecurityDAO.getUser(ctx, ctx.getUser());
			Integer certId = params.getCertId();
			
			checkCompany(company);
			checkConfig(icc);
			if ( icc.isCertificateNeeded() ) {
				checkCertificate(ctx, icc, certId);
			}
			
			params.setAttachExcluded( true );
			InvoiceConsoleAnalysis analysis = InvoiceConsoleDAO.analyze(ctx, params);
//			InvoiceConsoleDAO.getInvoiceHeadersStream(ctx, params)
//				.forEach( output::addInvoice );
//				
//				List<Invoice> invoices = output.invoiceStream().collect(Collectors.toCollection(LinkedList::new));
//				InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, certId, invoices)
//					.setConfig( icc )
//					.setCompany( company )
//					.setLogger( logger )
//					.setFailOnWrongValidation( AonCollectionUtils.size(invoices) == 1 );
//				communicateGeneratedInvoices( ctx, cc, expDate);
				
			chronometer.stop(); 
			logger.message(IC, "Tiempo total del proceso:" + chronometer.format());
			logger.ok(IC, "Fin del proceso" );
			return analysis;
		} catch (Throwable e) {
			e.printStackTrace();
			return InvoiceConsoleAnalysis.fromError( e.getMessage() );
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
		if (cc.getConfig() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0006);
		}
		try {
			for ( CommunicationData data : cc.getConfig().getTypes()) {
				InvoiceCommunicationType type = data.getCommunicationType().orElse(null);
				if (type != null) {
					type.visit( new InvoiceCommunicationTypeVisitor() {
						
						@Override public void visitSERES() throws InvoiceCommunicationException 		{ /*Nothing*/ }
						@Override public void visitEMAIL() throws InvoiceCommunicationException 		{ /*Nothing*/ }
						@Override public void visitCLOSING() throws InvoiceCommunicationException		{ /*Nothing*/ }
						@Override public void visitSII() throws InvoiceCommunicationException 			{ /*Nothing*/ }
						@Override public void visitTBAI() throws InvoiceCommunicationException 			{ /*Nothing*/ }
						@Override public void visitLROE() throws InvoiceCommunicationException 			{ /*Nothing*/ }
						@Override public void visitFACTURAE() throws InvoiceCommunicationException		{ /*Nothing*/ }
						@Override public void visitNO_VERIFACTU() throws InvoiceCommunicationException 	{ /*Nothing*/ }
						@Override public void visitSIF() throws InvoiceCommunicationException 			{ /*Nothing*/ }
						
						@Override
						public void visitVERIFACTU() throws InvoiceCommunicationException  {
							checkCertificate(ctx, cc.getConfig(), cc.getCertificateId());
						}
						
					});
				}
			}
		} catch (Exception e) {
			// Si es una InvoiceCommunicationException la lanzamos tal cual
			if (e instanceof InvoiceCommunicationException ice) {
				throw ice;
			}
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0021, e);
		}
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
			.map( es -> new InvoiceErrorException(es) )
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
				InvoiceCommunicationStatus status = r.getResponse() != null && r.getResponse().isOk() 
						? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG;
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

