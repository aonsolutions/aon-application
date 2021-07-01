import { AonToolbar } from "../../components/aon-toolbar.js";
import { AonElement } from "../../components/AonElement.js";
import { COLORS, CONSTANT, CSS, EVENT } from "../../environments/environments.js";
import { ToolbarType } from "../../models/enums.js";
import { newComponent, setClasses, setStyles, setValueName, waitEl } from "../../services/utils.js";
import { createMainView, createMobileMainView, inputId } from "./createComponents.js";
import { buildChat, buildMobileChat } from "./shared/messenger-chat.js";
import { buildDesktopWritter, sendMessage } from "./shared/messenger-writter.js";
import { MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS } from "./MessengerEnums.js";
import { createOutlinedMaterialIcon } from "./shared/creationUtils.js";
import * as ACTIONS from "../actions.js";
import { saveTask } from "../../services/taskService.js";

export class AonMessengerChat extends AonElement {
  _data;
  static get observedAttributes() {
    return [CONSTANT.DATA];
  }
  
  constructor() {
    super();
  }

  get data() {
    return JSON.parse(this.getAttribute(CONSTANT.DATA) || "{}");
  }

  set data(data){
    this.setAttribute(CONSTANT.DATA, JSON.stringify(data));
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  disconnectedCallback() {
    this.deleteToolbar();
  }

  initialize() {
    this.id = this.id || MESSENGER_VIEWS.AON_MESSENGER_CHAT;
    this.applicationEl = this.getApplication();
    this.applicationParentEl = this.getApplicationParent();
    this.deleteToolbar();
    this._data =  {
      id: undefined,
      number:undefined,
      title: undefined,
      type: 0,
      author: "",
      for: "Laboral",
      content: []
    }
  }

  deleteToolbar(){
    try {
      this.getApplication().removeFloatOption();
      this.getApplication().removeToolbarOptions();
    } catch (error) {}
  }
  
  build() {
    if(this.data && this.data.id) this._data={...this._data,...this.data};
    this.paintView();
  }

  paintView() {
    /*Base font-size*/
    this.style.fontSize = "12px";
    /**
     * Switching between mobile and desktop
     */
    if (this.isMobile()){ 
      this.paintMobile();
    } else { 
      this.paintDesktop();
    }
    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = this.selector(".continueLined");
    if (lined)
      lined.style.setProperty("--height", lined.scrollHeight + "px");
  
    this.appendChild(inputId());
    if(this._data.id) this.setValues();
  }

  paintDesktop() {
    const data = this._data;
    const mainView = createMainView();
    const toolbar = new AonToolbar();
    toolbar.id = "id";
		toolbar.type = ToolbarType.SECONDARY;
    toolbar.title = "#" + (data.id  || "00000");
    waitEl(`#${toolbar.id}`).then(bar => {
      bar.addButton2(ACTIONS.BACK,() => {
        mainView.element.style.opacity    = "0";
        mainView.element.style.transition = ".25s";null
        setTimeout(() => {
            const button = this.getElement("aonMessengerSidenavAbiertas");
            button.click();
        }, 250);
      });

      const titleSpan = bar.querySelector(`.${CSS.AON_SECONDARY_TOOLBAR_TITLE}`);

      setClasses(titleSpan,[CSS.FLEX_ROW,CSS.FLEX_ALIGN_CENTER]);

      const status = createOutlinedMaterialIcon({
        color: CSS.variable(COLORS.ONLINE_GREEN),
        name: "info",
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

    buildDesktopWritter(writter, data, this);
    buildChat(chat, data);

    writter.appendTo(mainView.element);
    chat.appendTo(mainView.element);

    this.appendChild(toolbar);
    mainView.appendTo(this);

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);

    this.getElement(MESSENGER_IDS.BUTTON_SUBMIT_COMMENT).addEventListener(EVENT.CLICK,()=>{
      this.save();
    });
  }

  paintMobile() {
    const data = this._data;
    if(data && data.id){
      let span = this.applicationEl.addFloatOption({
        name: "Addcomment",
        icon: "add_comment",
        id: "Addcomment",
      }, () => {
        /**
         * Hidding float button
         */
       setStyles(span.querySelector("button") , {
          transition : "0.25s",
          opacity : 0
        });
        /**
         * show writter
         */
        let componentWrite = setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{display : "flex"});
        setTimeout(() => {
          setStyles(componentWrite,{
            zIndex:  9,
            opacity: 1,
            left: 0,
          });
        }, 100);
      });
    }

    const mainView = createMobileMainView();
    const chat = newComponent({
      classes: [CSS.FLEX_ROW],
      styles: {
        width: "100%",
        height: "100%",
        maxWidth: "600px",
        paddingRight: "20px",
        paddingLeft: "20px",
      }
    });

    buildMobileChat(chat, data, this);
    chat.appendTo(mainView.element);
    mainView.appendTo(this);

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }

  setValues(){
    console.log(this._data);
    const {id, workgroup, task_holder} = this._data;
    if(id) setValueName(MESSENGER_IDS.TASK_ID, id);
    if(workgroup && workgroup.id) setValueName(MESSENGER_IDS.WORKGROUP, workgroup.id);
    if(task_holder && task_holder.id) setValueName(MESSENGER_IDS.TASKHOLDER, task_holder.id);
  }

  getData() {
    let data = undefined;
    if(this.data && this.data.id){
      data = {
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
    } 
    return data;
  }
  
  formSerialize(){
    let aonTextArea = this.getElement(MESSENGER_IDS.COMMENT_TASK);
    const comment = aonTextArea.value
    if(comment) sendMessage(aonTextArea); //remove comment and processed
    const sender = this.applicationParentEl.SENDER;
    return {
      id: this.selector("#"+MESSENGER_IDS.TASK_ID).value,
      title:this.selector(`#${MESSENGER_IDS.TITLE_TASK}`).innerText,
      description: this.selector(`#${MESSENGER_IDS.DESCRIPTION_TASK}`).innerText,
      domain: sender.domain.id,
      workgroup: {
        id:this.selector("#"+MESSENGER_IDS.WORKGROUP).value
      },
      task_holder:{
        id:this.selector("#"+MESSENGER_IDS.TASKHOLDER).value
      },
      sender,
      comment,
    }
  }

  async save(){
    this.applicationEl.startLoading();
    try {
      const form = this.formSerialize();
      const resp = await saveTask(form);
      setValueName(MESSENGER_IDS.TASK_ID, resp.id);
      console.log(resp);
    } catch (error) {
      this.showError(error);
    }
    this.applicationEl.stopLoading();
  }

  selector(selector){
    return document.querySelector(selector) || {};
  }
}

window.customElements.define("aon-messenger-chat", AonMessengerChat);
