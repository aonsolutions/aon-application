package net.aonsolutions.aon.invoice.communication;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.JsonUtils.JSONArrayCollector;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.TBAIInformation;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.verifactu.VERIFACTU;
import net.aonsolutions.aon.verifactu.VerifactuContext;

public class InvoiceCommunicator {
	
	private InvoiceCommunicator() {
	}

	// *************************************************************
	// ******************************************* [HISTORY] *******
	// *************************************************************
	public static Map<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(Occam occam, Integer invoiceId) throws InvoiceCommunicationException {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return history(ctx, occam.getDomain(), invoiceId);
		}
	}
	
	public static Map<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> history(AONContext ctx, final Integer domainId, final Integer invoiceId ) {
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
				});
			} catch (Exception e) {
				return AonCollectionUtils.toList("Error al interpretar la respuesta: " + e.getMessage() );						
			}
			return t.getResponseMessages();
		});
		
		EnumMap<InvoiceCommunicationType,InvoiceCommunicationHistoryMapValue> map = new EnumMap<>(InvoiceCommunicationType.class);
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
		System.out.println( json.toString(1)); 
		return Optional.ofNullable(json);
	}
	
	// *************************************************************
	// ******************************************* [ISSUE] ********
	// *************************************************************
	public static InvoiceCommunicatorContext issueInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(trx -> 
				issue(ctx, communicator));
		}
	}
	
	private static InvoiceCommunicatorContext issue(AONContext ctx, final InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		try {
			check(ctx, cc);
			checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));
			if(invoice.isSales()) {
				InvoiceDAO.preIssue(ctx, invoice );
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
								VerifactuContext vc = VERIFACTU.accept(ctx,cc);
								if (vc.isResponseCorrecta(invoice.getId())) {
									InvoiceDAO.postIssue(ctx, invoice );								
								}
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
	// ******************************************* [ACCEPT] ********
	// *************************************************************
	public static InvoiceCommunicatorContext acceptInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				accept(ctx, communicator));
		}
	}
	
	private static InvoiceCommunicatorContext accept(AONContext ctx, final InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		try {
			check(ctx, cc);
			checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));

			invoice = InvoiceDAO.accept(ctx, invoice, invoice.getRawdocId().orElse(null));
			// TODO Cambiarlo cuando accept2 sea viable para la pantalla del portal.
			// invoice = InvoiceDAO.accept2(ctx, invoice, cc.getRawdocId());
					
			if(invoice.isSales() && cc.getConfig().hasCommunication() ) {
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
							VERIFACTU.accept(ctx,cc);
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
			.map(info -> info.isPartialAccepted())
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
						
						@Override
						public void visitVERIFACTU() throws InvoiceCommunicationException  {
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
		if (cc.getCompany() == null
			|| cc.getCompany().getId() == null
			|| AonStringUtils.isBlank(cc.getCompany().getDocument())
			|| AonStringUtils.isBlank(cc.getCompany().getName())) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0002);
		}
		if (cc.invoiceCount() == 0) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005);
		}
		if (cc.getConfig() == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0006);
		}
		checkCertificate(ctx, cc);
	}
	
	private static void checkCertificate(AONContext ctx, final InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		if (cc.getConfig().getCertificate() == null) {
			try {
				if (cc.getCertificateId() != null) {
					cc.getConfig().setCertificate(
						CertificateDAO.getStream(ctx, f -> f.getIdProperty().eq(cc.getCertificateId()))
							.findFirst()
							.orElse(null)
					);
//				} else {
//					cc.getConfig().setCertificate(SecurityDAO.getCertificate(ctx, cc.getUser().getId(), CertificateType.AEAT.name()));  
				}
			} catch (Exception e) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0021,e);
			}
			try {
				if (cc.getConfig().getCertificate() != null 
				 && !checkCert(cc.getConfig().getCertificate())) {
					throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0022);
				}
			} catch (Exception e) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0022,e);
			}
			if(cc.getConfig().getCertificate() == null || cc.getConfig().getCertificate().isEmpty()) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0023);
			}
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
					.map(icm -> InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,icm.getCode(),icm.getMessage()) )
					.collect(Collectors.toCollection(LinkedList::new))
				)
			.map( es -> new InvoiceErrorException(es) )
			.findFirst()
			.orElse( new InvoiceErrorException(InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,"",e.getMessage())))
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
				InvoiceCommunicationHistoryMapValue lroe = h.computeIfAbsent(InvoiceCommunicationType.TBAI, 
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
				lroe.add(new InvoiceCommunicationHistory()
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

