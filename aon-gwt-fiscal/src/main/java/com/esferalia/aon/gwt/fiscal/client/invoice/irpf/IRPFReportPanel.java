package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class IRPFReportPanel extends ScrollPanel{
	
	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/IRPFReportStream");

	public IRPFReportPanel(IrpfReportModuleOptions options, IRPFParams params, String title , String subtitle) {
		setStyleName(AON.CSS.aonScrollArea());
		JsIRPFBreakdownInvoiceGridPanel grid = new JsIRPFBreakdownInvoiceGridPanel();
		grid.addSelectionHandler(event -> showInvoice(options, event.getSelectedItem()));
		grid.setTitle(title);
		grid.setSubTitle(subtitle);
		setWidget(grid);
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, REPORT_URL);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			
			private int loaded = 0;
			
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();
				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
					String text = xhr.getResponseText();
					try {
						for (JsIRPFBreakdown irpf = read(text); text != null; irpf = read(text)) {
							grid.addRow(irpf);
						}
					} catch (IndexOutOfBoundsException e) {
						// Nothing
					}
				}
				if (state == XMLHttpRequest.DONE) {
					grid.addFooterRow();
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
					if (text.charAt(end) == '}') {
						return end;
					} else if (text.charAt(end) == '{') {
						end = findEnd(text, end + 1);
					}
				}
				throw new IndexOutOfBoundsException();
			}
		});
		StringBuilder requestData = new StringBuilder();
		requestData.append("&domainName=" + options.getDomainName()  );
		requestData.append("&domainId=" + options.getDomain() );
		requestData.append("&user=" + options.getUser() );
		requestData.append("&irpfParams=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());

	}

	private void showInvoice(IrpfReportModuleOptions options, JsIRPFBreakdown br) {
		int invoiceId = br.getInvoice();
		IRPFReport.SERVICE.getInvoice(options.getOccam(), invoiceId,new AsyncCallback<Invoice>() {
			@Override
			public void onSuccess(Invoice inv) {
				AonCustomPopup dialog = new AonCustomPopup();
				dialog.setWidth((Window.getClientWidth() - 100) + "px");
				dialog.setHeight((Window.getClientHeight() - 100) + "px");
				dialog.setAnimationEnabled(true);
				dialog.setGlassEnabled(true);
				dialog.setModal(true);
				dialog.setCaption(AON.MSG.invoice());
				dialog.add(new AonInvoiceViewer(inv));
				dialog.center();
				dialog.show();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Error inesperado: " + caught.getMessage()));
			}
		});
	}
}
