package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class InvoiceConsoleTextPanel extends ScrollPanel {

	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	static final InvoiceConsoleServiceAsync INVOICE_SERVICE;
	static {
		InvoiceConsoleServiceAsync fiscalServiceRaw = GWT.create(InvoiceConsoleService.class);
		INVOICE_SERVICE = new InvoiceConsoleAsyncDecorator(fiscalServiceRaw);
	}

	private static final String SIN_ASIGNAR = "Sin asignar.";

	private boolean abbrv = true;

	private int getLineSize() {
		return this.abbrv ? 110 : 172;
	}
	public InvoiceConsoleTextPanel(Occam occam, Integer domain, Integer id) {
		INVOICE_SERVICE.getInvoice(occam, domain, id, new AsyncCallback<Invoice>() {
			@Override
			public void onSuccess(Invoice inv) {
				print( inv );
			}
			
			@Override
			public void onFailure(Throwable e) {
				AonMessageDialog.error(e.getMessage());
			}
		} ); 
	}
	public InvoiceConsoleTextPanel(Invoice invoice) {
		print( invoice ); 
	}
	
	private void print (Invoice invoice) {
		StringBuilder out = new StringBuilder();
		header(invoice, out);
		details(invoice, out);
		vatBreakdown(invoice, out);
		withholding(invoice, out);
		totals(invoice, out);
		finances(invoice, out);
		fiscal(invoice, out);
		doc(invoice, out);
		messages(invoice, out);
		HTMLPanel panel = new HTMLPanel("pre",out.toString());
		panel.setStyleName( AON.CSS.aonFixedFont() );
		this.setStyleName( AON.CSS.aonScrollArea() );
		this.addStyleName( AON.CSS.aonTextCenter() );
		this.setWidget( panel );
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

	private InvoiceConsoleTextPanel header(Invoice invoice, StringBuilder out) {
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
		println(out,buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(invoice.getType().getAbbrDescription(), 8));
		buf.append(VERTICAL_BAR);
		buf.append(" N\u00BA Fra: ");
		buf.append(AonStringUtils.rightPad(invoice.getDocumentNumber(), 16));
		buf.append(VERTICAL_BAR);
		buf.append(" Fecha: ");
		buf.append(AON.DATE_FORMAT.format(invoice.getIssueDate()));
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
		println(out,buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(8));
		buf.append(VERTICAL_BAR);
		buf.append(" Total : ");
		buf.append(AonStringUtils.rightPad(AON.FMT.format(invoice.getTotal()), 16));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(19));
		buf.append(VERTICAL_BAR);
		buf.append("  R. Social: ");
		buf.append(AonStringUtils.abbreviate(invoice.getRegistryName(), 42));
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		println(out,buf.toString());

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
		println(out,buf.toString());
		return this;
	}


	private InvoiceConsoleTextPanel details(Invoice invoice, StringBuilder out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(TOP_RIGHT_CORNER);
		println(out,buf.toString());

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
		println(out,buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(VERTICAL_LEFT_BAR);
		println(out,buf.toString());

		invoice.detailStream().forEach(det -> detail(det, out));

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(LOWER_RIGHT_CORNER);
		println(out,buf.toString());
		return this;
	}

	private void detail(InvoiceDetail detail, StringBuilder out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils
				.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(detail.getDescription()), 34), 35));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(AON.FMT.format(detail.getQuantity()), 10));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(AON.FMT.format(detail.getPrice()), 12));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(AON.FMT_BASE.format(detail.getTaxableBase()), 17));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		println(out,buf.toString());
		detail.taxStream().forEach(tax -> invoiceTax(tax, out));
		
		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(">>>>");
		buf.append(" [SOURCE ");
		buf.append(detail.getSource());
		buf.append(AonStringUtils.CLOSE_BRACKET);
		buf.append(" [Cuenta contable ");
		buf.append(detail.getAccountCode());
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.HYPHEN);
		buf.append(AonStringUtils.SPACE);
		buf.append(detail.getAccountDescription());
		buf.append(AonStringUtils.CLOSE_BRACKET);
		
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		println(out,buf.toString());
	}

	private void invoiceTax(InvoiceTax tax, StringBuilder out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(">>>>");
		buf.append(AonStringUtils.leftPad(tax.getTaxType().getName2(), 5));
		buf.append(AonStringUtils.leftPad(AON.FMT.format(tax.getPercentage()), 6));
		buf.append(AonStringUtils.PERCENT);
		buf.append(" [Base:");
		buf.append(AonStringUtils.leftPad(AON.FMT_BASE.format(tax.getBase()), 17));
		buf.append("] ");
		buf.append("[Cuota:");
		buf.append(AonStringUtils.leftPad(AON.FMT.format(tax.getQuota()), 13));
		buf.append("] ");
		buf.append("[% Ded:");
		buf.append(AonStringUtils.leftPad(AON.FMT.format(tax.getDeductiblePercent()), 6));
		buf.append("] ");
		buf.append("[Cuota Ded.:");
		buf.append(AonStringUtils.leftPad(AON.FMT.format(tax.getDeductibleQuota()), 13));
		buf.append("] ");
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		println(out,buf.toString());
	}

	private InvoiceConsoleTextPanel vatBreakdown(Invoice invoice, StringBuilder out) {
		invoice.getTaxBreakdown().ifPresent( tb -> {
			StringBuilder buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(40));
			buf.append(TOP_LEFT_CORNER);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
			buf.append(TOP_RIGHT_CORNER);
			println(out,buf.toString());
			tb.vatStream()
				.forEach( tax -> vatInvoiceBreakdown(tax, out) );
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(40));
			buf.append(LOWER_LEFT_CORNER);
			buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
			buf.append(LOWER_RIGHT_CORNER);
			println(out,buf.toString());
		});
		return this;
	}

	private InvoiceConsoleTextPanel withholding(Invoice invoice, StringBuilder out) {
		invoice.getTaxBreakdown()
			.flatMap( tb -> tb.getInvoiceWithholding())
			.ifPresent( iw -> {
				StringBuilder buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(40));
				buf.append(TOP_LEFT_CORNER);
				buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
				buf.append(TOP_RIGHT_CORNER);
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(40));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(1));
				buf.append(AonStringUtils.rightPad(TaxType.RETENTION.getName2(), 5));
				buf.append(AonStringUtils.leftPad(AON.FMT_BASE.format(iw.getBase()), 16));
				buf.append(AonStringUtils.leftPad(AON.FMT.format(iw.getPercentage()), 12));
				buf.append(AonStringUtils.PERCENT);
				buf.append(AonStringUtils.leftPad(AON.FMT.format(iw.getQuota()), 17));
				buf.append(AonStringUtils.repeat(" ", 10));
				buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
				buf.append(VERTICAL_BAR);
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(40));
				buf.append(LOWER_LEFT_CORNER);
				buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
				buf.append(LOWER_RIGHT_CORNER);
				println(out,buf.toString());
				
			});
		return this;
	}

	private void vatInvoiceBreakdown(InvoiceBreakdown tax, StringBuilder out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(40));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(1));
		buf.append(AonStringUtils.rightPad(tax.getTaxType().getName2(), 5));
		buf.append(AonStringUtils.leftPad(AON.FMT_BASE.format(tax.getBase()), 16));
		buf.append(AonStringUtils.leftPad(AON.FMT.format(tax.getPercentage()), 12));
		buf.append(AonStringUtils.PERCENT);
		buf.append(AonStringUtils.leftPad(AON.FMT.format(tax.getQuota()), 17));
		buf.append(AonStringUtils.repeat(" ", 10));
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		println(out,buf.toString());
	}

	private InvoiceConsoleTextPanel totals(Invoice invoice, StringBuilder out) {
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
		buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
		println(out,buf.toString());

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
		buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
		println(out,buf.toString());

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
		buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
		println(out,buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(38));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(AON.FMT_BASE.format(invoice.getTaxableBase()), 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(AON.FMT.format(invoice.getVatQuota()), 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(AON.FMT.format(invoice.getRetentionQuota()), 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.center(AON.FMT.format(invoice.getTotal()), 17));
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
		println(out,buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.spaces(38));
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(HORIZONTAL_UP_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 17));
		buf.append(LOWER_RIGHT_CORNER);
		buf.append(AonStringUtils.spaces( getLineSize() - buf.length()));
		println(out,buf.toString());
		return this;
	}

	
	private InvoiceConsoleTextPanel finances(Invoice invoice, StringBuilder out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(TOP_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(TOP_RIGHT_CORNER);
		println(out,buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.rightPad("FECHA", 10));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad("IMPORTE", 17));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		println(out,buf.toString());

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_RIGHT_BAR);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(VERTICAL_LEFT_BAR);
		println(out,buf.toString());

		invoice.financeStream().forEach(f -> finance(f, out));

		buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(LOWER_LEFT_CORNER);
		buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, getLineSize() - buf.length()));
		buf.append(LOWER_RIGHT_CORNER);
		println(out,buf.toString());
		return this;
	}

	private void finance(Finance finance, StringBuilder out) {
		StringBuilder buf = new StringBuilder();
		buf.append(AonStringUtils.SPACE);
		buf.append(VERTICAL_BAR);
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.center(AON.DATE_FORMAT.format(finance.getDueDate()), 10));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(AON.FMT_BASE.format(finance.getAmount()), 17));
		buf.append(AonStringUtils.SPACE);
		buf.append(AonStringUtils.leftPad(" ", getLineSize() - buf.length()));
		buf.append(VERTICAL_BAR);
		println(out,buf.toString());
	}
	
	private InvoiceConsoleTextPanel fiscal(Invoice invoice, StringBuilder out) {
		Optional.ofNullable( invoice.getFiscal() )
			.ifPresent( invoiceFiscal -> {
				StringBuilder buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append( TOP_LEFT_CORNER );
				buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,92));
				buf.append( TOP_RIGHT_CORNER);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
				println(out, buf.toString() );
				
				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.center("INFORMACI\u00D3N FISCAL",92));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_RIGHT_BAR);
				buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 92));
				buf.append(VERTICAL_LEFT_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
				println(out,buf.toString());
				
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
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad("Fecha factura (fiscal) ....: " 
					+ (invoiceFiscal.getIssueDate() == null
						?SIN_ASIGNAR
						:AON.DATE_FORMAT.format(invoiceFiscal.getIssueDate()))
					,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad("Fecha IVA (fiscal) ........: " 
					+ (invoiceFiscal.getIssueDate() == null
						?SIN_ASIGNAR
						:AON.DATE_FORMAT.format(invoiceFiscal.getTaxDate()))
					,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad("Fecha expedicion (fiscal)..: " 
					+ (invoiceFiscal.getExpDate() == null
						?SIN_ASIGNAR
						:AON.DATE_FORMAT.format(invoiceFiscal.getExpDate()))
					,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad("Actividad .................: " 
						+ invoice.optActivity()
							.map(a -> (a.getEpigraph() + " " +a.getDescription()))
							.orElse("TODAS"),87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());
				
				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad("Rectificativa  ............: "
					+ Optional.ofNullable(invoice.getRectificationInvoice())
						.map( ri -> "SI --> [ " + invoice.getRectificationInvoice() +" ]")
						.orElse("NO") ,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(92));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());
				

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad(checkLabel("Retenci\u00F3n", invoice.isWithholding()) ,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());
				
				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad(checkLabel("Servicio", invoice.isService()) ,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad(checkLabel("R\u00E9gimen agrario", invoice.isWithholdingFarmer()) ,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad(checkLabel("R\u00E9gimen criterio de caja", invoice.isVatAccrualPayment()) ,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(5));
				buf.append(AonStringUtils.rightPad(checkLabel("Inversi\u00F3n", invoice.isInvestment()) ,87));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

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
						println(out,b.toString());
					});
				
				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(LOWER_LEFT_CORNER);
				buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 92));
				buf.append(LOWER_RIGHT_CORNER);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
				println(out,buf.toString());
			}); 
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
	
	private InvoiceConsoleTextPanel doc(Invoice invoice, StringBuilder out) {
		invoice.getDoc()
			.ifPresent( d -> {
				
				StringBuilder buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append( TOP_LEFT_CORNER );
				buf.append(AonStringUtils.repeat(HORIZONTAL_BAR,92));
				buf.append( TOP_RIGHT_CORNER);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
				println(out, buf.toString() );

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.SPACE);
				buf.append("AON ID .:");
				buf.append(AonStringUtils.SPACE);
				buf.append(AonStringUtils.rightPad( AonStringUtils.defaultIfBlank(AonNumberUtils.toString(d.getAonId()), "<NULL>") ,81));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.SPACE);
				buf.append("Bucket..:");
				buf.append(AonStringUtils.SPACE);
				buf.append(AonStringUtils.rightPad( d.getS3Bucket() ,81));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.SPACE);
				buf.append("Key ....:");
				buf.append(AonStringUtils.SPACE);
				buf.append(AonStringUtils.abbreviateMiddle( d.getS3Key(), " [...] " ,81));
				buf.append(VERTICAL_BAR);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));			
				println(out,buf.toString());

				buf = new StringBuilder();
				buf.append(AonStringUtils.spaces(10));
				buf.append(LOWER_LEFT_CORNER);
				buf.append(AonStringUtils.repeat(HORIZONTAL_BAR, 92));
				buf.append(LOWER_RIGHT_CORNER);
				buf.append(AonStringUtils.spaces(getLineSize() - buf.length()));
				println(out,buf.toString());
			});
		return this;
	}
			
	private InvoiceConsoleTextPanel messages(Invoice invoice, StringBuilder out) {
		if (invoice.hasMessages()) {
			println(out,"");
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
			println(out,buf.toString());
			
			buf = new StringBuilder();
			buf.append(AonStringUtils.spaces(15));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.rightPad("TYP", 4));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.rightPad("CODE", 5));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.rightPad("FIELD", 25));
			buf.append(VERTICAL_BAR);
			buf.append(AonStringUtils.rightPad("MESSAGE", 80));
			buf.append(VERTICAL_BAR);
			println(out,buf.toString());			
			

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
			println(out,buf.toString());

			invoice.messageStream()
			.forEach( e -> {
				StringBuilder bf = new StringBuilder();
				bf.append(AonStringUtils.spaces(15));
				bf.append(VERTICAL_BAR);
				bf.append(AonStringUtils.rightPad(e.getLevel() == null ? "" : e.getLevel().toString(), 4));
				bf.append(VERTICAL_BAR);
				bf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(e.getCode()), 5));
				bf.append(VERTICAL_BAR);
				bf.append(AonStringUtils.rightPad(e.getContext() == null ? "" : e.getContext().toString(), 25));
				bf.append(VERTICAL_BAR);
				bf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(e.getMessage()),79),80));
				bf.append(VERTICAL_BAR);
				println(out,bf.toString());			
			});
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
			println(out,buf.toString());
		}
		return this;
	}

	private void println(StringBuilder out, String text) {
		out.append(text);
		out.append("\n");
	}
}
