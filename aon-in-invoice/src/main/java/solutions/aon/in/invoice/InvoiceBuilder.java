package solutions.aon.in.invoice;

import java.util.Collection;
import java.util.Date;

import solutions.aon.in.invoice.templates.Document;

public interface InvoiceBuilder<T extends Object>  {
	
	T get();
	void addInsightNifs( Collection<Document> nifs);
	void addInsightDates( Collection<Date> dates);
	void addInsightAmounts( Collection<Double> amounts);
	void setInsightIssueDate( Date issueDate );
	void setInsightTotal( Double total );
	
	void setReference( String reference);
	String[] getReferencePatterns();
	
	void finalizeParse();
	
}
