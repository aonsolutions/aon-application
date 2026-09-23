import {AonElement} from './AonElement.js';
import { PDF_OR_IMAGE_ACCEPT } from '../services/imageFileService.js';
import { CONSTANT, CSS, TAG, EVENT } from '../environments/environments.js';

export class AonDashboardUploadButton extends AonElement {

  DIV;
  INPUT;
  ICON;
  TEXT;

  type;

  accept;

  get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

  getId() {
    return this.id;
  }

  setId(id) {
    this.id = id;
  }

  getIcon() {
    return this.icon;
  }

  setIcon(icon) {
    this.icon = icon;
  }

  getTitle() {
    return this.title;
  }

  setTitle(title) {
    this.title = title;
  }
  
  getColor() {
    return this.color;
  }

  setColor(color) {
    this.color = color;
  }

  isDisabled() {
    return this.disabled;
  }

  setDisabled(disabled) {
    this.disabled = disabled;
    let button = this.getElement(this.BUTTON);
    if(button) button.disabled = disabled;
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
      this.accept = this.accept || PDF_OR_IMAGE_ACCEPT;

      this.type = 'Documental';
  }

  build() {
      let div = this.createElement(TAG.DIV);
      div.id = this.DIV;
      div.className = CSS.AON_DASHBOARD_BUTTON;
      div.style.height = 'auto';
      this.appendChild(div);

      let input = this.createElement(TAG.INPUT);
      input.id = this.INPUT;
      input.type = CONSTANT.FILE;
      input.accept = this.accept;
      input.className = CSS.AON_NONE;
      input.addEventListener(EVENT.CHANGE, ({target}) => {
          let desktop = this.getElement('aonDesktop');
          desktop.uploadDocumentsDesktop(input, target.files);
      });
      div.appendChild(input);

      if(this.color) {
        div.style.backgroundColor = this.color;
      }

      if(this.icon) {
        let icon = this.createElement(TAG.I);
        icon.id = this.ICON;
        icon.className = CSS.MATERIAL_ICONS;
        icon.innerHTML = this.getIcon();
        div.appendChild(icon);
      }
  
      if(this.message) {
        let text = this.createElement(TAG.SPAN);
        text.id = this.TEXT;
        text.innerHTML = this.message;
        div.appendChild(text);
      }
      
      div.addEventListener(EVENT.CLICK, (ev) => {
        if(this.isClasic()){
          ev.preventDefault();
          ev.stopPropagation();
        }
        input.click();  
      });
  }

}

if(!window.customElements.get(TAG.AON_DASHBOARD_UPLOAD_BUTTON)){
  window.customElements.define(TAG.AON_DASHBOARD_UPLOAD_BUTTON, AonDashboardUploadButton);
}
