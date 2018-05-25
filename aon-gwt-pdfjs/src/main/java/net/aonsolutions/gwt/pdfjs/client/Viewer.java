package net.aonsolutions.gwt.pdfjs.client;


import java.util.function.Supplier;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Viewer extends ResizeComposite {
	
	interface Style extends CssResource {
		@ClassName("page")
		String page();
		@ClassName("page-shadow")
		String pageShadow();
		
	}
	
	interface ScaleCallback {
		void then();
	}

	interface PDFJSCallback {
		void onSuccess(JavaScriptObject obj);
	}


	interface Binder extends UiBinder<Widget, Viewer> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	
	static {
		ScriptInjector.fromUrl(GWT.getModuleName() + "/pdfjs/build/pdf.js")
		.setCallback( new Callback<Void, Exception>() {
			
			@Override
			public void onSuccess(Void result) {
			}
			
			@Override
			public void onFailure(Exception reason) {
			}
		}).inject();
	}
	
	@UiField
	Style style;

	@UiField
	Frame printFrame;
	@UiField
	Anchor downloadAnchor;

	@UiField
	DeckPanel deckPanel;
	@UiField
	VerticalPanel verticalPanel;
	
	
	private JavaScriptObject pdf;
	
	public Viewer() {
		initWidget(binder.createAndBindUi(this));
		deckPanel.showWidget(deckPanel.getWidgetIndex(verticalPanel));
	}
	
	public void print() {
		print(printFrame.getElement(), pdf);
	}
	
	public void download(String fileName) {
		download(pdf, fileName, downloadAnchor.getElement());
	}

	public void scale(double scale) {
		clear();
		scale(this::createPreviewCanvas, pdf, scale, () -> {});
	}
	
	public void setDocument(String url, double scale) {
		clear();
		setDocument(this::createPreviewCanvas, url, scale, this::setPdf);
	}
	
	// ---------------------------------------------------------------- Private
	
	private void clear () {
		verticalPanel.clear();
	}

	private Element createPrintCanvas() {
		Canvas canvas = Canvas.createIfSupported();
		canvas.addStyleName(style.page());
		canvas.addStyleName(style.pageShadow());

		FrameElement el = printFrame.getElement().cast();
		el.getContentDocument().getBody().appendChild(canvas.getElement());
		
		return canvas.getElement();
	}
	
	private Element createPreviewCanvas() {
		Canvas canvas = Canvas.createIfSupported();
		canvas.addStyleName(style.page());
		canvas.addStyleName(style.pageShadow());
		verticalPanel.add(canvas);
		return canvas.getElement();
	}

	private void setPdf(JavaScriptObject pdf) {
		this.pdf = pdf;
	}
	
	// ----------------------------------------------------------------- Native

	private static native void setDocument(Element canvas)/*-{
		// atob() is used to convert base64 encoded PDF to binary-like data.
		// (See also https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/
		// Base64_encoding_and_decoding.)
		var pdfData = atob(
		  'JVBERi0xLjcKCjEgMCBvYmogICUgZW50cnkgcG9pbnQKPDwKICAvVHlwZSAvQ2F0YWxvZwog' +
		  'IC9QYWdlcyAyIDAgUgo+PgplbmRvYmoKCjIgMCBvYmoKPDwKICAvVHlwZSAvUGFnZXMKICAv' +
		  'TWVkaWFCb3ggWyAwIDAgMjAwIDIwMCBdCiAgL0NvdW50IDEKICAvS2lkcyBbIDMgMCBSIF0K' +
		  'Pj4KZW5kb2JqCgozIDAgb2JqCjw8CiAgL1R5cGUgL1BhZ2UKICAvUGFyZW50IDIgMCBSCiAg' +
		  'L1Jlc291cmNlcyA8PAogICAgL0ZvbnQgPDwKICAgICAgL0YxIDQgMCBSIAogICAgPj4KICA+' +
		  'PgogIC9Db250ZW50cyA1IDAgUgo+PgplbmRvYmoKCjQgMCBvYmoKPDwKICAvVHlwZSAvRm9u' +
		  'dAogIC9TdWJ0eXBlIC9UeXBlMQogIC9CYXNlRm9udCAvVGltZXMtUm9tYW4KPj4KZW5kb2Jq' +
		  'Cgo1IDAgb2JqICAlIHBhZ2UgY29udGVudAo8PAogIC9MZW5ndGggNDQKPj4Kc3RyZWFtCkJU' +
		  'CjcwIDUwIFRECi9GMSAxMiBUZgooSGVsbG8sIHdvcmxkISkgVGoKRVQKZW5kc3RyZWFtCmVu' +
		  'ZG9iagoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMDEwIDAwMDAwIG4g' +
		  'CjAwMDAwMDAwNzkgMDAwMDAgbiAKMDAwMDAwMDE3MyAwMDAwMCBuIAowMDAwMDAwMzAxIDAw' +
		  'MDAwIG4gCjAwMDAwMDAzODAgMDAwMDAgbiAKdHJhaWxlcgo8PAogIC9TaXplIDYKICAvUm9v' +
		  'dCAxIDAgUgo+PgpzdGFydHhyZWYKNDkyCiUlRU9G');
		
		// Loaded via <script> tag, create shortcut to access PDF.js exports.
		var pdfjsLib = window['pdfjs-dist/build/pdf'];
		
		// The workerSrc property shall be specified.
		//pdfjsLib.GlobalWorkerOptions.workerSrc = moduleName + '/pdfjs/build/pdf.worker.js';
		
		// Using DocumentInitParameters object to load binary data.
		var loadingTask = pdfjsLib.getDocument({data: pdfData});
		loadingTask.promise.then(function(pdf) {
		  console.log('PDF loaded');
		  
		  // Fetch the first page
		  var pageNumber = 1;
		  pdf.getPage(pageNumber).then(function(page) {
		    console.log('Page loaded');
		    
		    var scale = 1.5;
		    var viewport = page.getViewport(scale);
		
		    // Prepare canvas using PDF page dimensions
		    //var canvas = document.getElementById('the-canvas');
		    var context = canvas.getContext('2d');
		    canvas.height = viewport.height;
		    canvas.width = viewport.width;
		
		    // Render PDF page into canvas context
		    var renderContext = {
		      canvasContext: context,
		      viewport: viewport
		    };
		    var renderTask = page.render(renderContext);
		    renderTask.then(function () {
		      console.log('Page rendered');
		    });
		  });
		}, function (reason) {
		  // PDF loading error
		  console.error(reason);
		});		
	}-*/;

	private static native void setDocument(Supplier<Element> supplier, String url, double scale, PDFJSCallback callback)/*-{
		// If absolute URL from the remote server is provided, configure the CORS
		// header on that server.
		//var url = '//cdn.mozilla.net/pdfjs/helloworld.pdf';
		
		// Loaded via <script> tag, create shortcut to access PDF.js exports.
		var pdfjsLib = window['pdfjs-dist/build/pdf'];
		
		// The workerSrc property shall be specified.
		//pdfjsLib.GlobalWorkerOptions.workerSrc = '//mozilla.github.io/pdf.js/build/pdf.worker.js';
		
		// Asynchronous download of PDF
		var loadingTask = pdfjsLib.getDocument(url);
		loadingTask.promise.then(function(pdf) {
		  console.log('PDF loaded');
		  
		  var pageNumber ;
		  // Fetch the first page
		  //var pageNumber = 1;

		  for ( pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++) {
			  pdf.getPage(pageNumber).then(function(page) {
			    console.log('Page loaded');
			    
			    //var scale = 1.00;
			    var viewport = page.getViewport(scale);
			
			    // Prepare canvas using PDF page dimensions
			    var canvas = supplier.@java.util.function.Supplier::get()(); //document.getElementById('the-canvas');
			    var context = canvas.getContext('2d');
			    canvas.height = viewport.height;
			    canvas.width = viewport.width;
			
			    // Render PDF page into canvas context
			    var renderContext = {
			      canvasContext: context,
			      viewport: viewport
			    };
			    var renderTask = page.render(renderContext);
			    renderTask.then(function () {
			      console.log('Page rendered');
			    });
			  });
		  }
		  callback.@net.aonsolutions.gwt.pdfjs.client.Viewer.PDFJSCallback::onSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(pdf); 
		}, function (reason) {
		  // PDF loading error
		  //console.error(reason);
		});
	}-*/;

	private static native void scale(Supplier<Element> supplier, JavaScriptObject pdf, double scale, ScaleCallback callback)/*-{

		var pageNumber ;
	
		for ( pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++) {
			pdf.getPage(pageNumber).then(function(page) {
			    console.log('Page loaded');
		    
			    var viewport = page.getViewport(scale);
			
			    // Prepare canvas using PDF page dimensions
			    var canvas = supplier.@java.util.function.Supplier::get()(); //document.getElementById('the-canvas');
			    var context = canvas.getContext('2d');
			    canvas.height = viewport.height;
			    canvas.width = viewport.width;
		
			    // Render PDF page into canvas context
			    var renderContext = {
			      canvasContext: context,
			      viewport: viewport
			    };
			    var renderTask = page.render(renderContext);
			    renderTask.then(function () {
			      console.log('Page rendered');
				  callback.@net.aonsolutions.gwt.pdfjs.client.Viewer.ScaleCallback::then()(); 
			    });
			});
		}
	}-*/;
	
	private static native void print(Element frame, JavaScriptObject pdf)/*-{

		// The size of the canvas in pixels for printing.
		var PRINT_RESOLUTION = 150;
		var PRINT_UNITS = PRINT_RESOLUTION / 72.0;

		var frameWindow = frame.contentWindow;
		var frameDocument = frame.contentDocument;

		var pageNumber ;
		for ( pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++) {
			pdf.getPage(pageNumber).then(function(page) {
			    console.log('Page loaded');
		    
			    var viewport = page.getViewport(1);
			
			    var canvas = document.createElement('canvas');
			    frameDocument.body.appendChild(canvas);

			    canvas.width = Math.floor(viewport.width * PRINT_UNITS);
			    canvas.height = Math.floor(viewport.height * PRINT_UNITS);
			    
			    var context = canvas.getContext('2d');
			    context.save();
			    context.fillStyle = 'rgb(255, 255, 255)';
			    context.fillRect(0, 0, canvas.width, canvas.height);
			    context.restore();

			    // Render PDF page into canvas context
			    var renderContext = {
			      intent: 'print',
			      viewport: viewport,
			      canvasContext: context,
			      transform : [PRINT_UNITS, 0, 0, PRINT_UNITS, 0, 0]
			    };
			    var renderTask = page.render(renderContext);
			    renderTask.then(function () {
			      console.log('Page rendered');
				  frameWindow.focus();
				  frameWindow.print();		
			    });
			});
		}
	}-*/;

	private static native void download(JavaScriptObject pdf, String filename, Element a) /*-{
		// Loaded via <script> tag, create shortcut to access PDF.js exports.
		var pdfjsLib = window['pdfjs-dist/build/pdf'];

	    pdf.getData().then(function (data) {
			var blob = (0, pdfjsLib.createBlob)(data, 'application/pdf');
			var blobUrl = URL.createObjectURL(blob);
	      
			a.href = blobUrl;
			a.target = '_parent';
			if ('download' in a) {
			  a.download = filename;
			}
			a.click();
		  
	    });
	    //.catch(downloadByUrl);
	}-*/;

	
}
