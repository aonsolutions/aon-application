package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonScalableImage;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
			String attachURL = null;
			MimeType mimeType = null; 
			if (ai.getAttach() != null) {
				attachURL = ai.getAttach().getAttachURL();
				mimeType = ai.getAttach().getMimeType();
			}

			InvoiceDoc d = callback.getInvoice().getInvoice().getDoc().orElse(null);
			String url = d == null ? attachURL : d.getUrl();
			mimeType = d == null ? mimeType : d.getMimeType();
			
			if (d != null && AonStringUtils.isBlank( url )) {
				String params = "domain="+ callback.getOccam().getDomain() 
						+ "&id=" +  d.getId() 
						+ "&attach_type=invoice";
				params = InvoiceAttachPanel.b64encode(params);
				url = URL.encode(GWT.getModuleBaseURL() + "ms/download_attachment" 
						+ "/" + callback.getOccam().getDomainName() 
						+ "/" + callback.getOccam().getUser() 
						+ "/" +  params);
			}
			
			if ( mimeType != null && mimeType.isPDF()) {
				this.setWidget(new FullViewer(url,ViewerDefaultScale.PAGE_WIDTH));
			} else if ( mimeType != null && mimeType.isImage()) {
				AonScalableImage scalableImage = new AonScalableImage();
				InvoiceAttachPanel.this.setWidget(scalableImage);
				final String finalURL = url;
				Scheduler.get().scheduleDeferred(() -> scalableImage.setImage( finalURL ));
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
