package net.aonsolutions.aon.invoice.communication;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.verifactu.VERIFACTU;

public class InvoiceCommunicator {
	
	private InvoiceCommunicator() {
	}
	
		
	public static InvoiceCommunicatorContext acceptInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				accept(ctx, communicator));
		}
	}

	public static InvoiceCommunicatorContext cancelInvoice(final InvoiceCommunicatorContext communicator) throws InvoiceCommunicationException {
		checkAONContextValues(communicator);
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				cancel(ctx, communicator));
		}
	}

	// *************************************************************
	// ******************************************* [ACCEPT] ********
	// *************************************************************
	private static InvoiceCommunicatorContext accept(AONContext ctx, final InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		try {
			check(ctx, cc);
			checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));
			fillCompany( ctx, cc);
			if(invoice.isSales()) {
				// third Part???
				if(cc.getConfig().isVerifactu()) {
					Certificate certificate = checkCertificate(ctx, cc);
					cc.getConfig().setCertificate(certificate);
					invoiceValidation(invoice);
				}
			}
			
			invoice = InvoiceDAO.accept(ctx, invoice, invoice.getRawdocId().orElse(null));
			// TODO Cambiarlo cuando accept2 sea viable para la pantalla del portal.
			// invoice = InvoiceDAO.accept2(ctx, invoice, cc.getRawdocId());
					
			if(invoice.isSales() && cc.getConfig().hasCommunication() ) {
				cc.getConfig().getType().visit( new IInvoiceCommunicationTypeVisitor() {

					@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
					@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
					@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
					@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
					@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
					@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }

					@Override
					public void visitVERIFACTU() throws InvoiceCommunicationException  {
						DataResponse response = VERIFACTU.accept(ctx,cc);
						cc.setDataResponse(response);
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
	public static InvoiceCommunicatorContext cancel(AONContext ctx, final InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		try {
			check(ctx,cc);
			checkUniqueInvoice(cc);
			Invoice invoice = cc.invoiceStream()
				.findFirst()
				.orElseThrow(() -> new InvoiceCommunicationException(InvoiceCommunicationError.AON_0005));
			fillCompany( ctx, cc);
			if(invoice.isSales()) {
				// third Part???
				if(cc.getConfig().isVerifactu()) {
					Certificate certificate = checkCertificate(ctx, cc);
					cc.getConfig().setCertificate(certificate);
					invoiceValidation(invoice);
				}
			}
			
			if(invoice.isSales() && cc.getConfig().hasCommunication() ) {
				cc.getConfig().getType().visit( new IInvoiceCommunicationTypeVisitor() {
	
					@Override public void visitSERES() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0012); }
					@Override public void visitEMAIL() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0013); }
					@Override public void visitCLOSING() throws InvoiceCommunicationException 	{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0014); }
					@Override public void visitSII() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0015); }
					@Override public void visitTBAI() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0016); }
					@Override public void visitLROE() throws InvoiceCommunicationException 		{ throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0017); }

					@Override
					public void visitVERIFACTU() throws InvoiceCommunicationException  {
						DataResponse response = VERIFACTU.cancel(ctx,cc);
						cc.setDataResponse(response);
					}
				});
			}
			return cc;
		} catch (Exception e) {
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
	
	private static Certificate checkCertificate(AONContext ctx, final InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		if (cc.getConfig().getCertificate() != null) {
			return cc.getConfig().getCertificate();
		}
		Certificate cert = null;
		try {
			if(cc.getCertificateId() != null) {
				cert = CertificateDAO.getStream(ctx, f -> f.getIdProperty().eq(cc.getCertificateId()))
					.findFirst()
					.orElse(null);
			} else {
				cert = SecurityDAO.getCertificate(ctx, cc.getUser().getId(), CertificateType.AEAT.name());  
			}
		} catch (Exception e) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0021,e);
		}
		if (cert != null) {
			try {
				if(!checkCert(cert.getData(), cert.getPassword())) {
					throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0022);
				}
			} catch (Exception e) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0022,e);
			}		
		}
		if(cert == null || cert.isEmpty()) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0023);
		}
		return cert;	
	}
	
	private static boolean checkCert(byte[] cert, String password) {
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
	
	private static void fillCompany(AONContext ctx, InvoiceCommunicatorContext cc) throws InvoiceCommunicationException {
		Company company = CompanyDAO.getByDomain(ctx, cc.getDomain().getId());
		if (company == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0002);
		}
		cc.setCompany(company);
	}
	
	private static void invoiceValidation(Invoice invoice) throws InvoiceCommunicationException {
		if(invoice.isRectifier() && AonStringUtils.isBlank(invoice.getSeries())) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0010);
		}
		
		Date date = AonDateUtils.getDateWithoutTime(invoice.getIssueDate());
		if(date.after(new Date())) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0011);
		}

		if(AonStringUtils.isBlank(invoice.getRegistryDocument()) 
				&& !invoice.isSimplified()) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0019);
		}
			
		if(Country.ES.equals(invoice.getRegistryDocumentCountry()) 
				&& !AonDocumentUtil.isValid(invoice.getRegistryDocument())
				&& !invoice.isSimplified()) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0020);
		}
	}

}

