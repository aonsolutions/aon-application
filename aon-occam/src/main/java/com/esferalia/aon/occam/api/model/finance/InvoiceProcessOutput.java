package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceProcessOutput implements Serializable {
	
	public static class InvoicesInfo implements Serializable {
		private static final long serialVersionUID = -6061576110389791837L;
		private int totalCount;
		private double totalAmount;
		private double totalVAT;
		private double totalRetention;
		private int totalPrepaymentCount;
		
		public int getTotalCount() {
			return totalCount;
		}
		public InvoicesInfo setTotalCount(int totalCount) {
			this.totalCount = totalCount;
			return this;
		}
		
		public double getTotalAmount() {
			return totalAmount;
		}
		public InvoicesInfo setTotalAmount(double totalAmount) {
			this.totalAmount = totalAmount;
			return this;
		}
		
		public double getTotalVAT() {
			return totalVAT;
		}
		public InvoicesInfo setTotalVAT(double totalVAT) {
			this.totalVAT = totalVAT;
			return this;
		}
		
		public double getTotalRetention() {
			return totalRetention;
		}
		public InvoicesInfo setTotalRetention(double totalRetention) {
			this.totalRetention = totalRetention;
			return this;
		}
		
		public int getTotalPrepaymentCount() {
			return totalPrepaymentCount;
		}
		public InvoicesInfo setTotalPrepaymentCount(int totalPrepaymentCount) {
			this.totalPrepaymentCount = totalPrepaymentCount;
			return this;
		}
		
		public void addInvoice(Invoice invoice) {
			addTotalCount();
			addTotalAmount( invoice.getTotal() );
			addTotalVAT( invoice.getVatQuota() );
			addTotalRetention( invoice.getRetentionQuota() );
			addPrepaymentCount( invoice.hasPrepayments() );
		}
		
		private InvoicesInfo addTotalCount() {
			this.totalCount++;
			return this;
		}
		private InvoicesInfo addTotalAmount(double amount) {
			this.totalAmount = AonMathUtils.round( this.totalAmount + amount);
			return this;
		}
		private InvoicesInfo addTotalVAT(double vat) {
			this.totalVAT = AonMathUtils.round( this.totalVAT + vat);
			return this;
		}
		private InvoicesInfo addTotalRetention(double retention) {
			this.totalRetention = AonMathUtils.round( this.totalRetention + retention);
			return this;
		}
		private InvoicesInfo addPrepaymentCount(boolean hasPrepayments) {
			totalPrepaymentCount += hasPrepayments ? 1 : 0;
			return this;
		}
		
	}
	
	private static final long serialVersionUID = -4479134079637305099L;
	
	private InvoiceErrorLevel processErrorLevel;
	private String processMessage;
	private LinkedList<Invoice> invoices = new LinkedList<>();
	private Integer fromId = null;
	private Integer toId = null;
	private InvoicesInfo invoicesInfo = new InvoicesInfo();
	private InvoicesInfo proformasInfo = new InvoicesInfo();
	
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
	
	public InvoicesInfo getInvoicesInfo() {
		return invoicesInfo;
	}
	public InvoiceProcessOutput setInvoicesInfo(InvoicesInfo invoicesInfo) {
		this.invoicesInfo = invoicesInfo;
		return this;
	}
	public InvoicesInfo getProformasInfo() {
		return proformasInfo;
	}
	public InvoiceProcessOutput setProformasInfo(InvoicesInfo proformasInfo) {
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
