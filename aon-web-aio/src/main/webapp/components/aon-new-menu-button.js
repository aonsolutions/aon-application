import { AonElement } from "./AonElement";
import { CONSTANT, MATERIAL_ICONS, MSG } from '../environments/environments.js';
import { OPTION } from "../environments/constants.js";
import { AonInvoicePanel } from "../modules/invoice/aon-invoice-panel.js";
import { EVENT } from "../environments/materialIcons.js";
import { AonMessenger } from "../modules/messenger/aon-messenger.js";
import { TASK_SOURCE } from "../modules/messenger/MessengerEnums.js";
import Apps from "../services/app.js";
import { AonDialog } from "./aon-dialog.js";
import { AonUploadToast } from "./aon-upload-toast.js";
import { uploadOption } from "../modules/documental/DocumentalUtils.js";


export class AonNewMenuButton extends AonElement {

    DIV;
    CONTENT;
    LIST;
    TRIGGER;
    dur;

    options = [];

    constructor() {
        super();
    }

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
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
        this.id = this.id || 'aonNewMenuButton';
        this.DIV = this.id + 'Div';
        this.CONTENT = this.id + 'Content';
        this.LIST = this.id + 'List';
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

        // boton visible con icono y texto
        const trigger = this.createElement('button');
        trigger.className = 'aonNewMenuTrigger';

        // crear icono
        const icon = this.createElement('i');
        icon.id = 'aonMenuListAppImgTop-new';
        icon.className = 'material-symbols-outlined aonNewMenuAppIcon';
        icon.setAttribute('data-icon', MATERIAL_ICONS.ADD_CIRCLE_OUTLINE);
        icon.textContent = MATERIAL_ICONS.ADD_CIRCLE_OUTLINE;

        // crear texto
        const span = this.createElement('span');
        span.id = 'aonMenuListAppTitle-new';
        span.className = 'aonNewMenuAppSpan';
        span.textContent = 'Nuevo';

        // añadir al boton
        trigger.appendChild(icon);
        trigger.appendChild(span);

        trigger.addEventListener('click', (e) => {
            e.stopPropagation();
            this.toggle();
        });

        this.appendChild(trigger);
        this.TRIGGER = trigger;

        // contenedor del menu
        const div = this.createElement('div');
        div.id = this.DIV;
        div.className = 'aonDialog';
        //borrar, es para testeo
        div.style.display = 'none';
        this.appendChild(div);

        // contenido del menu
        const content = this.createElement('div');
        content.id = this.CONTENT;
        content.className = 'aonNewMenuButtonContent';
        div.appendChild(content);

        // lista de opciones
        const ul = this.createElement('ul');
        ul.id = this.LIST;
        ul.className = 'aonUl';
        content.appendChild(ul);
    }

    setOptions(options = []) {
        this.options = options;

        const ul = this.getElement(this.LIST);
        if (!ul) return;
        ul.innerHTML = '';

        options.forEach((option) => {
            const li = this.createElement('li');
            li.className = 'aonAppLi';

            if (option.icon) {
                const icon = this.createElement('i');
                icon.className = 'material-symbols-outlined aonNewMenuAppIcon';
                icon.setAttribute('data-icon', option.icon);
                icon.textContent = option.icon;
                li.appendChild(icon);
            }

            const span = this.createElement('span');
            span.title = option.name;
            span.textContent = option.name;
            li.appendChild(span);

            if (option.subOptions) {
                // crear sublista oculta inicialmente
                const subUl = this.createElement('ul');
                subUl.className = 'aonSubUl'; 
                subUl.style.display = 'none';

                option.subOptions.forEach(sub => {
                    const subLi = this.createElement('li');
                    subLi.className = 'aonAppLi sub-option';

                    const subIcon = this.createElement('i');
                    subIcon.className = 'material-symbols-outlined aonNewMenuAppIcon';
                    subIcon.setAttribute('data-icon', sub.icon);
                    subIcon.textContent = sub.icon;
                    subLi.appendChild(subIcon);

                    const subSpan = this.createElement('span');
                    subSpan.title = sub.name;
                    subSpan.textContent = sub.name;
                    subLi.appendChild(subSpan);

                    subLi.addEventListener('click', () => {
                        sub.fn();
                        this.hide();
                    });

                    subUl.appendChild(subLi);
                });

                li.appendChild(subUl);

                li.addEventListener('click', (e) => {
                    e.stopPropagation();
                    // alternar la visibilidad de la sublista
                    subUl.style.display = (subUl.style.display === 'none') ? 'block' : 'none';
                });

            } else {
                li.addEventListener('click', () => {
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
                name: MSG.TICKETS + "/" + MSG.SUPPORTING_DOCUMENTS,
                fn: () => this.newInvoice('Ticket')
            },
            {
                name: 'Subir Factura',
                fn: () => this.newInvoice('Subir Factura')
            }
        ];

        optionsMenu.push({
            name: 'Nueva Factura',
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
                subOptions: gastosSubOptions
		});

        optionsMenu.push({
            name: MSG.UPLOAD_DOCUMENT,
            fn: () => this.addDocumentalFile()
        })

        if (this.getDur().isMessenger() || this.getDur().isMessengerManager()) {
            optionsMenu.push({
                name: MSG.CREATE_QUERY,
                fn: () => {
                    let aonMessengerChat = new AonMessenger();
                    aonMessengerChat.data = { source: TASK_SOURCE.QUERY };
                    this.rootPanel(aonMessengerChat);
                }
            });
        }
        if ((this.getDur().isPayroll() && this.getDur().isMessenger()) || (this.getDur().isPayrollManager() && this.getDur().isMessengerManager())) {
            optionsMenu.push({
                name: MSG.NEW_EMPLOYEE,
                fn: () => {
                    let aonMessengerChat = new AonMessenger();
                    aonMessengerChat.data = { source: TASK_SOURCE.REQUEST };
                    this.rootPanel(aonMessengerChat);
                }
            });
        }

        this.setOptions(optionsMenu);
    }
 
    //funcion de aon-new-menu
    newInvoice(invoice) {
        let invoicePanel = new AonInvoicePanel();
        invoicePanel.option = OPTION.CREATE_INVOICE_ISSUED;
        invoicePanel.addEventListener(EVENT.BUILD, () => invoicePanel.aonInvoice(invoice));
        this.rootPanel(invoicePanel);
        // this.setAppClassName(Apps.INVOICE);
        this.dispatchEvent(new CustomEvent(EVENT.AON_APPLICATION_SELECT, { detail: { app: Apps.INVOICE } }));
    }
    
    show() {
    const div = this.getElement(this.DIV);
    if (div) {
        const subMenus = div.querySelectorAll('ul.aonSubUl');
        subMenus.forEach(subMenu => {
            subMenu.style.display = 'none';
        });

        div.style.display = 'block';
        this.setAttribute('opened', 'true');
    }
}


    hide() {
        const div = this.getElement(this.DIV);
        if (div) {
            div.style.display = 'none';
            this.setAttribute('opened', 'false');
        }
    }


   handleOutsideClick() {
    if (this._outsideClickHandler) return; 
    this._outsideClickHandler = (e) => {
        const div = this.getElement(this.DIV);
            if (div && !this.contains(e.target)) {
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
    //funcion de aon-new-menu
    async isElementLoaded(selector) {
        while (document.querySelector(selector) === null) {
            await new Promise(resolve => requestAnimationFrame(resolve))
        }
        return document.querySelector(selector);
    };
    //funcion de aon-new-menu
    // setAppClassName(app) {
    //     let appsDiv = this.getElement("aonMenuLeftop-applications");
    //     let appName = app.app[0].toUpperCase() + app.app.slice(1);
    //     appsDiv.className = `${CSS.AON_MENU_LEFTOP}${appName}`
    // }

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

if (!window.customElements.get('aon-new-menu-button')) {
    window.customElements.define('aon-new-menu-button', AonNewMenuButton);
}
