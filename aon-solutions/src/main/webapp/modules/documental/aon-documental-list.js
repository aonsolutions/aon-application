import {AonElement} from '../../components/AonElement.js';
import {getDocuments, downloadDocuments, sendDocumentMail, updateFiles, deleteFile, getDomainUserRoles} from '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

import '../../components/aon-table.js';

import { CONSTANT, MSG } from '../../environments/environments.js';
import * as ACTION from '../actions.js';
import * as LS from '../../services/localStorageService.js';
import { DOCUMENTAL } from '../../services/app.js';
import { AonTable } from '../../components/aon-table.js';

export class AonDocumentalList extends AonElement {

	more;
	_roles;

	TABLE;

	static get observedAttributes() {
		return [];
	}

	get filter() {
    return this.getAttribute(CONSTANT.FILTER);
  }

  set filter(filter) {
    this.setAttribute(CONSTANT.FILTER, filter);
  }

	attributeChangedCallback(name, oldValue, newValue) {
		this.initialize();
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		getDomainUserRoles({}).then(r => {
			this._roles = new DomainUserRoles(r);
			this.build();
		});
 	}

	initialize() {
		this.more = true;
		this.TABLE = 'aonDocumentalTable';
	}

 	build() {
		let aonDocumentalTable =  new AonTable();
		aonDocumentalTable.id = this.TABLE;
		aonDocumentalTable.selectable = 'true';
		aonDocumentalTable.setApp(DOCUMENTAL);
		this.appendChild(aonDocumentalTable);

		aonDocumentalTable.addColumn(MSG.DATE, 'date', 'date', '20%');
		aonDocumentalTable.addColumn(MSG.NAME, 'string', 'title', '60%');
		aonDocumentalTable.addColumn(MSG.SIZE, 'string', 'size', '15%');

		// INFO
		// aonInvoiceTable.addColumn('', '', '');
		this.init();
		aonDocumentalTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore()
		});

		aonDocumentalTable.addEventListener('select', () => {
			if(aonDocumentalTable.selected.length === 1) {
				this.addDocumentalActions();
			} else if(aonDocumentalTable.selected.length === 0){
				this.removeDocumentalActions();
			}
		});
	}

	loadMore() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		let filter = this.getFilter();

		if(aonDocumentalTable && filter.page) {
			filter.page = filter.page + 1;
			this.setFilter(filter);
			getDocuments(filter).then(documents => {
				if(documents.length == 0)
					this.more = false;
				documents.forEach((doc, i) => {
					aonDocumentalTable.addRow(doc, () => this.aonDocument(doc, i), (e) => this.aonDocumentContextMenu(e, doc, i));
				});
			});
		}
	}

	init() {
		this.more = true;
		let aonDocumentalTable = this.getElement(this.TABLE);
		if(aonDocumentalTable) {
			getDocuments(this.getFilter()).then(documents => {
				aonDocumentalTable.removeRows();
				aonDocumentalTable.selected = [];
				this.removeDocumentalActions();
				documents.forEach((doc, i) => {
					aonDocumentalTable.addRow(doc, () => this.aonDocument(doc, i), (e) => this.aonDocumentContextMenu(e, doc, i));
				});
			});
		}
	}

	aonDocument(doc, i) {
		this.removeDocumentalActions();
		this.getApplication().setContentHTML(`<aon-document document='${JSON.stringify(doc)}'> </aon-document>`);
	}

	downloadFiles() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		let data = {
			domainId: LS.getDomainId(),
			domainName: LS.getDomainName(),
			domainLogin: LS.getDomainLogin(),
			ids: aonDocumentalTable.selected.map(r => r.id),
			type: this.getFilter().type
		};
		let json = btoa(JSON.stringify(data));
		downloadDocuments(json);
	}

	editFiles() {
		let aonDocumental = this.getApplication();
		let parent = aonDocumental.getParent();
		let d = document.getElementById(aonDocumental.DIALOG);
		let aonDocumentalTable = this.getElement(this.TABLE);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.EDIT_FILES);
		d.setContent(parent.uploadOption());
		// d.setContentHTML(MSG.IN_DEVELOPMENT);
		d.addAcceptAction(() => {
			let data = {
				category: this.getElement("aonDocumentalUploadCategory").value,
				scope: this.getElement("aonDocumentalUploadScope").value,
				tag: this.getElement("aonDocumentalUploadTag").value,
				documents: aonDocumentalTable.selected
			}
	    updateFiles(data);
		});
		d.open();
	}

	removeFiles() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		let aonDocumental = this.getApplication();
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_FILE);
		d.setContentHTML(`Estás seguro de eliminar los ficheros?`);
		d.addAcceptAction(() => {
		  deleteFile({
			id: aonDocumentalTable.selected.map(r => r.id),
			attach_type: 'registry'
		  }).then(() => this.init() );
		});
		d.open();
	}

	sendFiles() {
		let aonDocumental = this.getApplication();
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.SEND_FILES);
		d.setContentHTML('<aon-input id="sendDocumentsMail" description="Email"></aon-input>');
		d.addAcceptAction(() => {
			let mail = this.getElement('sendDocumentsMail');
			let aonDocumentalTable = this.getElement(this.TABLE);
			let message = {
				to: mail.value,
				documents: aonDocumentalTable.selected
			};
			sendDocumentMail(message).then(() => {});
		});
		d.open();
	}

	addDocumentalActions() {
		this.removeDocumentalActions();
		let aonDocumental = this.getApplication();
		let toolbar = this.getElement(aonDocumental.TOOLBAR);
		toolbar.addSeparator();
		if(this._roles.isDocumentalManager() || this._roles.isDocumentalPortal()){
			aonDocumental.addToolbarOption2(ACTION.EDIT_FILE, () => this.editFiles());
			aonDocumental.addToolbarOption2(ACTION.DELETE_FILE, () => this.removeFiles());
		}
		aonDocumental.addToolbarOption2(ACTION.DOWNLOAD_FILE, () => this.downloadFiles());
		aonDocumental.addToolbarOption2(ACTION.SEND_FILE, () => this.sendFiles());
	}

	removeDocumentalActions() {
		let aonDocumental = this.getApplication();
		let toolbar = this.getElement(aonDocumental.TOOLBAR);
		toolbar.removeSeparators();
		aonDocumental.removeToolbarOption(ACTION.DELETE_FILE);
		aonDocumental.removeToolbarOption(ACTION.EDIT_FILE);
		aonDocumental.removeToolbarOption(ACTION.DOWNLOAD_FILE);
		aonDocumental.removeToolbarOption(ACTION.SEND_FILE);
	}

	aonDocumentContextMenu(e, doc, i) {
		e.preventDefault();
		let rect = e.target.getBoundingClientRect();
    let x = e.clientX - rect.left;
		let y = e.clientY - rect.top;

	  const top  = rect.top + y;
	  const left = rect.left + x;

    let aonDocumental = this.getElement('aonDocumental');
		let d = document.getElementById(aonDocumental.OPTION_DIALOG);

		let send = ACTION.SEND_FILE;
		send.fn = () => {}; //this.send();

		let download = ACTION.DOWNLOAD_FILE;
		download.fn = () => {}; //this.download();

		let remove = ACTION.DELETE_FILE;
		remove.fn = () => {}; //this.remove();

		let actions = [send, download];
		if(this._roles.isDocumentalManager() || this._roles.isDocumentalPortal()) {
			actions.push(remove);
		}
		d.setMenuOptions(actions, top, left);
		d.open();
	}

	getFilter() {
		return this.hasAttribute('filter')
 			? JSON.parse(this.getAttribute('filter'))
			: {status: 'inbox'};
	}

	setFilter(filter) {
		return this.setAttribute('filter', JSON.stringify(filter));
	}
}
window.customElements.define('aon-documental-list', AonDocumentalList);
