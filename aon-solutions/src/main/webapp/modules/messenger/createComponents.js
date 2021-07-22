import { COLORS, CSS, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { newComponent } from "../../services/utils.js";
import { createMaterialIcon, createOutlinedMaterialIcon, createStartJustifiedRow, createText, RIGHT } from "./shared/creationUtils.js";
import { ICON_TYPES, MESSENGER_COMPONENTS, MESSENGER_IDS } from "./MessengerEnums.js";

const fontColor = CSS.variable(COLORS.GRAYSON);

// ----------------------------------------------------
// MAIN VIEW
// ----------------------------------------------------
export const createMainView = () => newComponent({
  type: TAG.DIV,
  id: MESSENGER_IDS.MAIN_DIV,
  classes: [CSS.FLEX_JUSTIFY_BETWEEN, CSS.NO_COPY],
  styles: {
    transition: ".5s",
    display: "flex",
    flexDirection: "row",
    opacity: 0,
    marginTop: "0vh",
    padding: "0px",
    width: "100%",
    maxWidth: "1400px",
    height: "100%",
    overflow: 'hidden'
  },
});

export const createMobileMainView = () => newComponent({
  type: TAG.DIV,
  id: MESSENGER_IDS.MAIN_DIV,
  classes: [CSS.FLEX_COLUMN, CSS.NO_COPY],
  styles: {
    transition: ".5s",
    opacity: 0,
    marginTop: "0vh",
    padding: "0px",
    width: "100%",
    height: "100%",
    overflow: 'hidden'
  },
});

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
    maxWidth: "600px",
  },
});

export const createDivEditable = (title, id, placeholder) => newComponent({
  type: "text",
  id,
  text: title ? title : null,
  classes : [CSS.TRANSITION_QUICK,CSS.CONTENT_EDITABLE, CSS.NO_FOCUS],
  styles: {
    fontSize: "1.5em",
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
  text: title == "" ? "Escriba titulo aquí" : title,
  styles: {
    fontSize: "1.8em",
    fontWeight: "400",
    border: "none",
    width: "100%",
    color: CSS.variable(COLORS.AON_GRAY),
    paddingTop: "10px"
  },
});

export const createEditIcon = () => newComponent({
  type: "i",
  text: "edit",
  classes: ["material-icons"],
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
    // marginTop: "15px",
    // marginBottom: "5px",
    width: "100%",
    maxWidth: "600px",
  },
});



// ----------------------------------------------------
// SEND BAR IN DESKTOP VIEW
// ----------------------------------------------------
export const createSendBar = () => newComponent({
  type: TAG.DIV,
  classes: [CSS.FLEX_ROW, CSS.NO_COPY, CSS.FLEX_JUSTIFY_START],
  styles: {
    maxWidth: "600px",
    width: "100%"
  },
});


const preventDefault = (ev) =>{
  ev.preventDefault();
  ev.stopPropagation();
}

export const createUpload = () => newComponent({
  type: TAG.DIV,
  classes: [CSS.FLEX_ROW, CSS.FLEX_JUSTIFY_END, CSS.FLEX_ALIGN_CENTER, CSS.DIV_DRAG_OVER],
  styles: {
    marginTop: "15px",
    padding: "0 10px",
    border:"2px dashed rgba(3,88,216,.3)",
    cursor: "pointer",
  },
  events:{
    dragenter: preventDefault,
    dragover:  preventDefault,
    dragleave: preventDefault,
    drop:      preventDefault
  }
});

export const createUploadIcon = () => newComponent({
  type: "i",
  text: MATERIAL_ICONS.FILE_UPLOAD,
  classes: ["material-icons"],
  styles: {
    fontSize: "2.5em",
    color: CSS.variable(COLORS.AON_BLUE),
    cursor: "pointer",
  },
});

export const createUploadText = () => newComponent({
  type: "span",
  text: "Suelta los archivos o haz clic para subirlos.",
  classes: [
    CSS.FLEX_ROW,
    CSS.FLEX_JUSTIFY_END,
    CSS.FLEX_ALIGN_CENTER
  ],
  styles: {
    fontSize: "1.2em",
    fontWeight: "300",
    minWidth: "150px",
    color: CSS.variable(COLORS.AON_BLUE),
    paddingLeft: "10px",
    height: "100%",
  },
});

export const createButtonWrapper = () => newComponent({
  type: TAG.DIV,
  classes: [
    CSS.FLEX_ROW,
    CSS.FLEX_JUSTIFY_END,
    CSS.FLEX_ALIGN_CENTER
  ],
  styles: {
    paddingTop: "15px",
    width: "100%",
  },
});


export const createSendButton = (text=null) => newComponent({
  type: TAG.BUTTON,
  text: text || MSG.SEND,
  id: MESSENGER_IDS.BUTTON_SEND,
  classes: [
    "materialButton",
    CSS.FLEX_ROW,
    CSS.FLEX_JUSTIFY_CENTER,
    CSS.FLEX_ALIGN_CENTER,
    CSS.NO_FOCUS
  ],
  styles: {
    transition: ".25s",
    padding: "13px",
    minWidth: "105px",
    minHeight: "35px",
    fontSize: "1.2em",
    background: CSS.variable(COLORS.AON_BLUE),
    boxShadow: "0px 2px 4px rgba(0,0,0,.15)",
    border: "none",
    margin: "10px",
    borderRadius: "1000px",
    color: "#fff",
  },
}).element;



export const createSendIcon = () => newComponent({
  type: "i",
  text: "send",
  classes: ["material-icons"],
  styles: {
    fontSize: "1.2em",
    color: "white",
    justifySelf: "flex-end",
    cursor: "pointer",
    paddingLeft: "15%",
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
    maxWidth: "600px",
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
  classes:
  [
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
  classes: ["material-icons-outlined"],
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

export const createMessageBox = (properties) => newComponent({
  type: MESSENGER_COMPONENTS.MESSAGE,
  classes : [CSS.FLEX_COLUMN],
  styles: {
      margin: '10px',
      padding: '20px',
      background : properties.direction == RIGHT ? CSS.variable(COLORS.AON_WHITE) : "#fafafa",
      boxShadow : '0px 2px 6px rgba(0,0,0,.1)',
      borderRadius : '5px',
      maxWidth: '500px',
      width: '96%',
     // overflow: 'hidden'
  },
  dataset:{
    id: properties.id
  }
});

export const createMessageAuthor = (properties) => newComponent({
  text: "<b>" + properties.name + "</b>",
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
  comp.element.style.width = "100%";
  comp.element.style.paddingLeft = "calc(30px - .9em)";
  comp.element.style.paddingTop = "10px";
  comp.element.style.paddingBottom = "10px";
  

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