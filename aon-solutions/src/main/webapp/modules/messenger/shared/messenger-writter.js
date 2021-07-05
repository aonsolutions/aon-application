import { AonSelect } from "../../../components/aon-select.js";
import { AonTextArea } from "../../../components/aon-textarea.js";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setEvents, setStyles, waitChildEl, waitEl } from "../../../services/utils.js";
import * as ACTIONS from "../../actions.js";
import { createButtonWrapper, createDivEditable, createReceiverDiv, createSendBar, createSendButton, createSendIcon, createUpload, createUploadIcon, createUploadText } from "../createComponents.js";
import { MESSENGER_COMPONENTS, MESSENGER_IDS, MESSENGER_VIEWS, TASK_WORKFLOW_TYPE } from "../MessengerEnums.js";
import { createStartJustifiedColumn } from "./creationUtils.js";
import { bold, compileHTML, italic, link, list, tab } from "./markup.js";
import { appendChatMessage, fillWorkGroup, LEFT, RIGHT } from "./messenger-chat.js";

/**
 * @TODO THINGS TO ENCHANCE
 *  
 *  1 - DEMO mode disable.
 *  2 - Refactor 'send message code' --> @duplicated
 */

/**
 * Build desktop version of the writter 
 * @param {*} parent 
 * @param {*} data 
 */
export const buildDesktopWritter = (writter, data, parent) => {
    const application = parent.getApplication();
    /**
     * Building title
     */
    const titleDiv = createStartJustifiedColumn();
    setStyles(titleDiv.element ,{ width : "100%" });
    //TITLE
    const title = createDivEditable(data.title, MESSENGER_IDS.TITLE_TASK, "Escriba su titulo aquí");
    titleDiv.element.appendChild(title);
    //DESCRIPTION
    const description = createDivEditable(data.description, MESSENGER_IDS.DESCRIPTION_TASK, MSG.DESCRIPTION);
    description.style.fontSize = "14px";
    titleDiv.element.appendChild(description);
    //----------------WORKGROUP    //----------------WORKGROUP
    const receiverDiv = createReceiverDiv();
    const workgroupSelect = setAttributes( new AonSelect(),{
        id: MESSENGER_IDS.WORKGROUP,
        name: MESSENGER_IDS.WORKGROUP,
        title: MSG.WORKGROUP
    });

    receiverDiv.element.appendChild(workgroupSelect);
    fillWorkGroup(workgroupSelect,data, application);

    //-----------------TASK HOLDER
    const taskHolderSelect = setAttributes( new AonSelect(),{
        id: MESSENGER_IDS.TASKHOLDER,
        name: MESSENGER_IDS.TASKHOLDER,
        title: "Asignar a"
    });
    taskHolderSelect.style.marginLeft = "5px";
    receiverDiv.element.appendChild(taskHolderSelect);

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
    aonTextArea.id = MESSENGER_IDS.COMMENT_TASK;
    aonTextArea.name = MESSENGER_IDS.COMMENT_TASK;
    waitEl(`#${aonTextArea.id}`).then(el =>  buildTextareaToolbar(el));


    /**
     * Creating send bar
     */
    const sendBar = createSendBar();
    const upload = createUpload();
    const uploadIcon = createUploadIcon();
    const uploadText = createUploadText();

    const sendButtonWrapper = createButtonWrapper();
    const sendButton = createSendButton();
    sendButton.element.addEventListener(EVENT.CLICK,()=>parent.save());

    const sendIcon = createSendIcon();
    uploadIcon.appendTo(upload.element);
    uploadText.appendTo(upload.element);
    upload.appendTo(sendBar.element);

    sendIcon.appendTo(sendButton.element);
    sendButton.appendTo(sendButtonWrapper.element);
    sendButtonWrapper.appendTo(sendBar.element);

    /* main elements append */
    titleDiv.appendTo(writter.element);

    receiverDiv.appendTo(writter.element);
    writter.appendChild(aonTextArea);

    sendBar.appendTo(writter.element);
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
    bar.title = MSG.COMMENT;
    writter.element.appendChild(bar);

    const textarea = setStyles(new AonTextArea(), {
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        boxShadow: "none",
        margin: 0,
    });
    textarea.id = MESSENGER_IDS.COMMENT_TASK
    textarea.name = MESSENGER_IDS.COMMENT_TASK;
    writter.element.appendChild(textarea);

    waitEl(`#${bar.id}`).then(tb => {
        /** 
        * Set save button 
        */    
        tb.addButton2(ACTIONS.SAVE,() => {
            /*
            * Showing float button 
            * with little animation
            */
            let button = setStyles(document.getElementById(MESSENGER_IDS.ADD_ICON_BUTTON) , {
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
            let button = setStyles(document.getElementById(MESSENGER_IDS.ADD_ICON_BUTTON) , {
                transition : "0.25s",
                opacity : "1"
            });

            setTimeout(() => button.style.display = "block", 100);
            hideWritter();
      })
    })


    writter.appendTo(parent.element);


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

    waitEl(`#${textarea.id}`).then(el => buildTextareaToolbar(el));

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
export const sendMessage = (aonTextArea) => {

    /**
     * If preview mode is enabled, 
     * click on eye to disable.
     */
    // if(aonTextArea.querySelector("textarea") == null){
    //     const eyeButton = document.querySelector("#preview");
    //     if(eyeButton) eyeButton.click();
    // }

    const value =  aonTextArea.value;
    if(!value || (value && value.trim().length)) return ;
    
    const parent = document.getElementById(MESSENGER_VIEWS.AON_MESSENGER_CHAT);
    const data = parent._data;
    aonTextArea.clear();

    
    const message = {
        type: TASK_WORKFLOW_TYPE.COMMENT,
        sender: "Yo",
        message: value,
        creation_date: new Date()
    }
    data.workflows.push(message);

    appendChatMessage({
        id: "id",
        name: message.sender,
        message:message.message,
        date: message.creation_date,
        attach: message.attach,
        direction : RIGHT
    });

    /**
     * Setting the chat line once all is rendered
     * DO NOT change this, is compulsory.
     */
    const lined = document.querySelector(".continueLined");
    if (lined)
        lined.style.setProperty("--height", lined.scrollHeight + "px");

    parent.data = data;
}


/**
 * Build standard toolbar options 
 * @param {*} aonTextArea 
 */
 export const buildTextareaToolbar = async (aonTextArea) => {
    waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA).then(el=>{
        el.style.minHeight = "300px";
    // aonTextArea.compile = () => compileHTML(aonTextArea);

    /**
     * Bold format button **bold**
     */

     aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_BOLD,
        icon: MATERIAL_ICONS.FORMAT_BOLD,
    },
        (e) => {
            documentExec("bold")
            // setSelectionMarkup(el,
            // (selection) => {
            //     el.dataset.start = -1;
            //     el.dataset.end = -1;
            //     return bold(selection);
            // },() => true)
        }
    );

    /**
     * Italic format buttton _italic_
     */
    aonTextArea.addToolbarOptionLeft({
        id: MATERIAL_ICONS.FORMAT_ITALIC,
        icon: MATERIAL_ICONS.FORMAT_ITALIC,
    },(e) => {
        documentExec("italic")
        // setSelectionMarkup(el, (selection) => italic(selection), () => true)
    });

    /**
     * Indent format button \tIdented
     */
    // aonTextArea.addToolbarOptionRight({
    //     id: MATERIAL_ICONS.FORMAT_INDENT_INCREASE,
    //     icon: MATERIAL_ICONS.FORMAT_INDENT_INCREASE,
    // },(e) => {
    //     waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
    //     .then(el => setSelectionMarkup(el, (selection) => tab(selection), () => true));
    // });

    /**
     * List bulleted button - listItem
     */
    // aonTextArea.addToolbarOptionRight({
    //     id: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
    //     icon: MATERIAL_ICONS.FORMAT_LIST_BULLETED,
    // },(e) => {
    //     waitChildEl(aonTextArea, "#" + aonTextArea.TEXTAREA)
    //     .then(el => setSelectionMarkup(el, (selection) => list(selection), () => true));
    // });

    /**
     * Link send button [Name](url)
     */
    aonTextArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.LINK,
        icon: MATERIAL_ICONS.LINK,
    },(e) => {
        createLink();
        // setSelectionMarkup(el, (selection) => link(selection), () => true)
    });

    /**
     * Attach file button
     */
    // aonTextArea.addToolbarOptionRight({
    //     id: MATERIAL_ICONS.ATTACH_FILE,
    //     icon: MATERIAL_ICONS.ATTACH_FILE,
    // },(e) => { alert("Not supported yet") });
    });

}

/**
 * Set markup to selection
 * @param {*} element - The input itself (Aon-textarea>textarea)
 * @param {*} funct - The Compile function.
 */
//  const setSelectionMarkup = (element, funct, conditions) => {
//     /**
//      * Get text and selected 
//      * text start and end indexes
//      */
//     const text = element.innerText;
//     let start = element.dataset.start;
//     let end = element.dataset.end;
//     /**
//      * If invalid index then return;
//      */
//     if (start == -1)  return;
//     /**
//      * If conditions are valid,
//      * then compile in markup.
//      */
//     const selection = text.substring(start, end);

//     console.log(start, end, selection);

//     if (conditions(text, selection , start, end)){

//         const compiled = funct(selection);
//         let pre = "";
//         let post = "";

//         if(start !== 0)
//             pre = text.substr(0, start);
        
//         if(end !== text.length)
//             post = text.substr(end, text.length);

//         element.value = pre + compiled + post;
//     }
// }


const documentExec = (exec) => document.execCommand(exec) ? document.execCommand("normal") : document.execCommand(exec);

const createLink =() =>{
    const selection = document.getSelection();
    if(selection && selection.toString().trim()){
        const linkURL = prompt('URL:', 'https://');
        const aEl = setStyles(document.createElement("a"),{
            textDecoration:"underline",
            cursor:"pointer",
            color:"blue",
        });
        aEl.href  = linkURL;
        aEl.target = "_blank";
        aEl.textContent = selection;
        document.execCommand('insertHTML', false, aEl.outerHTML);
    }
}