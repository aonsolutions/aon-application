
import { CONSTANT, EVENT, TAG } from './environments.js';
import { AonIcon } from './aon-icon.js';
import { AonIconButton } from './aon-icon-button.js';
import { AonElement } from './AonElement.js';

export class AonViewer extends AonElement{

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
		this.disableZoom();
		this.setControls();

		this.initialize();
		let divCanvas = this.createElement(TAG.DIV);
		divCanvas.id = this.AON_CANVAS_DIV;
		divCanvas.style.width = '100%';
		this.appendChild(divCanvas);


		if(this.file){
			if (this.type && this.type.includes('pdf')) {
				this.printPdf();
			} else if (this.type && this.type.includes('image')) {
				this.printImage();
			} else if (this.type) {
				this.notSupport(this.type);
			}
		} else {
			
		}

		let div = this.createElement(TAG.DIV);
		div.id = this.AON_VIEWER_DIV;
		div.style.display = 'none';
		this.appendChild(div);
		
		if (!this.isMobile())
			this.buildButtons();
	}

	initialize(){
		this._scale = 1;
		this.AON_VIEWER_DIV  = "aonViewerButtonsDiv";
		this.AON_CANVAS_DIV  = "aonViewerCanvasDiv";	
	}

	/**
	 * Remove the buttons from the viewer
	 */
	removeButtons() {
		let div = this.getElement(this.AON_VIEWER_DIV);
		div.innerHTML = '';
	}

	/**
	 * Build the buttons for the viewer
	 */
	buildButtons() {
		let div = this.getElement(this.AON_VIEWER_DIV);
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.position = "absolute";
		div.style.right = "2%";
		div.style.top = "1%";
		div.style.height = "80%";

		/*
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
		*/

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
				this.zoomReset();
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
				this.zoomIn();
				this.updatePdf(this.PDF);
			});
			zoomPlus.appendChild(aibz)
			div.appendChild(zoomPlus);

			let zoomMinus = this.createElement(TAG.SPAN);
			let aibzm = new AonIconButton();
			aibzm.id = "aonViewerButtonsDivZoomMinus";
			aibzm.icon = "zoom_out";
			aibzm.background = "#f1f1f1";
			aibzm.addEventListener(EVENT.CLICK, () => {
				this.zoomOut();
				this.updatePdf(this.PDF);
			});
			zoomMinus.appendChild(aibzm);
			zoomMinus.style.marginTop = "4px";
			div.appendChild(zoomMinus);
		}
	}

	/**
	 * Prints an image 
	 */
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

	/**
	 * Show a message when the file is not supported
	 * @param {*} type 
	 * @param {*} reason 
	 */
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

	/**
	 * Print the PDF file the first time
	 * @param {*} scalation 
	 */
	printPdf() {
		document.querySelectorAll(TAG.CANVAS).forEach((item, i) => item.remove());
		const pdfjsLib = window['pdfjs-dist/build/pdf'];
		pdfjsLib.GlobalWorkerOptions.workerSrc = '//mozilla.github.io/pdf.js/build/pdf.worker.js';

		// Asynchronous download of PDF
		const loadingTask = pdfjsLib.getDocument({
			url: this.file,
			httpHeaders: {
				'Access-Control-Allow-Origin': '*',
				'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, PUT, DELETE, HEAD',
				'Access-Control-Allow-Headers': 'X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept',
				'Access-Control-Max-Age': '1728000'
			},
			withCredentials: true
		});

		this.classList.add("loading");
		this._scale = 1;

		loadingTask.promise.then( (pdf) =>  {
			this.PDF = pdf;
			console.log('PDF loaded.');
			this.renderPdf(pdf);
			this.classList.remove("loading");
			this.zoomReset();
		}, (reason)=> {
			console.log(reason);
		});
	}

	/**
	 * Render the PDF file for the first time
	 * @param {*} pdf 
	 */
	renderPdf(pdf) {
		const div = this.getElement(this.AON_CANVAS_DIV);

		for (let pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++) {
			let c = this.getElement('canvas' + pageNumber);
			if(c){ 
				c.parentElement.removeChild(c);
			}
			
			const canvas = this.createElement(TAG.CANVAS);
			canvas.id = 'canvas' + pageNumber;
			canvas.style.border = '1px solid #ebebeb';
		
			div.appendChild(canvas);

			pdf.getPage(pageNumber).then((page) =>  {
				console.log('Page loaded');

				let scale = 3;
				console.log(scale);
				let viewport = page.getViewport({ scale });

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
	}



	/**
	 * Update the PDF file
	 */
	updatePdf() {
		const div = this.getElement(this.AON_CANVAS_DIV);
		this.scale(div, this._scale);
	}

	setControls() {

		const viewer = this;
		document.onkeydown = function(event) {
			if (event.ctrlKey==true && (event.key == '+') ) {
				event.preventDefault();
				viewer.zoomIn();
				viewer.updatePdf();
			}

			if (event.ctrlKey==true && (event.key == '-') ) {
				event.preventDefault();
				viewer.zoomOut();
				viewer.updatePdf();
			}

			if (event.ctrlKey==true && (event.key == 'e') ) {
				viewer.zoomReset();
				viewer.updatePdf();
			}
		};
	}

	/**
	 * Disable the global Zoom in the document
	 */
	disableZoom() {	
			
		window.addEventListener('mousewheel', function (event) {
			if (event.ctrlKey == true) {
				event.preventDefault();
			}
		});

		window.addEventListener('DOMMouseScroll', function (event) {
			if (event.ctrlKey == true) {
				event.preventDefault();
				console.log("ZOOM DISABLED");
			}
		});
			
	}

	scale(element, scale) {
		  element.style.transform         = `scale(${scale})`;
		  element.style["-moz-transform"] = `scale(${scale})`;
	}

	zoomIn() {
		this._scale = this._scale * 1.25;
	}

	zoomOut() {
		this._scale = this._scale * 0.85;
	}

	zoomReset() {
		this._scale = .5;
		this.updatePdf();
	}

}

if (!window.customElements.get(TAG.AON_VIEWER)) {
	window.customElements.define(TAG.AON_VIEWER, AonViewer);
}
