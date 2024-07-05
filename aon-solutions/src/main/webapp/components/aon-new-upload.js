import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { Attach } from '../models/Attach.js';
import { AonDialog } from './aon-dialog.js';
import { AonIconButton } from './aon-icon-button.js';
import { AonElement } from './AonElement.js';
import * as LS from "../services/localStorageService.js";

export class AonNewUpload extends AonElement {
	
    DIV;
    INPUT;
    LABEL;
    SPAN;

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
        this.id = this.id || 'aonUpload';
        this.DIV = this.id + 'Div';
        this.INPUT = this.id + 'Input';
        this.LABEL = this.id + 'Label';
        this.SPAN = this.id + 'Span';
        this.message = this.message || MSG.ATTACH_FILES_DRAGGING_DROPPING;
        this.accept = this.accept || 'image/jpeg, image/png, application/pdf';

        this.type = this.type || 'Documental';

        this.style.flex = "1";
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.className = CSS.AON_UPLOAD;
        div.style.height = 'auto';
        this.appendChild(div);
        this.builDragAndDrop(div);

        let input = this.createElement(TAG.INPUT);
        input.id = this.INPUT;
        input.type = CONSTANT.FILE;
        input.accept = this.accept;
        input.className = CSS.AON_NONE;
        input.addEventListener(EVENT.CHANGE, ({target}) => {
            let desktop = this.getElement('aonDesktop');
            let invoiceHome = this.getElement('aonInvoiceHome'); 
            if(desktop) {
                if(this.type === "Documental"){
                    desktop.uploadDocumentsDesktop(input, target.files);
                } else if(this.type === "Invoice"){
                    desktop.uploadInvoiceDesktop(input, target.files);
                }
            } else if(invoiceHome) {
                invoiceHome.uploadInvoiceHome(input, target.files);
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
        label.className = CSS.AON_NEW_UPLOAD_LABEL;
        div.appendChild(label);

        let span = this.createElement(TAG.SPAN);
        span.id = this.SPAN;
        span.innerHTML = this.message;
        label.appendChild(span);

        let uploadIcon = this.createElement(TAG.I);
        uploadIcon.className = CSS.MATERIAL_ICONS;
        uploadIcon.innerHTML = MATERIAL_ICONS.CLOUD_UPLOAD;
        label.appendChild(uploadIcon);
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
                if(desktop) {
                    if(this.type === "Documental"){
                        desktop.uploadDocumentsDesktop(element, files);
                    } else if(this.type === "Invoice"){
                        desktop.uploadInvoiceDesktop(element, files);
                    }
                } else if(invoiceHome) {
                    invoiceHome.uploadInvoiceHome(element, files);
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