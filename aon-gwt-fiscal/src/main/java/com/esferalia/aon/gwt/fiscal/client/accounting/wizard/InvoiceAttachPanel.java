package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class InvoiceAttachPanel extends ScrollPanel {

	private Viewer pdfViewer = new Viewer();

	public InvoiceAttachPanel( IInvoicePanelCallback callback ) {
		AccountingInvoice ai = callback.getInvoice();
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setStyleName(AON.AON_CSS.aonBlockCenter());
		verticalPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		verticalPanel.addStyleName(AON.AON_CSS.aonMarginBottom());
		this.setWidget(verticalPanel);

		if (ai != null && ai.isDocumentAttached() ) {
			String params = "domain="+ callback.getCurrentDomainId() 
				+ "&id=" +  ai.getAttach().getId() 
				+ "&attach_type=invoice";
			params = InvoiceAttachPanel.b64encode(params);
			String url = URL.encode(GWT.getModuleBaseURL() + "ms/download_attachment" 
					+ "/" + callback.getCurrentDomainName() 
					+ "/" + callback.getCurrentUser() 
					+ "/" +  params);
			
			FlowPanel anchorContainer = new FlowPanel();
			anchorContainer.setStyleName(AON.AON_CSS.aonPadding());
			anchorContainer.addStyleName(AON.AON_CSS.aonSimpleBorder());
			Anchor anchor = new Anchor("Descargar" ,url, "_blank");
			anchor.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			anchor.addStyleName(AON.AON_CSS.aonIconDownload());
			anchor.addStyleName(AON.AON_CSS.aonClickableLabel());
			anchorContainer.add(anchor);
			verticalPanel.add(anchorContainer);
			
			if ( ai.getAttach().getMimeType() != null && ai.getAttach().getMimeType().isPDF()) {
				verticalPanel.add(pdfViewer);
				pdfViewer.addStyleName(AON.AON_CSS.aonWidthAll());
				pdfViewer.addStyleName(AON.AON_CSS.aonHeightAll());
				pdfViewer.setDocument(url, 1.5);
			} else if ( ai.getAttach().getMimeType() != null && ai.getAttach().getMimeType().isImage()) {
				Image image = new Image( url );
				verticalPanel.add(image);
			} else {
				Label unknown = new Label("No se ha podido determinar un visor para este tipo de documento.");
				unknown.setStyleName(AON.AON_CSS.aonBlockCenter());
				unknown.addStyleName(AON.AON_CSS.aonMarginTop());
				unknown.addStyleName(AON.AON_CSS.aonBold());
				verticalPanel.add(unknown);			
			}
		} else {
			Label unknown = new Label("La factura no tiene documentos adjuntos.");
			unknown.setStyleName(AON.AON_CSS.aonBlockCenter());
			unknown.addStyleName(AON.AON_CSS.aonMarginTop());
			unknown.addStyleName(AON.AON_CSS.aonBold());
			verticalPanel.add(unknown);			
		}
	}

	private static native String b64encode(String a) /*-{
	  return window.btoa(a);
	}-*/;	
	
}
