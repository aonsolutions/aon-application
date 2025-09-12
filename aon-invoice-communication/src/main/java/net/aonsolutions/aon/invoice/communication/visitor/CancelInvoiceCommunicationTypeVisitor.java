package net.aonsolutions.aon.invoice.communication.visitor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.security.User;

import net.aonsolutions.aon.sii.SIIManager;
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
		try {
			SIIManager manager = SIIManager.getInstance(getSiiConfiguration());
			
			AccountingReportParams params = new AccountingReportParams();
			params.setDomain(getDomain().getId());
			params.setInvoices(new Integer[] {getInvoice().getId()});
			
			LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(getDomain().getName(), getDomain().getId(), getUser().getLogin(), params, "")
					.collect(Collectors.toCollection(LinkedList::new));
			
			manager.bajaFacturas(getDomain(), getUser().getLogin(), getCompany(), getInvoice(), contextList, null);
		} catch (JAXBException | ParserConfigurationException | SOAPException | IOException e) {
			throw new InvoiceCommunicationException(e);
		}
	}

	@Override
	public void visitTBAI() {
		try {
			TBAI.cancel(getTbaiConfiguration(), getCompany(), getInvoice());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visitLROE() {
		try {
			TBAI.cancel(getTbaiConfiguration(), getCompany(), getInvoice());
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
}
