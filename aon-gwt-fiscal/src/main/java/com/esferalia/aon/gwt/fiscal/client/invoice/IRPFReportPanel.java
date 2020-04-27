package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
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

public class IRPFReportPanel extends ScrollPanel{
	
	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/IRPFReportStream");
	private static final String HEADER = AonStringUtils.rightPad("TIPO",5)
			+ AonStringUtils.rightPad("TIPO RET.",15)
			+ AonStringUtils.rightPad("EPIGR.",8)
			+ AonStringUtils.rightPad("N\u00BA DOCUMENTO",15)
			+ AonStringUtils.rightPad("TITULAR FACTURA",31)
			+ AonStringUtils.rightPad("FECHA FAC.",11)
			+ AonStringUtils.rightPad("FECHA IMP.",11)
			+ AonStringUtils.leftPad("BASE IMP.",15)		
			+ AonStringUtils.leftPad("% IRPF",8)
			+ AonStringUtils.leftPad("CUOTA",15)
			+ AonStringUtils.leftPad("% DED.",8)
			+ AonStringUtils.leftPad("CUOTA DED.",15)
			+ AonStringUtils.SPACE
			+ AonStringUtils.rightPad("N\u00BA REFERENCIA",25)
			+ AonStringUtils.rightPad("COD. POSTAL",15)
			+ AonStringUtils.rightPad("MUNICIPIO",25)
			+ AonStringUtils.repeat(" ", 2)
			;
	private static final String GROUPED_HEADER = AonStringUtils.rightPad("TITULAR FACTURA",31)
			+ AonStringUtils.leftPad("BASE IMP.",15)
			+ AonStringUtils.leftPad("CUOTA",15)
			+ AonStringUtils.leftPad("CUOTA DED.",15)
			+ AonStringUtils.SPACE
			+ AonStringUtils.repeat(" ", 2)
			;

	public IRPFReportPanel(String domainName,String user, int domain, IRPFParams params, String title , String subtitle) {
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
			private double sumDedQuota = 0.0;
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
	
				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						for (JsIRPFBreakdown irpf = read(text); text != null; irpf = read(text)) {
							if (html.getWidgetCount() == 0) {
								if (params.getGroupByNif() == 0) {
									addHeaderWidget(html, title, subtitle);
								} else {
									addGroupedHeaderWidget(html, title, subtitle);
								}
							}
							if (params.getGroupByNif() == 0) {
								html.add( getIRPFWidget(irpf) );
							} else {
								html.add( getGroupedIRPFWidget(irpf) );
							}
							sumBase = sumBase + irpf.getBase();
							sumQuota = sumQuota + irpf.getQuota();
							if (!irpf.isSales()) sumDedQuota = sumDedQuota + irpf.getDeductibleQuota();
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
							+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumDedQuota),15)
							+ AonStringUtils.repeat(" ", 28)
						);
						Label total_grouped = new Label(AonStringUtils.leftPad("TOTAL: ",29)
							+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumBase),17)
							+ AonStringUtils.leftPad(" ",7)
							+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumQuota),3)
							+ AonStringUtils.leftPad(" ",7)
							+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumDedQuota),3)
							+ AonStringUtils.repeat(" ", 10)
						);
						html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
						if (params.getGroupByNif() == 0) {
							html.add(bold(total));
						} else {
							html.add(bold(total_grouped));
						}
						html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
					}
				}
			}

			private JsIRPFBreakdown read(String text) {
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
		requestData.append("&irpfParams=" + JsonParams.convert( params ));
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
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(HEADER)));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER.length()))));
	}
	
	private void addGroupedHeaderWidget(FlowPanel html, String title, String subtitle) {
		if (AonStringUtils.isNotBlank(title)) {
			html.add(bold(new Label(AonStringUtils.center(title, HEADER.length()))));
		}	
		if (AonStringUtils.isNotBlank(subtitle)) {
			html.add(bold(new Label(AonStringUtils.center(subtitle, HEADER.length()))));
		}
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(GROUPED_HEADER)));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER.length()))));
	}

	private void addNoDataWidget(FlowPanel html) {
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.center(AON.MSG.noData(), HEADER.length()))));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER.length()))));
	}

	private Widget getIRPFWidget(JsIRPFBreakdown irpf) {
		InvoiceType invoiceType = InvoiceType.safeValueOf(irpf.getInvoiceType());
		WithholdingType type = WithholdingType.safeValueOf(irpf.getWithholdingType());
		String issueDate = AON.DATE_FORMAT.format(irpf.getIssueDate());
		String taxDate = AON.DATE_FORMAT.format(irpf.getTaxDate());
		HTML line = new HTML();
		line.setStyleName(AON.AON_CSS.aonReportRow());
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.substring((invoiceType==null?null:invoiceType.getDescription()),0,4) ,5));
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.substring((type==null?null:type.getDescription()),0,14) ,15));
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.defaultIfBlank(irpf.getEpigraph(), AonStringUtils.SPACE),8));
		builder.appendEscaped( AonStringUtils.rightPad(irpf.getDocumentNumber(),15));
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.abbreviate( 
				  AonStringUtils.defaultIfBlank(irpf.getRegistryDocument(), AonStringUtils.EMPTY) 
				+ (AonStringUtils.isBlank(irpf.getRegistryDocument())?AonStringUtils.EMPTY:AonStringUtils.HYPHEN)
				+ AonStringUtils.defaultIfBlank(irpf.getName(), AonStringUtils.EMPTY)  
				,29 ),30));
		builder.appendEscaped( AonStringUtils.SPACE);
		if (!AonStringUtils.equals( issueDate, taxDate)) builder.appendHtmlConstant("<span style=\"color: orange;\">");
		builder.appendEscaped( AonStringUtils.rightPad(issueDate,11) );
		if (!AonStringUtils.equals( issueDate, taxDate)) builder.appendHtmlConstant("</span>"); 
		builder.appendEscaped( AonStringUtils.rightPad(taxDate,11) );
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(irpf.getBase()),15));		
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(irpf.getPercent() ) + AonStringUtils.PERCENT,8)); 
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(irpf.getQuota()),15));
		builder.appendEscaped( AonStringUtils.leftPad(irpf.isSales()?AonStringUtils.SPACE:AON.CURRENCY_FORMAT.format(irpf.getDeductiblePercent()) + AonStringUtils.PERCENT,8));
		builder.appendEscaped( AonStringUtils.leftPad(irpf.isSales()?AonStringUtils.SPACE:AON.CURRENCY_FORMAT.format(irpf.getDeductibleQuota()),15));
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.rightPad(AonStringUtils.abbreviate(irpf.getReferenceCode(),25),25)); 
		builder.appendEscaped( AonStringUtils.rightPad(irpf.getZip()==null?"":irpf.getZip(),15)); 
		builder.appendEscaped( AonStringUtils.rightPad(irpf.getCity()==null?"":irpf.getCity(),25));
		builder.appendEscaped( AonStringUtils.repeat(" ", 2));
		line.setHTML(builder.toSafeHtml());
		return line;
	}

	private Widget getGroupedIRPFWidget(JsIRPFBreakdown irpf) {
		HTML line = new HTML();
		line.setStyleName(AON.AON_CSS.aonReportRow());
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.abbreviate( 
				  AonStringUtils.defaultIfBlank(irpf.getRegistryDocument(), AonStringUtils.EMPTY) 
				+ (AonStringUtils.isBlank(irpf.getRegistryDocument())?AonStringUtils.EMPTY:AonStringUtils.HYPHEN)
				+ AonStringUtils.defaultIfBlank(irpf.getName(), AonStringUtils.EMPTY)  
				,29 ),30));
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(irpf.getBase()),15));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(irpf.getQuota()),15));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(irpf.getDeductibleQuota()),15));
		builder.appendEscaped( AonStringUtils.SPACE);
		builder.appendEscaped( AonStringUtils.repeat(" ", 2));
		line.setHTML(builder.toSafeHtml());
		return line;
	}

}
