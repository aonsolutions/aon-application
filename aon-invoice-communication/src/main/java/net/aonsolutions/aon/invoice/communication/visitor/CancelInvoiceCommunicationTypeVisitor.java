package net.aonsolutions.aon.invoice.communication.visitor;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.User;

import net.aonsolutions.aon.sii.SII;
import net.aonsolutions.aon.tbai.LROE;
import net.aonsolutions.aon.tbai.TBAI;

public class CancelInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements InvoiceCommunicationTypeVisitor {

	public CancelInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice) {
		super(domain, user, invoice);
	}
	
	public CancelInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		super(domain, user, invoice, certificateId);
	}
	
	@Override
	public void visitSII() throws InvoiceCommunicationException {
		try (CloseableAONContext ctx = AONContext.getAONContext(getOccam())){			
			InvoiceCommunicatorContext context = new InvoiceCommunicatorContext(getDomain(), getUser(), getCertificateId(), getInvoices());
			SII.cancel(ctx, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visitTBAI() {
		try (CloseableAONContext ctx = AONContext.getAONContext(getOccam())){			
			InvoiceCommunicatorContext context = new InvoiceCommunicatorContext(getDomain(), getUser(), getCertificateId(), getInvoices());
			TBAI.cancel(ctx, context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visitLROE() {
		try (CloseableAONContext ctx = AONContext.getAONContext(getOccam())){			
			InvoiceCommunicatorContext context = new InvoiceCommunicatorContext(getDomain(), getUser(), getCertificateId(), getInvoices());
			LROE.cancel(ctx, context);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void visitSERES() {
		// Not implemented
	}
	
	@Override
	public void visitEMAIL() {
		// Not implemented
	}
	
	@Override
	public void visitCLOSING() {
		// Not implemented
	}
	
	@Override
	public void visitVERIFACTU() {
		// Not implemented
//		try {
//			VERIFACTU.cancel(getOccam(), getVerifactuConfiguration(), getCompany(), getInvoices(), getVerifactuBlockchain());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void visitNO_VERIFACTU() throws InvoiceCommunicationException {
		// Not implemented
	}

	@Override
	public void visitSIF() throws InvoiceCommunicationException {
		// Not implemented
	}

	@Override
	public void visitFACTURAE() throws InvoiceCommunicationException {
		// Not implemented
	}
}
