package net.aonsolutions.aon.invoice.communication;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.verifactu.VERIFACTU;
import net.aonsolutions.aon.verifactu.VerifactuContext;

public class InvoiceCommunicator {
	
	private InvoiceCommunicator() {
	}

	// *************************************************************
	// ******************************************* [HISTORY] *******
	// *************************************************************
	public static List<InvoiceCommunicationHistory> history(Occam occam, Integer invoiceId) throws InvoiceCommunicationException {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			InvoiceCommunicationConfiguration config = InvoiceCommunicationConfigurationDAO.get(ctx, ctx.getDomainId());
			return history(ctx, config, invoiceId);
		}
	}
	
	private static List<InvoiceCommunicationHistory> history(AONContext ctx, final InvoiceCommunicationConfiguration config, final Integer invoiceId ) throws InvoiceCommunicationException{
		return InvoiceCommunicationDAO.getHistory(ctx, invoiceId, new Function<InvoiceCommunicationHistory, List<String>>() {
			
			@Override
			public List<String> apply(InvoiceCommunicationHistory t) {
				try {
					config.getType().visit( new InvoiceCommunicationTypeVisitor() {
						
						@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
						@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
						@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
						@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
						@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
						@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }
						
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
			}
		});
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
					cc.getConfig().getType().visit( new InvoiceCommunicationTypeVisitor() {
						
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
				cc.getConfig().getType().visit( new InvoiceCommunicationTypeVisitor() {

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
				cc.getConfig().getType().visit( new InvoiceCommunicationTypeVisitor() {
	
					@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
					@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
					@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
					@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
					@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
					@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }

					@Override
					public void visitVERIFACTU() throws InvoiceCommunicationException  {
						VERIFACTU.cancel(ctx,cc);
					}
				});
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


}

