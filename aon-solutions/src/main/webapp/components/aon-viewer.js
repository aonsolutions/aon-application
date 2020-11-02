class AonViewer extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get type() {
		return this.getAttribute('type');
	}

	set type(type) {
		this.setAttribute('type', type);
	}

	get file() {
		return this.getAttribute('file');
	}

	set file(file) {
		this.setAttribute('file', file);
	}

	get width() {
		return this.getAttribute('width');
	}

	set width(width) {
		this.setAttribute('width', width);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		if(this.type && this.type.includes('pdf')){
			this.printPdf();
		} else if(this.type && this.type.includes('image')){
			this.printImage();
		}
	}

	printImage() {
		let img = document.createElement('img');
		img.style.width = '100%';
		img.src = this.file;

		this.appendChild(img);
	}
	printPdf() {
		let me = this;
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
		loadingTask.promise.then(function(pdf) {
  		console.log('PDF loaded');
			alert(pdf.numPages);
  		// Fetch the first page
  		let pageNumber = 1;
			for(let pageNumber = 1; pageNumber <= pdf.numPages; pageNumber++){
				let canvas = document.createElement('canvas');
				canvas.id = 'canvas' + pageNumber;
				me.appendChild(canvas);

  			pdf.getPage(pageNumber).then(function(page) {
    			console.log('Page loaded');

					let scale = 1;
    			let viewport = page.getViewport({scale});
					if(width) {
						scale = width / viewport.width;
						viewport = page.getViewport({ scale });
					}

    			// Prepare canvas using PDF page dimensions
    			//var canvas = document.getElementById('the-canvas');
					let canvasPage = document.getElementById('canvas' + pageNumber);

    			let context = canvasPage.getContext('2d');
    			canvasPage.height = viewport.height;
    			canvasPage.width = viewport.width;

    			// Render PDF page into canvas context
    			let renderContext = {
      			canvasContext: context,
      			viewport: viewport
    			};
    			let renderTask = page.render(renderContext);
    			renderTask.promise.then(function () {
      			console.log('Page rendered');
    			});
  			});
			}
		}, function (reason) {
  		// PDF loading error
  		console.error(reason);
		});
	}
}

window.customElements.define('aon-viewer', AonViewer);
