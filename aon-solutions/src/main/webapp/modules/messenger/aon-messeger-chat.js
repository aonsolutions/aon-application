import { AonElement } from "../../components/AonElement.js";
import { newComponent } from "../../services/utils.js";
import { MESSENGER_VIEWS } from "./MessengerEnums.js";

export class AonMessengerChat extends AonElement {
  constructor() {
    super();
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
    this.paintView();
    await this.getChat();
  }

  async getData() {}

  paintView() {
    let innerHTML = ``;
    let aonTableHtml = "";
    if (this.isMobile()) this.getApplication().development();
    else this.paintDesktop();
  }

  async getChat(idDivAppend = undefined) {}

  paintDesktop() {
    const mainView = newComponent({
      type: "div",
      styles: {
        transition: ".5s",
        display: "flex",
        "flex-direction": "column",
        opacity: 0,
        "margin-top": "-5vh",
        padding: "40px",
        "justify-content": "center",
      },
    });
    this.buildDesktopWritter(mainView);
    this.buildDesktopChat(mainView);
    this.appendChild(mainView.element);

    setTimeout(() => {
      mainView.element.style.opacity = 1;
      mainView.element.style.marginTop = 0;
    }, 100);
  }

  buildDesktopWritter(parent) {
    const fontColor = "#787878";

    const title = newComponent({
      type: "aon-title",
      text: "Solicitud #1",
      styles: {
        "font-size": "2em",
        color: fontColor,
      },
    });

    const subtitleDiv = newComponent({
      type: "div",
      styles: {
        display: "flex",
        "flex-direction": "row",
        "padding-top": "10px",
        "padding-bottom": "10px",
        width: "100%",
        "max-width": "550px",
      },
    });

    const subtitle = newComponent({
      type: "input",
      attributes: {
        type: "text",
        placeholder: "Escriba su titulo aqui...",
      },
      styles: {
        "font-size": "1.5em",
        border: "none",
        width: "100%",
      },
    });

    const edit = newComponent({
      type: "i",
      text: "edit",
      classes: ["material-icons"],
      styles: {
        "font-size": "1.5em",
        color: fontColor,
        cursor: "pointer",
      },
    });

    const receiverDiv = newComponent({
      type: "div",
      classes: ["flexRow", "flexAlignCenter"],
      styles: {
        "margin-bottom": "5px",
      },
    });

    const receiverTitle = newComponent({
      type: "span",
      text: "Para: ",
      styles: {
        "font-size": "1.5em",
        "padding-right": "5px",
        color: fontColor,
      },
    });

    const receiverSelect = newComponent({
      type: "select",
      text: "<option>Laboral</option>",
      styles: {
        "box-shadow": "0px 0px 2px rgba(0,0,0,.5)",
        border: "none",
        color: "#A9A9A9",
        "border-radius": "3px",
        background: "#fff",
        padding: "7px",
        "margin-left": "5px",
        width: "80%",
        "max-width": "200px",
        "border-radius": "3px",
      },
    });

    const writter = newComponent({
      type: "aon-writter",
      classes: ["flexColumn"],
      styles: {
        "min-width": "500px",
        "max-width": "600px",
        width: "100%",
        height: "300px",
        "box-shadow": "0px 0px 2px rgba(0,0,0,.5)",
        "margin-top": "20px",
      },
    });

    const bar = this.buildWritterBar();

    const textarea = newComponent({
      type: "textarea",
      styles: {
        border: "none",
        padding: "10px",
        height: "100%",
        width: "100%",
        color: fontColor,
        "border-radius": "3px",
        resize: "no-resize",
      },
    });

    const sendBar = newComponent({
      type: "div",
      classes: ["flexRow"],
      styles: {
        "max-width": "600px",
      },
    });

    const upload = newComponent({
      type: "div",
      classes: ["flexRow", "flexJustifyEnd", "flexAlignCenter"],
      styles: {
        "padding-top": "15px",
        cursor: "pointer",
      },
    });

    const uploadIcon = newComponent({
      type: "i",
      text: "file_upload",
      classes: ["material-icons"],
      styles: {
        "font-size": "2.5em",
        color: "var(--materialBlue)",
        cursor: "pointer",
      },
    });

    const uploadText = newComponent({
      type: "span",
      text: "Agregar un archivo",
      classes: ["flexRow", "flexJustifyEnd", "flexAlignCenter"],
      styles: {
        "font-size": "1.2em",
        "font-weight": "300",
        color: "var(--materialBlue)",
        "padding-left": "10px",
        height: "100%",
      },
    });

    const sendButtonWrapper = newComponent({
      type: "div",
      classes: ["flexRow", "flexJustifyEnd", "flexAlignCenter"],
      styles: {
        "padding-top": "15px",
        width: "100%",
      },
    });

    const sendButton = newComponent({
      type: "button",
      text: "Enviar",
      classes: [
        "materialButton",
        "flexRow",
        "flexJusfityCenter",
        "flexAlignCenter",
      ],
      styles: {
        transition: ".25s",
        padding: "13px",
        "min-width": "105px",
        "min-height": "35px",
        "font-size": "1.2em",
        background: "var(--materialBlue)",
        "box-shadow": "0px 2px 4px rgba(0,0,0,.15)",
        border: "none",
        margin: "10px",
        "border-radius": "3px",
        color: "#fff",
      },
    });

    const sendIcon = newComponent({
      type: "i",
      text: "send",
      classes: ["material-icons"],
      styles: {
        "font-size": "1.2em",
        color: "white",
        "justify-self": "flex-end",
        cursor: "pointer",
        "padding-left": "15%",
      },
    });

    subtitle.appendTo(subtitleDiv.element);
    edit.appendTo(subtitleDiv.element);

    receiverTitle.appendTo(receiverDiv.element);
    receiverSelect.appendTo(receiverDiv.element);

    bar.appendTo(writter.element);
    textarea.appendTo(writter.element);

    uploadIcon.appendTo(upload.element);
    uploadText.appendTo(upload.element);
    upload.appendTo(sendBar.element);

    sendIcon.appendTo(sendButton.element);
    sendButton.appendTo(sendButtonWrapper.element);
    sendButtonWrapper.appendTo(sendBar.element);

    //main elements append
    title.appendTo(parent.element);
    subtitleDiv.appendTo(parent.element);
    receiverDiv.appendTo(parent.element);
    writter.appendTo(parent.element);
    sendBar.appendTo(parent.element);
  }

  buildDesktopChat(parent) {
    //not yet implemented
  }

  /**
   * Builds the writter bar
   * @returns Bar Object
   */
  buildWritterBar() {
    const bar = newComponent({
      type: "toolbar",
      classes: ['aonWritterBar','flexRow','flexAlignCenter','flexJustifyBetween'],
    });

    const textFormat = newComponent({
      classes: ['textFormat','flexRow','flexJustifyStart','flexAlignCenter'],
    });

    const bold = newComponent({
      text: "format_bold",
      classes: ['icon',"material-icons"],
    });

    const italic = newComponent({
      text: "format_italic",
      classes: ['icon',"material-icons"],
    });

    const otherOptions = newComponent({
      classes: ['otherOptions','flexRow','flexAlignCenter','flexJustifyEnd'],
    });

    const indentDecrease = newComponent({
      text: "format_indent_decrease",
      classes: ['icon',"material-icons"],
    });

    const indentIncrease = newComponent({
      text: "format_indent_increase",
      classes: ['icon',"material-icons"],
    });


    const listNumbered = newComponent({
      text: "format_list_numbered",
      classes: ['icon',"material-icons"],
    });

    const list = newComponent({
      text: "format_list_bulleted",
      classes: ['icon',"material-icons"],
    });

    const link = newComponent({
      text: "link",
      classes: ['icon',"material-icons"],
    });

    const attach = newComponent({
      text: "attach_file",
      classes: ['icon',"material-icons"],
    });

    bold.appendTo(textFormat.element);
    italic.appendTo(textFormat.element);

    attach.appendTo(otherOptions.element);
    link.appendTo(otherOptions.element);
    indentDecrease.appendTo(otherOptions.element);
    indentIncrease.appendTo(otherOptions.element);
    listNumbered.appendTo(otherOptions.element);
    list.appendTo(otherOptions.element);

    textFormat.appendTo(bar.element);
    otherOptions.appendTo(bar.element);
    return bar;
  }
}
window.customElements.define("aon-messenger-chat", AonMessengerChat);
