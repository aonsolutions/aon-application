package net.aonsolutions.gwt.pdfjs.client;

import java.util.logging.Logger;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.user.client.ui.Frame;

public class FullViewer extends Frame {
	
	private static final Logger LOGGER = Logger.getLogger(FullViewer.class.getName());
	private static final String URL_TO_AVOID_CORS = GWT.getModuleBaseURL() + "ms/AonPDFBridgeServlet?URL=";
	private static final String VIEWER_PATH = GWT.getModuleName() + "/pdfjs/web/FullViewer.html";
	
	public FullViewer() {
		this( null );
	}
	
	public FullViewer(String url) {
		setWidth("100%");
		setHeight("100%");
		String fileURL = null;
		if (url != null) {
			if (isURLocal(url)) {
				fileURL = url;
			} else {
				fileURL = URL_TO_AVOID_CORS + url;
			}
			fileURL = VIEWER_PATH + "?file=" + encodeURIComponent(fileURL);
			LOGGER.info("Attemp to load PDF file [" + fileURL + "]");
			setUrl(fileURL);
		} else {
			setUrl(VIEWER_PATH + "?file=");
		}
	}

	private boolean isURLocal(String url) {
		return AonStringUtils.contains(url,"ms/download_attachment");
	}

	public void open(String dataURI) {
		LOGGER.info("Attemp to native OPEN");
		nativeOpen( this.getElement().cast() ,dataURI);
	}

	
	private native String encodeURIComponent(String URI) /*-{
		return encodeURIComponent(URI);
	}-*/;
	
	private native void nativeOpen(FrameElement el, String dataURI) /*-{
		if (el.contentWindow.PDFViewerApplication.pdfDocument) {
			console.log("Destroing previous document");
			el.contentWindow.PDFViewerApplication.pdfDocument.destroy();
		}  
    	el.contentWindow.PDFViewerApplication.open(dataURI);
	}-*/;
}
