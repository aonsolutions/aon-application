import { AonToolbar } from "../../../components/aon-toolbar";
import { COLORS, CSS, MATERIAL_ICONS, MSG, EVENT } from "../../../environments/environments";
import { ToolbarType } from "../../../models/enums";
import { newComponent, setAttributes, setClasses, setStyles } from "../../../services/utils";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums";
import * as ACTIONS from "../../actions.js";
import { createAonTextArea, createOutlinedMaterialIcon, createProcessType, createStartJustifiedColumn, createTaskHolder, createWorkgroup, titleFirstDiv } from "./creationUtils";
import { createButtonWrapper, createDivEditable, createMainView, createReceiverDiv, createSendBar, createSendButton, createTitle } from "../createComponents";
import { buildTextareaToolbar, fillProcessType, fillWorkGroup } from "./utils";
import { AonIconButton } from "../../../components/aon-icon-button";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
export const buildDesktop = (aonMessengerChat)=> {

  buildToolbarDesktop(aonMessengerChat);

  const mainView = createMainView(aonMessengerChat);

  if(aonMessengerChat.task.source === TASK_SOURCE.GITHUB) 
    buildProcess(mainView, aonMessengerChat);
  else 
    buildManual(mainView, aonMessengerChat); // SOURCE MANUAL 

  buildDesktopChat(mainView, aonMessengerChat);

}

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
const buildToolbarDesktop = (aonMessengerChat) => {
    const task = aonMessengerChat.task;

    const toolbar = setAttributes(new AonToolbar(), {
      id:aonMessengerChat.TOOLBAR,
      type:ToolbarType.SECONDARY,
      title:"#" + (task.number || "0").toString().padStart(5, 0)
    });

    aonMessengerChat.appendChild(toolbar);

    if(task.id){
      if(task.status == TASK_STATUS.PENDING || task.status == TASK_STATUS.IN_PROGRESS){
        toolbar.addButton2({
          ...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
          name: 'Cerrar',
        }, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED));
      }
      if(task.status == TASK_STATUS.DELETED || task.status == TASK_STATUS.FINISHED)
        toolbar.addButton2({...ACTIONS.RESTORE, name:"Reabrir"}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.PENDING));

      if(task.status != TASK_STATUS.DELETED) 
        toolbar.addButton2({...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE, name:MSG.STORE}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.DELETED));
    }

    if(task.status == TASK_STATUS.PENDING || task.status == TASK_STATUS.IN_PROGRESS)
      toolbar.addButton2(ACTIONS.SAVE, () => aonMessengerChat.save());

    toolbar.addButton2(ACTIONS.BACK, () => aonMessengerChat.back());

    const titleSpan = setClasses(toolbar.querySelector( `.${CSS.AON_SECONDARY_TOOLBAR_TITLE}` ), [CSS.FLEX_ROW, CSS.FLEX_ALIGN_CENTER]);

    const status = createOutlinedMaterialIcon({
      color: CSS.variable(task.status == TASK_STATUS.PENDING || task.status == TASK_STATUS.IN_PROGRESS ? COLORS.ONLINE_GREEN : COLORS.GRAYSON),
      name: "info",
      size: "20px",
    });
    status.element.style.marginLeft = "10px";
    status.appendTo(titleSpan);
}


/**
 * SOURCE MANUAL
 * @param {HTMLElement} mainView div principal
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const buildManual = (mainView, aonMessengerChat) => {

    const task = aonMessengerChat.task;
    const application = aonMessengerChat.applicationEl;

    const firstDiv = newComponent({
      classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER, CSS.MATERIAL_SCROLL],
      id: MESSENGER_IDS.FIRST_DIV,
      styles: {
        width: "50%",
        height: "100%",
        minWidth: "400px",
        maxWidth: "600px",
        paddingTop: "5vh",
        paddingRight: "20px",
        paddingLeft: "60px",
        overflow:"auto",
        top: 0
      },
    });
    firstDiv.appendTo(mainView);

    const titleDiv = titleFirstDiv();
    firstDiv.appendChild(titleDiv);
    //TITLE
    const title = createDivEditable(task.title, MESSENGER_IDS.TITLE_TASK, `Escriba su ${MSG.ISSUE} aquí`);
    titleDiv.appendChild(title);
  
    const receiverDiv = createReceiverDiv();
    receiverDiv.appendTo(firstDiv.element);

     //-----------------WORKGROUP
    const workgroupSelect = createWorkgroup();
    workgroupSelect.style.width = "100%";
    receiverDiv.appendChild(workgroupSelect);
    fillWorkGroup(task, application);

    //-----------------TASK HOLDER
    const taskHolderSelect = createTaskHolder();
    taskHolderSelect.style.marginLeft = "5px";
    taskHolderSelect.style.width = "100%";
    receiverDiv.appendChild(taskHolderSelect);

    const aonTextArea = setStyles(createAonTextArea(aonMessengerChat.task.id ? `${MSG.WRITE_A_COMMENT}...` : `${MSG.WRITE_A_DESCRIPTION}...`), {
        height: '100%',
        maxHeight: '300px'
    });
    firstDiv.appendChild(aonTextArea);
    buildTextareaToolbar(aonTextArea, task);

    if(aonMessengerChat.task.id){ //UPDATE
        /**
         * Creating send bar
         */
        const sendBar = createSendBar();
        sendBar.appendTo(firstDiv.element);

        const sendButtonWrapper = createButtonWrapper();
        sendButtonWrapper.appendTo(sendBar.element);

        //BUTTON SEND COMMENT
        const sendButton = createSendButton();
        sendButton.addEventListener(EVENT.CLICK,()=> aonMessengerChat.saveTaskWorkflow());
        sendButtonWrapper.appendChild(sendButton);
    }

    setTimeout(() => {
      mainView.style.opacity = 1;
      mainView.style.marginTop = 0;
    }, 100);
}

/**
 * SOURCE PROCESS
 * @param {HTMLElement} mainView div principal
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
 const buildProcess = (mainView, aonMessengerChat) => {

  const task = aonMessengerChat.task;
  const application = aonMessengerChat.applicationEl;

  const firstDiv = newComponent({
    classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER, CSS.MATERIAL_SCROLL],
    id: MESSENGER_IDS.FIRST_DIV,
    styles: {
      width: "50%",
      height: "100%",
      minWidth: "400px",
      maxWidth: "600px",
      paddingTop: "5vh",
      paddingRight: "20px",
      paddingLeft: "60px",
      overflow:"auto",
      top: 0
    },
  });
  mainView.appendChild(firstDiv.element)

  const receiverDiv = createReceiverDiv();
  receiverDiv.appendTo(firstDiv.element);

  //-----------------TYPE PROCESS
  const typeProcess = createProcessType();
  typeProcess.style.width = "100%";
  receiverDiv.appendChild(typeProcess);
  fillProcessType(task, application);
  
   //-----------------WORKGROUP
  const workgroupSelect = createWorkgroup();
  workgroupSelect.style.width = "100%";
  workgroupSelect.style.marginLeft = "5px";
  receiverDiv.appendChild(workgroupSelect);
  fillWorkGroup(task, application);


  // DIV PROCESS
  const divProcess = createStartJustifiedColumn().element;
  divProcess.id = MESSENGER_IDS.PROCESS_DIV;
  firstDiv.appendChild(divProcess);


  const aonTextArea = setStyles(createAonTextArea(`${MSG.WRITE_A_COMMENT}...`), { height: '100%', maxHeight: '100px' });
  firstDiv.appendChild(aonTextArea);
  buildTextareaToolbar(aonTextArea, task);

  if(aonMessengerChat.task.id){ //UPDATE
      /**
       * Creating send bar
       */
      const sendBar = createSendBar();
      sendBar.appendTo(firstDiv.element);

      const sendButtonWrapper = createButtonWrapper();
      sendButtonWrapper.appendTo(sendBar.element);

      //BUTTON SEND COMMENT
      const sendButton = createSendButton();
      sendButton.addEventListener(EVENT.CLICK,()=> aonMessengerChat.saveTaskWorkflow());
      sendButtonWrapper.appendChild(sendButton);
  }

  setTimeout(() => { 
    setStyles(mainView, { opacity: 1, marginTop: 0 }) 
  }, 100);
}

/**
 * Create desktop chat for messenger
 * @param {HTMLElement} mainView div principal
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const buildDesktopChat = (mainView, aonMessengerChat) => {

    const secondDiv = newComponent({
        classes: [CSS.FLEX_ROW],
        id: MESSENGER_IDS.SECOND_DIV,
        styles: {
            width: "50%",
            height: "90%",
            minWidth: "400px",
            maxWidth: "600px",
            paddingTop: "5vh",
            display: !aonMessengerChat.task.id  ? "none" : null
        },
    }).element;

    mainView.appendChild(secondDiv);

    /**
     * Wrapper 
     * if some new side menus / toolbars needed, here.
     */
    const wrapper = newComponent({
        type: MESSENGER_COMPONENTS.WRAPPER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            width: "90%",
            height: '100%',
        }
    });
    wrapper.appendTo(secondDiv);

    const title = setStyles(createTitle(MSG.COMMENTS), {
            maxWidth: '550px',
            alignSelf: 'center',
            paddingBottom: '10px',
            borderBottom: '1px solid #f0f0f0',
        }
    );
    wrapper.appendChild(title);
    
    /**
     * The chat itself
     */
    const chat = newComponent({
        type: MESSENGER_COMPONENTS.CHAT,
        id: MESSENGER_IDS.MESSENGER_CHAT,
        classes: ["continueLined", CSS.FLEX_COLUMN, CSS.MATERIAL_SCROLL, CSS.FLEX_ALIGN_CENTER],
        styles: {
            position: "relative",
            width: '100%',
            zIndex: "0",
            height: '100%',
            padding: "20px",
            paddingTop: "20px",
            overflow: 'auto',
            borderBottom: '1px solid #f0f0f0',
            "scroll-behavior": "smooth",
        }
    }).element;
    wrapper.appendChild(chat);

    /**
     * A side buttonbar 
     */
    const leftButtonBar = newComponent({
        classes: [CSS.FLEX_COLUMN, CSS.FLEX_JUSTIFY_END],
        styles: {
            position: 'relative',
            width: "10%",
            height: "100%"
        }
    });
    leftButtonBar.appendTo(secondDiv);

    const upIcon = setAttributes(new AonIconButton(), {
        icon: MATERIAL_ICONS.EXPAND_LESS,
        id: "upIcon",
        background: "transparent",
    });
    upIcon.addEventListener(EVENT.CLICK, ()=>chat.scrollTo(0,0));
    leftButtonBar.appendChild(upIcon);

    const downIcon = setAttributes(new AonIconButton(), {
        icon: MATERIAL_ICONS.EXPAND_MORE,
        id: "downIcon",
        background: "transparent",
    });
    downIcon.addEventListener(EVENT.CLICK, ()=> chat.scrollTo(0, chat.scrollHeight))
    leftButtonBar.appendChild(downIcon);

     /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = document.querySelector(".continueLined");
    if (lined) lined.style.setProperty("--height", lined.scrollHeight + "px");
}