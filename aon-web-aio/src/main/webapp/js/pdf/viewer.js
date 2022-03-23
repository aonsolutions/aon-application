import {
	CONSTANT,
	EVENT,
	TAG
} from './environments.js';
import {
	AonIcon
} from './aon-icon.js';
import {
	AonIconButton
} from './aon-icon-button.js';
import {
	AonElement
} from './AonElement.js';

export class AonViewer extends AonElement {

	_scale;
	AON_VIEWER_DIV;
	AON_CANVAS_DIV;
	AON_PAGE_NUMBER_DIV;
	PDF;

	current;
	pages;
	cursor;

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
		this.initialize();
	}

	connectedCallback() {

		this.pages = [];
		this.setCurrent(0);

		//this.showCursor();
		this.setScrollEvents();
		this.setKeyboardControls();

		this.initialize();
		let divCanvas = this.createElement(TAG.DIV);
		divCanvas.id = this.AON_CANVAS_DIV;
		divCanvas.style.width = '100%';
		this.appendChild(divCanvas);


		if (this.file) {
			if (this.type && this.type.includes('pdf')) {
				this.printPdf();
			} else if (this.type && this.type.includes('image')) {
				this.printImage();
			} else if (this.type) {
				this.notSupport(this.type);
			}
		}

		let div = this.createElement(TAG.DIV);
		div.id = this.AON_VIEWER_DIV;
		div.style.display = 'none';
		this.appendChild(div);

		if (!this.isMobile())
			this.buildButtons();
	}

	initialize() {
		this._scale = 1;
		this.AON_VIEWER_DIV = "aonViewerButtonsDiv";
		this.AON_CANVAS_DIV = "aonViewerCanvasDiv";
		this.AON_PAGE_NUMBER_DIV = "page-number";
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
		let buttonBar = this.getElement(this.AON_VIEWER_DIV);
		buttonBar.style.display = "flex";
		buttonBar.style.flexDirection = "column";
		buttonBar.style.position = "absolute";
		buttonBar.style.right = "2%";
		buttonBar.style.top = "1%";
		buttonBar.style.height = "80%";
		buttonBar.style.justifyContent = "space-between";

		if (this.type.includes('pdf')) {
			this.buildZoomControls(buttonBar);
			this.buildPageControls(buttonBar);
		}

		this.buildDownloadRelatedControls(buttonBar);
	}

	/**
	 *  Build the buttons for zooming in, out and reset
	 */
	buildZoomControls(parent) {

		const zoomDiv = this.createElement(TAG.DIV);
		zoomDiv.style.display = "flex";
		zoomDiv.style.flexDirection = "column";

		// Zoom in
		const zoomPlus = this.createElement(TAG.SPAN);
		zoomPlus.style.marginTop = ".25rem";

		const zoomPlusButton = new AonIconButton();
		zoomPlusButton.id = "aonViewerButtonsDivZoomPlus";
		zoomPlusButton.icon = "zoom_in";
		zoomPlusButton.background = "#f1f1f1";
		zoomPlusButton.addEventListener(EVENT.CLICK, () => {
			this.zoomIn();
			this.updatePdf(this.PDF);
		});
		zoomPlus.appendChild(zoomPlusButton)
		zoomDiv.appendChild(zoomPlus);

		// Auto zoom
		const ajustar = this.createElement(TAG.SPAN);
		ajustar.style.marginTop = ".25rem";

		const autoZoomButton = new AonIconButton();
		autoZoomButton.id = "aonViewerButtonsDivAjustar";
		autoZoomButton.icon = "zoom_out_map";
		autoZoomButton.background = "#f1f1f1";
		
		autoZoomButton.addEventListener(EVENT.CLICK, () => {
			this.zoomReset();
		});
		ajustar.appendChild(autoZoomButton);
		zoomDiv.appendChild(ajustar);

		// Zoom out
		const zoomMinus = this.createElement(TAG.SPAN);
		const zoomMinusButton = new AonIconButton();
		zoomMinusButton.id = "aonViewerButtonsDivZoomMinus";
		zoomMinusButton.icon = "zoom_out";
		zoomMinusButton.background = "#f1f1f1";
		zoomMinusButton.addEventListener(EVENT.CLICK, () => {
			this.zoomOut();
			this.updatePdf(this.PDF);
		});
		zoomMinus.appendChild(zoomMinusButton);
		zoomMinus.style.marginTop = ".25rem";
		zoomDiv.appendChild(zoomMinus);
		parent.appendChild(zoomDiv);
	}

	/**
	 * Build the page controls next , previous and page number
	 */
	buildPageControls(parent) {
		const pageControlDiv = this.createElement(TAG.DIV);
		pageControlDiv.style.display = "flex";
		pageControlDiv.style.flexDirection = "column";

		const previous = this.createElement(TAG.SPAN);
		const previousButton = new AonIconButton();
		previousButton.id = "aonViewerButtonsDivPreviousPage";
		previousButton.icon = "keyboard_arrow_up";
		previousButton.background = "#f1f1f1";
		previousButton.addEventListener(EVENT.CLICK, () => {
			this.setCurrent(this.current - 1);
			this.jump();
		});
		previous.appendChild(previousButton);
		previous.style.marginTop = ".25rem";
		pageControlDiv.appendChild(previous);
		
		const page = this.createElement(TAG.INPUT);
		page.type = "text";
		page.value = "0";
		page.style.marginTop = ".25rem";

		const oneKey = 48;
		const nineKey = 57;
		const backspaceKey = 46;
		const deleteKey = 8;
		const leftArrowKey = 37;
		const rightArrowKey = 39;

		page.addEventListener(EVENT.KEYDOWN, (e) => {
			// if not a number or not delete or not backspace
			if ((e.keyCode < oneKey || e.keyCode > nineKey) && e.keyCode != backspaceKey && e.keyCode != deleteKey && e.keyCode != leftArrowKey && e.keyCode != rightArrowKey) {
				e.preventDefault();
				page.value = this.current;
				return;
			}
		});

		page.addEventListener(EVENT.KEYUP, (e) => {
			this.setCurrent(parseInt(page.value));
			if (isNaN(this.current)) {
				this.setCurrent("");
			} else
				this.jump();
		});

		page.addEventListener(EVENT.BLUR, (e) => {
			//if not a number
			if (isNaN(this.current)) {
				this.setCurrent(1);
			}
			page.value = this.current;
			this.jump();
		});

		page.id = this.AON_PAGE_NUMBER_DIV;
		pageControlDiv.appendChild(page);

		const next = this.createElement(TAG.SPAN);
		const nextButton = new AonIconButton();
		nextButton.id = "aonViewerButtonsDivPreviousPage";
		nextButton.icon = "keyboard_arrow_down";
		nextButton.background = "#f1f1f1";
		nextButton.addEventListener(EVENT.CLICK, () => {
			this.setCurrent(this.current + 1); 
			this.jump();
		});
		next.appendChild(nextButton);
		next.style.marginTop = ".25rem";
		pageControlDiv.appendChild(next);

		parent.appendChild(pageControlDiv);
	}

	/**
	 *  Build the buttons for printing and downloading the file, 
	 *  The print button is only available for PDF files
	 */
	buildDownloadRelatedControls(parent) {

		const downloadDiv = this.createElement(TAG.DIV);
		downloadDiv.style.display = "flex";
		downloadDiv.style.flexDirection = "column";

		if (this.type.includes('pdf')) {
			let print = this.createElement(TAG.SPAN);
			print.style.marginTop = ".25rem";
			let printButton = new AonIconButton();
			printButton.id = "aonViewerButtonsDivPrint";
			printButton.icon = "print";
			printButton.background = "#f1f1f1";
			printButton.addEventListener(EVENT.CLICK, () => this.printDocument());
			print.appendChild(printButton);
			downloadDiv.appendChild(print);
		}

		const download = this.createElement(TAG.SPAN);
		download.style.marginTop = ".25rem";

		const downloadButton = new AonIconButton();
		downloadButton.id = "aonViewerButtonsDivDownload";
		downloadButton.icon = "download";
		downloadButton.background = "#f1f1f1";
		downloadButton.addEventListener(EVENT.CLICK, () => open(this.file));
		download.appendChild(downloadButton)
		downloadDiv.appendChild(download);

		parent.appendChild(downloadDiv);


		/*
			//  Mail button
			// --------------------------------------------------
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
	}

	/**
	 * Prints an image 
	 */
	printImage() {
		let img = this.createElement(TAG.IMG);
		img.style.width = '100%';
		img.src = this.file;
		img.onerror = () => {
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
		this.pages.forEach((item, i) => item.remove());
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

		loadingTask.promise.then((pdf) => {
			this.PDF = pdf;
			this.renderPdf(pdf);
			this.classList.remove("loading");

			this.zoomReset();
		}, (reason) => {
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
			if (c) {
				c.parentElement.removeChild(c);
			}

			const canvas = this.createElement(TAG.CANVAS);
			canvas.id = 'canvas' + pageNumber;
			canvas.style.border = '1px solid #ebebeb';

			// TODO create text layer and add it to the canvas
			// --------------------------------------------------
				const textLayerDiv = this.createElement(TAG.DIV);
			//---------------------------------------------------

			this.pages.push(canvas);
			div.appendChild(canvas);

			pdf.getPage(pageNumber).then((page) => {
				let scale = 3;
				let viewport = page.getViewport({
					scale
				});

				const context = canvas.getContext('2d');
				canvas.height = viewport.height;
				canvas.width = viewport.width;

				const viewer  = this;
				canvas.onclick = (e) => {
					viewer.current = pageNumber;
					const pageIndicator = document.getElementById("page-number")

					if(pageIndicator) {
						pageIndicator.value = this.current;
					}
				}


				// Render PDF page into canvas context
				const renderContext = {
					canvasContext: context,
					viewport: viewport
				};
				const renderTask = page.render(renderContext);
				renderTask.promise.then(() => {
					//console.log('Page rendered');
				});

				/*
					// TODO create text layer and add it to the canvas
					// --------------------------------------------------
						page.getTextContent().then(function (textContent) {
							var textLayer = new TextLayerBuilder({
								textLayerDiv: $textLayerDiv.get(0),
								pageIndex: page_num - 1,
								viewport: viewport
							});

							textLayer.setTextContent(textContent);
							textLayer.render();
						});
				*/
			});
		}
	}


	/**
	 * Update the PDF file scale and observers
	 */
	updatePdf() {
		const div = this.getElement(this.AON_CANVAS_DIV);
		this.scale(div, this._scale);
	}

	/**
	 * Set the keyboard controls for the PDF files
	 * ctrl + -> zoom in
	 * ctrl - -> zoom out
	 * ctrl + e -> zoom reset
	 * ctrl + up -> next page
	 * ctrl + down -> previous page
	 */
	setKeyboardControls() {
		const viewer = this;
		document.onkeydown = function (event) {
			if (event.ctrlKey == true && (event.key == '+')) {
				event.preventDefault();
				viewer.zoomIn();
				viewer.updatePdf();
			}

			if (event.ctrlKey == true && (event.key == '-')) {
				event.preventDefault();
				viewer.zoomOut();
				viewer.updatePdf();
			}

			if (event.ctrlKey == true && (event.key == 'e')) {
				viewer.zoomReset();
				viewer.updatePdf();
			}

			if (event.ctrlKey == true && (event.key == 'ArrowUp')) {
				event.preventDefault();
				viewer.current = (viewer.current || 0) - 1;
				viewer.jump();
				console.log(viewer.current);
			}

			if (event.ctrlKey == true && (event.key == 'ArrowDown')) {
				event.preventDefault();
				viewer.current = (viewer.current || 0) + 1;
				viewer.jump();
				console.log(viewer.current);
			}
		};
	}

	/**
	 * Set the scrollbar observers for the PDF files
	 */
	setScrollEvents() {
		const viewer = this;
		const cursor = this.cursor;

		setInterval(function () {
			const offsets = cursor.getBoundingClientRect();
			const top = offsets.top;
			const left = offsets.left;

			viewer.click(top, left);	
		}, 100);
	}

	/**
	 * Scale an element with CSS
	 * @param {*} element The HTML element to scale 
	 * @param {*} scale The scale to apply to the element
	 */
	scale(element, scale) {
		element.style.transform = `scale(${scale})`;
		element.style["-moz-transform"] = `scale(${scale})`;
	}

	/**
	 * Zoom in the document
	 */
	zoomIn() {
		this._scale = this._scale * 1.25;
		this.jump();
	}

	/**
	 * Zoom out the document
	 */
	zoomOut() {
		this._scale = this._scale * 0.85;
		this.jump();
	}

	/**
	 * Reset the zoom to the default
	 */
	zoomReset() {

		if(window.innerWidth < 1200){
			this._scale = (window.innerWidth / 1785) * .85;
		} else {
			this._scale = (window.innerWidth / 1785) * .65;
		}

		this.updatePdf();
		this.jump();
	}

	/**
	 * Print the file to the printer
	 */
	printDocument() {
		if (this.PDF && this.type) {
			this.PDF.getData().then(data => {
				const pdfUrl = URL.createObjectURL(new Blob([data], {
					type: this.type
				}));
				const iframeId = this.id + "iframeTmp";
				let iframe = this.querySelector('#' + iframeId) || this.createElement('iframe'); //load content in an iframe to print later
				iframe.id = iframeId;
				iframe.src = pdfUrl;
				iframe.style.display = 'none';
				this.appendChild(iframe);
				iframe.onload = () => {
					setTimeout(() => {
						iframe.focus();
						iframe.contentWindow.print();
					}, 1);
				};
			});
		}
	}


	/**
	 * Jump to current page
	 */
	jump() {
		const page = document.querySelector("#page-number");
		if (this.current > this.pages.length) {
			this.setCurrent(this.pages.length);
		}

		if (this.current < 1) {
			this.setCurrent(1);
		}

		const pageCanvas = this.pages[this.current - 1];
		if (pageCanvas) {
			page.value = this.current;
			pageCanvas.scrollIntoView();
		}
	}

	click(x, y)
	{
		var ev = new MouseEvent('click', {
			'view': window,
			'bubbles': true,
			'cancelable': true,
			'screenX': x,
			'screenY': y
		});
	
		var el = document.elementFromPoint(x, y);

		console.log("INTERSECTION: ",el);
		if(el)
			el.dispatchEvent(ev);
	}


	showCursor() {
		this.cursor = this.createElement(TAG.SPAN);
		this.cursor.classList.add("material-icons");
		this.cursor.innerHTML = "api";
		this.cursor.style.position = "fixed";
		this.cursor.style.zIndex = "9999";
		this.cursor.style.color = "red";
		this.cursor.style.top = this.offsetHeight/2 + "px";
		this.cursor.style.left = this.offsetWidth/2 + "px";

		setInterval(() => {
			this.cursor.style.top = this.offsetHeight/2 + "px";
			this.cursor.style.left = this.offsetWidth/2 + "px";
		}, 10);

		document.body.appendChild(this.cursor);
	}

	setCurrent(current) {
		if(this.current != current)
			this.current = current;
	}
	

}

if (!window.customElements.get(TAG.AON_VIEWER)) {
	window.customElements.define(TAG.AON_VIEWER, AonViewer);
}