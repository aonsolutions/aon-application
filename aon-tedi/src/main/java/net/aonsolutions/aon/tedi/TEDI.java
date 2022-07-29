package net.aonsolutions.aon.tedi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.in.invoice.img.InvoiceIMGException;
import solutions.aon.in.invoice.img.InvoiceIMGParser;
import solutions.aon.in.invoice.pdf.InvoicePDFException;
import solutions.aon.in.invoice.pdf.InvoicePDFParser;

public class TEDI {
	
	private static final Logger LOGGER = Logger.getLogger(TEDI.class.getName());
	
	private static final String TEDI_CONTEXT_CAN_NOT_BE_NULL = "TediContext can not be null";
	private static final String TEDI_CONTEXT_USER_CAN_NOT_BE_NULL = "TediContext.user can not be null";
	private static final String TEDI_CONTEXT_DOMAIN_CAN_NOT_BE_NULL = "TediContext.domain can not be null";
	private static final String TEDI_CONTEXT_DOMAIN_NAME_CAN_NOT_BE_NULL = "TediContext.domainName can not be null";

	private TEDI() {
	}
	
	public static TediResult validateInvoice(TediContext tctx, TediResult result) throws TediException {
		if (tctx == null) throw new IllegalArgumentException(TEDI_CONTEXT_CAN_NOT_BE_NULL);
		boolean mustCloseCtx =  tctx.getAONContext() == null; 
		AONContext ctx = tctx.getAONContext();
		try {
			result.clearMessages();
			if (ctx == null) {
				fillAONContext( tctx );
				ctx = tctx.getAONContext();
			}
			TediValidator.validateInvoice(ctx,result);
		} catch (Exception e) {
			throw new TediException(e.getMessage());
		} finally {
			if (ctx != null && mustCloseCtx )
				(( CloseableAONContext )ctx).close();
		}
		return result;
	}

	public static TediResult parse(TediContext tctx, InputStream input, MimeType mimeType) throws TediException {
		if (tctx == null) throw new IllegalArgumentException(TEDI_CONTEXT_CAN_NOT_BE_NULL);
		boolean mustCloseCtx =  tctx.getAONContext() == null; 
		try {
			if (tctx.getAONContext() == null) { 
				fillAONContext( tctx );
			}
			if (tctx.getAonConfiguration() == null) {
				tctx.setAonConfiguration( ConfigurationDAO.getConfiguration(tctx.getAONContext()) );
			}
			if (tctx.getAonConfiguration() == null || tctx.getAonConfiguration().getCompany() == null) {
				throw new TediException("No se ha encontrado una compa\u00F1ia v\u00E1lida " 
					+ "para el dominio " + "( " + tctx.getDomain() + " - " + tctx.getDomainName() +")");
			}
			LOGGER.info("[TEDI] Attempt to parse document for [" + tctx.getDomainName() + "]");
			TediInvoiceBuilder tediInvoiceBuilder = new TediInvoiceBuilder( tctx );
			
			if ( mimeType == null )
				throw new TediException( "Formato desconocido" ); 
			else if ( mimeType.isPDF() )
				InvoicePDFParser.parse(input, tediInvoiceBuilder);
			else if ( mimeType.isImage() )
				InvoiceIMGParser.parse(input, tediInvoiceBuilder);
			else
				throw new TediException( String.format("Formato, '%s' no soportado", mimeType.getName() ) ); 
			
			return TediParser.toFullInvoice(tctx.getAONContext(), tctx.getAonConfiguration(), tediInvoiceBuilder.get()); 
		} catch (InvoicePDFException | InvoiceIMGException e) {
			e.printStackTrace();
			throw new TediException(e.getMessage());
		} finally {
			if (tctx.getAONContext() != null && mustCloseCtx ) {
				(( CloseableAONContext ) tctx.getAONContext()).close();
				tctx.setAONContext(null);
			}
		}
	}
	
	private static void fillAONContext(TediContext tctx) {
		if (tctx.getDomainName() == null) throw new IllegalArgumentException(TEDI_CONTEXT_DOMAIN_NAME_CAN_NOT_BE_NULL);		
		if (tctx.getDomain() == null) throw new IllegalArgumentException(TEDI_CONTEXT_DOMAIN_CAN_NOT_BE_NULL);
		if (tctx.getUser() == null) throw new IllegalArgumentException(TEDI_CONTEXT_USER_CAN_NOT_BE_NULL);
		tctx.setAONContext(AONContext.getAONContext(tctx.getDomainName(), tctx.getDomain(), tctx.getUser()));
	}

	public static TediResult fromRawdoc(TediContext tctx, int rawdocId) throws TediException {
		if (tctx == null) throw new IllegalArgumentException(TEDI_CONTEXT_CAN_NOT_BE_NULL);
		boolean mustCloseCtx =  tctx.getAONContext() == null; 
		AONContext ctx = tctx.getAONContext();
		try {
			if (ctx == null) {
				fillAONContext(tctx); 
				ctx = tctx.getAONContext();
			}
			if (tctx.getAonConfiguration() == null) {
				tctx.setAonConfiguration(ConfigurationDAO.getConfiguration(ctx));
			} 
			if (tctx.getAonConfiguration() == null || tctx.getAonConfiguration().getCompany() == null) {
				throw new TediException("No se ha encontrado una compa\u00F1ia v\u00E1lida "
					+ "para el dominio " + "( " + ctx.getDomainId() + " - " + ctx.getDomainName() +")");
			}
			Rawdoc rawdoc = RawdocDAO.get(ctx, rawdocId);
			if (rawdoc == null) {
				throw new TediException("No se ha encontrado el documento " + rawdocId + "en el dominio " + "( " + ctx.getDomainId() + " - " + ctx.getDomainName() +")");
			}
			TediResult result = null;
			if ( AonStringUtils.isBlank( rawdoc.getJson() )) {
				rawdoc = RawdocDAO.getFull(ctx, rawdocId);
				result = parse(tctx, new ByteArrayInputStream(rawdoc.getData()), rawdoc.getMimeType());
			} else {
				result = TediParser.toFullInvoice(ctx, tctx.getAonConfiguration(), rawdoc);
			}
			Attach attach = new Attach();
			attach.setId(rawdoc.getId());
			attach.setAttachType(AttachType.INVOICE);
			attach.setMimeType( rawdoc.getMimeType());
			attach.setData(rawdoc.getData());
			attach.setAttachURL("RAWDOC");
			result.getAccountingInvoice().setAttach(attach);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TediException(e.getMessage());
		} finally {
			if (ctx != null && mustCloseCtx)
				(( CloseableAONContext)ctx).close();
		}
	}
}
