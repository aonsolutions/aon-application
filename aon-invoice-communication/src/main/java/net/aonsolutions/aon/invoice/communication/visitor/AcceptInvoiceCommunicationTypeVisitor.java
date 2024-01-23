package net.aonsolutions.aon.invoice.communication.visitor;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import net.aonsolutions.aon.tbai.InvoiceCommunication;
import net.aonsolutions.aon.tbai.LroeMain;
import net.aonsolutions.aon.tbai.TBAI;
import net.aonsolutions.aon.tbai.TbaiMain;

public class AcceptInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements IInvoiceCommunicationTypeVisitor {
		
	public AcceptInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice) {
		super(domain, user, invoice);
	}
	
	public AcceptInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		super(domain, user, invoice, certificateId);
	}

	@Override
	public void visitSII() {		
		
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
			TbaiMain tbai = new TbaiMain();
			try {
				tbai.createEmisionTBAI(getCompany(), getInvoice(), getTbaiConfiguration());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void visitLROE() {
		if(InvoiceType.SALES.equals(getInvoice().getType())) visitTBAI();
		else {
			Company company = getCompany();
			InvoiceCommunication ic = new InvoiceCommunication()
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
			lroe.alta(ic);
		}
	}
	
	@Override
	public void visitSERES() {		
		
	}

}
