package net.aonsolutions.aon.tedi;

import java.util.LinkedList;
import java.util.regex.Pattern;
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
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonMathUtils;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceDetail;
import es.translogia.tedi.ewok.TediInvoiceStatus;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediInvoiceTransaction;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediPayMethod;
import es.translogia.tedi.ewok.TediRegistry;
import es.translogia.tedi.ewok.TediTaxType;

public class AonParser {
	
	public TediInvoice aon2Tedi(Domain domain, String login, Integer invoiceId) {
		Invoice invoice = AON.getInvoice(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(invoiceId));
		invoice.setDetails(AON.getInvoiceDetails(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(invoiceId))
				.collect(Collectors.toCollection(LinkedList::new)));
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
						: getRegistry(registry)
				)
				.setReceiver(InvoiceType.SALES.equals(invoice.getType())
						? getRegistry(registry)
						: getRegistry(company)
				)
				.setDetails(getDetails(invoice.getDetails()))
				.setTaxes(getTaxes(taxes))
				.setFinances(getFinances(finances))
				.setStatus(TediInvoiceStatus.safeValueOf(invoice.getStatus()))
				;
	}
	
	public LinkedList<TediFinance> getFinances(LinkedList<Finance> finances) {
		return finances.stream().map(r -> new TediFinance()
				.setDueDate(r.getDueDate())
				.setAmount(r.getAmount())
				.setIban(r.getBankAccountSafeValue())
				.setPayMethod(getPaymethod(r.getPayMethodType())))
		.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public TediPayMethod getPaymethod(PayMethodType pt) {
		if(PayMethodType.CASH_BASIS.equals(pt))
			return TediPayMethod.CASH;
		else if(PayMethodType.BANK_TRANSFER.equals(pt)) 
			return TediPayMethod.TRANSFER;
		else if(PayMethodType.CREDIT_CARD.equals(pt) || PayMethodType.DEBIT_CARD.equals(pt)) 
			return TediPayMethod.CARD;
		else return TediPayMethod.BANK;
	}
	
	public LinkedList<TediInvoiceTax> getTaxes(LinkedList<InvoiceTax> taxes) {
		return taxes.stream().map(r -> new TediInvoiceTax()
					.setBase(r.getBase())
					.setPercentage(r.getPercentage())
					.setQuota(AonMathUtils.round(r.getBase()/100 * r.getPercentage(), 2))
					.setSurcharge(r.getSurcharge())
					.setSurchargeQuota(r.getSurchargeQuota())
					.setTaxType(r.getTaxType() == TaxType.RETENTION ? TediTaxType.IRPF : TediTaxType.IVA))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public TediRegistry getRegistry(Registry company) {
		return new TediRegistry()
				.setDocument(company.getDocument())
				.setName(company.getName())
				.setAddress(getAddress(company.getMainAddress()));
	}
	
	public TediAddress getAddress(RAddress address) {
		if(address == null) address = new RAddress();
 		return new TediAddress()
				.setAddress(address.getFullAddress())
				.setCity(address.getCity())
				.setProvince(address.getGeozoneName())
				.setPostalCode(address.getZip());
	}
	
	public LinkedList<TediInvoiceDetail> getDetails(LinkedList<InvoiceDetail> details) {
		return details.stream().map(r -> {
			Double amount = AonMathUtils.round( r.getQuantity() * r.getPrice());
			amount = amount - amount * (calculateDiscountExpression(r.getDiscountExpression()) / 100);
			return new TediInvoiceDetail()
					.setDescription(r.getDescription())
					.setBase(r.getTaxableBase())
					.setPrice(r.getPrice())
					.setDiscount(calculateDiscountExpression(r.getDiscountExpression()))
					.setQuantity(r.getQuantity())
					.setAmount(amount);
			}).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Double calculateDiscountExpression(String discountExpression) {
		Pattern PATTERN = Pattern.compile("\\+");
		String[] arr = PATTERN.split(discountExpression);
    	Double discount = 0.0;
		for (int i = 0; i < arr.length; i++) {
        	discount =  discount + Double.parseDouble(arr[i].trim());
    	}
		return AonMathUtils.round(discount);
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
