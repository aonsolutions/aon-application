package solutions.aon.in.invoice.templates;

import java.io.IOException;

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
		handler.setInsightAmounts(AmountParser.getAmounts(text));
		return this;
	}

}
