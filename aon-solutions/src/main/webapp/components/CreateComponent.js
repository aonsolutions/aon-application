import { AonCard } from "./aon-card.js";
import { AonDate } from "./aon-date.js";
import { AonIconButton } from "./aon-icon-button.js";
import { AonInput } from "./aon-input.js";
import { AonSelect } from "./aon-select.js";
import { AonSwitch } from "./aon-switch.js";
import { AonToolbar } from "./aon-toolbar.js";
import { CONSTANT, CSS, TAG } from "../environments/environments.js";
import { setAttributes, setClasses, setEvents } from "../services/utilsComponents.js";
import { AonNumber } from "./aon-number.js";


/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
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
 * @param {HTMLElement} parent
 * @param {String} autocomplete  
 * @returns 
 */
const createAonSelectAutocomplete = ({attributes, events}, parent, autocomplete) => {
  if(attributes && attributes.options && typeof attributes.options !== "string") {
    attributes.options = JSON.stringify(attributes.options);
  }

  let aonSelect = new AonSelect();
  aonSelect.autocomplete = autocomplete;

  let element = setAttributes(aonSelect, attributes);

  setClasses(element,[CSS.TRANSITION_CASCADE]);

  if(events) setEvents(element, events);
  
  if(parent) parent.appendChild(element);

  return element;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
 * @returns 
 */
const createAonInput = ({attributes, events}, parent) => {
  let input = setAttributes(new AonInput(), attributes);
  if(events) setEvents(input, events);
  if(parent) parent.appendChild(input);
  return input;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
 * @returns 
 */
 const createAonIconButton = ({attributes, events}, parent) => {
  let icon = setAttributes(new AonIconButton(), attributes);
  if(events) setEvents(icon, events);
  if(parent) parent.appendChild(icon);
  return icon;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
 * @returns 
 */
const createAonDate = ({attributes, events}, parent) => {
  let date = setAttributes( new AonDate(), attributes);
  if(events) setEvents(date, events);
  if(parent) parent.appendChild(date);
  return date;
}


/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
 * @returns 
 */
 const createAonNumber = ({attributes, events}, parent) => {
  let input = setAttributes(new AonNumber(), attributes);
  if(events) setEvents(input, events);
  if(parent) parent.appendChild(input);
  return input;
}

/**
 * 
 * @param {obj, parent} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
 * @returns 
 */
 const createAonSwitch = ({attributes, events}, parent) => {
  let input = setAttributes(new AonSwitch(), attributes);
  if(events) setEvents(input, events);
  if(parent) parent.appendChild(input);
  return input;
}


/**
 * 
 * @param {Object} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
 * @returns 
 */
const createAonCard = (attributes, parent) => {
  const element =  setAttributes(new AonCard(), {
      ...attributes,
      flex:CONSTANT.TRUE
  });
  if(parent) parent.appendChild(element);
  return element;
}

/**
 * 
 * @param {Object} attributes, events. parent for appendChild  
 * @param {HTMLElement} parent 
 * @returns 
 */
const createAonToolbar = (attributes, parent) => {
  const element = setAttributes( new AonToolbar(), attributes);
  if(parent) parent.appendChild(element);
  return element;
}

/**
 * 
 * @param {String} id 
 * @param {HTMLElement} parent 
 * @returns 
 */
const createForm = (id = "form", parent) => {
  const form = document.createElement(TAG.FORM);
  form.id = id;
  form.action = "#";
  form.onsubmit = (ev)=> ev.preventDefault();
  if(parent) parent.appendChild(form);
  return form;
};
 
export const CreateComponent = {
  createAonSelect,
  createAonSelectAutocomplete,
  createAonInput,
  createAonSwitch,
  createAonNumber,
  createAonIconButton,
  createAonCard,
  createAonDate,
  createAonToolbar,
  createForm
}