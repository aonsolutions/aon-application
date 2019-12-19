package net.aonsolutions.gwt.pdfjs.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.user.client.ui.Frame;

public class FullViewer extends Frame {
	
	private static  final Logger LOGGER = Logger.getLogger(FullViewer.class.getName());
	
	public FullViewer() {
		this( null );
	}
	
	public FullViewer(String url) {
		setWidth("100%");
		setHeight("100%");
		String viewerPath = GWT.getModuleName() + "/pdfjs/web/viewer.html"; 
		if (url != null) {
			viewerPath = viewerPath + "?file=" + encodeURIComponent(url);
			LOGGER.info("Attemp to load PDF file [" + viewerPath + "]");
		}
		setUrl(viewerPath);
	}

	public void open(String dataURI) {
		nativeOpen( this.getElement().cast() ,dataURI);
	}

	private native String encodeURIComponent(String URI) /*-{
		return encodeURIComponent(URI);
	}-*/;
	
	private native void nativeOpen(FrameElement el, String dataURI) /*-{
		el.contentWindow.PDFViewerApplication.open(dataURI);
	}-*/;
}
