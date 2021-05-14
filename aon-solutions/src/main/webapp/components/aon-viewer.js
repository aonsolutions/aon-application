import { AonElement } from './AonElement.js';
import { CONSTANT, EVENT, TAG } from '../environments/environments.js';
import './aon-icon.js';
import './aon-icon-button.js';


export class AonViewer extends AonElement {

	_scale;

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
		this._scale = 1;
	}

	connectedCallback() {
		let divCanvas = this.createElement(TAG.DIV);
		divCanvas.id = 'aonViewerCanvasDiv';
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
		div.id = 'aonViewerButtonsDiv';
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

	removeButtons() {
		let div = this.getElement('aonViewerButtonsDiv');
		div.innerHTML = '';
	}

	buildButtons() {
		let div = this.getElement('aonViewerButtonsDiv');
		div.style.visibility = 'hidden';
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.position = "absolute";
		div.style.right = "2%";
		div.style.top = "1%";
		// div.style.justifyContent = "space-between";
		div.style.height = "80%";
		let mail = this.createElement(TAG.SPAN);
		mail.innerHTML = `<aon-icon-button id="aonViewerButtonsDivEmail" icon="email" background="#f1f1f1"></aon-icon-button>`;
		div.appendChild(mail);

		let print = this.createElement(TAG.SPAN);
		print.style.marginTop =  "4px";
		print.innerHTML = `<aon-icon-button id="aonViewerButtonsDivPrint" icon="print" background="#f1f1f1"></aon-icon-button>`;
		div.appendChild(print);
		let printButton = this.getElement('aonViewerButtonsDivPrint');
		printButton.addEventListener(EVENT.CLICK, () => {

		});

		let download = this.createElement(TAG.SPAN);
		download.style.marginTop =  "4px";
		download.innerHTML = `<aon-icon-button id="aonViewerButtonsDivDownload" icon="download" background="#f1f1f1"></aon-icon-button>`;
		div.appendChild(download);
		let downloadButton = this.getElement('aonViewerButtonsDivDownload');
		downloadButton.addEventListener(EVENT.CLICK, () => open(this.file));


		if (this.type.includes('pdf')) {
			let ajustar = this.createElement(TAG.SPAN);
			ajustar.style.marginTop =  "auto";
			ajustar.innerHTML = `<aon-icon-button id="aonViewerButtonsDivAjustar" icon="zoom_out_map" background="#f1f1f1"></aon-icon-button>`;
			div.appendChild(ajustar);
			this.getElement('aonViewerButtonsDivAjustar').addEventListener(EVENT.CLICK, () => {
				document.querySelectorAll('canvas').forEach((item, i) => {
					item.remove();
				});
				this._scale = 1;
				this.printPdf(this._scale);
			});

			let zoomPlus = this.createElement(TAG.SPAN);
			zoomPlus.style.marginTop = "4px";
			zoomPlus.innerHTML = `<aon-icon-button id="aonViewerButtonsDivZoomPlus" icon="zoom_in" background="#f1f1f1"></aon-icon-button>`;
			div.appendChild(zoomPlus);
			this.getElement('aonViewerButtonsDivZoomPlus').addEventListener(EVENT.CLICK, () => {
				document.querySelectorAll('canvas').forEach((item, i) => {
					item.remove();
				});
				this._scale = this._scale - 0.25;
				this.printPdf(this._scale);
			});

			let zoomMinus = this.createElement(TAG.SPAN)
			zoomMinus.innerHTML = `<aon-icon-button id="aonViewerButtonsDivZoomMinus" icon="zoom_out" background="#f1f1f1"></aon-icon-button>`;
			zoomMinus.style.marginTop = "4px";
			div.appendChild(zoomMinus);
			this.getElement('aonViewerButtonsDivZoomMinus').addEventListener(EVENT.CLICK, () => {
				document.querySelectorAll('canvas').forEach((item, i) => {
					item.remove();
				});
				this._scale = this._scale + 0.25;
				this.printPdf(this._scale);
			});
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

	notSupport(type) {
		let div = this.createElement(TAG.DIV);
		div.style.backgroundColor = 'rgba(128, 128, 128, 0.1)';
		div.style.height = '100%';
		div.style.display = 'flex';
		this.appendChild(div);

		let div2 = this.createElement(TAG.DIV);
		div2.style.margin = 'auto';
		div2.innerHTML = this.getTypeIcon(type);
		let div3 = this.createElement(TAG.DIV);
		div3.innerHTML = 'Vista previa no disponible';
		div2.appendChild(div3);
		div.appendChild(div2);
	}

	getTypeIcon(type) {
		if (type.includes('pdf')) {
			return `<aon-icon icon="aon_pdf" size="160"></aon-icon>`;
		} else if (type.includes('powerpoint') || type.includes('presentation')) {
			return `<aon-icon icon="aon_powerpoint" size="160" color="orange"></aon-icon>`
		} else if (type.includes('excel') || type.includes('spreadsheet')) {
			return `<aon-icon icon="aon_excel" size="160" color="green"></aon-icon>`
		} else if (type.includes('word') || type.includes('text')) {
			return `<aon-icon icon="aon_word" size="160" color="cornflowerblue"></aon-icon>`
		} else {
			return `<aon-icon icon="aon_file" size="160"></aon-icon>`
		}
	}

	printPdf(scalation) {
		let div = this.getElement('aonViewerCanvasDiv');
		let width = this.getAttribute('width');
		// this.innerHTML = `<script src="//mozilla.github.io/pdf.js/build/pdf.js"></script>`;
		// let canvas = document.createElement('canvas');
		// this.appendChild(canvas);

		let pdfjsLib = window['pdfjs-dist/build/pdf'];

		// The workerSrc property shall be specified.
		pdfjsLib.GlobalWorkerOptions.workerSrc = '//mozilla.github.io/pdf.js/build/pdf.worker.js';

		// Asynchronous download of PDF
		//		var url = 'https://raw.githubusercontent.com/mozilla/pdf.js/ba2edeae/examples/learning/helloworld.pdf';

		let loadingTask = pdfjsLib.getDocument({
			url: this.file,
			httpHeaders: {
				'Access-Control-Allow-Origin': '*',
				'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, PUT, DELETE, HEAD',
				'Access-Control-Allow-Headers': 'X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept',
				'Access-Control-Max-Age': '1728000'
			},
			withCredentials: true
		});
		loadingTask.promise.then( (pdf) =>  {
			console.log('PDF loaded');
			// Fetch the first page
			// let pageNumber = 1;
			for (let pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++) {
				let canvas = this.createElement(TAG.CANVAS);
				canvas.id = 'canvas' + pageNumber;
				div.appendChild(canvas);

				pdf.getPage(pageNumber).then( (page) =>  {
					console.log('Page loaded');

					let scale = scalation || 1;
					let viewport = page.getViewport({ scale });
					if (width) {
						scale = width / viewport.width;
						viewport = page.getViewport({ scale });
					}

					// Prepare canvas using PDF page dimensions
					//var canvas = document.getElementById('the-canvas');
					let canvasPage = this.getElement('canvas' + pageNumber);

					let context = canvasPage.getContext('2d');
					canvasPage.height = viewport.height;
					canvasPage.width = viewport.width;

					// Render PDF page into canvas context
					let renderContext = {
						canvasContext: context,
						viewport: viewport
					};
					let renderTask = page.render(renderContext);
					renderTask.promise.then( ()=> {
						console.log('Page rendered');
					});
				});
			}
		},  (reason)=> {
			// PDF loading error
			console.error(reason);
		});
	}
}
if (!window.customElements.get('aon-viewer')) {
	window.customElements.define('aon-viewer', AonViewer);
}
