import { AonCard } from "../../components/aon-card.js";
import { AonDate } from "../../components/aon-date.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { AonInput } from "../../components/aon-input.js";
import { AonNumber } from "../../components/aon-number.js";
import { AonSelect } from "../../components/aon-select.js";
import { AonToolbar } from "../../components/aon-toolbar.js";
import { CSS, TAG } from "../../environments/environments.js";
import { createDiv, createSpan, newComponent, setAttributes, setClasses, setEvents } from "../../services/utilsComponents.js";

export const createUl = (id) => newComponent({
  id,
  type: TAG.UL,
  classes: [CSS.AON_UL],
  styles:{
    listStyle: "none",
    padding: 0,
    paddingTop: "5px",
    margin: 0
  }
});

export const createLi = (dataset) => newComponent({
    dataset,
    type: TAG.LI,
    styles:{
      width: "100%",
      position: "relative",
      transition: "background-color 1s",
      userSelect: "none"
    },
  });

export const createContent = (text) => createSpan({
    text,
    styles:{
      fontSize: "14px",
      wordWrap: "break-word"
    }
}); 

export const createTitle = (text) => createSpan({
  text,
  classes:[CSS.AON_COLOR_PRIMARY]
}); 

export const createDivFooter = () => createDiv({
  styles:{
    display: "flex",
    marginTop: "10px",
    fontWeight: "800",
    fontSize: "10px"
  }
})

export const createDivFooter1 = (text) => createDiv({
  text,
  styles:{
    marginLeft: "auto",
  }
})

export const createAonNotification = (id) =>createDiv({
  id,
  styles:{
    margin: "auto",
    width: "80%",
  }
});

export const createButtonClose = () => newComponent({
  type: TAG.LABEL,
  text: "×",
  styles:{
    float: "right",
    marginTop: "-23px",
    marginRight: "-19px",
    cursor: "pointer",
    padding: "10px",
  },
});

export const createSpanFloat = () => createSpan({
  id: "aonNotificationFloatSpan",
  styles:{
    position: "fixed",
    right: "6%",
    bottom: "70px"
  }
});

export const createForm = (id="form") => newComponent({
  type:TAG.FORM,
  id,
  attributes:{
    action: "#"
  },
  events:{
    submit: (ev)=>  ev.preventDefault()
  }
});

export const createBadge = (id) => createSpan({
  id,
  styles:{
    position: "absolute", 
    right: "8px",
    top: "5px",
    padding: "4px",
    borderRadius: "50%",
    background: "rgb(220, 77, 48)",
    color: "white",
    fontSize: "10px",
    fontWeight: 800,
  }
});

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
export const createSelect = ({attributes, events}, parent) => {
  
  if(attributes.options && typeof attributes.options !== "string") {
    attributes.options = JSON.stringify(attributes.options);
  }

  let select = setAttributes(new AonSelect(), attributes);

  setClasses(select,[CSS.TRANSITION_CASCADE]);

  if(events) setEvents(select, events);
  
  parent.appendChild(select);

  return select;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
export const createInput = ({attributes, events}, parent) => {
  let input = setAttributes(new AonInput(), attributes);
  if(events) setEvents(input, events);
  parent.appendChild(input);
  return input;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
 export const createNumber = ({attributes, events}, parent) => {
  let input = setAttributes(new AonNumber(), attributes);
  if(events) setEvents(input, events);
  parent.appendChild(input);
  return input;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
export const createIconButton = ({attributes, events}, parent) => {
  let icon = setAttributes(new AonIconButton(), attributes);
  if(events) setEvents(icon, events);
  parent.appendChild(icon);
  return icon;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
 export const createDate = ({attributes, events}, parent) => {
  let date = setAttributes( new AonDate(), attributes);
  if(events) setEvents(date, events);
  parent.appendChild(date);
  return date;
}

export const createCard = (attributes, parent) => {
  const aonCard = setAttributes(new AonCard(), {
      ...attributes,
      flex:"true"
  });
  parent.appendChild(aonCard);
  return aonCard;
}


export const createToolbar= (attributes, parent) => {
  let toolbar = setAttributes( new AonToolbar(), attributes);
  parent.appendChild(toolbar);
  return toolbar;
}