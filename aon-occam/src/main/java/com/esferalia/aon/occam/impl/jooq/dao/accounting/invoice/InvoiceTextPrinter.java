package com.esferalia.aon.occam.impl.jooq.dao.accounting.invoice;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.invoice.InvoiceError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceTextPrinter {

	// ? --> \u00C1 ? --> \u00E1
	// ? --> \u00C9 ? --> \u00E9
	// ? --> \u00CD ? --> \u00ED
	// ? --> \u00D3 ? --> \u00F3
	// ? --> \u00DA ? --> \u00FA
	// ? --> \u00D1 ? --> \u00F1
	// ? --> \u00AA ? --> \u00BA
	// ? --> \u00BF

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00");
	private static final DecimalFormat FMT1 = new DecimalFormat("#,##0.0000");

	private boolean abbrv;

	private InvoiceTextPrinter(boolean abbrv) {
		this.abbrv = abbrv;
	}

	private int getLineSize() {
		return this.abbrv ? 110 : 172;
	}

	private static final String TOP_LEFT_CORNER = "\u250C";
	private static final String TOP_RIGHT_CORNER = "\u2510";
	private static final String LOWER_LEFT_CORNER = "\u2514";
	private static final String LOWER_RIGHT_CORNER = "\u2518";
	private static final String VERTICAL_BAR = "\u2502";
	private static final String HORIZONTAL_BAR = "\u2500";
	private static final String VERTICAL_RIGHT_BAR = "\u251C"; // ?
	private static final String VERTICAL_LEFT_BAR = "\u2524"; // ?
	private static final String HORIZONTAL_DOWN_BAR = "\u252C"; // ?
	private static final String HORIZONTAL_UP_BAR = "\u2534"; // ?
	private static final String CROSS = "\u253C"; // ?

	private InvoiceTextPrinter header(Invoice invoice, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 8));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 25));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 19));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(TOP_RIGHT_CORNER);
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(invoice.getType().getAbbrDescription(), 8));
		buf.append(VERTICAL_BAR);
		buf.append(" N\u00BA Fra: ");
		buf.append(AonStringUtils.rightPad(invoice.getDocumentNumber(), 16));
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
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(8));
		buf.append(VERTICAL_BAR);
		buf.append(" Total : ");
		buf.append(AonStringUtils.rightPad(FMT.format(invoice.getTotal()), 16));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(19));
		buf.append(VERTICAL_BAR);
		buf.append("  R. Social: ");
		buf.append(AonStringUtils.abbreviate(invoice.getRegistryName(), 42));
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 8));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 25));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 19));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(LOWER_RIGHT_CORNER);
		out.println(buf.toString());
		return this;
	}

	private InvoiceTextPrinter details(Invoice invoice, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(TOP_RIGHT_CORNER);
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad("PRODUCTO", 35));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad("CANTIDAD", 10));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad("PRECIO", 12));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad("IMPORTE", 17));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(VERTICAL_LEFT_BAR);
		out.println(buf.toString());

		invoice.getDetails().stream().forEach(det -> detail(det, out));

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(LOWER_RIGHT_CORNER);
		out.println(buf.toString());
		return this;
	}

	private void detail(InvoiceDetail detail, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils
				.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(detail.getDescription()), 34), 35));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(FMT.format(detail.getQuantity()), 10));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(FMT.format(detail.getPrice()), 12));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(FMT1.format(detail.getTaxableBase()), 17));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
		AonCollectionUtils.stream(detail.getInvoiceTaxes()).forEach(tax -> invoiceTax(tax, out));
	}

	private void invoiceTax(InvoiceTax tax, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(">>>>");
		buf.append(AonStringUtils.leftPad(tax.getTaxType().getName2(), 5));
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getPercentage()), 6));
		buf.append(AonStringUtils.PERCENT);
		buf.append(" [Base:");
		buf.append(AonStringUtils.leftPad(FMT1.format(tax.getBase()), 17));
		buf.append("] ");
		buf.append("[Cuota:");
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getQuota()), 13));
		buf.append("] ");
		buf.append("[% Ded:");
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getDeductiblePercent()), 6));
		buf.append("] ");
		buf.append("[Cuota Ded.:");
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getDeductibleQuota()), 13));
		buf.append("] ");
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
	}

	private InvoiceTextPrinter vatBreakdown(Invoice invoice, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(40));
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(TOP_RIGHT_CORNER);
		out.println(buf.toString());

		AonCollectionUtils.stream(invoice.getVats()).forEach(tax -> vatInvoiceBreakdown(tax, out));

		buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(40));
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(LOWER_RIGHT_CORNER);
		out.println(buf.toString());
		return this;
	}

	private InvoiceTextPrinter withholding(Invoice invoice, PrintStream out) {
		Optional<InvoiceWithholding> iw = invoice.getWithholding();
		if (iw.isPresent()) {
			StringBuilder buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(40));
			buf.append(TOP_LEFT_CORNER);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
			buf.append(TOP_RIGHT_CORNER);
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(40));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(1));
			buf.append(AonStringUtils.rightPad(TaxType.RETENTION.getName2(), 5));
			buf.append(AonStringUtils.leftPad(FMT1.format(iw.get().getBase()), 16));
			buf.append(AonStringUtils.leftPad(FMT.format(iw.get().getPercentage()), 12));
			buf.append(AonStringUtils.PERCENT);
			buf.append(AonStringUtils.leftPad(FMT.format(iw.get().getQuota()), 17));
			buf.append(AonStringUtils.repeat(" ", 10));
			buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
			buf.append(VERTICAL_BAR);
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(40));
			buf.append(LOWER_LEFT_CORNER);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
			buf.append(LOWER_RIGHT_CORNER);
			out.println(buf.toString());
		}
		return this;
	}

	private void vatInvoiceBreakdown(InvoiceBreakdown tax, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(40));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(1));
		buf.append(AonStringUtils.rightPad(tax.getTaxType().getName2(), 5));
		buf.append(AonStringUtils.leftPad(FMT1.format(tax.getBase()), 16));
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getPercentage()), 12));
		buf.append(AonStringUtils.PERCENT);
		buf.append(AonStringUtils.leftPad(FMT.format(tax.getQuota()), 17));
		buf.append(AonStringUtils.repeat(" ", 10));
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());
	}

	private InvoiceTextPrinter totals(Invoice invoice, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(38));
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_DOWN_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(TOP_RIGHT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(38));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Base Imponible", 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Cuota IVA", 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("Retenci\u00F3n", 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center("TOTAL FACTURA", 17));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(38));
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(CROSS);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(VERTICAL_LEFT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(38));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(FMT1.format(invoice.getTaxableBase()), 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(FMT.format(invoice.getVatQuota()), 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(FMT.format(invoice.getRetentionQuota()), 17));
		buf.append(VERTICAL_BAR);
		buf.append(ConsoleColors.whiteBold(AonStringUtils.center(FMT.format(invoice.getTotal()), 17)));
		buf.append(VERTICAL_BAR);
		out.println(buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(38));
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(LOWER_RIGHT_CORNER);
		out.println(buf.toString());
		return this;
	}

	private InvoiceTextPrinter fiscal(Invoice invoice, PrintStream out) {
		InvoiceFiscal invoiceFiscal = invoice.getFiscal();
		if (invoiceFiscal != null) {
			StringBuilder buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append( TOP_LEFT_CORNER );
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,92));
			buf.append( TOP_RIGHT_CORNER);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
			out.println( buf.toString() );
			
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(ConsoleColors.whiteRed(AonStringUtils.center("INFORMACIÓN FISCAL",92)));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_RIGHT_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 92));
			buf.append(VERTICAL_LEFT_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
			out.println(buf.toString());
			
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.center("Factura de "
					+invoice.getType().getDescription()
					+ " "
					+ invoice.getTransaction().getDescription()		
					,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad("Fecha factura (fiscal) ....: " 
				+ (invoiceFiscal.getIssueDate() == null
					?"<sin asignar>"
					:DATE_FORMAT.format(invoiceFiscal.getIssueDate()))
				,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad("Fecha IVA (fiscal) ........: " 
				+ (invoiceFiscal.getIssueDate() == null
					?"<sin asignar>"
					:DATE_FORMAT.format(invoiceFiscal.getTaxDate()))
				,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad("Fecha expedición (fiscal) .: " 
				+ (invoiceFiscal.getExpDate() == null
					?"<sin asignar>"
					:DATE_FORMAT.format(invoiceFiscal.getExpDate()))
				,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad("Actividad .................: " 
					+ (invoice.getActivity() != null && invoice.getActivity().getId() != null
						?(invoice.getActivity().getEpigraph() 
						 + " "
						 +invoice.getActivity().getDescription())
					:"TODAS")
					,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());
			
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad("Rectificativa  ............: "
				+ invoice.getRectificationInvoice()
					.map( ri -> "SI --> "  + ri.getReferenceCode() + " [ " + ri.getId() +" ]")
					.orElse("NO") ,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(92));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());
			

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad(checkLabel("Retención", invoice.isWithholding()) ,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());
			
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad(checkLabel("Servicio", invoice.isService()) ,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad(checkLabel("Régimen agrario", invoice.isWithholdingFarmer()) ,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad(checkLabel("Régimen criterio de caja", invoice.isVatAccrualPayment()) ,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(5));
			buf.append(AonStringUtils.rightPad(checkLabel("Inversión", invoice.isInvestment()) ,87));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
			out.println(buf.toString());

			AonCollectionUtils.keysStream( invoiceFiscal.getVatRegimes() )
				.forEach( k -> {
					StringBuilder b = new StringBuilder();
					b.append(AonStringUtils.spaces(10));
					b.append(VERTICAL_BAR);
					b.append(AonStringUtils.spaces(5));
					String name = AonStringUtils.abbreviateMiddle(k.getName(), " (..) ", 75); 
					b.append(AonStringUtils.rightPad(checkLabel(name, invoiceFiscal.getVatRegimes().get(k) ) ,87));
					b.append(VERTICAL_BAR);
					b.append(AonStringUtils.spaces(getLineSize() - b.length()));			
					out.println(b.toString());
				});
			
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(10));
			buf.append(LOWER_LEFT_CORNER);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 92));
			buf.append(LOWER_RIGHT_CORNER);
			buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
			out.println(buf.toString());
		}
		return this;
	}
	
	private static String checkLabel( String label, boolean checked) {
		return "  "
			+ (checked? "[\u2713]" : "[ ]" )
			+ " > " 
			+ label
			+ "."
			; 
	}
	

	public static void print(Invoice invoice) {
		print(System.out, invoice, true);
	}

	public static void print(PrintStream out, Invoice invoice) {
		print(out, invoice, true);
	}

	public static void print(PrintStream out, Invoice invoice, boolean abbrv) {
		new InvoiceTextPrinter(abbrv)
			.header(invoice, out)
			.details(invoice, out)
			.vatBreakdown(invoice, out)
			.withholding(invoice, out)
			.totals(invoice, out)
			.fiscal(invoice, out)
			.messages(invoice, out);
		out.flush();
	}

	public static void printMessages(PrintStream out, Invoice invoice) {
		new InvoiceTextPrinter(false)
			.messages(invoice, out);
		out.flush();
	}

	public InvoiceTextPrinter messages(Invoice invoice, PrintStream out) {
		if (AonCollectionUtils.isNotEmpty(invoice.getMessages())) {
			out.println();
			StringBuilder buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(15));
			buf.append(TOP_LEFT_CORNER);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 4));
			buf.append(HORIZONTAL_DOWN_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 5));
			buf.append(HORIZONTAL_DOWN_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 25));
			buf.append(HORIZONTAL_DOWN_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 80));
			buf.append(TOP_RIGHT_CORNER);
			out.println(buf.toString());
			
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(15));
			buf.append(VERTICAL_BAR);
			buf.append(ConsoleColors.whiteRed(AonStringUtils.rightPad("TYP", 4)));
			buf.append(VERTICAL_BAR);
			buf.append(ConsoleColors.whiteRed(AonStringUtils.rightPad("CODE", 5)));
			buf.append(VERTICAL_BAR);
			buf.append(ConsoleColors.whiteRed(AonStringUtils.rightPad("FIELD", 25)));
			buf.append(VERTICAL_BAR);
			buf.append(ConsoleColors.whiteRed(AonStringUtils.rightPad("MESSAGE", 80)));
			buf.append(VERTICAL_BAR);
			out.println(buf.toString());			
			

			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(15));
			buf.append(VERTICAL_RIGHT_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 4));
			buf.append(CROSS);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 5));
			buf.append(CROSS);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 25));
			buf.append(CROSS);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 80));
			buf.append(VERTICAL_LEFT_BAR);
			out.println(buf.toString());

			for (InvoiceError e : invoice.getMessages()) {
				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(15));
				buf.append(VERTICAL_BAR);
				if (e.getLevel() == InvoiceErrorLevel.ERR) buf.append(ConsoleColors.RED);
				if (e.getLevel() == InvoiceErrorLevel.WRN) buf.append(ConsoleColors.YELLOW);
				if (e.getLevel() == InvoiceErrorLevel.INF) buf.append(ConsoleColors.BLUE);
				buf.append(AonStringUtils.rightPad(e.getLevel() == null ? "" : e.getLevel().toString(), 4));
				buf.append(ConsoleColors.RESET);
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(e.getCode()), 5));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.rightPad(e.getContext() == null ? "" : e.getContext().toString(), 25));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(e.getMessage()),79),80));
				buf.append(VERTICAL_BAR);
				out.println(buf.toString());			
			}
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(15));
			buf.append(LOWER_LEFT_CORNER);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 4));
			buf.append(HORIZONTAL_UP_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 5));
			buf.append(HORIZONTAL_UP_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 25));
			buf.append(HORIZONTAL_UP_BAR);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 80));
			buf.append(LOWER_RIGHT_CORNER);
			out.println(buf.toString());
			
			System.out.println("\t\t"
					+" " + AonStringUtils.repeat("-", 4)
					+" " + AonStringUtils.repeat("-", 5)
					+" " + AonStringUtils.repeat("-", 25)
					+" " + AonStringUtils.repeat("-", 80)
					+" "
					);
		}
		return this;
	}

	public static class ConsoleColors {
		// Reset
		public static final String RESET = "\033[0m"; // Text Reset

		// Regular Colors
		public static final String BLACK = "\033[0;30m"; // BLACK
		public static final String RED = "\033[0;31m"; // RED
		public static final String GREEN = "\033[0;32m"; // GREEN
		public static final String YELLOW = "\033[0;33m"; // YELLOW
		public static final String BLUE = "\033[0;34m"; // BLUE
		public static final String PURPLE = "\033[0;35m"; // PURPLE
		public static final String CYAN = "\033[0;36m"; // CYAN
		public static final String WHITE = "\033[0;37m"; // WHITE

		// Bold
		public static final String BLACK_BOLD = "\033[1;30m"; // BLACK
		public static final String RED_BOLD = "\033[1;31m"; // RED
		public static final String GREEN_BOLD = "\033[1;32m"; // GREEN
		public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
		public static final String BLUE_BOLD = "\033[1;34m"; // BLUE
		public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
		public static final String CYAN_BOLD = "\033[1;36m"; // CYAN
		public static final String WHITE_BOLD = "\033[1;37m"; // WHITE

		// Underline
		public static final String BLACK_UNDERLINED = "\033[4;30m"; // BLACK
		public static final String RED_UNDERLINED = "\033[4;31m"; // RED
		public static final String GREEN_UNDERLINED = "\033[4;32m"; // GREEN
		public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
		public static final String BLUE_UNDERLINED = "\033[4;34m"; // BLUE
		public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
		public static final String CYAN_UNDERLINED = "\033[4;36m"; // CYAN
		public static final String WHITE_UNDERLINED = "\033[4;37m"; // WHITE

		// Background
		public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK
		public static final String RED_BACKGROUND = "\033[41m"; // RED
		public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN
		public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
		public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
		public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
		public static final String CYAN_BACKGROUND = "\033[46m"; // CYAN
		public static final String WHITE_BACKGROUND = "\u001B[47m"; //"\033[47m"; // WHITE

		// High Intensity
		public static final String BLACK_BRIGHT = "\033[0;90m"; // BLACK
		public static final String RED_BRIGHT = "\033[0;91m"; // RED
		public static final String GREEN_BRIGHT = "\033[0;92m"; // GREEN
		public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
		public static final String BLUE_BRIGHT = "\033[0;94m"; // BLUE
		public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
		public static final String CYAN_BRIGHT = "\033[0;96m"; // CYAN
		public static final String WHITE_BRIGHT = "\033[0;97m"; // WHITE

		// Bold High Intensity
		public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
		public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // RED
		public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
		public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
		public static final String BLUE_BOLD_BRIGHT = "\033[1;94m"; // BLUE
		public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
		public static final String CYAN_BOLD_BRIGHT = "\033[1;96m"; // CYAN
		public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

		// High Intensity backgrounds
		public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
		public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
		public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
		public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
		public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
		public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
		public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m"; // CYAN
		public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m"; // WHITE

		public static String whiteBold(String text) {
			return ConsoleColors.WHITE_BOLD + text + ConsoleColors.RESET;
		}
		public static String whiteRed(String text) {
			return ConsoleColors.RED_BACKGROUND + WHITE + text + ConsoleColors.RESET;
		}
	}
}
