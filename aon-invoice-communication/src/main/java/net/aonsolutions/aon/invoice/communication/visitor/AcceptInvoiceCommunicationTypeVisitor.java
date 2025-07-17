package net.aonsolutions.aon.invoice.communication.visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import net.aonsolutions.aon.sii.SIIManager;
import net.aonsolutions.aon.tbai.InvoiceCommunication;
import net.aonsolutions.aon.tbai.LroeMain;
import net.aonsolutions.aon.tbai.TBAI;
import net.aonsolutions.aon.tbai.TbaiMain;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.verifactu.VERIFACTU;

public class AcceptInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements IInvoiceCommunicationTypeVisitor {
		
	public AcceptInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice) {
		super(domain, user, invoice);
	}
	
	public AcceptInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
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
		
		manager.suministroFacturas(getDomain(), getUser().getLogin(), getCompany(), getInvoice(), contextList, null);
	}

	@Override
	public void visitTBAI() {
		if(InvoiceType.SALES.equals(getInvoice().getType())) {
			try {
				TBAI.getInstance().accept(getTbaiConfiguration(), getCompany(), getInvoice(), 
						getBlockchain(getCertificateId()));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void visitLROE() throws Exception {
		if(InvoiceType.SALES.equals(getInvoice().getType())) visitTBAI();
		else {
			Company company = getCompany();
			InvoiceCommunication ic = new InvoiceCommunication()
					.setDomain(company.getDomain())
					.setCompany(company)
					.setInvoice(getInvoice())
					.setOperation(InvoiceCommunicationOperation.REGISTER)
					.setTbaiConfiguration(getTbaiConfiguration())
					.setType(InvoiceCommunicationType.LROE)
					.setPerson(isPersonaFisica(company.getDocument()) 
							? getPerson(company.getId()) : null)
					.setModel(isPersonaFisica(company.getDocument()) 
							? FiscalModelType.M140 : FiscalModelType.M240);
			
			LroeMain lroe = new LroeMain();
			LROEResponse resp = lroe.alta(ic);
			if(resp.isError()) {
				throw new Exception(resp.getErrorMessage());
			}
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
				VERIFACTU.getInstance().accept(getVerifactuConfiguration(), getCompany(), list, 
						getVerifactuBlockchain(getInvoice().getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
