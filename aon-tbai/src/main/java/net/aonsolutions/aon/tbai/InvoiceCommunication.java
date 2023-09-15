package net.aonsolutions.aon.tbai;

import java.util.List;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.OldInvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public class InvoiceCommunication {
	
	private FiscalModelType model;
	private OldInvoiceCommunicationType type;
	private InvoiceCommunicationOperation operation;
	private Company company;
	private Person person;
	
	private TbaiConfiguration tbaiConfiguration;
	private SiiConfiguration siiConfiguration;
	private Invoice invoice;
	private List<Invoice> invoices;
	
	public FiscalModelType getModel() {
		return model;
	}
	public InvoiceCommunication setModel(FiscalModelType model) {
		this.model = model;
		return this;
	}
	public OldInvoiceCommunicationType getType() {
		return type;
	}
	public InvoiceCommunication setType(OldInvoiceCommunicationType type) {
		this.type = type;
		return this;
	}
	public InvoiceCommunicationOperation getOperation() {
		return operation;
	}
	public InvoiceCommunication setOperation(InvoiceCommunicationOperation operation) {
		this.operation = operation;
		return this;
	}
	public Company getCompany() {
		return company;
	}
	public InvoiceCommunication setCompany(Company company) {
		this.company = company;
		return this;
	}
	public Person getPerson() {
		return person;
	}
	public InvoiceCommunication setPerson(Person person) {
		this.person = person;
		return this;
	}
	public TbaiConfiguration getTbaiConfiguration() {
		return tbaiConfiguration;
	}
	public InvoiceCommunication setTbaiConfiguration(TbaiConfiguration tbaiConfiguration) {
		this.tbaiConfiguration = tbaiConfiguration;
		return this;
	}
	public SiiConfiguration getSiiConfiguration() {
		return siiConfiguration;
	}
	public InvoiceCommunication setSiiConfiguration(SiiConfiguration siiConfiguration) {
		this.siiConfiguration = siiConfiguration;
		return this;
	}
	
	public Invoice getInvoice() {
		return invoice;
	}
	
	public InvoiceCommunication setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public List<Invoice> getInvoices() {
		return invoices;
	}
	public InvoiceCommunication setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
		return this;
	}
}
