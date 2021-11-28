import { AonCard } from "../../../components/aon-card.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonTextArea } from "../../../components/aon-textarea.js";
import { AonSwitch } from "../../../components/aon-switch.js";
import { CSS, MSG, TAG, COLORS, MATERIAL_ICONS, EVENT, CONSTANT } from "../../../environments/environments.js";
import { taskHistoricSend } from "../../../services/taskService.js";
import { newComponent, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { MESSENGER_COMPONENTS, MESSENGER_DIRECTION, MESSENGER_IDS, MESSENGER_VIEWS } from "../MessengerEnums.js";
import { checkFilesAddEventClick, downChat } from "./utils.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";


/**
 * 
 * @param {HTMLElement} parent appenchild
 * @param {HTMLElement} child element add Optional
 * @param {Object} properties 
 * @returns 
 */
 export const createDivGrid = (parent, child, properties)=> {

  const div = newComponent({ type: TAG.DIV, ...properties }).element;

  if(parent) parent.appendChild(div);

  if(child) div.appendChild(child);

  return div;
}

export const createBtnAccept = () => {
  let btnAccept = setStyles(document.createElement(TAG.BUTTON),{ margin:"15px 0 0 15px"});
  btnAccept.className = CSS.AON_BUTTON;
  btnAccept.textContent = "Procesar";
  return btnAccept;
}
// ----------------------------------------------------
// MAIN VIEW
// ----------------------------------------------------
export const createMainView = (aonMessengerChat) =>{
  const div = document.createElement(TAG.DIV);
  div.className = CSS.AON_SUB_CONTENT;
  div.style.width = "100%";
  aonMessengerChat.appendChild(div);
  const mainView = newComponent({
    type: TAG.DIV,
    classes: [CSS.FLEX_JUSTIFY_BETWEEN, CSS.NO_COPY],
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

export const createMobileMainView = () => newComponent({
  type: TAG.DIV,
  classes: [CSS.FLEX_COLUMN, CSS.NO_COPY],
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


export const createDivEditable = (parent, title, value, id, placeholder) => {
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

export const createTitle = (title) => newComponent({
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


export const createReceiverDiv = () => newComponent({
  type: TAG.DIV,
  classes: [
    CSS.FLEX_ROW,
    CSS.FLEX_ALIGN_CENTER,
    CSS.FLEX_JUSTIFY_START
  ],
  styles: {
    width: "100%"
  },
});


// ----------------------------------------------------
// MESSAGE COMPONENT
// ----------------------------------------------------

const createMessageBox = (properties) =>{
  let component =  newComponent({
    type: MESSENGER_COMPONENTS.MESSAGE,
    classes : [CSS.FLEX_COLUMN],
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
  if(properties.direction == MESSENGER_DIRECTION.RIGHT)
    component.style.marginLeft = "auto";
  else 
    component.style.marginRight = "auto";

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
export const createAction = (icon, message, submessage) => {
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
    marginRight : "1em",
    background : CSS.variable(COLORS.AON_LIGHT_GRAY),
   }
  });

  let properties = {
    name :  icon.icon,
    color : icon.color,
    size : "1.4em"
  };

  const image = icon.type === CONSTANT.MATERIAL_OUTLINED ? createOutlinedMaterialIcon(properties) : createMaterialIcon(properties);
  const text = createText({
    text : message,
    fontSize : "1.1em",
    fontWeight:400,
    color : CSS.variable(COLORS.GRAYSON)
  });
  text.element.style.flex = "1 0";
  
  image.appendTo(wrapper.element);
  wrapper.appendTo(comp.element);
  text.appendTo(comp.element);
  if(submessage){
    const blockquote = newComponent({
      type:"blockquote",
      text:submessage,
      styles : {
        margin:"0px 0px 0px 5.8ex",
        borderLeft:"1px solid rgb(204,204,204)",
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
export const createStartJustifiedRow = (styles) => newComponent({
    classes: [CSS.FLEX_ROW, CSS.FLEX_JUSTIFY_START, CSS.FLEX_ALIGN_CENTER],
    styles: styles
});

export const createStartJustifiedColumn = () =>newComponent({
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
      title: properties.name,
    }
});

/**
 * Creates a material icon
 * @param {object} properties 
 * @returns 
 */
 export const createOutlinedMaterialIcon = (properties) => {
  return newComponent({
      type: 'i',
      text: properties.name,
      classes: [CONSTANT.MATERIAL_ICONS_OUTLINED],
      styles: {
          fontSize: properties.size ? properties.size : "24px",
          color: properties.color ? properties.color : "#404040"
      },
      attributes:{
        title: properties.name,
      }
  });

 } 

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
export const createSelectCau = (name, id, title) => setAttributes( new AonSelect(),{
  name,
  id,
  title,
});

//----------------TYPE REQUEST   
export const createRequestType = () =>setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.SOURCE_TASK,
  name: MESSENGER_IDS.SOURCE_TASK,
  title: MSG.TYPE_REQUEST
});


//----------------WORKGROUP   
export const createWorkgroup = () =>setAttributes( new AonSelect(),{
    id: MESSENGER_IDS.WORKGROUP,
    name: MESSENGER_IDS.WORKGROUP,
    title: MSG.WORKGROUP
});

//----------------PROCESS
export const createProcessType = () =>setAttributes( new AonSelect(),{
    id: MESSENGER_IDS.PROCESS_TYPE,
    name: MESSENGER_IDS.PROCESS_TYPE,
    title: MSG.PROCESS_TYPE
});

 //-----------------TASK HOLDER
 export const createTaskHolder = () => setAttributes( new AonSelect(),{
    id: MESSENGER_IDS.TASKHOLDER,
    name: MESSENGER_IDS.TASKHOLDER,
    title: "Asignar a"
});

 //-----------------TAG
 export const createTaskTag = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.TASKTAG,
  name: MESSENGER_IDS.TASKTAG,
  title: MSG.TAG
});

 //-----------------CUSTOMER
 export const createCustomer = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.CUSTOMER_TASK,
  name: MESSENGER_IDS.CUSTOMER_TASK,
  title: MSG.ENTERPRISE,
  autocomplete: CONSTANT.OFF,
  readonly: CONSTANT.FALSE
});

 //-----------------PROJECT
 export const createProject = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.PROJECT_TASK,
  name: MESSENGER_IDS.PROJECT_TASK,
  title: "Receptor",
  autocomplete: CONSTANT.OFF
});

 //-----------------ADVISORY
 export const createAdvisory = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.ADVISORY_TASK,
  name: MESSENGER_IDS.ADVISORY_TASK,
  title: "Asesoria",
  autocomplete: CONSTANT.OFF
});


//-------------TEXT AREA COMMENT
export const createAonTextArea = (placeholder) =>  setAttributes(new AonTextArea(),{
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
        background: "rgba(0, 0, 0, 0)",
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
 * Create a new message
 * @param {*} properties 
 * @returns 
 */
export const createChatMessage = (properties, chat) => {
    properties = checkProperties(properties);

    let me = properties.direction === MESSENGER_DIRECTION.RIGHT;

    let messageSend = properties.notification_user; // si el mensaje fue enviado

    const message = createMessageBox(properties);
    chat.appendChild(message); //ADD MESSAGE IN DIV CHAT

    if(messageSend || me){
      const iconSendWorkflow = createOutlinedMaterialIcon({name: messageSend ? MATERIAL_ICONS.MARK_EMAIL_READ : MATERIAL_ICONS.FORWARD_TO_INBOX}).element;
      iconSendWorkflow.title = messageSend ? "Enviado "+AonDateUtils.setDateTimestampDay(new Date(properties.notification_date)) : `${MSG.SEND} por ${MSG.EMAIL}`;
      iconSendWorkflow.id = MESSENGER_IDS.ICON_SEND_WORKFLOW;
      let color = COLORS.AON_BLUE;

      if(messageSend){
        color = me ? COLORS.ONLINE_GREEN : COLORS.AON_BLACK;
        message.classList.add(CSS.MESSAGE_AFTER, me ? "colorMe" : "colorOther");
      } 
      
      setStyles(iconSendWorkflow, { color: CSS.variable(color), fontSize: "17px", position:"absolute", top: "14px", zIndex: 1 });

      if(me){
        setStyles(iconSendWorkflow, { right: "17px", cursor: "pointer" });
        iconSendWorkflow.addEventListener(EVENT.CLICK, async()=> sendHistoric(parseInt(message.dataset.id)));

        //-------------------icon share
        // const textShare = "Compartir entre ramas (En desarrollo)";
        // const iconShare = createOutlinedMaterialIcon({name:MATERIAL_ICONS.IOS_SHARE}).element;
        // iconShare.title = textShare;
        // setStyles(iconShare, { color: CSS.variable(color), fontSize: "17px", position:"absolute", top: "12px", zIndex: 1 , right: "39px", cursor: "pointer" });
        // iconShare.addEventListener(EVENT.CLICK, ()=> alert(textShare));
        // message.appendChild(iconShare);

         //-------------------icon delete
      } else {
        properties.marginLeft = "20px";
      }
      
      message.appendChild(iconSendWorkflow);
    }

    const name = createMessageAuthor(properties);
    name.appendTo(message);

    const description = createCommentContent(properties);
    description.appendTo(message);

    const date = createText({
        text: AonDateUtils.setDateTimestampDay(new Date(properties.date)),
        color: CSS.variable(COLORS.AON_GRAY),
        fontSize : "11px",//'0.6em',
        classes: [CSS.FIRST_LETTER_UPPER]
    });
    date.appendTo(name.element);
    

    checkFilesAddEventClick(message); //ADD EVENT CLICK

    downChat();

    return message;
}

/**
 * 
 * @param {HTMLElement} div div append
 * @returns Object divs
 */
export const createSectionComment = (div) => {

    const divWrite =  setStyles(document.createElement(TAG.DIV),{ width: "100%", display: "flex", flexDirection: "column" });
    div.appendChild(divWrite);

    const divComment = setStyles(document.createElement(TAG.DIV),{ display: "flex", minHeight: "57px"});
    divComment.classList.add(CSS.RESIZE_VERTICAL);
    divComment.title = MSG.COMMENT;
    divWrite.appendChild(divComment);
  
    const divMain  = setStyles(document.createElement(TAG.DIV),{
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

export const createChat = () => newComponent({
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
export const createCardMessenger = (id, title) =>{
  const aonCard = new AonCard();
  aonCard.title = title;
  aonCard.id = id;
  aonCard.style.width = "100%";
  return aonCard;
}

export const createInputContact = () =>  setAttributes(new AonInput(),{
  name:MESSENGER_IDS.GTASK_ID_TASK,
  id: MESSENGER_IDS.GTASK_ID_TASK,
  description: MSG.CONTACT + ` (${MSG.OPTIONAL})`
});

export const createLabelFileText = () => {
  const label = setStyles(document.createElement(TAG.LABEL),{ color:"grey",  cursor:"pointer", width:"100%", borderTop :"1px dotted grey"});
  const span  = setStyles(document.createElement(TAG.SPAN),{ margin:"0 5px"});
  span.innerHTML = MSG.ATTACH_FILES_DRAGGING_DROPPING;
  label.appendChild(span);
  return label;
}

export const createAonSwitch = (title) => {
  let btn = new AonSwitch();
  btn.id = MESSENGER_IDS.EXTERNAL_TASK;
  btn.title = title;
  return btn;
}

export const createNoMessage = ()=>  newComponent({
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
 * @param {Number} workflowId taskworkflow id 
 */
const sendHistoric = async (workflowId) => {
  const aonMessengerChat = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
  const workflows  = await taskHistoricSend({...aonMessengerChat.task, workflowId});
  if(workflows && workflows.length) {
    for (const workflow of workflows) {
      const message = document.querySelector( `#${MESSENGER_IDS.MESSENGER_CHAT} ${MESSENGER_COMPONENTS.MESSAGE}[data-id='${workflow.id}']`);
      if(message){
        //CHANGE STYLE IF SEND MESSAGE
        message.classList.add(CSS.MESSAGE_AFTER, "colorMe");
        const iconSendWorkflow = message.querySelector(`#${MESSENGER_IDS.ICON_SEND_WORKFLOW}`);
        if(iconSendWorkflow){
          iconSendWorkflow.title = "Enviado "+AonDateUtils.setDateTimestampDay(workflow.notification_date)
          iconSendWorkflow.innerText =  MATERIAL_ICONS.MARK_EMAIL_READ;
          iconSendWorkflow.style.color = CSS.variable(COLORS.ONLINE_GREEN);
        }
      }
    }
  }
}

/**
 * 
 * @param {Tag} tag 
 * @param {HTMLElement} parent div for append 
 * @param {Function} fn click
 * @returns 
 */
export const appendTaskTag = ( tag, parent, fn) =>{
  const divOne = setStyles(document.createElement(TAG.DIV),{ 
    whiteSpace: "nowrap",
    borderRadius: "4px",
    padding: "0 4px",
    backgroundColor: "rgb(221, 221, 221)",
    color: "rgb(102, 102, 102)",
    margin: "5px",
    fontWeight: "450" 
  });
  divOne.dataset.taskTag = tag.id;
  parent.appendChild(divOne);

  const divTwo = setStyles(document.createElement(TAG.DIV),{ display: "inline-block"});
  divTwo.innerText = tag.name;
  divOne.appendChild(divTwo);

  const divThree = setStyles(document.createElement(TAG.DIV),{  display: "inline-block", verticalAlign:"bottom", cursor:"pointer"});
  divThree.title = MSG.DELETE_TAG;
  divThree.addEventListener(EVENT.CLICK,()=> fn(tag.id));
  divOne.appendChild(divThree);

  const i = setStyles(document.createElement("i"),{ fontSize: "15px" });
  i.className = CONSTANT.MATERIAL_ICONS;
  i.innerText = MATERIAL_ICONS.CLOSE;
  divThree.appendChild(i);

  return divThree;
}
