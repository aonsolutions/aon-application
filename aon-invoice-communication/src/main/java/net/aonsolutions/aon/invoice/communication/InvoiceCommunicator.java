package net.aonsolutions.aon.invoice.communication;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDocDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceJSONUtils;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.verifactu.VERIFACTU;

public class InvoiceCommunicator {
	
	private InvoiceCommunicator() {
	}
	
		
	public static CommunicatorContext acceptInvoice(final CommunicatorContext communicator) {
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				accept(ctx, communicator));
		}
	}

	public static CommunicatorContext cancelInvoice(final CommunicatorContext communicator) {
		try (CloseableAONContext ctx = AONContext.getAONContext(communicator.getDomain(), communicator.getUser())) {
			return ctx.getDslContext().transactionResult(configuration -> 
				cancel(ctx, communicator));
		}
	}

	// *************************************************************
	// ******************************************* [ACCEPT] ********
	// *************************************************************
	private static CommunicatorContext accept(AONContext ctx, final CommunicatorContext cc) {
		try {
			fillCompany( ctx, cc);
			Invoice invoice = cc.getInvoice();
			if(invoice.isSales()) {
				if (cc.getConfig() == null) {
					cc.setConfig( InvoiceCommunicationConfigurationDAO.get(ctx) );
				}
				if (cc.getConfig() == null) {
					throw new AonCoreException("No se ha encontrado la configuración de comunicación de facturas.");
				}
				// third Part???
				if(cc.getConfig().isVerifactu()) {
					Certificate certificate = checkCertificate(ctx, cc);
					cc.getConfig().setCertificate(certificate);
					invoiceValidation(invoice);
				}
			}
			
			invoice = InvoiceDAO.accept(ctx, invoice, cc.getRawdocId());
			// TODO Cambiarlo cuando accept2 sea viable para la pantalla del portal.
			// invoice = InvoiceDAO.accept2(ctx, invoice, cc.getRawdocId());
			
			// TODO ---> JSON VERSION 2.0???
			// cc.setOutputInvoiceJSON( InvoiceJSON.to(invoice)
			//	.orElseThrow(() --> new AonCoreException("Error al convertir la factura a JSON.")) );
			// -----
			cc.setOutputInvoiceJSON( InvoiceJSON.toJSON(invoice) );
			
			
			if(invoice.isSales() && cc.getConfig().hasCommunication() ) {
				cc.getConfig().getType().visit( new IInvoiceCommunicationTypeVisitor() {

					@Override public void visitSERES() 		{ throw new AonCoreException("El tipo de comunicación SERES no está implementado."); }
					@Override public void visitEMAIL() 		{ throw new AonCoreException("El tipo de comunicación EMAIL no está implementado."); }
					@Override public void visitCLOSING() 	{ throw new AonCoreException("El tipo de comunicación CLOSING no está implementado."); }
					@Override public void visitSII() 		{ throw new AonCoreException("El tipo de comunicación SII no está implementado."); }
					@Override public void visitTBAI() 		{ throw new AonCoreException("El tipo de comunicación TBAI no está implementado."); }
					@Override public void visitLROE() 		{ throw new AonCoreException("El tipo de comunicación LROE no está implementado."); }

					@Override
					public void visitVERIFACTU() throws Exception  {
						List<Invoice> invoices = new LinkedList<>();
						invoices.add(cc.getInvoice());
						DataResponse response = VERIFACTU.accept(ctx,cc.getConfig(),cc.getCompany(),invoices);
						cc.setResponse(response);
						
						InvoiceDataDAO.get(ctx, cc.getInvoice().getId(),InvoiceData.VERIFACTU_QR)
						.ifPresent( d -> {
							cc.getOutputInvoiceJSON().put(IJsonNames.VERIFACTU, true);
							cc.getOutputInvoiceJSON().put(IJsonNames.VERIFACTU_URL, d.getValue());
						});
					}
				});
			}
			
			JSONObject fileJson = InvoiceJSONUtils.buildInvoiceFileJSON(ctx, cc.getDomain(), cc.getUser().getLogin(), invoice);
			cc.getOutputInvoiceJSON().put(IJsonNames.FILE, fileJson);

			return cc;
		} catch (Exception e) {
			throw new AonCoreException("Error al aceptar la factura.", e);
		}
	}

	// *************************************************************
	// ******************************************* [CANCEL] ********
	// *************************************************************
	public static CommunicatorContext cancel(AONContext ctx, final CommunicatorContext cc) {
		try {
			fillCompany( ctx, cc);
			Invoice invoice = cc.getInvoice();
			if(invoice.isSales()) {
				if (cc.getConfig() == null) {
					cc.setConfig( InvoiceCommunicationConfigurationDAO.get(ctx) );
				}
				if (cc.getConfig() == null) {
					throw new AonCoreException("No se ha encontrado la configuración de comunicación de facturas.");
				}
				// third Part???
				if(cc.getConfig().isVerifactu()) {
					Certificate certificate = checkCertificate(ctx, cc);
					cc.getConfig().setCertificate(certificate);
					invoiceValidation(invoice);
				}
			}
			
			// TODO ---> JSON VERSION 2.0???
			// cc.setOutputInvoiceJSON( InvoiceJSON.to(invoice)
			//	.orElseThrow(() --> new AonCoreException("Error al convertir la factura a JSON.")) );
			// -----
			cc.setOutputInvoiceJSON( InvoiceJSON.toJSON(invoice) );
			
			
			if(invoice.isSales() && cc.getConfig().hasCommunication() ) {
				cc.getConfig().getType().visit( new IInvoiceCommunicationTypeVisitor() {
	
					@Override public void visitSERES() 		{ throw new AonCoreException("El tipo de comunicación SERES no está implementado."); }
					@Override public void visitEMAIL() 		{ throw new AonCoreException("El tipo de comunicación EMAIL no está implementado."); }
					@Override public void visitCLOSING() 	{ throw new AonCoreException("El tipo de comunicación CLOSING no está implementado."); }
					@Override public void visitSII() 		{ throw new AonCoreException("El tipo de comunicación SII no está implementado."); }
					@Override public void visitTBAI() 		{ throw new AonCoreException("El tipo de comunicación TBAI no está implementado."); }
					@Override public void visitLROE() 		{ throw new AonCoreException("El tipo de comunicación LROE no está implementado."); }
	
					@Override
					public void visitVERIFACTU() throws Exception  {
						List<Invoice> invoices = new LinkedList<>();
						invoices.add(cc.getInvoice());
						DataResponse response = VERIFACTU.cancel(ctx,cc.getConfig(),cc.getCompany(),invoices);
						cc.setResponse(response);
					}
				});
			}
			
			JSONObject fileJson = InvoiceJSONUtils.buildInvoiceFileJSON(ctx, cc.getDomain(), cc.getUser().getLogin(), invoice);
			cc.getOutputInvoiceJSON().put(IJsonNames.FILE, fileJson);
	
			return cc;
		} catch (Exception e) {
			throw new AonCoreException("Error al aceptar la factura.", e);
		}
	}
	

	// *************************************************************
	// ******************************************* [PRIVATE] *******
	// *************************************************************
	private static void fillCompany(AONContext ctx, CommunicatorContext cc) {
		Company company = CompanyDAO.getByDomain(ctx, cc.getDomain().getId());
		if (company == null) {
			throw new AonCoreException("Company not found for domain: " + cc.getDomain().getId());
		}
		cc.setCompany(company);
	}
	
	private static void invoiceValidation(Invoice invoice) {
		if(invoice.isRectifier() && AonStringUtils.isBlank(invoice.getSeries())) {
			throw new AonCoreException("Las Facturas rectificativas tienen que tener serie.");
		}
		
		Date date = AonDateUtils.getDateWithoutTime(invoice.getIssueDate());
		if(date.after(new Date())) {
			throw new AonCoreException("Las Fecha de la factura no puede ser superior a la fecha actual.");
		}

		if(AonStringUtils.isBlank(invoice.getRegistryDocument()) 
				&& !invoice.isSimplified()) {
			throw new AonCoreException("El Documento del cliente está vacio.");
		}
			
		if(Country.ES.equals(invoice.getRegistryDocumentCountry()) 
				&& !AonDocumentUtil.isValid(invoice.getRegistryDocument())
				&& !invoice.isSimplified()) {
			throw new AonCoreException("El Documento del cliente no es válido.");
		}
	}

	private static Certificate checkCertificate(AONContext ctx, final CommunicatorContext cc) {
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
			throw new AonCoreException("Error al obtener el certificado.",e);
		}
		if (cert != null) {
			try {
				if(!checkCert(cert.getData(), cert.getPassword())) {
					throw new AonCoreException("El certificado o la contraseña no son correctos.");
				}
			} catch (Exception e) {
				throw new AonCoreException("El certificado o la contraseña no son correctos.",e);			
			}		
		}
		if(cert == null || cert.isEmpty()) {
			throw new AonCoreException("El certificado no existe.");
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
	
	public static void processInvoiceFile(AONContext ctx, final CommunicatorContext cc) {
		JSONObject fileJSON = cc.getFileJSON();
		if (JsonUtils.isEmpty(fileJSON)) {
			String s3Key = JsonUtils.getString(fileJSON, IJsonNames.S3_KEY);
			String contentType = JsonUtils.getString(fileJSON, "content_type");
					
			if(s3Key != null) {
				MimeType mimetype = MimeType.safeValueFromContenType(contentType);
				InvoiceDoc invoiceDoc = new InvoiceDoc()
					.setExternalStorage(ExternalStorage.AWS)
					.setS3Bucket("aon-upload-post")
					.setS3Key(s3Key)
					.setInvoice(cc.getInvoice().getId())
					.setMimeType(mimetype != null ? mimetype : MimeType.PDF)
					.setType(InvoiceAttachmentType.INVOICE)
					.setDomain(cc.getInvoice().getDomain());
				InvoiceDocDAO.save(ctx, invoiceDoc);
			}
		}
	}

}
