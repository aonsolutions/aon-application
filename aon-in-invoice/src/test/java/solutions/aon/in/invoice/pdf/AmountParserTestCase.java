package solutions.aon.in.invoice.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.junit.Test;

import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.AmountParser;
import solutions.aon.in.invoice.templates.ParserContext;

public class AmountParserTestCase {
	
    private static LinkedHashMap<String,Double> AMOUNTS = new LinkedHashMap<String,Double>() {
		private static final long serialVersionUID = -4540560976156810274L;
		{
			  // VALID
			  put("1,25", 1.25);
			  put("10,25", 10.25);
    	      put("100,25", 100.25);
    	      put("10,253245235", 10.253245235);
    	      put("100;253245235", null);
    	      put("1000,253245235", 1000.253245235);
    	      put("10000,253245235", 10000.253245235);
    	      put("100,25X\u20ac¬", null);
    	      put("100,25 \u20ac¬", 100.25);
    	      put("100,25 EUR", 100.25);
    	      put("100,25EUR", null);
    	      put("100,25X$", null);
    	      put("100,25 $", 100.25);
    	      put("100,25$", 100.25);
    	      put("100,25USD", null);
    	      put("100,25 USD", 100.25);
    	      
    	      put("-100,25", -100.25);
    	      put("+100,25",  100.25);
    	      
    	      put("1111.100,25 \u20ac¬", 1111100.25);
    	      put("1.111.100,25 \u20ac¬", 1111100.25);
    	      // WRONG
    	      put(".25", null);
    	      put(".111.111.111.111.111.100,25â\u20ac¬", null);
    	      put("X100,25 $", null);
    	      put("100", null);
    	      
    	      put("771,76", 771.76);
    	      put("  Condiciones de pago: Pago a la entrega Base imponible 771,76", 771.76);
    	}
    };		

    @Test
	public void testAmount() throws IOException, UnknownInvoiceException {
		for ( String text : AMOUNTS.keySet()) {
			Collection<Double> amounts = AmountParser.getAmounts( ParserContext.SPANISH, text  );
			assertNotNull(text,amounts);
			Double expected = AMOUNTS.get(text);
			if ( expected == null) {
				assertEquals(text,0,amounts.size());
			} else {
				assertEquals(text,1,amounts.size());
				assertEquals(text,AMOUNTS.get(text),amounts.stream().findFirst().get().doubleValue(),0);
			}
		}
		
	}
	
}


