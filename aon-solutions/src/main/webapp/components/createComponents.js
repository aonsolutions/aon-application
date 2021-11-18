import { AonCard } from "./aon-card.js";
import { AonDate } from "./aon-date.js";
import { AonIconButton } from "./aon-icon-button.js";
import { AonInput } from "./aon-input.js";
import { AonSelect } from "./aon-select.js";
import { AonToolbar } from "./aon-toolbar.js";
import { CONSTANT, CSS } from "../environments/environments.js";
import { setAttributes, setClasses, setEvents } from "../services/utilsComponents.js";


/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
export const createAonSelect = ({attributes, events}, parent) => {
  if(attributes.options && typeof attributes.options !== "string") attributes.options=JSON.stringify(attributes.options);
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
export const createAonInput = ({attributes, events}, parent) => {
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
export const createAonIconButton = ({attributes, events}, parent) => {
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
 export const createAonDate = ({attributes, events}, parent) => {
  let date = setAttributes( new AonDate(), attributes);
  if(events) setEvents(date, events);
  parent.appendChild(date);
  return date;
}

export const createAonCard = (attributes) => {
  const aonCard = setAttributes(new AonCard(), {
      ...attributes,
      flex:CONSTANT.TRUE
  });
  return aonCard;
}


export const createAonToolbar= (attributes) => setAttributes( new AonToolbar(), attributes);