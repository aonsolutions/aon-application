import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, MATERIAL_ICONS, MSG, EVENT, CONSTANT, TAG, AON_ICONS} from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_EVALUATION, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums.js";
import { TaskCreationUtils } from "./TaskCreationUtils.js";
import { addIconToolbar, buildForm, buildTextareaToolbar, dialogTaskTags, downChat, getIconJson, taskNumberParse, upChat } from "./utils.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { getNextTask, getPreviousTask } from "../TaskCache.js";
import * as ACTIONS from "../../actions.js";
import { SigninSidenav } from "../../timecontrol/signinEnums.js";
import { AonTab } from "../../../components/aon-tab.js";
import { Task } from "../../../models/task/Task.js";
import { sortBy } from "../../../services/utils.js";
import { AonIcon } from "../../../components/aon-icon.js";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
export const buildDesktop = (aonMessengerChat)=> {

  buildToolbar(aonMessengerChat);

  const mainView = TaskCreationUtils.createMainView(aonMessengerChat); //DIV MAIN
  mainView.style.overflow = 'auto';
  mainView.classList.add(CSS.NO_SCROLLBAR);

  const firstDiv = createFirstDiv(mainView); //-------------------------DIV LEFT

  buildForm(firstDiv, aonMessengerChat);

  if(aonMessengerChat.task.id){
    const secondDiv = createSecondDiv(mainView); //-------------------------DIV RIGHT

    firstDiv.style.boxSizing = 'border-box';
    secondDiv.style.boxSizing = 'border-box';
    secondDiv.style.height = '96%';

    buildTabs(secondDiv, aonMessengerChat);
  }
}

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
const buildToolbar = (aonMessengerChat) => {
    const task = aonMessengerChat.task;
    let application = aonMessengerChat.getApplication();
    if(application){
      application.addToolbarOption2(SigninSidenav.ADD, () =>application.getParent().showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY}));
    }

    const sourceText =  MSG[task.source.toString().toUpperCase()] || task.source;
    const toolbar = setAttributes(new AonToolbar(), {
      id:aonMessengerChat.TOOLBAR,
      type:ToolbarType.SECONDARY,
      title:sourceText +" "+taskNumberParse(task.number)
    });

    aonMessengerChat.appendChild(toolbar);

    toolbar.addButton2(ACTIONS.NEXT, () => aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getNextTask()) );
		toolbar.addButton2(ACTIONS.PREVIOUS, () =>  aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getPreviousTask()) );

    if( task.status && [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) ){
      if(!aonMessengerChat.isCau()){

        if(task.id){
          toolbar.addButton2({
            id: MESSENGER_IDS.TOOLBAR_BRANCH,
            name: "Crear Rama",
            aonIcon: AON_ICONS.AON_BRANCH,
          }, (e) =>TaskCreationUtils.openDialogBranch(e));
        }

        toolbar.addButton2({
          id:  MESSENGER_IDS.TOOLBAR_LABELS,
          name: MSG.LABELS,
          icon: MATERIAL_ICONS.LABEL
          }, (ev) =>  dialogTaskTags(ev, aonMessengerChat)
        );
      }
      
      if(task.id){
        toolbar.addButton2({
          ...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
          name: MSG.CLOSE,
          icon:MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE
        }, () =>{
          aonMessengerChat.closeTask();
        });
      }
    }

    if(task.id){
      if([TASK_STATUS.DELETED, TASK_STATUS.FINISHED].includes(task.status)){
        toolbar.addButton2({...ACTIONS.RESTORE, name:MSG.REOPEN}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.PENDING));
      }

      if(task.status != TASK_STATUS.DELETED) {
        toolbar.addButton2({...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE, name:MSG.STORE,  icon: MATERIAL_ICONS.ARCHIVE}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.DELETED));
      } else {
        toolbar.addButtonAfter(ACTIONS.DELETE, () => aonMessengerChat.deleteTask(), ACTIONS.PREVIOUS.id);
      }
    }

    if( [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) )
      toolbar.addButton2(ACTIONS.SAVE, () =>{
        aonMessengerChat.save().then((success) =>{
          if(success) aonMessengerChat.showMessage();
        });
      });

    toolbar.addButton2(ACTIONS.BACK, () => aonMessengerChat.back());

    addIconToolbar(toolbar, task);
}

/**
 * Create desktop chat for messenger
 * @param {HTMLElement} secondDiv secondDiv
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat
 */
const buildTabs = async (secondDiv, aonMessengerChat) => {
    secondDiv.classList.add(CSS.FLEX_WRAP);

    const task = aonMessengerChat.task;

    let tab = new AonTab();
    tab.id = MESSENGER_IDS.AON_TAB;
    secondDiv.appendChild(tab);

    // let divTab = document.getElementById(tab.DIV);
    // console.log("newDiv", divTab);
    // divTab.style.display = "flex";
    // divTab.style.flexWrap = "wrap";
    // divTab.style.height = "auto";
    // divTab.style.minHeight = "40px";

    const wrapper = buildWrapper(secondDiv);

    buildChat(task, wrapper);

    tab.addOption({ 
      title: MSG.CONVERSATION, 
      dataset:{
        id: task.id,
        number: taskNumberParse(task.number)
      },
      fn: () => {
        buildChat(task, wrapper);
        aonMessengerChat.getTaskWorkflow(task);
      },
    });

    let parentObj = task.getParentObj();
    if(parentObj){
      const tk = new Task(parentObj);
      let title = getTitleHtml(tk, true);

      const parentTab = tab.addOption({
        title,
        dataset:{
          id: tk.id,
          number: taskNumberParse(tk.number)
        },
        fn: ()=>{
          // buildChat(tk, wrapper);
          // aonMessengerChat.getTaskWorkflow(tk);
        }
      });
      if(parentTab){
        parentTab.style.display = 'none';
      }
    }

    sortBy(task.getChilds(), 'number').forEach(t=>{

      const tk = new Task(t);
      let title = getTitleHtml(tk, false);

      tab.addOption({
        title,
        dataset:{
          id: tk.id,
          number: taskNumberParse(tk.number)
        },
        fn: ()=>{
          buildChat(tk, wrapper);
          aonMessengerChat.getTaskWorkflow(tk);
        }
      });
    });
}

const buildWrapper = (secondDiv) => {
   /**
   * Wrapper 
   * if some new side menus / toolbars needed, here.
   */
    const wrapper = newComponent({
      type: MESSENGER_COMPONENTS.WRAPPER,
      classes: [CSS.FLEX_COLUMN],
      styles: {
        width:'94%',
        height: '100%',
      }
    }).element;
    secondDiv.appendChild(wrapper);

    /**
     * The chat itself
     */
    const chat = TaskCreationUtils.createChat(); 
    
    chat.classList.add(CSS.MATERIAL_SCROLL);
    wrapper.appendChild(chat);

    addChatButtonsUpDown(secondDiv); //BUTTONS DOWN UP CHAT

    return wrapper;
}

const buildChat = (task, wrapper) => {
    wrapper.innerHTML = "";

    wrapper.dataset.taskId = task.id;

    const chat = TaskCreationUtils.createChat(); 
    
    chat.classList.add(CSS.MATERIAL_SCROLL);
    wrapper.appendChild(chat);

    //-----------------ADD TEXT AREA CHAT
    if( [TASK_STATUS.IN_PROGRESS, TASK_STATUS.PENDING].includes(task.status) ){
      addTextAreaChat(wrapper, task); //
    } else if(task.evaluation){ // create section rating section
      const evaluation = task.evaluation;

      let section = TaskCreationUtils.createSectionRating(wrapper, false);
      
      Object.values(TASK_EVALUATION)
      .forEach((r)=>{
        const selected = r === evaluation;
        section.appendChild(TaskCreationUtils.createIconEvaluation(`../../../assets/img/evaluation/${r}.png`, selected));
      });
    }
}

const addTextAreaChat = (wrapper, task) => {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
  const divs = TaskCreationUtils.createSectionComment(wrapper);
  setStyles(divs.divWrite,{
    borderRadius:"5px",
    border: `1px solid ${CSS.variable(COLORS.AON_BLUE)}`
  });

  const label = TaskCreationUtils.createLabelFileText();
  label.addEventListener(EVENT.CLICK, ()=>divs.aonTextArea.clickFile());
  divs.divWrite.appendChild(label);

  divs.aonTextArea.height = "60px";
  divs.aonTextArea.addEventListener(EVENT.KEYDOWN, (ev)=> {
    if (ev.ctrlKey && ev.keyCode == 13) {
      aonMessengerChat.saveComment(undefined, task);
    } else if(ev.ctrlKey && ev.keyCode == 88){
      openFullComment(aonMessengerChat, divs.aonTextArea, task);
    }
  });

  divs.iconSend.addEventListener(EVENT.CLICK, ()=> aonMessengerChat.saveComment(undefined, task));
  divs.iconOpenFull.addEventListener(EVENT.CLICK,()=> openFullComment(aonMessengerChat, divs.aonTextArea));
}

const openFullComment = (aonMessengerChat, aonTextArea, task) => {
  const dialog = aonMessengerChat.applicationEl.getDialog();
  dialog.clear();
  if (!aonMessengerChat.isMobile()) dialog.width = "600px";
  const textarea = setStyles(TaskCreationUtils.createAonTextArea(`${MSG.WRITE_A_COMMENT}...`), {
    height: '100%',
    maxHeight: '300px',
    minHeight: '250px'
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
      aonMessengerChat.saveComment(undefined, task);
      dialog.close();
    }, 
    MSG.SEND
  );
  button.style.padding = "0.7rem 1em";
  dialog.open();
}

const createFirstDiv = (mainView) => {
  const div = newComponent({
    classes: [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER, CSS.NO_SCROLLBAR],
    id: MESSENGER_IDS.FIRST_DIV,
    styles: {
      width: "40%",
      minWidth: "400px",
      paddingTop: "15px",// "20px",
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
      width: "60%",
      // paddingTop: "4px",
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
  const transparent = "transparent";
  const leftButtonBar = newComponent({
    classes: [CSS.FLEX_COLUMN, CSS.FLEX_JUSTIFY_CENTER],
    styles: { width: "6%" }
  });
  leftButtonBar.appendTo(secondDiv);

  const upIcon = setAttributes(new AonIconButton(), {
    icon: MATERIAL_ICONS.EXPAND_LESS,
    id: "upIcon",
    background: transparent,
  });
  upIcon.addEventListener(EVENT.CLICK, ()=>upChat());
  leftButtonBar.appendChild(upIcon);

  const downIcon = setAttributes(new AonIconButton(), {
    icon: MATERIAL_ICONS.EXPAND_MORE,
    id: "downIcon",
    background: transparent,
  });
  downIcon.addEventListener(EVENT.CLICK, ()=>downChat())
  leftButtonBar.appendChild(downIcon);
}


const getTitleHtml = (task, isParent) => {

  let span = document.createElement(TAG.SPAN);

  const { icon_color } = getIconJson(task);

  let icon = document.createElement(TAG.I);

  if(isParent){
    setStyles(icon,{
      position: "relative",
      fontSize: "1.4em",
      top: "3px",
      marginLeft: "1px",
      color:icon_color
    });

    icon.title = MSG.PARENT;
    icon.innerText = MATERIAL_ICONS.FORK_LEFT;
    icon.className  = CONSTANT.MATERIAL_ICONS_OUTLINED;
  } else {
    icon = new AonIcon();
    icon.icon = AON_ICONS.AON_BRANCH;
    icon.title = "Branch";
    icon.color = icon_color;
  }

  span.appendChild(icon);

  let assigned = "";   
  if(task.task_holder&&task.task_holder.id)    {
    assigned = task.task_holder.alias || task.task_holder.name; 
  } else if(task.workgroup&&task.workgroup.description) {
    assigned = task.workgroup.description;
  }                                       
   
  let spanTwo = document.createElement(TAG.SPAN);
  spanTwo.innerHTML = taskNumberParse(task.number);
  spanTwo.title = assigned;
  span.appendChild(spanTwo);

  return span.outerHTML;
};
