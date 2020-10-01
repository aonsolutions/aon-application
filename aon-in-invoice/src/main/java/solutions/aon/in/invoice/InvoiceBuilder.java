package solutions.aon.in.invoice;

import java.util.Collection;
import java.util.Date;

import solutions.aon.in.invoice.templates.Document;

public interface InvoiceBuilder<T extends Object>  {
	
	void setInsightNifs( Collection<Document> nifs);
	void setInsightDates( Collection<Date> dates);
	void setInsightAmounts( Collection<Double> amounts);

}
