import Apps from  '../services/app.js';

const ID = 'id';
const OPENED = 'opened';
const APP = 'app';

class AonMenu extends HTMLElement {

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	get opened() {
		return this.getAttribute('opened');
	}

	set opened(opened) {
		this.setAttribute('opened', opened);
	}


	get app() {
		return this.getAttribute('app');
	}

	set app(app) {
		this.setAttribute('app', app);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `

			<div id="aonMenuSidenav" class="aon-menu-sidenav">

			</div>
			`;
			this.buildMenu();
  }

	toogle() {
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');
		let rootPanel = document.getElementById('rootPanel');
		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '0px';
			rootPanel.style.marginRight = '0px';
			this.removeAttribute('opened')
		} else if(this.getAttribute('app')){
			aonMenuSidenav.style.width = '250px';
			rootPanel.style.marginRight = '250px';
		} else {
			aonMenuSidenav.style.width = '100px';
			rootPanel.style.marginRight = '100px';
		}
	}

	appSelection(app) {
		switch(app){
    	case Apps.INVOICE.app:
				rootPanel('<aon-invoice-panel></aon-invoice-panel>');
				break;
    	case Apps.DOCUMENTAL.app:
				startModule('aon_gwt_aio', 'documents');
				break;
			case Apps.HELPDESK.app:
				startModule('aon_gwt_aio', 'issues');
				break;
    	case Apps.ACCOUNTING.app:
				alert('ACCOUNTING');
				break;
			case Apps.FISCAL.app:
				alert('FISCAL');
				break;
			case Apps.PAYROLL.app:
				alert('PAYROLL');
				break;
			case Apps.OCR.app:
				alert('OCR');
				break;
			case Apps.AIO.app:
				alert('AIO');
				break;
			case Apps.SELFCONTA.app:
				alert('SELFCONTA');
				break;
			case Apps.SALTRA.app:
				alert('SALTRA');
				break;
			case Apps.BIDOQ.app:
				alert('BIDOQ');
				break;
			case Apps.ALMA.app:
				alert('ALMA');
				break;
			case Apps.LEARNING.app:
				alert('LEARNING');
				break;
		}
	}

	buildMenu() {
		let rootPanel = document.getElementById('rootPanel');
		let aonMenuSidenav = document.getElementById('aonMenuSidenav');

		if(this.getAttribute('opened')) {
			aonMenuSidenav.style.width = '100px';
			rootPanel.style.marginRight = '100px';
		} else {
			aonMenuSidenav.style.width = '0px';
			rootPanel.style.marginRight = '0px';
		}

		let ul = document.createElement('ul');
		ul.style.margin = '0px';
		ul.style.padding = '0px';
		ul.style.listStyle = 'none';

		ul.appendChild(this.buildApp(Apps.INVOICE));
		ul.appendChild(this.buildApp(Apps.DOCUMENTAL));
		aonMenuSidenav.appendChild(ul);
	}

	buildApp(app) {
		let li = document.createElement('li');
		li.style.height = '80px';
		li.style.backgroundColor = 'transparent';
		li.addEventListener('mouseover', () => {
 			li.style.backgroundColor = '#f1f1f1';
		});
		li.addEventListener('mouseleave', () => {
			li.style.backgroundColor = 'transparent';
		});

		let a = document.createElement('a');
		a.style.width = '80px';
		a.style.cursor = 'pointer';
		a.style.margin = '8px 2px';
		a.style.textAlign = 'center';

		a.addEventListener('click', () => {
			this.appSelection(app.app);
		});

		let div = document.createElement('div');
		div.style.padding = '8px 0px';
		let img = document.createElement('img');
		img.style.width = '40px';
		img.src = app.logo;
		img.title = app.title;

		let div2 = document.createElement('div');

		let span = document.createElement('span');
		span.style.fontSize = '12px';
		span.style.fontFamily = 'Roboto,sans-serif';
		span.style.color = 'black';
		span.innerHTML = app.title;
		div2.appendChild(span);
		div.appendChild(img);
		div.appendChild(div2);
		a.appendChild(div);
		li.appendChild(a);

		return li;
	}

}

window.customElements.define('aon-menu', AonMenu);
