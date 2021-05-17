import { AonTextArea } from "../../../components/aon-textarea.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, MATERIAL_ICONS } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setEvents, setStyles, waitChildEl, waitEl } from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import { createButtonWrapper, createEditableTitle, createReceiverDiv, createReceiverselect, createReceiverTitle, createSendBar, createSendButton, createSendIcon, createUpload, createUploadIcon, createUploadText } from "../createComponents.js";
import { MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS } from "../MessengerEnums.js";
import { createStartJustifiedRow } from "./creationUtils.js";
import { bold, compileHTML, italic, link, list, tab } from "./markup.js";
import { appendChatMessage, fillReceiverInput, LEFT, RIGHT } from "./messenger-chat.js";

/**
 * @TODO THINGS TO ENCHANCE
 *  
 *  1 - DEMO mode disable.
 *  2 - Refactor 'send message code' --> @duplicated
 */

let me = true;


/**
 * Build desktop version of the writter 
 * @param {*} parent 
 * @param {*} data 
 */
export const buildDesktopWritter = (parent, data) => {

    /**
     * Building title
     */
    const titleDiv = createStartJustifiedRow();
    setStyles(titleDiv.element ,{ width : "100%" });
    
    const title = createEditableTitle(data.title);
    setEvents(title.element, { input : () => {
        /**
         * Changing data > title here 
         */
    } });

    /**
     * Building receiver select
     * 
     *           ------------------
     *  Para:    |  Laboral     v |
     *           ------------------
     */
    const receiverDiv = createReceiverDiv();
    const receiverTitle = createReceiverTitle();
    const receiverSelect = createReceiverselect();
    fillReceiverInput(receiverSelect,data)
    /**
     * Building aon-textarea
     * 
     * ----------------------------------
     * | <o>  B  I                      |
     * ----------------------------------
     * | My text here...                |
     * |                                |
     * |                                |
     * ----------------------------------
     * 
     */
    const aonTextArea = new AonTextArea();
    aonTextArea.id = "aonWritter"
    aonTextArea.style.height = "300px";
   waitEl("#aonWritter").then(el =>  buildTextareaToolbar(el));

    /**
     * Creating send bar
     */
    const sendBar = createSendBar();
    const upload = createUpload();
    const uploadIcon = createUploadIcon();
    const uploadText = createUploadText();

    const sendButtonWrapper = createButtonWrapper();
    const sendButton = createSendButton();

    /**
     * Set send button click envent
     */
    setEvents(sendButton.element,{
        click : () => sendMessage(aonTextArea)
    });

    const sendIcon = createSendIcon();


    /**
     * Setting up UI 
     */
    title.appendTo(titleDiv.element);

    receiverTitle.appendTo(receiverDiv.element);
    receiverDiv.element.appendChild(receiverSelect);

    uploadIcon.appendTo(upload.element);
    uploadText.appendTo(upload.element);
    upload.appendTo(sendBar.element);

    sendIcon.appendTo(sendButton.element);
    sendButton.appendTo(sendButtonWrapper.element);
    sendButtonWrapper.appendTo(sendBar.element);

    /* main elements append */
    titleDiv.appendTo(parent.element);

    receiverDiv.appendTo(parent.element);
    parent.appendChild(aonTextArea);

    sendBar.appendTo(parent.element);
}

/**
 * Build mobile version of the writter 
 * @param {*} parent 
 * @param {*} data 
 */
export const buildMobileWritter = (parent) => {
       
    const writter = newComponent({
        id : MESSENGER_COMPONENTS.WRITTER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            height: '100%',
            width: '100%',
            background: COLORS.AON_WHITE,
            position: 'absolute',
            display: 'none',
            top: '0%',
            opacity: 0,
            transition: '.5s',
            zIndex : -9
        }
    });

    const bar = new AonToolbar();
    bar.style.background = "#fff";

    bar.id = "writterbar";
    bar.type = ToolbarType.SECONDARY;
    bar.title = "Comentario";

    waitEl("#writterbar").then(tb => {

        /** 
        * Set save button 
        */    
        tb.addButton2(ACTIONS.SAVE,() => {
            /*
            * Showing float button 
            * with little animation
            */
            let button = document.getElementById(MESSENGER_IDS.ADD_ICON_BUTTON);
            setStyles(button , {
                transition : "0.25s",
                opacity : "1"
            });
  
            setTimeout(() => button.style.display = "block", 100);
            sendMessage(textarea);
            hideWritter();
        })

        /**
         *  <- Set back button 
         */
        tb.addButton2(ACTIONS.BACK,() => {

            /*
            * Showing float button 
            * with little animation
            */
            let button = document.getElementById(MESSENGER_IDS.ADD_ICON_BUTTON);
            setStyles(button , {
                transition : "0.25s",
                opacity : "1"
            });

            setTimeout(() => button.style.display = "block", 100);
            hideWritter();
      })
    })

    writter.element.appendChild(bar);

    const textarea = new AonTextArea();
    textarea.id = "aonWritter"

    waitEl("#aonWritter").then(el => buildTextareaToolbar(el));

    setStyles(textarea, {
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        boxShadow: "none",
        margin: 0,
    });

    waitChildEl(textarea, "#" + textarea.TEXTAREA).then(writtable => {
        setStyles(writtable, {
            resize: "none",
            height: "100%",
        });
    });

    waitChildEl(textarea, "toolbar").then(toolbar => {
        setStyles(toolbar, {
            background: CSS.variable(COLORS.AON_LIGHT_GRAY),
            border: "none",
            padding: "10px",
            height: "50px",
            justifyContent: "flex-start"
        });
    });

    writter.element.appendChild(textarea);
    writter.appendTo(parent.element);
}

/**
 * Show the writter with animation
 */
export const showWritter = () => {
    setTimeout(() => {
        setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{
          zIndex : 9,
          opacity : 1,
          left : 0,
        });
      }, 100);
}

/**
 * Hide writter with animation
 */
export const hideWritter = () => {
    setTimeout(() => {
        setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{
          zIndex : -9,
          opacity : 0,
        });
    }, 100);
}

/**
 * Send message to chat
 * @param {*} aonTextArea - The mensaje source
 * @returns void.
 */
const sendMessage = (aonTextArea) => {

    /**
     * If preview mode is enabled, 
     * click on eye to disable.
     */
    if(aonTextArea.querySelector("textarea") == null){
        const eyeButton = document.querySelector("#preview");
        eyeButton?.click();
    }

    const value = aonTextArea.compiledValue;
    const parent = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const data = parent.data;
    aonTextArea.clear();
    
    if(!value || value === "") return;
    const message = {
        type: "message",
        sender: "Tú",
        message: value,
        date: new Date()
    }
    data.content.push(message);

    appendChatMessage({
        name: me ? message.sender : "Receptor@email.com",
        message: message.message,
        id: "id",
        date: message.date,
        attach: message.attach,
        direction : me ?  RIGHT : LEFT
    });

    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = document.querySelector(".continueLined");
    if (lined)
        lined.style.setProperty("--height", lined.scrollHeight + "px");
     

    me =! me;
    parent.data = data;
}


/**
 * Build standard toolbar options 
 * @param {*} aonTextArea 
 */
 export const buildTextareaToolbar = (aonTextArea) => {


    aonTextArea.compile = () => compileHTML(aonTextArea);

    /**
     * Bold format button **bold**
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_BOLD,
        icon: MATERIAL_ICONS.FORMAT_BOLD,
    },
        (e) => {
            waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
            .then(el => setSelectionMarkup(el,
                (selection) => {
                    el.dataset.start = -1;
                    el.dataset.end = -1;
                    return bold(selection);
                },() => true)
            );
        }
    );

    /**
     * Italic format buttton _italic_
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_ITALIC,
        icon: MATERIAL_ICONS.FORMAT_ITALIC,
    },(e) => {
        waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
        .then(el => setSelectionMarkup(el, (selection) => italic(selection), () => true));
    });

    /**
     * Indent format button \tIdented
     */
    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.FORMAT_INDENT_INCREASE,
        icon: MATERIAL_ICONS.FORMAT_INDENT_INCREASE,
    },(e) => {
        waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
        .then(el => setSelectionMarkup(el, (selection) => tab(selection), () => true));
    });

    /**
     * List bulleted button - listItem
     */
    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
        icon: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
    },(e) => {
        waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
        .then(el => setSelectionMarkup(el, (selection) => list(selection), () => true));
    });

    /**
     * Link send button [Name](url)
     */
    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.LINK,
        icon: MATERIAL_ICONS.LINK,
    },(e) => {
            waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
                .then(el => setSelectionMarkup(el, (selection) => link(selection), () => true));
    });

    /**
     * Attach file button
     */
    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.ATTACH_FILE,
        icon: MATERIAL_ICONS.ATTACH_FILE,
    },(e) => { alert("Not supported yet") });
}

/**
 * Set markup to selection
 * @param {*} element - The input itself (Aon-textarea>textarea)
 * @param {*} funct - The Compile function.
 */
 const setSelectionMarkup = (element, funct, conditions) => {
    
    /**
     * Get text and selected 
     * text start and end indexes
     */
    const text = element.value;
    let start = element.dataset.start;
    let end = element.dataset.end;

    /**
     * If invalid index then return;
     */
    if (start == -1)  return;
    
    /**
     * Calculate index of the next space
     * and set endpoint there or on 
     * the selection end.
     */
    let indexOfNextSpace = text.indexOf(" ",start);
    if(indexOfNextSpace !== end)
        indexOfNextSpace = end;
    
    /**
     * If end has an invalid value, 
     * then return. 
     */
    if(indexOfNextSpace == -1)
        indexOfNextSpace = text.length

    /**
     * If conditions are valid,
     * then compile in markup.
     */
    const selection = text.substr(start,indexOfNextSpace);
    if (conditions(text, selection , start, indexOfNextSpace)){

        let pre = "";
        let compiled = funct(selection);
        let post = "";

        if(start !== 0)
            pre = text.substr(0, start);
        
        if(indexOfNextSpace !== text.length)
            post = text.substr(indexOfNextSpace, text.length);

        element.value = pre + compiled + post;
    }
}