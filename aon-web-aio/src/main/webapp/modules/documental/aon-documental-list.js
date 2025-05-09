import {AonElement} from '../../components/AonElement.js';
import {
  getDocuments, downloadDocuments, sendDocumentMail, updateFiles, deleteFile, 
  getDomainUserRoles, getS3Document, deleteS3Document, downloadS3Documents, getS3Category, getCategories,
  getTags
} from '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';
import '../../components/aon-table.js';

import { CONSTANT, MSG, EVENT } from '../../environments/environments.js';
import * as ACTION from '../actions.js';
import * as LS from '../../services/localStorageService.js';
import { createList } from '../../components/CreateComponent.js';
import { 
  DOCUMENTAL_FILTER_ASESOR, DOCUMENTAL_FILTER_ENTERPRISE, DOCUMENTAL_FILTER,
  ASESOR_TYPE, EMPLOYEE_TYPE
} from "./DocumentalEnums.js";
import { formatBytes } from '../../services/utils.js';

export class AonDocumentalList extends AonElement {
	more;
	_roles;
	TABLE;
    btnSearch;

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
		if(this.isBetaDoc()){
          this.buildToolbarSearch();
		}
		let aonDocumentalTable = createList(this.TABLE);
		aonDocumentalTable.selectable = 'true';
		this.appendChild(aonDocumentalTable);
        // Columnas y datos
        // addColumn(name, type, id, width, textAlign)
        if(this.isBetaDoc()){
          aonDocumentalTable.addColumn(MSG.DATE_CREATION, 'creation_date', 'creation_date', '20%');
          aonDocumentalTable.addColumn(MSG.DATE+' del documento', 'date', 'date', '20%');
        } else {
          aonDocumentalTable.addColumn(MSG.DATE, 'date', 'date', '20%');
        }
		aonDocumentalTable.addColumn(MSG.NAME, 'string', this.isBetaDoc() ? 'name' : 'title', '50%');
		aonDocumentalTable.addColumn(MSG.SIZE, 'string', 'size', '15%');

		// Iniciar tabla
		this.init();
		aonDocumentalTable.addEventListener('more', () => {
			if(this.more)
				this.loadMore();
		});

		aonDocumentalTable.addEventListener('select', () => {
			if(aonDocumentalTable.selected.length === 1) {
				this.addDocumentalActions();
			} else if(aonDocumentalTable.selected.length === 0){
				this.removeDocumentalActions();
			}
		});
	}

	buildToolbarSearch(){
	    this.btnSearch = this.getApplication().addSearchOption(true, true);
	    let timeOut = null;
        // Mostramos si esta oculto
        if(this.btnSearch.hidden){
          this.btnSearch.hidden = false;
        }
        // Montamos el filtro
	    this.btnSearch.addEventListener(EVENT.SEARCH_NEW, ({detail})=>{
          clearTimeout(timeOut);
          timeOut = setTimeout(() => {
            this._list = [];
			if (detail.tags) {
				// ---- RECONSTRUIR TAGS DESDE LOS CHECKBOXES ----
				const tagIds = Object.keys(detail)
                  .filter(key => key.startsWith("checkbox"))
                  .filter(key => detail[key] === 'true') // solo los que están marcados
                  .map(key => parseInt(key.replace("checkbox", '')))
                  .filter(id => !isNaN(id));

				if (tagIds.length > 0) {
                  detail.tag = JSON.stringify(tagIds.map(id => ({ id })));
				}

				// Eliminar los checkboxes individuales del filtro para que no se muestren en la llamada
				Object.keys(detail).forEach(key => {
                  if (key.startsWith("checkbox")) {
                    delete detail[key];
                  }
				});
				delete detail.tags;
			}

	        if(detail) {
              // El tipo de permiso
                if(detail[ASESOR_TYPE] && detail[ASESOR_TYPE] !== 'false'){
                  // Buscar solo asesor
                    detail.categoryType = ASESOR_TYPE;
					delete detail[ASESOR_TYPE];
                } else if (detail[ASESOR_TYPE]){
					delete detail[ASESOR_TYPE];
                }
                if(detail[EMPLOYEE_TYPE] && detail[EMPLOYEE_TYPE] !== 'false'){
                  // Buscar solo empleado
                    detail.categoryType = EMPLOYEE_TYPE;
					delete detail[EMPLOYEE_TYPE];
                } else if (detail[EMPLOYEE_TYPE]){
					delete detail[EMPLOYEE_TYPE];
                }
              // Mis categorias
                const myCategory = detail.categoryOldFilter && detail.categoryOldFilter !== 'false';
                if(myCategory){
                    detail.category = detail.categoryOld;
					delete detail.categoryOld;
					delete detail.categoryOldFilter;
                } else if (detail.categoryOldFilter){
					delete detail.categoryOld;
					delete detail.categoryOldFilter;
                } 
              // Enviamos un evento con la categoria marcada, principal (antes de meter en el valor de las subcategorias)
                const selectCategory   = detail.category ? detail.category : MSG.ALL_FILES;
                let selectCategoryName = MSG.ALL_FILES;
                if( selectCategory !== MSG.ALL_FILES){
                  // El nombre de la categoria de despcho o las creadas por la empresa
                  const category        = myCategory ? this.getElement("categoryOld") : this.getElement("category");
                  const categoryOptions = JSON.parse(category.options);
                  const selectedOption  = categoryOptions.find(opt => opt.value === parseInt(selectCategory));
                  selectCategoryName    = selectedOption.name;
                }
                const evento  = new CustomEvent('category_filter', { detail: {category: selectCategory, categoryName: selectCategoryName}});
                window.dispatchEvent(evento);
              // El tipo de category despacho
				if(detail.category2){
					detail.category = detail.category2;
					delete detail.category2;
				}
				if(detail.category3){
					detail.category = detail.category3;
					delete detail.category3;
				}
				if(detail.category4){
					detail.category = detail.category4;
					delete detail.category4;
				}
				detail.name = detail.search;
				this.setFilter(detail);
				this.init();
			} // this.getApplicationParent().setDataFilter(detail);
          }, 300);
	    });
        // Rango por encima de empresa, ve el permiso de asesor (documentos que solo ve el asesor)
        if (this._roles.isDocumentalManager()) {
          this.btnSearch.buildOptionsFilter([
            ...DOCUMENTAL_FILTER_ASESOR
          ]);
        } else if (this._roles.isDocumentalPortal()){
          this.btnSearch.buildOptionsFilter([
            ...DOCUMENTAL_FILTER_ENTERPRISE
          ]);
        } else {
          this.btnSearch.buildOptionsFilter([
            ...DOCUMENTAL_FILTER
          ]);
        }
		//Etiquetas
		this.tagFilter();
        // Categoria despacho(categorias predefinidas)
	    this.searchValueDefault();
        // tus categorias (creadas por la empresa)
        this.categoryOldFilter();
        // Visible solo (solo tiene los dos datos si esta por encima de empresa)
        if (this._roles.isDocumentalManager()) {
          this.visibleOnly();
        }
	}

    async visibleOnly(){
      let asesor   = this.getElement(ASESOR_TYPE);
      let employee = this.getElement(EMPLOYEE_TYPE);
      
      asesor.addEventListener(EVENT.CLICK, () => {
        // Estado que estaba y el que esta el empleado
        if(asesor.value === 'false' && employee.value === 'true'){
          employee.value    = 'false';
          employee.checked  = false;
        }
      });
      employee.addEventListener(EVENT.CLICK, () => {
        // Estado que estaba y el que esta el asesor
        if(employee.value === 'false' && asesor.value === 'true'){
          asesor.value    = 'false';
          asesor.checked  = false;
        }
      });
    }

	async tagFilter(){
		let tagSel = this.getElement("tags");
		let listTags = await getTags({domain : localStorage.getItem('aon_domain_id')});
		if(!listTags){
			tagSel.remove();
		}
		tagSel.setOptions(
			listTags
			  .filter(tag => tag.id != null)
			  .map(tag => ({ name: tag.name, value: tag.id }))
		  );
	}

    async categoryOldFilter(){
      let categoryOldEl = this.getElement("categoryOldFilter");
      // Datos
      const data = {
        parent: null,
        domain: LS.getDomainId()
      };
      const listCategoryOld = await getS3Category(data);
      // solo si tiene creadas
      if(listCategoryOld.length > 0){
        // rellenar
        let categoryOld = this.getElement("categoryOld");
//        categoryOld.setOptions(listCategoryOld.map((category) => ({ name: category.name, value: category.id})));
		categoryOld.setOptions(
          listCategoryOld.filter((category)=> {
            // Categoria antiguas o que se pueden borrar (si se pueden borrar son creadas por la empresa)
            return !category.hasOwnProperty('is_deletable') || category.is_deletable === 1;
          }).map((category) => {
            return {name: category.name, value: category.id };
          })
        );

        if(categoryOld.getOptions().length > 0){
          // mostrar o no
          categoryOldEl.addEventListener(EVENT.CHANGE, () => {
            let category  = this.getElement("category");
            let category2 = this.getElement("category2");
            let category3 = this.getElement("category3");
            let category4 = this.getElement("category4");

            if(categoryOldEl.value === 'true'){
              // Mostramos las creadas por el usuario
              categoryOld.hidden = false;
              // Ocultamos las nuevas categorias
              category.hidden  = true;
              category2.hidden = true;
              category3.hidden = true;
              category4.hidden = true;
              // limpiamos
              let inputElement   = category.querySelector("input[type='select']");
              categoryOld.value  = '';
              inputElement.value = '';
            } else {
              // Mostramos las nuevas categorias
              category.hidden = false;
              // Ocultamos las creadas por el usuario
              categoryOld.hidden = true;
              // limpiamos
              let inputElement   = categoryOld.querySelector("input[type='select']");
              categoryOld.value  = '';
              inputElement.value = '';
            }
          });
        } else {
          categoryOldEl.hidden = true;
        }
      } else {
        categoryOldEl.hidden = true;
      }
	}

	async searchValueDefault(){
        const data = {
          parent: null,
          domain: LS.getDomainId()
        };
		let categories = await getS3Category(data);
		let categoryEl = this.getElement("category");
//		categoryEl.setOptions(categories.map((category) => ({ name: category.name, value: category.id})));
		categoryEl.setOptions(
          categories.filter((category)=> {
            // Categoria nuevas y que no se pueden borrar (Son las categorias de despacho)
            return category.hasOwnProperty('is_deletable') && category.is_deletable === 0;
          }).map((category) => {
            return {name: category.name, value: category.id };
          })
        );

		categoryEl.addEventListener(EVENT.CHANGE, ({detail}) => {
			this.getElement("category2").hidden = true;
			this.getElement("category3").hidden = true;
			this.getElement("category4").hidden = true;
		    if(detail) this.getSubcategories(detail);
		});
	}

	async getSubcategories(detail){
	    try {
	      let categoryEl   = this.getElement("category2");
          let inputElement = categoryEl.querySelector("input[type='select']");
          // limpiamos
          categoryEl.value    = '';
          inputElement.value  = '';
          // traemos
	      let categories = await getS3Category({parent: detail.value});
	      if(categories.length>0) {
	        categoryEl.setOptions(
	          categories.map((category)=> ({name:category.name, value:category.id}))
	        );
	        categoryEl.hidden   = false;
	      } else 
            categoryEl.hidden = true;
		  categoryEl.addEventListener(EVENT.CHANGE, ({detail}) => {
			this.getElement("category3").hidden = true;
			this.getElement("category4").hidden = true;
		    if(detail) this.getAdministrations(detail);
		  });
	    } catch (error) {
	      console.log(error);
	    }
	}
	async getAdministrations(detail){
      try {
        let categoryEl = this.getElement("category3");
        let inputElement = categoryEl.querySelector("input[type='select']");
        // limpiamos
        categoryEl.value    = '';
        inputElement.value  = '';
        // traemos
        let categories = await getS3Category({parent: detail.value});
        if(categories.length>0) {
          categoryEl.setOptions(
            categories.map((category)=> ({name:category.name, value:category.id}))
          );
          categoryEl.hidden =  false;
        }
        else categoryEl.hidden =  true;
        categoryEl.addEventListener(EVENT.CHANGE, ({detail}) => {
          this.getElement("category4").hidden = true;
          if(detail) this.getModels(detail);
        });
      } catch (error) {
        console.log(error);
      }
    }
			  
    async getModels(detail){
        try {
          let categoryEl = this.getElement("category4");
          let inputElement = categoryEl.querySelector("input[type='select']");
          // limpiamos
          categoryEl.value    = '';
          inputElement.value  = '';
          // traemos
          let categories = await getS3Category({parent: detail.value});
          if(categories.length>0) {
            categoryEl.setOptions(
              categories.map((category)=> ({name:category.name, value:category.id}))
            );
            categoryEl.hidden =  false;
          }
          else categoryEl.hidden =  true;
        } catch (error) {
          console.log(error);
        }
    }
	  
    loadMore() {
        let aonDocumentalTable = this.getElement(this.TABLE);
        let filter = this.getFilter();
        if (aonDocumentalTable && filter.page) {
            this.loadDocumentsIntoTable(aonDocumentalTable, filter, false);
        }
    }

    init() {
        this.more = true;
        let aonDocumentalTable = this.getElement(this.TABLE);
        let filter = this.getFilter();
        if (aonDocumentalTable) {
            this.loadDocumentsIntoTable(aonDocumentalTable, filter, true);
        }
    }

    loadDocumentsIntoTable(table, filter, isInit = false) {
        // Barra loader de AonApplication - Iniciar
        this.getApplication().startLoader();
        // Traer los documentos
        const fetchDocuments = this.isBetaDoc() ? getS3Document : getDocuments;
        fetchDocuments(filter).then(documents => {
            if (isInit) {
                table.removeRows();               // Limpiar la tabla si es la inicializaci�n
                table.selected = [];              // Limpiar la selecci�n
                this.removeDocumentalActions();   // Eliminar acciones de documentos
            } 
              // Si no es el inicio (es loadMore), actualizar el filtro para la siguiente p�gina
                filter.page = filter.page + 1;
				this.setFilter(filter);
            if (isInit && documents.length === 0 ) {
                this.more = false;  // Si no hay documentos, no se puede cargar mas
                // Mostrar mensaje si no hay documentos
                table.addRowNoData("No existen documentos disponibles");
            }
			
            // Insertar los documentos en la tabla
            documents.forEach((doc, i) => { 
				if(this.isBetaDoc() && documents[i].size){
					documents[i].size = formatBytes(documents[i].size);
				}
                let tr = table.addRow(doc, () => this.aonDocument(doc, i), (e) => this.aonDocumentContextMenu(e, doc, i));
                tr.id = "aonDocumentalRow";
            });
            // Barra loader de AonApplication - Finalizar
            this.getApplication().stopLoader();
        }).catch((error) => {
          // En caso de error, detener el loader y mostrar mensaje
//          console.error("Error al cargar documentos:", error);
          // Barra loader de AonApplication - Finalizar
          this.getApplication().stopLoader();
        });
    }

	aonDocument(doc, i) {
      // Al ver un documento ocultamos el buscar
      if( this.isBetaDoc() ){
        this.btnSearch.hidden = true;
      } 
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

	async downloadS3Files() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		let data = aonDocumentalTable.selected.map(r => ({
			id: r.id,
			type: r.type
		  }));
		let json = JSON.stringify(data);
		let i = await downloadS3Documents(encodeURI(json));
		const url = URL.createObjectURL(i);
		const a = document.createElement('a');
		a.href = url;
		a.download = 'documentos.zip';
		a.click();
		URL.revokeObjectURL(url);
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

	removeS3Files() {
		let aonDocumentalTable = this.getElement(this.TABLE);
		let aonDocumental = this.getApplication();
		let d = document.getElementById(aonDocumental.DIALOG);
		d.clear();
		if(!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.DELETE_FILE);
		d.setContentHTML(`Estás seguro de eliminar los ficheros?`);
		d.addAcceptAction(() => {
			let data = aonDocumentalTable.selected.map(r => ({
				id: r.id,
				type: r.type
			  }));
		deleteS3Document({data}).then(() => this.init() );
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
        if(this.isBetaDoc()){
			if(this._roles.isDocumentalManager()){
				aonDocumental.addToolbarOption2(ACTION.DELETE_FILE, () => this.removeS3Files());
			}
            aonDocumental.addToolbarOption2(ACTION.DOWNLOAD_FILE, () => this.downloadS3Files());
		}else if(!this.isBetaDoc() && (this._roles.isDocumentalManager() || this._roles.isDocumentalPortal())){
			// El boton este de editar  no hace nada??
            aonDocumental.addToolbarOption2(ACTION.EDIT_FILE, () => this.editFiles());
			aonDocumental.addToolbarOption2(ACTION.DELETE_FILE, () => this.removeFiles());
			aonDocumental.addToolbarOption2(ACTION.DOWNLOAD_FILE, () => this.downloadFiles());
		}
		// aonDocumental.addToolbarOption2(ACTION.SEND_FILE, () => this.sendFiles());
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
