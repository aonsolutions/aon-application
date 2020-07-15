	function invoiceDropHandler(ev) {
		dropHandler(ev);
	}

	function documentalDropHandler(ev) {
		dropHandler(ev);
	}

	function dropHandler(ev) {
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
		removeDragData(ev)
	}

	function dragOverHandler(ev) {
		ev.preventDefault();
	}

	function removeDragData(ev) {
		console.log('Removing drag data')
		if (ev.dataTransfer.items) {
			// Use DataTransferItemList interface to remove the drag data
			ev.dataTransfer.items.clear();
		} else {
			// Use DataTransfer interface to remove the drag data
		    ev.dataTransfer.clearData();
		}
	}

	function invoiceClickHandler() {
		document.getElementById("invoice_file").click();
	}

	function documentalClickHandler() {
		document.getElementById("documental_file").click();
	}

	function documentalUploadFile(files) {
		let file = files[0];
		//const fn = function (base){
			let formData = new FormData();
	        formData.append('file', file, file.name);
			const token = localStorage.getItem("session_id");
			requestFile("/aon_gwt_aio/ms/uploadDocumentalx", token, formData);
		//};
		//uploadFile(files, fn);
	}

	function invoiceUploadFile(files) {
		const fn = function (base){
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
		uploadFile(files, fn);
	}

	function uploadFile(files, fn) {
		let photo = files[0];
		const reader = new FileReader();
		reader.onloadend = function () {
			const base64File = reader.result.split(',')[1];
		    fn(base64File)
		}
		reader.readAsDataURL(photo);
	}

class AonDesktop extends HTMLElement {
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
			<li style="display: inline-block;">

			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 style="font-size: 20px;" class="mdl-card__title-text">Facturas</h2>
				</div>
				<div id="invoice_drop_zone" class='dropzone' ondrop="invoiceDropHandler(event);" ondragover="dragOverHandler(event);" onclick="invoiceClickHandler()" >
					<div class='dropzone-text-wrapper'>
						<div class='dropzone-centered'>Arrastre aquí el archivo o click para seleccionar</div>
					</div>
					<input id='invoice_file' style='display:none;' type='file' name='invoice_file' multiple onchange="invoiceUploadFile(this.files)" >
				</div>


				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-invoice-add-button" icon="add"></aon-icon-button>
				</div>
			</div>
			</li>

			<li style="display: inline-block;">
			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 style="font-size: 20px;" class="mdl-card__title-text">Documental</h2>
				</div>
				<div id="documental_drop_zone" class='dropzone' ondrop="documentalDropHandler(event);" ondragover="dragOverHandler(event);" onclick="documentalClickHandler()" >
					<div class='dropzone-text-wrapper'>
						<div class='dropzone-centered'>Arrastre aquí el archivo o click para seleccionar</div>
					</div>
					<input id='documental_file' style='display:none;' type='file' name='documental_file' multiple onchange="documentalUploadFile(this.files)">
				</div>

				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-documental-add-button" icon="add"></aon-icon-button>
				</div>
			</div>
			</li>

			<li style="display: inline-block;">
			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 style="font-size: 20px;" class="mdl-card__title-text">Carga Ficheros Excel</h2>
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

			<li style="display: inline-block;">
			<div class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 style="font-size: 20px;" class="mdl-card__title-text">Carga de Nóminas (PDF)</h2>
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

			<li style="display: inline-block;">
			<div style="min-height: 40px;" class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 style="font-size: 20px;" class="mdl-card__title-text">Portal Laboral</h2>
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-payroll-launch-button" icon="launch"></aon-icon-button>
				</div>
			</div>
			</li>

			<li style="display: inline-block;">
			<div style="min-height: 40px;" class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 style="font-size: 20px;" class="mdl-card__title-text">Resumen Contable/Fiscal</h2>
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-fiscal-launch-button" icon="launch"></aon-icon-button>
				</div>
			</div>
			</li>

			<li style="display: inline-block;">
			<div style="min-height: 40px;" class="demo-card-wide mdl-card mdl-shadow--2dp">
				<div class="mdl-card__title">
					<h2 style="font-size: 20px;" class="mdl-card__title-text">Estadística Gestión</h2>
				</div>
				<div class="mdl-card__menu" style="top:10px;">
					<aon-icon-button id="aon-desktop-management-stat-launch-button" icon="launch"></aon-icon-button>
				</div>
			</div>
			</li>
			</ul>
			`;
  }
}

window.customElements.define('aon-desktop', AonDesktop);
window.dragOverHandler = dragOverHandler;

window.invoiceDropHandler = invoiceDropHandler;
window.invoiceClickHandler = invoiceClickHandler;
window.invoiceUploadFile = invoiceUploadFile;

window.documentalDropHandler = documentalDropHandler;
window.documentalClickHandler = documentalClickHandler;
window.documentalUploadFile = documentalUploadFile;
