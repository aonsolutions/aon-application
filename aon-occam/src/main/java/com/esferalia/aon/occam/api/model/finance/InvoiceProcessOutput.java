package com.esferalia.aon.occam.api.model.finance;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceProcessOutput implements java.io.Serializable {
	
	private static final long serialVersionUID = -4479134079637305099L;
	
	private InvoiceErrorLevel processErrorLevel;
	private String processMessage;
	private LinkedList<Invoice> invoices = new LinkedList<>();
	private Integer fromId = null;
	private Integer toId = null;
	private int totalCount;
	private double totalAmount;
	private double totalVAT;
	private double totalRetention;
	private int totalPrepaymentCount;
	
	public Stream<Invoice> invoiceStream() {
		return AonCollectionUtils.stream(invoices);
	}
	public void addInvoice(Invoice invoice) {
		invoices.add(invoice);
		if ( fromId == null ) fromId = invoice.getId();
		toId = invoice.getId();
		addTotalCount();
		addTotalAmount( invoice.getTotal() );
		addTotalVAT( invoice.getVatQuota() );
		addTotalRetention( invoice.getRetentionQuota() );
		addPrepaymentCount( invoice.hasPrepayments() );
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
	public Integer getToId() {
		return this.toId;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	public InvoiceProcessOutput setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		return this;
	}
	private InvoiceProcessOutput addTotalCount() {
		this.totalCount++;
		return this;
	}
	
	public double getTotalAmount() {
		return totalAmount;
	}
	public InvoiceProcessOutput setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}
	private InvoiceProcessOutput addTotalAmount(double amount) {
		this.totalAmount = AonMathUtils.round( this.totalAmount + amount);
		return this;
	}
	
	public double getTotalVAT() {
		return totalVAT;
	}
	public InvoiceProcessOutput setTotalVAT(double totalVAT) {
		this.totalVAT = totalVAT;
		return this;
	}
	private InvoiceProcessOutput addTotalVAT(double vat) {
		this.totalVAT = AonMathUtils.round( this.totalVAT + vat);
		return this;
	}
	
	public double getTotalRetention() {
		return totalRetention;
	}
	public InvoiceProcessOutput setTotalRetention(double totalRetention) {
		this.totalRetention = totalRetention;
		return this;
	}
	private InvoiceProcessOutput addTotalRetention(double retention) {
		this.totalRetention = AonMathUtils.round( this.totalRetention + retention);
		return this;
	}
	
	public int getTotalPrepaymentCount() {
		return totalPrepaymentCount;
	}
	public InvoiceProcessOutput setTotalPrepaymentCount(int totalPrepaymentCount) {
		this.totalPrepaymentCount = totalPrepaymentCount;
		return this;
	}
	private InvoiceProcessOutput addPrepaymentCount(boolean hasPrepayments) {
		totalPrepaymentCount += hasPrepayments ? 1 : 0;
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
