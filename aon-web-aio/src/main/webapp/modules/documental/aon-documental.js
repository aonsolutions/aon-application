import { AonElement } from '../../components/AonElement.js';
import {
	DocumentalSidenav, EMPLOYEE_TYPE, ASESOR_TYPE, ENTERPRISE_TYPE
} from './DocumentalEnums.js';
import {
	getCategories, getTags, createTag, createCategory, editCategory,
	deleteCategory, editTag, deleteTag, getScopes,
	getDomainUserRoles, getDocument, getS3Category, getS3Document,
	getBidoqDocuments, checkBidoq
} from '../../services/service.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { MSG, MATERIAL_ICONS, EVENT, CONSTANT } from '../../environments/environments.js';
import * as ACTION from '../actions.js';
import { AonNewInput } from '../../components/aon-new-input';
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
import { AonSwitch } from '../../components/aon-switch.js';

import * as LS from '../../services/localStorageService.js';
import { createInput } from '../../components/CreateComponent.js';
import { IFRAME } from '../../environments/aonTag.js';

export class AonDocumental extends AonElement {
	_filter;
	_tags;
	_categories;
	_scopes;
	dur;

	DOCUMENTAL;
	INPUTFILE;

	constructor() {
      super();
      // Coger la category seleccionada del filtro
        // Enlazamos el m�todo al contexto de la clase
        this.categoryEvento = this.categoryEvento.bind(this);
        // Nos aseguramos de escuchar el evento
        window.addEventListener('category_filter', this.categoryEvento);
	}

	connectedCallback() {
		this.initialize();

		getDomainUserRoles({}).then(r => {
			this.dur = new DomainUserRoles(r);
			this.build();
		});
	}

	initialize() {
		this.DOCUMENTAL = 'aonDocumental';
		this.INPUTFILE = this.DOCUMENTAL + 'InputFile';
		this._filter = this.isBetaDoc() 
          ?
            {
              type    : 'all',
              page    : 1,
              perPage : 30,
              domain  : localStorage.getItem('aon_domain_id')
            }
          :
            {
              type    : 'all',
              page    : 1,
              per_page: 30,
              domain  : localStorage.getItem('aon_domain_id')
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

	async build() {
		if(LS.isFutureTheme()){
			LS.setBetaDoc(true);
		}
		
		let aonDocumental = this.getApplication();
		if(this.isBeta() ||  this.isAyudaTorInfoautonomos()) {
			let titleSection = aonDocumental.getToolbar().getTitleSection();

			let newView = new AonSwitch();
			newView.style.marginLeft = '20px';
			newView.id = this.id + "NewView";
			newView.title = "Nueva Vista";
			newView.checked = this.isBetaDoc();
			newView.addEventListener(EVENT.CHANGE, () => {
				LS.setBetaDoc(newView.checked);
				this.rootPanel(new AonDocumental());
			});
			if(!LS.isFutureTheme())
				titleSection.appendChild(newView);
		}

		this.getElement("aonDocumentalToolbarHeaderTitleSection");

		if (this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
			aonDocumental.drag_and_drop = true;
		}

		let input = this.getElement(this.INPUTFILE);

		input.addEventListener(EVENT.CHANGE, () => this.upload(input.files));

        aonDocumental.addEventListener(EVENT.AON_APPLICATION_DROP, (e) => this.upload(e.detail));

		if (this.isMobile()) {
			if (this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager())
				aonDocumental.addFloatOption(ACTION.UPLOAD_FILE, () => this.addDocumentalFile());
		} else {
			if (this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager()) {
				aonDocumental.addToolbarOption2(ACTION.UPLOAD_FILE, () => this.addDocumentalFile());
			}
            if(!this.isBetaDoc()){
              // como se carga el boton de buscar aqui
              const btnSearch = aonDocumental.addSearchOption();
              btnSearch.addEventListener(EVENT.SEARCH, (event) => this.search(event.detail));
            }
		}

		if (this.isMobile()) {
			this.getApplication().addMobileSidenavHeader(Apps.DOCUMENTAL);
		}

		if(!this.getDur().isDocumentalPortal() && !this.getDur().isDocumentalManager()){
			this.addDocumentOptions();
		}
		
		if (this.isBetaDoc() && this.getDur().isDocumentalManager() || this.isBetaDoc() && this.getDur().isDocumentalPortal() ) {
			aonDocumental.addToolbarOption2(ACTION.ADD, () => this.createOptions());
		}

		if (this.getDur().isDocumentalPortal() || this.getDur().isDocumentalManager()){
			this.addDocumentOptions();
		}

		if(this.getDur().isBidoq()) {
        	let bool = await this.hasBidoq();
			if(bool) aonDocumental.addToolbarOption2(ACTION.BIDOQ_IMPORT, () => this.importBidoqDocumentsToAon());
		}


		this.addTypeOptions();
		this.addCategoryOptions();
		this.addTagOptions();
		this.loadScopes();
		if (this.value) {
			this.aonDocumentById(this.value);
		} else {
			this.aonDocumentalList();
		}
	}

	addDocumentOptions() {
		let aonDocumental = this.getElement(this.DOCUMENTAL);

        if (this.isBetaDoc()) {
			let data2 = DocumentalSidenav.OFFICE_CATEGORIES;
			aonDocumental.addSidenavOptions3(data2);
			let data3 = DocumentalSidenav.USER_CATEGORIES;
			aonDocumental.addSidenavOptions3(data3);
        }else{
          let documentOptions = [
              {
                  id  : MSG.ALL_FILES,
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
	}

	addTypeOptions() {
		if (!this.isBetaDoc()) {
			if (this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
				let typeOptions = [{
					name: MSG.ENTERPRISE,
					icon: MATERIAL_ICONS.BUSINESS,
					fn: () => {
						this._filter.category = undefined;
						this._filter.tag = undefined;
						this._filter.type = ENTERPRISE_TYPE;
						this.aonDocumentalList();
					}
				}, {
					name: MSG.EMPLOYEE,
					icon: MATERIAL_ICONS.PERSON,
					fn: () => {
						this._filter.category = undefined;
						this._filter.tag = undefined;
						this._filter.type = EMPLOYEE_TYPE;
						this.aonDocumentalList();
					}
				}];
				if (this.getDur().isDocumentalManager()) {
					typeOptions.push({
						name: MSG.ASESOR,
						icon: 'work',
						fn: () => {
							this._filter.category = undefined;
							this._filter.tag = undefined;
							this._filter.type = ASESOR_TYPE;
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
	}

	addCategoryOptions() {
		let application = this.getApplication();
		let data = DocumentalSidenav.CATEGORIES;
		data.options = [];

		if (!this.isBetaDoc()) {
			if (this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
				application.addSidenavOptions3(data, () => this.createCategory());
			} else {
				application.addSidenavOptions3(data);
			}
		}
		this.loadCategories();
	}

    // Categoria pasada desde el filtro
    categoryEvento(event) {
      let application = this.getApplication();
      // Nombre que va al lado del titulo
      application.getToolbar().attributeChangedCallback(CONSTANT.OPTION, '', event.detail.categoryName);
      // Quitamos el marcado
      application.removeBackgroundSidenavAll(DocumentalSidenav.DOCUMENTS.app.color);
      // Marcamos la opcion que se esta filtrando
      application.addBackgroundSidenav(event.detail.category, DocumentalSidenav.DOCUMENTS.app.color);
    }

    // Metodo para limpiar el evento
    eliminarEvento() {
      window.removeEventListener('category_filter', this.categoryEvento);
    }

	loadCategories() {
		let data = {
			parent: null,
			domain: LS.getDomainId()
		};
		if (this.isBetaDoc()) {
            let application = this.getApplication();
            // Las categorias del cliente, las ponemos como ocultas si no tiene
            let yourCategoriesVisibility = false;
            if (!yourCategoriesVisibility)
              this.hideElementByVisibility(application.SIDENAV + DocumentalSidenav.USER_CATEGORIES.id);
            // llamamos a las categorias
			getS3Category(data).then(categories => {
				this._categories = categories.map(c => {
					return {
						value: c.id,
						name: c.name
					};
				});
				
                // Limpiamos
                this.clearElementById(application.SIDENAV + DocumentalSidenav.OFFICE_CATEGORIES.id + 'List');
                this.clearElementById(application.SIDENAV + DocumentalSidenav.USER_CATEGORIES.id + 'List');
                // Metemos el todo los documentos
                let documentOptions = {
                  id  : MSG.ALL_FILES,
                  name: MSG.ALL_FILES,
                  icon: 'insert_drive_file',
                  fn: () => {
                    this._filter.category = undefined;
                    this._filter.tag = undefined;
                    this._filter.type = 'all';
                    this.aonDocumentalList();
                  }
                };
                application.addSidenavOptionsListValue(DocumentalSidenav.OFFICE_CATEGORIES, documentOptions);
                // Marcamos la primera opcion
                application.getToolbar().attributeChangedCallback(CONSTANT.OPTION, '', MSG.ALL_FILES);
                application.addBackgroundSidenav(MSG.ALL_FILES, DocumentalSidenav.DOCUMENTS.app.color);
                // Relenamos
				categories.forEach(item => {
					if(item.is_deletable === 0){
						let optionDefaultCategory = {
                            id  : item.id,
							name: item.name,
							icon: 'insert_drive_file',
							fn: () => {
								this._filter.tag = undefined;
								this._filter.category = item.id;
								this.aonDocumentalList();
							}
						};
						application.addSidenavOptionsListValue(DocumentalSidenav.OFFICE_CATEGORIES, optionDefaultCategory);
					}else{
                        // Tiene datos en tus catogiras 
                        yourCategoriesVisibility = true;
						let optionUserCategories = {
                            id  : item.id,
							name: item.name,
							icon: 'insert_drive_file',
							fn: () => {
								this._filter.tag = undefined;
								this._filter.category = item.id;
								this.aonDocumentalList();
							}
						};
						if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()){
							optionUserCategories.actions = [{
								id: 'Edit',
								icon: 'edit',
								action: () => this.editCategory(item)
							}];
						}
						application.addSidenavOptionsListValue(DocumentalSidenav.USER_CATEGORIES, optionUserCategories);
					}
					// Si estamos en modo beta, agregamos las categorías al nivel del apartado documentos
				});
                // Mostrar tus categorias
                if (yourCategoriesVisibility)
                  this.showElementByVisibility(application.SIDENAV + DocumentalSidenav.USER_CATEGORIES.id);
			});
		} else {
			getCategories(data).then(categories => {
				this._categories = categories.map(c => {
					return {
						value: c.id,
						name: c.name
					};
				});
				let application = this.getApplication();
				// Si no es beta, agregamos las categorías bajo el apartado categorías
				this.clearElementById(application.SIDENAV + DocumentalSidenav.CATEGORIES.id + 'List');
				categories.forEach(item => {
					let option = {
						name: item.name,
						icon: 'label',
						fn: () => {
							this._filter.tag = undefined;
							this._filter.category = item.id;
							this.aonDocumentalList();
						}
					};
					// Si no es beta, agregamos las opciones de eliminar y editar las categorías
					if (this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
						option.actions = [{
							id: 'Delete',
							icon: 'delete',
							action: () => this.deleteCategory(item)
						}, {
							id: 'Edit',
							icon: 'edit',
							action: () => this.editCategory(item)
						}];
					}
					application.addSidenavOptionsListValue(DocumentalSidenav.CATEGORIES, option);
				});
			});
		}
	}

	loadScopes() {
		getScopes().then(scopes => {
			this._scopes = scopes.map(s => {
				return {
					value: s.id,
					name: s.name
				};
			});
		});
	}

	createOptions() {
      	// Limpiar botones anteriores
		const btnAccept = document.getElementById('aonDocumentalDialogDialogActionAccept');
		const btnCancel = document.getElementById('aonDocumentalDialogDialogActionCancel');
		if (btnAccept) btnAccept.remove();
		if (btnCancel) btnCancel.remove();
	
		// Obtener y preparar el diálogo
		let doc = document.getElementById(this.getApplication().DIALOG);
		doc.clear();
		if (!this.isMobile()) doc.width = '400px';
		doc.setTitle(MSG.WHAT_DO_YOU_WANT_TO_CREATE);
	
		// Radios
		let radioCategory = document.createElement('input');
		radioCategory.type = 'radio';
		radioCategory.name = 'creationType';
		radioCategory.id = 'radioCategory';
		radioCategory.checked = true;
	
		let labelCategory = document.createElement('label');
		labelCategory.setAttribute('for', 'radioCategory');
		labelCategory.textContent = 'Crear categoría';
	
		let radioTag = document.createElement('input');
		radioTag.type = 'radio';
		radioTag.name = 'creationType';
		radioTag.id = 'radioTag';
	
		let labelTag = document.createElement('label');
		labelTag.setAttribute('for', 'radioTag');
		labelTag.textContent = 'Crear etiqueta';
	
		// Contenedor de radios
		let radioContainer = document.createElement('div');
		radioContainer.appendChild(radioCategory);
		radioContainer.appendChild(labelCategory);
		radioContainer.appendChild(document.createElement('br'));
		radioContainer.appendChild(radioTag);
		radioContainer.appendChild(labelTag);
	
		// Input compartido
		const input =  new AonNewInput();
		input.id = 'aonDocumentalCreationInput';
		if (radioCategory.checked) {
			input.title = MSG.CATEGORY;
		}
		// Reaccionar al cambio de radio
		const handleRadioChange = () => {
			const labelText = radioCategory.checked ? MSG.CATEGORY : MSG.TAG;
			// Actualizar atributo 'title'
			input.title = labelText;
		
			// Actualizar contenido del título visible
			const titleSpan = input.getElement(input.TITLE);
			if (titleSpan) {
				titleSpan.innerHTML = labelText + (input.isRequired() ? " *" : "");
			}

			// Actualizar el placeholder
			const inputEl = input.getElement(input.INPUT);
			if (inputEl) {
				inputEl.placeholder = labelText;
			}
		};

		radioCategory.addEventListener('change', handleRadioChange);
		radioTag.addEventListener('change', handleRadioChange);

		// Contenedor general
		let container = document.createElement('div');
		container.appendChild(radioContainer);
		container.appendChild(document.createElement('br'));
		container.appendChild(input);

		doc.setContent(container);

		// Acción de aceptar
		doc.addAcceptAction(() => {
			const value = input.value?.trim();
			if (!value) return;

			if (radioCategory.checked) {
              createCategory({ name: value }).then(() => {
                this.loadCategories();
                // recargar el filtro
                this.aonDocumentalListRestFilter();
              });
			} else {
              createTag({ name: value }).then(() => {
                this.loadTags();
                // recargar el filtro
                this.aonDocumentalListRestFilter();
              });
			}
		});

		doc.open();
	}

	createCategory() {
        let d = document.getElementById(this.getApplication().DIALOG);
        d.clear();
        if (!this.isMobile()) d.width = '400px';
        d.setTitle(MSG.ADD_CATEGORY);
        let input = createInput("aonDocumentalAddCategory", MSG.CATEGORY);
        d.setContent(input);

        d.addAcceptAction(() => {
            if (!input.value.isEmpty()) {
                createCategory({ name: input.value }).then(() => {
                    this.loadCategories();
                });
            }
        });
        d.open();
	}	

	editCategory(category) {
		let d = document.getElementById(this.getApplication().DIALOG);
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.EDIT_CATEGORY);
		let input = createInput("aonDocumentalAddCategory", MSG.CATEGORY);
		if (category.name) input.value = category.name;
		d.setContent(input);
		d.addAcceptAction(() => {
			if (!input.value.isEmpty()) {
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
		if (!this.isMobile()) d.width = '400px';
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
		if (!this.isBetaDoc()) {
			let application = this.getApplication();
			let data = DocumentalSidenav.TAGS;
			data.options = [];
			if (this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
				application.addSidenavOptions3(data, () => this.createTag());
			} else application.addSidenavOptions3(data);
			this.loadTags();
		}
	}

	loadTags() {
		let application = this.getApplication();
		getTags({ domain: localStorage.getItem('aon_domain_id') }).then(tags => {
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
				if (this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
					option.actions = [{
						id: 'Delete',
						icon: 'delete',
						action: () => this.deleteTag(item)
					}, {
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
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.ADD_TAG);
		let input = createInput("aonDocumentalAddTag", MSG.TAG);
		d.setContent(input);
		d.addAcceptAction(() => {
			if (!input.value.isEmpty()) {
				createTag({ name: input.value }).then(() => {
					this.loadTags();
				});
			}
		});
		d.open();
	}

	editTag(tag) {
		let d = document.getElementById(this.getApplication().DIALOG);
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		let input = createInput("aonDocumentalAddTag", MSG.TAG);
		if (tag.name) input.value = tag.name;
		d.setContent(input);
		d.setTitle(MSG.EDIT_TAG);
		d.addAcceptAction(() => {
			if (!input.value.isEmpty()) {
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
		if (!this.isMobile()) d.width = '400px';
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

	async importBidoqDocumentsToAon() {
		// Crear overlay
		let loadingOverlay = document.createElement('div');
		loadingOverlay.id = 'aonDocumentalLoadingOverlay';
		loadingOverlay.style.position = 'absolute';
		loadingOverlay.style.top = '0';
		loadingOverlay.style.left = '0';
		loadingOverlay.style.width = '100%';
		loadingOverlay.style.height = '100%';
		loadingOverlay.style.backgroundColor = 'rgba(241, 236, 236, 0.8)';
		loadingOverlay.style.display = 'flex';
		loadingOverlay.style.alignItems = 'center';
		loadingOverlay.style.justifyContent = 'center';
		loadingOverlay.style.zIndex = '10';
	
		// Contenedor del spinner + texto
		let spinnerContainer = document.createElement('div');
		spinnerContainer.style.display = 'flex';
		spinnerContainer.style.flexDirection = 'column';
		spinnerContainer.style.alignItems = 'center';
	
		// Spinner
		let spinner = document.createElement('div');
		spinner.classList.add('preloader-wrapper', 'active');
		spinner.innerHTML = `
			<span class="material-symbols-outlined">
				refresh
			</span>
		`;
	
		let icon = spinner.querySelector('.material-symbols-outlined');
		icon.style.fontSize = '48px';
		icon.style.animation = 'rotate 2s linear infinite';
	
		// Texto
		let text = document.createElement('div');
		text.textContent = 'Importando documentos desde Bidoq...';
		text.style.marginTop = '12px';
		text.style.fontSize = '16px';
		text.style.color = '#333';
		text.style.fontFamily = 'Arial, sans-serif';
	
		// Estilo para la animación del spinner
		if (!document.getElementById('spinner-style')) {
			let style = document.createElement('style');
			style.id = 'spinner-style';
			style.innerHTML = `
				@keyframes rotate {
					0% {
						transform: rotate(0deg);
					}
					100% {
						transform: rotate(360deg);
					}
				}
			`;
			document.head.appendChild(style);
		}
	
		// Armar estructura
		spinnerContainer.appendChild(spinner);
		spinnerContainer.appendChild(text);
		loadingOverlay.appendChild(spinnerContainer);
	
		// Agregar overlay al contenedor
		let container = document.body;
		container.appendChild(loadingOverlay);
		try {
			let data = {
				document: localStorage.getItem('aon_domain_document'),
			};
			let response = await getBidoqDocuments(data);
			if (response) {
				// documentalList.init();
				this.loadCategories();
			}
		} catch (error) {
			console.error("Error al importar documentos:", error);
		} finally {
			// Quitar el spinner
			loadingOverlay.remove();
		}
	}

	async hasBidoq(){
		let data = {
          document: localStorage.getItem('aon_domain_document')
		};
		let response = await checkBidoq(data);
		return response;
	}

    aonDocumentalListRestFilter(){
      let documentalList = this.getElement('aonDocumentalList');
      documentalList.buildToolbarSearch();
    }

	aonDocumentalList(filter) {
		filter = filter || this._filter;
		this._filter = filter;
		let documentalList = this.getElement('aonDocumentalList');
		if (documentalList) {
			documentalList.setFilter(filter);
			documentalList.init();
		} else {
			let application = this.getApplication();
			if (this.isMobile()) {
				application.setContentHTML(filter
					? `<aon-mobile-documental-list id="aonDocumentalList" filter='${JSON.stringify(filter)}'></aon-mobile-documental-list>`
					: `<aon-mobile-documental-list id="aonDocumentalList"></aon-mobile-documental-list>`);
			} else {
				application.setContentHTML(filter
					? `<aon-documental-list id="aonDocumentalList" filter='${JSON.stringify(filter)}'></aon-documental-list>`
					: `<aon-documental-list id="aonDocumentalList"></aon-documental-list>`);
			}
		}
	}

	aonDocumentById(id) {
      if(this.isBetaDoc()){
          getS3Document(id).then(doc => this.aonDocument(doc)).catch(error => this.showToast(error));
      }else{
          getDocument(id).then(doc => this.aonDocument(doc)).catch(error => this.showToast(error));
      }
	}

	aonDocument(doc) {
		let application = this.getApplication();
		if (this.isMobile()) {
			application.setContentHTML(`<aon-mobile-document document='${JSON.stringify(doc)}'> </aon-mobile-document>`);
		} else {
			application.setContentHTML(`<aon-document document='${JSON.stringify(doc)}'> </aon-document>`);
		}
	}

	addDocumentalFile() {
		if (this.isBetaDoc()) {
			// Si es beta, primero abrimos el modal
			let d = document.getElementById(this.getApplication().DIALOG);
			d.clear();
			if (!this.isMobile()) d.width = '400px';
			d.setTitle(MSG.UPLOAD_FILE);
			d.setContent(uploadOption(this.getDur(), this.isBetaDoc()));
			d.addAcceptAction(async () => {

				let data = {};

				// Verificar si el elemento existe antes de acceder a su valor
				let categoryElement = document.getElementById("aonDocumentalUploadCategory");
				if (categoryElement && categoryElement.value !== null) {
					let category = categoryElement.value;
					data.category = category;
				}

				let subCategoryElement = document.getElementById("aonDocumentalUploadSubCategory");
				if (subCategoryElement && subCategoryElement.value !== null) {
					let subcategory = subCategoryElement.value;
					data.category = subcategory;  // Se sobrescribe 'category' si subcategoría existe
				}

				let administrationElement = document.getElementById("aonDocumentalAdministration");
				if (administrationElement && administrationElement.value !== null) {
					let administration = administrationElement.value;
					data.category = administration;  // Se sobrescribe 'category' si administración existe
				}

				let modelElement = document.getElementById("aonDocumentalModels");
				if (modelElement && modelElement.value !== null) {
					let model = modelElement.value;
					data.category = model;  // Se sobrescribe 'category' si modelo existe
				}

				let tagElement = document.getElementById("aonDocumentalUploadTag");
				if (tagElement) {					
					const selectedTags = tagElement.getSelectable();
					// Transformamos los tags a un array con solo id 
					data.tags = selectedTags.map(tag => ({ id: tag.value }));
				}
		
				let datePickerElement = document.getElementById("aonDocumentalUploadDatePicker");
				if (datePickerElement && datePickerElement.getValue() !== null) {
					let date = datePickerElement.getValue();
					data.date = date;
				}

				let scopeElement = document.getElementById("aonDocumentalUploadScope");
				if (scopeElement && scopeElement.value !== null) {
					let scope = scopeElement.value;
					data.scope = scope;
				}
				
				if(document.getElementById("visibleEmpleadoCheckbox") && document.getElementById("visibleEmpleadoCheckbox").checked){
					data.registryType = EMPLOYEE_TYPE;
				} else if(document.getElementById("visibleEmpresaCheckbox") && document.getElementById("visibleEmpresaCheckbox").checked){
					data.registryType = ASESOR_TYPE;
				}

				let uploadToast = this.getElement('aonUploadToast');
				if (!uploadToast) {
					uploadToast = new AonUploadToast();
					this.appendChild(uploadToast);
				}
				let input = document.createElement("input");
				input.type = "file";
				input.name = "file";
				input.multiple = true;

				input.addEventListener(EVENT.CHANGE, () => {
					for (let file of input.files) {
						file.date = data.date;
						uploadToast.addFile("documental", file, data, () => this.aonDocumentalList());
					}
				});
				input.click();
			});
			d.open();
		} else {
			// Si no es beta, abrimos directamente el selector de archivos
			let el = this.getElement(this.INPUTFILE);
			el.click();
		}
	}

	upload(files) {
		let d = document.getElementById(this.getApplication().DIALOG);
		d.clear();
		if (!this.isMobile()) d.width = '400px';
		d.setTitle(MSG.UPLOAD_FILE);
		d.setContent(uploadOption(this.getDur(), this.isBetaDoc()));
		d.addAcceptAction(async () => {
			let data = {};

			if (this.isBetaDoc()) {
				const categoryElement       = document.getElementById("aonDocumentalUploadCategory");
                const subcategoryElement    = document.getElementById("aonDocumentalUploadSubCategory");
                const administrationElement = document.getElementById("aonDocumentalAdministration");
                const modelsElement         = document.getElementById("aonDocumentalModels");
                const tagElement            = document.getElementById("aonDocumentalUploadTag");
                const dateElement           = document.getElementById("aonDocumentalUploadDatePicker");

                // Verificar si los elementos tienen valores v�lidos y asignarlos a la propiedad 'category' del objeto 'data'
                if (categoryElement && categoryElement.value !== null && categoryElement.value.trim() !== "") {
                    data.category = categoryElement.value;

                    if (subcategoryElement && subcategoryElement.value !== null && subcategoryElement.value.trim() !== "") {
                        data.category = subcategoryElement.value;

                        if (administrationElement && administrationElement.value !== null && administrationElement.value.trim() !== "") {
                            data.category = administrationElement.value;

                            if (modelsElement && modelsElement.value !== null && modelsElement.value.trim() !== "") {
                                data.category = modelsElement.value;
                            }
                        }
                    }
                }

				if (tagElement && tagElement.value !== null &&  tagElement.value.trim() !== "") {
					data.tag = tagElement.getSelectable();
				}

				if (dateElement && dateElement.getValue() !== null && dateElement.getValue().trim() !== "") {
					data.date = dateElement.getValue();
				}
			} else {
				data = {
					category: document.getElementById("aonDocumentalUploadCategory").value,
					scope   : document.getElementById("aonDocumentalUploadScope").value,
					tag     : document.getElementById("aonDocumentalUploadTag").value,
					type    : document.getElementById("aonDocumentalUploadType").value
				}
			}

			let uploadToast = this.getElement('aonUploadToast');
			if (!uploadToast) {
				uploadToast = new AonUploadToast();
				this.appendChild(uploadToast);
			}
			for (let file of files) {
				uploadToast.addFile("documental", file, data, () => this.aonDocumentalList());
			}
		});
		d.open();
	}

}
window.customElements.define('aon-documental', AonDocumental);