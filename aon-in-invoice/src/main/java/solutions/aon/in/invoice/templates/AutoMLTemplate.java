package solutions.aon.in.invoice.templates;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.pdf.InvoicePDFException;

public class AutoMLTemplate extends AbstractTemplate {
	
	public static final  AbstractTemplate AUTO_ML_TEMPLATE = new AutoMLTemplate();

	private AutoMLTemplate() {
	}

	@Override
	public InvoiceTemplate parse(String text, InvoiceBuilder<?> handler) throws InvoicePDFException {
		try {
			handler.addInsightNifs(DocumentParser.getNifs(text));
			handler.setInsightIssueDate(IssueDateParser.getIssueDate(text));
			handler.addInsightDates(DateParser.getDates(text));
			handler.setInsightTotal(TotalParser.getTotal(text));
			handler.addInsightAmounts(AmountParser.getAmounts(text));
			handler.setReference(ReferenceParser.getReference(handler.getReferencePatterns(), text));
			return this;
		} catch (Throwable e) {
			throw new InvoicePDFException( e );
		}
	}

}
