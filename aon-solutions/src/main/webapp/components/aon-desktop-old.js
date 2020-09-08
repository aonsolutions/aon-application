import {request, requestFile} from  '../services/request.js';

class AonDesktop extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get company() {
		return thiS.getAttribute('company');
	}

	set company(company) {
		this.setAttribute('company', company);
	}

	get user() {
		return this.getAttribute('user');
	}

	set user(user) {
		this.setAttribute('user', user);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
			<link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css">
			<script defer src="https://code.getmdl.io/1.3.0/material.min.js"></script>
			<!-- Wide card with share menu button -->
			<style>
				.demo-card-wide.mdl-card {
					margin: 20px;
				}

				.dropzone {
					min-height: 100px;
					height: 100px;
					display: table;
					margin-top: 1em;
					margin-left: auto;
					margin-right: auto;
					width: 80%;
					border-width: 2px;
					border-style: dashed;
					border-color: #bbb;
					margin-bottom: 1em;
				}

				.dropzone-text-wrapper {
					display: table-cell;
					text-align:center;
					vertical-align:middle;
					pointer-events: none;
				}

				.dropzone-centered {
					font-size: 0.8em;
					font-style: italic;
					text-align:center;
				}

				.aon-desktop-list {
					margin: 0px;
					height: 100%;
					overflow-y: auto;
					background-color: #f1f1f1;
				}
			</style>

			<ul class="aon-desktop-list">
			<li id="aonDesktop-invoice" style="display: inline-block;">

			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Facturas</h2>
				</div>
				<div id="invoice_drop_zone" class='dropzone' >
					<div class='dropzone-text-wrapper'>
						<div class='dropzone-centered'>Arrastre aquí el archivo o click para seleccionar</div>
					</div>
					<input id='invoice_file' style='display:none;' type='file' name='invoice_file' multiple>
				</div>


				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-invoice-add-button" icon="add"></aon-icon-button>
				</div>
			</div>
			</li>

			<li id="aonDesktop-documental" style="display: inline-block;">
			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Documental</h2>
				</div>
				<div id="documental_drop_zone" class='dropzone' >
					<div class='dropzone-text-wrapper'>
						<div class='dropzone-centered'>Arrastre aquí el archivo o click para seleccionar</div>
					</div>
					<input id='documental_file' style='display:none;' type='file' name='documental_file' multiple >
				</div>

				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-documental-add-button" icon="add"></aon-icon-button>
				</div>
			</div>
			</li>

			<li id="aonDesktop-import" style="display: inline-block;">
			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Carga Ficheros Excel</h2>
				</div>
				<div id="excel_drop_zone" class='dropzone'  >
					<div class='dropzone-text-wrapper'>
						<div class='dropzone-centered'>Arrastre aquí el archivo o click para seleccionar</div>
					</div>
					<input id='excel_file' style='display:none;' type='file' name='excel_file' multiple >
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-excel-add-button" icon="add"></aon-icon-button>
				</div>
			</div>
			</li>

			<li id="aonDesktop-payroll" style="display: inline-block;">
			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Carga de Nóminas (PDF)</h2>
				</div>
				<div id="excel_drop_zone" class='dropzone'  >
					<div class='dropzone-text-wrapper'>
						<div class='dropzone-centered'>Arrastre aquí el archivo o click para seleccionar</div>
					</div>
					<input id='nominas_file' style='display:none;' type='file' name='nominas_file' multiple >
				</div>

				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-payroll-add-button" icon="add"></aon-icon-button>
				</div>
			</div>
			</li>

			<li id="aonDesktop-payroll2" style="display: inline-block;">
			<div style="min-height: 40px;" class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Portal Laboral</h2>
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-payroll-launch-button" icon="launch"></aon-icon-button>
				</div>
			</div>
			</li>

			<li id="aonDesktop-accounting" style="display: inline-block;">
			<div style="min-height: 40px;" class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Resumen Contable</h2>
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-fiscal-launch-button" icon="launch"></aon-icon-button>
				</div>
			</div>
			</li>

			<li id="aonDesktop-fiscal" style="display: inline-block;">
			<div style="min-height: 40px;" class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Resumen Fiscal</h2>
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-fiscal-launch-button" icon="launch"></aon-icon-button>
				</div>
			</div>
			</li>

			<li id="aonDesktop-managementStat" style="display: inline-block;">
			<div style="min-height: 40px;" class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 class="aonTitle">Estadística Gestión</h2>
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-management-stat-launch-button" icon="launch"></aon-icon-button>
				</div>
			</div>
			</li>
			</ul>
			`;

			let invoiceDropZone = document.getElementById('invoice_drop_zone');
			invoiceDropZone.addEventListener('drop', (event) => {
				this.invoiceDropHandler(event);
			});
			invoiceDropZone.addEventListener('dragover', (event) => {
				this.dragOverHandler(event);
			});
			invoiceDropZone.addEventListener('click', (event) => {
				this.invoiceClickHandler();
			});

			let documentalDropZone = document.getElementById('documental_drop_zone');
			documentalDropZone.addEventListener('drop', (event) => {
				this.documentalDropHandler(event);
			});
			documentalDropZone.addEventListener('dragover', (event) => {
				this.dragOverHandler(event);
			});
			documentalDropZone.addEventListener('click', (event) => {
				this.documentalClickHandler();
			});

			let documentalFile = document.getElementById('documental_file');
			documentalFile.addEventListener('change', (event) => {
				this.documentalUploadFile(documentalFile.files);
			});

			let invoiceFile = document.getElementById('invoice_file');
			invoiceFile.addEventListener('change', (event) => {
				this.invoiceUploadFile(invoiceFile.files);
			});
	}


	invoiceDropHandler(ev) {
		this.dropHandler(ev);
	}

	documentalDropHandler(ev) {
		this.dropHandler(ev);
	}

	dropHandler(ev) {
		console.log('File(s) dropped');

		// Prevent default behavior (Prevent file from being opened)
		ev.preventDefault();

		if (ev.dataTransfer.items) {
			// Use DataTransferItemList interface to access the file(s)
			for (var i = 0; i < ev.dataTransfer.items.length; i++) {
		    	// If dropped items aren't files, reject them
		    	if (ev.dataTransfer.items[i].kind === 'file') {
		    		var file = ev.dataTransfer.items[i].getAsFile();
		    		console.log('... file[' + i + '].name = ' + file.name);
		    	}
		    }
		} else {
			// Use DataTransfer interface to access the file(s)
		    for (var i = 0; i < ev.dataTransfer.files.length; i++) {
		      console.log('... file[' + i + '].name = ' + ev.dataTransfer.files[i].name);
		    }
		}

		// Pass event to removeDragData for cleanup
		this.removeDragData(ev)
	}

	dragOverHandler(ev) {
		ev.preventDefault();
	}

	removeDragData(ev) {
		console.log('Removing drag data')
		if (ev.dataTransfer.items) {
			// Use DataTransferItemList interface to remove the drag data
			ev.dataTransfer.items.clear();
		} else {
			// Use DataTransfer interface to remove the drag data
		    ev.dataTransfer.clearData();
		}
	}

	invoiceClickHandler() {
		document.getElementById("invoice_file").click();
	}

	documentalClickHandler() {
		document.getElementById("documental_file").click();
	}

	documentalUploadFile(files) {
		let file = files[0];
		let formData = new FormData();
	  formData.append('file', file, file.name);
		const token = localStorage.getItem("session_id");
		requestFile("/aon_gwt_aio/ms/uploadDocumentalx", token, formData);
	}

	invoiceUploadFile(files) {
		const fn = (base) => {
			const invoiceData = {
					company: 'B01480201',
				    content: base,
				    contentType: 'image/jpeg',
				    contentEncoding: 'base64'
		    };
			const url = 'https://europe-west1-tedi-snapshot.cloudfunctions.net/invoice';
			const token = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqZ2FyY2lhQGFvbnNvbHV0aW9ucy5lcyIsImlhdCI6MTU3NzM2NTM4NywiZXhwIjoxNjA4NDY5Mzg3fQ.0cfTgABc7d6cM8TbDbP1bhsRVxSlz_9te5fzbYJjAjg';
			request('POST', url, token, invoiceData);
		};
		this.uploadFile(files, fn);
	}

	uploadFile(files, fn) {
		let photo = files[0];
		const reader = new FileReader();
		reader.onloadend = () => {
			const base64File = reader.result.split(',')[1];
		    fn(base64File)
		}
		reader.readAsDataURL(photo);
	}

}

window.customElements.define('aon-desktop', AonDesktop);
