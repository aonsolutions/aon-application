import {AonElement} from '../../components/AonElement.js';
import {DocumentalAction} from './DocumentalEnums.js';
import {getDocuments, downloadDocuments, sendDocumentMail, updateFiles,
	getDomainUserRoles} from '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

import '../../components/aon-table.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";

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
		this.innerHTML = `
			<aon-table id='${this.TABLE}' selectable='true'></aon-table>
		`;
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
		let aonDocumentalTable = this.getElement(this.TABLE);
		aonDocumentalTable.addColumn(MSG.AON_MSG_DATE, 'date', 'date', '20%');
		aonDocumentalTable.addColumn(MSG.AON_MSG_NAME, 'string', 'title', '60%');
		aonDocumentalTable.addColumn(MSG.AON_MSG_SIZE, 'string', 'size', '15%');

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
		let ad = document.querySelector('aon-documental');
		ad.aonDocument(doc);
	}

	downloadFiles() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		let data = {
			domain_id: localStorage.getItem('aon_domain_id'),
			domain_name: localStorage.getItem('aon_domain_name'),
			domain_login: localStorage.getItem('aon_domain_login'),
			ids: aonDocumentalTable.selected.map(r => r.id),
			type: this.getFilter().type
		};
		let json = btoa(JSON.stringify(data));
		downloadDocuments(json);
	}

	editFiles() {
		let aonDocumental = this.getElement('aonDocumental');
		let parent = document.querySelector('aon-documental');
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_EDIT_FILES);
		d.setContent(parent.uploadOption());
		// d.setContentHTML('Esta opción está en desarrollo...');
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

	sendFiles() {
		let aonDocumental = this.getElement('aonDocumental');
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_SEND_FILES);
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
		let ad = document.querySelector('aon-documental');
		let aonDocumental = this.getElement(ad.DOCUMENTAL);
		let toolbar = this.getElement(aonDocumental.TOOLBAR);
		toolbar.addSeparator();
		if(this._roles.isDocumentalManager() || this._roles.isDocumentalPortal())
			aonDocumental.addToolbarOption2(DocumentalAction.EDIT, () => this.editFiles());
		aonDocumental.addToolbarOption2(DocumentalAction.DOWNLOAD, () => this.downloadFiles());
		aonDocumental.addToolbarOption2(DocumentalAction.SEND, () => this.sendFiles());
	}

	removeDocumentalActions() {
		let ad = document.querySelector('aon-documental');
		let aonDocumental = this.getElement(ad.DOCUMENTAL);
		let toolbar = this.getElement(aonDocumental.TOOLBAR);
		toolbar.removeSeparators();
		aonDocumental.removeToolbarOption(DocumentalAction.EDIT);
		aonDocumental.removeToolbarOption(DocumentalAction.DOWNLOAD);
		aonDocumental.removeToolbarOption(DocumentalAction.SEND);
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

		let send = DocumentalAction.SEND;
		send.fn = () => {}; //this.send();

		let download = DocumentalAction.DOWNLOAD;
		download.fn = () => {}; //this.download();

		let remove = DocumentalAction.DELETE;
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
