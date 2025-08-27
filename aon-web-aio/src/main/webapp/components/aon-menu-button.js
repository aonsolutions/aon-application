import { AonElement } from "./AonElement";
import Apps from "../services/app.js";
import { CONSTANT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { AonDialog } from "./aon-dialog";
import { AonUploadToast } from "./aon-upload-toast";
import { AonIconButton } from "./aon-icon-button";
import { AonIcon } from "./aon-icon";
import { OPTION } from "../environments/constants.js";
import { AonInvoicePanel } from "../modules/invoice/aon-invoice-panel.js";
import { EVENT } from "../environments/materialIcons.js";
import { AonMessenger } from "../modules/messenger/aon-messenger.js";
import { TASK_SOURCE } from "../modules/messenger/MessengerEnums.js";
import { uploadOption } from "../modules/documental/DocumentalUtils.js";

export class AonMenuButton extends AonElement {
    dur;
    BUTTON;
    CONTENT;
    LIST;
    SUBLIST;
    options = [];

    constructor() {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.init().then(() => {
            this.loadDefaultOptions();
        }).catch((error) => {
            console.error('Error en init:', error);
        });
        this.handleOutsideClick();
    }

    initialize() {
        this.id      = 'aon-menu-button';
        this.BUTTON  = this.id + '-button';
        this.CONTENT = this.id + '-content';
        this.LIST    = this.id + '-list';
    }

    init() {
      return new Promise((resolve, reject) => {
        this.buildDur().then((buildDurResult) => {
            this.dur = buildDurResult;
            this.clear();
            this.build();
            resolve(buildDurResult);
        }).catch((error) => {
            console.error('Error en buildDur:', error);
            reject(error);
        });
      });
    }

    build() {
      //eliminar contenido previo si hubiera
      this.innerHTML = '';

      // contenido del menu
      const content = this.createElement(TAG.DIV);
      content.id    = this.CONTENT;
      this.appendChild(content);

      // boton
      let button  = new AonIconButton();
      button.id   = this.BUTTON;
      button.icon = 'plus-circle';
      button.addEventListener('click', (e) => {
        e.stopPropagation();
        this.toggle();
      });
      // Agregar el boton
      content.appendChild(button);

      // lista de opciones
      const ul = this.createElement(TAG.UL);
      ul.id    = this.LIST;
      content.appendChild(ul);
    }

    setOptions(options = []) {
        this.options = options;

        const ul = this.getElement(this.LIST);
        if (!ul)
          return;
        ul.innerHTML = '';

        options.forEach((option) => {
            const li     = this.createElement(TAG.LI);
            li.className = 'aonAppLi';
            li.title     = option.name;
            
            const span = this.createElement(TAG.SPAN);
            span.textContent = option.name;
            if (option.icon) {
              const icon  = new AonIcon();
              icon.id     = this.AON_ICON;
              icon.icon   = option.icon;
              span.appendChild(icon);
            }
            li.appendChild(span);

            if (option.subOptions) {
                // crear sublista oculta inicialmente
                const subUl     = this.createElement(TAG.UL);
                subUl.className = 'aonSubUl hidden';

                option.subOptions.forEach(sub => {
                    const subLi     = this.createElement(TAG.LI);
                    subLi.className = 'aonAppLi sub-option';

                    const subSpan       = this.createElement(TAG.SPAN);
                    subSpan.title       = sub.name;
                    subSpan.textContent = sub.name;
                    if (sub.icon) {
                      const subIcon = new AonIcon();
                      subIcon.id    = this.AON_ICON;
                      subIcon.icon  = sub.icon;
                      subSpan.appendChild(subIcon);
                    }
                    subSpan.addEventListener('click', () => {
                      sub.fn();
                      this.hide();
                    });

                    subLi.appendChild(subSpan);
                    subUl.appendChild(subLi);
                });

                li.appendChild(subUl);

                span.addEventListener('click', (e) => {
                    e.stopPropagation();
                    // alternar la visibilidad de la sublista
                    span.classList.toggle('submenu-open');
                    subUl.classList.toggle('hidden');
                });

            } else {
                span.addEventListener('click', () => {
                    if (option.fn) option.fn();
                    this.hide();
                });
            }

            ul.appendChild(li);
        });
    }
    
    loadDefaultOptions() {
      let optionsMenu = [];

      const invoiceSubOptions = [
        {
          name: MSG.ISSUEDS,
          fn: () => this.newInvoice('Emitida')
        },
        {
          name: MSG.RECEIVEDS,
          fn: () => this.newInvoice('Recibida')
        },
        {
          name: MSG.TICKETS + " / " + MSG.SUPPORTING_DOCUMENTS,
          fn: () => this.newInvoice('Ticket')
        },
        {
          name: 'Subir Factura',
          fn: () => this.newInvoice('Subir Factura')
        }
      ];

      optionsMenu.push({
        name      : 'Nueva Factura',
        icon      : 'file-plus-2',
        subOptions: invoiceSubOptions // aqui anidamos las 4 opciones
      });

      const gastosSubOptions = [
        {
          name: 'Nuevo ingreso',
          fn: () => this.getApplication().setContent(new AonIncome(new Income()))
        }, {
          name: "Nuevo gasto",
          fn: () => this.getApplication().setContent(new AonExpense(new Expense()))
        }
      ];

      optionsMenu.push({
        name: 'Otros gastos/ingresos',
        icon: 'coins',
        subOptions: gastosSubOptions
      });

      optionsMenu.push({
        name: MSG.UPLOAD_DOCUMENT,
        icon: 'upload-cloud',
        fn  : () => this.addDocumentalFile()
      });

      if (this.getDur().isMessenger() || this.getDur().isMessengerManager()) {
        optionsMenu.push({
          name: MSG.CREATE_QUERY,
          icon: 'concierge-bell',
          fn  : () => {
            let aonMessengerChat = new AonMessenger();
            aonMessengerChat.data = { source: TASK_SOURCE.QUERY };
            this.rootPanel(aonMessengerChat);
          }
        });
      }
      if ((this.getDur().isPayroll() && this.getDur().isMessenger()) || (this.getDur().isPayrollManager() && this.getDur().isMessengerManager())) {
        optionsMenu.push({
          name: MSG.NEW_EMPLOYEE,
          icon: 'user-plus',
          fn  : () => {
            let aonMessengerChat = new AonMessenger();
            aonMessengerChat.data = { source: TASK_SOURCE.REQUEST };
            this.rootPanel(aonMessengerChat);
          }
        });
      }

      this.setOptions(optionsMenu);
    }
 
    //funcion de aon-menu
    newInvoice(invoice) {
        let invoicePanel = new AonInvoicePanel();
        invoicePanel.option = OPTION.CREATE_INVOICE_ISSUED;
        invoicePanel.addEventListener(EVENT.BUILD, () => invoicePanel.aonInvoice(invoice));
        this.rootPanel(invoicePanel);
        this.setAppClassName(Apps.INVOICE);
        this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_SELECT, { detail: { app: Apps.INVOICE } }));
    }
    
    show() {
      const button = this.getElement(this.BUTTON);
      const list   = this.getElement(this.LIST);
      if (list) {
        button.setIcon('x-circle');
        this.setAttribute('opened', 'true');
      }
    }

    hide() {
      const button = this.getElement(this.BUTTON);
      const list   = this.getElement(this.LIST);
      if (list) {
        // para el subMenu todos los 'li' dentro del 'ul'
        const lis = list.querySelectorAll(TAG.LI);
        lis.forEach(li => {
          const span = li.querySelector(TAG.SPAN);
          if (span && span.classList.contains('submenu-open')) {
            span.classList.remove('submenu-open');
            const subMenu = li.querySelector(TAG.UL);
            if (subMenu) {
              subMenu.classList.add('hidden');
            }
          }
        });
        button.setIcon('plus-circle');
        this.setAttribute('opened', 'false');
      }
    }

    handleOutsideClick() {
      if (this._outsideClickHandler)
        return; 
      this._outsideClickHandler = (e) => {
        const list = this.getElement(this.LIST);
        if (list && !this.contains(e.target)) {
          this.hide();
        }
      };
      document.addEventListener('click', this._outsideClickHandler);
    }

    disconnectedCallback() {
        if (this._outsideClickHandler) {
            document.removeEventListener('click', this._outsideClickHandler);
        }
    }

    isOpen() {
        return this.getAttribute('opened') === 'true';
    }

    toggle() {
        if (this.isOpen()) {
            this.hide();
        } else {
            this.show();
        }
    }
    //funcion de aon-menu
    async isElementLoaded(selector) {
        while (document.querySelector(selector) === null) {
            await new Promise(resolve => requestAnimationFrame(resolve))
        }
        return document.querySelector(selector);
    };
    //funcion de aon-menu
    setAppClassName(app) {
        let appsDiv = this.getElement("aonMenuLeftop-applications");
        let appName = app.app[0].toUpperCase() + app.app.slice(1);
        appsDiv.className = `${CSS.AON_MENU_LEFTOP}${appName}`
    }

    addDocumentalFile() {
        const overlay = document.createElement('div');
        overlay.className = 'aonDialogOverlay';

        const d = new AonDialog();
        d.id = 'aonDocumentalDialogDialogActionAccept';
        d.classList.add('aonDialogContainer');

        const rootPanel = document.getElementById("rootPanel");
        rootPanel.appendChild(overlay);
        rootPanel.appendChild(d);

        d.clear();
        if (!this.isMobile()){
            d.width = '400px';
        } 

        d.setTitle(MSG.UPLOAD_FILE);
        d.setContent(uploadOption(this.getDur(), true));

        d.addAcceptAction(async () => {
          let data = {};
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

          let uploadToast = document.getElementById('aonUploadToast');
          if (!uploadToast) {
              uploadToast = new AonUploadToast();
              rootPanel.appendChild(uploadToast);
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
    }
}

if (!window.customElements.get('aon-menu-button')) {
    window.customElements.define('aon-menu-button', AonMenuButton);
}
