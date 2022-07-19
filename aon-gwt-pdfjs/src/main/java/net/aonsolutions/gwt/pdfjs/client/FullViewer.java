package net.aonsolutions.gwt.pdfjs.client;

import java.util.logging.Logger;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;

public class FullViewer extends Frame {
	
	private static final Logger LOGGER = Logger.getLogger(FullViewer.class.getName());
	private static final String URL_TO_AVOID_CORS = GWT.getModuleBaseURL() + "ms/AonPDFBridgeServlet?URL=";
	private static final String VIEWER_PATH = GWT.getModuleName() + "/pdfjs/web/viewer.html";
	
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
	
	private String dataURI;

	
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
		setWidth("98%");
		setHeight("100%");
		
		getElement().getStyle().setProperty("margin-left", "auto");;	
		getElement().getStyle().setProperty("margin-right", "auto");;
		getElement().getStyle().setBorderWidth(0, Unit.PX);

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
	
	public String getDataURI() {
		return dataURI;
	}

	public void open(String dataURI) {
		this.dataURI = dataURI;
		LOGGER.info("Attemp to native OPEN");
		nativeOpen( this.getElement().cast() ,dataURI);
	}

	public void getData(CallbackData callbackData) {
		getDataWait(this.getElement().cast(), callbackData);
	}
	
	private native void getDataWait(FrameElement el, CallbackData callbackData) /*-{
			el.contentWindow.PDFViewerApplication.pdfDocument
			.saveDocument()
			.then( function(buffer){
		  		var binary = '';
			    var bytes = new Uint8Array( buffer );
			    var len = bytes.byteLength;
			    for (var i = 0; i < len; i++) {
			        binary += String.fromCharCode( bytes[ i ] );
			    }
			    var base64 = window.btoa( binary );
				callbackData.@net.aonsolutions.gwt.pdfjs.client.CallbackData::getData(Ljava/lang/String;)(base64);
			});
	}-*/;


	private native byte[] getData(FrameElement el) /*-{
		el.contentWindow.PDFViewerApplication.pdfDocument.getData()
		.then(function (data) {
			console.log("Inner getData : " + data);
			return data;
		});
	}-*/;
	
	private native String encodeURIComponent(String URI) /*-{
		return encodeURIComponent(URI);
	}-*/;
	
	private native void nativeOpen(FrameElement el, String dataURI) /*-{
		if (el.contentWindow.PDFViewerApplication && el.contentWindow.PDFViewerApplication.pdfDocument) {
			console.log("Destroing previous document");
			el.contentWindow.PDFViewerApplication.pdfDocument.destroy();
		}  

		if (el.contentWindow.PDFViewerApplication ) {
    		el.contentWindow.PDFViewerApplication.open(dataURI);
		} else {
			function openDataURI(e) {
				switch(el.contentDocument.readyState) {
					case "loading":
					    console.log("The document is still loading.");
					    break;
					case "interactive":
					    console.log("The document has finished loading. We can now access the DOM elements.");
					    break;
					case "complete":
					    console.log("The page is fully loaded.");
					    el.contentWindow.PDFViewerApplication.open(dataURI)
					    break;
    			}
			} 
			el.contentDocument.addEventListener('readystatechange', openDataURI);
		}
		
	}-*/;
	
	private native void download(JavaScriptObject pdf, String filename, Element a) /*-{
	// Loaded via <script> tag, create shortcut to access PDF.js exports.
	var pdfjsLib = window['pdfjs-dist/build/pdf'];

    pdf.getData().then(function (data) {
		var blob = (0, pdfjsLib.createBlob)(data, 'application/pdf');
		var blobUrl = URL.createObjectURL(blob);
      
		a.href = blobUrl;
		if ('download' in a) {
		  a.download = filename;
		}
		a.click();
	  
    });
    //.catch(downloadByUrl);
}-*/;
	
//			a.target = '_parent';
}
