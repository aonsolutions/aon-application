package net.aonsolutions.aon.invoice.communication.visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import net.aonsolutions.aon.sii.SIIManager;
import net.aonsolutions.aon.tbai.TBAI;
import net.aonsolutions.aon.verifactu.VERIFACTU;

public class CancelInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements IInvoiceCommunicationTypeVisitor {

	public CancelInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice) {
		super(domain, user, invoice);
	}
	
	public CancelInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		super(domain, user, invoice, certificateId);
	}
	
	@Override
	public void visitSII() throws Exception {
		SIIManager manager = SIIManager.getInstance(getSiiConfiguration());
		
		AccountingReportParams params = new AccountingReportParams();
		params.setDomain(getDomain().getId());
		params.setInvoices(new Integer[] {getInvoice().getId()});
		
		LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(getDomain().getName(), getDomain().getId(), getUser().getLogin(), params, "")
				.collect(Collectors.toCollection(LinkedList::new));
			
		manager.bajaFacturas(getDomain(), getUser().getLogin(), getCompany(), getInvoice(), contextList, null);
	}

	@Override
	public void visitTBAI() {
		try {
			TBAI.getInstance().cancel(getTbaiConfiguration(), getCompany(), getInvoice());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visitLROE() {
		try {
			TBAI.getInstance().cancel(getTbaiConfiguration(), getCompany(), getInvoice());
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
		if(InvoiceType.SALES.equals(getInvoice().getType())) {
			try {
				List<Invoice> list = new LinkedList<>();
				list.add(getInvoice());
				VERIFACTU.cancel(getVerifactuConfiguration(), getCompany(), list, getVerifactuBlockchain(), getUser().getLogin());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
