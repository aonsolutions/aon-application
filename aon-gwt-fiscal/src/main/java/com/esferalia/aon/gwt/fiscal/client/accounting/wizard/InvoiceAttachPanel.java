package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class InvoiceAttachPanel extends SimpleLayoutPanel {

	public InvoiceAttachPanel( IInvoicePanelCallback callback ) {
		setStyleName(AON.AON_CSS.aonTextCenter());
		
		AccountingInvoice ai = callback.getInvoice();
		if (ai != null && ai.isDocumentAttached() ) {
			String params = "domain="+ callback.getCurrentDomainId() 
				+ "&id=" +  ai.getAttach().getId() 
				+ "&attach_type=invoice";
			params = InvoiceAttachPanel.b64encode(params);
			String url = URL.encode(GWT.getModuleBaseURL() + "ms/download_attachment" 
					+ "/" + callback.getCurrentDomainName() 
					+ "/" + callback.getCurrentUser() 
					+ "/" +  params);
			if ( ai.getAttach().getMimeType() != null && ai.getAttach().getMimeType().isPDF()) {
				this.setWidget(new FullViewer(url));
			} else if ( ai.getAttach().getMimeType() != null && ai.getAttach().getMimeType().isImage()) {
				Image image = new Image( url );
				this.setWidget(image);
			} else {
				Label unknown = new Label("No se ha podido determinar un visor para este tipo de documento.");
				unknown.setStyleName(AON.AON_CSS.aonBlockCenter());
				unknown.addStyleName(AON.AON_CSS.aonMarginTop());
				unknown.addStyleName(AON.AON_CSS.aonBold());
				this.setWidget(unknown);
			}
		} else {
			Label unknown = new Label("La factura no tiene documentos adjuntos.");
			unknown.setStyleName(AON.AON_CSS.aonBlockCenter());
			unknown.addStyleName(AON.AON_CSS.aonMarginTop());
			unknown.addStyleName(AON.AON_CSS.aonBold());
			this.setWidget(unknown);
		}
	}

	private static native String b64encode(String a) /*-{
	  return window.btoa(a);
	}-*/;	
	
}
