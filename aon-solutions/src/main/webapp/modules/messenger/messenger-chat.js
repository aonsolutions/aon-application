import { AonIconButton } from "../../components/aon-icon-button.js";
import { COLORS, CSS } from "../../environments/environments.js";
import { newComponent, setAttributes, setEvents, setStyles } from "../../services/utils.js";
import { createAction, createMessageAuthor, createMessageBox, createMessageContent, createTitle } from "./createComponents.js";
import { createOutlinedMaterialIcon, createSpaceBetweenRow, createText } from "./creationUtils.js";
import { buildMobileWritter } from "./messenger-writter.js";


export const RIGHT = "RIGHT";
export const LEFT = "LEFT";

/**
 * Create desktop chat for messenger (mobile)
 * @param {*} parent 
 * @param {*} data 
 */
export const buildMobileChat = (parent, data) => {

    parent.element.style.padding = 0;
    const wrapper = newComponent({
        type: 'wrapper',
        classes: [CSS.FLEX_COLUMN],
        styles: {
            width: "100%",
            height: '100%',
            fontSize: '12px'
        }
    });

    const chat = newComponent({
        type: 'chat',
        classes: [
            "continueLined", 
            CSS.FLEX_COLUMN, 
            CSS.NO_SCROLLBAR, 
            CSS.FLEX_ALIGN_CENTER
        ],
        styles: {
            position: "relative",
            width: '100%',
            zIndex: "0",
            "scroll-behavior": "smooth",
            height: '100%',
            padding: "20px",
            paddingTop: "0px",
            overflow: 'auto',
            borderBottom: '1px solid #f0f0f0',
        }
    });

    const semiheader = newComponent({
        classes: [
            CSS.FLEX_ROW,
            CSS.FLEX_ALIGN_CENTER,
            CSS.FLEX_JUSTIFY_BETWEEN
        ],
        styles: {
            padding: "10px",
            paddingTop: "5px",
            paddingBottom: "5px",
            color:  CSS.variable(COLORS.AON_GRAY),
            borderBottom: "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
        }
    });

    const back = createOutlinedMaterialIcon({
        name: "arrow_back",
        color: "#404040",
        size: "20px"
    });
   back.element.style.marginLeft = "16px"
   back.element.onclick = () => {
       document.querySelector("#aonMessengerSidenavAbiertas").click();
   }

    const info = newComponent({
        classes : ['flexRow','flexAlignCenter']
    });

    const status = createOutlinedMaterialIcon({
        name: "info",
        color: "#5cb85c",
        size: "20px"
    });
    status.element.style.marginLeft = "10px"
   
    const id = createText({
        text :  "# " + data.id,
        color : "gray",
        size : "1em"
    });
    id.element.style.paddingLeft = "5px";
  

    const title = createTitle(data.title);
    setStyles(title.element,
        {
            display : 'block',
            fontSize: '1em',
            paddingTop: "10px",
            paddingBottom: "10px",
            width : '100%',
            background : "#fff",
            borderBottom : "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
        }
    );

    id.appendTo(info.element);
    status.appendTo(info.element);
    back.appendTo(semiheader.element);
    info.appendTo(semiheader.element);

    wrapper.appendChild(semiheader.element);
    title.appendTo(chat.element);


    const start = newComponent({ id: "start" });
    start.appendTo(chat.element);
    
    data.content.forEach(element => {
        if (element.type == "action") {
            const action = createAction(chooseActionIcon(element.action), element.message);
            action.appendTo(chat.element);
        }

        if (element.type == "message") {
            const message = createChatMessage({
                name: element.sender,
                message: element.message,
                id: "is",
                date: element.date,
                attach: element.attach
            });
            message.appendTo(chat.element);
        }

    });

    const end = newComponent({ id: "end" });
    end.appendTo(chat.element);

    buildMobileWritter(wrapper,data)
    chat.appendTo(wrapper.element);
    wrapper.appendTo(parent.element);
}

/**
 * Create desktop chat for messenger
 * @param {*} parent 
 * @param {*} data 
 */
export const buildChat = (parent, data) => {

    const wrapper = newComponent({
        type: 'wrapper',
        classes: ['flexColumn'],
        styles: {
            width: "90%",
            height: '100%',
        }
    });

    const chat = newComponent({
        type: 'chat',
        classes: ["continueLined", "flexColumn", "noScrollbar", "flexAlignCenter"],
        styles: {
            position: "relative",
            width: '100%',
            zIndex: "0",
            "scroll-behavior": "smooth",
            height: '100%',
            padding: "20px",
            paddingTop: "20px",
            overflow: 'auto',
            borderBottom: '1px solid #f0f0f0',
        }
    });

    const title = createTitle("Comentarios");
    setStyles(title.element,
        {
            maxWidth: '550px',
            alignSelf: 'center',
            paddingBottom: '10px',
            borderBottom: '1px solid #f0f0f0',
        }
    );
    title.appendTo(wrapper.element);

    const start = newComponent({ id: "start" });
    start.appendTo(chat.element);

    data.content.forEach(element => {
        if (element.type == "action") {
            const action = createAction(chooseActionIcon(element.action), element.message);
            action.appendTo(chat.element);
        }

        if (element.type == "message") {
            const message = createChatMessage({
                name: element.sender,
                message: element.message,
                id: "is",
                date: element.date,
                attach: element.attach
            });
            message.appendTo(chat.element);
        }

    });

    const end = newComponent({ id: "end" });
    end.appendTo(chat.element);

    chat.appendTo(wrapper.element);
    wrapper.appendTo(parent.element);

    const leftButtonBar = newComponent({
        classes: ["flexColumn", "flexJustifyEnd"],
        styles: {
            position: 'relative',
            width: "10%",
            height: "100%"
        }
    });

    const upIcon = setAttributes(new AonIconButton(), {
        icon: "expand_less",
        id: "id",
        background: "transparent",
    });

    setEvents(upIcon, {
        click: () => {
            document.querySelector("#start").scrollIntoView();
        }
    });

    const downIcon = setAttributes(new AonIconButton(), {
        icon: "expand_more",
        id: "id",
        background: "transparent",
    });

    setEvents(downIcon, {
        click: () => {
            document.querySelector("#end").scrollIntoView();
        }
    });

    leftButtonBar.appendChild(upIcon);
    leftButtonBar.appendChild(downIcon);
    leftButtonBar.appendTo(parent.element);
}

/**
 * Choose icon for the actions
 * @param {*} actionType 
 * @returns 
 */
const chooseActionIcon = (actionType) => {
    switch (actionType) {
        case "close": return "close";
        default: return "out-info";
    }
}


/**
 * Append a new message to the chat with a little animation
 * @param {*} properties 
 */
export const appendChatMessage = (properties) => {

    const message = createChatMessage(properties);
    setStyles(message.element, {
        opacity : 0,
        marginTop : '10px',
        transition : ".25s"
    });

    const chat = document.querySelector("chat");
    const end = chat.querySelector("#end");
    chat.removeChild(end);
    
    message.appendTo(chat);
    chat.appendChild(end);

    end.scrollIntoView();

    setTimeout(() => {
        setStyles(message.element, {
            opacity : 1,
            marginTop : '0px'
        });
    }, 100);
} 


/**
 * Create a new message
 * @param {*} properties 
 * @returns 
 */
export const createChatMessage = (properties) => {
    properties = checkProperties(properties);

    const message = createMessageBox(properties);
    const name = createMessageAuthor(properties);
    const description = createMessageContent(properties);
    const footer = createSpaceBetweenRow({ paddingTop: '5px', height: "20px" });

    const dateObject = properties.date;
    let dateString =  dateObject.getDate() + "/" + dateObject.getMonth() + "/" + dateObject.getFullYear();

    const date = createText({
        text: dateString,
        color: '#c5c5c5',
        fontSize : '.7em'
    });
    date.appendTo(name.element);
    name.appendTo(message.element);
    description.appendTo(message.element);
    footer.appendTo(message.element);


    return message;
}

/**
 * Check the properties of the message
 * @param {*} properties 
 * @returns 
 */
const checkProperties = (properties) => {
    if (!properties.name)
        properties.name = ""

    if (!properties.direction || (properties.direction != RIGHT && properties.direction != LEFT))
        properties.direction = LEFT;

    if (!properties.message)
        properties.message = ""

    if (!properties.attach)
        properties.attach = [];

    if (!properties.date)
        properties.date = "";

    return properties;
}