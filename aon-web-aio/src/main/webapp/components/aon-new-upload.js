import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
// import { Attach } from '../models/Attach.js';
// import { AonDialog } from './aon-dialog.js';
// import { AonIconButton } from './aon-icon-button.js';
import { AonElement } from './AonElement.js';
import * as LS from "../services/localStorageService.js";
import { AonIcon } from './aon-icon.js';

export class AonNewUpload extends AonElement {
    DIV;
    INPUT;
    LABEL;
    TEXT;
    type;
    accept;

    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

    setType(type) {
        this.type = type;
    }
    
    setMessage(message) {
        this.message = message;
    }

    setAccept(accept) {
        this.accept = accept;
    }

    getInput() {
        return this.getElement(this.INPUT);
    }
    
    constructor() {
		super();
	}

	connectedCallback() {
        this.initialize();
        this.build();
	}

    initialize() {
        const id = this.id || `aonNewUpload-${generateSimpleUUID()}`;
        this.id = id;
        this.DIV = this.id + 'Div';
        this.INPUT = this.id + 'Input';
        this.LABEL = this.id + 'Label';
        this.TEXT = this.id + 'Span';
        this.message = this.message || MSG.ATTACH_FILES_DRAGGING_DROPPING;
        this.accept = this.accept || 'image/jpeg, image/png, application/pdf';
        this.type = this.type || 'Documental';
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.classList.add(CSS.AON_UPLOAD, "upload-component");
        this.appendChild(div);
        this.builDragAndDrop(div);

        let input = this.createElement(TAG.INPUT);
        input.id = this.INPUT;
        input.type = CONSTANT.FILE;
        input.accept = this.accept;
        input.className = CSS.AON_NONE;
        input.multiple = 'multiple';
        input.addEventListener(EVENT.CHANGE, ({target}) => {
            let desktop = this.getElement('aonDesktop');
            let invoiceHome = this.getElement('aonInvoiceHome'); 
			let accountingBeta = this.getElement('aonAccountingBetaId');
            if(desktop) {
                if(this.type === "Documental"){
                    desktop.uploadDocumentsDesktop(input, target.files);
                } else if(this.type === "Invoice"){
                    desktop.uploadInvoiceDesktop(input, target.files);
                }
            } else if(invoiceHome) {
                invoiceHome.uploadInvoiceHome(input, target.files);
            } else if(accountingBeta) {
				accountingBeta.uploadInvoiceAccounting(input, target.files);
			}
        });
        div.appendChild(input);
        
        div.addEventListener(EVENT.CLICK, (ev) => {
          if(this.isClasic()){
            ev.preventDefault();
            ev.stopPropagation();
          }
          input.click();  
        });

        let label = this.createElement(TAG.LABEL);
        label.id = this.LABEL;
        label.classList.add(CSS.AON_NEW_UPLOAD_LABEL, "upload-component-label");
        div.appendChild(label);

        let uploadIcon  = new AonIcon;
        uploadIcon.icon = MATERIAL_ICONS.FILE_UPLOAD;
        label.appendChild(uploadIcon);

        let text       = this.createElement(TAG.DIV);
        text.id        = this.TEXT;
        text.innerHTML = this.message;
        text.classList.add(CSS.AON_NEW_UPLOAD_LABEL, "upload-component-text");
        label.appendChild(text);
    }

    builDragAndDrop(element) {
        const preventDefault = (ev)=> {
            ev.preventDefault();
            ev.stopPropagation();
        }
        const dragoverFn = (event) => {
            preventDefault(event);
            if(LS.isDarkTheme())
                element.style.borderColor = "white";
            else
                element.style.borderColor = "#002469";
        };
        
        const dragenterFn = (event) => {
            preventDefault(event);
            if(LS.isDarkTheme())
                element.style.borderColor = "white";
            else
                element.style.borderColor = "#002469";
        };
        
        const mouseleaveFn = (event) => {
            preventDefault(event);
            element.style.borderColor = "#aaa";
        };
        
        const mouseoverFn = (event) => {
            preventDefault(event);
            if(LS.isDarkTheme())
                element.style.borderColor = "white";
            else
                element.style.borderColor = "#002469";
        };
        
        const dragleaveFn = (event) => {
            preventDefault(event);
            let isClickInside = element.contains(event.target) || element === event.target;
            if (!isClickInside) {
                element.style.borderColor = "#aaa";
            }
        }
        
        const dropFn = (event) => {
            preventDefault(event);
            
            element.style.borderColor = "#aaa";
  
            if(event && event.dataTransfer && event.dataTransfer.files){
                let files = event.dataTransfer.files;
                let desktop = this.getElement('aonDesktop');
                let invoiceHome = this.getElement('aonInvoiceHome'); 
				let accountingBeta = this.getElement('aonAccountingBetaId');
                if(desktop) {
                    if(this.type === "Documental"){
                        desktop.uploadDocumentsDesktop(element, files);
                    } else if(this.type === "Invoice"){
                        desktop.uploadInvoiceDesktop(element, files);
                    }
                } else if(invoiceHome) {
                    invoiceHome.uploadInvoiceHome(element, files);
                } else if(accountingBeta) {
					accountingBeta.uploadInvoiceAccounting(element, files);
				}

            }
        };

        element.addEventListener(EVENT.DRAGOVER, dragoverFn);
        element.addEventListener(EVENT.DRAGENTER, dragenterFn);
        document.addEventListener(EVENT.DRAGLEAVE, dragleaveFn);
        element.addEventListener(EVENT.DROP, dropFn);
    }
}

if(!window.customElements.get(TAG.AON_NEW_UPLOAD)){
	window.customElements.define(TAG.AON_NEW_UPLOAD, AonNewUpload);
}