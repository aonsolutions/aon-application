import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { getReader } from '../services/utils.js';
import { AonIconButton } from './aon-icon-button.js';
import { AonElement } from './AonElement.js';

export class AonUpload extends AonElement {
	
    DIV;
    INPUT;
    LABEL;
    SPAN;
    DELETE_BUTTON;

    message;
    accept;

    showDeleteButton;
    data;

    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
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
        this.DELETE_BUTTON = this.id + 'DeleteButton';
        this.message = this.message || MSG.ATTACH_FILES_DRAGGING_DROPPING;
        this.accept = this.accept || 'image/jpeg, image/png';
    }

    build() {
        let div = this.createElement(TAG.DIV);
        div.id = this.DIV;
        div.className = CSS.AON_UPLOAD;
        this.appendChild(div);
        this.builDragAndDrop(div);

        let input = this.createElement(TAG.INPUT);
        input.id = this.INPUT;
        input.type = CONSTANT.FILE;
        input.accept = this.accept;
        input.className = CSS.AON_NONE;
        input.addEventListener(EVENT.CHANGE, () => {
            this.upload(input.files[0]);
            input.value = "";
        });
        div.appendChild(input);
        div.addEventListener(EVENT.CLICK, () => {
          input.click();  
        });

        let label = this.createElement(TAG.LABEL);
        label.id = this.LABEL;
        label.className = CSS.AON_UPLOAD_LABEL;
        div.appendChild(label);

        let uploadIcon = this.createElement(TAG.I);
        uploadIcon.className = CSS.MATERIAL_ICONS;
        uploadIcon.innerHTML = MATERIAL_ICONS.CLOUD_UPLOAD;
        label.appendChild(uploadIcon);
        
        let span = this.createElement(TAG.SPAN);
        span.id = this.SPAN;
        span.innerHTML = this.message;
        label.appendChild(span);

        let button = new AonIconButton();
        button.id = this.DELETE_BUTTON;
        button.icon = MATERIAL_ICONS.CLOSE;
        if(!this.showDeleteButton) {
            button.classList.add(CSS.AON_NONE);
        }
        button.style.position = 'absolute';
        button.style.top = '0px';
        button.style.right = '0px';
        button.addEventListener(EVENT.CLICK, (e) => {
            e.stopPropagation();
            e.preventDefault();
            button.classList.add(CSS.AON_NONE);
            this.dispatchEvent(new Event(EVENT.DELETE))
        });
       div.appendChild(button);
    }

    builDragAndDrop(element) {
        const dragoverFn = (event) => {
            event.preventDefault();
            console.log(EVENT.DRAGOVER);
            element.style.borderColor = "#002469";
        };
        
        const dragenterFn = (event) => {
            event.preventDefault();
            element.style.borderColor = "#002469";
        };
        
        const mouseleaveFn = (event) => {
            element.style.borderColor = "#aaa";
        };
        
        const mouseoverFn = (event) => {
            element.style.borderColor = "#002469";
        };
        
        const dragleaveFn = (event) => {
            event.preventDefault();
            let isClickInside = element.contains(event.target) || element === event.target;
            if (!isClickInside) {
                element.style.borderColor = "#aaa";
            }
        }
        
        const dropFn = (event) => {
            event.preventDefault();
            element.style.borderColor = "#aaa";
            if(event && event.dataTransfer && event.dataTransfer.files){
                this.upload(event.dataTransfer.files[0]);
            }
        };

        element.addEventListener(EVENT.DRAGOVER, dragoverFn);
        element.addEventListener(EVENT.DRAGENTER, dragenterFn);
        // element.addEventListener(EVENT.MOUSELEAVE, mouseleaveFn);
        // element.addEventListener(EVENT.MOUSEOVER, mouseoverFn);
        document.addEventListener(EVENT.DRAGLEAVE, dragleaveFn);
        element.addEventListener(EVENT.DROP, dropFn);
    }

    upload(file) {
        this.getElement(this.DELETE_BUTTON).classList.remove(CSS.AON_NONE);
        this.dispatchEvent(new CustomEvent(EVENT.UPLOAD, { detail: file}));
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

    setShowDeleteButton(showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }
}
if(!window.customElements.get(TAG.AON_UPLOAD)){
	window.customElements.define(TAG.AON_UPLOAD, AonUpload);
}