import { TAG } from "../environments/environments.js";


export const createDiv = (properties)=> newComponent({
  type: TAG.DIV,
  ...properties
});

export const createSpan = (properties)=> newComponent({
  type: TAG.SPAN,
  ...properties
});


/** 
 * Creates a new component
 * @param {*} properties - json with properties
 *  
 *  EXAMPLE
 * {
 *    type : tag-type,
 *    id :  id,
 *    classes : [class1,class2],
 *    styles : {
 *        style : value 
 *    },
 *    data : {
 *       name : value
 *    }
 *    events : {
 *        event : function 
 *    },
 * }
 * 
 * @returns Object of the component
 */
 export const newComponent = (properties) =>{
    if(properties == undefined) properties = {};
    properties.element  =  properties.element || document.createElement('div');
  
    //Internal functions
    properties.appendTo =  (e) => e.appendChild(properties.element);
    properties.appendChild =  (e) => properties.element.appendChild(e);
    properties.clean = () => properties.element.innerHTML = '';
  
    //Check information
    if(properties.type)   
        properties.element = document.createElement(properties.type);
  
    if(properties.id)    
        properties.element.id  = properties.id;
  
    if(properties.text)   
        properties.element.innerHTML = properties.text;
  
    //Set data to element
    setAttributes(properties.element,properties.attributes);
    setDataset(properties.element,properties.dataset);
    setEvents(properties.element,properties.events);
    setStyles(properties.element,properties.styles);
    setClasses(properties.element,properties.classes);
  
    return properties;
  } 
  
  /**
   * Set attributes to element
   * @param {*} element 
   * @param {*} attributes 
   */
  export const setAttributes = (element, attributes) =>{
    if(element && attributes) 
        for (const key in attributes)  
          element.setAttribute(key, attributes[key]);
    return element;
  }
  
  /**
   * Set dataset to an element
   * @param {*} element 
   * @param {*} dataset 
   */
  export const setDataset = (element, dataset) => {
    if(element && dataset) 
        for (const key in dataset) 
          element.dataset[key] = dataset[key];
    return element;
  }
  
  /**
   * Set events to an element
   * @param {*} element 
   * @param {*} events 
   */
  export const setEvents = (element,events) => {
    if(element && events) 
        for (const key in events)  
          element.addEventListener(key,events[key]);
    return element;
  }
  
  /**
   * Set styles to an element
   * @param {*} element 
   * @param {*} styles 
   */
  export const setStyles = (element,styles) => {
    if(element && styles) 
      for (const key in styles)  
        element.style[key] = styles[key];  
    return element;
  }
  
  /**
   * Set classes 
   * @param {*} element 
   * @param {*} classes 
   */
  export const setClasses = (element,classes) => {
    if(element && classes)
      classes.forEach(cl => { if(cl) element.classList.add(cl); });
    return element;
  }
  
  export const getOffsetTop = (element) => {
  	let totalOffsetTop = 0;
  	for ( let el = element; el; el = el.offsetParent ) {
  		totalOffsetTop += el.offsetTop;
  	}
  	return totalOffsetTop;
  }

  