package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCollectionInfo;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceConsoleAnalysis implements Serializable {

	private static final long serialVersionUID = -1917839993671549270L;
	
	private LinkedList<InvoiceCommunicatorAnalysisMessage> messages = new LinkedList<>();
	private InvoiceCollectionInfo invoiceInfo = new InvoiceCollectionInfo();
	private InvoiceCollectionInfo proformaInfo = new InvoiceCollectionInfo();
	private HashMap<InvoiceCommunicationType, HashMap<InvoiceCommunicationStatus, InvoiceCollectionInfo>> typesInfo = new HashMap<>();
	
	private InvoiceCommunicationConfiguration icc;

	public Optional<InvoiceCommunicationConfiguration> getCommunicationConfiguration() {
		return Optional.ofNullable( icc );
	}
	public InvoiceConsoleAnalysis setCommunicationConfiguration(InvoiceCommunicationConfiguration icc) {
		this.icc = icc;
		return this;
	}
	
	public boolean hasMessages() {
		return AonCollectionUtils.isNotEmpty(messages);
	}
	public Stream<InvoiceCommunicatorAnalysisMessage> messages() {
		return AonCollectionUtils.stream(messages);
	}
	public InvoiceConsoleAnalysis addMessage(InvoiceErrorLevel level, String message) {
		this.messages.add(
			new InvoiceCommunicatorAnalysisMessage()
				.setLevel(level)
				.setMessage(message))
		;
		return this;
	}

	public InvoiceCollectionInfo getInvoiceInfo() {
		return invoiceInfo;
	}
	public InvoiceCollectionInfo getProformaInfo() {
		return proformaInfo;
	}
	
	public HashMap<InvoiceCommunicationType, HashMap<InvoiceCommunicationStatus, InvoiceCollectionInfo>> getTypesInfo() {
		return typesInfo;
	}
	
	public InvoiceConsoleAnalysis addInvoice( Invoice invoice ) {
		if (invoice.isProforma()) {
			getProformaInfo().addInvoice(invoice);
		} else {
			getInvoiceInfo().addInvoice(invoice);
			if (AonCollectionUtils.isEmpty(invoice.getCommunicationInfo())) {
				addToTypesInfo(null, invoice);
			} else {
				AonCollectionUtils.valuesStream(invoice.getCommunicationInfo())
				.forEach( info -> {
					typesInfo
					.computeIfAbsent(info.getType(), k -> new HashMap<>() ) 
					.computeIfAbsent(info.getStatus(), k -> new InvoiceCollectionInfo())
					.addInvoice(invoice);
				});
			}
		}
		return this;
	}
	
	private void addToTypesInfo(InvoiceCommunicationType ict, Invoice invoice) {
		invoice.getInvoiceInfo(ict)
			.ifPresentOrElse( 
				info -> typesInfo
					.computeIfAbsent(ict, k -> new HashMap<>() ) 
					.computeIfAbsent(info.getStatus(), k -> new InvoiceCollectionInfo())
					.addInvoice(invoice)
				,() -> typesInfo
					.computeIfAbsent(ict, k -> new HashMap<>() ) 
					.computeIfAbsent(null, k -> new InvoiceCollectionInfo())
					.addInvoice(invoice)	
			);
	}
	
	public static InvoiceConsoleAnalysis fromError(String msg) {
		InvoiceConsoleAnalysis analysis = new InvoiceConsoleAnalysis();
		analysis.addMessage(InvoiceErrorLevel.ERR,msg);
		return analysis;
	}
}
	