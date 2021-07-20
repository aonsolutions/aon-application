import { AonSelect } from "../../../components/aon-select.js";
import { AonTextArea } from "../../../components/aon-textarea.js";
import { CONSTANT, EVENT, MSG } from "../../../environments/environments.js";
import { openFileUrl } from "../../../services/fileService.js";
import { newComponent, setAttributes } from "../../../services/utils.js";
import { MESSENGER_IDS, WORKFLOW_TYPES } from "../MessengerEnums.js";

/**
 * Create a row with space between elements inside 
 * @param {object} styles 
 * @returns 
 */
export const createSpaceBetweenRow = (styles) => newComponent({
    classes: ['flexRow', 'flexJustifyBetween', 'flexAlignCenter'],
    styles: styles
});

/**
 * Create a row with elements inside aligned to the end 
 * @param {object} styles 
 * @returns 
 */
 export const createEndJustifiedRow = (styles) => newComponent({
    classes: ['flexRow', 'flexJustifyEnd', 'flexAlignCenter'],
    styles: styles
});

/**
 * Create a row with elements inside aligned to the start 
 * @param {object} styles 
 * @returns 
 */
 export const createStartJustifiedRow = (styles) => newComponent({
    classes: ['flexRow', 'flexJustifyStart', 'flexAlignCenter'],
    styles: styles
});

export const createStartJustifiedColumn = (styles) => newComponent({
    classes: ['flexColumn', 'flexJustifyStart', 'flexAlignCenter'],
    styles: styles
});

/**
 * Create a text 
 * @param {object} properties 
 * @returns 
 */
export const createText = (properties) => newComponent({
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
export const createMaterialIcon = (properties) => newComponent({
    type: 'i',
    text: properties.name,
    classes: ['material-icons'],
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
    classes: ['material-icons-outlined'],
    styles: {
        fontSize: properties.size ? properties.size : "24px",
        color: properties.color? properties.color : "#404040"
    }
});


export const RIGHT = "RIGHT";
export const LEFT = "LEFT";
/**
 * Check the properties of the comment
 * AVOID showing null or undefined in UI.
 * @param {*} properties 
 * @returns Valid properties object.
 */

 export const checkProperties = (properties) => {
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

 //-----------------TASK HOLDER
 export const createTaskHolder = () => setAttributes( new AonSelect(),{
    id: MESSENGER_IDS.TASKHOLDER,
    name: MESSENGER_IDS.TASKHOLDER,
    title: "Asignar a"
});

//-------------TEXT AREA COMMENT
export const createAonTextArea = (placeholder) =>  setAttributes(new AonTextArea(),{
    id:MESSENGER_IDS.COMMENT_TASK,
    name:MESSENGER_IDS.COMMENT_TASK,
    placeholder: placeholder || MSG.COMMENT+"..."
});

/**
 * 
 * @param {HTMLElement} parent check html and add event 
 * @param {*} json 
 */
 export const checkFilesAddEventClick = (parent)=>{
    const elements = parent.querySelectorAll(`[${CONSTANT.TYPE}=${WORKFLOW_TYPES.AON_FILE}]`);
    for (const element of elements) {
        const url = element.src || element.href;
        if(url) element.addEventListener(EVENT.CLICK, ()=> openFileUrl(url));
    }
}