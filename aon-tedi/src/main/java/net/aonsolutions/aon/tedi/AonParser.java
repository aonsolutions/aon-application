package net.aonsolutions.aon.tedi;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediInvoiceTransaction;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediRegistry;

public class AonParser {
	
	public TediInvoice aon2Tedi(Domain domain, String login, Integer invoiceId) {
		Invoice invoice = AON.getInvoice(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(invoiceId));
		Company company = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));
		Registry registry = AON.getRegistry(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(invoice.getRegistry()));
		LinkedList<InvoiceTax> taxes = AON.getInvoiceTaxStream(domain.getName(), domain.getId(), login, invoiceId)
			.collect(Collectors.toCollection(LinkedList::new));
		LinkedList<Finance> finances = AON.getFinanceList(domain.getName(), domain.getId(), login, f -> f.getInvoiceProperty().eq(invoiceId));
		return aon2Tedi(invoice, company, registry, taxes, finances);
	}
	
	public TediInvoice aon2Tedi(Invoice invoice, Company company, Registry registry,
			LinkedList<InvoiceTax> taxes, LinkedList<Finance> finances){
		return new TediInvoice()
				.setCompany(company.getDocument())
				.setSeries(invoice.getSeries())
				.setNumber(invoice.getNumber())
				.setReference(invoice.getReferenceCode())
				.setType(getInvoiceType(invoice.getType()))// TODO
				.setDate(invoice.getIssueDate())
				.setTransaction(getTransaction(invoice.getTransaction()))
//				.setCategory()
				.setTotal(invoice.getTotal())
				.setSender(InvoiceType.SALES.equals(invoice.getType())
						? getRegistry(company)
						: getRegistry((Company) registry)
				)
				.setReceiver(InvoiceType.SALES.equals(invoice.getType())
						? getRegistry((Company) registry)
						: getRegistry(company)
				)
				.setDetails(getDetails(invoice.getDetails()))
				.setTaxes(getTaxes(taxes))
				.setFinances(getFinances(finances));
	}
	
	public LinkedList<TediFinance> getFinances(LinkedList<Finance> finances) {
		return new LinkedList<TediFinance>();		
	}
	
	public LinkedList<TediInvoiceTax> getTaxes(LinkedList<InvoiceTax> taxes) {
		return new LinkedList<TediInvoiceTax>();
	}
	
	public TediRegistry getRegistry(Company company) {
		return new TediRegistry()
				.setDocument(company.getDocument())
				.setName(company.getName())
				.setAddress(getAddress(company.getAddress()));
	}
	
	public TediAddress getAddress(RAddress address) {
		return new TediAddress()
				.setAddress(address.getFullAddress())
				.setCity(address.getCity())
				.setProvince(address.getGeozoneName())
				.setPostalCode(address.getZip());
	}
	
	public LinkedList<TediInvoiceDetail> getDetails(LinkedList<InvoiceDetail> details) {
		return new LinkedList<TediInvoiceDetail>();
	}
	
	public TediInvoiceType getInvoiceType(InvoiceType type){
		if(InvoiceType.UNDEDUCTIBLE.equals(type))
			return TediInvoiceType.TICKET;
		else if(InvoiceType.SALES.equals(type)) {
			return TediInvoiceType.EMITIDA;
		} else return TediInvoiceType.RECIBIDA;
	}
	
	public TediInvoiceTransaction getTransaction(InvoiceTransactionType type){
		if(InvoiceTransactionType.NATIONAL.equals(type))
			return TediInvoiceTransaction.NAC;
		else if(InvoiceTransactionType.INTRACOMMUNITY.equals(type))
			return TediInvoiceTransaction.INTR;
		if(InvoiceTransactionType.EXTRACOMMUNITY.equals(type))
			return TediInvoiceTransaction.EXTR;
		if(InvoiceTransactionType.OTHER_ISP.equals(type))
			return TediInvoiceTransaction.ISP;
		else return TediInvoiceTransaction.CCM;
	}
	
	
	
	
}
