import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../environments/environments";
import { MESSENGER_DIRECTION } from "../modules/messenger/MessengerEnums";
import { setAttributes } from "../services/utilsComponents";
import { AonTextArea } from "./aon-textarea";
import { AonElement } from "./AonElement";

import * as LS from "../services/localStorageService";

export class AonChat extends AonElement {

  workflows;

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
    this.workflows = this.workflows || [];
    // [
    //   {
    //     action: {
    //       title: "Creada",
    //       color: "green",
    //       icon: MATERIAL_ICONS.COFFEE
    //     },
    //     user: "Ander",
    //     date: "hace 5 minutos"
    //   },
    //   {
    //     action: {
    //       title: "Rechazada",
    //       color: "red",
    //       icon: MATERIAL_ICONS.CAR_REPAIR
    //     },
    //     user: "Pepe",
    //     comment: "No se han cumplido los requisitos para aprobar esta tarea, por favor revisa la documentación y vuelve a intentarlo.",
    //     date: "hace 2 minutos"
    //   },
    //   {
    //     user: "Ander",
    //     comment: "Tu puta madre!",
    //     date: "hace 1 minuto"
    //   }
    // ];
  }

  build() {
    this.buildChat();
    this.buildChatWorkflow();
    this.buildTextArea();
  }

  buildChat = () => {
    let chat = this.createDiv()
    chat.id = 'chat';
    chat.classList.add("continueLined");
    chat.classList.add(CSS.FLEX_COLUMN);
    chat.classList.add(CSS.FLEX_ALIGN_CENTER);
    chat.classList.add(CSS.MATERIAL_SCROLL);
    chat.style.position = "relative";
    chat.style.width = "100%";
    chat.style.zIndex = "0";
    chat.style.minHeight = "311px";
    chat.style.padding = "15px";
    chat.style.overflow = "auto";
    chat.style.borderBottom = "1px solid #f0f0f0";
    chat.style.scrollBehavior = "smooth";
    this.appendChild(chat);
  }

  buildChatWorkflow = () => {
    if (this.workflows.length == 0) {
      let noMessage = this.createDiv();
      noMessage.innerHTML = 'No hay mensajes para mostrar';
      noMessage.style.fontSize = '1em';
      noMessage.style.color = CSS.variable(COLORS.GRAYSON);
      chat.appendChild(noMessage);
    } else {
      this.workflows.forEach(workflow => {
        this.buildWorkflow(workflow);
      });
    }
  }

  buildWorkflow(workflow) {
    this.buildAction(workflow);
    this.buildMessage(workflow);
  }

  buildAction = (workflow) => {
    if (workflow.action) {
      let chat = this.getElement("chat");
      let action = this.createDiv();
      action.classList.add(CSS.FLEX_ROW);
      action.classList.add(CSS.FLEX_JUSTIFY_START);
      action.classList.add(CSS.FLEX_ALIGN_CENTER);
      action.style.width = "100%";
      action.style.padding = "5px 2px";
      action.style.textAlign = "justify";
      action.style.flexWrap = "wrap";

      let iconDiv = this.createDiv();
      iconDiv.style.width = "20px";
      iconDiv.style.height = "20px";
      iconDiv.style.borderRadius = "100em";
      iconDiv.style.backgroundColor = CSS.variable(COLORS.AON_LIGHT_GRAY);
      action.appendChild(iconDiv);

      let icon = this.createElement(TAG.I);
      icon.className = CONSTANT.MATERIAL_ICONS;
      icon.style.fontSize = "1.4em";
      icon.style.color = workflow.action.color || CSS.variable(COLORS.GRAYSON);
      icon.innerHTML = workflow.action.icon || MATERIAL_ICONS.INFO;
      icon.title = workflow.action.title || "";
      iconDiv.appendChild(icon);

      let actionText = this.createDiv();
      actionText.style.color = CSS.variable(COLORS.GRAYSON);
      actionText.style.fontSize = "1.1em";
      actionText.style.fontWeight = "400";
      actionText.innerHTML = workflow.action.title + " por <b>" + workflow.user + "</b> el " + workflow.date;
      action.appendChild(actionText);

      chat.appendChild(action);
    }
  }

  buildMessage = (workflow) => {
    if (workflow.comment) {
      let chat = this.getElement("chat");
      let messageBox = this.createDiv();
      messageBox.classList.add(CSS.FLEX_COLUMN);
      messageBox.classList.add(CSS.IMG_MAX_WIDTH);
      messageBox.classList.add(CSS.AON_NOT_RICH);
      messageBox.style.margin = "5px";
      messageBox.style.padding = '15px';
      messageBox.style.background = this.isMe(workflow.user) 
        ? "#f0fff0" 
        : CSS.variable(COLORS.AON_WHITE);
      messageBox.style.boxShadow = '0px 2px 6px rgba(0,0,0,.1)';
      messageBox.style.borderRadius = '5px';
      messageBox.style.position = "relative";
      messageBox.style.width = '90%';    
      if(this.isMe(workflow.user)) messageBox.style.marginLeft = "auto";
      else messageBox.style.marginRight = "auto";
      
      let author = this.createDiv();
      author.classList.add(CSS.FLEX_ROW);
      author.classList.add(CSS.FLEX_JUSTIFY_BETWEEN);
      author.classList.add(CSS.FLEX_ALIGN_CENTER);
      author.style.textAlign = this.isMe(workflow.user) ? "right" : "left";
      author.style.flexDirection = this.isMe(workflow.user) ? "row-reverse" : "row";
      messageBox.appendChild(author);

      let spanAuthor = this.createElement(TAG.SPAN);
      spanAuthor.style.fontSize = "12px";
      spanAuthor.style.fontWeight = "600";
      spanAuthor.style.color = CSS.variable(COLORS.GRAYSON);
      spanAuthor.style.textOverflow = "ellipsis";
      spanAuthor.style.overflow = "hidden";
      spanAuthor.style.whiteSpace = "nowrap";
      spanAuthor.style.zIndex = 1;
      spanAuthor.innerText = workflow.user;
      spanAuthor.title = workflow.user;
      author.appendChild(spanAuthor);

      let spanDate = this.createElement(TAG.SPAN);
      spanDate.classList.add(CSS.FIRST_LETTER_UPPER);
      spanDate.style.fontSize = "11px";
      spanDate.style.color = CSS.variable(COLORS.GRAYSON);
      spanDate.innerText = workflow.date;
      author.appendChild(spanDate);

      let comment = this.createDiv();
      comment.classList.add(CSS.MESSAGE_CONTENT);
      comment.style.fontSize = "1em";
      comment.style.textAlign = this.isMe(workflow.user) ? "right" : "left";
      comment.style.fontWeight = "400";
      comment.style.color = CSS.variable(COLORS.GRAYSON);
      comment.style.paddingTop = "5px";
      comment.style.wordWrap = "break-word";
      comment.innerHTML = workflow.comment;
      messageBox.appendChild(comment);

      chat.appendChild(messageBox);
    }
  }

  buildTextArea = () => {
    let div = this.createDiv();
    div.style.width = "100%";
    div.style.display = "flex";
    div.style.flexDirection = "column";
    div.style.borderRadius = "5px";
    div.style.border = "1px solid var(--aonBlue)";
    this.appendChild(div);

    let divComment = this.createDiv();
    divComment.style.display = "flex";
    divComment.style.minHeight = "57px";
    divComment.classList.add(CSS.RESIZE_VERTICAL);
    divComment.title = MSG.COMMENT;
    div.appendChild(divComment);

    let divMain = this.createDiv();
    divMain.style.width = "100%";
    divMain.style.display = "flex";
    divMain.style.flexDirection = "row-reverse";
    divMain.style.overflow = "hidden";
    divMain.classList.add(CSS.FOCUS_COLOR_MINUS);
    divComment.appendChild(divMain);

    let iconSend = this.iconComment(MATERIAL_ICONS.SEND);
    iconSend.id = 'sendIcon';
    iconSend.title = `Ctrl+Enter (${MSG.SEND})`;
    iconSend.addEventListener("click", () => {
      let ta = document.getElementById('chatTextArea');
      let comment = {
        user: LS.getDomainLogin(),
        comment: ta.value,
        date: new Date().toLocaleString()
      };
      ta.value = "";
      this.buildWorkflow(comment);
      this.dispatchEvent(new CustomEvent(EVENT.COMMENT, { detail: comment, bubbles: true }));
    });
    divComment.appendChild(iconSend);

    let textArea = this.createAonTextArea(MSG.COMMENT);
    textArea.style.position = "relative";
    textArea.style.margin = "5px 0 5px 5px";
    textArea.style.color = "#4b4b4b";
    textArea.style.border = "none";
    textArea.style.outline = "none";
    textArea.style.width = "82%";
    textArea.style.resize = "none";
    textArea.style.fontSize = "15px";
    textArea.style.fontWeight = "400";
    textArea.style.maxHeight = "200px";
    textArea.style.boxShadow = "none";
    textArea.style.height = "auto !important";
    textArea.style.overflow = "hidden";
    textArea.style.flex = "1";
    textArea.id = 'chatTextArea';

    textArea.addEventListener(EVENT.KEYDOWN, (event) => {
      if (event.ctrlKey && event.key === "Enter") {
        event.preventDefault();
        let comment = {
          user: LS.getDomainLogin(),
          comment: textArea.value,
          date: new Date().toLocaleString()
        };
        textArea.value = "";
        this.buildWorkflow(comment);
        this.dispatchEvent(new CustomEvent(EVENT.COMMENT, { detail: comment, bubbles: true }));
      }
    });

    divMain.appendChild(textArea);
    textArea.height = "45px";
    textArea.removeToolbar();
    textArea.draggableEnable();
    textArea.removeBackground();
  }

  iconComment = (icon_name) => {
    const a = this.createElement(TAG.A);
    a.style.boxShadow = "none";
    a.style.margin = "5px";
    a.style.marginTop = "auto";
    a.style.marginBottom = "auto";
    a.style.visibility = "visible";
    a.style.float = "right";
    a.style.background = "transparent";
    a.style.cursor = "pointer";

    const icon = this.createElement("i");
    icon.style.fontSize = "1.8em";
    icon.style.lineHeight = "44px";
    icon.style.color = CSS.variable(COLORS.AON_BLUE);
    icon.className = CONSTANT.MATERIAL_ICONS;
    icon.textContent = icon_name;
    a.appendChild(icon);

    return a;
  }

  createAonTextArea = (placeholder) => setAttributes(new AonTextArea(), {
    name: 'COMMENTTASK',
    title: placeholder || MSG.COMMENT + "..."
  });

  isMe(user) {
    return user == LS.getDomainLogin();
  }

  onComment = (callback) => this.addEventListener(EVENT.COMMENT, callback);
}
if (!window.customElements.get(TAG.AON_CHAT)) {
  window.customElements.define(TAG.AON_CHAT, AonChat);
}
