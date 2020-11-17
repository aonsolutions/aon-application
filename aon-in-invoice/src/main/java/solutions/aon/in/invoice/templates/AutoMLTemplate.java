package solutions.aon.in.invoice.templates;

import java.io.IOException;
import java.util.List;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;

public class AutoMLTemplate extends AbstractTemplate {
	
	public static final  AbstractTemplate AUTO_ML_TEMPLATE = new AutoMLTemplate();

	private AutoMLTemplate() {
	}

	@Override
	public InvoiceTemplate parse(String text, InvoiceBuilder<?> handler) throws IOException, UnknownInvoiceException {
		handler.addInsightNifs(DocumentParser.getNifs(text));
		if ( handler.hasSender() && !handler.hasReference()) {
			handler.setReference(ReferenceParser.getReference(handler.getSenderDocument(), text));		
		}
		if (!handler.hasIssueDate()) {
			handler.setIssueDate(IssueDateParser.getIssueDate(text));
		}
		handler.addInsightDates(DateParser.getDates(text));
		List<Double> amounts = AmountParser.getAmounts(text); 
		handler.addInsightAmounts(amounts);
		InvoiceTaxParser.setTaxes(amounts, handler);
		handler.setInsightTotals(TotalParser.getAmounts(text));
		return this;
	}

}
