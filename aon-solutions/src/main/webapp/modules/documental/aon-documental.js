import {AonElement} from '../../components/AonElement.js';
import {DocumentalAction, DocumentalSidenav} from './DocumentalEnums.js';
import {getCategories, getTags, createTag, createCategory, editCategory,
   deleteCategory, editTag, deleteTag, uploadFileDocumental} from '../../services/service.js';

import './aon-documental-list.js';
import './aon-document.js';

import './aon-mobile-documental-list.js';
import './aon-mobile-document.js';

import '../../components/aon-application.js';


import * as MSG from "../../environments/msg.js";


export class AonDocumental extends AonElement {

    _filter;

    DOCUMENTAL;
  	INPUTFILE;

  	constructor () {
  		super();
      this.DOCUMENTAL = 'aonDocumental';
      this.INPUTFILE = this.DOCUMENTAL + 'InputFile';
      this._filter = {
        type: 'all',
        page:1,
        per_page:30,
        domain: localStorage.getItem('aon_domain_id')
      };
    }

    connectedCallback () {
      this.innerHTML = `
        <aon-application id="${this.DOCUMENTAL}" title="${MSG.AON_MSG_DOCUMENTARY}" drag_and_drop="true"></aon-application>
        <input id="${this.INPUTFILE}" style='display:none;' type='file' name='file' multiple>
      `;
      this.build();
    }

    build(){
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let input = this.getElement(this.INPUTFILE);

      input.addEventListener('change', () => this.upload(input.files));

  		aonDocumental.addEventListener('drop', (event) => {
  			if(event && event.dataTransfer && event.dataTransfer.files){
  				this.upload(event.dataTransfer.files);
  			}
  		});

      if(this.isMobile()) {
        aonDocumental.addFloatOption(DocumentalAction.UPLOAD, () => this.addDocumentalFile());
      } else {
        aonDocumental.addToolbarOption2(DocumentalAction.UPLOAD, () => this.addDocumentalFile());
        aonDocumental.addSearchOption();
        aonDocumental.addEventListener('search', (event) => this.search(event.detail));
      }

      this.addDocumentOptions();
      this.addCategoryOptions();
      this.addTagOptions();

  		this.aonDocumentalList();
    }

    addDocumentOptions() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let documentOptions = [{
          name: MSG.AON_MSG_ALL_FILES,
          icon: 'insert_drive_file',
          fn: () => {
            this._filter.category = undefined;
            this._filter.tag = undefined;
            this._filter.type = 'all';
            this.aonDocumentalList();
          }
        },{
          name: MSG.AON_MSG_SYSTEM_MESSAGES,
          icon: 'settings',
          fn: () => {
            this._filter.category = undefined;
            this._filter.tag = undefined;
            this._filter.type = 'system';
            this.aonDocumentalList();
          }
        }];
      aonDocumental.addSidenavOptions2(DocumentalSidenav.DOCUMENTS, documentOptions);
    }

    addCategoryOptions() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      aonDocumental.addSidenavOptions2(DocumentalSidenav.CATEGORIES, [], () => this.createCategory());
      this.loadCategories();
    }

    loadCategories() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      getCategories({domain: localStorage.getItem('aon_domain_id')}).then( categories => {
        this.clearElement(aonDocumental.SIDENAV + DocumentalSidenav.CATEGORIES.id + 'List');
        categories.forEach((item, i) => {
          let option = {
            name: item.name,
            icon: 'label',
            fn: () => {
              this._filter.tag = undefined;
              this._filter.category = item.id;
              this.aonDocumentalList();
            },
            actions: [{
                id: 'Delete',
                icon: 'delete',
                action: () => this.deleteCategory(item)
              },{
                id: 'Edit',
                icon: 'edit',
                action: () => this.editCategory(item)
              }
            ]
          };
          aonDocumental.addSidenavOptionsListValue(DocumentalSidenav.CATEGORIES, option);
        });
      });
    }

    createCategory() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let d = document.getElementById(aonDocumental.DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.AON_MSG_ADD_CATEGORY);
      d.setContentHTML(`<aon-input id="aonDocumentalAddCategory" description="${MSG.AON_MSG_CATEGORY}"> </aon-input>`);
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
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let d = document.getElementById(aonDocumental.DIALOG);
      d.clear();
  		if(!this.isMobile()) d.width = '400px';
  		d.setTitle(MSG.AON_MSG_EDIT_CATEGORY);
  		d.setContentHTML(`<aon-input id="aonDocumentalAddCategory" description="${MSG.AON_MSG_CATEGORY}"> </aon-input>`);
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
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let d = document.getElementById(aonDocumental.DIALOG);
      d.clear();
  		if(!this.isMobile()) d.width = '400px';
  		d.setTitle(MSG.AON_MSG_DELETE_CATEGORY);
      d.setContentHTML(`Estás seguro de eliminar la Categoría ${category.name}`);
  		d.addAcceptAction(() => {
        deleteCategory(category).then(() => {
          this.loadCategories();
        });
      });
  		d.open();
    }

    addTagOptions() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      aonDocumental.addSidenavOptions2(DocumentalSidenav.TAGS, [], () => this.createTag());
      this.loadTags();
    }

    loadTags() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      getTags({domain: localStorage.getItem('aon_domain_id')}).then( tags => {
        this.clearElement(aonDocumental.SIDENAV + DocumentalSidenav.TAGS.id + 'List');
        tags.forEach((item, i) => {
          let option = {
            name: item.name,
            icon: 'label',
            fn: () => {
              this._filter.category = undefined;
              this._filter.tag = item.id;
              this.aonDocumentalList();
            },
            actions: [{
                id: 'Delete',
                icon: 'delete',
                action: () => this.deleteTag(item)
              },{
                id: 'Edit',
                icon: 'edit',
                action: () => this.editTag(item)
              }
            ]
          };
          aonDocumental.addSidenavOptionsListValue(DocumentalSidenav.TAGS, option);
        });
      });
    }

    createTag() {
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let d = document.getElementById(aonDocumental.DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.AON_MSG_ADD_TAG);
      d.setContentHTML(`<aon-input id="aonDocumentalAddTag" description="${MSG.AON_MSG_TAG}"> </aon-input>`);
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
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let d = document.getElementById(aonDocumental.DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.AON_MSG_EDIT_TAG);
      d.setContentHTML(`<aon-input id="aonDocumentalAddTag" description="${MSG.AON_MSG_TAG}"> </aon-input>`);
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
      let aonDocumental = this.getElement(this.DOCUMENTAL);
      let d = document.getElementById(aonDocumental.DIALOG);
      d.clear();
      if(!this.isMobile()) d.width = '400px';
      d.setTitle(MSG.AON_MSG_DELETE_TAG);
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
  			let aonDocumental = this.getElement(this.DOCUMENTAL);
  			if(this.isMobile()) {
          aonDocumental.setContentHTML(filter
  					? `<aon-mobile-documental-list id="aonDocumentalList" filter='${JSON.stringify(filter)}'></aon-mobile-documental-list>`
  					: `<aon-mobile-documental-list id="aonDocumentalList"></aon-mobile-documental-list>`);
  			}  else {
  				aonDocumental.setContentHTML(filter
  					? `<aon-documental-list id="aonDocumentalList" filter='${JSON.stringify(filter)}'></aon-documental-list>`
  					: `<aon-documental-list id="aonDocumentalList"></aon-documental-list>`);
  			}
  		}
  	}

    aonDocument(doc) {
      let aonDocumental = document.getElementById('aonDocumental');
      if(this.isMobile()) {
        aonDocumental.setContentHTML(`<aon-mobile-document document='${JSON.stringify(doc)}'> </aon-mobile-invoice>`);
      } else {
        aonDocumental.setContentHTML(`<aon-document document='${JSON.stringify(doc)}'> </aon-document>`);
      }
    }

    addDocumentalFile() {
      let el = this.getElement(this.INPUTFILE);
      el.click();
    }

    upload(files) {
      for(let i = 0; i < files.length; i++) {
        const READER = new FileReader();
        READER.readAsDataURL(files[i]);
        READER.onload = (_event) => {
          this.attach(READER.result, files[i]);
        };
      }
    }

    attach(fileDataUri,  file){
      if (fileDataUri.length > 0) {
        const base64File = fileDataUri.split(',')[1];
        const data = {
          content: base64File,
          contentType: file.type,
          contentEncoding: 'base64',
          contentName: file.name,
          contentSize: file.size
        };
        let aonDocumental = this.getElement(this.DOCUMENTAL);
        aonDocumental.startLoader();
        uploadFileDocumental(data).then((r) => {
          aonDocumental.stopLoader();
          this.aonDocumentalList()
          //this.getInvoice().id = r.id;
        });
      }
    }
}
window.customElements.define('aon-documental', AonDocumental);
