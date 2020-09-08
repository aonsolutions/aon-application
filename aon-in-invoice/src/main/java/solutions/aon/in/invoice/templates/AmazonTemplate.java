package solutions.aon.in.invoice.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;


//                                              ________________________________________________________
//                                             | PAGADO                    	                            |
//                                             | Vendido por Amazon EU S.á r.l., Sucursal en España     |
//                                             | IVA ESW0184081H                                        |
//                                             |________________________________________________________|
//                                             | XXXXXXXXXXXXXX             XXXXXXXXXXXXXXXXXXX         |
// XXXXXXXXXXXXXXXXXXXXXXXXXXX                 | XXXXXXXXXXXXXX             XXXXXXXXXXXXXXXXXXX         |
// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX              | Total a pagar              XX,XX $                     |
// XXXXXXXXXXXXXX                              |________________________________________________________|
// XX
// 
// Si tienes preguntas sobre tus pedidos, visita https:// www.amazon.es/contacto
//  ___________________________________________________________________________________________________________
// |                                                                           								  |
// |   Dirección XXXXXXXXXXXXXXX           Dirección de envío             Vendido por                         |
// |   xxxxxx xxxxxx xxxxxxx               xxxxxx xxxxxx xxxxxxx          xxxxxx xxxxxx xxxxxxx               |
// |   xxxxxx xxxxxx xxxxxxxxxxxxx         xxxxxx xxxxxx xxxxxxxxxxxxx    xxxxxx xxxxxx xxxxxxxxxxxxx         |
// |   xxxxxxxx xxxxxx xxxx                xxxxxxxx xxxxxx xxxx           xxxxxxxx xxxxxx xxxx                |
// |   xx                                  xx                             xxxxxxx                             |
// |                                                                      xxxxxxxxxxxxxxxx                    |
// |__________________________________________________________________________________________________________|
// |   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                                             |
// |                                                                                                          |
// |   Fecha  del pedido               XXXXXXXXX 			                                                  |
// |   Número del pedido               XXXXXXXXXXXXXXX 			                                              |
// |   XXXXXXXXXXXXXXXXX               XXXXXXXXXXXXXXX 			                                              |
// |__________________________________________________________________________________________________________|
// |                                                                                                          |
// |   DETALLES DEL DOCUMENTO                                                                                 |
// |__________________________________________________________________________________________________________|
// |   Descripción                                     Cant.  P.Unitario  IVA %   P.Unitario     Precio total |
// |                                                       (IVA excluido)      (IVA incluido)  (IVA incluido) |
// |                                                                                                          |
// |                                                                                                          |
// |   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx           1      XX,XX$     21%        XX,XX$           XX,XX$ |
// |   ASIN: B07HJ8B5V1                                                        								  |
// |__________________________________________________________________________________________________________|
//                                                     TOTAL                        				   XX,XX$ |
//                                                                                                            |
//                                                               IVA %         Precio total            IVA    |
//                                                                            (IVA excluido)                  |
//                      																                      |
//                                                               21%          	xx,xx $				  x,xx $  |
//                                                     _______________________________________________________|
//                                                     Total                 	xx,xx $				  x,xx $  |
//____________________________________________________________________________________________________________|
//
// Amazon EU S.à r.l., Sucursal en España - Calle de Ramírez de Prado 5, 28045 Madrid, España
//

public class AmazonTemplate extends AbstractTemplate {
	
	public static final  AmazonTemplate AMAZON_PDF_TEMPLATE = new AmazonTemplate();

	private AmazonTemplate() {
	}

	@Override
	public InvoiceTemplate parse(String text, InvoiceBuilder<?> builder) throws IOException, UnknownInvoiceException {
		System.out.println(text);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			
			Matcher matcher = find(reader, IVA_W0184081H);		
			
			builder.setSenderCountry(string(matcher, "country"));
			builder.setSenderDocument(string(matcher, "document"));
			builder.setSenderCity("MADRID");
			builder.setSenderProvince("MADRID");
			builder.setSenderPostalCode("28045");
			builder.setSenderAddress("CALLE DE RAMÍREZ DE PRADO 5");
			builder.setSenderName("AMAZON EU S.À R.L., SUCURSAL EN ESPAÑA");
			
			matcher = find(reader, dd_MMM_yyyy);
			builder.setDate(date(matcher, "date", "dd MMM yyyy"));
			
			
			
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	public static void main(String[] args) {
		check(IVA_W0184081H, "IVA ESW0184081H");
		check(dd_MMM_yyyy, "Fecha de envío 10 enero 2019");
		
	}
	
	private static void check(Pattern pattern, String str) {
		Matcher matcher =
		pattern.matcher(str);
		matcher.matches();
		for ( int g = 1; g <= matcher.groupCount(); g++) 
			System.out.println(matcher.group(g));
				
	}
	
	private static final Pattern IVA_W0184081H = 
	Pattern.compile("IVA\\s?(?<country>ES)(?<document>W0184081H)", Pattern.CASE_INSENSITIVE);
	private static final Pattern dd_MMM_yyyy = 
	Pattern.compile(".*(?<date>(\\d{2})\\s?([a-z]{3,})\\s?(\\d{4})).*", Pattern.CASE_INSENSITIVE);
}
