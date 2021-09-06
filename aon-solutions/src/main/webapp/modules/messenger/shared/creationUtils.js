import { AonCard } from "../../../components/aon-card.js";
import { AonInput } from "../../../components/aon-input.js";
import { AonSelect } from "../../../components/aon-select.js";
import { AonTextArea } from "../../../components/aon-textarea.js";
import { CSS, MSG, TAG, COLORS, MATERIAL_ICONS } from "../../../environments/environments.js";
import { newComponent, setAttributes, setDateTimestampDay, setStyles } from "../../../services/utils.js";
import { ICON_TYPES, MESSENGER_COMPONENTS, MESSENGER_IDS } from "../MessengerEnums.js";
import { checkFilesAddEventClick } from "./utils.js";

const fontColor = CSS.variable(COLORS.GRAYSON);

export const RIGHT = "RIGHT";
export const LEFT = "LEFT";

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
    id: MESSENGER_IDS.MAIN_DIV,
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
  id: MESSENGER_IDS.MAIN_DIV,
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

// ----------------------------------------------------
// TITLE AND INPUTS IN WRITTER SECTION IN DESKTOP VIEW
// ----------------------------------------------------
export const createTitleDiv = () => newComponent({
  type: TAG.DIV,
  classes : [CSS.FLEX_ROW,CSS.FLEX_ALIGN_CENTER],
  styles: {
    paddingTop: "10px",
    paddingBottom: "10px",
    width: "100%",
    // maxWidth: "600px",
  },
});

export const createDivEditable = (title, id, placeholder) => newComponent({
  type: "text",
  id,
  text: title ? title : null,
  classes : [CSS.TRANSITION_QUICK, CSS.CONTENT_EDITABLE, CSS.NO_FOCUS, CSS.FOCUS_COLOR_MINUS],
  styles: {
    fontSize: "15px",
    fontWeight: "400",
    padding : "10px",
    background: "transparent",
    border: "none",
    width: "100%",
    color: CSS.variable(COLORS.AON_BLUE),
  },
  attributes: {
    contentEditable : "",
    placeholder: placeholder || "...",
  }
}).element;

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

export const createEditIcon = () => newComponent({
  type: "i",
  text: "edit",
  classes: [ICON_TYPES.MATERIAL_ICONS],
  styles: {
    fontSize: "1.5em",
    color: fontColor,
    cursor: "pointer",
  },
});

export const createReceiverDiv = () => newComponent({
  type: TAG.DIV,
  classes: [
    CSS.FLEX_ROW,
    CSS.FLEX_ALIGN_CENTER,
    CSS.FLEX_JUSTIFY_START
  ],
  styles: {
    width: "100%",
    // maxWidth: "600px"
  },
});

// ----------------------------------------------------
// ALL ATTACHMENTS SECTION IN DESKTOP VIEW
// ----------------------------------------------------

export const createAttachHistory = () => newComponent({
  type: 'attachHistory',
  classes: [CSS.FLEX_COLUMN],
  styles: {
    width: "100%",
    // maxWidth: "600px",
    marginTop: "25px",
    boxShadow: "0px 0px 2px rgba(0,0,0,.15)",
    border: "1px solid #E0E0E0",
    padding: "20px",
    paddingTop: "15px",
    paddingBottom: "15px"
  }
});

export const createAttachTitle = () => newComponent({
  type: TAG.DIV,
  classes:[
    CSS.FLEX_ROW,
    CSS.FLEX_JUSTIFY_BETWEEN,
    CSS.FLEX_ALIGN_CENTER  
  ],
  styles: {
    color: CSS.variable(COLORS.AON_GRAY),
    fontSize: "1.3em",
    fontWeight: "400",
  }
});

export const createAttachTitleText = () => newComponent({
  type: 'text',
  text: 'Archivos adjuntos',
  styles : {
    marginLeft : "10px"
  }
});


export const createExpandIcon = () => newComponent({
  type: "i",
  text: "expand_more",
  classes: [ICON_TYPES.MATERIAL_ICONS_OUTLINED],
  styles: {
    fontSize: "1.2em",
    color: "#c5c5c5",
    justifySelf: "flex-end",
    cursor: "pointer",
    paddingLeft: "15%",
  },
});


// ----------------------------------------------------
// MESSAGE COMPONENT
// ----------------------------------------------------

export const createMessageBox = (properties) =>{
  let component =  newComponent({
    type: MESSENGER_COMPONENTS.MESSAGE,
    classes : [CSS.FLEX_COLUMN],
    styles: {
        margin:"5px",
        padding: '20px',
        background : properties.direction == RIGHT ? "#f0fff0" : CSS.variable(COLORS.AON_WHITE),
        boxShadow : '0px 2px 6px rgba(0,0,0,.1)',
        borderRadius : '5px',
        // maxWidth: '500px',
        width: '90%'
    },
    dataset:{
      id: properties.id
    }
  });
  if(properties.direction == RIGHT)
    component.element.style.marginLeft = "auto";
  else 
    component.element.style.marginRight = "auto";
  return component;
} 

export const createMessageAuthor = (properties) => newComponent({
  text: `<span style="font-size: 14px;font-weight: 600;">${properties.name}</span>`,
  classes :[CSS.FLEX_ROW,CSS.FLEX_JUSTIFY_BETWEEN,CSS.FLEX_ALIGN_CENTER],
  styles: {
      textAlign : properties.direction == RIGHT ? "right" : "left",
      flexDirection : properties.direction == RIGHT ? "row-reverse" : "reverse",
      fontSize: "1.4em",
      fontWeight : "400", 
      color : CSS.variable(COLORS.GRAYSON)
  }
});

export const createCommentContent = (properties) => newComponent({
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


export const createAction = (icon, message, outlined) => {
  const comp = createStartJustifiedRow();
  setStyles(comp.element,{
    width :"100%",
    padding:"10px",
    paddingLeft :"calc(35px - .9em)",
    textAlign: "justify"
  })

  const wrapper = newComponent({
  classes : [CSS.CENTER_FLEX],
   styles : {
    width : '2em',
    height : '2em',
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

  const image = icon.type === ICON_TYPES.MATERIAL_OUTLINED ? createOutlinedMaterialIcon(properties) : createMaterialIcon(properties);
  const text = createText({
    text : message,
    fontSize : "1.1em",
    fontWeight:400,
    color : fontColor
  });
  
  image.appendTo(wrapper.element);
  wrapper.appendTo(comp.element);
  text.appendTo(comp.element);

  return comp;
}

/**
 * Create a row with elements inside aligned to the end 
 * @param {object} styles 
 * @returns 
 */
 export const createEndJustifiedRow = (styles) => newComponent({
    classes: [CSS.FLEX_ROW, CSS.FLEX_JUSTIFY_END, CSS.FLEX_ALIGN_CENTER],
    styles: styles
});

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

export const titleFirstDiv  = (title="") => {
    const div = createStartJustifiedColumn();
    let span = setStyles(document.createElement(TAG.SPAN),{
        fontSize: "0.9375rem",
        width:"100%",
        color:CSS.variable(COLORS.AON_COLOR_INK_MEDIUM_CONTRANST)
    });
    span.textContent = title;
    div.appendChild(span);

    return div.element;
}

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
    classes: [ICON_TYPES.MATERIAL_ICONS],
    styles: {
        fontSize: properties.size,
        color: properties.color
    }
});

/**
 * Creates a material icon
 * @param {object} properties 
 * @returns 
 */
 export const createOutlinedMaterialIcon = (properties) => newComponent({
    type: 'i',
    text: properties.name,
    classes: [ICON_TYPES.MATERIAL_ICONS_OUTLINED],
    styles: {
        fontSize: properties.size ? properties.size : "24px",
        color: properties.color? properties.color : "#404040"
    }
});



/**
 * Check the properties of the comment
 * AVOID showing null or undefined in UI.
 * @param {*} properties 
 * @returns Valid properties object.
 */

const checkProperties = (properties) => {
    if (!properties.name)
        properties.name = ""

    if (!properties.direction || (properties.direction != RIGHT && properties.direction != LEFT))
        properties.direction = LEFT;

    if (!properties.comment)
        properties.comment = ""

    if (!properties.attach)
        properties.attach = [];

    if (!properties.date)
        properties.date = "";

    return properties;
}

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

 //-----------------CUSTOMER
 export const createCustomer = () => setAttributes( new AonSelect(),{
  id: MESSENGER_IDS.CUSTOMER_TASK,
  name: MESSENGER_IDS.CUSTOMER_TASK,
  title: MSG.SENDER,
  autocomplete: "off",
  readonly: "false"
});


//-------------TEXT AREA COMMENT
export const createAonTextArea = (placeholder) =>  setAttributes(new AonTextArea(),{
    name:MESSENGER_IDS.COMMENT_TASK,
    placeholder: placeholder || MSG.COMMENT+"..."
});

const iconComment = (icon) => {
    const a = setStyles(document.createElement("a"),{
        boxShadow: "none",
        margin: "5px",
        marginTop: "auto",
        marginBottom: "auto",
        visibility: "visible",
        float: "right",
        background: "rgba(0, 0, 0, 0)",
        cursor: "pointer"
    });

    let iconSend = setStyles(document.createElement("i"),{
        fontSize: "1.8em",
        lineHeight: "44px",
        color: CSS.variable(COLORS.AON_BLUE)
    });
    iconSend.className   = ICON_TYPES.MATERIAL_ICONS;
    iconSend.textContent = icon;
    a.appendChild(iconSend);

    return a;
}


/**
 * Create a new message
 * @param {*} properties 
 * @returns 
 */
export const createChatMessage = (properties, chat) => {
    properties = checkProperties(properties);

    const message = createMessageBox(properties);

    // const label = setStyles(document.createElement("label"),{
    //     color: "grey",
    //     fontSize: "17px",
    //     textDecoration: "none",
    //     cursor: "pointer",
    //     textAlign: "right",
    // });
    // label.innerText="×";
    // message.appendChild(label);

    const name = createMessageAuthor(properties);
    name.appendTo(message.element);

    const description = createCommentContent(properties);
    description.appendTo(message.element);

    const date = createText({
        text: setDateTimestampDay(new Date(properties.date)),
        color: CSS.variable(COLORS.AON_GRAY),
        fontSize : "12px",//'0.6em',
        classes: [CSS.FIRST_LETTER_UPPER]
    });
    date.appendTo(name.element);

    message.appendTo(chat); //ADD MESSAGE IN DIV CHAT

    checkFilesAddEventClick(message.element); //ADD EVENT CLICK

    return message;
}

/**
 * 
 * @param {HTMLElement} div div append
 * @returns Object divs
 */
export const createSectionComment = (div) => {

    const divWrite =  setStyles(document.createElement(TAG.DIV),{
      width: "100%",
      display: "flex",
      flexDirection: "column"
    });
    div.appendChild(divWrite);

    const divComment = setStyles(document.createElement(TAG.DIV),{
      display: "flex",
      minHeight: "57px"
    });
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
    iconSend.title = MSG.SEND;
    divComment.appendChild(iconSend);
  
    const iconOpenFull = iconComment(MATERIAL_ICONS.OPEN_IN_FULL); 
    iconOpenFull.title = MSG.MAXIMIZE;
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
  const label =  setStyles(document.createElement(TAG.LABEL),{
    color:"grey",
    cursor:"pointer",
    width:"100%",
    borderTop :"1px dotted grey"
  })
  const span = document.createElement(TAG.SPAN);
  span.style.margin = "0 5px";
  span.innerHTML = "Adjunte archivos arrastrándolos y soltándolos, seleccionándolos o pegándolos.";
  label.appendChild(span);
  return label;
}

