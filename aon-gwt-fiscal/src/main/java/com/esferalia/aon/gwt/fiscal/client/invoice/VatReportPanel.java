package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class VatReportPanel extends ScrollPanel{
	
	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/VatReportStream");
	private static final String LEGEND = "S (Servicio); I (Inversi\u00F3n); A (R\u00E9gimen agrario); R (Rectificativa); C (Criterio de caja)";
	private static final String HEADER = AonStringUtils.rightPad("TIPO",5)
			+ AonStringUtils.rightPad("TRAN",5)
			+ "S I A R C "
			+ AonStringUtils.rightPad("EPIGR.",8)
			+ AonStringUtils.rightPad("N\u00BA DOCUMENTO",15)
			+ AonStringUtils.rightPad("TITULAR FACTURA",31)
			+ AonStringUtils.rightPad("FECHA FAC.",11)
			+ AonStringUtils.rightPad("FECHA IMP.",11)
			+ AonStringUtils.leftPad("BASE IMP.",15)		
			+ AonStringUtils.leftPad("% IVA",8)
			+ AonStringUtils.leftPad("CUOTA",15)
			+ AonStringUtils.leftPad("% RE",8)
			+ AonStringUtils.leftPad("CUOTA RE",15)
			+ AonStringUtils.leftPad("% DED.",8)
			+ AonStringUtils.leftPad("CUOTA DED.",15)
			+ AonStringUtils.SPACE
			+ AonStringUtils.rightPad("N\u00BA REFERENCIA",25)
			+ AonStringUtils.repeat(" ", 2)
			;

	public VatReportPanel(String domainName, String user, int domain, AccountingReportParams params, String title , String subtitle) {
		setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel html = new FlowPanel( PreElement.TAG );
		html.setStyleName(AON.AON_CSS.aonFixedFont());
		html.addStyleName(AON.AON_CSS.aonMarginBottom());
		html.addStyleName(AON.AON_CSS.aonFontSmall());
		html.addStyleName(AON.AON_CSS.aonReport());
		setWidget(html);
		
		final AonToast toast = new AonToast();
		final InlineLabel label =  new InlineLabel("Un momento, por favor ...");
		toast.show("Cargando ...", label);
		
//		final MutableDouble sumBase = new MutableDouble();

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, REPORT_URL);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			
			private int loaded = 0;
			private double sumBase = 0.0;
			private double sumQuota = 0.0;
			private double sumReQuota = 0.0;
			private double sumDedQuota = 0.0;
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
	
				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						for (JsVatContext vat = read(text); text != null; vat = read(text)) {
							if (html.getWidgetCount() == 0) {
								addHeaderWidget(html, title, subtitle);		
							}
							html.add( getVatWidget(vat) );
							sumBase = sumBase + vat.getBase();
							sumQuota = sumQuota + vat.getQuota();
							if (vat.isSurcharge()) sumReQuota = sumReQuota + vat.getSurchargeQuota();
							if (!vat.isSales()) sumDedQuota = sumDedQuota + vat.getDeductibleQuota();
						}
						
					} catch (IndexOutOfBoundsException e) {
					}
				}
				if (state == XMLHttpRequest.DONE) {
					toast.hide();
					if (html.getWidgetCount() == 0) {
						addNoDataWidget(html);
					} else {
						Label total = new Label(AonStringUtils.leftPad("TOTAL: ",94)				
							+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumBase),17)
							+ AonStringUtils.leftPad(" ",8)
							+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumQuota),15)
							+ AonStringUtils.leftPad(" ",8)
							+ AonStringUtils.leftPad(AonMathUtils.isZero(sumReQuota)? " " : AON.CURRENCY_FORMAT.format(sumReQuota),15)
							+ AonStringUtils.leftPad(" ",8)
							+ AonStringUtils.leftPad(AonMathUtils.isZero(sumDedQuota)? " " : AON.CURRENCY_FORMAT.format(sumDedQuota),15)
							+ AonStringUtils.repeat(" ", 28)
							);
						html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
						html.add(bold(total));
						html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
					}
				}
			}
	
			private JsVatContext read(String text) {
				for (int begin = loaded; begin < text.length(); begin++) {
					if (text.charAt(begin) == '{') {
						loaded = findEnd(text, begin + 1) + 1;
						String json = text.substring(begin, loaded);
						return JsonUtils.safeEval(json);
					}
				}
				throw new IndexOutOfBoundsException();
			}
	
			private int findEnd(String text, int start) {
				for (int end = start; end < text.length(); end++) {
					switch (text.charAt(end)) {
					case '}':
						return end;
					case '{':
						end = findEnd(text, end + 1);
					}
				}
				throw new IndexOutOfBoundsException();
			}
		});
		StringBuffer requestData = new StringBuffer();
		requestData.append("&domainName=" + domainName  );
		requestData.append("&domainId=" + domain );
		requestData.append("&user=" + user );
		requestData.append("&vatParams=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());

		
	}

	private Widget bold( Widget w) {
		w.setStyleName(AON.AON_CSS.aonBold());
		return w;
	}
	private void addHeaderWidget(FlowPanel html, String title, String subtitle) {
		if (AonStringUtils.isNotBlank(title)) {
			html.add(bold(new Label(AonStringUtils.center(title, HEADER.length()))));
		}	
		if (AonStringUtils.isNotBlank(subtitle)) {
			html.add(bold(new Label(AonStringUtils.center(subtitle, HEADER.length()))));
		}
		html.add(bold(new Label(AonStringUtils.rightPad("     " + LEGEND, HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(HEADER)));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER.length()))));
	}

	private void addNoDataWidget(FlowPanel html) {
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.center(AON.MSG.noData(), HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
	}

	private Widget getVatWidget(JsVatContext vat) {
		InvoiceType invoiceType = InvoiceType.safeValueOf(vat.getInvoiceType());
		InvoiceTransactionType transaction = InvoiceTransactionType.safeValueOf(vat.getTransaction());
		String documentCountry = (vat.getRegistryDocumentCountry() == null)?AonStringUtils.EMPTY:vat.getRegistryDocumentCountry();
		if (Country.ES.getIso2().equals(vat.getRegistryDocumentCountry())) {
			documentCountry = AonStringUtils.EMPTY;
		} else {
			documentCountry += AonStringUtils.SLASH; 
		}
		String issueDate = AON.DATE_FORMAT.format(vat.getIssueDate());
		String taxDate = AON.DATE_FORMAT.format(vat.getTaxDate());
		HTML line = new HTML();
		line.setStyleName(AON.AON_CSS.aonReportRow());
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.substring((invoiceType==null?null:invoiceType.getDescription()),0,4) ,5));
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.substring((transaction==null?null:transaction.getDescription()),0,4) ,5));
		builder.appendEscaped( vat.isService()?"S":AonStringUtils.SPACE); 
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( vat.isInvestment()?"I":AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( vat.isFarmerRegime()?"A":AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( vat.isRectification()?"R":AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( vat.isVatAccrualRegime()?"C":AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(vat.getEpigraph(), AonStringUtils.SPACE),8));
		builder.appendEscaped( AonStringUtils.rightPad(vat.getDocumentNumber(),15));
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.abbreviate(
				  documentCountry
				+ AonStringUtils.defaultIfBlank(vat.getRegistryDocument(), AonStringUtils.EMPTY) 
				+ (AonStringUtils.isBlank(vat.getRegistryDocument())?AonStringUtils.EMPTY:AonStringUtils.HYPHEN)
				+ AonStringUtils.defaultIfBlank(vat.getRegistryName(), AonStringUtils.EMPTY)  
				,29 ),30));
		builder.appendEscaped( AonStringUtils.SPACE);
		if (!AonStringUtils.equals( issueDate, taxDate)) builder.appendHtmlConstant("<span style=\"color: orange;\">");
		builder.appendEscaped( AonStringUtils.rightPad(issueDate,11) );
		if (!AonStringUtils.equals( issueDate, taxDate)) builder.appendHtmlConstant("</span>"); 
		builder.appendEscaped( AonStringUtils.rightPad(taxDate,11) );
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(vat.getBase()),15));		
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(vat.getPercentage() ) + AonStringUtils.PERCENT,8)); 
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(vat.getQuota()),15));
		builder.appendEscaped( AonStringUtils.leftPad(vat.isSurcharge()?AON.CURRENCY_FORMAT.format(vat.getSurchargePercent()) + AonStringUtils.PERCENT:AonStringUtils.SPACE,8));
		builder.appendEscaped( AonStringUtils.leftPad(vat.isSurcharge()?AON.CURRENCY_FORMAT.format(vat.getSurchargeQuota()):AonStringUtils.SPACE,15));
		builder.appendEscaped( AonStringUtils.leftPad(vat.isSales()?AonStringUtils.SPACE:AON.CURRENCY_FORMAT.format(vat.getDeductiblePercent()) + AonStringUtils.PERCENT,8));
		builder.appendEscaped( AonStringUtils.leftPad(vat.isSales()?AonStringUtils.SPACE:AON.CURRENCY_FORMAT.format(vat.getDeductibleQuota()),15));
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.abbreviate(vat.getReferenceCode(),25),25)); 
		builder.appendEscaped( AonStringUtils.repeat(" ", 2));
		line.setHTML(builder.toSafeHtml());
		return line;
	}
}
