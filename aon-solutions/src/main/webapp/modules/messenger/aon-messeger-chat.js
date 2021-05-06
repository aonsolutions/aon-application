import { AonElement } from "../../components/AonElement.js";
import { CONSTANT, CSS } from "../../environments/environments.js";
import { newComponent, setStyles } from "../../services/utils.js";
import { SigninSidenav } from "../signin/signinEnums.js";
// import { createMainView, createMobileMainView } from "./createComponents.js";
// import { buildChat, buildMobileChat } from "./messenger-chat.js";
// import { buildDesktopWritter } from "./messenger-writter.js";
import { MESSENGER_COMPONENTS, MESSENGER_VIEWS } from "./MessengerEnums.js";

export class AonMessengerChat extends AonElement {
  
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }
  
  constructor() {
    super();
  }

  get data() {
    const content = this.getAttribute(CONSTANT.DATA) || "{}"; 
    return JSON.parse(content);
  }

  set data(data){
    this.setAttribute(CONSTANT.DATA,JSON.stringify(data));
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_CHAT;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
  }

  async build() {
    const data = await this.getData();
    this.paintView(data);
  }

  paintView(data) {

    console.info(data);
    if (this.isMobile()) this.paintMobile(data);
    else this.paintDesktop(data);

    const lined = document.querySelector(".continueLined");
    if (lined)
      lined.style.setProperty("--height", lined.scrollHeight + "px");

  }

  async getData() {

    if(this.data && this.data.id){
      const data = {
        type: 0,
        id: "1001237",
        author: "akrck02@gmail.com",
        title: "Creación y borrado de IT en condiciones extracurriculares de acuerdo al convenio vigente y el estatuto de los trabajadores y tal",
        for: "Laboral",
        content: [
          {
            type: "message",
            sender: "Emisor",
            message: "Hola, las <i>bajas de IT deben </i> ser notificadas en <b>Laboral</b>. Ejemplo : <br><br><li>Ejemplo 1.</li> <li>Ejemplo 2.</li> ",
            date:  new Date(2020,23,4),
            attach: [
              {
                name: "File.docx"
              }
            ]
          },
          {
            type: "message",
            sender: "Receptor",
            message: "Oh! Gracias. ¿Como se eliminan?",
            date:  new Date(2020,23,4),
          },
          {
            type: "action",
            action: "move",
            message: "Esta solicitud se movió a Laboral",
          },
          {
            type: "message",
            sender: "Emisor",
            message: "Con el botón de borrar.",
            date : new Date(2020,23,4),
            attach: [
              {
                name: "File1.docx"
              },
              {
                name: "File2.docx"
              },
              {
                name: "File3.docx"
              }
            ]
          },
          {
            type: "action",
            action: "close",
            message: "La solicitud se cerró.",
          },
        ]
      }
      this.data = data;
      return data;

    }

    const clean = {
      type: 0,
      id: "00000",
      author: "",
      title: undefined,
      for: "Laboral",
      content: []
    }
    this.data = clean;

    return clean;
  }

  paintDesktop(data) {
    const mainView = createMainView();
    const writter = newComponent({
      classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
      styles: {
        width: "50%",
        height: '80%',
        minWidth: "400px",
        maxWidth: "600px",
        paddingTop: '5vh',
        paddingRight: '20px',
        paddingLeft: '60px',
        top: 0
      }
    });

    const chat = newComponent({
      classes: [CSS.FLEX_ROW],
      styles: {
        width: "50%",
        height: '90%',
        minWidth: "400px",
        maxWidth: "600px",
        paddingTop: '5vh',
        paddingRight: '40px',
        paddingLeft: '40px',
      }
    });

    buildDesktopWritter(writter, data);
    buildChat(chat, data);

    writter.appendTo(mainView.element);
    chat.appendTo(mainView.element);
    mainView.appendTo(this);

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }

  paintMobile(data) {
    this.applicationEl.addFloatOption(SigninSidenav.ADD, () => {
      this.applicationEl.removeFloatOption();
      setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{display : "flex",});
      setTimeout(() => {
        setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{
          zIndex : 9,
          opacity : 1,
          left : 0,
        });
  
      }, 100);
    } );
    const mainView = createMobileMainView();
    const chat = newComponent({
      classes: [CSS.FLEX_ROW],
      styles: {
        width: "100%",
        height: '100%',
        maxWidth: "600px",
        paddingRight: '20px',
        paddingLeft: '20px',
      }
    });

    buildMobileChat(chat, data);
    chat.appendTo(mainView.element);
    mainView.appendTo(this);

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
