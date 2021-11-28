import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, MATERIAL_ICONS, MSG, EVENT} from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums.js";
import {  createMainView, createTitle, createAonTextArea, createChat, createSectionComment, createLabelFileText} from "./creationUtils.js";
import { addIconToolbar, buildForm, buildTextareaToolbar, dialogTaskTags, downChat, upChat } from "./utils.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { getNextTask, getPreviousTask } from "../TaskCache.js";
import * as ACTIONS from "../../actions.js";
import { SigninSidenav } from "../../timecontrol/signinEnums.js";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
export const buildDesktop = (aonMessengerChat)=> {

  buildToolbar(aonMessengerChat);

  const mainView = createMainView(aonMessengerChat); //DIV MAIN

  const firstDiv = createFirstDiv(mainView); //-------------------------DIV LEFT

  buildForm(firstDiv, aonMessengerChat);

  if(aonMessengerChat.task.id){
    const secondDiv = createSecondDiv(mainView); //-------------------------DIV RIGHT
    buildSectionHistoric(secondDiv);
  }
}

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
const buildToolbar = (aonMessengerChat) => {
    const task = aonMessengerChat.task;
    let application = aonMessengerChat.getApplication();
    if(application)
      application.addToolbarOption2(SigninSidenav.ADD, () =>application.getParent().showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY}));

    const sourceText =  MSG[task.source.toString().toUpperCase()] || task.source;
    const toolbar = setAttributes(new AonToolbar(), {
      id:aonMessengerChat.TOOLBAR,
      type:ToolbarType.SECONDARY,
      title:sourceText +" #" + (task.number || "0").toString().padStart(5, 0)
    });

    aonMessengerChat.appendChild(toolbar);

    toolbar.addButton2(ACTIONS.NEXT, () => aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getNextTask()) );
		toolbar.addButton2(ACTIONS.PREVIOUS, () =>  aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getPreviousTask()) );

    if( task.status && [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) ){
      toolbar.addButton2({
      id: 'Labels',
      name: 'Labels',
      icon: MATERIAL_ICONS.LABEL
      }, (ev) =>  dialogTaskTags(ev, aonMessengerChat));
    }

    if(task.id){
      if( [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) ){
        toolbar.addButton2({
          ...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
          name: MSG.CLOSE,
          icon:MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE
        }, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED));
      }
      if([TASK_STATUS.DELETED, TASK_STATUS.FINISHED].includes(task.status))
        toolbar.addButton2({...ACTIONS.RESTORE, name:MSG.REOPEN}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.PENDING));

      if(task.status != TASK_STATUS.DELETED) 
        toolbar.addButton2({...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE, name:MSG.STORE,  icon: MATERIAL_ICONS.ARCHIVE}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.DELETED));
    }

    if( [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) )
      toolbar.addButton2(ACTIONS.SAVE, () => aonMessengerChat.save());

    toolbar.addButton2(ACTIONS.BACK, () => aonMessengerChat.back());

    addIconToolbar(toolbar, task);
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
            width:'100%',
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
    const chat = createChat(); 
    
    chat.classList.add(CSS.MATERIAL_SCROLL);
    wrapper.appendChild(chat);


    //-----------------ADD TEXT AREA CHAT
    addTextAreaChat(wrapper); //

    addChatButtonsUpDown(secondDiv); //BUTTONS DOWN UP CHAT
}

const addTextAreaChat = (wrapper) => {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
  const task = aonMessengerChat.task;

  if( [TASK_STATUS.IN_PROGRESS, TASK_STATUS.PENDING].includes(task.status) ){
    const divs = createSectionComment(wrapper);
    setStyles(divs.divWrite,{
      borderRadius:"5px",
      border: `1px solid ${CSS.variable(COLORS.AON_BLUE)}`
    });

    const label = createLabelFileText();
    label.addEventListener(EVENT.CLICK, ()=>divs.aonTextArea.clickFile());
    divs.divWrite.appendChild(label);

    divs.aonTextArea.addEventListener(EVENT.KEYDOWN, (ev)=> {
      if (ev.ctrlKey && ev.keyCode == 13) {
        aonMessengerChat.saveComment();
      } else if(ev.ctrlKey && ev.keyCode == 88){
        openFullComment(aonMessengerChat, divs.aonTextArea);
      }
    });

    divs.iconSend.addEventListener(EVENT.CLICK, ()=> aonMessengerChat.saveComment());
    divs.iconOpenFull.addEventListener(EVENT.CLICK,()=> openFullComment(aonMessengerChat, divs.aonTextArea));
  }
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

  buildTextareaToolbar(textarea);

  if(aonTextArea.value) textarea.value = aonTextArea.value;
  textarea.addEventListener(EVENT.INPUT, ({target})=>{
    aonTextArea.value = target.value || "";
    aonTextArea.FILES = target.FILES;
  });

  let button = dialog.addSendAction(
    ()=>{
      aonMessengerChat.saveComment();
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
      paddingTop: "20px",
      paddingRight: "20px",
      paddingLeft: "30px",
      paddingBottom: "30px",
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
      paddingTop: "4px",
      paddingBottom: "30px",
      paddingRight: "10px"
    },
  }).element;

  mainView.appendChild(secondDiv);
  return secondDiv;
}

/**
 * 
 * @param {HTMLElement} secondDiv div right
 * @param {HTMLElement} chat div chat
 */
const addChatButtonsUpDown = (secondDiv) => {
  const leftButtonBar = newComponent({
    classes: [CSS.FLEX_COLUMN, CSS.FLEX_JUSTIFY_CENTER]
  });
  leftButtonBar.appendTo(secondDiv);

  const upIcon = setAttributes(new AonIconButton(), {
    icon: MATERIAL_ICONS.EXPAND_LESS,
    id: "upIcon",
    background: "transparent",
  });
  upIcon.addEventListener(EVENT.CLICK, ()=>upChat());
  leftButtonBar.appendChild(upIcon);

  const downIcon = setAttributes(new AonIconButton(), {
    icon: MATERIAL_ICONS.EXPAND_MORE,
    id: "downIcon",
    background: "transparent",
  });
  downIcon.addEventListener(EVENT.CLICK, ()=>downChat())
  leftButtonBar.appendChild(downIcon);
}
