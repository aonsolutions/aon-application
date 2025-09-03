import { CONSTANT, TAG } from '../environments/environments.js';
import { newComponent, setStyles } from '../services/utilsComponents.js';
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
    if(!this.isNewStyle()){
      this.setStyleComponent();
    }
    this.paintView();
  }

  setStyleComponent(){
    setStyles(this, {
      position: "absolute",
      bottom: "10%",
      minWidth: "250px",
      maxWidth: "300px",
      marginLeft: "auto",
      marginRight: "auto",
      left: 0,
      right: 0,
      textAlign: "center",
      zIndex: 99, 
      height: 0,
      overflow: "hidden",
      opacity: 0,
      transition: "opacity 1s ease-out"
    });
  }

  paintView(){
    if(!this.isNewStyle()){
      newComponent({
        type:TAG.DIV,
        id: this.DIV,
        styles:{
          backgroundColor: "#333",
          color:" #fff",
          borderRadius: ".2rem",
          padding: "16px",
          fontSize: "17px",
          wordWrap: "break-word"
        }
      }).appendTo(this);
    } else {
      newComponent({
        type:TAG.DIV,
        id: this.DIV
      }).appendTo(this);
    }
  }

  start(options) {
    let toast = this.getElement(this.DIV);
    let { message, delay, type} = options;
    let color = '#333';
    if (!delay)
      delay = 3000;
    toast.innerHTML = message;
    if(!this.isNewStyle()){
      if (type === CONSTANT.ERROR) 
        color = '#f44336';
      else if (type === CONSTANT.SUCCESS)
        color = '#4CAF6E';
      else if (type === CONSTANT.PRIMARY)
        color = '#2196f3';
      toast.style.background = color;
      // mostramos
      this.displayToast();
      // ocultamos
      setTimeout(() => this.displayToast(false), delay);
    } else {
      // Componente padre
      this.className = "view";
      // agregamos clase para el tipo de mensaje
      if (type === CONSTANT.ERROR) 
        toast.className = "error";
      else if (type === CONSTANT.SUCCESS)
        toast.className = "success";
      else if (type === CONSTANT.PRIMARY)
        toast.className = "primary";
      // Ocultamos
      setTimeout(() => {
        this.className  = "";
        toast.className = "hidden"
      }, delay);
    }
  }

  displayToast(boolean = true){
    if(boolean){
      setStyles(this, {
        opacity: 1,
        height: "auto"
      });
    } else {
      setStyles(this, {
        transition: "opacity 1s ease-out",
        opacity: 0,
        height: 0,
        overflow: "hidden"
      });
    }
  }
}

if(!window.customElements.get(TAG.AON_TOAST)){
  window.customElements.define(TAG.AON_TOAST, AonToast);
}
