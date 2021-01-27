package net.aonsolutions.aon.tedi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.in.invoice.img.InvoiceIMGParser;
import solutions.aon.in.invoice.pdf.InvoicePDFException;
import solutions.aon.in.invoice.pdf.InvoicePDFParser;

public class TEDI {
	private static Logger LOGGER = Logger.getLogger(TEDI.class.getName());

	private TEDI() {
	}
	
	public static TediResult validateInvoice(TediContext tctx, TediResult result) throws TediException {
		if (tctx == null) throw new IllegalArgumentException("TediContext can not be null");
		boolean mustCloseCtx =  tctx.getAONContext() != null; 
		AONContext ctx = tctx.getAONContext();
		try {
			result.clearMessages();
			if (ctx == null) {
				if (tctx.getDomainName() == null) throw new IllegalArgumentException("TediContext.domainName can not be null");		
				if (tctx.getDomain() == null) throw new IllegalArgumentException("TediContext.domain can not be null");
				if (tctx.getUser() == null) throw new IllegalArgumentException("TediContext.user can not be null");
				ctx = AONContext.getAONContext(tctx.getDomainName(), tctx.getDomain(), tctx.getUser());
			}
			TediValidator.validateInvoice(ctx,result);
		} catch (Throwable t) {
			throw new TediException(t.getMessage());
		} finally {
			if (ctx != null && mustCloseCtx)
				ctx.close();
		}
		return result;
	}

	public static TediResult parse(TediContext tctx, InputStream input, MimeType mimeType) throws TediException {
		if (tctx == null) throw new IllegalArgumentException("TediContext can not be null");
		boolean mustCloseCtx =  tctx.getAONContext() == null; 
		try {
			if (tctx.getAONContext() == null) {
				if (tctx.getDomainName() == null) throw new IllegalArgumentException("TediContext.domainName can not be null");		
				if (tctx.getDomain() == null) throw new IllegalArgumentException("TediContext.domain can not be null");
				if (tctx.getUser() == null) throw new IllegalArgumentException("TediContext.user can not be null");
				tctx.setAONContext(AONContext.getAONContext(tctx.getDomainName(), tctx.getDomain(), tctx.getUser()));
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
				throw new TediException( String.format("Formato desconocido") ); 
			if ( mimeType.isPDF() )
				InvoicePDFParser.parse(input, tediInvoiceBuilder);
			else if ( mimeType.isImage() )
				InvoiceIMGParser.parse(input, tediInvoiceBuilder);
			else
				throw new TediException( String.format("Formato, '%s' no soportado", mimeType.getName() ) ); 
			
			TediResultBuilder.build( tediInvoiceBuilder.get() );
			TediResult result = TediParser.toFullInvoice(tctx.getAONContext(), tctx.getAonConfiguration(), tediInvoiceBuilder.get()); 
			return result;
		} catch (InvoicePDFException e) {
			e.printStackTrace();
			throw new TediException(e.getMessage());
		} finally {
			if (tctx.getAONContext() != null && mustCloseCtx) {
				tctx.getAONContext().close();
				tctx.setAONContext(null);
			}
		}
	}
	
	public static TediResult fromRawdoc(TediContext tctx, int rawdocId) throws TediException {
		if (tctx == null) throw new IllegalArgumentException("TediContext can not be null");
		boolean mustCloseCtx =  tctx.getAONContext() == null; 
		AONContext ctx = tctx.getAONContext();
		try {
			if (ctx == null) {
				if (tctx.getDomainName() == null) throw new IllegalArgumentException("TediContext.domainName can not be null");		
				if (tctx.getDomain() == null) throw new IllegalArgumentException("TediContext.domain can not be null");
				if (tctx.getUser() == null) throw new IllegalArgumentException("TediContext.user can not be null");
				ctx = AONContext.getAONContext(tctx.getDomainName(), tctx.getDomain(), tctx.getUser());
				tctx.setAONContext(ctx);
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
				result = TediParser.toFullInvoice(ctx, tctx.getAonConfiguration(), rawdoc.getTediInvoice());
			}
			Attach attach = new Attach();
			attach.setId(rawdoc.getId());
			attach.setAttachType(AttachType.INVOICE);
			attach.setMimeType( rawdoc.getMimeType());
			attach.setData(rawdoc.getData());
			attach.setAttachURL("RAWDOC");
			result.getAccountingInvoice().setAttach(attach);
			return result;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new TediException(e.getMessage());
		} finally {
			if (ctx != null && mustCloseCtx)
				ctx.close();
		}
	}
}
