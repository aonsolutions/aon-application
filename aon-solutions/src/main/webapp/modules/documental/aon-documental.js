import {AonElement} from '../../components/AonElement.js';
import { DocumentalSidenav, ASESOR_TYPE_OPTION,
  ENTERPRISE_TYPE_OPTION, EMPLOYEE_TYPE_OPTION } from './DocumentalEnums.js';
import {getCategories, getTags, createTag, createCategory, editCategory,
    deleteCategory, editTag, deleteTag, uploadFileDocumental, getScopes,
    getDomainUserRoles, getDocument} from '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import {AonSelect} from '../../components/aon-select.js';
import { MSG, MATERIAL_ICONS, EVENT } from '../../environments/environments.js';
import * as ACTION from '../actions.js';
import { AonInput } from '../../components/aon-input.js';
import { getReader } from '../../services/utils.js';
import Apps from '../../services/app.js';
import { AonApplication } from '../../components/aon-application.js';

import './aon-documental-list.js';
import './aon-document.js';
import './aon-mobile-documental-list.js';
import './aon-mobile-document.js';
import '../../css/aon-mobile.css';
import 'aoncss';
import { uploadOption } from './DocumentalUtils.js';
import { AonUploadToast } from '../../components/aon-upload-toast.js';

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

    //   this.innerHTML = `
    //   <aon-application id="${this.DOCUMENTAL}" title="${}" drag_and_drop='true'></aon-application>
    //   <input id="${this.INPUTFILE}" style='display:none;' type='file' name='file' multiple>
    // `;

    
      let aonApplication = new AonApplication();
      aonApplication.setAttribute("drag_and_drop", true);
      this.createApplication(this.DOCUMENTAL, MSG.DOCUMENTARY, aonApplication);

      let input = document.createElement("input");
      input.id = this.INPUTFILE;
      input.style.display = "none";
      input.type = "file";
      input.name = "file";
      input.multiple = true;
      this.appendChild(input);
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

      input.addEventListener(EVENT.CHANGE, () => this.upload(input.files));

      aonDocumental.addEventListener(EVENT.AON_APPLICATION_DROP, (e) => this.upload(e.detail));

      if(this.isMobile()) {
        if(this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager())
          aonDocumental.addFloatOption(ACTION.UPLOAD_FILE, () => this.addDocumentalFile());
      } else {
        if(this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager()){
          aonDocumental.addToolbarOption2(ACTION.UPLOAD_FILE, () => this.addDocumentalFile());
        }
        const btnSearch = aonDocumental.addSearchOption();
        btnSearch.addEventListener(EVENT.SEARCH, (event) => this.search(event.detail));
      }

      if(this.isMobile()){
        this.getApplication().addMobileSidenavHeader(Apps.DOCUMENTAL);
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
      let data = DocumentalSidenav.DOCUMENTS;
      data.options = documentOptions;
      aonDocumental.addSidenavOptions3(data);
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
        let data = DocumentalSidenav.TYPES;
        data.options = typeOptions;
        application.addSidenavOptions3(data);
      }
    }

    addCategoryOptions() {
      let application = this.getApplication();
      let data = DocumentalSidenav.CATEGORIES;
      data.options = [];
      if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()){
        application.addSidenavOptions3(data, () => this.createCategory());
      } else application.addSidenavOptions3(data);
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
      let input = new AonInput();
      input.id ="aonDocumentalAddCategory";
      input.description = MSG.CATEGORY;
      d.setContent(input);
      d.addAcceptAction(() => {
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
      let input = new AonInput();
      input.id = "aonDocumentalAddCategory";
      input.description = MSG.CATEGORY;
      if(category.name) input.value = category.name;
  		d.setContent(input);
  		d.addAcceptAction(() => {
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
      let data = DocumentalSidenav.TAGS;
      data.options = [];
      if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()){
        application.addSidenavOptions3(data, () => this.createTag());
      } else application.addSidenavOptions3(data);
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
      let input = new AonInput();
      input.id = "aonDocumentalAddTag";
      input.description = MSG.TAG;
      d.setContent(input);
      d.addAcceptAction(() => {
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
      let input = new AonInput();
      input.id = "aonDocumentalAddTag";
      input.description = MSG.TAG;
      if(tag.name) input.value = tag.name;
      d.setContent(input);
      d.setTitle(MSG.EDIT_TAG);
      d.addAcceptAction(() => {
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
      getDocument(id).then(doc => this.aonDocument(doc)).catch(error =>this.showToast(error));
    }

    aonDocument(doc) {
      let application = this.getApplication();
      if(this.isMobile()) {
        application.setContentHTML(`<aon-mobile-document document='${JSON.stringify(doc)}'> </aon-mobile-document>`);
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
      d.setContent(uploadOption(this.getDur()));
      d.addAcceptAction(async() => {
          let data = {
            category: document.getElementById("aonDocumentalUploadCategory").value,
            scope: document.getElementById("aonDocumentalUploadScope").value,
            tag: document.getElementById("aonDocumentalUploadTag").value,
            type: document.getElementById("aonDocumentalUploadType").value
          }
    
          let uploadToast = this.getElement('aonUploadToast');
          if(!uploadToast){ 
            uploadToast = new AonUploadToast();
            uploadToast.setDur(this.getDur());
            this.appendChild(uploadToast);
          }
          for (let file of files) {
            uploadToast.addFile("documental", file, data, () =>  this.aonDocumentalList());
          }
      });
      d.open();
    }
  
}
window.customElements.define('aon-documental', AonDocumental);
