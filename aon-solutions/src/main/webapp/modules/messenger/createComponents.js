import { COLORS, CSS, MSG, TAG } from "../../environments/environments.js";
import { newComponent } from "../../services/utils.js";
import { createMaterialIcon, createOutlinedMaterialIcon, createStartJustifiedRow, createText, RIGHT } from "./shared/creationUtils.js";
import { ICON_TYPES, MESSENGER_COMPONENTS, MESSENGER_IDS } from "./MessengerEnums.js";

const fontColor = CSS.variable(COLORS.GRAYSON);

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
      opacity: 0,
      marginTop: "0vh",
      padding: "0px",
      width: "100%",
      maxWidth: "1400px",
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
    opacity: 0,
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
    maxWidth: "600px",
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
    width: "100%",
    maxWidth: "600px"
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
        maxWidth: '500px',
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
      fontSize: "16px",//"1em",
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