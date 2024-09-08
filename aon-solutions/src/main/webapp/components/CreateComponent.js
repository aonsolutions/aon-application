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
import { AonNewInput } from "./aon-new-input.js";
import { AonNewDate } from "./aon-new-date.js";
import { AonBasicTable } from "./aon-basic-table.js";
import { AonNewSuggestion } from "./aon-new-suggestion.js";
import { AonNewNumber } from "./aon-new-number.js";
import { AonNewSelect } from "./aon-new-select.js";
import { AonNewTextarea } from "./aon-new-textarea.js";
import { AonEmail } from "./aon-email.js";
import { AonQuantity } from "./aon-quantity.js";

export const createAonElement = (el, id, title, parent) => {
  el.id = id || '';
  el.title = title || '';
  el.description = title || '';
  if(parent) parent.appendChild(el);
  return el;
}

export const createCard = (id, title, parent) => {
  return createAonElement(new AonCard(), id, title, parent);
}

export const createInput = (id, title, parent) => {
  return createAonElement(new AonNewInput(), id, title, parent); // AonInput
}

export const createEmail = (id, title, parent) => {
  return createAonElement(new AonEmail(), id, title, parent);
}

export const createDate = (id, title, parent) => {
  return createAonElement(new AonNewDate(), id, title, parent); // AonDate
}

export const createNumber = (id, title, parent) => {
  return createAonElement(new AonNewNumber(), id, title, parent); // AonNumber
}

export const createSelect = (id, title, parent) => {
  return createAonElement(new AonNewSelect(), id, title, parent); // AonSelect
}

export const createTable = (id, parent) => {
  return createAonElement(new AonBasicTable(), id, '', parent);
}

export const createSuggestion = (id, title, parent) => {
  return createAonElement(new AonNewSuggestion(), id, title, parent); // AonSuggestion
}

export const createTextarea = (id, title, parent) => {
  return createAonElement(new AonNewTextarea(), id, title, parent); // AonAutosizeTextarea
}

export const createQuantity = (id, title, parent) => {
  return createAonElement(new AonQuantity(), id, title, parent); // AonAutosizeTextarea
}

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