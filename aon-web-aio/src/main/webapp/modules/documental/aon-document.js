import { AonElement } from '../../components/AonElement.js';
import { ToolbarType } from '../../models/enums.js';
import { ASESOR_TYPE_OPTION, ENTERPRISE_TYPE_OPTION,
   EMPLOYEE_TYPE_OPTION, 
   ASESOR_TYPE,
   EMPLOYEE_TYPE,
   ENTERPRISE_TYPE} from './DocumentalEnums.js';
import { deleteFile, getCategories, getScopes, updateFile, openFileUrl, getS3Document_File, putS3DocumentUpdate, deleteS3Document, downloadS3Documents, getS3Category, getTags, getS3Document } from '../../services/service.js';
import { EVENT, MSG, TAG } from '../../environments/environments.js';
import * as ACTION from '../actions.js';
import '../../components/aon-toolbar.js';
import '../../components/aon-viewer.js';
import '../../components/aon-switch.js';
import '../../components/aon-card.js';
import { createDate, createInput, createSelect } from '../../components/CreateComponent.js';
import { setAttributes, setEvents } from '../../services/utilsComponents.js';
import { AonSwitch } from '../../components/aon-switch.js';
import { AonNewSelect } from '../../components/aon-new-select.js';
import { loadOldCategories } from './DocumentalUtils.js';
export class AonDocument extends AonElement {
  doc;
  docS3;
  _tags;
  //nuevo tag para s3 document
  tag;

  TOOLBAR;
  DATA;
  DATA_CARD;
  FILE;

  DATE;
  NAME;
  CATEGORY;
  SCOPE;
  TAG;
  TYPE;

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
    this.buildDur().then(() => {
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
    this.tag = [];

    this.DATE = this.id + 'Date';
    this.NAME = this.id + 'Name';
    this.CATEGORY = this.id + 'Category';
    this.SCOPE = this.id + 'Scope';
    this.TAG = this.id + 'Tag';
    this.TYPE = this.id + 'Type';
  }

  async build() {
    let title   = this.isBetaDoc() ? this.document.name : this.document.title;
    let toolbar = ` <aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="${title}"> </aon-toolbar>`;

    this.innerHTML = toolbar +`
      <div style="display:flex;">
        <div id="${this.DATA}" class="aonSubContent" style="width:100%">
          <aon-card id="${this.DATA_CARD}" title="${MSG.FILE_DATA}"> </aon-card>
        </div>
        <div id="${this.FILE}" class="aonSubContent">

        </div>
      </div>
    `;

    let fileDiv           = this.getElement(this.FILE);
    fileDiv.style.display = 'block';
    fileDiv.style.width   = '50%';
    
    if(this.isBetaDoc()){
      let data = { type: this.document.type, id: this.document.id};
      getS3Document_File(data).then(document => {
          this.docS3 = document;
          const viewPixels = fileDiv.offsetWidth + (fileDiv.offsetWidth * 0.5);
          fileDiv.innerHTML = `<aon-viewer type="${this.document.contentType}" file="${document}" width="${viewPixels}"></aon-viewer>`;
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
      });
    
    } else {
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

  }

  buildData() {
    if(this.isBetaDoc()){
      this.buildDataS3();
    } else {
      let card = this.getElement(this.DATA_CARD);
      card.setContentHTML('');
          let table = this.createElement(TAG.TABLE);
          table.style.width = '100%';
          card.setContent(table);

      let tr = this.createElement(TAG.TR);
      table.appendChild(tr);

      let tdDate = this.createElement(TAG.TD);
      tdDate.setAttribute('colspan', '1');
          tr.appendChild(tdDate);

      let date = createDate(this.DATE, MSG.DATE);
      if(!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()){
        date.readonly = 'true';
      }
      if(this.document.date) {
        let d = this.document.date.split('/');
        date.setDate(new Date(d[2], d[1] - 1, d[0]));
      }
      tdDate.appendChild(date);

      let tdConfidential = this.createElement(TAG.TD);
      tdConfidential.setAttribute('colspan', '1');
      tdConfidential.innerHTML = `<aon-switch id="confidential" title="${MSG.CONFIDENTIAL}"></aon-switch>`;
      tr.appendChild(tdConfidential);
      let confidential = this.getElement('confidential');
      confidential.disabled = !this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal();
      confidential.checked = this.document.confidential;
      confidential.addEventListener(EVENT.CHANGE, () => this.updateConfidential(confidential.checked));

      let tr2 = this.createElement(TAG.TR);
      table.appendChild(tr2);

      let tdName = this.createElement(TAG.TD);
      tdName.setAttribute('colspan', '2');
          tr2.appendChild(tdName);
          let name = createInput(this.NAME, MSG.NAME, tdName);
      if(!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()){
        name.readonly = 'true';
      }
      name.setValue(this.document.title);
          name.addEventListener(EVENT.CHANGE, () => this.updateName(name.value));

      let tr3 = this.createElement(TAG.TR);
      table.appendChild(tr3);

      // CATEGORY
      let tdCategory = this.createElement(TAG.TD);
      tdCategory.setAttribute('colspan', '1');
      let categorySelect = createSelect(this.CATEGORY, MSG.CATEGORY, tdCategory);
      if(!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()){
        categorySelect.readonly = 'true';
      }
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
      let scopeSelect =  createSelect(this.SCOPE, MSG.SCOPE, tdScope);
      if(!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()){
        scopeSelect.readonly = 'true';
      }
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

      let tagSelect = createSelect(this.TAG, MSG.TAG, tdTag);
      if(!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()){
        tagSelect.readonly = 'true';
      }
      tr4.appendChild(tdTag);
      tagSelect.addEventListener(EVENT.SELECT, (event) => {
        this.addTag(event.detail);
        this.setTagsAvaible();
        tagSelect.clear();
      });

      let tdType = this.createElement(TAG.TD);
      tdType.setAttribute('colspan', '1');

      let typeSelect = createSelect(this.TYPE, MSG.TYPE, tdType);
      if(!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()){
        typeSelect.readonly = 'true';
      }
      let typeOptions = EMPLOYEE_TYPE_OPTION;
      if(this.getDur().isDocumentalManager()) {
        typeOptions = ASESOR_TYPE_OPTION;
      } else if(this.getDur().isDocumentalPortal()){
        typeOptions = ENTERPRISE_TYPE_OPTION;
      }
      typeSelect.setOptions(typeOptions);
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
      // this.document.tags.forEach((item) => {
      //   this.addTag(item);
      // });
      //set tags avaibles
      this.setTagsAvaible();
    }
  }

  getInput(attributes) {
    let container = document.createDocumentFragment(); 

    attributes.forEach(attribute => {
        if (attribute.type === 'checkbox') {
            let switchAon = new AonSwitch(); 
            let el = setAttributes(switchAon, attribute);
            if (el) container.appendChild(el);
        }
    });
    return container.childNodes.length === 1 ? container.firstChild : container;
  }

  async visibleOnly(){
    if(this.getElement(ASESOR_TYPE + "_checkbox")){
      var asesor = this.getElement(ASESOR_TYPE + "_checkbox");
    }

    if(this.getElement(EMPLOYEE_TYPE + "_checkbox")){
      var employee = this.getElement(EMPLOYEE_TYPE + "_checkbox");
    }
    if (asesor && employee) {
      asesor.addEventListener(EVENT.CLICK, (event) => {
        if (asesor.value === 'false' && employee.value === 'true') {
          employee.value = 'false';
          employee.checked = false;
          asesor.value = 'true';
          asesor.checked = true;
          this.updateType(ASESOR_TYPE);
        } else if (asesor.value === 'true' && employee.value === 'false') {
          asesor.value = 'false';
          asesor.checked = false;
          this.updateType(ENTERPRISE_TYPE);
        } else if (asesor.value === 'false') {
          asesor.value = 'true';
          asesor.checked = true;
          this.updateType(ASESOR_TYPE);
        }
        event.preventDefault();
        event.stopPropagation();
      });

      employee.addEventListener(EVENT.CLICK, (event) => {
        if (employee.value === 'false' && asesor.value === 'true') {
          asesor.value = 'false';
          asesor.checked = false;
          employee.value = 'true';
          employee.checked = true;
          this.updateType(EMPLOYEE_TYPE);
        } else if (employee.value === 'true' && asesor.value === 'false') {
          employee.value = 'false';
          employee.checked = false;
          this.updateType(ENTERPRISE_TYPE);
        } else if (employee.value === 'false') {
          employee.value = 'true';
          employee.checked = true;
          this.updateType(EMPLOYEE_TYPE);
        }
        event.preventDefault();
        event.stopPropagation();
      });
    } else if(!asesor && employee) {
      employee.addEventListener(EVENT.CLICK, (event) => {
        if (employee.value === 'false') {
          employee.value = 'true';
          employee.checked = true;
          this.updateType(EMPLOYEE_TYPE);
        } else if (employee.value === 'true') {
          employee.value = 'false';
          employee.checked = false;
          this.updateType(ENTERPRISE_TYPE);
        } 
        event.preventDefault();
        event.stopPropagation();
      });
    } else {
      console.error("Los checkboxes de asesor o employee no se encuentran en el DOM.");
    }
  }

  setCheckboxesBasedOnRegistryType(document) {
    let employeeCheckbox = this.getElement(EMPLOYEE_TYPE + "_checkbox");
    let asesorCheckbox = this.getElement(ASESOR_TYPE + "_checkbox");

    let registryType = document.registryType;

    if (registryType === 'employee') {
        if (employeeCheckbox) employeeCheckbox.checked = true;
        if (asesorCheckbox) asesorCheckbox.checked = false;
    } else if (registryType === 'asesor') {
        if (employeeCheckbox) employeeCheckbox.checked = false;
        if (asesorCheckbox) asesorCheckbox.checked = true;
    } else {
        if (employeeCheckbox) employeeCheckbox.checked = false;
        if (asesorCheckbox) asesorCheckbox.checked = false;
    }
  }

  async getS3Doc() {
    let data = {
      id: this.document.id,
      type: this.document.type
    };
    const doc = await getS3Document(data);
    return doc;
  }

  async buildDataS3() {
    let s3Doc = await this.getS3Doc();
    if (this.isBetaDoc() && Array.isArray(s3Doc.tag) && !this.doc.tag) {
      this.doc.tag = s3Doc.tag.map(t => ({ id: t.id }));
    }

    let card = this.getElement(this.DATA_CARD);
    card.setContentHTML('');
    let table = this.createElement(TAG.TABLE);
    table.style.width = '100%';
    table.id = 'documentTable';
    card.setContent(table);
    let attributes = [];

    if (this.getDur().isDocumentalManager()) {
      attributes = [
        {
          type: "checkbox",
          id: ASESOR_TYPE + "_checkbox",
          name: ASESOR_TYPE,
          title: "No visible para " + MSG.ENTERPRISE
        },
        {
          type: "checkbox",
          id: EMPLOYEE_TYPE + "_checkbox",
          name: EMPLOYEE_TYPE,
          title: "Visible solo para " + MSG.EMPLOYEE,
        }
      ];
    } else if (this.getDur().isDocumentalPortal()) {
      attributes = [
        {
          type: "checkbox",
          id: EMPLOYEE_TYPE + "_checkbox",
          name: EMPLOYEE_TYPE,
          title: "Visible solo para " + MSG.EMPLOYEE,
        }
      ];
    }

    // Crear checkboxes si existen
    attributes.forEach(attribute => {
      let trCheckbox = this.createElement('tr');
      table.appendChild(trCheckbox);

      let tdCheckbox = this.createElement('td');
      trCheckbox.appendChild(tdCheckbox);

      // Crear el input usando getInput
      let el = this.getInput([attribute]);
      if (el) {
        tdCheckbox.appendChild(el);
      }
    });

    if(this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal()) {
      // Aquí pasamos el documento a la función de visibilidad
      this.setCheckboxesBasedOnRegistryType(s3Doc);
      // Llamamos a la función para manejar la visibilidad después
      this.visibleOnly();
    }

    // Fecha
    let tr = this.createElement(TAG.TR);
    tr.id = 'trDate';
    table.appendChild(tr);
    let tdDate = this.createElement(TAG.TD);
    tdDate.setAttribute('colspan', '1');
    tr.appendChild(tdDate);

    let date = createDate(this.DATE, MSG.DATE + ' del documento');
    if (!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()) {
        date.readonly = 'true';
    }
    if (s3Doc.date) {
        let d = s3Doc.date.split('-');
        date.setDate(new Date(d[0], d[1] - 1, d[2]));
    }
    tdDate.appendChild(date);
    // Si se modifica la fecha
    date.addEventListener(EVENT.CHANGE,
       (event) => this.updateDate(date.getDateValue()));

    // Nombre
    let tr2 = this.createElement(TAG.TR);
    tr2.id = 'trName';
    table.appendChild(tr2);
    let tdName = this.createElement(TAG.TD);
    tdName.setAttribute('colspan', '2');
    tr2.appendChild(tdName);
    let name = createInput(this.NAME, MSG.NAME, tdName);
    name.setValue(s3Doc.name);

    if (!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()) {
        name.setDisabled(true)
    }
    name.addEventListener(EVENT.CHANGE, () => this.updateName(name.value));
    // Scope
    let trScope = this.createElement(TAG.TR);
    trScope.id = 'trScope';
    table.appendChild(trScope);
    trScope.setAttribute('colspan', '2');
    let scopeSelect = createSelect(this.SCOPE, MSG.SCOPE + ' (solo visible...)', trScope);
    if (!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()) {
        scopeSelect.disabled = true;
    }

    getScopes().then(scopes => {
      scopeSelect.setOptions(scopes.map(s => {
        return {
          value: s.id,
          name: s.name
        };
      }));
      if (s3Doc.scope)
        scopeSelect.value = s3Doc.scope;
      scopeSelect.addEventListener(EVENT.SELECT, () => this.updateScope(scopeSelect.value));
    });

     //Tags
     getTags({ domain: localStorage.getItem('aon_domain_id') }).then(tags => {
      if (Array.isArray(tags) && tags.length > 0) {
        // Solo creamos la fila si hay etiquetas
        let trTag = this.createElement(TAG.TR);
        trTag.id = 'trTag';
        trTag.setAttribute('colspan', '1'); 
        table.appendChild(trTag);
    
        let tagSelect = createSelect(this.TAG_SELECT, MSG.TAGS, trTag);
        if (!this.getDur().isDocumentalManager() && !this.getDur().isDocumentalPortal()) {
          tagSelect.setDisabled(true);
        }
    
        tagSelect.multiple = true;
    
        tagSelect.setOptions(tags.map(t => ({
          value: t.id,
          name: t.name
        })));
    
        const input = tagSelect.getElement(tagSelect.INPUT);
        if (s3Doc.tag?.length) {
          const names = s3Doc.tag
            .map(tag => {
              const fullTag = tags.find(t => t.id === tag.id);
              return fullTag ? fullTag.name : '';
            })
            .filter(Boolean)
            .join(', ');
    
          if (input) {
            input.value = names;
          }
        }
    
        let clickHandled = false;
    
        input.addEventListener(EVENT.CLICK, () => {
          let originalTags = input.value;
          if (clickHandled) return;
    
          if (Array.isArray(s3Doc.tag)) {
            s3Doc.tag.forEach(tag => {
              const checkbox = document.getElementById(`checkbox${tag.id}`);
              if (checkbox) {
                checkbox.value = true;
              }
            });
          }
          input.value = originalTags;
          clickHandled = true;
    
          tagSelect.addEventListener(EVENT.SELECT, () => {
            const selectedTags = tagSelect.getSelectable();
    
            const selectedNames = selectedTags.map(tag => tag.name).join(', ');
            if (input) {
              input.value = selectedNames;
            } 
    
            const selectedIds = selectedTags.map(tag => tag.value).sort();
            const currentIds = (s3Doc.tag || []).map(tag => tag.id).sort();
    
            const isSame = selectedIds.length === currentIds.length &&
                           selectedIds.every((id, idx) => id === currentIds[idx]);
    
            if (!isSame) {
              this.updateTags(selectedTags);
            } else {
              console.log("No se actualizan los tags: no hay cambios.");
            }
          });
        });
      } else {
        console.log('No hay etiquetas disponibles.');
      }
    });
    //Category
    let oldCategories = await loadOldCategories();
    if(oldCategories){
      this.createCategoryRadio(table, 's3CategoriesRadio', MSG.DEFAULT_CATEGORIES, this.handleCategoryChange.bind(this));
      this.createCategoryRadio(table, 'oldCategoriesRadio', MSG.USER_CATEGORIES, this.handleCategoryChange.bind(this));

      let radioS3 = document.getElementById('s3CategoriesRadio');
      let radioOld = document.getElementById('oldCategoriesRadio');
      if(!this.getDur().isDocumentalManager()) {
        radioS3.hidden = true;
        radioOld.hidden = true;
      }
      // Cargar ruta de categorías si existe
      if (s3Doc.category) {
        const s3Categories = await getS3Category(); // Carga todas las S3
        const isS3Category = s3Categories.some(cat => cat.id === s3Doc.category && cat.is_deletable === 0);
        if (isS3Category) {
          // === Categoría tipo S3 ===
          const path = await this.getCategoryPath(s3Doc.category);
          const s3Radio = document.getElementById('s3CategoriesRadio');
          s3Radio.checked = true;
          if(!this.getDur().isDocumentalManager()) {
            s3Radio.hidden = true;
          }

          const trCategory = document.createElement('tr');
          trCategory.id = 'trCategory';
          table.appendChild(trCategory);

          const td = document.createElement('td');
          td.setAttribute('colspan', '1');
          trCategory.appendChild(td);

          const select = new AonNewSelect();
          select.id = "aonDocumentalSheetCategory";
          select.title = MSG.CATEGORY;
          td.appendChild(select);
          if(!this.getDur().isDocumentalManager()) {
            select.disabled = true;
          }

          const rootCategories = await getS3Category({ parent: null });
          select.options = JSON.stringify(rootCategories.filter(c => c.is_deletable === 0).map(c => ({
            value: c.id,
            name: c.name
          })));

          select.value = path[0].id;
          this.s3CategoryTree(select, table);

          for (let i = 1; i < path.length; i++) {
            const current = path[i];
            const parent = path[i - 1];
            const children = await getS3Category({ parent: parent.id });
            if (!children.length){
              break;
            } 

            const levelNames = ['Subcategoria', 'Administración', 'Modelos'];
            const levelIds = ['SubcategoriaSelect', 'AdministracionSelect', 'ModelosSelect'];

            const tr = document.createElement('tr');
            tr.id = `tr${levelNames[i - 1]}`;
            table.appendChild(tr);

            const td = document.createElement('td');
            td.setAttribute('colspan', '1');
            tr.appendChild(td);

            const sel = new AonNewSelect();
            sel.id = levelIds[i - 1];
            sel.title = levelNames[i - 1];
            sel.options = JSON.stringify(children.map(opt => ({
              value: opt.id,
              name: opt.name
            })));
            sel.value = current.id;
            sel.disabled = true;

            td.appendChild(sel);
          }

        } else {
          // === Categoría antigua ===
          const oldRadio = document.getElementById('oldCategoriesRadio');
          oldRadio.checked = true;
          if(!this.getDur().isDocumentalManager()) {
            oldRadio.hidden = true;
          }

          const trCategory = document.createElement('tr');
          trCategory.id = 'trCategory';
          table.appendChild(trCategory);

          const td = document.createElement('td');
          td.setAttribute('colspan', '1');
          trCategory.appendChild(td);

          const categorySelect = new AonNewSelect();
          categorySelect.id = "aonDocumentalSheetCategory";
          categorySelect.title = MSG.CATEGORY;
          if (!this.getDur().isDocumentalManager()) {
            categorySelect.disabled = true;
          }
          td.appendChild(categorySelect);

          const categories = await getCategories({ domain: localStorage.getItem('aon_domain_id') });
          categorySelect.options = JSON.stringify(categories.map(c => ({
            value: c.id,
            name: c.name
          })));

          // Esperamos a que se monten las options antes de asignar valor
          setTimeout(() => {
            categorySelect.value = s3Doc.category;
          }, 0);
          categorySelect.addEventListener(EVENT.SELECT, () => this.updateCategory(categorySelect.value));
        }
      }
    } else {
      // Solo cargamos S3 ya que no hay categorías antiguas
      this.createCategoryRadio(table, 's3CategoriesRadio', '', this.handleCategoryChange.bind(this));
      let radio = document.getElementById('s3CategoriesRadio');
      radio.hidden = true;  
      if (s3Doc.category) {
        const s3Categories = await getS3Category();
        const isS3Category = s3Categories.some(cat => cat.id === s3Doc.category && cat.is_deletable === 0);
        if (isS3Category) {
          const path = await this.getCategoryPath(s3Doc.category);
          const s3Radio = document.getElementById('s3CategoriesRadio');
          s3Radio.checked = true;

          const trCategory = document.createElement('tr');
          trCategory.id = 'trCategory';
          table.appendChild(trCategory);

          const td = document.createElement('td');
          td.setAttribute('colspan', '1');
          trCategory.appendChild(td);

          const select = new AonNewSelect();
          select.id = "aonDocumentalSheetCategory";
          select.title = MSG.CATEGORY;
          td.appendChild(select);

          const rootCategories = await getS3Category({ parent: null });
          select.options = JSON.stringify(rootCategories.filter(c => c.is_deletable === 0).map(c => ({
            value: c.id,
            name: c.name
          })));

          select.value = path[0].id;
          this.s3CategoryTree(select, table);

          for (let i = 1; i < path.length; i++) {
            const current = path[i];
            const parent = path[i - 1];
            const children = await getS3Category({ parent: parent.id });
            if (!children.length){
              break;
            } 

            const levelNames = ['Subcategoria', 'Administración', 'Modelos'];
            const levelIds = ['SubcategoriaSelect', 'AdministracionSelect', 'ModelosSelect'];

            const tr = document.createElement('tr');
            tr.id = `tr${levelNames[i - 1]}`;
            table.appendChild(tr);

            const td = document.createElement('td');
            td.setAttribute('colspan', '1');
            tr.appendChild(td);

            const sel = new AonNewSelect();
            sel.id = levelIds[i - 1];
            sel.title = levelNames[i - 1];
            sel.options = JSON.stringify(children.map(opt => ({
              value: opt.id,
              name: opt.name
            })));
            sel.value = current.id;
            sel.disabled = true;

            td.appendChild(sel);
          }
        }
      }
    }
  }

  async getCategoryPath(categoryId) {
    const categories = await getS3Category();
    const categoryMap = new Map();

    // Mapeamos por ID para acceso rápido
    categories.forEach(cat => {
      categoryMap.set(cat.id, {
        id: cat.id,
        parent: cat.parent,
        name: cat.name
      });
    });

    let path = [];

    let current = categoryMap.get(categoryId);

    while (current) {
      path.unshift(current); // Lo ponemos al principio para construir desde la raíz
      current = categoryMap.get(current.parent);
    }
    return path;
  }

  createCategoryRadio(table, radioId, labelText, onChangeCallback){
    let trRadio = this.createElement(TAG.TR);
    let tdRadio = this.createElement(TAG.TD);
    tdRadio.setAttribute('colspan', '2');
    tdRadio.style.marginTop = '10px';
    tdRadio.style.marginBottom = '10px';

    let radio = this.createElement('input');
    radio.type = 'radio';
    radio.name = 'categoryRadioGroup';
    radio.id = radioId;

    let label = this.createElement('label');
    label.setAttribute('for', radioId);
    if(!this.getDur().isDocumentalManager()) {
      label.textContent = '';
    }else{
      label.textContent = labelText;
    }

    tdRadio.appendChild(radio);
    tdRadio.appendChild(label);

    trRadio.appendChild(tdRadio);
    table.appendChild(trRadio);

    if (onChangeCallback) {
      radio.addEventListener('change', onChangeCallback);
    }
  }

  handleCategoryChange(event) {
    const table = this.getElement('documentTable');
  
    this.clearFields(table);
  
    const existingCategoryTr = document.getElementById('trCategory');
    if (existingCategoryTr) {
      existingCategoryTr.remove();
    }
  
    const radioId = event.target.id;
    const isS3 = radioId === 's3CategoriesRadio';
  
    const tr = document.createElement('tr');
    tr.id = 'trCategory';
    table.appendChild(tr);
  
    const td = document.createElement('td');
    td.setAttribute('colspan', '1');
    tr.appendChild(td);
  
    const select = new AonNewSelect();
    select.id = "aonDocumentalSheetCategory";
    select.title = MSG.CATEGORY;
    if (!this.getDur().isDocumentalManager()) {
      select.readonly = 'true';
    }
    td.appendChild(select);

  
    if (isS3) {
      getS3Category({ parent: null }).then(categories => {
        select.options = JSON.stringify(categories.filter(c => c.is_deletable === 0).map(c => ({
          value: c.id,
          name: c.name
        })));
        this.s3CategoryTree(select, table);
      });
    } else {
      getCategories({ domain: localStorage.getItem('aon_domain_id') }).then(categories => {
        select.options = JSON.stringify(categories.map(c => ({
          value: c.id,
          name: c.name
        })));
        select.addEventListener(EVENT.SELECT, () => {
          this.updateCategory(select.value);
        });
      });
    }
  }
  
  s3CategoryTree(select, table) {
    // Nivel 1: Categoría
    select.addEventListener('change', (event) => {
      const selectedCategoryId = event.target.value;
      this.clearFields(table);
  
      // Aplica el readonly si no es documental manager
      if (!this.getDur().isDocumentalManager()) {
        select.readonly = 'true';
      }
      if (selectedCategoryId !== null && selectedCategoryId !== undefined) {
        this.checkAndUpdateCategory(); // por si no hay subniveles
  
        let trSubCategory = document.createElement('tr');
        trSubCategory.id = 'trSubcategory';
        table.appendChild(trSubCategory);
  
        let tdSubCategory = document.createElement('td');
        tdSubCategory.setAttribute('colspan', '1');
  
        let selSubCat = new AonNewSelect();
        selSubCat.title = 'Subcategoria';
        selSubCat.id = 'SubcategoriaSelect';
  
        // Aplica el readonly si no es documental manager
        if (!this.getDur().isDocumentalManager()) {
          selSubCat.readonly = 'true';
        }
  
        tdSubCategory.appendChild(selSubCat);
  
        let data = { parent: selectedCategoryId };
        getS3Category(data).then(subcategories => {
          if (subcategories.length > 0) {
            selSubCat.options = JSON.stringify(subcategories.map(sc => ({
              value: sc.id,
              name: sc.name
            })));
            trSubCategory.appendChild(tdSubCategory);
  
            // Nivel 2: Subcategoría
            selSubCat.addEventListener('change', (event) => {
              const selectedSubCategoryId = event.target.value;
              this.clearFields(table, [trSubCategory]);
              if (selectedSubCategoryId != null && selectedSubCategoryId != undefined) {
                this.checkAndUpdateCategory(); //si no hay administración ni modelo
  
                let trAdministration = document.createElement('tr');
                trAdministration.id = 'trAdministration';
                table.appendChild(trAdministration);
  
                let tdAdministration = document.createElement('td');
                tdAdministration.setAttribute('colspan', '1');
  
                let selAdministration = new AonNewSelect();
                selAdministration.title = "Administración";
                selAdministration.id = 'AdministracionSelect';
  
                // Aplica el readonly si no es documental manager
                if (!this.getDur().isDocumentalManager()) {
                  selAdministration.readonly = 'true';
                }
  
                tdAdministration.appendChild(selAdministration);
  
                let data = { parent: selectedSubCategoryId };
                getS3Category(data).then(administrations => {
                  if (administrations.length > 0) {
                    selAdministration.options = JSON.stringify(administrations.map(adm => ({
                      value: adm.id,
                      name: adm.name
                    })));
                    trAdministration.appendChild(tdAdministration);
  
                    // Nivel 3: Administración
                    selAdministration.addEventListener('change', (event) => {
                      const selectedAdministrationId = event.target.value;
                      this.clearFields(table, [trSubCategory, trAdministration]);
                      if (selectedAdministrationId !== null && selectedAdministrationId !== undefined) {
                        this.checkAndUpdateCategory(); // por si no hay modelo
  
                        let trModel = document.createElement('tr');
                        trModel.id = 'trModel';
                        table.appendChild(trModel);
  
                        let tdModel = document.createElement('td');
                        tdModel.setAttribute('colspan', '1');
  
                        let selModel = new AonNewSelect();
                        selModel.id = "ModelosSelect";
                        selModel.title = "Modelos";
  
                        if (!this.getDur().isDocumentalManager()) {
                          selModel.readonly = 'true';
                        }
  
                        tdModel.appendChild(selModel);
  
                        let data = { parent: selectedAdministrationId };
                        getS3Category(data).then(models => {
                          selModel.options = JSON.stringify(models.map(mod => ({
                            value: mod.id,
                            name: mod.name
                          })));
                          trModel.appendChild(tdModel);
  
                          // Nivel 4: Modelo
                          selModel.addEventListener('change', () => {
                            this.checkAndUpdateCategory(); // definitivo
                          });
                        });
                      }
                    });
                  }
                });
              }
            });
          }
        });
      }
    });
  }
  

  checkAndUpdateCategory() {
    const modelSelect = document.getElementById("ModelosSelect");
    const adminSelect = document.getElementById("AdministracionSelect");
    const subcatSelect = document.getElementById("SubcategoriaSelect");
  
    let categoryId = null;
  
    if (modelSelect && modelSelect.value) {
      categoryId = modelSelect.value;
    } else if (adminSelect && adminSelect.value) {
      categoryId = adminSelect.value;
    } else if (subcatSelect && subcatSelect.value) {
      categoryId = subcatSelect.value;
    }
  
    if (categoryId !== null) {
      this.updateCategory(categoryId);
    }
  }

  clearFields(table, subFieldsToKeep = []) {
    const trElements = table.querySelectorAll('tr');
    let checkBoxAsesor = document.getElementById(ASESOR_TYPE + '_checkbox');
    let checkBoxEmployee = document.getElementById(EMPLOYEE_TYPE + "_checkbox");
    let trDate = document.getElementById('trDate');
    let trName = document.getElementById('trName');
    let trScope = document.getElementById('trScope'); 
    let trCategory = document.getElementById('trCategory');
    let trTag = document.getElementById('trTag');
  
    // Filtramos por si alguno no existe
    let fieldsToKeep = [trCategory, trDate, trName, trScope, trTag].filter(Boolean);
  
    trElements.forEach((tr) => {
      const radioInTr = tr.querySelector('input[type="radio"]');
  
      const shouldKeep =
        fieldsToKeep.includes(tr) ||
        subFieldsToKeep.includes(tr) ||
        (checkBoxAsesor && tr.contains(checkBoxAsesor)) ||
        (checkBoxEmployee && tr.contains(checkBoxEmployee)) ||
        radioInTr;
  
      if (!shouldKeep) {
        tr.remove();
      }
    });
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
      this._tags = this._tags.filter(tag => t.id !== tag.id && t.id !== tag.value );
      const tagEl = this.getElement(`tag${t.id}`);
      if(tagEl) tagEl.remove();
      this.setTagsAvaible();
      this.save();
    });
    this.save();
  }

  setTagsAvaible(){
    const tagSelect = this.getElement(this.TAG);
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
      documentToolbar.addSeparator();
      if(this.isBetaDoc()){
        // Solo si no eres empleado o empresa, entiendo que es este permiso
        if (this.getDur().isDocumentalManager()) {
          documentToolbar.addButton2(ACTION.DELETE_FILE, () => this.removeS3());
        }
        documentToolbar.addButton2(ACTION.DOWNLOAD_FILE, () => this.downloadS3());
      } else if(!this.isBetaDoc() && (this.getDur().isDocumentalManager() || this.getDur().isDocumentalPortal())){
        documentToolbar.addButton2(ACTION.NEXT, () => this.next());
        documentToolbar.addButton2(ACTION.PREVIOUS, () => this.previous());
        documentToolbar.addButton2(ACTION.DELETE_FILE, () => this.remove());
        documentToolbar.addButton2(ACTION.DOWNLOAD_FILE, () => this.download());
      }
      // documentToolbar.addButton2(ACTION.SEND_FILE, () => this.send());
    }
    documentToolbar.addButton2(ACTION.BACK, () => this.back());
  }

  back() {
    let aonDocumental = this.getApplication();
    aonDocumental.getParent().aonDocumentalList();
  }

  next() {
    this.getApplication().development();
  }

  previous() {
    this.getApplication().development();
  }

  send() {
    let aonDocumental = this.getApplication();
    let d = document.getElementById(aonDocumental.DIALOG);
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle(MSG.SEND_FILE);
    d.setContentHTML(MSG.IN_DEVELOPMENT);
    d.addAcceptAction(() => {});
    d.open();
  }

  
  removeS3() {
    let aonDocumental = this.getApplication();
    let d = document.getElementById(aonDocumental.DIALOG);
    d.clear();
    if(!this.isMobile()) d.width = '400px';
    d.setTitle(MSG.DELETE_FILE);
    d.setContentHTML(`Estás seguro de eliminar el Fichero ${this.document.name}`);
    d.addAcceptAction(() => {
      let data = {
        data: [
          {
            id: this.document.id,
            type: this.document.type
          }
        ]
      };      
      deleteS3Document(data).then(() => {
        this.back();
      });
    });
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
    if(this.isBetaDoc()){
      openFileUrl(this.docS3);
    } else {
      openFileUrl(this.document.file.url);
    }
  }

  async downloadS3() {
    let data = [
      {
        id: this.document.id,
        type: this.document.type
      }
    ];    
    let json = JSON.stringify(data);
		let i = await downloadS3Documents(encodeURI(json));
		const url = URL.createObjectURL(i);
		const a = document.createElement('a');
		a.href = url;
		a.download = 'documento.zip';
		a.click();
		URL.revokeObjectURL(url);
  }
  updateCategory(category) {
    if(this.isBetaDoc()){
      this.doc.category = category
    }else{
      if(!this.doc.category)
      this.doc.category = {};
      this.doc.category.id = category;
    }
    this.save();
  }

  updateScope(scope) {
    if(this.isBetaDoc()){
      this.doc.scope = scope
    }else{
      if(!this.doc.scope)
        this.doc.scope = {};
        this.doc.scope.id = scope;
    }
    this.save();
  }

  updateType(type) {
    if(this.isBetaDoc()){
      this.doc.registryType = type
    }else{
      this.doc.type = type;
    }
    this.save();
  }

  updateConfidential(confidential) {
    this.doc.confidential = confidential;
    this.save();
  }

  updateName(name) { 
    if(this.isBetaDoc()){ 
      this.doc.name = name;
    } else {
      this.doc.title = name;
    }

    this.save();
  }
  
  updateDate(newDate){
    this.doc.date = newDate;
    this.save();
  }

  updateTags(tags) {  
    const tagsArray = Array.isArray(tags)
      ? tags.map(tag => ({ id: parseInt(tag.value) })) 
      : [{ id: parseInt(tags.value) }];
  
    this.doc.tag = tagsArray;
    this.save();
  }
  
  save() {
    if(this.isBetaDoc()){
      putS3DocumentUpdate(this.doc);
    } else {
      let d = {
        id          : this.doc.id,
        name        : this.doc.title,
        confidential: this.doc.confidential,
        category    : this.doc.category ? this.doc.category.id : undefined,
        scope       : this.doc.scope ? this.doc.scope.id : undefined,
        type        : this.doc.type,
        tags        : this._tags.map(t => t.id || t.value)
      };
      updateFile(d);
    }
  }

}

window.customElements.define('aon-document',  AonDocument);
