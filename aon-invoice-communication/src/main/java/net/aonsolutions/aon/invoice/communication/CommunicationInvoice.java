package net.aonsolutions.aon.invoice.communication;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;

public class CommunicationInvoice {

	Company company;
	Invoice invoice;
	
	public Company getCompany() {
		return company;
	}
	
	public CommunicationInvoice setCompany(Company company) {
		this.company = company;
		return this;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	
	public CommunicationInvoice setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}

	public void alta(ICommunicationInvoiceVisitor visitor) throws Exception {
		visitor.alta(this);
	}
	
	public void baja(ICommunicationInvoiceVisitor visitor) throws Exception {
		visitor.baja(this);
	}
}
