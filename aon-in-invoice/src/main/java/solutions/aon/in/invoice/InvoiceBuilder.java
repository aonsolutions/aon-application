package solutions.aon.in.invoice;

import java.util.Collection;
import java.util.Date;

import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.InvoiceTax;

public interface InvoiceBuilder<T extends Object>  {
	
	void setInsightNifs( Collection<Document> nifs);
	void setInsightDates( Collection<Date> dates);
	void setInsightAmounts( Collection<Double> amounts);
	void setInsightTotals( Collection<Double> totals);
	
	boolean hasIssueDate();
	void setIssueDate( Date date);
	
	void setTotal( double total);
	void setTax( InvoiceTax tax);

}
