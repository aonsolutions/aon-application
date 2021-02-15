package solutions.aon.in.invoice;

import java.util.Collection;
import java.util.Date;

import es.translogia.tedi.ewok.TediNif;
import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.ParserContext;

public interface InvoiceBuilder<T extends Object>  {

	ParserContext getParserContext();
	void setParserContext(ParserContext context);
	T get();
	void addInsightNifs( Collection<Document> nifs);
	TediNif[] getInsightNifs();
	void addInsightDates( Collection<Date> dates);
	void addInsightAmounts( Collection<Double> amounts);
	Double[] getInsightAmounts();
	void setInsightIssueDate( Date issueDate );
	void setInsightTotal( Double total );
	
	void setReference( String reference);
	String[] getReferencePatterns();
	
	void finalizeParse();
	
	
}
