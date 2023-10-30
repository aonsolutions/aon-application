package com.esferalia.aon.occam.test.finance.invoice;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoicePrinter {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat FMT = new  DecimalFormat("#,##0.00");
	private static final DecimalFormat FMT1 = new  DecimalFormat("#,##0.0000");

	private boolean abbrv;
	private InvoicePrinter( boolean abbrv ) {
		this.abbrv = abbrv;
	}
	private int getLineSize() {
		return this.abbrv?110:172;
	}
	
	private static final String TOP_LEFT_CORNER = "\u250C";
	private static final String TOP_RIGHT_CORNER = "\u2510"; 
	private static final String LOWER_LEFT_CORNER = "\u2514"; 
	private static final String LOWER_RIGHT_CORNER ="\u2518";
	private static final String VERTICAL_BAR = "\u2502";
	private static final String HORIZONTAL_BAR = "\u2500";
	private static final String VERTICAL_RIGHT_BAR = "\u251C"; 		//	â”œ
	private static final String VERTICAL_LEFT_BAR = "\u2524";		//	â”¤
	private static final String HORIZONTAL_DOWN_BAR = "\u252C";		//	â”¬
	private static final String HORIZONTAL_UP_BAR = "\u2534";		//	â”´
	private static final String CROSS = "\u253C";		//	â”¼
	
	private InvoicePrinter header(Invoice invoice, PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append( AonStringUtils.SPACE );
		buf.append( TOP_LEFT_CORNER );
		buf.append( AonStringUtils.repeat(HORIZONTAL_BAR, 8) );
		buf.append( HORIZONTAL_DOWN_BAR );
		buf.append( AonStringUtils.repeat(HORIZONTAL_BAR, 21) );
		buf.append( HORIZONTAL_DOWN_BAR );
		buf.append( AonStringUtils.repeat(HORIZONTAL_BAR, 19) );
		buf.append( HORIZONTAL_DOWN_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append( TOP_RIGHT_CORNER);
		out.println( buf.toString() );
		
		buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(invoice.getType().getAbbrDescription(), 8));
		buf.append(VERTICAL_BAR);
		buf.append(" N\u00BA Fra: ");
		buf.append(AonStringUtils.rightPad(invoice.getDocumentNumber(),12));
		buf.append(VERTICAL_BAR);
		buf.append(" Fecha: ");
		buf.append(DATE_FORMAT.format(invoice.getIssueDate()));
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.SPACE);
		buf.append(" Documento: ");
		buf.append(invoice.getRegistryDocumentCountry());
		buf.append(AonStringUtils.HYPHEN);
		buf.append(invoice.getRegistryDocument());
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println( buf.toString() );

		buf = new StringBuffer();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(8));
		buf.append(VERTICAL_BAR);
		buf.append(" Total : ");
		buf.append(AonStringUtils.rightPad(FMT.format(invoice.getTotal()),12));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(19));
		buf.append(VERTICAL_BAR);
		buf.append("  R. Social: ");
		buf.append(AonStringUtils.abbreviate(invoice.getRegistryName(),42));
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println( buf.toString() );

		buf = new StringBuffer();
		buf.append( AonStringUtils.SPACE );
		buf.append( LOWER_LEFT_CORNER );
		buf.append( AonStringUtils.repeat(HORIZONTAL_BAR, 8) );
		buf.append( HORIZONTAL_UP_BAR );
		buf.append( AonStringUtils.repeat(HORIZONTAL_BAR, 21) );
		buf.append( HORIZONTAL_UP_BAR );
		buf.append( AonStringUtils.repeat(HORIZONTAL_BAR, 19) );
		buf.append( HORIZONTAL_UP_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append( LOWER_RIGHT_CORNER);
		out.println( buf.toString() );
		return this;
	}
	
	private InvoicePrinter details( Invoice invoice, PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append( AonStringUtils.SPACE );
		buf.append( TOP_LEFT_CORNER );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append( TOP_RIGHT_CORNER);
		out.println( buf.toString() );
		
		buf = new StringBuffer();
		buf.append( AonStringUtils.SPACE );
		buf.append( VERTICAL_BAR );
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.rightPad("PRODUCTO",35));
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad("CANTIDAD",10));		
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad("PRECIO",12));
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad("IMPORTE",17));
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append( VERTICAL_BAR);
		out.println( buf.toString() );

		buf = new StringBuffer();
		buf.append( AonStringUtils.SPACE );
		buf.append( VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,getLineSize() - buf.length()));
		buf.append( VERTICAL_LEFT_BAR);
		out.println( buf.toString() );
		
		invoice.getDetails().stream().forEach(det -> detail(det, out));

		buf = new StringBuffer();
		buf.append( AonStringUtils.SPACE );
		buf.append( LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,getLineSize() - buf.length()));
		buf.append( LOWER_RIGHT_CORNER);
		out.println( buf.toString() );
		return this;
}
	
	private void detail( InvoiceDetail detail, PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append( AonStringUtils.SPACE );
		buf.append( VERTICAL_BAR );
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(detail.getDescription()),34),35));
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad(FMT.format(detail.getQuantity()),10));		
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad(FMT.format(detail.getPrice()),12));
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad(FMT1.format(detail.getTaxableBase()),17));
		buf.append( AonStringUtils.SPACE );
		AonCollectionUtils.stream(detail.getInvoiceTaxes())
			.forEach(tax -> invoiceTax(tax,buf));		
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append( VERTICAL_BAR);
		out.println( buf.toString() );
		
	}
	

	private String invoiceTax(InvoiceTax tax, StringBuffer buf) {
		buf.append( AonStringUtils.SPACE );
		buf.append(AonStringUtils.leftPad(tax.getTaxType().getName2(),5));
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getPercentage()),6));
		buf.append(AonStringUtils.PERCENT);
		return buf.toString();
	}
	
	private InvoicePrinter  vatBreakdown(Invoice invoice, PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.spaces( 40));
		buf.append( TOP_LEFT_CORNER );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append( TOP_RIGHT_CORNER);
		out.println( buf.toString() );

		AonCollectionUtils.stream(invoice.getTaxBreakdown().getVats())
			.forEach(tax -> vatInvoiceBreakdown(tax,out));

		buf = new StringBuffer();
		buf.append(AonStringUtils.spaces( 40));
		buf.append( LOWER_LEFT_CORNER );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append( LOWER_RIGHT_CORNER);
		out.println( buf.toString() );
		return this;
	}

	private InvoicePrinter  withholding(Invoice invoice, PrintStream out) {
		if ( AonCollectionUtils.stream(invoice.getTaxBreakdown().getBreakdown()).anyMatch( tax -> tax.isWithholding() ) ) {
			StringBuffer buf = new StringBuffer();
			buf.append(AonStringUtils.spaces( 40));
			buf.append( TOP_LEFT_CORNER );
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
			buf.append( TOP_RIGHT_CORNER);
			out.println( buf.toString() );
			
			AonCollectionUtils.stream(invoice.getTaxBreakdown().getBreakdown())
			.filter( tax -> tax.isWithholding() )
			.forEach(tax -> vatInvoiceBreakdown(tax,out));
			
			buf = new StringBuffer();
			buf.append(AonStringUtils.spaces( 40));
			buf.append( LOWER_LEFT_CORNER );
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
			buf.append( LOWER_RIGHT_CORNER);
			out.println( buf.toString() );
		}
		return this;
	}
	
	private void vatInvoiceBreakdown(InvoiceBreakdown tax, PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.spaces( 40));
		buf.append( VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(1));
		buf.append(AonStringUtils.rightPad(tax.getTaxType().getName2(),5));
		buf.append(AonStringUtils.leftPad(FMT1.format(tax.getBase()),16));		
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getPercentage()),12));
		buf.append(AonStringUtils.PERCENT);
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getQuota()),17));
		buf.append(AonStringUtils.repeat(" ",10 ));
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append( VERTICAL_BAR);
		out.println( buf.toString() );
	}
	
	private InvoicePrinter totals(Invoice invoice, PrintStream out) {
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.spaces(38));
		buf.append( TOP_LEFT_CORNER );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( HORIZONTAL_DOWN_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( HORIZONTAL_DOWN_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( HORIZONTAL_DOWN_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( TOP_RIGHT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		out.println( buf.toString() );
		
		
		buf = new StringBuffer();
		buf.append(AonStringUtils.spaces(38));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center("Base Imponible",17));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center("Cuota IVA",17));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center("Retenci\u00F3n",17));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center("TOTAL FACTURA",17));
		buf.append( VERTICAL_BAR );
		out.println( buf.toString() );

		buf = new StringBuffer();
		buf.append(AonStringUtils.spaces(38));
		buf.append( VERTICAL_RIGHT_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( CROSS );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( CROSS );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( CROSS );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( VERTICAL_LEFT_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		out.println( buf.toString() );

		buf = new StringBuffer();
		buf.append(AonStringUtils.leftPad("(INVOICE) ",38));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center(FMT1.format(invoice.getTaxableBase()),17));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center(FMT.format(invoice.getVatQuota()),17));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center(FMT.format(invoice.getRetentionQuota()),17));
		buf.append( VERTICAL_BAR );
		buf.append( ConsoleColors.whiteBold( AonStringUtils.center(FMT.format(invoice.getTotal()),17) ));  
		buf.append( VERTICAL_BAR );
		out.println( buf.toString() );

		buf = new StringBuffer();
		
		buf.append(AonStringUtils.leftPad("(DESGLOSE) ",38));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center(FMT1.format(invoice.getTaxBreakdown().getVatBase()),17));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center(FMT.format(invoice.getTaxBreakdown().getVatQuota()),17));
		buf.append( VERTICAL_BAR );
		buf.append(AonStringUtils.center(FMT.format(invoice.getTaxBreakdown().getRetentionQuota()),17));
		buf.append( VERTICAL_BAR );
		buf.append( ConsoleColors.whiteBold( AonStringUtils.center(FMT.format(InvoiceCalculator.getTotal(invoice)),17) ));  
		buf.append( VERTICAL_BAR );
		out.println( buf.toString() );

		buf = new StringBuffer();
		buf.append(AonStringUtils.spaces(38));
		buf.append( LOWER_LEFT_CORNER );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( HORIZONTAL_UP_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( HORIZONTAL_UP_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,17));
		buf.append( HORIZONTAL_UP_BAR );
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append( LOWER_RIGHT_CORNER);
		out.println( buf.toString() );
		return this;
	}

	static void print( Invoice invoice) {
		print(System.out, invoice, true);
	}
	static void print( PrintStream out, Invoice invoice) {
		print(out, invoice, true);
	}
	static void print( PrintStream out, Invoice invoice, boolean abbrv) {
		new InvoicePrinter(abbrv)	
			.header(invoice, out)
			.details(invoice, out)
			.vatBreakdown(invoice, out)
			.withholding(invoice, out)
			.totals(invoice, out)
			;
		out.flush();
	}
	
	public static class ConsoleColors {
	    // Reset
	    public static final String RESET = "\033[0m";  // Text Reset

	    // Regular Colors
	    public static final String BLACK = "\033[0;30m";   // BLACK
	    public static final String RED = "\033[0;31m";     // RED
	    public static final String GREEN = "\033[0;32m";   // GREEN
	    public static final String YELLOW = "\033[0;33m";  // YELLOW
	    public static final String BLUE = "\033[0;34m";    // BLUE
	    public static final String PURPLE = "\033[0;35m";  // PURPLE
	    public static final String CYAN = "\033[0;36m";    // CYAN
	    public static final String WHITE = "\033[0;37m";   // WHITE

	    // Bold
	    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
	    public static final String RED_BOLD = "\033[1;31m";    // RED
	    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
	    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
	    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
	    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
	    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
	    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

	    // Underline
	    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
	    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
	    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
	    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
	    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
	    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
	    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
	    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

	    // Background
	    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
	    public static final String RED_BACKGROUND = "\033[41m";    // RED
	    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
	    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
	    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
	    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
	    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
	    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

	    // High Intensity
	    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
	    public static final String RED_BRIGHT = "\033[0;91m";    // RED
	    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
	    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
	    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
	    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
	    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
	    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

	    // Bold High Intensity
	    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
	    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
	    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
	    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
	    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
	    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
	    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
	    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

	    // High Intensity backgrounds
	    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
	    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
	    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
	    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
	    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
	    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
	    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
	    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE
	    
		public static String whiteBold(String text) {
			return ConsoleColors.WHITE_BOLD + text + ConsoleColors.RESET;
		}
	}
	
}
