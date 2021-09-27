import { AonToolbar } from "../../../components/aon-toolbar.js";
import { COLORS, CSS, EVENT, MSG, TAG } from "../../../environments/environments.js";
import { ToolbarType } from "../../../models/enums.js";
import { newComponent, setAttributes, setStyles} from "../../../services/utilsComponents";
import * as ACTIONS from "../../actions.js";
import {  MESSENGER_COMPONENTS, MESSENGER_IDS, TASK_STATUS } from "../MessengerEnums.js";
import {  createMobileMainView, createTitle, createAonTextArea, createChat, createSectionComment} from "./creationUtils.js";
import { buildForm, buildTextareaToolbar } from "./utils.js";

/**
 * 
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
export const buildMobile = (aonMessengerChat)=> {
    const mainView = createMobileMainView();
    const task = aonMessengerChat.task;
    aonMessengerChat.appendChild(mainView);

    const wrapper = createFirstDiv(mainView);
    if(task.id)
        buildSectionHistoric(aonMessengerChat, wrapper);

    const secondDiv = createSecondDiv(mainView);

    buildForm(secondDiv, aonMessengerChat);

    if(!task.id)
        showForm(true);
}


/**
 VIEW EDIT TASK MOBILE
 * @param {HTMLElement} aonMessengerChat 
 * @param {HTMLElement} wrapper div wrapper
 */
const buildSectionHistoric = (aonMessengerChat, wrapper)=>{
    const task = aonMessengerChat.task;
    buildToolbar(aonMessengerChat,wrapper, false);
    
    let titleText = task.title;
    if(task.registry && task.registry.name)
        titleText = `<b>[${task.registry.name}]</b> ${task.title}`;

    const title = setStyles(createTitle(titleText),{
        display : 'block',
        fontSize: '1.3em',
        padding: "10px 18px",
        width : '100%',
        borderBottom : "1px solid " + CSS.variable(COLORS.AON_LIGHT_GRAY)
    });
    wrapper.appendChild(title);
    
    let titleEl = document.getElementById(MESSENGER_IDS.TITLE_TASK);
    if(titleEl) titleEl.addEventListener(EVENT.KEYUP, ({target})=>  title.innerText = target.innerText);

        /**
     * The chat itself
     */
    const chat = createChat();
    chat.classList.add(CSS.NO_SCROLLBAR);
    wrapper.appendChild(chat);

    addTextAreaChat(aonMessengerChat);
}

const addTextAreaChat = (aonMessengerChat) => {
    const firstDiv = document.getElementById(MESSENGER_IDS.FIRST_DIV);
    const task = aonMessengerChat.task;

    if( [TASK_STATUS.IN_PROGRESS, TASK_STATUS.PENDING].includes(task.status) ){
        const div = newComponent({
            classes: [CSS.FLEX_ROW],
            styles: {
                width: "100%",
                position: "absolute",
                bottom: 0
            },
        });
        div.appendTo(firstDiv);
    
        const divs = createSectionComment(div);
        divs.iconOpenFull.addEventListener(EVENT.CLICK, ()=>showFullComment(true));
        divs.iconSend.addEventListener(EVENT.CLICK, ()=> aonMessengerChat.saveTaskWorkflow());
        changeStyleSectionComment(divs);
    
        buildFullComment(aonMessengerChat, divs.aonTextArea);
    } else {
        firstDiv.style.height="100%";
    }
}

/**
 * Build mobile version of the writter 
 * @param {HTMLElement} aonMessengerChat aon-messenger-chat component 
 * @param {HTMLElement} aonTextArea textarea principal
 */
const buildFullComment = (aonMessengerChat, aonTextArea) => { 
    const wrapper = document.getElementById(MESSENGER_IDS.MAIN_WRAPPER);
    const writter = newComponent({
        id : MESSENGER_COMPONENTS.WRITTER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            background: COLORS.AON_WHITE,
            height: '100%',
            width: '100%',
            position: 'absolute',
            display: 'none',
            transition: '.5s',
            top: 0,
            opacity: 0,
            zIndex: -9
        }
    }).element;
    wrapper.appendChild(writter);

    const bar = setAttributes(new AonToolbar(),{
       type:ToolbarType.SECONDARY,
       title:MSG.COMMENT
    });
    bar.style.background = "#fff";
    writter.appendChild(bar);

    bar.addButton2(ACTIONS.SEND,() => {
        aonMessengerChat.saveTaskWorkflow();
        showFullComment(false);
    });
    bar.addButton2(ACTIONS.BACK,() => showFullComment(false));

    const textarea = setStyles(createAonTextArea(), {
        flexDirection: 'column',
        height: '100%',
        width: '100%',
        boxShadow: "none",
        background: CSS.variable(COLORS.AON_WHITE),
        margin: 0,
    });
    writter.appendChild(textarea);
    buildTextareaToolbar(textarea);

    const textAreaToolbar = textarea.querySelector("toolbar");
    if(textAreaToolbar){
        setStyles(textAreaToolbar, {
            background: CSS.variable(COLORS.AON_LIGHT_GRAY),
            border: "none",
            padding: "10px",
            height: "50px"
        });
    }
    textarea.addEventListener(EVENT.INPUT, ()=>{
      aonTextArea.value = textarea.value || "";
      aonTextArea.FILES = textarea.FILES;
    });
}


const changeStyleSectionComment = (divs) => {
    divs.aonTextArea.style.fontSize = "15px";
    divs.aonTextArea.style.margin = "0";
    divs.aonTextArea.style.minHeight = "55px";
    divs.divComment.style. background = "#fff";
    divs.iconOpenFull.querySelector("i").style.fontSize = "1.8em";
    divs.iconSend.querySelector("i").style.fontSize = "1.8em";
}

const buildToolbar = (aonMessengerChat, div, create = false) => {
    const task = aonMessengerChat.task;
    /**
     * Building toolbars
     */
     const sourceText =  MSG[task.source.toString().toUpperCase()] || task.source;
     const toolbar = setAttributes(new AonToolbar(),{
        type: ToolbarType.SECONDARY,
        title:sourceText +" #" + (task.number || "0").toString().padStart(5, 0)
    });
    toolbar.style.width = "100%"; 
    
    div.appendChild(toolbar);

    if(create){
        if([TASK_STATUS.PENDING, TASK_STATUS.IN_PROGRESS].includes(task.status))
            toolbar.addButton2(ACTIONS.SAVE,() => aonMessengerChat.save())
    } else {
        toolbar.addButton2(ACTIONS.EDIT,() => showForm(true));
    }
    
    toolbar.addButton2(ACTIONS.BACK,() =>{
        if(task.id && create) {
            showForm(false);
        }
        else 
            aonMessengerChat.back();
    });
}

/**
 * 
 * @param {Boolean} b true or false 
 */
const showForm = (b) => {
    const divMainTwo = document.getElementById(MESSENGER_IDS.DIV_MAIN_MOBILE);
    let styles = {zIndex : -9, opacity : 0};
    if(b)
        styles = {zIndex: 9, opacity: 1, left: 0};

    setTimeout(() => setStyles(divMainTwo, styles), 100);
}

/**
 * 
 * @param {Boolean} b true or false 
 */
const showFullComment  = (b) => {
    const writter = document.getElementById(MESSENGER_COMPONENTS.WRITTER);
    const textarea = writter.querySelector("aon-textarea");
    const aonTextArea = document.getElementById(MESSENGER_IDS.COMMENT_TASK)
    if(b){
        if(aonTextArea.value) textarea.value = aonTextArea.value;
        // show writter
        let componentWrite = setStyles(writter, { display: "flex" });
        setTimeout(() => setStyles(componentWrite, {zIndex: 9,opacity: 1,left: 0}), 100);
    } else {
        setTimeout(() => setStyles(document.getElementById(MESSENGER_COMPONENTS.WRITTER),{zIndex : -9, opacity : 0}), 100);
    }
} 

/**
 * 
 * @param {HTMLElement} div div class aonMobileSubContent
 * @param {HTMLElement} aonMessengerChat component aon-messenger-chat.js
 */
 const buildQuery = (div, aonMessengerChat) => {
    const task = aonMessengerChat.task;

    // buildFormQuery(div, aonMessengerChat);

    // /**
    //  * Creating text area
    //  */
    const aonTextArea = setStyles(createAonTextArea(`${MSG.WRITE_A_DESCRIPTION}...`),{
        height: "100%",
        width: "100%",
        boxShadow : "none",
        marginTop : 0
    });
    aonTextArea.id = MESSENGER_IDS.DESCRIPTION_TASK;
    div.appendChild(aonTextArea);
    if(task && task.getDescriptionJson().observation) aonTextArea.value = task.getDescriptionJson().observation;
    buildTextareaToolbar(aonTextArea);

   /**
    * CHANGE STYLE AONTEXTAAREA
    */
    let textAreaDiv = aonTextArea.getTextAreaDiv();
    if(textAreaDiv) textAreaDiv.style.padding = "20px";

    const aonTextAreaToolbar = aonTextArea.getToolbar();
    if(aonTextAreaToolbar){
        setStyles(aonTextAreaToolbar,{
            paddingLeft  : "calc(1.5em - 5px)",
            paddingRight : "calc(1.5em - 5px)",
            borderBottom : "1px solid #e0e0e0"
        });
    }
}

const createFirstDiv = (mainView) => {
    const firstDiv = newComponent({
        classes: [CSS.FLEX_ROW],
        id: MESSENGER_IDS.FIRST_DIV,
        styles: {
          width: "100%",
          height: "90%",
          maxWidth: "600px",
          padding: "0",
        },
    });
    firstDiv.appendTo(mainView);
    /**
     * Wrapper 
     * if some new menus / toolbars needed, here.
     */
    const wrapper = newComponent({
        type: MESSENGER_COMPONENTS.WRAPPER,
        id:MESSENGER_IDS.MAIN_WRAPPER,
        classes: [CSS.FLEX_COLUMN],
        styles: {
            width: "100%",
            height: '100%',
            fontSize: '14px'
        }
    }).element;
    firstDiv.element.appendChild(wrapper);
    return wrapper;
}
  

const createSecondDiv = (mainView) => {
    let div = newComponent({
        type: TAG.DIV,
        id:MESSENGER_IDS.DIV_MAIN_MOBILE,
        classes : [CSS.FLEX_COLUMN, CSS.FLEX_ALIGN_CENTER],
        styles : {
            height: '100%',
            width: '100%',
            background: CSS.variable(COLORS.AON_WHITE),
            position: 'absolute',
            top: '0%',
            transition: '.5s',
            opacity: 0,
            zIndex: -9
        }
    });
    div.appendTo(mainView);

    buildToolbar(aonMessengerChat, div, true);

    const secondDiv = document.createElement(TAG.DIV);
    secondDiv.id = MESSENGER_IDS.SECOND_DIV;
    secondDiv.className = CSS.AON_MOBILE_SUB_CONTENT;
    secondDiv.style.width = "100%";
    div.appendChild(secondDiv);
    return secondDiv;
}
  