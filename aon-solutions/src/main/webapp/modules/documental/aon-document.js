import { AonElement } from '../../components/AonElement.js';
import { ToolbarType } from '../../models/enums.js';
import { ASESOR_TYPE_OPTION, ENTERPRISE_TYPE_OPTION,
   EMPLOYEE_TYPE_OPTION } from './DocumentalEnums.js';
import { deleteFile, getCategories, getScopes, updateFile,
  getDomainUserRoles, openFileUrl } from '../../services/service.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { EVENT, MSG, TAG } from '../../environments/environments.js';
import * as ACTION from '../actions.js';
import { AonSelect } from '../../components/aon-select.js';
import '../../components/aon-toolbar.js';
import '../../components/aon-date.js';
import '../../components/aon-input.js';
import '../../components/aon-viewer.js';
import '../../components/aon-switch.js';
import '../../components/aon-card.js';


export class AonDocument extends AonElement {

  doc;
  _tags;
  _roles;

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
  }

  connectedCallback () {
    this.initialize();
    this.innerHTML = `
      <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${this.document.title}"> </aon-toolbar>
      <div style="display:flex;">
        <div id="${this.DATA}" class="aonSubContent" style="width:100%">
          <aon-card id="${this.DATA_CARD}" title="${MSG.FILE_DATA}"> </aon-card>
        </div>
        <div id="${this.FILE}" class="aonSubContent">

        </div>
      </div>
    `;

    getDomainUserRoles({}).then(r => {
      this._roles = new DomainUserRoles(r);
      this.build();
    });
  }

  initialize() {
    this.id = this.id || 'aonDocumentalSheet';
    this.TOOLBAR = this.id + 'Toolbar';
    this.DATA = this.id + 'Data';
    this.DATA_CARD = this.DATA + 'Card';
    this.FILE = this.id + 'File';
    this.doc = this.document;
    this._tags = [];
  }

  build() {
    this.doc = this.document;
    let fileDiv = this.getElement(this.FILE);
    fileDiv.style.display = 'block';
    fileDiv.style.width = '50%';
		fileDiv.innerHTML = `<aon-viewer type="${this.document.file.type}" file="${this.document.file.url}" width="${fileDiv.offsetWidth}"></aon-viewer>`;

    let dataDiv = this.getElement(this.DATA);
    dataDiv.style.width = '50%';

		if(localStorage.getItem('aon_solutions') === undefined || localStorage.getItem('aon_solutions') === null) {
      let offset1 = fileDiv.getBoundingClientRect();
      fileDiv.style.height = `calc(100vh - ${offset1.top + 2}px)`;

      let offset2 = dataDiv.getBoundingClientRect();
  		dataDiv.style.height = `calc(100vh - ${offset2.top + 2}px)`;
    }

    this.buildData();

    this.buildDocumentToolbar();
  }

  buildData() {
    let card = this.getElement(this.DATA_CARD);
    card.setContentHTML('');
		let table = this.createElement(TAG.TABLE);
		table.style.width = '100%';
		card.setContent(table);

    let tr = this.createElement(TAG.TR);
    table.appendChild(tr);

    let tdDate = this.createElement(TAG.TD);
    tdDate.setAttribute('colspan', '1');
		tdDate.innerHTML = `<aon-date id="date" title="${MSG.DATE}"></aon-date>`;
		tr.appendChild(tdDate);
		let date = document.getElementById('date');
    if(!this._roles.isDocumentalManager() && !this._roles.isDocumentalPortal()){
      date.readonly = 'true';
    }
    if(this.document.date) {
      let d = this.document.date.split('/');
      date.setDate(new Date(d[2], d[1] - 1, d[0]));
    }
    let tdConfidential = this.createElement(TAG.TD);
    tdConfidential.setAttribute('colspan', '1');
    tdConfidential.innerHTML = `<aon-switch id="confidential" title="${MSG.CONFIDENTIAL}"></aon-switch>`;
    tr.appendChild(tdConfidential);
    let confidential = this.getElement('confidential');
    confidential.disabled = !this._roles.isDocumentalManager() && !this._roles.isDocumentalPortal();
    confidential.checked = this.document.confidential;
    confidential.addEventListener(EVENT.CHANGE, () => this.updateConfidential(confidential.checked));

    let tr2 = this.createElement(TAG.TR);
    table.appendChild(tr2);

    let tdName = this.createElement(TAG.TD);
    tdName.setAttribute('colspan', '2');
		tdName.innerHTML = `<aon-input id="name" description="${MSG.NAME}"></aon-input>`;
		tr2.appendChild(tdName);
		let name = this.getElement('name');
    if(!this._roles.isDocumentalManager() && !this._roles.isDocumentalPortal()){
      name.readonly = 'true';
    }
    name.value = this.document.title;
		name.addEventListener(EVENT.CHANGE, () => this.updateName(name.value));

    let tr3 = this.createElement(TAG.TR);
    table.appendChild(tr3);

    // CATEGORY
    let tdCategory = this.createElement(TAG.TD);
    tdCategory.setAttribute('colspan', '1');
    let categorySelect = new AonSelect();
    categorySelect.id = 'category';
    if(!this._roles.isDocumentalManager() && !this._roles.isDocumentalPortal()){
      categorySelect.readonly = 'true';
    }
    categorySelect.title = MSG.CATEGORY;
    tdCategory.appendChild(categorySelect);
    tr3.appendChild(tdCategory);
    getCategories({domain: localStorage.getItem('aon_domain_id')}).then( categories => {
      categorySelect.setOptions(categories.map(c => {
        return {
          value: c.id,
          name: c.name
        }
      }));
      if(this.document.category)
        categorySelect.value = this.document.category.id;
      categorySelect.addEventListener(EVENT.SELECT, () => this.updateCategory(categorySelect.value));
    });

    // SCOPE
    let tdScope = this.createElement(TAG.TD);
    tdScope.setAttribute('colspan', '1');
    let scopeSelect = new AonSelect();
    scopeSelect.id = 'scope';
    if(!this._roles.isDocumentalManager() && !this._roles.isDocumentalPortal()){
      scopeSelect.readonly = 'true';
    }
    scopeSelect.title = MSG.SCOPE;
    tdScope.appendChild(scopeSelect);
    tr3.appendChild(tdScope);
    getScopes().then( scopes => {
      scopeSelect.setOptions(scopes.map(s => {
        return {
          value: s.id,
          name: s.name
        }
      }));

      if(this.document.scope)
        scopeSelect.value = this.document.scope.id;
      scopeSelect.addEventListener(EVENT.SELECT, () => this.updateScope(scopeSelect.value));
    });

    let tr4 = this.createElement(TAG.TR);
    table.appendChild(tr4);

    // TAG
    let tdTag = this.createElement(TAG.TD);
    tdTag.setAttribute('colspan', '1');

    let tagSelect = new AonSelect();
    tagSelect.id = 'tag';
    tagSelect.title = MSG.TAG;
    if(!this._roles.isDocumentalManager() && !this._roles.isDocumentalPortal()){
      tagSelect.readonly = 'true';
    }
    tdTag.appendChild(tagSelect);
    tr4.appendChild(tdTag);
    tagSelect.addEventListener(EVENT.SELECT, (event) => {
      this.addTag(event.detail);
      this.setTagsAvaible();
      tagSelect.clear();
    });

    let tdType = this.createElement(TAG.TD);
    tdType.setAttribute('colspan', '1');

    let typeSelect = new AonSelect();
    typeSelect.id = 'type';
    typeSelect.title = MSG.TYPE;
    if(!this._roles.isDocumentalManager() && !this._roles.isDocumentalPortal()){
      typeSelect.readonly = 'true';
    }
    let typeOptions = EMPLOYEE_TYPE_OPTION;
    if(this._roles.isDocumentalManager()) {
      typeOptions = ASESOR_TYPE_OPTION;
    } else if(this._roles.isDocumentalPortal()){
      typeOptions = ENTERPRISE_TYPE_OPTION;
    }
    typeSelect.setOptions(typeOptions);
    tdType.appendChild(typeSelect);
    tr4.appendChild(tdType);
    typeSelect.value = this.document.type;
    typeSelect.addEventListener(EVENT.SELECT, () => this.updateType(typeSelect.value));

    let tr5 = this.createElement(TAG.TR);
    table.appendChild(tr5);


    let containerTags = this.createElement(TAG.DIV);
    containerTags.style.display = "flex";
    containerTags.style.flexWrap = "wrap";
    containerTags.id = "containerTags";
    card.setContent(containerTags);
    this.document.tags.forEach((item) => {
      this.addTag(item);
    });
    //set tags avaibles
    this.setTagsAvaible();
  }

  addTag(tag){
    let containerTags = this.getElement("containerTags");
    let t = {
      id: tag.id || tag.value,
      name: tag.name
    }
    this._tags.push(tag);
    let divTag = this.createElement(TAG.DIV);
    divTag.id = 'tag' + t.id;
    divTag.innerHTML = `
      <span style="background-color: #eee;padding:3px;"> ${tag.name}</span>
      <i id='closeTag${t.id}'class="material-icons" style="font-size:1rem;cursor: pointer;">close</i>
    `;
    containerTags.appendChild(divTag);
    this.getElement('closeTag' + t.id).addEventListener(EVENT.CLICK, () => {
      this._tags.forEach((item, i) => {
        if(t.id === item.id || t.id === item.value) {
          this._tags.splice(i, 1);
          this.getElement('tag' + t.id).remove();
          this.setTagsAvaible();
          this.save();
        }
      });
    });
    this.save();
  }

  setTagsAvaible(){
    const tagSelect = this.getElement("tag");
    if(tagSelect){
      const applicationParent = this.getApplication().getParent();
      const tags = applicationParent._tags ? applicationParent._tags : [];
      const newTags = tags.filter(({name}) => !this._tags.some(it=> it.name.includes(name)));
      tagSelect.setOptions(newTags);
    }
  }


  buildDocumentToolbar() {
    // let aonDocumental = this.getApplication();
    let documentToolbar = this.getElement(this.TOOLBAR);
    documentToolbar.removeButtons();
    if(!this.isMobile()){
      documentToolbar.addButton2(ACTION.NEXT, () => this.next());
      documentToolbar.addButton2(ACTION.PREVIOUS, () => this.previous());

      documentToolbar.addSeparator();
      if(this._roles.isDocumentalManager() || this._roles.isDocumentalPortal()){
        documentToolbar.addButton2(ACTION.DELETE_FILE, () => this.remove());
      }
      //  documentToolbar.addButton2(ACTION.SEND_FILE, () => this.send());
      documentToolbar.addButton2(ACTION.DOWNLOAD_FILE, () => this.download());
    }
    documentToolbar.addButton2(ACTION.BACK, () => this.back());
  }


  back() {
    let aonDocumental = this.getApplication();
    aonDocumental.getParent().aonDocumentalList();
  }

  next() {
    let aonDocumental = this.getApplication();
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.NEXT);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
  }

  previous() {
    let aonDocumental = this.getApplication();
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.PREVIOUS);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
  }

  send() {
    let aonDocumental = this.getApplication();
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.SEND_FILE);
		d.setContentHTML('Esta opción está en desarrollo...');
		d.addAcceptAction(() => {});
		d.open();
  }

  remove() {
    let aonDocumental = this.getApplication();
    let d = document.getElementById(aonDocumental.DIALOG);
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle(MSG.DELETE_FILE);
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
    openFileUrl(this.document.file.url);
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

  updateType(type) {
    this.doc.type = type;
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
      type: this.doc.type,
      tags: this._tags.map(t => t.id || t.value)
    }
    updateFile(d);
  }

}

window.customElements.define('aon-document',  AonDocument);
