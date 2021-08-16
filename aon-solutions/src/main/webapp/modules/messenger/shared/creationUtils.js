import { AonSelect } from "../../../components/aon-select.js";
import { AonTextArea } from "../../../components/aon-textarea.js";
import { CSS, MSG, TAG, COLORS, MATERIAL_ICONS } from "../../../environments/environments.js";
import { newComponent, setAttributes, setDateTimestampDay, setStyles } from "../../../services/utils.js";
import { createCommentContent, createMessageAuthor, createMessageBox } from "../createComponents.js";
import { ICON_TYPES, MESSENGER_IDS } from "../MessengerEnums.js";
import { checkFilesAddEventClick } from "./utils.js";

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

export const titleFirstDiv  = () => {
    const div = createStartJustifiedColumn();
    let span = setStyles(document.createElement(TAG.SPAN),{
        fontSize: "0.9375rem",
        width:"100%",
        color:CSS.variable(COLORS.AON_COLOR_INK_MEDIUM_CONTRANST)
    });
    span.textContent = MSG.ISSUE;
    div.appendChild(span);

    return div.element;
}

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
    classes: [ICON_TYPES.MATERIAL_ICONS_OUTLINED],
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

//-------------TEXT AREA COMMENT
export const createAonTextArea = (placeholder) =>  setAttributes(new AonTextArea(),{
    name:MESSENGER_IDS.COMMENT_TASK,
    placeholder: placeholder || MSG.COMMENT+"..."
});

export const iconComment = (icon) => {
    const a = setStyles(document.createElement("a"),{
        boxShadow: "none",
        margin: "5px",
        visibility: "visible",
        float: "right",
        background: "rgba(0, 0, 0, 0)",
        cursor: "pointer"
    });

    let iconSend = setStyles(document.createElement("i"),{
        fontSize: "1.6em",
        lineHeight: "35px",
        color: "#42a5f5"
    });
    iconSend.className   = "material-icons";
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
        fontSize : '.7em',
        classes: [CSS.FIRST_LETTER_UPPER]
    });
    date.appendTo(name.element);

    message.appendTo(chat); //ADD MESSAGE IN DIV CHAT

    checkFilesAddEventClick(message.element); //ADD EVENT CLICK

    return message;
}

