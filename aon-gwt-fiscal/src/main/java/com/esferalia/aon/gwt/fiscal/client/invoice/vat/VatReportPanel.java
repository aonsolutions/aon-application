package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class VatReportPanel extends ScrollPanel{
	
	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/VatReportStream");
	
	public VatReportPanel(VatReportModuleOptions options, AccountingReportParams params) {
		this( options, params, null, null);
	}

	public VatReportPanel(VatReportModuleOptions options, AccountingReportParams params, String title , String subtitle) {
		setStyleName(AON.CSS.aonScrollArea());
		
		FlowPanel line = new FlowPanel();
		line.setStyleName(AON.CSS.aonMargin());
		Label label =  new Label("Un momento, por favor ... cargando ...");
		label.setStyleName(AON.CSS.aonTextCenter());
		label.addStyleName(AON.CSS.aonBold());
		line.add(label);
		setWidget(line);
		
		XMLHttpRequest xmlhr = XMLHttpRequest.create();
		xmlhr.open(FormPanel.METHOD_POST, REPORT_URL);
		xmlhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhr.setOnReadyStateChange(xhr -> {
			if (xhr.getReadyState() == XMLHttpRequest.DONE) {
				JavaScriptObject arrayObject = JsonUtils.safeEval(xhr.getResponseText());
				JsArray<JsVatContext> array = arrayObject.cast();
				JsVatContextBreakdownGridPanel grid = new JsVatContextBreakdownGridPanel();
				grid.render( array );
				setWidget(grid);
				grid.addSelectionHandler(event -> showInvoice(options, event.getSelectedItem()));
			}
		});
		
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+ IRequestParamsNames.DOMAIN_NAME + "=" + options.getDomainName()  );
		requestData.append("&"+ IRequestParamsNames.DOMAIN_ID + "=" + options.getDomain() );
		requestData.append("&"+ IRequestParamsNames.USER + "=" + options.getUser() );
		requestData.append("&"+ IRequestParamsNames.VAT_PARAMS + "=" + JsonParams.convert( params ));
		xmlhr.send(requestData.toString());
	}
	
	private void showInvoice(VatReportModuleOptions options,JsVatContext vt) {
		int invoiceId = vt.getInvoice();
		VatReport.SERVICE.getInvoice(options.getOccam(), invoiceId,new AsyncCallback<Invoice>() {
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
				AonMessageDialog.error(caught.getMessage());
			}
		});
	}

}
