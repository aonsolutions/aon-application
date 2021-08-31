import { AonToolbar } from "../../../components/aon-toolbar";
import { COLORS, CSS, MATERIAL_ICONS, MSG, EVENT } from "../../../environments/environments";
import { ToolbarType } from "../../../models/enums";
import { newComponent, setAttributes, setClasses, setStyles } from "../../../services/utils";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums";
import * as ACTIONS from "../../actions.js";
import {  createMainView, createTitle, createAonTextArea, createChat, createOutlinedMaterialIcon, createSectionComment} from "./creationUtils";
import { addLine, buildFormQuery, buildFormRequest, buildTextareaToolbar } from "./utils";
import { AonIconButton } from "../../../components/aon-icon-button";
import { getNextTask, getPreviousTask } from "../TaskCache";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
export const buildDesktop = (aonMessengerChat)=> {

  buildToolbar(aonMessengerChat);

  const mainView = createMainView(aonMessengerChat); //DIV MAIN

  const firstDiv = createFirstDiv(mainView); //-------------------------DIV LEFT

  const secondDiv = createSecondDiv(mainView); //-------------------------DIV RIGHT

  if(aonMessengerChat.task.source === TASK_SOURCE.REQUEST) 
    buildRequest(firstDiv, aonMessengerChat);
  else 
    buildQuery(firstDiv, aonMessengerChat); 

  if(aonMessengerChat.task.id){
    buildSectionHistoric(secondDiv);
  }
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
        toolbar.addButton2({...ACTIONS.RESTORE, name:MSG.REOPEN}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.PENDING));

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
 * SOURCE QUERY
 * @param {HTMLElement} firstDiv firstDiv
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const buildQuery = (firstDiv, aonMessengerChat) => {
    const task = aonMessengerChat.task;

    buildFormQuery(firstDiv, aonMessengerChat);

    const aonTextArea = setStyles(createAonTextArea(`${MSG.WRITE_A_DESCRIPTION}...`), {
        minHeight: '200px',
        maxHeight: '300px'
    });
    aonTextArea.id = MESSENGER_IDS.DESCRIPTION_TASK;
    firstDiv.appendChild(aonTextArea);
    if(task && task.getDescriptionJson().observation) aonTextArea.value = task.getDescriptionJson().observation;

    buildTextareaToolbar(aonTextArea, task, false);
}

/**
 * SOURCE PROCESS
 * @param {HTMLElement} firstDiv firstDiv
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
 const buildRequest = (firstDiv, aonMessengerChat) => {

  buildFormRequest(firstDiv, aonMessengerChat);

}

/**
 * Create desktop chat for messenger
 * @param {HTMLElement} secondDiv secondDiv
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const buildSectionHistoric = (secondDiv) => {
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
          alignSelf: 'center',
          paddingBottom: '10px',
          borderBottom: '1px solid #f0f0f0',
        }
    );
    wrapper.appendChild(title);
    
    /**
     * The chat itself
     */
    const sectionComment = createChat(); 
    sectionComment.classList.add(CSS.MATERIAL_SCROLL);
    wrapper.appendChild(sectionComment);


    //-----------------ADD TEXT AREA CHAT
    addTextAreaChat(wrapper); //

    addChatButtonsUpDown(secondDiv, sectionComment); //BUTTONS DOWN UP CHAT

    addLine();
}

const addTextAreaChat = (wrapper) => {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);

  const divs = createSectionComment(wrapper);
  setStyles(divs.divComment,{
    borderRadius:"8px 8px",
    border: `1px solid ${CSS.variable(COLORS.AON_BLUE)}`
  });

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

  buildTextareaToolbar(textarea, aonMessengerChat.task, true);

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
  textarea.addEventListener(EVENT.INPUT, ({target})=>{
    aonTextArea.value = target.value || "";
    aonTextArea.FILES = target.FILES;
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

const createFirstDiv = (mainView) => {
  const div = newComponent({
    classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER, CSS.MATERIAL_SCROLL],
    id: MESSENGER_IDS.FIRST_DIV,
    styles: {
      width: "50%",
      minWidth: "400px",
      paddingTop: "5vh",
      paddingRight: "20px",
      paddingLeft: "30px",
      overflow:"auto",
      top: 0
    },
  });
  div.appendTo(mainView);

  return div.element;
}

const createSecondDiv = (mainView) => {
  const secondDiv = newComponent({
    classes: [CSS.FLEX_ROW],
    id: MESSENGER_IDS.SECOND_DIV,
    styles: {
        width: "50%",
        // height: "90%",
        minWidth: "400px",
        paddingTop: "20px",
        paddingBottom: "30px",
    },
  }).element;

  mainView.appendChild(secondDiv);
  return secondDiv;
}

/**
 * 
 * @param {HTMLElement} secondDiv div right
 * @param {HTMLElement} sectionComment div sectionComment
 */
const addChatButtonsUpDown = (secondDiv, sectionComment) => {
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
  upIcon.addEventListener(EVENT.CLICK, ()=>sectionComment.scrollTo(0,0));
  leftButtonBar.appendChild(upIcon);

  const downIcon = setAttributes(new AonIconButton(), {
    icon: MATERIAL_ICONS.EXPAND_MORE,
    id: "downIcon",
    background: "transparent",
  });
  downIcon.addEventListener(EVENT.CLICK, ()=> sectionComment.scrollTo(0, sectionComment.scrollHeight))
  leftButtonBar.appendChild(downIcon);

}