import {AonElement} from '../../components/AonElement.js';
import {ToolbarType} from '../../models/enums.js';
import {DocumentalAction} from './DocumentalEnums.js';
import {deleteFile, getCategories, getScopes, getTags, updateFile} from '../../services/service.js';
import '../../components/aon-date.js';
import '../../components/aon-viewer.js';
import '../../components/aon-card.js';

import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonDocument extends AonElement {

  doc;
  _tags;

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
    this.FILE = this.id + 'File';
    this.doc = this.document;
    this._tags = [];
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
    this.doc = this.document;
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
    if(this.document.date) {
      let d = this.document.date.split('/');
      date.setDate(new Date(d[2], d[1] - 1, d[0]));
    }   
    let tdConfidential = document.createElement('td');
    tdConfidential.setAttribute('colspan', '1');
    tdConfidential.innerHTML = `<aon-switch id="confidential" title="${MSG.AON_MSG_CONFIDENTIAL}"></aon-switch>`;
    tr.appendChild(tdConfidential);
    let confidential = this.getElement('confidential');
    confidential.checked = this.document.confidential;
    confidential.addEventListener('change', () => this.updateConfidential(confidential.checked));

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    let tdName = document.createElement('td');
    tdName.setAttribute('colspan', '2');
		tdName.innerHTML = `<aon-input id="name" description="${MSG.AON_MSG_NAME}"></aon-input>`;
		tr2.appendChild(tdName);
		let name = this.getElement('name');
    name.value = this.document.title;
		name.addEventListener('change', () => this.updateName(name.value));

    let tr3 = document.createElement('tr');
    table.appendChild(tr3);

    // CATEGORY
    let tdCategory = document.createElement('td');
    tdCategory.setAttribute('colspan', '1');
    tdCategory.innerHTML = `<aon-select id="category" title="${MSG.AON_MSG_CATEGORY}"></aon-select>`;
    tr3.appendChild(tdCategory);
    getCategories({domain: localStorage.getItem('aon_domain_id')}).then( categories => {
      let category = document.getElementById('category')
      let cat = categories.map(c => {
        return {
          value: c.id,
          name: c.name
        }
      });
      category.options = JSON.stringify(cat);
      if(this.document.scope)
        category.value = this.document.category.id;
      category.addEventListener('select', () => this.updateCategory(category.value));
    });

    // SCOPE
    let tdScope = document.createElement('td');
    tdScope.setAttribute('colspan', '1');
    tdScope.innerHTML = `<aon-select id="scope" title="${MSG.AON_MSG_SCOPE}"></aon-select>`;
    tr3.appendChild(tdScope);
    getScopes({domain: localStorage.getItem('aon_domain_id')}).then( scopes => {
      let scope = document.getElementById('scope');
      let scp = scopes.map(s => {
        return {
          value: s.id,
          name: s.name
        }
      });
      scope.options = JSON.stringify(scp);
      if(this.document.scope)
        scope.value = this.document.scope.id;
      scope.addEventListener('select', () => this.updateScope(scope.value));
    });

    let tr4 = document.createElement('tr');
    table.appendChild(tr4);

    // TAG
    let tdTag = document.createElement('td');
    tdTag.setAttribute('colspan', '2');
    tdTag.innerHTML = `<aon-select id="tag" title="${MSG.AON_MSG_TAG}"></aon-select>`;
    tr4.appendChild(tdTag);
    getTags({domain: localStorage.getItem('aon_domain_id')}).then( tags => {
      let tag = document.getElementById('tag')
      let t = tags.map(c => {
        return {
          value: c.id,
          name: c.name
        }
      });
      tag.options = JSON.stringify(t);
      tag.addEventListener('select', (event) => {
        this.addTag(event.detail);
        tag.value = '';
      });
    });

    let tr5 = document.createElement('tr');
    table.appendChild(tr5);

    let tdTags = document.createElement('td');
    tdTags.setAttribute('colspan', '2');
    tdTags.innerHTML = `<table>
      <tr id='tags'>

      </tr>
    </table>`;
    tr5.appendChild(tdTags);
    this.document.tags.forEach((item, i) => {
      this.addTag(item);
    });
  }

  addTag(tag){
    let t = {
      id: tag.id || tag.value,
      name: tag.name
    }
    let bool = true;
    this._tags.forEach((item, i) => {
      if(t.id === item.id || t.id === item.value) {
        bool = false;
      }
    });

    if(bool) {
      this._tags.push(tag);
      let td = this.createElement('td');
      td.id = 'tag' + t.id;
      td.innerHTML = `
        <span style="background-color: #eee;padding:3px;"> ${tag.name}</span>
        <i id='closeTag${t.id}'class="material-icons" style="font-size:1rem;cursor: pointer;">close</i>
      `;
      this.getElement('tags').appendChild(td);
      this.getElement('closeTag' + t.id).addEventListener('click', () => {
        this._tags.forEach((item, i) => {
          if(t.id === item.id || t.id === item.value) {
        		this._tags.splice(i, 1);
            this.getElement('tag' + t.id).remove();
            this.save();
          }
        });
      });
      this.save();
    }

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

  updateCategory(category) {
    if(!this.doc.category)
      this.doc.category = {};
    this.doc.category.id = category;
    this.save();
  }

  updateScope(scope) {
    if(!this.doc.scope)
      this.doc.scope = {};
    this.doc.scope.id = category;
    this.save();
  }

  updateConfidential(confidential) {
    this.doc.confidential = confidential;
    this.save();
  }

  updateName(name) {
    this.doc.title = name;
    this.save();
  }

  save() {
    let d = {
      id: this.doc.id,
      name: this.doc.title,
      confidential: this.doc.confidential,
      category: this.doc.category ? this.doc.category.id : undefined,
      scope: this.doc.scope ? this.doc.scope.id : undefined,
      tags: this._tags.map(t => t.id || t.value)
    }
    updateFile(d);
  }

}

window.customElements.define('aon-document',  AonDocument);
