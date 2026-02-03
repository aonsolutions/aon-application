package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceProcessOutput implements Serializable {
	
	private static final long serialVersionUID = -4479134079637305099L;
	
	private InvoiceErrorLevel processErrorLevel;
	private String processMessage;
	private LinkedList<Invoice> invoices = new LinkedList<>();
	private Integer fromId = null;
	private Integer toId = null;
	private InvoiceCollectionInfo invoicesInfo = new InvoiceCollectionInfo();
	private InvoiceCollectionInfo proformasInfo = new InvoiceCollectionInfo();
	
	public Stream<Invoice> invoiceStream() {
		return AonCollectionUtils.stream(invoices);
	}
	public void addInvoice(Invoice invoice) {
		invoices.add(invoice);
		if ( fromId == null ) fromId = invoice.getId();
		toId = invoice.getId();
		if ( invoice.isProforma() ) {
			proformasInfo.addInvoice( invoice );
		} else {
			invoicesInfo.addInvoice( invoice );
		}
		invoice.getMoreSeriousLevel()
			.ifPresent(level -> setProcessErrorLevel( InvoiceErrorLevel.mostSeriousLevel(processErrorLevel, level)));
	}

	public Optional<InvoiceErrorLevel> getProcessErrorLevel() {
		return Optional.ofNullable(processErrorLevel);
	}
	public InvoiceProcessOutput setProcessErrorLevel(InvoiceErrorLevel processErrorLevel) {
		this.processErrorLevel = processErrorLevel;
		return this;
	}
	
	public String getProcessMessage() {
		return processMessage;
	}
	public InvoiceProcessOutput setProcessMessage(String processMessage) {
		this.processMessage = processMessage;
		return this;
	}
	
	public Integer getFromId() {
		return this.fromId;
	}
	public InvoiceProcessOutput setFromId(Integer fromId) {
		this.fromId = fromId;
		return this;
	}
	
	public Integer getToId() {
		return this.toId;
	}
	public InvoiceProcessOutput setToId(Integer toId) {
		this.toId = toId;
		return this;
	}
	
	public InvoiceCollectionInfo getInvoicesInfo() {
		return invoicesInfo;
	}
	public InvoiceProcessOutput setInvoicesInfo(InvoiceCollectionInfo invoicesInfo) {
		this.invoicesInfo = invoicesInfo;
		return this;
	}
	public InvoiceCollectionInfo getProformasInfo() {
		return proformasInfo;
	}
	public InvoiceProcessOutput setProformasInfo(InvoiceCollectionInfo proformasInfo) {
		this.proformasInfo = proformasInfo;
		return this;
	}
	
	public static Collector<Invoice, InvoiceProcessOutput, InvoiceProcessOutput> collector() {
		return collector( InvoiceProcessOutput::new );
	}	
	public static Collector<Invoice, InvoiceProcessOutput, InvoiceProcessOutput> collector( Supplier<InvoiceProcessOutput> supplier ) {
	    return Collector.of(
	        supplier,      						// supplier
	        InvoiceProcessOutput::addInvoice, 	// accumulator
	        (left, right) -> {               	// combiner
	            right.invoiceStream()
	            	.forEach(left::addInvoice);
	            return left;
	        },
	        Collector.Characteristics.UNORDERED
	    );
		
	}
}
