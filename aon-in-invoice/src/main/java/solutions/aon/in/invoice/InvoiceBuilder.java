package solutions.aon.in.invoice;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.InvoiceTax;

public interface InvoiceBuilder<T extends Object>  {
	
	void addInsightNifs( Collection<Document> nifs);
	void addInsightDates( Collection<Date> dates);
	void addInsightAmounts( Collection<Double> amounts);
	void setInsightTotals( Collection<Double> totals);
	
	
	void setIssueDate( Date date);
	void setReference( String referecne);
	double getTotal();
	void setTotal( double total);
	LinkedList<InvoiceTax> getTaxes();
	void setTax( InvoiceTax tax);

	String getSenderDocument();
	
	boolean hasIssueDate();
	boolean hasTotal();
	boolean hasReference();
	boolean hasSender();
	boolean hasTaxes();
}
