import { AonCard } from "../../../components/aon-card.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonTime } from "../../../components/aon-time.js";
import { AonTextArea } from "../../../components/aon-textarea.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { CSS, MSG, TAG, COLORS, MATERIAL_ICONS, EVENT, CONSTANT } from "../../../environments/environments.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { MESSENGER_COMPONENTS, MESSENGER_DIRECTION, MESSENGER_IDS, MESSENGER_VIEWS, TASK_STATUS, WORKFLOW_TYPES } from "../MessengerEnums.js";
import {TaskUtils} from "./TaskUtils.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";
import { saveTaskBranch, getTaskOne, getJobType, getDailyTrackingByTask } from "../../../services/taskService.js";
import { AonMessengerChat } from "../aon-messeger-chat.js";
import { DailyTracking } from "../../../models/task/DailyTracking.js";
import { sortBy } from "../../../services/utils.js";
import { AonMessengerSimpleList } from "../aon-messenger-simple-list.js";


/**
 * 
 * @param {HTMLElement} parent appenchild
 * @param {HTMLElement} child element add Optional
 * @param {Object} properties 
 * @returns 
 */
const createDivGrid = (parent, child, properties)=> {

  const div = newComponent({ type: TAG.DIV, ...properties }).element;

  if(parent) parent.appendChild(div);

  if(child) div.appendChild(child);

  return div;
}

const createDivGridBefore = (parent, child, properties)=> {

  const div = newComponent({ type: TAG.DIV, ...properties }).element;

  parent.parentNode.insertBefore(div, parent.lastElementChild);

  if(child) div.appendChild(child);

  return div;
}

const createBtnAccept = () => {
  let btnAccept = setStyles(document.createElement(TAG.BUTTON),{ margin:"15px 0 0 15px"});
  btnAccept.className = CSS.AON_BUTTON;
  btnAccept.textContent = "Procesar";
  return btnAccept;
}

// ----------------------------------------------------
// MAIN VIEW
// ----------------------------------------------------
const createMainView = (parent) =>{
  const div = document.createElement(TAG.DIV);
  div.className = CSS.AON_SUB_CONTENT;
  div.style.width = "100%";
  parent.appendChild(div);
  const mainView = newComponent({
    type: TAG.DIV,
    id:MESSENGER_IDS.MAIN_VIEW,
    classes: [CSS.FLEX_JUSTIFY_BETWEEN], // CSS.NO_COPY
    styles: {
      transition: ".5s",
      display: "flex",
      flexDirection: "row",
      marginTop: "0vh",
      padding: "0px",
      width: "100%",
      height: "100%",
      overflow: 'hidden'
    },
  }).element;

  div.appendChild(mainView);

  return mainView;
} 

const createMobileMainView = () => newComponent({
  type: TAG.DIV,
  classes: [CSS.FLEX_COLUMN], //  CSS.NO_COPY
  styles: {
    transition: ".5s",
    opacity: 1,
    marginTop: "0vh",
    padding: "0px",
    width: "100%",
    height: "100%",
    overflow: 'hidden'
  },
}).element;


const createDivEditable = (parent, title, value, id, placeholder) => {
  const div = createStartJustifiedColumn();
  if(parent) parent.appendChild(div.element);
  let span = setStyles(document.createElement(TAG.SPAN),{
      fontSize: "0.9375rem",
      width:"100%",
      color:CSS.variable(COLORS.AON_COLOR_INK_MEDIUM_CONTRANST)
  });
  span.textContent = title +` (${MSG.OPTIONAL})`;
  div.appendChild(span);

  const divTwo =  newComponent({
    type: "text",
    id,
    text: value ? value : null,
    classes : [CSS.TRANSITION_QUICK, CSS.CONTENT_EDITABLE, CSS.NO_FOCUS, CSS.FOCUS_COLOR_MINUS],
    styles: {
      fontSize: "15px",
      fontWeight: "400",
      padding : "10px",
      background: "transparent",
      borderBottom: `1px solid ${CSS.variable(COLORS.GRAYSON)}`,
      width: "100%",
      color: CSS.variable(COLORS.AON_BLUE),
    },
    attributes: {
      contentEditable : "",
      placeholder: placeholder || "...",
    }
  }).element;

  div.appendChild(divTwo);  

  return div.element;
}

const createTitle = (title) => newComponent({
  type: "text",
  text: title ? title : "",
  styles: {
    fontSize: "1.8em",
    fontWeight: "400",
    border: "none",
    width: "100%",
    color: CSS.variable(COLORS.AON_GRAY),
    paddingTop: "14px"
  }
}).element;


const createReceiverDiv = () => newComponent({
  type: TAG.DIV,
  classes: [
    CSS.FLEX_ROW,
    CSS.FLEX_ALIGN_CENTER,
    CSS.FLEX_JUSTIFY_START
  ],
  styles: {
    width: "100%"
  },
}).element;

// ----------------------------------------------------
// MESSAGE COMPONENT
// ----------------------------------------------------

const createMessageBox = (properties) =>{
  let component =  newComponent({
    type: MESSENGER_COMPONENTS.MESSAGE,
    classes : [CSS.FLEX_COLUMN, CSS.IMG_MAX_WIDTH],
    styles: {
        margin:"5px",
        padding: '15px',
        background : properties.direction == MESSENGER_DIRECTION.RIGHT ? "#f0fff0" : CSS.variable(COLORS.AON_WHITE),
        boxShadow : '0px 2px 6px rgba(0,0,0,.1)',
        borderRadius : '5px',
        position: "relative",
        width: '90%'
    },
    dataset:{
      id: properties.id,
      me: properties.direction == MESSENGER_DIRECTION.RIGHT ? true : false
    }
  }).element;
  if(properties.direction == MESSENGER_DIRECTION.RIGHT){
    component.style.marginLeft = "auto";
  } else {
    component.style.marginRight = "auto";
  }

  return component;
} 

const createMessageAuthor = (properties) =>{

  let author = newComponent({
    classes :[CSS.FLEX_ROW,CSS.FLEX_JUSTIFY_BETWEEN,CSS.FLEX_ALIGN_CENTER],
    styles: {
      textAlign : properties.direction == MESSENGER_DIRECTION.RIGHT ? MESSENGER_DIRECTION.RIGHT : MESSENGER_DIRECTION.LEFT,
      flexDirection : properties.direction == MESSENGER_DIRECTION.RIGHT ? "row-reverse" : "reverse",
    }
  });

  let span = setStyles(document.createElement(TAG.SPAN),{
    fontSize: "12px",
    fontWeight:" 600",
    color : CSS.variable(COLORS.GRAYSON),
    textOverflow: "ellipsis",
    overflow: "hidden",
    whiteSpace: "nowrap",
    zIndex: 1
  });
  if(properties.marginLeft) span.style.marginLeft = properties.marginLeft;
  span.innerText = properties.name;
  span.title = properties.name;
  author.appendChild(span);

  return author;
}

const createCommentContent = (properties) => newComponent({
  text: properties.comment,
  classes : [CSS.MESSAGE_CONTENT],
  styles: {
    fontSize: "1em",
    textAlign : "left",
    fontWeight : "400",
    color :  CSS.variable(COLORS.GRAYSON),
    paddingTop :"5px",
    wordWrap: "break-word"
  }
});

/**
 * 
 * @param {Object} icon 
 * @param {String} message 
 * @param {String} submessage optional submessage
 * @returns 
 */
const createAction = (icon, message, submessage, margin=true) => {
  const comp = createStartJustifiedRow();
  setStyles(comp.element,{
    width :"100%",
    padding:"5px 0",
    textAlign: "justify",
    flexWrap: "wrap"
  });

  const wrapper = newComponent({
  classes : [CSS.CENTER_FLEX],
   styles : {
    width : '20px',
    height : '20px',
    borderRadius : "100em",
    marginRight : margin ? "1em" : "0",
    background : CSS.variable(COLORS.AON_LIGHT_GRAY),
   }
  }).element;
  comp.element.appendChild(wrapper);

  let properties = {
    name :  icon.icon,
    color : icon.color,
    title: icon.title,
    size : "1.4em",
  };

  const image = icon.type === CONSTANT.MATERIAL_OUTLINED ? createOutlinedMaterialIcon(properties) : createMaterialIcon(properties);
  wrapper.appendChild(image);
  
  if(message){
    const text = createText({
      text : message,
      fontSize : "1.1em",
      fontWeight:400,
      color : CSS.variable(COLORS.GRAYSON)
    });
    text.element.style.flex = "1 0";
    text.appendTo(comp.element);
  } 

  if(submessage){
    const blockquote = newComponent({
      type:"blockquote",
      text:submessage,
      styles : {
        margin:"0px 0px 0px 5.8ex",
        borderLeft:"1px solid #cccccc",
        paddingLeft:"1ex",
        flex: "100%",
        fontWeight: 500,
        color:CSS.variable(COLORS.ONLINE_GREEN)
      }
    });
    blockquote.appendTo(comp.element);
  }

  return comp;
}

/**
 * Create a row with elements inside aligned to the start 
 * @param {object} styles 
 * @returns 
 */
const createStartJustifiedRow = (styles) => newComponent({
  classes: [CSS.FLEX_ROW, CSS.FLEX_JUSTIFY_START, CSS.FLEX_ALIGN_CENTER],
  styles: styles
});

const createStartJustifiedColumn = () => newComponent({
  classes: [CSS.FLEX_COLUMN, CSS.FLEX_JUSTIFY_START, CSS.FLEX_ALIGN_CENTER],
  styles: {
    width : "100%", 
    marginBottom: "5px"
  }
});

/**
 * Create a text 
 * @param {object} properties 
 * @returns 
 */
const createText = (properties) => newComponent({
  ...properties,
  text: properties.text,
  styles: {
      color: properties.color,
      fontSize: properties.fontSize ? properties.fontSize : "1em",
      fontFamily: properties.fontFamily ? properties.fontFamily : "Roboto",
      fontWeight: properties.fontWeight ? properties.fontWeight : "500",
  }
});

/**
 * Creates a material icon
 * @param {object} properties 
 * @returns 
 */
const createMaterialIcon = (properties) => newComponent({
  type: 'i',
  text: properties.name,
  classes: [CONSTANT.MATERIAL_ICONS],
  styles: {
      fontSize: properties.size,
      color: properties.color
  },
  attributes:{
    title: properties.title || properties.name 
  }
}).element;

/**
 * Creates a material icon
 * @param {object} properties 
 * @returns 
 */
const createOutlinedMaterialIcon = (properties) =>  newComponent({
    type: 'i',
    text: properties.name,
    classes: [CONSTANT.MATERIAL_ICONS_OUTLINED],
    styles: {
        fontSize: properties.size ? properties.size : "24px",
        color: properties.color ? properties.color : "#404040"
    },
    attributes:{
      title: properties.title || properties.name,
    }
}).element;


/**
 * Check the properties of the comment
 * AVOID showing null or undefined in UI.
 * @param {*} properties 
 * @returns Valid properties object.
 */

const checkProperties = (properties) => {
    if (!properties.name)
        properties.name = ""

    if (!properties.direction || (properties.direction != MESSENGER_DIRECTION.RIGHT && properties.direction != MESSENGER_DIRECTION.LEFT))
        properties.direction = MESSENGER_DIRECTION.LEFT;

    if (!properties.comment)
        properties.comment = ""

    if (!properties.attach)
        properties.attach = [];

    if (!properties.date)
        properties.date = "";

    return properties;
}

//----------------TYPE REQUEST CAU 
const createSelectCau = (name, id, title) => setAttributes( new AonSelect(),{
  name,
  id,
  title,
});

//----------------TYPE REQUEST   
const createRequestType = () =>setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.SOURCE_TASK,
  name: MESSENGER_IDS.SOURCE_TASK,
  title: MSG.TYPE_REQUEST
});

//----------------WORKGROUP   
const createWorkgroup = () =>setAttributes( new AonSelect(),{
    id: MESSENGER_IDS.WORKGROUP,
    name: MESSENGER_IDS.WORKGROUP,
    title: MSG.WORKGROUP,
    autocomplete:true
});

//----------------PROCESS
const createProcessType = () =>setAttributes( new AonSelect(),{
    id: MESSENGER_IDS.PROCESS_TYPE,
    name: MESSENGER_IDS.PROCESS_TYPE,
    title: MSG.PROCESS_TYPE
});

 //-----------------TASK HOLDER
 const createTaskHolder = () => setAttributes( new AonSelect(),{
    id: MESSENGER_IDS.TASKHOLDER,
    name: MESSENGER_IDS.TASKHOLDER,
    title: "Asignar a", // TODO
    autocomplete:true
});

 //-----------------TAG
 const createTaskTag = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.TASKTAG,
  name: MESSENGER_IDS.TASKTAG,
  title: MSG.TAG
});

 //-----------------CUSTOMER
 const createCustomer = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.CUSTOMER_TASK,
  name: MESSENGER_IDS.CUSTOMER_TASK,
  title: MSG.CUSTOMER,
  autocomplete: CONSTANT.OFF,
  readonly: CONSTANT.FALSE
});

 //-----------------PROJECT
 const createProject = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.PROJECT_TASK,
  name: MESSENGER_IDS.PROJECT_TASK,
  title: "Receptor", // TODO
  autocomplete: CONSTANT.OFF
});

 //-----------------ADVISORY
 const createAdvisory = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.ADVISORY_TASK,
  name: MESSENGER_IDS.ADVISORY_TASK,
  title: "Asesoria", // TODO
  autocomplete: CONSTANT.OFF
});

//-------------TEXT AREA COMMENT
const createAonTextArea = (placeholder) =>  setAttributes(new AonTextArea(),{
    name:MESSENGER_IDS.COMMENT_TASK,
    placeholder: placeholder || MSG.COMMENT+"..."
});

const iconComment = (icon_name) => {
    const a = setStyles(document.createElement(TAG.A),{
        boxShadow: "none",
        margin: "5px",
        marginTop: "auto",
        marginBottom: "auto",
        visibility: "visible",
        float: "right",
        background: "transparent",
        cursor: "pointer"
    });

    let icon = setStyles(document.createElement("i"),{
        fontSize: "1.8em",
        lineHeight: "44px",
        color: CSS.variable(COLORS.AON_BLUE)
    });
    icon.className   = CONSTANT.MATERIAL_ICONS;
    icon.textContent = icon_name;
    a.appendChild(icon);

    return a;
}

/**
 * 
 * @param {HTMLElement} message  
 * @param {Boolean} messageSend 
 * @param {Boolean} iconSendMail
 * @param {Object} properties 
 */
const createIconMessage = (message, messageSend, iconSendMail, properties) => {
  const {me, notification_date, date, task} = properties;

  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
  
  if(messageSend || me){
    let color = COLORS.AON_BLUE;

    let iconSend = undefined;
    
    if(iconSendMail){
      iconSend = createOutlinedMaterialIcon({name: messageSend ? MATERIAL_ICONS.MARK_EMAIL_READ : MATERIAL_ICONS.FORWARD_TO_INBOX});
      message.appendChild(iconSend);
  
      iconSend.title = messageSend ? "Enviado "+AonDateUtils.setDateTimestampDay(new Date(notification_date)) : `${MSG.SEND} por ${MSG.EMAIL}`;
      iconSend.id = MESSENGER_IDS.ICON_SEND_WORKFLOW;

      if(messageSend){
        color = me ? COLORS.ONLINE_GREEN : COLORS.AON_BLACK;
        message.classList.add(CSS.MESSAGE_AFTER, me ? "colorMe" : "colorOther");
      } 

      setStyles(iconSend, { color: CSS.variable(color), fontSize: "17px", position:"absolute", top: "14px", zIndex: "1" });

      if(me){
        setStyles(iconSend, { right: "17px", cursor: "pointer" });
        iconSend.addEventListener(EVENT.CLICK, async()=> {
          if(aonMessengerChat) 
            aonMessengerChat.sendMessageHistoric(task, parseInt(message.dataset.id), true);
        });
      } 
    }

    //-------------------icon edit
    if(!messageSend && me && date){
      const iconEdit = createOutlinedMaterialIcon({name:MATERIAL_ICONS.EDIT});
      iconEdit.id = MESSENGER_IDS.ICON_EDIT_WORKFLOW;
      message.appendChild(iconEdit);
      iconEdit.title = MSG.EDIT;
      setStyles(iconEdit, { color: CSS.variable(COLORS.AON_BLUE), fontSize: "17px", position:"absolute", top: "14px", zIndex: "1" , right: "17px", cursor: "pointer" });
      iconEdit.addEventListener(EVENT.CLICK, ()=> {
        TaskUtils.setContentMessageChat(task, parseInt(message.dataset.id))
      });

      if(iconSend){
        iconSend.style.right = "41px";
      }
        
    }
    //-------------------icon edit
  }
}

/**
 * Create a new message
 * @param {*} properties 
 * @returns 
 */
 const createMessageOpen = (properties, chat) => {
  properties = checkProperties(properties);

  const message = createMessageBox(properties);
  message.style.width = "100%";
  message.style.background = "#f5f5f5";

  chat.appendChild(message); //ADD MESSAGE IN DIV CHA

  createIconMessage(message, false, false, properties);
 
  const description = createCommentContent(properties);
  description.appendTo(message);

  TaskUtils.checkFilesAddEventClick({id:properties.task}, message); //ADD EVENT CLICK

  return message;
}

/**
 * Create a new message
 * @param {*} properties 
 * @returns 
 */
 const createChatMessageNew = (properties, chat) => {
  properties = checkProperties(properties);

  let parentElement = chat;

  if(properties.number){
    const action = createAction({
      icon:MATERIAL_ICONS.FORK_LEFT,
      color: CSS.variable(COLORS.MATERIAL_BLUE),
      type: CONSTANT.MATERIAL_OUTLINED,
      title: properties.number || ""
    }, undefined, undefined, false);
  
    action.appendTo(parentElement);

    parentElement = action.element;
  }

  const message = createMessageBox(properties);
  parentElement.appendChild(message); //ADD MESSAGE IN DIV CHAT

  const messageSend = properties.notification_user; // si el mensaje fue enviado
 
  createIconMessage(message, messageSend, properties.isSend, properties);

  if(!properties.me){
    properties.marginLeft = "20px";
  }

  const name = createMessageAuthor(properties);
  name.appendTo(message);

  const description = createCommentContent(properties);
  description.appendTo(message);

  const date = createText({
      text: AonDateUtils.setDateTimestampDay(new Date(properties.date)),
      color: CSS.variable(COLORS.AON_GRAY),
      fontSize : "11px",
      classes: [CSS.FIRST_LETTER_UPPER]
  });
  date.appendTo(name.element);
  
  TaskUtils.checkFilesAddEventClick({id: properties.task},message); //ADD EVENT CLICK

  TaskUtils.downChat();

  return message;
}

/**
 * 
 * @param {HTMLElement} div div append
 * @returns Object divs
 */
const createSectionComment = (div) => {

    const divWrite = setStyles(document.createElement(TAG.DIV),{ width: "100%", display: "flex", flexDirection: "column" });
    div.appendChild(divWrite);

    const divComment = setStyles(document.createElement(TAG.DIV),{ display: "flex", minHeight: "57px"});
    divComment.classList.add(CSS.RESIZE_VERTICAL);
    divComment.title = MSG.COMMENT;
    divWrite.appendChild(divComment);
  
    const divMain = setStyles(document.createElement(TAG.DIV),{
      width: "100%",
      display: "flex",
      flexDirection: "row-reverse",
      overflow: "hidden",
    });
    divMain.classList.add(CSS.FOCUS_COLOR_MINUS);
    divComment.appendChild(divMain);
  
    const iconSend = iconComment(MATERIAL_ICONS.SEND);
    iconSend.id = MESSENGER_IDS.BTN_SEND_MESSAGE;
    iconSend.title = `Ctrl+Enter (${MSG.SEND})`;
    divComment.appendChild(iconSend);
  
    const iconOpenFull = iconComment(MATERIAL_ICONS.OPEN_IN_FULL); 
    iconOpenFull.title = `Ctrl+X (${MSG.MAXIMIZE})`;
    divMain.appendChild(iconOpenFull);
  
    const aonTextArea = setStyles(createAonTextArea(`${MSG.WRITE_A_COMMENT}...`), {
      position: "relative",
      margin: "5px 0 5px 5px",
      color: "#4b4b4b",
      border: "none",
      outline: "none",
      width: "82%",
      resize: "none",
      fontSize: "15px",
      fontWeight: "400",
      maxHeight:"200px",
      boxShadow: "none",
      height: "auto !important",
      overflow:"hidden",
      flex: 1
    });
    
    aonTextArea.id = MESSENGER_IDS.COMMENT_TASK;
    divMain.appendChild(aonTextArea);
    aonTextArea.height = "45px";
    aonTextArea.removeToolbar();
    aonTextArea.draggableEnable(); 
    aonTextArea.removeBackground();

    return {
        divWrite,
        divComment,
        aonTextArea,
        iconSend,
        iconOpenFull,
    }
  }

const createChat = () => newComponent({
    type: MESSENGER_COMPONENTS.CHAT,
    id: MESSENGER_IDS.MESSENGER_CHAT,
    classes: ["continueLined", CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
    styles: {
        position: "relative",
        width: '100%',
        zIndex: "0",
        minHeight: '311px',
        padding: "15px",
        overflow: 'auto',
        borderBottom: '1px solid #f0f0f0',
        "scroll-behavior": "smooth",
    }
}).element;


/**
 * 
 * @param {String} id 
 * @param {String} title 
 * @returns 
 */
const createCardMessenger = (id, title) =>{
  const aonCard = new AonCard();
  aonCard.title = title;
  aonCard.id = id;
  aonCard.style.width = "100%";
  return aonCard;
}

const createInputContact = () => setAttributes(new AonInput(),{
  name:MESSENGER_IDS.GTASK_ID_TASK,
  id: MESSENGER_IDS.GTASK_ID_TASK,
  description: `${MSG.CONTACT} (${MSG.OPTIONAL})`
});

const createInputTitle = () => setAttributes(new AonInput(),{
  name:MESSENGER_IDS.TITLE_TASK,
  id: MESSENGER_IDS.TITLE_TASK,
  description: MSG.ISSUE
});


const createLabelFileText = () => {
  const label = setStyles(document.createElement(TAG.LABEL),{ color:"grey",  cursor:"pointer", width:"100%", borderTop :"1px dotted grey"});
  const span  = setStyles(document.createElement(TAG.SPAN),{ margin:"0 5px"});
  span.innerHTML = MSG.ATTACH_FILES_DRAGGING_DROPPING;
  label.appendChild(span);
  return label;
}

const createAonSwitch = (title) => {
  let btn = new AonSwitch();
  btn.id = MESSENGER_IDS.EXTERNAL_TASK;
  btn.title = title;
  return btn;
}

const createNoMessage = ()=>  newComponent({
  type : MESSENGER_COMPONENTS.ADVICE,
  id : MESSENGER_IDS.NO_MESSAGES,
  text : 'No hay mensajes en esta solicitud',
  styles : {
    fontSize : '1em',
    color : CSS.variable(COLORS.GRAYSON),
  }
});

/**
 * 
 * @param {Tag} tag 
 * @param {HTMLElement} parent div for append 
 * @param {Function} fn click
 * @returns 
 */
const appendTaskTag = ( tag, parent, fn) =>{
  
  const divOne = createTagHtml(tag, parent);
  
  const divTwo = setStyles(document.createElement(TAG.DIV),{ display:"inline-block", verticalAlign:"bottom", cursor:"pointer"});
  divTwo.title = MSG.DELETE_TAG;
  divTwo.addEventListener(EVENT.CLICK,()=>{
    divOne.remove();
    fn(tag.id);
  });
  divOne.appendChild(divTwo);

  const icon = setStyles(document.createElement(TAG.I),{ fontSize:"15px" });
  icon.className = CONSTANT.MATERIAL_ICONS;
  icon.innerText = MATERIAL_ICONS.CLOSE;
  divTwo.appendChild(icon);

  return divTwo;
}

const createTagHtml = (tag, parent) => {
  const tagName = tag.name;
  const tagColor = tag.color;
  
  const color = CSS.variable(tagColor ? COLORS.AON_WHITE : COLORS.GRAYSON);
  let background = tagColor || "dddddd";
  background = background.includes("#") ? background : "#"+background;
    
  const divOne = setStyles(document.createElement(TAG.DIV),{ whiteSpace:"nowrap", borderRadius:"10px", padding:"3px 7px", margin:"5px", fontWeight:"550", color, background});
  divOne.title = tagName;
  divOne.dataset.taskTag = JSON.stringify(tag);
  parent.appendChild(divOne);

  const divTwo = setStyles(document.createElement(TAG.DIV),{ display:"inline-block" });
  divTwo.innerText = tagName;
  divOne.appendChild(divTwo);

  return divOne;
}

const openDialogBranch = (task)=> {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
  const application = aonMessengerChat.getApplication();

  const dialog = application.getDialog();

  if (!application.isMobile()) 
    dialog.width = '40%';

  dialog.clear();
  dialog.setTitle("Crear rama");
    

  const div = document.createElement(TAG.DIV);
  dialog.setContent(div);

  //---FORM------
  // const emailId  = "sendHistoricEmail";
  // const email = setAttributes(new AonInput(),{ name:emailId, id: emailId, description: MSG.EMAIL });
  // div.appendChild(email);

  const workgroup  = createSelectCau(MSG.WORKGROUP, MSG.WORKGROUP+"Random", MSG.WORKGROUP);
  div.appendChild(workgroup);
  aonMessengerChat.getWorkgroup().then(options=>{
    workgroup.setOptions(options);
  })

  const taskHolder = createSelectCau("taskHolderSendRandom", "taskHolderSendRandom", "Asignar a");
  div.appendChild(taskHolder);

  const myTaskHolder =  aonMessengerChat.MY_TASKHOLDER;

  aonMessengerChat.getTaskHolderByWorkgroup(myTaskHolder, {})
  .then(options=>{
    taskHolder.setOptions(options);
  });

  workgroup.addEventListener(EVENT.CHANGE, async ({detail})=>{
    taskHolder.clear();
    if(detail){
      aonMessengerChat.getTaskHolderByWorkgroup(myTaskHolder, {workgroup:workgroup.value})
      .then(options=>{
        taskHolder.clear();
        taskHolder.setOptions(options);
      });
    }
   });

  const noteId = "sendHistoricId";
  const note = createAonTextArea(`${MSG.WRITE_A_COMMENT} (Opcional)...`);
  note.id = noteId;
  note.name = noteId;
  div.appendChild(note);
  note.height = "100px";

  dialog.addSendAction(async()=>{
    if(taskHolder.value && workgroup.value){
      application.startLoading();
      try {
        const domain = task.domain;

        let params = {
          ...task.getWorkflowTmp(),
          type: WORKFLOW_TYPES.CONNECTED, 
          workgroup:workgroup.getDetail(), 
          task_holder_receiver:taskHolder.getDetail(), 
          comment: note.value
        };

        if(domain){
          params.domainId = domain.id;
          params.domainName = domain.name;
        }

        await saveTaskBranch(params);
        aonMessengerChat.showMessage(`Rama creada!`);
        
        let param = { id: task.id };
    
        if(domain){
          param.domainId = domain.id;
          param.domainName = domain.name;
        }

        const data = await getTaskOne(param);
        let element = new AonMessengerChat();
        element.data = data;

    		application.setContent(element);

        dialog.close();

        aonMessengerChat.applicationParentEl.updateCount();

      } catch (error) {
        console.log(error);
      }
      application.stopLoading();
    }
  }, MSG.CREATE)

  dialog.open();
}

const createSimpleList = (title, id, parent) => {

  let div = setStyles(document.createElement(TAG.DIV),{
    borderBottom: '1px solid #ddd',
    height: '40px',
    position: 'relative',
    backgroundColor:"##eeeeee "
  });

  let span = setStyles(document.createElement(TAG.SPAN),{
    position: 'absolute',
    margin: '16px',
    fontWeight: '500',
    color: 'rgb(95, 99, 104)',
    width: '100%'
  });
  span.innerHTML = title;

  div.appendChild(span);
  
  parent.appendChild(div);
  
  let simpleList = setStyles(new AonMessengerSimpleList(),{ width: "100%" });
  simpleList.id = id;

  parent.appendChild(simpleList);

  return simpleList;
}

// TODO
const openDialogDailyTracking = (task)=> {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
  const application = aonMessengerChat.getApplication();
  const myTaskHolder = aonMessengerChat.MY_TASKHOLDER;

  const dialog = application.getDialog();

  if (!application.isMobile()) {
    dialog.width = '40%';
  }

  dialog.clear();
  dialog.setTitle(`${MSG.ESTIMATED_TIME} (${MSG.OPTIONAL})`);

  dialog.open();

  getDailyTrackingByTask({task:task.id}).then(resp=>{
    const trackingTotal = resp.reduce((acc, obj)=> acc + obj.tracking_duration, 0);
    const lastJobType = resp.reduce((acc, obj)=> obj.job_type > acc ? obj.job_type : acc, 0);

    const form = document.createElement(TAG.FORM);
    form.onsubmit = () => false;
    dialog.setContent(form);

    if(trackingTotal){
      const div = setStyles(document.createElement(TAG.DIV),{
        marginLeft: "4px",
        color:CSS.variable(COLORS.GRAYSON)
      });
   
      div.innerHTML = `<p><b>${MSG.HOURS} empleadas</b>: ${TaskUtils.parseDoubleToTime(trackingTotal)}</p>`; // TODO
      form.appendChild(div);
    }
 
    const jobId = MESSENGER_IDS.JOB_TYPE;
    const jobType = createSelectCau(jobId, jobId, MSG.TYPE_JOB);
    form.appendChild(jobType);

    getJobType().then(opts=>{
      let options = sortBy(opts.map(op => ({...op, value:op.id, name:op.description })), 'description');
      jobType.setOptions(options);
      if(lastJobType) {
        jobType.value = lastJobType;
      }
    });

    const durationId = MESSENGER_IDS.DAILY_TRACKING;

    // const trackingDuration = setAttributes(new AonInput(),{
    //   name:durationId,
    //   id: durationId,
    //   type:"time",
    //   description: `${MSG.ESTIMATED_TIME} (${MSG.HOURS})`
    // });
    // form.appendChild(trackingDuration);

    const trackingDuration = setAttributes(new AonTime(),{
      name:durationId,
      id: durationId,
      max:"300:59",
      title: `${MSG.ESTIMATED_TIME} (${MSG.HOURS})`
    });
    form.appendChild(trackingDuration);

    const noteId = MESSENGER_IDS.COMMENT_DAILY_TRACKING;
    const note   = createAonTextArea(`${MSG.WRITE_A_COMMENT} (${MSG.OPTIONAL})...`);
    note.id = noteId;
    note.name = noteId;
    form.appendChild(note);
    note.height = "100px";

    dialog.addSendAction(async()=>{
      application.startLoading();

      const jobValue = jobType.value;
      const trackingValue = trackingDuration.value;
      let dailyTracking = undefined;

      if(jobValue && trackingValue){

        dailyTracking = new DailyTracking()
        .setDomain(task.domain)
        .setTask(task.id)
        .setTrackingDate(AonDateUtils.formatDateOrigin(new Date()))
        .setTaskHolder(myTaskHolder)
        .setJobType(jobValue)
        .setTrackingDuration(TaskUtils.parseTimeToDouble(trackingValue))
        .setComments(note.value)
        ;
      }

      await aonMessengerChat.updateTaskStatus(TASK_STATUS.FINISHED, null, dailyTracking); 

      dialog.close();
      
      application.stopLoading();
    }, MSG.ACCEPT);
  });
    
}


// create section rating
const createSectionRating = (parent, isMobile)=> {
  const div = setStyles(document.createElement(TAG.DIV),{
    display: 'flex',
    justifyContent: 'center',
    borderTop: `1px solid #ddd`,
    width:'100%',
  });
  parent.appendChild(div);

  if(isMobile){
    setStyles(div,{ position: "absolute", bottom  : "5px" });
  }

  const second = newComponent({
    type: MESSENGER_COMPONENTS.DIV,
    classes: [CSS.FLEX_ROW],
    styles: {
      maxWidth: "calc(100% - 130px)",
      padding: '1em 0',
      gap: '0 16px',
    }
  }).element;

  div.appendChild(second);
  return second;
}

const createIconEvaluation = (img, active = false) => {
  let label = setStyles(document.createElement(TAG.LABEL),{
    padding: "5px 3px",
    fontSize: "32px",
    opacity: "0.7",
    filter: "grayscale(1)",
    cursor: "pointer"
  });
  label.classList.add("rating");

  let i = setStyles(document.createElement(TAG.I),{
    backgroundImage: `url(${img})`,
    backgroundPosition: "center",
    backgroundRepeat: "no-repeat",
    backgroundSize: "contain",
    display: "inline-block",
    verticalAlign: "middle",
    height: "1em",
    width: "1em",
  });

  label.appendChild(i);

  if(active){
    setStyles(label,{ filter:"grayscale(0)", opacity:"1", transform:"scale(1.1)" });
  }

  return label;
}

export const TaskCreationUtils = {
  createDivGrid,
  createDivGridBefore,
  createBtnAccept,
  createMainView,
  createMobileMainView,
  createDivEditable,
  createTitle,
  createReceiverDiv,
  createAction,
  createStartJustifiedRow,
  createStartJustifiedColumn,
  createOutlinedMaterialIcon,
  createSelectCau,
  createRequestType,
  createWorkgroup,
  createProcessType,
  createTaskHolder,
  createTaskTag,
  createCustomer,
  createProject,
  createAdvisory,
  createAonTextArea,
  createMessageOpen,
  createChatMessageNew,
  createSectionComment,
  createChat,
  createCardMessenger,
  createInputContact,
  createInputTitle,
  createLabelFileText,
  createAonSwitch,
  createNoMessage,
  appendTaskTag,
  createTagHtml,
  openDialogBranch,
  openDialogDailyTracking,
  createSectionRating,
  createIconEvaluation,
  createSimpleList
};
