import { AonToolbar } from "../../components/aon-toolbar.js";
import { AonElement } from "../../components/AonElement.js";
import { COLORS, CONSTANT, CSS, MATERIAL_ICONS } from "../../environments/environments.js";
import { ToolbarType } from "../../models/enums.js";
import { newComponent, setClasses, setStyles, waitEl } from "../../services/utils.js";
import { SigninSidenav } from "../signin/signinEnums.js";
import { createMainView, createMobileMainView } from "./createComponents.js";
import { buildChat, buildMobileChat } from "./shared/messenger-chat.js";
import { buildDesktopWritter } from "./shared/messenger-writter.js";
import { MESSENGER_COMPONENTS, MESSENGER_VIEWS } from "./MessengerEnums.js";
import * as ACTIONS from "../actions.js";
import { createOutlinedMaterialIcon } from "./shared/creationUtils.js";

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
    /*Base font-size*/
    this.style.fontSize = "12px";

    /**
     * Switching between mobile and desktop
     */
    if (this.isMobile()){ 
        this.paintMobile(data);
    }
    else { 
      this.paintDesktop(data);
    }
    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
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
    const toolbar = new AonToolbar();

    toolbar.id = "id";
		toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = "#" + data.id;

    waitEl("#id").then(bar => {
      bar.addButton2(ACTIONS.BACK,() => {
        mainView.element.style.opacity = "0";
        mainView.element.style.transition = ".25s";
        
        setTimeout(() => {
            const button = document.getElementById("aonMessengerSidenavAbiertas");
            button.click();
        }, 250);
      })
      const titleSpan = bar.querySelector(".aonSecondaryToolbarTitle")

      setClasses(titleSpan,[CSS.FLEX_ROW,CSS.FLEX_ALIGN_CENTER]);

      const status = createOutlinedMaterialIcon({
        name: "info",
        color: CSS.variable(COLORS.ONLINE_GREEN),
        size: "20px"
      });
      status.element.style.marginLeft = "10px"
      status.appendTo(titleSpan);
    })

    const writter = newComponent({
      classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
      styles: {
        width: "50%",
        height: '100%',
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

    this.appendChild(toolbar);
    mainView.appendTo(this);

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }

  paintMobile(data) {
    this.applicationEl.addFloatOption(SigninSidenav.ADD, () => {
      
      /**
       * Hidding float button
       */
       let button = document.querySelector("#aonMessengeraddButtonIconButton")

       setStyles(button , {
           transition : "0.25s",
           opacity : 0
       });

       setTimeout(() => {
         button.style.display = "none";
       }, 100);

      /**
       * show writter
       */
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
