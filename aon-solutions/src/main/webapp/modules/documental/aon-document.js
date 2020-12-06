import {AonElement} from '../../components/AonElement.js';
import {ToolbarType} from '../../models/enums.js';
import {DocumentalAction} from './DocumentalEnums.js';
import {deleteFile} from '../../services/service.js';
import '../../components/aon-viewer.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonDocument extends AonElement {

  TOOLBAR;
  DATA;
  DATA_CARD;
  FILE;

  get document() {
    return JSON.parse(this.getAttribute('document'));
  }

  set document(value) {
    this.setAttribute('document', JSON.stringify(value));
  }

  constructor () {
    super();
    this.id = this.id || 'aonDocumentalSheet';
    this.TOOLBAR = this.id + 'Toolbar';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.File = this.id + 'File';
  }

  connectedCallback () {
    this.innerHTML = `
      <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${this.document.title}"> </aon-toolbar>
      <div style="display:flex;">
        <div id="${this.DATA}" class="aonSubContent" style="width:100%">
          <aon-card id="${this.DATA_CARD}" title="${MSG.AON_MSG_FILE_DATA}"> </aon-card>
        </div>
        <div id="${this.FILE}" class="aonSubContent">

        </div>
      </div>
    `;

    this.build();
  }

  build() {
    let fileDiv = this.getElement(this.FILE);
    fileDiv.style.display = 'block';
    fileDiv.style.width = '50%';
		fileDiv.innerHTML = `<aon-viewer type="${this.document.file.type}" file="${this.document.file.url}" width="${fileDiv.offsetWidth}"><aon-viewer>`;
    let dataDiv = this.getElement(this.DATA);
    dataDiv.style.width = '50%';

    this.buildData();

    if(!this.isMobile())
      this.buildDocumentToolbar();
  }

  buildData() {
    let card = this.getElement(this.DATA_CARD);
    card.setContentHTML('');
		let table = document.createElement('table');
		table.style.width = '100%';
		card.setContent(table);

    let tr = document.createElement('tr');
    table.appendChild(tr);

    let tdDate = document.createElement('td');
    tdDate.setAttribute('colspan', '1');
		tdDate.innerHTML = `<aon-date id="date" title="${MSG.AON_MSG_DATE}"></aon-date>`;
		tr.appendChild(tdDate);
		let date = document.getElementById('date');
    let d = this.document.date.split('/');
    date.setDate(new Date(d[2], d[1] - 1, d[0]));

    let tdConfidential = document.createElement('td');
    tdConfidential.setAttribute('colspan', '1');
    tdConfidential.innerHTML = `<aon-switch id="confidential" title="${MSG.AON_MSG_CONFIDENTIAL}"></aon-switch>`;
    tr.appendChild(tdConfidential);

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    let tdName = document.createElement('td');
    tdName.setAttribute('colspan', '2');
		tdName.innerHTML = `<aon-input id="name" description="${MSG.AON_MSG_NAME}"></aon-input>`;
		tr2.appendChild(tdName);
		let name = this.getElement('name');
    name.value = this.document.title;

    let tr3 = document.createElement('tr');
    table.appendChild(tr3);

    // CATEGORY
    let tdCategory = document.createElement('td');
    tdCategory.setAttribute('colspan', '1');
    tdCategory.innerHTML = `<aon-select id="category" title="${MSG.AON_MSG_CATEGORY}"></aon-select>`;
    tr3.appendChild(tdCategory);
    // let category = document.getElementById('category');
    // category.options = JSON.stringify(getInvoiceCategories(this._invoice.type));
    // category.value = this._invoice.category;
    // category.addEventListener('select', () => this.update('category'));

    // CATEGORY
    let tdScope = document.createElement('td');
    tdScope.setAttribute('colspan', '1');
    tdScope.innerHTML = `<aon-select id="scope" title="${MSG.AON_MSG_SCOPE}"></aon-select>`;
    tr3.appendChild(tdScope);
  }

  buildDocumentToolbar() {
    let aonDocumental = this.getElement('aonDocumental');
    let documentToolbar = this.getElement(this.TOOLBAR);
    documentToolbar.removeButtons();

    documentToolbar.addButton2(DocumentalAction.NEXT, () => this.next());
    documentToolbar.addButton2(DocumentalAction.PREVIOUS, () => this.previous());

    documentToolbar.addSeparator();

    documentToolbar.addButton2(DocumentalAction.DELETE, () => this.remove());
//  documentToolbar.addButton2(DocumentalAction.SEND, () => this.send());
    documentToolbar.addButton2(DocumentalAction.DOWNLOAD, () => this.download());
    documentToolbar.addButton2(DocumentalAction.BACK, () => this.back());
  }


  back() {
    let ad = document.querySelector('aon-documental');
		ad.aonDocumentalList();
  }

  next() {
    let aonDocumental = this.getElement('aonDocumental');
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_NEXT);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
  }

  previous() {
    let aonDocumental = this.getElement('aonDocumental');
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_PREVIOUS);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
  }

  send() {
    let aonDocumental = this.getElement('aonDocumental');
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.AON_MSG_SEND_FILE);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
  }

  remove() {
    let aonDocumental = this.getElement('aonDocumental');
    let d = document.getElementById(aonDocumental.DIALOG);
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle(MSG.AON_MSG_DELETE_FILE);
    d.setContentHTML(`Estás seguro de eliminar el Fichero ${this.document.title}`);
    d.addAcceptAction(() => {
      let data = {
        id: [this.document.id],
        attach_type: 'registry'
      };
      deleteFile(data).then(() => {
        this.back();
      });
    });
    d.open();
  }

  download() {
    open(this.document.file.url);
  }


}

window.customElements.define('aon-document',  AonDocument);
