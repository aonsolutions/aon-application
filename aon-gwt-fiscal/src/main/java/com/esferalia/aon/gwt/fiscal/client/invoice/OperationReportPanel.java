package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
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

public class OperationReportPanel extends ScrollPanel{
		
	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/OperationReportStream");
	private static final String HEADER_IVA = AonStringUtils.rightPad("ID", 5)
			+ AonStringUtils.rightPad("FECHA", 15)
			+ AonStringUtils.rightPad("FECHA IVA", 15)
			+ AonStringUtils.rightPad("CONCEPTO",50)
			+ AonStringUtils.rightPad("N\u00BA DOCUMENTO",15)
			+ AonStringUtils.rightPad("TITULAR",36)
			+ AonStringUtils.leftPad("BASE IMP.",15)
			+ AonStringUtils.leftPad("%IVA",15)
			+ AonStringUtils.leftPad("CUOTA IVA",15)
			+ AonStringUtils.leftPad("% REQ.",15)
			+ AonStringUtils.leftPad("CUOTA REQ.",15)
			+ AonStringUtils.leftPad("TOTAL FRA.",16)
			+ AonStringUtils.repeat(" ", 2)
			;
	private static final String HEADER_IRPF = AonStringUtils.rightPad("ID", 5)
			+ AonStringUtils.rightPad("FECHA", 15)
			+ AonStringUtils.rightPad("CONCEPTO",50)
			+ AonStringUtils.rightPad("N\u00BA DOCUMENTO",15)
			+ AonStringUtils.rightPad("TITULAR",36)
			+ AonStringUtils.leftPad("BASE IMP.",15)
			+ AonStringUtils.leftPad("IMPUESTOS",15)
			+ AonStringUtils.leftPad("TOTAL",16)
			+ AonStringUtils.repeat(" ", 2)
			;

	public OperationReportPanel(String domainName,String user, int domain, OperationParams params) {
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

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, REPORT_URL);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;
			private double sumBase = 0.0;
			private double sumQuota = 0.0;
			private double sumSurchargeQuota = 0.0;
			private double sumTotal = 0.0;
			Map<Double,Double[]> mapIvaSummary = new TreeMap<Double, Double[]>();
			Map<Double,Double[]> mapSurSummary = new TreeMap<Double, Double[]>();
			Map<String,Object[]> mapConceptSummary = new TreeMap<String, Object[]>();
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
	
				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						int i = 1;
						for (JsOperationBreakdown op = read(text); text != null; op = read(text)) {
							if (html.getWidgetCount() == 0) {
								addHeaderWidget(html, params.getIrpf());
							}
							
							sumBase = sumBase + op.getBase();
							sumQuota = sumQuota + op.getQuota();
							sumSurchargeQuota = sumSurchargeQuota + op.getSurchargeQuota();
							sumTotal = sumTotal + op.getTotal();
							if (params.getIrpf() == true) { // IRPF
								html.add( getIrpfWidget(op, i) );
								Object[] indexIrpf = mapConceptSummary.get(op.getAccount());								  
								if (indexIrpf != null) {
									Object[] obj = new Object[2];	
									obj[0] = op.getAccountDescription();
									obj[1] = (double) indexIrpf[1] + op.getBase();
									mapConceptSummary.put(op.getAccount(), obj);
								} else {
									Object[] obj = new Object[2];
									obj[0] = op.getAccountDescription();
									obj[1] = op.getBase();
									mapConceptSummary.put(op.getAccount(), obj);
								}
							} else { // IVA
								html.add( getIvaWidget(op, i) );
								
								Double[] indexIva = mapIvaSummary.get(op.getPercent());
								if (indexIva != null) {
									Double[] array = {0.0,0.0};	
									array[0] = indexIva[0] + op.getBase();
									array[1] = indexIva[1] + op.getQuota();
									mapIvaSummary.put(op.getPercent(), array);
								} else {
									Double[] array = {0.0,0.0};
									array[0] = op.getBase();
									array[1] = op.getQuota();
									mapIvaSummary.put(op.getPercent(), array);
								}
	
								if (op.getSurchargePercent() != 0) {								
									Double[] indexSur = mapSurSummary.get(op.getSurchargePercent());
									if (indexSur != null) {
										Double[] array = {0.0,0.0};	
										array[0] = indexSur[0] + op.getBase();
										array[1] = indexSur[1] + op.getSurchargeQuota();
										mapSurSummary.put(op.getSurchargePercent(), array);
									} else {
										Double[] array = {0.0,0.0};	
										array[0] = op.getBase();
										array[1] = op.getSurchargeQuota();
										mapSurSummary.put(op.getSurchargePercent(), array);
									}
								}
							}
							i++;
							
						}
					} catch (IndexOutOfBoundsException e) {
					}
				}
				if (state == XMLHttpRequest.DONE) {
					toast.hide();
					if (html.getWidgetCount() == 0) {
						addNoDataWidget(html);
					} else {
						if (params.getIrpf() == true) { // IRPF
							Label total = new Label(AonStringUtils.leftPad("TOTAL: ",119)				
								+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumBase),17)
								+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumQuota + sumSurchargeQuota), 15)
								+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumTotal),16)
							);
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IRPF.length()))));
							html.add(bold(total));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IRPF.length()))));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IRPF.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IRPF.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IRPF.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IRPF.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IRPF.length())));
							
							// Resumen por Concepto
							html.add(bold(new Label("RESUMEN POR CONCEPTO")));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, 90))));
							Label summaryIrpfHeader = new Label(AonStringUtils.rightPad("CUENTA", 15)
								+ AonStringUtils.rightPad("DESCRIPCI\u00D3N", 60)
								+ AonStringUtils.leftPad("TOTAL", 15));
							html.add(bold(summaryIrpfHeader));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, 90))));
							for (Entry<String, Object[]> entry : mapConceptSummary.entrySet()) {
								html.add(new Label(AonStringUtils.rightPad(entry.getKey(), 15)
										+ AonStringUtils.rightPad(entry.getValue()[0]==null?"":entry.getValue()[0].toString(), 60)
										+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format((double) entry.getValue()[1]), 15)));
								
							}
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
								
						} else { // IVA
							Label total = new Label(AonStringUtils.leftPad("TOTAL: ",134)				
								+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumBase),17)
								+ AonStringUtils.leftPad(" ",8)
								+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumQuota),22)
								+ AonStringUtils.leftPad(" ",8)
								+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumSurchargeQuota),22)
								+ AonStringUtils.leftPad(" ", 7)
								+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(sumTotal),9)
							);
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IVA.length()))));
							html.add(bold(total));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IVA.length()))));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
						
							// Resumen por Tipos de IVA
							html.add(bold(new Label("RESUMEN POR TIPOS DE IVA")));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, 30))));
							Label summaryIvaHeader = new Label(AonStringUtils.rightPad("BASE", 10)
								+ AonStringUtils.leftPad("TIPO IVA", 10)
								+ AonStringUtils.leftPad("CUOTA", 10));
							html.add(bold(summaryIvaHeader));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, 30))));
							for (Entry<Double, Double[]> entry : mapIvaSummary.entrySet()) {
								html.add(new Label(AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(entry.getValue()[0]), 10)
									+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(entry.getKey()) + AonStringUtils.PERCENT, 10)
									+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(entry.getValue()[1]), 10)));
							}
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
							html.add(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length())));
							
							// Resumen por Tipos de REq
							html.add(bold(new Label("RESUMEN POR TIPOS DE RECARGO DE EQUIVALENCIA")));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, 30))));
							Label summaryReqHeader = new Label(AonStringUtils.rightPad("BASE", 10)
								+ AonStringUtils.leftPad("TIPO REQ", 10)
								+ AonStringUtils.leftPad("CUOTA", 10));
							html.add(bold(summaryReqHeader));
							html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, 30))));
							for (Entry<Double, Double[]> entry : mapSurSummary.entrySet()) {
								html.add(new Label(AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(entry.getValue()[0]), 10)
									+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(entry.getKey()) + AonStringUtils.PERCENT, 10)
									+ AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(entry.getValue()[1]), 10)));
							}
						}
					}
				}
			}

			private JsOperationBreakdown read(String text) {
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
		requestData.append("&domainName=" + domainName );
		requestData.append("&domainId=" + domain );
		requestData.append("&user=" + user );
		requestData.append("&irpfParams=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
	}

	private Widget bold( Widget w) {
		w.setStyleName(AON.AON_CSS.aonBold());
		return w;
	}
	private void addHeaderWidget(FlowPanel html, boolean irpf) {
		if (irpf == true) {
			html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IRPF.length()))));
			html.add(bold(new Label(HEADER_IRPF)));
			html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IRPF.length()))));
			html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IRPF.length()))));
		} else {
			html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IVA.length()))));
			html.add(bold(new Label(HEADER_IVA)));
			html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IVA.length()))));
			html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.SPACE, HEADER_IVA.length()))));
		}
	}

	private void addNoDataWidget(FlowPanel html) {
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IVA.length()))));
		html.add(bold(new Label(AonStringUtils.center(AON.MSG.noData(), HEADER_IVA.length()))));
		html.add(bold(new Label(AonStringUtils.repeat(AonStringUtils.HYPHEN, HEADER_IVA.length()))));
	}

	private Widget getIvaWidget(JsOperationBreakdown op, int i) {
		String entryDate = AON.DATE_FORMAT.format(op.getEntryDate());
		String taxDate = AON.DATE_FORMAT.format(op.getTaxDate());
		HTML line = new HTML();
		line.setStyleName(AON.AON_CSS.aonReportRow());
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscaped( AonStringUtils.rightPad(i, 5));
		builder.appendEscaped( AonStringUtils.rightPad(entryDate,15) );
		builder.appendEscaped( AonStringUtils.rightPad(taxDate,15) );
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.abbreviate(op.getConcept(), 50), 50) );
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.trimToEmpty(op.getDocumentNumber()),15));
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.abbreviate(
				  AonStringUtils.defaultIfBlank(op.getRegistryDocument(), AonStringUtils.EMPTY)
				+ (AonStringUtils.isBlank(op.getRegistryDocument())?AonStringUtils.EMPTY:AonStringUtils.HYPHEN)
				+ AonStringUtils.defaultIfBlank(op.getRegistryName(), AonStringUtils.EMPTY)
				,34 ),35));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getBase()),16));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getPercent() ) + AonStringUtils.PERCENT,15));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getQuota()),15));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getSurchargePercent() ) + AonStringUtils.PERCENT,15));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getSurchargeQuota()),15));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getTotal()),16));
		builder.appendEscaped( AonStringUtils.repeat(" ", 2));
		line.setHTML(builder.toSafeHtml());
		return line;
	}

	private Widget getIrpfWidget(JsOperationBreakdown op, int i) {
		String entryDate = AON.DATE_FORMAT.format(op.getEntryDate());
		HTML line = new HTML();
		line.setStyleName(AON.AON_CSS.aonReportRow());
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendEscaped( AonStringUtils.rightPad(i, 5));
		builder.appendEscaped( AonStringUtils.rightPad(entryDate,15) );
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.abbreviate(op.getConcept(), 50), 50) );
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.trimToEmpty(op.getDocumentNumber()),15));
		builder.appendEscaped( AonStringUtils.rightPad( AonStringUtils.abbreviate(
			  AonStringUtils.defaultIfBlank(op.getRegistryDocument(), AonStringUtils.EMPTY)
			+ (AonStringUtils.isBlank(op.getRegistryDocument())?AonStringUtils.EMPTY:AonStringUtils.HYPHEN)
			+ AonStringUtils.defaultIfBlank(op.getRegistryName(), AonStringUtils.EMPTY)
			,34 ),35));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getBase()),16));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getQuota() + op.getSurchargeQuota()),15));
		builder.appendEscaped( AonStringUtils.leftPad(AON.CURRENCY_FORMAT.format(op.getTotal()),16));
		builder.appendEscaped( AonStringUtils.repeat(" ", 2));
		line.setHTML(builder.toSafeHtml());
		return line;
		
	}

}
