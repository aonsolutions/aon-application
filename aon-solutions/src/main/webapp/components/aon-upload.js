import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { Attach } from '../models/Attach.js';
import { AonDialog } from './aon-dialog.js';
import { AonIconButton } from './aon-icon-button.js';
import { AonElement } from './AonElement.js';

export class AonUpload extends AonElement {
	
    DIV;
    INPUT;
    IMG;
    LABEL;
    SPAN;
    DELETE_BUTTON;
    DIALOG;

    message;
    deleteMessage;

    accept;

    showDeleteButton;

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
        this.IMG = this.id + 'Img';
        this.LABEL = this.id + 'Label';
        this.SPAN = this.id + 'Span';
        this.DELETE_BUTTON = this.id + 'DeleteButton';
        this.DIALOG = this.id + 'Dialog';
        this.message = this.message || MSG.ATTACH_FILES_DRAGGING_DROPPING;
        this.deleteMessage = this.deleteMessage || 'Estás seguro de eliminar el fichero';
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
        input.addEventListener(EVENT.CHANGE, (ev) => {
            ev.preventDefault();
            ev.stopPropagation();
      
            const [file] = ev.target.files

            this.upload(file);

            input.value = "";
        });
        div.appendChild(input);
        
        div.addEventListener(EVENT.CLICK, (ev) => {
          if(this.isClasic()){
            ev.preventDefault();
            ev.stopPropagation();
          }
          input.click();  
        });

        let img = this.createElement(TAG.IMG);
        img.id = this.IMG;
        img.style.maxHeight = "100%";
        img.style.maxWidth = "100%";
        img.className = CSS.AON_NONE;
        div.appendChild(img);

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
        button.addEventListener(EVENT.CLICK, (ev) => {
            ev.preventDefault();
            ev.stopPropagation();
            this.deleteFile();
        });
       div.appendChild(button);
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
                const [file] =  event.dataTransfer.files;
            
                this.upload(file);
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
        if(file.type.includes('image')) {
            let img = this.getElement(this.IMG);
            img.src = URL.createObjectURL(file);
            img.classList.remove(CSS.AON_NONE);
            let label = this.getElement(this.LABEL);
            label.classList.add(CSS.AON_NONE);
        }
        this.setShowDeleteButton(true);
        this.dispatchEvent(new CustomEvent(EVENT.UPLOAD, { detail: file}));
    }

    setMessage(message) {
        this.message = message;
    }

    setDeleteMessage(deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    setAccept(accept) {
        this.accept = accept;
    }

    getInput() {
        return this.getElement(this.INPUT);
    }

    deleteFile() {
        let d = this.getElement(this.DIALOG);
        if(!d) {
            d = new AonDialog();
            d.id = this.DIALOG;
            this.appendChild(d);
        }
        d.clear();
        d.setTitle(MSG.DELETE_FILE);
        d.width = '400px';
        d.setContentHTML(this.deleteMessage);
        d.addAcceptAction(() => {
            let deleteButton = this.getElement(this.DELETE_BUTTON);
            deleteButton.classList.add(CSS.AON_NONE);
            let img = this.getElement(this.IMG);
            img.classList.add(CSS.AON_NONE);

            let label = this.getElement(this.LABEL);
            label.classList.remove(CSS.AON_NONE);
            this.dispatchEvent(new Event(EVENT.DELETE));
        });
        d.open();
    }

    setAttach(attach) {
        let att = new Attach(attach);
        if(att.getContentType().includes("image")){
            let img = this.getElement(this.IMG);
            let data = {
                domain_id: att.getDomain().getId(),
                attach_type: att.getAttachType(),
                domain_name: att.getDomain().getName(),
                id: att.getId()
            };
            let url = location.href + 'ms/api/file/' + btoa(JSON.stringify(data));
            img.src = url;
            img.classList.remove(CSS.AON_NONE);

            let label = this.getElement(this.LABEL);
            label.classList.add(CSS.AON_NONE);
        }
        this.setShowDeleteButton(true);
    }  

    setShowDeleteButton(showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
        let deleteButton = this.getElement(this.DELETE_BUTTON);
        if(deleteButton && this.showDeleteButton) {
            deleteButton.classList.remove(CSS.AON_NONE);
        }
    }
}
if(!window.customElements.get(TAG.AON_UPLOAD)){
	window.customElements.define(TAG.AON_UPLOAD, AonUpload);
}