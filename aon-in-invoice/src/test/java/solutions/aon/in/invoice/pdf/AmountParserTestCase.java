package solutions.aon.in.invoice.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.junit.Test;

import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.AmountParser;

public class AmountParserTestCase {
	
    private static LinkedHashMap<String,Double> AMOUNTS = new LinkedHashMap<String,Double>() {
		private static final long serialVersionUID = -4540560976156810274L;
		{
			  // VALID
			  put("1,25", 1.25);
			  put("10,25", 10.25);
    	      put("100,25", 100.25);
    	      put("10,253245235", 10.253245235);
    	      put("100;253245235", 100.253245235);
    	      put("1000,253245235", 1000.253245235);
    	      put("10000,253245235", 10000.253245235);
    	      put("100,25€", 100.25);
    	      put("100,25 €", 100.25);
    	      put("100,25 $", 100.25);
    	      put("100,25$", 100.25);
    	      
    	      put("-100,25", -100.25);
    	      put("+100,25",  100.25);
    	      
    	      put("1111.100,25€", 1111100.25);
    	      put("1.111.100,25€", 1111100.25);
    	      // WRONG
    	      put(".25", null);
    	      put(".111.111.111.111.111.100,25€", null);
    	      put("X100,25 $", null);
    	      put("100", null);
    	      
    	      put("771,76", 771.76);
    	      put("  Condiciones de pago: Pago a la entrega Base imponible 771,76", 771.76);
    	}
    };		

    @Test
	public void testAmount() throws IOException, UnknownInvoiceException {
		System.out.println();
		System.out.println("--------------------");
		System.out.println("---- testAmount ----");
		System.out.println("--------------------");
		System.out.println( "\tAmounts: " );
		for ( String text : AMOUNTS.keySet()) {
			System.out.print( "\t\t["+text +"]\t");
			Collection<Double> amounts = AmountParser.getAmounts( text  );
			assertNotNull(text,amounts);
			Double expected = AMOUNTS.get(text);
			if ( expected == null) {
				System.out.println( "\t\tNULL as expected");
				assertEquals(text,0,amounts.size());
			} else {
				for (Double amount : amounts ) {
					System.out.println( "\t\t{Parsed # Expected} ..: {"+amount + "  #  " + AMOUNTS.get(text)+"}");
				}
				assertEquals(text,1,amounts.size());
				assertEquals(text,AMOUNTS.get(text),amounts.stream().findFirst().get().doubleValue(),0);
			}
			System.out.println( "\t ------------------------------");
		}
		
	}
	
}


