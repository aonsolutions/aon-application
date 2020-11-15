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
	
	public static enum ViewerDefaultScale {
		PAGE_WIDTH("page-width"), 
		PAGE_HEIGHT("page-height"), 
		PAGE_FIT("page-fit"), 
		AUTO("auto")
		;
		private String value;
		private ViewerDefaultScale(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}
	
	public FullViewer() {
		this( null, ViewerDefaultScale.AUTO);
	}
	public FullViewer(ViewerDefaultScale scale) {
		this( null, scale);
	}
	public FullViewer(String url) {
		this( url, ViewerDefaultScale.AUTO);
	}
	public FullViewer(String url, ViewerDefaultScale scale) {
		if (scale == null) {
			scale = ViewerDefaultScale.AUTO;
		}
		setWidth("100%");
		setHeight("100%");
		String fileURL = null;
		if (url != null) {
			if (isURLocal(url)) {
				fileURL = url;
			} else {
				fileURL = URL_TO_AVOID_CORS + url;
			}
			fileURL = getViewerPath(scale) + "file=" + encodeURIComponent(fileURL);
			LOGGER.info("Attemp to load PDF file [" + fileURL + "]");
			setUrl(fileURL);
		} else {
			setUrl(getViewerPath(scale) + "file=");
		}
	}
	private String getViewerPath(ViewerDefaultScale scale) {
		return VIEWER_PATH + "?";// + "#" + scale.getValue() + "?";
	}
	private boolean isURLocal(String url) {
		return AonStringUtils.contains(url,"ms/download_attachment")
			|| AonStringUtils.contains(url,"ms/download_rawdoc");
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
