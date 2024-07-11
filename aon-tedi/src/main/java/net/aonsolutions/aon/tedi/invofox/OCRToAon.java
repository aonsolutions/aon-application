package net.aonsolutions.aon.tedi.invofox;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.watson.util.Pair;

import net.aonsolutions.invofox.model.OCRDocumentResponse;

public class OCRToAon {
	
	private OCRToAon() {
		
	}
	
	public static Pair<Invoice,Rawdoc> toAON(Occam occam, OCRDocumentResponse docResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			return ctx.getDslContext().transactionResult(configuration -> {
				OCRResult result = OCRInvoiceBuilder.toInvoice(ctx, docResponse.getDocument().get() );
				Invoice invoice = result.getInvoice();
				
				Invoice savedInvoice = null;
				Rawdoc savedRawdoc = null;
				try {
					savedInvoice = AON.acceptInvoice(ctx, invoice, null);
				} catch( Exception e ) {
					savedRawdoc = RawdocDAO.save(ctx, new Rawdoc()
						.setDomain( invoice.getDomain() )
						.setNature( RawdocNature.INVOICE )
						.setType( invoice.isSales()?RawdocType.OUTPUT:RawdocType.INPUT)
						.setStatus( RawdocStatus.INBOX )
						.setJson( InvoiceJSON.toJSON(result.getInvoice()).toString() ));
				}
				return Pair.of(savedInvoice, savedRawdoc );
			});
		}
	}
		
}



