import {AonElement} from '../../components/AonElement.js';
import { DocumentalSidenav, ASESOR_TYPE_OPTION,
  ENTERPRISE_TYPE_OPTION, EMPLOYEE_TYPE_OPTION } from './DocumentalEnums.js';
import {getCategories, getTags, createTag, createCategory, editCategory,
    deleteCategory, editTag, deleteTag, uploadFileDocumental, getScopes,
    getDomainUserRoles, getDocument} from '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import {AonSelect} from '../../components/aon-select.js';
import { MSG, MATERIAL_ICONS } from '../../environments/environments.js';
import * as ACTION from '../actions.js';
import './aon-documental-list.js';
import './aon-document.js';
import './aon-mobile-documental-list.js';
import './aon-mobile-document.js';
import '../../components/aon-application.js';
import '../../components/aon-input.js';



export class AonDocumental extends AonElement {
    _filter;
    _tags;
    _categories;
    _scopes;

    dur;

    DOCUMENTAL;
  	INPUTFILE;

  	constructor () {
  		super();
    }

    connectedCallback () {
      this.initialize();
      this.innerHTML = `
        <aon-application id="${this.DOCUMENTAL}" title="${MSG.DOCUMENTARY}"></aon-application>
        <input id="${this.INPUTFILE}" style='display:none;' type='file' name='file' multiple>
      `;
      getDomainUserRoles({}).then(r => {
        this.dur = new DomainUserRoles(r);
        this.build();
      });
    }

    initialize() {
      this._tags =[];
      this.DOCUMENTAL = 'aonDocumental';
      this.INPUTFILE = this.DOCUMENTAL + 'InputFile';
      this._filter = {
        type: 'all',
        page:1,
        per_page:30,
        domain: localStorage.getItem('aon_domain_id')
      };
    }

    getDur() {
      return this.dur;
    }

    build(){
      let aonDocumental = this.getApplication();
      if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
        aonDocumental.drag_and_drop = true;
      }

      let input = this.getElement(this.INPUTFILE);

      input.addEventListener('change', () => this.upload(input.files));

  		aonDocumental.addEventListener('drop', (event) => {
  			if(event && event.dataTransfer && event.dataTransfer.files){
  				this.upload(event.dataTransfer.files);
  			}
  		});
      if(this.isMobile()) {
        if(this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager())
          aonDocumental.addFloatOption(ACTION.UPLOAD_FILE, () => this.addDocumentalFile());
      } else {
        if(this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager()){
          aonDocumental.addToolbarOption2(ACTION.UPLOAD_FILE, () => this.addDocumentalFile());
        }
        aonDocumental.addSearchOption();
        aonDocumental.addEventListener('search', (event) => this.search(event.detail));
      }

      if(this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager())
        this.addDocumentOptions();
      this.addTypeOptions();
      this.addCategoryOptions();
      this.addTagOptions();
      this.loadScopes();

      if(this.value){
        this.aonDocumentById(this.value);
      } else this.aonDocumentalList();
    }

    addDocumentOptions() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let documentOptions = [
        {
          name: MSG.ALL_FILES,
          icon: 'insert_drive_file',
          fn: () => {
            this._filter.category = undefined;
            this._filter.tag = undefined;
            this._filter.type = 'all';
            this.aonDocumentalList();
          }
        }
      ];
      aonDocumental.addSidenavOptions2(DocumentalSidenav.DOCUMENTS, documentOptions);
    }

    addTypeOptions() {
      if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
        let typeOptions = [{
            name: MSG.ENTERPRISE,
            icon: MATERIAL_ICONS.BUSINESS,
            fn: () => {
              this._filter.category = undefined;
              this._filter.tag = undefined;
              this._filter.type = 'enterprise';
              this.aonDocumentalList();
            }
        },{
          name: MSG.EMPLOYEE,
          icon: MATERIAL_ICONS.PERSON,
          fn: () => {
            this._filter.category = undefined;
            this._filter.tag = undefined;
            this._filter.type = 'employee';
            this.aonDocumentalList();
          }
        }];
        if(this.getDur().isDocumentalManager()) {
          typeOptions.push({
            name: MSG.ASESOR,
            icon: 'work',
            fn: () => {
              this._filter.category = undefined;
              this._filter.tag = undefined;
              this._filter.type = 'asesor';
              this.aonDocumentalList();
            }
          });
        }
        let application = this.getApplication();
        application.addSidenavOptions2(DocumentalSidenav.TYPES, typeOptions);
      }
    }

    addCategoryOptions() {
      let application = this.getApplication();
      if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()){
        application.addSidenavOptions2(DocumentalSidenav.CATEGORIES, [], () => this.createCategory());
      } else application.addSidenavOptions2(DocumentalSidenav.CATEGORIES, []);
      this.loadCategories();
    }

    loadCategories() {
      let application = this.getApplication();
      getCategories({domain: localStorage.getItem('aon_domain_id')}).then( categories => {
        this._categories = categories.map(c => {
          return {
            value: c.id,
            name: c.name
          }
        });
        this.clearElementById(application.SIDENAV + DocumentalSidenav.CATEGORIES.id + 'List');
        categories.forEach((item, i) => {
          let option = {
            name: item.name,
            icon: 'label',
            fn: () => {
              this._filter.tag = undefined;
              this._filter.category = item.id;
              this.aonDocumentalList();
            }
          };
          if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()){
            option.actions = [{
                id: 'Delete',
                icon: 'delete',
                action: () => this.deleteCategory(item)
              },{
                id: 'Edit',
                icon: 'edit',
                action: () => this.editCategory(item)
              }
            ];
          }
          application.addSidenavOptionsListValue(DocumentalSidenav.CATEGORIES, option);
        });
      });
    }

    loadScopes() {
      getScopes().then( scopes => {
        this._scopes = scopes.map(s => {
          return {
            value: s.id,
            name: s.name
          }
        });
      });
    }

    createCategory() {
      let d = document.getElementById(this.getApplication().DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.ADD_CATEGORY);
      d.setContentHTML(`<aon-input id="aonDocumentalAddCategory" description="${MSG.CATEGORY}"> </aon-input>`);
      d.addAcceptAction(() => {
        let input = this.getElement('aonDocumentalAddCategory');
        if(!input.value.isEmpty()){
          createCategory({name: input.value}).then(() => {
            this.loadCategories();
          });
        }
      });
      d.open();
    }

    editCategory(category) {
      let d = document.getElementById(this.getApplication().DIALOG);
      d.clear();
  		if(!this.isMobile()) d.width = '400px';
  		d.setTitle(MSG.EDIT_CATEGORY);
  		d.setContentHTML(`<aon-input id="aonDocumentalAddCategory" description="${MSG.CATEGORY}"> </aon-input>`);
  		d.addAcceptAction(() => {
        let input = this.getElement('aonDocumentalAddCategory');
        if(!input.value.isEmpty()){
          category.name = input.value;
          editCategory(category).then(() => {
            this.loadCategories();
          });
        }
      });
  		d.open();
    }

    deleteCategory(category) {
      let d = document.getElementById(this.getApplication().DIALOG);
      d.clear();
  		if(!this.isMobile()) d.width = '400px';
  		d.setTitle(MSG.DELETE_CATEGORY);
      d.setContentHTML(`Estás seguro de eliminar la Categoría ${category.name}`);
  		d.addAcceptAction(() => {
        deleteCategory(category).then(() => {
          this.loadCategories();
        });
      });
  		d.open();
    }

    addTagOptions() {
      let application = this.getApplication();
      if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()){
        application.addSidenavOptions2(DocumentalSidenav.TAGS, [], () => this.createTag());
      } else application.addSidenavOptions2(DocumentalSidenav.TAGS, []);
      this.loadTags();
    }

    loadTags() {
      let application = this.getApplication();
      getTags({domain: localStorage.getItem('aon_domain_id')}).then( tags => {
        this._tags = tags.map(t => {
          return {
            value: t.id,
            name: t.name
          }
        });
        this.clearElementById(application.SIDENAV + DocumentalSidenav.TAGS.id + 'List');
        tags.forEach((item, i) => {
          let option = {
            name: item.name,
            icon: 'label',
            fn: () => {
              this._filter.category = undefined;
              this._filter.tag = item.id;
              this.aonDocumentalList();
            }
          };
          if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()){
            option.actions = [{
                id: 'Delete',
                icon: 'delete',
                action: () => this.deleteTag(item)
              },{
                id: 'Edit',
                icon: 'edit',
                action: () => this.editTag(item)
              }
            ];
          }

          application.addSidenavOptionsListValue(DocumentalSidenav.TAGS, option);
        });
      });
    }

    createTag() {
      let d = document.getElementById(this.getApplication().DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.ADD_TAG);
      d.setContentHTML(`<aon-input id="aonDocumentalAddTag" description="${MSG.TAG}"> </aon-input>`);
      d.addAcceptAction(() => {
        let input = this.getElement('aonDocumentalAddTag');
        if(!input.value.isEmpty()){
          createTag({name: input.value}).then(() => {
            this.loadTags();
          });
        }
      });
      d.open();
    }

    editTag(tag) {
      let d = document.getElementById(this.getApplication().DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.EDIT_TAG);
      d.setContentHTML(`<aon-input id="aonDocumentalAddTag" description="${MSG.TAG}"> </aon-input>`);
      d.addAcceptAction(() => {
        let input = this.getElement('aonDocumentalAddTag');
        if(!input.value.isEmpty()){
          tag.name = input.value;
          editTag(tag).then(() => {
            this.loadTags();
          });
        }
      });
      d.open();
    }

    deleteTag(tag) {
      let d = document.getElementById(this.getApplication().DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.DELETE_TAG);
      d.setContentHTML(`Estás seguro de eliminar la Etiqueta ${tag.name}`);
      d.addAcceptAction(() => {
        deleteTag(tag).then(() => {
          this.loadTags();
        });
      });
      d.open();
    }

    search(value) {
      this._filter.description = value;
      this.aonDocumentalList();
    }

  	aonDocumentalList(filter) {
  		filter = filter || this._filter;
  		this._filter = filter;
  		let documentalList = this.getElement('aonDocumentalList');
  		if(documentalList) {
  			documentalList.setFilter(filter);
  			documentalList.init();
  		} else {
        let application = this.getApplication();
  			if(this.isMobile()) {
          application.setContentHTML(filter
  					? `<aon-mobile-documental-list id="aonDocumentalList" filter='${JSON.stringify(filter)}'></aon-mobile-documental-list>`
  					: `<aon-mobile-documental-list id="aonDocumentalList"></aon-mobile-documental-list>`);
  			}  else {
  				application.setContentHTML(filter
  					? `<aon-documental-list id="aonDocumentalList" filter='${JSON.stringify(filter)}'></aon-documental-list>`
  					: `<aon-documental-list id="aonDocumentalList"></aon-documental-list>`);
  			}
  		}
  	}

    aonDocumentById(id) {
      getDocument(id)
        .then(doc => this.aonDocument(doc))
        .catch(error =>this.showToast(error));
    }

    aonDocument(doc) {
      let application = this.getApplication();
      if(this.isMobile()) {
        application.setContentHTML(`<aon-mobile-document document='${JSON.stringify(doc)}'> </aon-mobile-invoice>`);
      } else {
        application.setContentHTML(`<aon-document document='${JSON.stringify(doc)}'> </aon-document>`);
      }
    }

    addDocumentalFile() {
      let el = this.getElement(this.INPUTFILE);
      el.click();
    }

    upload(files) {
      let d = document.getElementById(this.getApplication().DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.UPLOAD_FILE);
      d.setContent(this.uploadOption(files.length === 1));
      let selType = this.getElement("aonDocumentalUploadType");
      selType.value = this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()
        ? 'enterprise' : 'employee';
      d.addAcceptAction(() => {
        let data = {
          category: this.getElement("aonDocumentalUploadCategory").value,
          scope: this.getElement("aonDocumentalUploadScope").value,
          tag: this.getElement("aonDocumentalUploadTag").value,
          type: this.getElement("aonDocumentalUploadType").value
        }
        for(let i = 0; i < files.length; i++) {
          const READER = new FileReader();
          READER.readAsDataURL(files[i]);
          READER.onload = (_event) => {
            this.attach(READER.result, files[i], data);
          };
        }
      });
      d.open();
    }

    uploadOption(one){
      let table = document.createElement('table');
      table.style.width = '100%';

      // if(one) {
      //   let tr1 = document.createElement('tr');
      //   table.appendChild(tr2);
      //
      //   let tdName = document.createElement('td');
      //   tdName.setAttribute('colspan', '1');
  		//   tdName.innerHTML = `<aon-input id="name" description="${MSG.NAME}"></aon-input>`;
  		//   tr1.appendChild(tdName);
  		//   let name = this.getElement('name');
      //   name.value = this.document.title;
      // }
      let tr2 = document.createElement('tr');
      table.appendChild(tr2);

      // CATEGORY
      let tdCategory = document.createElement('td');
      tdCategory.setAttribute('colspan', '1');

      let selCat = new AonSelect();
      selCat.id = "aonDocumentalUploadCategory";
      selCat.title = MSG.CATEGORY;
      selCat.options = JSON.stringify(this._categories);
      tdCategory.appendChild(selCat);

      tr2.appendChild(tdCategory);

      let tr3 = document.createElement('tr');
      table.appendChild(tr3);
      // SCOPE
      let tdScope = document.createElement('td');
      tdScope.setAttribute('colspan', '1');

      let selScp = new AonSelect();
      selScp.id = "aonDocumentalUploadScope";
      selScp.title = MSG.SCOPE;
      selScp.options = JSON.stringify(this._scopes);
      tdScope.appendChild(selScp);

      tr3.appendChild(tdScope);

      let tr4 = document.createElement('tr');
      table.appendChild(tr4);

      // TAG

      let tdTag = document.createElement('td');
      tdTag.setAttribute('colspan', '1');
      let selTag = new AonSelect();
      selTag.id = "aonDocumentalUploadTag";
      selTag.title = MSG.TAG;
      selTag.options = JSON.stringify(this._tags);
      tdTag.appendChild(selTag);

      tr4.appendChild(tdTag);

      let tr5 = document.createElement('tr');
      table.appendChild(tr5);

      // TYPE

      let tdType = document.createElement('td');
      tdType.setAttribute('colspan', '1');
      let selType = new AonSelect();
      selType.id = "aonDocumentalUploadType";
      selType.title = MSG.TYPE;

      let typeOptions = EMPLOYEE_TYPE_OPTION;
      if(this.getDur().isDocumentalManager()) {
        typeOptions = ASESOR_TYPE_OPTION;
      } else if(this.getDur().isDocumentalPortal()){
        typeOptions = ENTERPRISE_TYPE_OPTION;
      }
      selType.setOptions(typeOptions);

      tdType.appendChild(selType);
      tr5.appendChild(tdType);
      return table;
    }

    attach(fileDataUri,  file, d){
      if (fileDataUri.length > 0) {
        const base64File = fileDataUri.split(',')[1];
        const data = {
          content: base64File,
          contentType: file.type,
          contentEncoding: 'base64',
          contentName: file.name,
          contentSize: file.size,
          category: d.category,
          tag: d.tag,
          scope: d.scope,
          type: d.type
        };

        const application = this.getApplication();
        application.startLoader();

        uploadFileDocumental(data).then((r) => {
          application.stopLoader();
          this.aonDocumentalList()
        });
      }
    }
}
window.customElements.define('aon-documental', AonDocumental);
