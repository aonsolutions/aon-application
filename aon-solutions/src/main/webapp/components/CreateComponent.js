import { AonCard } from "./aon-card.js";
import { AonDate } from "./aon-date.js";
import { AonIconButton } from "./aon-icon-button.js";
import { AonInput } from "./aon-input.js";
import { AonSelect } from "./aon-select.js";
import { AonSwitch } from "./aon-switch.js";
import { AonToolbar } from "./aon-toolbar.js";
import { CONSTANT, CSS, TAG } from "../environments/environments.js";
import { newComponent, setAttributes, setClasses, setEvents } from "../services/utilsComponents.js";
import { AonNumber } from "./aon-number.js";


/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
 const createAonSelect = ({attributes, events}, parent) => {
  if(attributes && attributes.options && typeof attributes.options !== "string") {
    attributes.options = JSON.stringify(attributes.options);
  }

  let element = setAttributes(new AonSelect(), attributes);

  setClasses(element,[CSS.TRANSITION_CASCADE]);

  if(events) setEvents(element, events);
  
  if(parent) parent.appendChild(element);

  return element;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
const createAonInput = ({attributes, events}, parent) => {
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
 const createAonIconButton = ({attributes, events}, parent) => {
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
const createAonDate = ({attributes, events}, parent) => {
  let date = setAttributes( new AonDate(), attributes);
  if(events) setEvents(date, events);
  parent.appendChild(date);
  return date;
}


/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {*} parent 
 * @returns 
 */
 const createAonNumber = ({attributes, events}, parent) => {
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
 const createAonSwitch = ({attributes, events}, parent) => {
  let input = setAttributes(new AonSwitch(), attributes);
  if(events) setEvents(input, events);
  parent.appendChild(input);
  return input;
}

const createAonCard = (attributes, parent) => {
  const element =  setAttributes(new AonCard(), {
      ...attributes,
      flex:CONSTANT.TRUE
  });
  if(parent){
    parent.appendChild(element);
  }
  return element;
}


const createAonToolbar = (attributes, parent) => {
  const element = setAttributes( new AonToolbar(), attributes);
  if(parent){
    parent.appendChild(element);
  }
  return element;
}

const createForm = (id="form") => newComponent({
  type:TAG.FORM,
  id,
  attributes:{
    action: "#"
  },
  events:{
    submit: (ev)=>  ev.preventDefault()
  }
});


export const CreateComponent = {
  createAonSelect,
  createAonInput,
  createAonSwitch,
  createAonNumber,
  createAonIconButton,
  createAonCard,
  createAonDate,
  createAonToolbar,
  createForm
}