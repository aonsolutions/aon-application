import { AonElement } from './AonElement.js';
import { CONSTANT, EVENT, TAG, URL_PDF_VIEWER } from '../environments/environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';

export class AonViewer extends AonElement {

	_scale;
	AON_VIEWER_DIV;
	AON_CANVAS_DIV;
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

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
		let divCanvas = this.createElement(TAG.DIV);
		divCanvas.id = this.AON_CANVAS_DIV;
		divCanvas.style.width = '100%';
		this.appendChild(divCanvas);

		if (this.type && this.type.includes('pdf')) {
			this.printPdf();
		} else if (this.type && this.type.includes('image')) {
			this.printImage();
		} else if (this.type) {
			this.notSupport(this.type);
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
		this.AON_VIEWER_DIV  = "aonViewerButtonsDiv";
		this.AON_CANVAS_DIV  = "aonViewerCanvasDiv";	
	}
	removeButtons() {
		let div = this.getElement(this.AON_VIEWER_DIV);
		div.innerHTML = '';
	}

	buildButtons() {
		let div = this.getElement(this.AON_VIEWER_DIV);
		div.style.visibility = 'hidden';
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.position = "absolute";
		div.style.right = "2%";
		div.style.top = "1%";
		div.style.height = "80%";
		let mail = this.createElement(TAG.SPAN);
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
				document.querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
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
				document.querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
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
				document.querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
				this._scale = this._scale + 0.25;
				this.printPdf(this._scale);
			});
			zoomMinus.appendChild(aibzm);
			zoomMinus.style.marginTop = "4px";
			div.appendChild(zoomMinus);
		}
	}

	printImage() {
		let img = this.createElement(TAG.IMG);
		img.style.width = '100%';
		img.src = this.file;
		img.onerror = () =>{
			img.remove();
			this.notSupport(this.type);
		}
		this.appendChild(img);
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

	printPdf(scalation) {
		document.querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());

		const div = this.getElement(this.AON_CANVAS_DIV);
		const width = this.getAttribute('width');

		this.waitLib().then(pdfjsLib=>{
			// pdfjsLib.GlobalWorkerOptions.workerSrc = '//mozilla.github.io/pdf.js/build/pdf.worker.js';
			pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://sig.aonsolutions.org/html/build/pdf.worker.js';
			// Asynchronous download of PDF
			//		var url = 'https://raw.githubusercontent.com/mozilla/pdf.js/ba2edeae/examples/learning/helloworld.pdf';
			// this.file = "https://mozilla.github.io/pdf.js/web/compressed.tracemonkey-pldi-09.pdf";
	
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
	
			const loadingTask = pdfjsLib.getDocument(this.file);
			
			loadingTask.promise.then( (pdf) =>  {
				this.PDF = pdf;
				console.log('PDF loaded');
				// Fetch the first page
				// let pageNumber = 1;
				for (let pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++) {
					let c = this.getElement('canvas' + pageNumber);
					if(c) c.parentElement.removeChild(c);
					const canvas = this.createElement(TAG.CANVAS);
					canvas.id = 'canvas' + pageNumber;
					canvas.style.border = '1px solid #ebebeb';
					div.appendChild(canvas);
	
					pdf.getPage(pageNumber).then((page) =>  {
						console.log('Page loaded');
	
						let scale = scalation || 1;
						let viewport = page.getViewport({ scale });
						if (width) {
							scale = width / viewport.width;
							viewport = page.getViewport({ scale });
						}
	
						// Prepare canvas using PDF page dimensions
						//var canvas = document.getElementById('the-canvas');
						// const canvasPage = this.getElement('canvas' + pageNumber) || this.createElement(TAG.CANVAS);
						// canvasPage.id = 'canvas' + pageNumber;
	
						const context = canvas.getContext('2d');
						canvas.height = viewport.height;
						canvas.width = viewport.width;
	
						// Render PDF page into canvas context
						const renderContext = {
							canvasContext: context,
							viewport: viewport
						};
						const renderTask = page.render(renderContext);
						renderTask.promise.then( ()=> {
							console.log('Page rendered');
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

	waitLib(){
		return new Promise((resolve,reject)=>{
			const timeout = 100;// 10 seg
			let i = 0;
			let pdfjsLib = undefined;
			let element = undefined;
			let interval = setInterval(()=> {
				i++;
				element = this.querySelector(`script[src='${URL_PDF_VIEWER}']` );
				pdfjsLib = window['pdfjs-dist/build/pdf'];
				if (element && pdfjsLib) {
					clearInterval(interval);
					resolve(pdfjsLib);
				} else if (!element) { // CREATE ELEMENT
					let script = document.createElement("script");
					script.src = URL_PDF_VIEWER;
					this.appendChild(script);
				}  else if(i >= timeout){
					clearInterval(interval);
					reject("Element empty");
				}
			}, 100); // check every 100ms
		});
	}
}
if (!window.customElements.get(TAG.AON_VIEWER)) {
	window.customElements.define(TAG.AON_VIEWER, AonViewer);
}
