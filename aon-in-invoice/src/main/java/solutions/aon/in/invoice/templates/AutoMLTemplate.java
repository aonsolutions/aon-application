package solutions.aon.in.invoice.templates;

import java.util.List;

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
			if (handler.getParserContext() == null) {
				handler.setParserContext( LocaleParser.getContext( text )); 
			}
			handler.addInsightNifs(DocumentParser.getNifs(text));
			handler.setInsightIssueDate(IssueDateParser.getIssueDate(handler.getParserContext(),text));
			handler.addInsightDates(DateParser.getDates(handler.getParserContext(), text ));
			handler.setInsightTotal(TotalParser.getTotal(handler.getParserContext(), text));
			List<Double> amounts = AmountParser.getAmounts(handler.getParserContext(), text);
			if ((amounts == null || amounts.isEmpty()) &&
				(handler.getInsightAmounts() == null || handler.getInsightAmounts().length == 0)) {
				amounts = AmountParser.getAmounts(ParserContext.alternativeContext(handler.getParserContext()), text);	
			}
			handler.addInsightAmounts(amounts);
			handler.setReference(ReferenceParser.getReference(handler.getReferencePatterns(), text));
			return this;
		} catch (Throwable e) {
			throw new InvoicePDFException( e );
		}
	}

}
