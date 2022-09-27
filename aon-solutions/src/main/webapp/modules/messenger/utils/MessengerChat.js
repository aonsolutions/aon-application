import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, MATERIAL_ICONS, MSG, EVENT, CONSTANT, TAG, AON_ICONS} from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { MessengerOptions, MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_EVALUATION, TASK_SOURCE, TASK_STATUS } from "../MessengerEnums.js";
import { TaskCreationUtils } from "./TaskCreationUtils.js";
import { TaskUtils } from "./TaskUtils.js";
import { AonIconButton } from "../../../components/aon-icon-button.js";
import { getNextTask, getPreviousTask } from "../TaskCache.js";
import * as ACTIONS from "../../actions.js";
import { SigninSidenav } from "../../timecontrol/signinEnums.js";
import { AonTab } from "../../../components/aon-tab.js";
import { Task } from "../../../models/task/Task.js";
import { addHorizontalScroll, sortBy } from "../../../services/utils.js";
import { AonIcon } from "../../../components/aon-icon.js";
import { getTasks } from "../../../services/taskService.js";


/**
 * 
 * @param {Task} task
 */
const buildForm = (task)=> {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
  buildToolbar(task);

  const mainView = TaskCreationUtils.createMainView(aonMessengerChat); //DIV MAIN
  mainView.style.overflow = 'auto';
  mainView.classList.add(CSS.NO_SCROLLBAR);

  const firstDiv = createFirstDiv(mainView); //-------------------------DIV LEFT
  firstDiv.style.boxSizing = 'border-box';

  const secondDiv = setStyles(createSecondDiv(mainView),{ //-------------------------DIV RIGHT
    boxSizing: 'border-box',
    height: '96%'
  });

  TaskUtils.buildForm(task, firstDiv);

  if(task.id){
    buildTabs(task, secondDiv);
  } 
}


/**
 * 
 * @param {Task} task
 */
const buildToolbar = (task) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    let application = aonMessengerChat.getApplication();
    if(application){
      application.addToolbarOption2(SigninSidenav.ADD, () =>application.getParent().showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, {source:TASK_SOURCE.QUERY}));
    }

    const sourceText =  MSG[task.getSource().toString().toUpperCase()] || task.getSource();
    
    const toolbar = setAttributes(new AonToolbar(), {
      id:aonMessengerChat.TOOLBAR,
      type:ToolbarType.SECONDARY,
      title:sourceText +" "+TaskUtils.taskNumberParse(task.number)
    });

    aonMessengerChat.appendChild(toolbar);

    toolbar.addButton2(ACTIONS.NEXT, () => aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getNextTask()) );
		toolbar.addButton2(ACTIONS.PREVIOUS, () =>  aonMessengerChat.applicationParentEl.showView(MESSENGER_VIEWS.AON_MESSENGER_CHAT, getPreviousTask()) );

    const status = task.getStatus();

    if([TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(status) ){
      if(!aonMessengerChat.isCau()){

        if(task.getId()){
          toolbar.addButton2({
            id: MESSENGER_IDS.TOOLBAR_BRANCH,
            name: "Crear Rama",
            aonIcon: AON_ICONS.AON_BRANCH,
          }, () =>TaskCreationUtils.openDialogBranch(task));
        }

        toolbar.addButton2({
          id:  MESSENGER_IDS.TOOLBAR_LABELS,
          name: MSG.LABELS,
          icon: MATERIAL_ICONS.LABEL
          }, (ev) =>  TaskUtils.dialogTaskTags(ev, task)
        );
      }
      
      if(task.getId()){
        toolbar.addButton2({
          ...MessengerOptions.AON_MESSENGER_LIST_CLOSE,
          name: MSG.CLOSE,
          icon:MATERIAL_ICONS.CHECK_CIRCLE_OUTLINE
        }, () =>{
          aonMessengerChat.closeTask();
        });
      }
    }

    if(task.getId()){
      if([TASK_STATUS.DELETED, TASK_STATUS.FINISHED].includes(task.status)){
        toolbar.addButton2({...ACTIONS.RESTORE, name:MSG.REOPEN}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.PENDING));
      }

      if(task.status !== TASK_STATUS.DELETED) {
        if(task.getSource() !==  TASK_SOURCE.GROUPED){
          toolbar.addButton2({...MessengerOptions.AON_MESSENGER_LIST_ARCHIVE, name:MSG.STORE,  icon: MATERIAL_ICONS.ARCHIVE}, () => aonMessengerChat.updateTaskStatus(TASK_STATUS.DELETED));
        }
      } else {
        toolbar.addButtonAfter(ACTIONS.DELETE, () => aonMessengerChat.deleteTask(), ACTIONS.PREVIOUS.id);
      }
    }

    if( [TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status) ){
      toolbar.addButton2(ACTIONS.SAVE, () =>{
        aonMessengerChat.save().then((success) =>{
          if(success){
            aonMessengerChat.showMessage();
          } 
        });
      });
    }
    
    toolbar.addButton2(ACTIONS.BACK, () => aonMessengerChat.back());

    TaskUtils.addIconToolbar(toolbar, task);
}

/**
 * Create desktop chat for messenger
 * @param {Task} task
 * @param {HTMLElement} secondDiv secondDiv
 */
const buildTabs = (task, secondDiv) => {
    const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    secondDiv.classList.add(CSS.FLEX_WRAP);

    let tab = setStyles(new AonTab(),{
      overflow: 'auto',
      whiteSpace: 'nowrap'
    });
    tab.id = MESSENGER_IDS.AON_TAB;
    tab.className = CSS.MATERIAL_SCROLL;
    addHorizontalScroll(tab);

    secondDiv.appendChild(tab);

    const wrapper = buildWrapper(secondDiv);

    buildChat(task, wrapper);

    tab.addOption({ 
      title: MSG.CONVERSATION, 
      dataset:{
        id: task.id,
        number: TaskUtils.taskNumberParse(task.number)
      },
      fn: () => {
        buildChat(task, wrapper);
        aonMessengerChat.getTaskWorkflow(task);
      },
    });

    const parentObj = task.getParentObj();
    if(parentObj){
      const tk = new Task(parentObj);
      let title = getTitleHtml(tk, true);

      const parentTab = tab.addOption({
        title,
        dataset:{
          id: tk.id,
          number: TaskUtils.taskNumberParse(tk.number)
        },
        fn: ()=>{
          // buildChat(tk, wrapper); aonMessengerChat.getTaskWorkflow(tk);
        }
      });
      if(parentTab){
        parentTab.style.display = 'none';
      }
    }

    const childs = sortBy(task.getChilds(), 'number');

    if(task.isGrouped()){
      tab.addOption({ 
        title: MSG.GROUPED, 
        dataset:{
          id: "TasksList",
          number: MSG.TASKS
        },
        fn: () => {
          TaskUtils.checkButtonsToolbar(task, task.id);
          loadTaskGrouped(task, wrapper);
        },
      });
    }

    childs.forEach(t=>{

      const tk = new Task(t);
      let title = getTitleHtml(tk, false);

      tab.addOption({
        title,
        dataset:{
          id: tk.id,
          number: TaskUtils.taskNumberParse(tk.number)
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
  divs.iconOpenFull.addEventListener(EVENT.CLICK,()=> openFullComment(aonMessengerChat, divs.aonTextArea, task));
}

const openFullComment = (aonMessengerChat, aonTextArea, task) => {
  const dialog = aonMessengerChat.applicationEl.getDialog();
  dialog.autoclose = false;
  dialog.clear();
  dialog.width = "600px";

  const textarea = TaskCreationUtils.createAonTextAreaEditor(`${MSG.WRITE_A_COMMENT}...`);
  textarea.textBoxMinHeight = "250px";
  textarea.textBoxMaxHeight = "300px";
  dialog.setContent(textarea);

  textarea.moveBar();

  if(aonTextArea.value) textarea.value = aonTextArea.value;
  textarea.addEventListener(EVENT.INPUT, ({target})=>{
    aonTextArea.value = target.value || "";
    aonTextArea.FILES = textarea.getFiles();
  });


  let button = dialog.addSendAction(()=>{
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
      paddingBottom: "30px",
      paddingRight: "10px"
    },
  });
  secondDiv.appendTo(mainView);
  return secondDiv.element;
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
    background: transparent
  });
  upIcon.addEventListener(EVENT.CLICK, ()=> TaskUtils.upChat() );
  leftButtonBar.appendChild(upIcon);

  const downIcon = setAttributes(new AonIconButton(), {
    icon: MATERIAL_ICONS.EXPAND_MORE,
    id: "downIcon",
    background: transparent,
  });
  downIcon.addEventListener(EVENT.CLICK, ()=> TaskUtils.downChat() )
  leftButtonBar.appendChild(downIcon);
}


const getTitleHtml = (task, isParent = false) => {

  const span = document.createElement(TAG.SPAN);

  const { icon_color, icon } = TaskUtils.getIconJson(task);

  let iconEl = document.createElement(TAG.I);

  if(task.isTask()) {
    iconEl = new AonIcon();
    iconEl.icon = AON_ICONS.AON_BRANCH;
    iconEl.title = "Branch";
    iconEl.color = icon_color;
  } else {
    setStyles(iconEl,{
      position: "relative",
      fontSize: "1.4em",
      top: "3px",
      marginLeft: "1px",
      color:icon_color
    });
    
    iconEl.title      = !isParent ? task.getSource() : MSG.PARENT;
    iconEl.innerText  = !isParent ? icon : MATERIAL_ICONS.FORK_LEFT;
    iconEl.className  = CONSTANT.MATERIAL_ICONS_OUTLINED;
  } 

  span.appendChild(iconEl);

  let assigned = "";   
  if(task.task_holder&&task.task_holder.id)    {
    assigned = task.task_holder.alias || task.task_holder.name; 
  } else if(task.workgroup&&task.workgroup.description) {
    assigned = task.workgroup.description;
  }                                       
   
  let spanTwo = document.createElement(TAG.SPAN);
  spanTwo.innerHTML = TaskUtils.taskNumberParse(task.number);
  spanTwo.title = assigned;
  span.appendChild(spanTwo);

  return span.outerHTML;
};



// --------------------------------ADD TAB GROUP TASK

let tasksTmp = [];

const loadTaskGrouped = (task, parent=undefined) => {
  const id = "divTaskList";
  tasksTmp = [];
  
  if(!parent){
    parent = document.getElementById(MESSENGER_IDS.SECOND_DIV);
  } else {
    parent.innerHTML = "";
  }

  let div = document.getElementById(id);
  if(div){
    div.remove()
  }

  div = setStyles(document.createElement(TAG.DIV) ,{//-------------------------DIV RIGH
    boxSizing: "border-box",
    height: "96%",
    width: "100%",
    overflow: "auto"
  }); 
  div.id = id;
  div.classList.add(CSS.MATERIAL_SCROLL);
  parent.appendChild(div);

  getTasks({parent:true, status:TASK_STATUS.PENDING, page:1, perPage:200})
  .then( ts => {
    tasksTmp = ts.filter(t => task.getChilds().find(x => x.id === t.id) === undefined && t.source===TASK_SOURCE.CAU && task.getId() !== t.id );

    addListTasksExist(div, task);
  
    addListTasks(div, task);
    
    updateListTask(task);
  });
}

const addListTasksExist = (parent, task) => {
  const icon = MATERIAL_ICONS.DELETE;
  const fn = (t) => {
    task.removeChild(t.id);
    tasksTmp.unshift(t);
    updateListTask(task);
  };

  const simpleList = TaskCreationUtils.createSimpleList("Agrupadas", "simpleListTaskExist", parent);
  simpleList.option = {
    icon,
    fn
  };
}

const addListTasks = (parent, task) => {
  const icon = MATERIAL_ICONS.ADD;
  const fn = (t) => {
    tasksTmp = tasksTmp.filter(d => d.id !==t.id);
    task.addChild(t);
    updateListTask(task);
  };

  const simpleList = TaskCreationUtils.createSimpleList("Sin agrupar", "simpleListTask", parent);
  simpleList.option = {
    icon,
    fn
  };
  simpleList.style.marginTop = "10px";
}

const updateListTask = (task) => {
  const childs = task.getChilds();

  const simpleListTaskExist = document.getElementById("simpleListTaskExist");
  simpleListTaskExist.tasks = childs;
  simpleListTaskExist.init();

  const simpleListTask = document.getElementById("simpleListTask");
  simpleListTask.tasks = tasksTmp;
  simpleListTask.init();
}


export const MessengerChat = {
  buildForm,
  loadTaskGrouped
}