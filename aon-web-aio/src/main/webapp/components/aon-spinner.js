import {AonElement} from './AonElement.js';
import { TAG } from '../environments/environments.js';

export class AonSpinner extends AonElement {
  constructor() {
    super();
    this.spinner = null;
  }

  connectedCallback() {
    console.log("connectedCallback ejecutado");
    // Estilo CSS en línea para el spinner
    const style = super.createElement('style');
    style.textContent = `
      .spinner {
        position        : fixed;
        top             : 0;
        left            : 0;
        width           : 100%;
        height          : 100%;
        background-color: rgba(200, 200, 200, 0.6);
        display         : flex;
        align-items     : center;
        justify-content : center;
        z-index         : 9999;
        display         : flex;
    
        .material-symbols-outlined{
          font-size: 6rem;
          animation: rotate 2s linear infinite;
        }
      }

      @keyframes rotate {
        0% {
          transform: rotate(0deg);
        }
        100% {
          transform: rotate(360deg);
        }}
      }
    `;
    // Crear el div para el spinner
    this.spinner = super.createElement(TAG.DIV);
    this.spinner.classList.add('spinner');
    // Agregar el icono
    const icon = super.createElement(TAG.SPAN);
    icon.classList.add('material-symbols-outlined');
    icon.textContent = 'refresh';
    this.spinner.appendChild(icon);

    // Añadir el estilo
    this.appendChild(style);
    // Añadir el spinner
    this.appendChild(this.spinner);
    console.log("connectedCallback terminado");
  }

  // Método para mostrar el spinner
  show() {
    console.log('+++++++++++++++++++++++++++++++++++++++++++++++++')
      console.log("Mostrar spinner, spinner actual:", this.spinner);
      
    if (this.spinner) {
      
      
      this.spinner.style.display = 'flex';
    }
    console.log('+++++++++++++++++++++++++++++++++++++++++++++++++')
  }

  // Método para ocultar el spinner
  hide() {
     console.log("Ocultar spinner, spinner actual:", this.spinner);
    if (this.spinner) {
      this.spinner.style.display = 'none';
    }
  }
}

// Registrar el componente como un Custom Element
if (!window.customElements.get(TAG.AON_SPINNER)) {
  window.customElements.define(TAG.AON_SPINNER, AonSpinner);
}
