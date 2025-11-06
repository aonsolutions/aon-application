import { CONSTANT, TAG } from '../environments/environments.js';
import { newComponent } from '../services/utilsComponents.js';
import { AonElement } from './AonElement.js';

export class AonToast extends AonElement {
  DIV;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  constructor() {
    super();
    this.id = this.id || 'aonToast';
    this.DIV = this.id + 'Div';
  }

  connectedCallback() {
    this.paintView();
  }

  paintView(){
    newComponent({
      type: TAG.DIV,
      id  : this.DIV
    }).appendTo(this);
  }

  start(options) {
    let { type, message, delay } = options;
    let toast = this.getElement(this.DIV);
    if (!delay)
      delay = 3000;
    toast.innerHTML = message;
    // Componente padre
    this.classList.add("view");
    toast.classList.remove("hidden");
    // agregamos clase para el tipo de mensaje
    if (type === CONSTANT.ERROR)
      toast.classList.add("error");
    else if (type === CONSTANT.SUCCESS)
      toast.classList.add("success");
    else if (type === CONSTANT.PRIMARY)
      toast.classList.add("primary");
    // Ocultamos
    setTimeout(() => {
      this.classList.remove("view");
      toast.classList.remove("error","success","primary");
      toast.classList.add("hidden");
    }, delay);
  }
}

if(!window.customElements.get(TAG.AON_TOAST)){
  window.customElements.define(TAG.AON_TOAST, AonToast);
}
