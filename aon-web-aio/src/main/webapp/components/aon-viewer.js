import { AonElement } from './AonElement.js';
import { CONSTANT, EVENT, TAG, PDFJS_WORKER_URL, PDFJS_VIEWER_STYLESHEET_URL, CSS, PDFJS_PDF_URL } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';

export class AonViewer extends AonElement {

	_scale;
	AON_IMG_DIV;
	AON_TEXT_DIV;
	AON_VIEWER_DIV;
	AON_CANVAS_DIV;
	AON_CANVAS_IFRAME;
	PDF;
	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get type() {
		return this.getAttribute(CONSTANT.TYPE);
	}

	set type(type) {
		this.setAttribute(CONSTANT.TYPE, type);
	}

	get file() {
		return this.getAttribute(CONSTANT.FILE);
	}

	set file(file) {
		this.setAttribute(CONSTANT.FILE, file);
	}

	get width() {
		return this.getAttribute('width');
	}

	set width(width) {
		this.setAttribute('width', width);
	}
	
	setImgText

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();

		let iframeCanvas = this.createElement(TAG.IFRAME);
		iframeCanvas.id = this.AON_CANVAS_IFRAME;
		iframeCanvas.style.width = '100%';
		iframeCanvas.style.height = '100%';
		iframeCanvas.style.border = 'none';
		this.appendChild(iframeCanvas);
		
		iframeCanvas.onload = () => {
				let divCanvas = this.createIFrameElement(TAG.DIV);
				divCanvas.id = this.AON_CANVAS_DIV;
				divCanvas.style.width = '100%';
				this.getIFrameBody().appendChild(divCanvas);
				if (this.type && this.type.includes('pdf')) {
					this.printPdf();
				} else if (this.type && this.type.includes('image')) {
					this.printImage();
				} else if (this.type) {
					this.notSupport(this.type);
				}
    	}
		try {
			let divCanvas = this.createIFrameElement(TAG.DIV);
			divCanvas.id = this.AON_CANVAS_DIV;
			divCanvas.style.width = '100%';
			this.getIFrameBody().appendChild(divCanvas);
			if (this.type && this.type.includes('pdf')) {
				this.printPdf();
			} else if (this.type && this.type.includes('image')) {
				this.printImage();
			} else if (this.type) {
				this.notSupport(this.type);
			}
		} catch ( err ) {
			console.log(err);			
		}
		

		


		let div = this.createElement(TAG.DIV);
		div.id = this.AON_VIEWER_DIV;
		div.style.display = 'none';
		this.appendChild(div);

		this.addEventListener(EVENT.MOUSEOVER, () => {
			div.style.visibility = 'visible';
		});

		this.addEventListener(EVENT.MOUSELEAVE, () => {
			div.style.visibility = 'hidden';
		});

		if (!this.isMobile())
			this.buildButtons();
	}

	initialize(){
		this._scale = 1;
		this.AON_IMG_DIV  = "aonViewerImgDiv";
		this.AON_VIEWER_DIV  = "aonViewerButtonsDiv";
		this.AON_CANVAS_DIV  = "aonViewerCanvasDiv";	
		this.AON_CANVAS_IFRAME  = "aonViewerCanvasIFrame";	
	}
	removeButtons() {
		let div = this.getElement(this.AON_VIEWER_DIV);
		div.innerHTML = '';
	}

	removeButton(id) {
		let div = this.getElement(this.AON_VIEWER_DIV);
		div.removeChild(this.getElement(id));
	}

	buildButtons() {
		let div = this.getElement(this.AON_VIEWER_DIV);
		div.style.zIndex = "3";
		div.style.visibility = 'hidden';
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.position = "absolute";
		div.style.right = "2%";
		div.style.top = "1%";
		div.style.height = "80%";
		let mail = this.createElement(TAG.SPAN);
		mail.id = "aonViewerButtonsDivEmailSpan";
		let aibm = new AonIconButton();
		aibm.id = "aonViewerButtonsDivEmail";
		aibm.icon = "email";
		aibm.background = "#f1f1f1";
		aibm.addEventListener(EVENT.CLICK, () => {
			this.dispatchEvent(new CustomEvent(EVENT.SEND_MAIL));
		});
		mail.appendChild(aibm);
		div.appendChild(mail);

		if (this.type.includes('pdf')) {
			let print = this.createElement(TAG.SPAN);
			print.style.marginTop = "4px";
			let aibp = new AonIconButton();
			aibp.id = "aonViewerButtonsDivPrint";
			aibp.icon = "print";
			aibp.background = "#f1f1f1";
			aibp.addEventListener(EVENT.CLICK, () => this.printDocument());
			print.appendChild(aibp);
			div.appendChild(print);
		}

		let download = this.createElement(TAG.SPAN);
		download.style.marginTop =  "4px";
		let aibd = new AonIconButton();
		aibd.id = "aonViewerButtonsDivDownload";
		aibd.icon = "download";
		aibd.background = "#f1f1f1";
		aibd.addEventListener(EVENT.CLICK, () => open(this.file));
		download.appendChild(aibd)
		div.appendChild(download);


		if (this.type.includes('pdf')) {
			let ajustar = this.createElement(TAG.SPAN);
			ajustar.style.marginTop =  "auto";
			let aiba = new AonIconButton();
			aiba.id = "aonViewerButtonsDivAjustar";
			aiba.icon = "zoom_out_map";
			aiba.background = "#f1f1f1";
			aiba.addEventListener(EVENT.CLICK, () => {
				this.getIFrameDocument().querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
				this._scale = 1;
				this.printPdf(this._scale);
			});
			ajustar.appendChild(aiba);
			div.appendChild(ajustar);
	
			let zoomPlus = this.createElement(TAG.SPAN);
			zoomPlus.style.marginTop = "4px";
			let aibz = new AonIconButton();
			aibz.id = "aonViewerButtonsDivZoomPlus";
			aibz.icon = "zoom_in";
			aibz.background = "#f1f1f1";
			aibz.addEventListener(EVENT.CLICK, () => {
				this.getIFrameDocument().querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
				this._scale = this._scale - 0.25;
				this.printPdf(this._scale);
			});
			zoomPlus.appendChild(aibz)
			div.appendChild(zoomPlus);

			let zoomMinus = this.createElement(TAG.SPAN);
			let aibzm = new AonIconButton();
			aibzm.id = "aonViewerButtonsDivZoomMinus";
			aibzm.icon = "zoom_out";
			aibzm.background = "#f1f1f1";
			aibzm.addEventListener(EVENT.CLICK, () => {
				this.getIFrameDocument().querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
				this._scale = this._scale + 0.25;
				this.printPdf(this._scale);
			});
			zoomMinus.appendChild(aibzm);
			zoomMinus.style.marginTop = "4px";
			div.appendChild(zoomMinus);
		}
	}

	printImage() {
		let iframeCanvas = this.getElement(this.AON_CANVAS_IFRAME);
		if(iframeCanvas) {
			iframeCanvas.style.width = '0px';
			iframeCanvas.style.height = '0px';
		}

		let img = this.getElement(this.AON_IMG_DIV);
		if(!img) {
			img = this.createElement(TAG.IMG);
			img.id = this.AON_IMG_DIV;
			img.style.width = '100%';
			img.src = this.file;
			img.onerror = () =>{
				img.remove();
				this.notSupport(this.type);
			}
			this.appendChild(img);
			this.dispatchEvent(new CustomEvent(EVENT.PRINT_IMAGE));
		}
	}
	
	printImageTextLayer(textContent) {
		this.loadCSS(PDFJS_VIEWER_STYLESHEET_URL).then(() => {
			console.log(JSON.stringify(textContent));
			
			let img = this.getElement(this.AON_IMG_DIV);
			
			let page = textContent.pages[0];
			
			let textLayerDiv = this.createElement(TAG.DIV);
			textLayerDiv.className = CSS.PDFJS_TEXT_LAYER;
		    textLayerDiv.style.width = `${img.width}px`;
		    textLayerDiv.style.height = `${img.height}px`;
			textLayerDiv.style.top = `${img.offsetTop}px`;
			textLayerDiv.style.left = `${img.offsetLeft}px`;
			textLayerDiv.style.setProperty("--scale-factor", img.width / page.width );
			this.append(textLayerDiv);

			page.items.forEach(item => {
				console.log(JSON.stringify(item));
				let itemSpan = this.createElement(TAG.SPAN);
				itemSpan.innerText = item.str;
	
				itemSpan.style.top = `${item.top / page.height * 100.00}%`;
				itemSpan.style.left = `${item.left / page.width * 100.00}%`;
				itemSpan.style.width = `${item.width / page.width * 101.00}%`;
				itemSpan.style.height = `${item.height / page.height * 101.00}%`;
				/*
				itemSpan.style.top = `${item.top * screenPPI}px`;
				itemSpan.style.left = `${item.left * screenPPI}px`;
				itemSpan.style.width = `${item.width * screenPPI}px`;
				itemSpan.style.height = `${item.height * screenPPI}px`;
				*/			
				
				itemSpan.style.setProperty('overflow', `hidden`);
				itemSpan.style.setProperty('role', 'presentation');
				itemSpan.style.setProperty('font-family', 'sans-serif');
				itemSpan.style.setProperty('font-size', `calc(var(--scale-factor)*${item.height * 2}px)`);
	
				textLayerDiv.appendChild(itemSpan);
			});
		});		
		
	}

	notSupport(type, reason) {
		let div = this.createElement(TAG.DIV);
		div.style.backgroundColor = 'rgba(128, 128, 128, 0.1)';
		div.style.height = '100%';
		div.style.display = 'flex';
		this.appendChild(div);

		let div2 = this.createElement(TAG.DIV);
		div2.style.margin = 'auto';
		div2.appendChild(this.getTypeIcon(type));
		let div3 = this.createElement(TAG.DIV);
		div3.style.textAlign = "center";
		div3.innerHTML = reason || 'Vista previa no disponible';
		div2.appendChild(div3);
		div.appendChild(div2);
	}

	getTypeIcon(type) {
		let ai = new AonIcon();
		ai.size = "160";
		if (type.includes('pdf')) {
			ai.icon = "aon_pdf";
		} else if (type.includes('powerpoint') || type.includes('presentation')) {
			ai.icon = "aon_powerpoint";
			ai.color = "orange";
		} else if (type.includes('excel') || type.includes('spreadsheet')) {
			ai.icon = "aon_excel";
			ai.color = "green";
		} else if (type.includes('word') || type.includes('text')) {
			ai.icon = "aon_word";
			ai.color = "cornflowerblue";
		} else {
			ai.icon = "aon_file";
		}
		return ai;
	}
	
	// renderPdf(pdf) {
	// 	for(let page = 1; page <= pdf.numPages; page++) {
	// 		pdf.getPage(page).then(renderPage);
	// 	}
	// }
	
	printPdf(zoom) {

		this.getIFrameDocument().querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
		this.getIFrameDocument().querySelectorAll(`${TAG.DIV}.${CSS.PDFJS_TEXT_LAYER}`).forEach((item, i) => item.remove());

 		const div = this.getIFrameElement(this.AON_CANVAS_DIV);
		div.className = CSS.PDFJS_PDF_VIEWER; 
		//div.style.setProperty('--scale-factor', scale.toString());


		const width = this.getAttribute('width');
		
		this.wait4PdfJsLib().then(pdfjsLib => {
			
		    pdfjsLib.pageColorsBackground="#000";
		    pdfjsLib.pageColorsForeground="#FFF";
		    pdfjsLib.forcePageColors=true; 
    			
			pdfjsLib.GlobalWorkerOptions.workerSrc = PDFJS_WORKER_URL;
			
	
			//const loadingTask = pdfjsLib.getDocument({
			//	url: this.file
			//	,httpHeaders: {
			//		'Access-Control-Allow-Origin': '*',
			//		'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, PUT, DELETE, HEAD',
			//		'Access-Control-Allow-Headers': 'X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept',
			//		'Access-Control-Max-Age': '1728000'
			//	}
			//	,withCredentials: true
			// ))});
			const url = new URL(this.file, window.location.href);
			const loadingTask = pdfjsLib.getDocument({ url: url.toString() });
			
			loadingTask.promise.then( (pdf) =>  {
				this.PDF = pdf;
				console.log('PDF loaded');
				// Fetch the first page
				for (let pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++) {
					
					// Remove old canvas if exist. 
					let oldCanvas = this.getElement('canvas' + pageNumber);
					if ( oldCanvas ) { 
						oldCanvas.parentElement.removeChild(oldCanvas);
					}
					// Remove old textLayer if exist. 
					let oldTextlayerDiv = this.getElement('textLayerDiv' + pageNumber);
					if ( oldTextlayerDiv ) { 
						oldTextlayerDiv.parentElement.removeChild(oldTextlayerDiv);
					}
					
					const canvas = this.createIFrameElement(TAG.CANVAS);
					canvas.id = 'canvas' + pageNumber;
					canvas.style.border = '1px solid #ebebeb';
					// Append the canvas to the pdf container div
					div.appendChild(canvas);
	
					pdf.getPage(pageNumber).then((page) =>  {
						console.log('Page loaded');
	
						let scale = zoom || 1;
						let viewport = page.getViewport({ scale });
						if (width) {
							scale = width / viewport.width;
							viewport = page.getViewport({ scale });
						}
	 					
	 					div.style.setProperty("--scale-factor", viewport.scale);

						const context = canvas.getContext('2d');
						canvas.height = viewport.height;
						canvas.width = viewport.width;
						
						console.log(JSON.stringify(context));
						
						const canvasOffset = this.getIFrameOffset(canvas);
						
						let textLayerDiv = this.createIFrameElement(TAG.DIV);
						textLayerDiv.id = 'textLayerDiv' + pageNumber;
						textLayerDiv.className = CSS.PDFJS_TEXT_LAYER;
					    textLayerDiv.style.width = `${viewport.width}px`;
					    textLayerDiv.style.height = `${viewport.height}px`;
						textLayerDiv.style.top = `${canvasOffset.top - 8 * (zoom || 1) }px`;
						textLayerDiv.style.left =`${canvasOffset.left - 8 * (zoom || 1) }px`;

						div.appendChild(textLayerDiv);
						
						// Render PDF page into canvas context
						const renderContext = {
							canvasContext: context,
							viewport: viewport
						};
						const renderTask = page.render(renderContext);
						renderTask.promise
						.then( ()=> {
							console.log('Page rendered');
						})
						.catch( err => console.log('Error rendering page: ' + err ))
						;
						
						this.loadCSS(PDFJS_VIEWER_STYLESHEET_URL).then(() => {
							// clean viewer implicit styles.
							console.log('Clean viewer implicit styles');
							document.body.style.setProperty('background-color', 'transparent');
							
							page.getTextContent()
							.then((textContent) => {
								const textLayer = new pdfjsLib.TextLayer({
									viewport : viewport ,
									container : textLayerDiv,
									textContentSource : textContent
								});
								textLayer.render();

								let text = textContent.items.map( item => item.str).join();
								this.dispatchEvent(new CustomEvent(EVENT.PRINT_PDF_PAGE, {
									detail:{
										text: text,
										page: pageNumber
									}
								}));
							})
							.catch( err => console.log('Error rendering text layer : ' + err ))
							;
						});
			
					});
				}
			},  (reason)=> {			// PDF loading error
				console.log(reason);
			});
		}).catch(err=>{
			console.log(err);
		});
	}

	printPdfTextLayer(pageIndex, textContent) {
		
		console.log(JSON.stringify(textContent));
		
		let screenPPI = this.getScreenPPI() * 0.80;
		let page = textContent.pages[pageIndex-1];
		
		let textLayerDiv = this.getElement(`textLayerDiv${pageIndex}`);
		
		page.items.forEach(item => {
			let itemSpan = this.createElement(TAG.SPAN);
			itemSpan.innerText = item.str;

			itemSpan.style.top = `${item.top / page.height * 100.00}%`;
			itemSpan.style.left = `${item.left / page.width * 100.00}%`;
			itemSpan.style.width = `${item.width / page.width * 100.00}%`;
			itemSpan.style.height = `${item.height / page.height * 100.00}%`;
			/*
			itemSpan.style.top = `${item.top * screenPPI}px`;
			itemSpan.style.left = `${item.left * screenPPI}px`;
			itemSpan.style.width = `${item.width * screenPPI}px`;
			itemSpan.style.height = `${item.height * screenPPI}px`;
			*/			
			
			itemSpan.style.setProperty('role', 'presentation');
			itemSpan.style.setProperty('font-family', 'sans-serif');
			console.log(`calc(var(--scale-factor)*${item.height * screenPPI}px)`);
			itemSpan.style.setProperty('font-size', `calc(var(--scale-factor)*${item.height * screenPPI}px)`);

			textLayerDiv.appendChild(itemSpan);
		});
	}
	
	getIFrameHead(){
		const iframe = this.getElement(this.AON_CANVAS_IFRAME);
		return iframe.contentDocument.head;
	}

	getIFrameBody(){
		const iframe = this.getElement(this.AON_CANVAS_IFRAME);
		return iframe.contentDocument.body;
	}

	getIFrameDocument(){
		const iframe = this.getElement(this.AON_CANVAS_IFRAME);
		return iframe.contentDocument;
	}
	
	getIFrameElement(id) {
		const iframe = this.getElement(this.AON_CANVAS_IFRAME);
		return iframe.contentDocument.getElementById(id);
	}

	createIFrameElement(tag) {
		const iframe = this.getElement(this.AON_CANVAS_IFRAME);
		return iframe.contentDocument.createElement(tag);
	}

	createIframe(){
		const div = this.getElement(this.AON_CANVAS_DIV);
		div.innerHTML = "";
		const divButton = this.getElement(this.AON_VIEWER_DIV);
		if(divButton) divButton.remove();

		let iframe = this.createElement(TAG.IFRAME);
		iframe.frameborder = "0";
		iframe.style.width = "100%";
		iframe.style.height = "100%";
		iframe.src = this.file+"#zoom=FitH";
		div.appendChild(iframe);
	}

	printDocument(){
		if(this.PDF && this.type){
			this.PDF.getData().then(data=>{
				const pdfUrl = URL.createObjectURL(new Blob([data], {type: this.type }));
				const iframeId = this.id+"iframeTmp";
				let iframe = this.querySelector('#'+iframeId) || this.createElement('iframe'); //load content in an iframe to print later
				iframe.id = iframeId;
				iframe.src = pdfUrl;
				iframe.style.display = 'none';
				this.appendChild(iframe);
				iframe.onload = () =>{
				  setTimeout(() =>{
					iframe.focus();
					iframe.contentWindow.print();
				  }, 1);
				};
			});
		}
	}

	onScale(element) {
		let scale = 1.0;
		element.addEventListener('wheel', (ev) => {
		  ev.preventDefault();
		  let isPinch = ev.deltaY < 50;
		  if (isPinch) {
			// This is a pinch on a trackpad
			let factor = 1 - 0.01 * ev.deltaY;
			scale *= factor;
		  } else {
			// This is a mouse wheel
			let strength = 1.4;
			let factor = ev.deltaY < 0 ? strength : 1.0 / strength;
			scale *= factor;
		  }
		  element.style.transform         = `scale(${scale})`;
		  element.style["-moz-transform"] = `scale(${scale})`;
		//   console.log(typeof  element.style.width, element.style.width);
		//   const width  = parseFloat(element.style.width.replace("px", ""));
		//   const height = parseFloat(element.style.height.replace("px", ""));
		//   element.style.width  = width*scale;
		//   element.style.height = height*scale;
		});
	}


	wait4PdfJsLib(){
		return new Promise((resolve,reject)=>{
			let i = 0;
			const timeout = 100;// 10 seg
			let interval = setInterval(()=> {
				i++;
				let element = this.getIFrameDocument().querySelector(`script[src='${PDFJS_PDF_URL}']` );
				let { pdfjsLib } = this.getElement(this.AON_CANVAS_IFRAME).contentWindow;
				if (element && pdfjsLib) {
					clearInterval(interval);
					resolve(pdfjsLib);
				} else if (!element) { // CREATE ELEMENT
					let script = this.createIFrameElement("script");
					script.type = "module";
					script.src = PDFJS_PDF_URL;
					this.getIFrameHead().appendChild(script);
				}  else if(i >= timeout){
					clearInterval(interval);
					reject("Element empty");
				}
			}, 100); // check every 100ms
		});
	}
	
	loadCSS( href ) {
	    return new Promise((resolve, reject)=>{
			if ( this.getIFrameDocument().querySelector(`link[href='${href}']`)){
				resolve();
			} else {
		        const link =this.createIFrameElement(TAG.LINK);
		        link.href = href;
		        link.rel  = 'stylesheet';
		        this.getIFrameHead().appendChild(link);
		        link.onload = function() { 
		            resolve(); 
		            console.log( 'CSS has loaded!' ); 
		        };
	        }
	    });
	}
	
	getScreenPPI(){
		let ppiDiv = document.createElement(TAG.DIV);
		ppiDiv.style.width = "1in";
		ppiDiv.style.padding = "0px";
		ppiDiv.style.padding = "hidden";
		
		this.appendChild(ppiDiv);
		let screenPPI = ppiDiv.offsetWidth;  
		this.removeChild(ppiDiv);		
		
		return screenPPI;
	}
	
	getOffset( el ) {
	    var _x = 0;
	    var _y = 0;
	    while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {
	        _x += el.offsetLeft - el.scrollLeft;
	        _y += el.offsetTop - el.scrollTop;
	        el = el.offsetParent;
	    }
	    return { top: _y, left: _x };
	}	
	
	getIFrameOffset(el) {
		const iframe = this.getElement(this.AON_CANVAS_IFRAME)
		const rect = el.getBoundingClientRect();
		return {
		  left: rect.left + iframe.contentWindow.scrollX,
		  top: rect.top + iframe.contentWindow.scrollY
		};
	}	

}
if (!window.customElements.get(TAG.AON_VIEWER)) {
	window.customElements.define(TAG.AON_VIEWER, AonViewer);
}
