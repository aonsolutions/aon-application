package com.esferalia.aon.occam.api.model.finance;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceProcessOutput implements java.io.Serializable {
	
	private static final long serialVersionUID = -4479134079637305099L;
	
	private LinkedList<Invoice> invoices = new LinkedList<>();
	private InvoiceErrorLevel processErrorLevel;
	private String processMessage;
	
	public Stream<Invoice> invoiceStream() {
		return AonCollectionUtils.stream(invoices);
	}
	public void addInvoice(Invoice invoice) {
		invoices.add(invoice);
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
