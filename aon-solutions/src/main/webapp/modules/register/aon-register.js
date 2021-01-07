import { AonElement } from "../../components/AonElement.js";
import { serializeForm } from "../../services/utils.js";
import { registerUser } from '../../services/service.js';
import "../../components/aon-card.js";
import "../../components/aon-input.js";
import "../../components/aon-checkbox.js";
import '../../components/aon-toast.js';

export class AonRegister extends AonElement {
  TOAST;
  ID;
  static get observedAttributes() {
    return [];
  }

  constructor() {
    super();
    this.ID = "aonComponentRegister";
  }

  attributeChangedCallback(name, oldValue, newValue) {}

  connectedCallback() {
    this.paintView();
    this.build();
    this.eventListener();
  }

  paintView() {
    const initHtml = `
            <style>
              .aonCard {
                position: relative;
                display: -ms-flexbox;
                display: flex;
                -ms-flex-direction: column;
                flex-direction: column;
                min-width: 0;
                word-wrap: break-word;
                background-color: #fff;
                background-clip: border-box;
                border: 1px solid rgba(0,0,0,.125);
                border-radius: .25rem;
                margin: 2% 5%;
              }
              .tedi-link {
                text-decoration: none;
                color: #0040ff;
              }
              .logo {
                margin-top: 25px;
              }
              #aonComponentRegisterUsuarioCard > .aonCard{
                margin-top: 1px;
              }
            </style>
        `;

    const form = `
        <aon-toast id="${this.ID}Toast"></aon-toast>
        <div id="${this.ID}LogoDiv">
            <img id="${this.ID}LogoImg" class="logo"/>
        </div>
        <form id="${this.ID}Form" action="#" onsubmit="return false;">
            <div id="${this.ID}Div">
                <div class="aonCol-sm-12">
                    <aon-card id="${this.ID}EmpresaCard" title="Datos de la empresa"></aon-card>
                </div>
                <div class="aonCol-sm-12">
                    <aon-card id="${this.ID}UsuarioCard" title="Datos del usuario"></aon-card>
                </div>
            </div>
        </form>`;

    this.innerHTML = initHtml + form;

    let aonEmpresaCard = this.getElement(`${this.ID}EmpresaCard`);
    aonEmpresaCard.setContentHTML(`
            <div class="aonCol-sm-6 aonCol-md-6">
                <aon-input name="cif" id="cif" description="CIF"></aon-input>
            </div>
            <div class="aonCol-sm-6 aonCol-md-6">
                <aon-input name="razon_social" id="razon_social" description="Razón Social"></aon-input>
            </div>
            <div class="aonCol-sm-6 aonCol-md-6">
                <aon-input name="direccion" id="direccion" description="Dirección"></aon-input>
            </div>
            <div class="aonCol-sm-6 aonCol-md-6">
                <aon-input name="iban" id="iban" description="Iban"></aon-input>
            </div>
        `);
    
    let btnPolitics = `He leído el <a class=tedi-link href=https://translogia.es/aviso-legal/ target=_blank>Aviso Legal</a> y acepto la <a  class=tedi-link href=https://translogia.es/politica-de-privacidad/ target=_blank>Política de Privacidad.</a>`;

    let aonUsuarioCard = this.getElement(`${this.ID}UsuarioCard`);
    aonUsuarioCard.setContentHTML(`
        <div class="aonCol-sm-6 aonCol-md-6">
            <aon-input name="nombre" id="nombre" description="Nombre"></aon-input>
        </div>
        <div class="aonCol-sm-6 aonCol-md-6">
            <aon-input name="apellidos" id="apellidos" description="Apellidos"></aon-input>
        </div>
        <div class="aonCol-sm-6 aonCol-md-6">
            <aon-input name="ipf" id="ipf" description="NIF/NIE"></aon-input>
        </div>
        <div class="aonCol-sm-6 aonCol-md-6">
            <aon-input type="tel" name="phone" id="phone" description="Teléfono Móvil"></aon-input>
        </div>
        <div class="aonCol-sm-6 aonCol-md-6">
            <aon-input type="email" name="email" id="email" description="Correo"></aon-input>
        </div>
        <div class="aonCol-sm-6 aonCol-md-6">
            <aon-input type="password" name="password" id="password" description="Contraseña"></aon-input>
        </div>

        <aon-checkbox name="politics" id="politics" description="${btnPolitics}"></aon-checkbox> 
    
        <div style="float:right">
            <button class="aonButton" type="button" id="${this.ID}Submit">Guardar</button>
        </div>
    `);

    let icon = document.createElement('i');
    icon.setAttribute('id', this.ID + 'Icon');
    icon.className =  'material-icons';
    icon.setAttribute("style", "position: absolute; top: 30%; right: 2%; cursor: -webkit-grab; cursor: grab;");
    icon.innerHTML = 'visibility'; //'visibility_off'
    this.getElement('passwordLabel').appendChild(icon);
    icon.addEventListener('click',()=> {
        let ic = "visibility";
        let type = "password";
        if("visibility_off" !== icon.innerHTML){
            ic =  "visibility_off";
            type = "text";
        }
        icon.innerHTML = ic;
        this.getElement('passwordInput').type = type;
    })
  }

  build() {
    this.buildLogo();
  }

  buildLogo() {
    let logo = document.getElementById(`${this.ID}LogoImg`);
    if (window.location.href.includes("ayudat")) {
      logo.src = "assets/ayudat-logo4.png";
    } else if (
      window.location.href.includes("translogia") ||
      window.location.href.includes("tedi")
    ) {
      logo.src = "../assets/ayudat-logo4.png";
    } else logo.src = "assets/aon-logo.png";
  }

  eventListener() {
    let aonSubmit = this.getElement(`${this.ID}Submit`);
    if (aonSubmit) aonSubmit.addEventListener("click", (ev) => this.save(ev));

    let aonRegisterLogoDiv = this.getElement(`${this.ID}LogoDiv`);
    if (aonRegisterLogoDiv) aonRegisterLogoDiv.addEventListener("click", () => this.back());
  }

  getDatos() {
    const form = this.getElement(`${this.ID}Form`);
    return serializeForm(form);
  }

  async save({target}) {
    let toast = this.getElement(`${this.ID}Toast`);
    console.log(this.getDatos());
    target.setAttribute('disabled', 'disabled');
    try {
        await registerUser(this.getDatos());
        toast.start({
          message: "Usuario registrado!",
          type: "success",
          delay: 3000,
        });
        this.back();
    } catch (error) {
        toast.start({ message: error, type: "error" });
    }
    target.removeAttribute('disabled');
  }

  back() {
    this.showElement('aonLogin');
    this.hideElement('aonRegister');
  }
}

window.customElements.define("aon-register", AonRegister);
