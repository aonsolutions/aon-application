package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonScalableImage;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;
import net.aonsolutions.gwt.pdfjs.client.FullViewer.ViewerDefaultScale;

public class InvoiceAttachPanel extends SimpleLayoutPanel {

	private static final Logger LOGGER = Logger.getLogger(InvoiceAttachPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	public InvoiceAttachPanel( IInvoicePanelCallback callback ) {
		setWidth("100%");
		setHeight("100%");
		setStyleName(AON.CSS.aonTextCenter());
		
		AccountingInvoice ai = callback.getInvoice();
		if (ai != null && ai.isDocumentAttached() ) {
			String url = null;
			if (AonStringUtils.isNotBlank( ai.getAttach().getAttachURL())) {
				url = ai.getAttach().getAttachURL();
			} else {
				String params = "domain="+ callback.getCurrentDomainId() 
				+ "&id=" +  ai.getAttach().getId() 
				+ "&attach_type=invoice";
				params = InvoiceAttachPanel.b64encode(params);
				url = URL.encode(GWT.getModuleBaseURL() + "ms/download_attachment" 
						+ "/" + callback.getCurrentDomainName() 
						+ "/" + callback.getCurrentUser() 
						+ "/" +  params);
			}
			if ( ai.getAttach().getMimeType() != null && ai.getAttach().getMimeType().isPDF()) {
				this.setWidget(new FullViewer(url,ViewerDefaultScale.PAGE_WIDTH));
			} else if ( ai.getAttach().getMimeType() != null && ai.getAttach().getMimeType().isImage()) {
				AonScalableImage scalableImage = new AonScalableImage();
				InvoiceAttachPanel.this.setWidget(scalableImage);
				final String finalURL = url;
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						scalableImage.setImage( finalURL );
				}});
			} else {
				Label unknown = new Label("No se ha podido determinar un visor para este tipo de documento.");
				unknown.setStyleName(AON.CSS.aonBlockCenter());
				unknown.addStyleName(AON.CSS.aonMarginTop());
				unknown.addStyleName(AON.CSS.aonBold());
				this.setWidget(unknown);
			}
		} else {
			Label unknown = new Label("La factura no tiene documentos adjuntos.");
			unknown.setStyleName(AON.CSS.aonBlockCenter());
			unknown.addStyleName(AON.CSS.aonMarginTop());
			unknown.addStyleName(AON.CSS.aonBold());
			this.setWidget(unknown);
		}
	}

	private static native String b64encode(String a) /*-{
	  return window.btoa(a);
	}-*/;	

}
