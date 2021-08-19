import { AonToolbar } from "../../../components/aon-toolbar";
import { COLORS, CSS, MATERIAL_ICONS, MSG, EVENT } from "../../../environments/environments";
import { ToolbarType } from "../../../models/enums";
import { newComponent, setAttributes, setClasses, setStyles } from "../../../services/utils";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums";
import * as ACTIONS from "../../actions.js";
import {  createDivEditable, createMainView, createReceiverDiv, createTitle, createAonTextArea, createChat, createOutlinedMaterialIcon, createProcessType, createSectionComment, createStartJustifiedColumn, createTaskHolder, createWorkgroup, titleFirstDiv } from "./creationUtils";
import { addLine, buildTextareaToolbar, fillProcessType, fillWorkGroup } from "./utils";
import { AonIconButton } from "../../../components/aon-icon-button";
import { getNextTask, getPreviousTask } from "../TaskCache";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
export const buildDesktop = (aonMessengerChat)=> {

  buildToolbar(aonMessengerChat);

  const mainView = createMainView(aonMessengerChat);

  if(aonMessengerChat.task.source === TASK_SOURCE.GITHUB) 
    buildProcess(mainView, aonMessengerChat);
  else 
    buildManual(mainView, aonMessengerChat); // SOURCE MANUAL 

  buildChat(mainView, aonMessengerChat);

}

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
const buildToolbar = (aonMessengerChat) => {
    const task = aonMessengerChat.task;

    const toolbar = setAttributes(new AonToolbar(), {
      id:aonMessengerChat.TOOLBAR,
      type:ToolbarType.SECONDARY,
      title:"#" + (task.number || "0").toString().padStart(5, 0)
    });

    aonMessengerChat.appendChild(toolbar);

    toolbar.addButton2(ACTIONS.NEXT, () => aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getNextTask()) );
		toolbar.addButton2(ACTIONS.PREVIOUS, () =>  aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getPreviousTask()) );

    if(task.id){
      if(task.status == TASK_STATUS.PENDING || task.status == TASK_STATUS.IN_PROGRESS){
        toolbar.addButton2({
          ...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
          name: MSG.CLOSE,
          icon:MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE
        }, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED));
      }
      if(task.status == TASK_STATUS.DELETED || task.status == TASK_STATUS.FINISHED)
        toolbar.addButton2({...ACTIONS.RESTORE, name:"Reabrir"}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.PENDING));

      if(task.status != TASK_STATUS.DELETED) 
        toolbar.addButton2({...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE, name:MSG.STORE,  icon: MATERIAL_ICONS.ARCHIVE}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.DELETED));
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
        paddingLeft: "30px",
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

    const aonTextArea = setStyles(createAonTextArea(`${MSG.WRITE_A_DESCRIPTION}...`), {
        height: '100%',
        maxHeight: '300px'
    });
    aonTextArea.id = MESSENGER_IDS.DESCRIPTION_TASK;
    firstDiv.appendChild(aonTextArea);
    if(task && task.description) aonTextArea.value = task.description;

    buildTextareaToolbar(aonTextArea, task);

    setTimeout(() =>  setStyles(mainView, {opacity: 1, marginTop:0}), 100);
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
      paddingTop: "20px",
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

  setTimeout(() => { 
    setStyles(mainView, { opacity: 1, marginTop: 0 }) 
  }, 100);
}

/**
 * Create desktop chat for messenger
 * @param {HTMLElement} mainView div principal
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const buildChat = (mainView, aonMessengerChat) => {

    const secondDiv = newComponent({
        classes: [CSS.FLEX_ROW],
        id: MESSENGER_IDS.SECOND_DIV,
        styles: {
            width: "50%",
            height: "90%",
            minWidth: "400px",
            maxWidth: "600px",
            paddingTop: "20px",
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

    const title = setStyles(createTitle(MSG.HISTORIC), {
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
    const chat = createChat(); 
    chat.classList.add(CSS.MATERIAL_SCROLL);
    wrapper.appendChild(chat);


    //-----------------SECTION COMMENT
    sectionComment(wrapper);

    /**
     * A side buttonbar 
     */
    const leftButtonBar = newComponent({
        classes: [CSS.FLEX_COLUMN, CSS.FLEX_JUSTIFY_CENTER],
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

    addLine();
}

const sectionComment = (wrapper) => {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

  const divs = createSectionComment(wrapper);

  divs.iconSend.addEventListener(EVENT.CLICK, ()=> aonMessengerChat.saveTaskWorkflow());
  divs.iconOpenFull.addEventListener(EVENT.CLICK,()=> openFullComment(aonMessengerChat, divs.aonTextArea));
}

const openFullComment = (aonMessengerChat, aonTextArea) => {
  const dialog = aonMessengerChat.applicationEl.getDialog();
  dialog.clear();
  if (!aonMessengerChat.isMobile()) dialog.width = "600px";
  const textarea = setStyles(createAonTextArea(`${MSG.WRITE_A_COMMENT}...`), {
    height: '100%',
    maxHeight: '300px'
  });
  dialog.setContent(textarea);

  buildTextareaToolbar(textarea, aonMessengerChat.task);

  // const dialogMain = dialog.getMain();

  // const labelMinId = "labelMin";
  // let labelMin = document.getElementById(labelMinId);
  // if(!labelMin){
  //   labelMin = setStyles(document.createElement("label"),{
  //     color: "grey",
  //     fontSize: "30px",
  //     textDecoration: "none",
  //     float: "right",
  //     marginTop: "-17px",
  //     marginRight: "-9px",
  //     cursor: "pointer",
  //     padding: "10px"
  //   })
  //   labelMin.id = labelMinId;
  //   labelMin.title = MSG.MINIMIZE;
  //   const iconMinimize = document.createElement("i");
  //   iconMinimize.textContent = "close_fullscreen";
  //   iconMinimize.className ="material-icons";
  //   iconMinimize.style.fontSize = "21px";
  //   labelMin.appendChild(iconMinimize);
  //   dialogMain.insertBefore(labelMin, dialogMain.children[1]);
  //   iconMinimize.addEventListener(EVENT.CLICK, ()=>dialog.close());
  // }

  if(aonTextArea.value) textarea.value = aonTextArea.value;
  textarea.addEventListener(EVENT.INPUT, ()=>{
    aonTextArea.value = textarea.value || "";
    aonTextArea.FILES = textarea.FILES;
  });

  let button = dialog.addSendAction(
    ()=>{
      aonMessengerChat.saveTaskWorkflow();
      dialog.close();
    }, 
    MSG.SEND
  );
  button.style.padding = "0.7rem 1em";
  dialog.open();
}