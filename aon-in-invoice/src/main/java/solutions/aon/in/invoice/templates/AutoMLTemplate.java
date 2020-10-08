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
		handler.setInsightNifs(DocumentParser.getNifs(text));
		handler.setInsightDates(DateParser.getDates(text));
		List<Double> aomunts = AmountParser.getAmounts(text); 
		handler.setInsightAmounts(aomunts);
		InvoiceTaxParser.getTaxes(aomunts, handler);
		return this;
	}

}
